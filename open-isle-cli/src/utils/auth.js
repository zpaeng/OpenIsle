import { API_BASE_URL } from '../main'
import { reactive } from 'vue'

const TOKEN_KEY = 'token'

export const authState = reactive({
  loggedIn: false
})

authState.loggedIn = localStorage.getItem(TOKEN_KEY) !== null && localStorage.getItem(TOKEN_KEY) !== ''

export function getToken() {
  return localStorage.getItem(TOKEN_KEY)
}

export function setToken(token) {
  localStorage.setItem(TOKEN_KEY, token)
  authState.loggedIn = true
}

export function clearToken() {
  localStorage.removeItem(TOKEN_KEY)
  authState.loggedIn = false
}

export async function fetchCurrentUser() {
  const token = getToken()
  if (!token) return null
  try {
    const res = await fetch(`${API_BASE_URL}/api/users/me`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (!res.ok) return null
    return await res.json()
  } catch (e) {
    return null
  }
}

export function isLogin() {
  return authState.loggedIn
}

export async function checkToken() {
  const token = getToken()
  if (!token) return false
  try {
    const res = await fetch(`${API_BASE_URL}/api/auth/check`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    authState.loggedIn = res.ok
    return res.ok
  } catch (e) {
    authState.loggedIn = false
    return false
  }
}
