import { API_BASE_URL } from '../main'

const TOKEN_KEY = 'token'

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
}

export function isLogin() {
  const token = getToken()
  console.log('token', token)
  return token !== null && token !== ''
}

export async function checkToken() {
  const token = getToken()
  if (!token) return false
  try {
    const res = await fetch(`${API_BASE_URL}/api/auth/check`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    return res.ok
  } catch (e) {
    return false
  }
}
