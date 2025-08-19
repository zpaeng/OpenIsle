import { navigateTo, useRuntimeConfig } from 'nuxt/app'
import { reactive, ref } from 'vue'
import { toast } from '~/composables/useToast'
import { authState, getToken } from '~/utils/auth'
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
  POINT_REDEEM: 'fas fa-gift',
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
  const notificationsAll = ref([])
  const notificationsUnread = ref([])
  const isLoadingAll = ref(false)
  const isLoadingUnread = ref(false)
  const pageSize = 30

  function pushNotification(n, target) {
    if (n.type === 'COMMENT_REPLY') {
      target.push({
        ...n,
        src: n.comment.author.avatar,
        iconClick: () => {
          markRead(n.id)
          navigateTo(`/users/${n.comment.author.id}`, { replace: true })
        },
      })
    } else if (n.type === 'REACTION') {
      target.push({
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
      target.push({
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
      target.push({
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
      target.push({
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
      target.push({
        ...n,
        src: n.comment.author.avatar,
        iconClick: () => {
          markRead(n.id)
          navigateTo(`/users/${n.comment.author.id}`, { replace: true })
        },
      })
    } else if (n.type === 'USER_ACTIVITY') {
      target.push({
        ...n,
        src: n.comment.author.avatar,
        iconClick: () => {
          markRead(n.id)
          navigateTo(`/users/${n.comment.author.id}`, { replace: true })
        },
      })
    } else if (n.type === 'MENTION') {
      target.push({
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
      target.push({
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
      target.push({
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
      target.push({
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
      target.push({
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
      target.push({
        ...n,
        icon: iconMap[n.type],
        iconClick: () => {},
      })
    } else {
      target.push({
        ...n,
        icon: iconMap[n.type],
      })
    }
  }

  async function fetchAllNotifications(page = 0) {
    const config = useRuntimeConfig()
    const API_BASE_URL = config.public.apiBaseUrl
    try {
      const token = getToken()
      if (!token) {
        toast.error('请先登录')
        return
      }
      isLoadingAll.value = true
      const res = await fetch(`${API_BASE_URL}/api/notifications?page=${page}&size=${pageSize}`, {
        headers: { Authorization: `Bearer ${token}` },
      })
      isLoadingAll.value = false
      if (!res.ok) {
        toast.error('获取通知失败')
        return
      }
      const data = await res.json()
      if (page === 0) notificationsAll.value = []
      for (const n of data) {
        pushNotification(n, notificationsAll.value)
      }
      return data.length
    } catch (e) {
      console.error(e)
      return 0
    }
  }

  async function fetchUnreadNotifications(page = 0) {
    const config = useRuntimeConfig()
    const API_BASE_URL = config.public.apiBaseUrl
    try {
      const token = getToken()
      if (!token) {
        toast.error('请先登录')
        return
      }
      isLoadingUnread.value = true
      const res = await fetch(
        `${API_BASE_URL}/api/notifications/unread?page=${page}&size=${pageSize}`,
        { headers: { Authorization: `Bearer ${token}` } },
      )
      isLoadingUnread.value = false
      if (!res.ok) {
        toast.error('获取通知失败')
        return
      }
      const data = await res.json()
      if (page === 0) notificationsUnread.value = []
      for (const n of data) {
        pushNotification(n, notificationsUnread.value)
      }
      return data.length
    } catch (e) {
      console.error(e)
      return 0
    }
  }

  async function markRead(id) {
    if (!id) return
    const nAll = notificationsAll.value.find((n) => n.id === id)
    const nUnreadIndex = notificationsUnread.value.findIndex((n) => n.id === id)
    const target = nAll || notificationsUnread.value[nUnreadIndex]
    if (!target || target.read) return
    target.read = true
    if (nUnreadIndex !== -1) notificationsUnread.value.splice(nUnreadIndex, 1)
    if (notificationState.unreadCount > 0) notificationState.unreadCount--
    const ok = await markNotificationsRead([id])
    if (!ok) {
      target.read = false
      if (nUnreadIndex !== -1) notificationsUnread.value.splice(nUnreadIndex, 0, target)
      notificationState.unreadCount++
    } else {
      fetchUnreadCount()
    }
  }

  async function markAllRead() {
    const ids = [
      ...new Set(
        [...notificationsAll.value, ...notificationsUnread.value]
          .filter((n) => n.type !== 'REGISTER_REQUEST' && !n.read)
          .map((n) => n.id),
      ),
    ]
    if (ids.length === 0) return
    notificationsAll.value.forEach((n) => {
      if (n.type !== 'REGISTER_REQUEST') n.read = true
    })
    notificationsUnread.value = []
    notificationState.unreadCount = 0
    const ok = await markNotificationsRead(ids)
    if (!ok) {
      await fetchUnreadCount()
      return
    }
    fetchUnreadCount()
    if (authState.role === 'ADMIN') {
      toast.success('已读所有消息（注册请求除外）')
    } else {
      toast.success('已读所有消息')
    }
  }

  return {
    fetchAllNotifications,
    fetchUnreadNotifications,
    markRead,
    notificationsAll,
    notificationsUnread,
    isLoadingAll,
    isLoadingUnread,
    markAllRead,
  }
}

export const {
  fetchAllNotifications,
  fetchUnreadNotifications,
  markRead,
  notificationsAll,
  notificationsUnread,
  isLoadingAll,
  isLoadingUnread,
  markAllRead,
} = createFetchNotifications()
