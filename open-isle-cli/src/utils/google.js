import { API_BASE_URL, GOOGLE_CLIENT_ID, toast } from '../main'
import { setToken, loadCurrentUser } from './auth'

export async function googleGetIdToken() {
  return new Promise((resolve, reject) => {
    if (!window.google || !GOOGLE_CLIENT_ID) {
      toast.error('Google 登录不可用')
      reject()
      return
    }
    window.google.accounts.id.initialize({
      client_id: GOOGLE_CLIENT_ID,
      callback: ({ credential }) => resolve(credential)
    })
    window.google.accounts.id.prompt()
  })
}

export async function googleAuthWithToken(idToken, reason, redirect) {
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
      if (redirect) redirect()
    } else {
      toast.error(data.error || '登录失败')
    }
  } catch (e) {
    toast.error('登录失败')
  }
}

export async function googleSignIn(redirect, reason) {
  try {
    const token = await googleGetIdToken()
    await googleAuthWithToken(token, reason, redirect)
  } catch {
    /* ignore */
  }
}
