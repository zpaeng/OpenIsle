import { ref } from 'vue'
import { getToken } from '~/utils/auth'

const hasUnread = ref(false)

export function useChannelUnread() {
  const config = useRuntimeConfig()
  const API_BASE_URL = config.public.apiBaseUrl

  const setFromList = (channels) => {
    hasUnread.value = Array.isArray(channels) && channels.some((c) => c.unreadCount > 0)
  }

  const fetchChannelUnread = async () => {
    const token = getToken()
    if (!token) {
      hasUnread.value = false
      return
    }
    try {
      const response = await fetch(`${API_BASE_URL}/api/channels`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      if (response.ok) {
        const data = await response.json()
        setFromList(data)
      }
    } catch (e) {
      console.error('Failed to fetch channel unread status:', e)
    }
  }

  return {
    hasUnread,
    fetchChannelUnread,
    setFromList,
  }
}
