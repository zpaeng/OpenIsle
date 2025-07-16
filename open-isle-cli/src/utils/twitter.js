import { API_BASE_URL, TWITTER_CLIENT_ID, toast } from '../main'
import { setToken, loadCurrentUser } from './auth'

export function twitterAuthorize(state = '') {
  if (!TWITTER_CLIENT_ID) {
    toast.error('Twitter 登录不可用')
    return
  }
  const redirectUri = `${window.location.origin}/twitter-callback`
  const url = `https://twitter.com/i/oauth2/authorize?response_type=code&client_id=${TWITTER_CLIENT_ID}&redirect_uri=${encodeURIComponent(redirectUri)}&scope=tweet.read%20users.read&state=${state}`
  window.location.href = url
}

export async function twitterExchange(code, state, reason) {
  try {
    const res = await fetch(`${API_BASE_URL}/api/auth/twitter`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ code, redirectUri: `${window.location.origin}/twitter-callback`, reason, state })
    })
    const data = await res.json()
    if (res.ok && data.token) {
      setToken(data.token)
      await loadCurrentUser()
      toast.success('登录成功')
      return { success: true, needReason: false }
    } else if (data.reason_code === 'NOT_APPROVED') {
      toast.info('当前为注册审核模式，请填写注册理由')
      return { success: false, needReason: true, token: data.token }
    } else if (data.reason_code === 'IS_APPROVING') {
      toast.info('您的注册理由正在审批中')
      return { success: true, needReason: false }
    } else {
      toast.error(data.error || '登录失败')
      return { success: false, needReason: false, error: data.error || '登录失败' }
    }
  } catch (e) {
    toast.error('登录失败')
    return { success: false, needReason: false, error: '登录失败' }
  }
}
