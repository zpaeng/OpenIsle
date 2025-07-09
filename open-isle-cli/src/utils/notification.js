import { API_BASE_URL } from '../main'
import { getToken } from './auth'

export async function fetchUnreadCount() {
  try {
    const token = getToken()
    if (!token) return 0
    const res = await fetch(`${API_BASE_URL}/api/notifications/unread-count`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (!res.ok) return 0
    const data = await res.json()
    return data.count
  } catch (e) {
    return 0
  }
}

export async function markNotificationsRead(ids) {
  try {
    const token = getToken()
    if (!token || !ids || ids.length === 0) return false
    const res = await fetch(`${API_BASE_URL}/api/notifications/read`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ ids })
    })
    return res.ok
  } catch (e) {
    return false
  }
}
