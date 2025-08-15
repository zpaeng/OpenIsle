import { navigateTo, useRuntimeConfig } from 'nuxt/app'
import { reactive, ref } from 'vue'
import { toast } from '~/composables/useToast'
import { getToken } from '~/utils/auth'
import { reactionEmojiMap } from '~/utils/reactions'

export const notificationState = reactive({
  unreadCount: 0,
})

const iconMap = {
  POST_VIEWED: 'fas fa-eye',
  COMMENT_REPLY: 'fas fa-reply',
  POST_REVIEWED: 'fas fa-shield-alt',
  POST_REVIEW_REQUEST: 'fas fa-gavel',
  POST_UPDATED: 'fas fa-comment-dots',
  USER_ACTIVITY: 'fas fa-user',
  FOLLOWED_POST: 'fas fa-feather-alt',
  USER_FOLLOWED: 'fas fa-user-plus',
  USER_UNFOLLOWED: 'fas fa-user-minus',
  POST_SUBSCRIBED: 'fas fa-bookmark',
  POST_UNSUBSCRIBED: 'fas fa-bookmark',
  REGISTER_REQUEST: 'fas fa-user-clock',
  ACTIVITY_REDEEM: 'fas fa-coffee',
  LOTTERY_WIN: 'fas fa-trophy',
  LOTTERY_DRAW: 'fas fa-bullhorn',
  MENTION: 'fas fa-at',
}

export async function fetchUnreadCount() {
  const config = useRuntimeConfig()
  const API_BASE_URL = config.public.apiBaseUrl
  try {
    const token = getToken()
    if (!token) {
      notificationState.unreadCount = 0
      return 0
    }
    const res = await fetch(`${API_BASE_URL}/api/notifications/unread-count`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    if (!res.ok) {
      notificationState.unreadCount = 0
      return 0
    }
    const data = await res.json()
    notificationState.unreadCount = data.count
    return data.count
  } catch (e) {
    notificationState.unreadCount = 0
    return 0
  }
}

export async function markNotificationsRead(ids) {
  try {
    const config = useRuntimeConfig()
    const API_BASE_URL = config.public.apiBaseUrl

    const token = getToken()
    if (!token || !ids || ids.length === 0) return false
    const res = await fetch(`${API_BASE_URL}/api/notifications/read`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ ids }),
    })
    return res.ok
  } catch (e) {
    return false
  }
}

export async function fetchNotificationPreferences() {
  try {
    const config = useRuntimeConfig()
    const API_BASE_URL = config.public.apiBaseUrl

    const token = getToken()
    if (!token) return []
    const res = await fetch(`${API_BASE_URL}/api/notifications/prefs`, {
      headers: { Authorization: `Bearer ${token}` },
    })
    if (!res.ok) return []
    return await res.json()
  } catch (e) {
    return []
  }
}

export async function updateNotificationPreference(type, enabled) {
  try {
    const config = useRuntimeConfig()
    const API_BASE_URL = config.public.apiBaseUrl
    const token = getToken()
    if (!token) return false
    const res = await fetch(`${API_BASE_URL}/api/notifications/prefs`, {
      method: 'POST',
      headers: {
        Authorization: `Bearer ${token}`,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify({ type, enabled }),
    })
    return res.ok
  } catch (e) {
    return false
  }
}

/**
 * 处理信息的高阶函数
 * @returns
 */
function createFetchNotifications() {
  const notifications = ref([])
  const isLoadingMessage = ref(false)
  const fetchNotifications = async () => {
    const config = useRuntimeConfig()
    const API_BASE_URL = config.public.apiBaseUrl
    if (isLoadingMessage && notifications && markRead) {
      try {
        const token = getToken()
        if (!token) {
          toast.error('请先登录')
          return
        }
        isLoadingMessage.value = true
        notifications.value = []
        const res = await fetch(`${API_BASE_URL}/api/notifications`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        })
        isLoadingMessage.value = false
        if (!res.ok) {
          toast.error('获取通知失败')
          return
        }
        const data = await res.json()

        for (const n of data) {
          if (n.type === 'COMMENT_REPLY') {
            notifications.value.push({
              ...n,
              src: n.comment.author.avatar,
              iconClick: () => {
                markRead(n.id)
                navigateTo(`/users/${n.comment.author.id}`, { replace: true })
              },
            })
          } else if (n.type === 'REACTION') {
            notifications.value.push({
              ...n,
              emoji: reactionEmojiMap[n.reactionType],
              iconClick: () => {
                if (n.fromUser) {
                  markRead(n.id)
                  navigateTo(`/users/${n.fromUser.id}`, { replace: true })
                }
              },
            })
          } else if (n.type === 'POST_VIEWED') {
            notifications.value.push({
              ...n,
              src: n.fromUser ? n.fromUser.avatar : null,
              icon: n.fromUser ? undefined : iconMap[n.type],
              iconClick: () => {
                if (n.fromUser) {
                  markRead(n.id)
                  navigateTo(`/users/${n.fromUser.id}`, { replace: true })
                }
              },
            })
          } else if (n.type === 'LOTTERY_WIN') {
            notifications.value.push({
              ...n,
              icon: iconMap[n.type],
              iconClick: () => {
                if (n.post) {
                  markRead(n.id)
                  router.push(`/posts/${n.post.id}`)
                }
              },
            })
          } else if (n.type === 'LOTTERY_DRAW') {
            notifications.value.push({
              ...n,
              icon: iconMap[n.type],
              iconClick: () => {
                if (n.post) {
                  markRead(n.id)
                  router.push(`/posts/${n.post.id}`)
                }
              },
            })
          } else if (n.type === 'POST_UPDATED') {
            notifications.value.push({
              ...n,
              src: n.comment.author.avatar,
              iconClick: () => {
                markRead(n.id)
                navigateTo(`/users/${n.comment.author.id}`, { replace: true })
              },
            })
          } else if (n.type === 'USER_ACTIVITY') {
            notifications.value.push({
              ...n,
              src: n.comment.author.avatar,
              iconClick: () => {
                markRead(n.id)
                navigateTo(`/users/${n.comment.author.id}`, { replace: true })
              },
            })
          } else if (n.type === 'MENTION') {
            notifications.value.push({
              ...n,
              icon: iconMap[n.type],
              iconClick: () => {
                if (n.fromUser) {
                  markRead(n.id)
                  navigateTo(`/users/${n.fromUser.id}`, { replace: true })
                }
              },
            })
          } else if (n.type === 'USER_FOLLOWED' || n.type === 'USER_UNFOLLOWED') {
            notifications.value.push({
              ...n,
              icon: iconMap[n.type],
              iconClick: () => {
                if (n.fromUser) {
                  markRead(n.id)
                  navigateTo(`/users/${n.fromUser.id}`, { replace: true })
                }
              },
            })
          } else if (n.type === 'FOLLOWED_POST') {
            notifications.value.push({
              ...n,
              icon: iconMap[n.type],
              iconClick: () => {
                if (n.post) {
                  markRead(n.id)
                  navigateTo(`/posts/${n.post.id}`, { replace: true })
                }
              },
            })
          } else if (n.type === 'POST_SUBSCRIBED' || n.type === 'POST_UNSUBSCRIBED') {
            notifications.value.push({
              ...n,
              icon: iconMap[n.type],
              iconClick: () => {
                if (n.post) {
                  markRead(n.id)
                  navigateTo(`/posts/${n.post.id}`, { replace: true })
                }
              },
            })
          } else if (n.type === 'POST_REVIEW_REQUEST') {
            notifications.value.push({
              ...n,
              src: n.fromUser ? n.fromUser.avatar : null,
              icon: n.fromUser ? undefined : iconMap[n.type],
              iconClick: () => {
                if (n.post) {
                  markRead(n.id)
                  navigateTo(`/posts/${n.post.id}`, { replace: true })
                }
              },
            })
          } else if (n.type === 'REGISTER_REQUEST') {
            notifications.value.push({
              ...n,
              icon: iconMap[n.type],
              iconClick: () => {},
            })
          } else {
            notifications.value.push({
              ...n,
              icon: iconMap[n.type],
            })
          }
        }
      } catch (e) {
        console.error(e)
      }
    }
  }

  const markRead = async (id) => {
    if (!id) return
    const n = notifications.value.find((n) => n.id === id)
    if (!n || n.read) return
    n.read = true
    if (notificationState.unreadCount > 0) notificationState.unreadCount--
    const ok = await markNotificationsRead([id])
    if (!ok) {
      n.read = false
      notificationState.unreadCount++
    } else {
      fetchUnreadCount()
    }
  }
  return {
    fetchNotifications,
    markRead,
    notifications,
    isLoadingMessage,
  }
}

export const { fetchNotifications, markRead, notifications, isLoadingMessage } =
  createFetchNotifications()
