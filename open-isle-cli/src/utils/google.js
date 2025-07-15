import { API_BASE_URL, config, toast } from '../main'
import { setToken, loadCurrentUser } from './auth'

export async function googleGetIdToken() {
  if (!config.googleClientId) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/config`)
      if (res.ok) {
        const data = await res.json()
        if (data.googleClientId) {
          config.googleClientId = data.googleClientId
        }
      }
    } catch { /* ignore */ }
  }
  return new Promise((resolve, reject) => {
    if (!window.google || !config.googleClientId) {
      toast.error('Google 登录不可用')
      reject()
      return
    }
    window.google.accounts.id.initialize({
      client_id: config.googleClientId,
      callback: ({ credential }) => resolve(credential),
      use_fedcm: true
    })
    window.google.accounts.id.prompt()
  })
}

export async function googleAuthWithToken(idToken, reason, redirect_success, redirect_not_approved) {
  try {
    const res = await fetch(`${API_BASE_URL}/api/auth/google`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ idToken, reason })
    })
    const data = await res.json()
    if (res.ok && data.token) {
      setToken(data.token)
      await loadCurrentUser()
      toast.success('登录成功')
      if (redirect_success) redirect_success()
    } else if (data.reason_code === 'NOT_APPROVED') {
      toast.info('当前为注册审核模式，请填写注册理由')
      sessionStorage.setItem('google_id_token', idToken)
      if (redirect_not_approved) redirect_not_approved()
    } else if (data.reason_code === 'IS_APPROVING') {
      toast.info('您的注册理由正在审批中')      
      if (redirect_success) redirect_success()
    }
  } catch (e) {
    toast.error('登录失败')
  }
}

export async function googleSignIn(redirect_success, redirect_not_approved) {
  try {
    const token = await googleGetIdToken()
    await googleAuthWithToken(token, '', redirect_success, redirect_not_approved)
  } catch {
    /* ignore */
  }
}
