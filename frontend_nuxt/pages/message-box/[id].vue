<template>
  <div class="chat-container">
    <div class="window-controls">
      <button v-if="!isFloat" @click="shrink" class="control-btn">
        <i class="fas fa-window-minimize"></i>
      </button>
      <button v-else @click="expand" class="control-btn">
        <i class="fas fa-expand"></i>
      </button>
    </div>
    <div v-if="!loading" class="chat-header">
      <NuxtLink to="/message-box" class="back-button">
        <i class="fas fa-arrow-left"></i>
      </NuxtLink>
      <h2 class="participant-name">
        {{ isChannel ? conversationName : otherParticipant?.username }}
      </h2>
    </div>

    <div class="messages-list" ref="messagesListEl">
      <div v-if="loading" class="loading-container">
        <l-hatch size="28" stroke="4" speed="3.5" color="var(--primary-color)"></l-hatch>
      </div>
      <div v-else-if="error" class="error-container">{{ error }}</div>
      <template v-else>
        <div class="load-more-container" v-if="hasMoreMessages">
          <div @click="loadMoreMessages" :disabled="loadingMore" class="load-more-button">
            {{ loadingMore ? '加载中...' : '查看更多消息' }}
          </div>
        </div>
        <BaseTimeline :items="messages" hover>
          <template #item="{ item }">
            <div class="message-header">
              <div class="user-name">
                {{ item.sender.username }}
              </div>
              <div class="message-timestamp">
                {{ TimeManager.format(item.createdAt) }}
              </div>
            </div>
            <div class="message-content">
              <div class="info-content-text" v-html="renderMarkdown(item.content)"></div>
            </div>
          </template>
        </BaseTimeline>
        <div class="empty-container">
          <BasePlaceholder
            v-if="messages.length === 0"
            text="暂无会话，发送消息试试 🎉"
            icon="fas fa-inbox"
          />
        </div>
      </template>
    </div>

    <div class="message-input-area">
      <MessageEditor :loading="sending" @submit="sendMessage" />
    </div>
  </div>
</template>

<script setup>
import {
  ref,
  onMounted,
  onUnmounted,
  nextTick,
  computed,
  watch,
  onActivated,
  onDeactivated,
} from 'vue'
import { useRoute } from 'vue-router'
import { getToken, fetchCurrentUser } from '~/utils/auth'
import { toast } from '~/main'
import { renderMarkdown } from '~/utils/markdown'
import MessageEditor from '~/components/MessageEditor.vue'
import { useWebSocket } from '~/composables/useWebSocket'
import { useUnreadCount } from '~/composables/useUnreadCount'
import { useChannelsUnreadCount } from '~/composables/useChannelsUnreadCount'
import TimeManager from '~/utils/time'
import BaseTimeline from '~/components/BaseTimeline.vue'
import BasePlaceholder from '~/components/BasePlaceholder.vue'
import { openMessageFloat } from '~/composables/useMessageFloat'

const config = useRuntimeConfig()
const route = useRoute()
const router = useRouter()
const API_BASE_URL = config.public.apiBaseUrl
const { connect, disconnect, subscribe, isConnected } = useWebSocket()
const { fetchUnreadCount: refreshGlobalUnreadCount } = useUnreadCount()
const { fetchChannelUnread: refreshChannelUnread } = useChannelsUnreadCount()
let subscription = null

const messages = ref([])
const participants = ref([])
const loading = ref(true)
const sending = ref(false)
const error = ref(null)
const conversationId = route.params.id
const currentUser = ref(null)
const messagesListEl = ref(null)
let lastMessageEl = null
const currentPage = ref(0)
const totalPages = ref(0)
const loadingMore = ref(false)
let scrollInterval = null
const conversationName = ref('')
const isChannel = ref(false)
const isFloat = computed(() => route.query.float === '1')

const hasMoreMessages = computed(() => currentPage.value < totalPages.value - 1)

const otherParticipant = computed(() => {
  if (isChannel.value || !currentUser.value || participants.value.length === 0) {
    return null
  }
  return participants.value.find((p) => p.id !== currentUser.value.id)
})

function isSentByCurrentUser(message) {
  return message.sender.id === currentUser.value?.id
}

function handleAvatarError(event) {
  event.target.src = '/default-avatar.svg'
}

function shrink() {
  openMessageFloat(route.fullPath)
  router.push('/')
}

function expand() {
  const base = route.fullPath.replace(/(\?|&)float=1/, '')
  window.top.location.href = base
}

function openUser(id) {
  if (isFloat.value) {
    window.top.location.href = `/users/${id}`
  } else {
    navigateTo(`/users/${id}`, { replace: true })
  }
}

// No changes needed here, as renderMarkdown is now imported.
// The old function is removed.

async function fetchMessages(page = 0) {
  if (page === 0) {
    loading.value = true
    messages.value = []
  } else {
    loadingMore.value = true
  }
  error.value = null
  const token = getToken()
  if (!token) {
    toast.error('请先登录')
    loading.value = false
    return
  }

  try {
    const response = await fetch(
      `${API_BASE_URL}/api/messages/conversations/${conversationId}?page=${page}&size=20`,
      {
        headers: { Authorization: `Bearer ${token}` },
      },
    )
    if (!response.ok) throw new Error('无法加载消息')

    const conversationData = await response.json()
    const pageData = conversationData.messages

    if (page === 0) {
      participants.value = conversationData.participants
      conversationName.value = conversationData.name
      isChannel.value = conversationData.channel
    }

    // Since the backend sorts by descending, we need to reverse for correct chat order
    const newMessages = pageData.content.reverse().map((item) => ({
      ...item,
      src: item.sender.avatar,
      iconClick: () => {
        openUser(item.sender.id)
      },
    }))

    const list = messagesListEl.value
    const oldScrollHeight = list ? list.scrollHeight : 0

    if (page === 0) {
      messages.value = newMessages
    } else {
      messages.value = [...newMessages, ...messages.value]
    }

    currentPage.value = pageData.number
    totalPages.value = pageData.totalPages

    // Scrolling is now fully handled by the watcher
    await nextTick()
    if (page > 0 && list) {
      const newScrollHeight = list.scrollHeight
      list.scrollTop = newScrollHeight - oldScrollHeight
    }
  } catch (e) {
    error.value = e.message
    toast.error(e.message)
  } finally {
    loading.value = false
    loadingMore.value = false
  }
}

async function loadMoreMessages() {
  if (hasMoreMessages.value && !loadingMore.value) {
    await fetchMessages(currentPage.value + 1)
  }
}

async function sendMessage(content, clearInput) {
  if (!content.trim()) return
  sending.value = true
  const token = getToken()
  try {
    let response
    if (isChannel.value) {
      response = await fetch(
        `${API_BASE_URL}/api/messages/conversations/${conversationId}/messages`,
        {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({ content }),
        },
      )
    } else {
      const recipient = otherParticipant.value
      if (!recipient) {
        toast.error('无法确定收信人')
        return
      }
      response = await fetch(`${API_BASE_URL}/api/messages`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          recipientId: recipient.id,
          content: content,
        }),
      })
    }
    if (!response.ok) throw new Error('发送失败')

    const newMessage = await response.json()
    messages.value.push({
      ...newMessage,
      src: newMessage.sender.avatar,
      iconClick: () => {
        openUser(newMessage.sender.id)
      },
    })
    clearInput()
    setTimeout(() => {
      scrollToBottom()
    }, 100)
  } catch (e) {
    toast.error(e.message)
  } finally {
    sending.value = false
  }
}

async function markConversationAsRead() {
  const token = getToken()
  if (!token) return
  try {
    await fetch(`${API_BASE_URL}/api/messages/conversations/${conversationId}/read`, {
      method: 'POST',
      headers: { Authorization: `Bearer ${token}` },
    })
    // After marking as read, refresh the global unread count
    refreshGlobalUnreadCount()
    refreshChannelUnread()
  } catch (e) {
    console.error('Failed to mark conversation as read', e)
  }
}

function scrollToBottom() {
  if (messagesListEl.value) {
    const element = messagesListEl.value
    // 強制滾動到底部，使用 smooth 行為確保視覺效果
    element.scrollTop = element.scrollHeight

    // 再次確認滾動位置
    setTimeout(() => {
      if (element.scrollTop < element.scrollHeight - element.clientHeight) {
        element.scrollTop = element.scrollHeight
      }
    }, 50)
  }
}

watch(
  messages,
  async (newMessages) => {
    if (newMessages.length === 0) return

    await nextTick()

    // Simple, reliable scroll to bottom
    setTimeout(() => {
      scrollToBottom()
    }, 100)
  },
  { deep: true },
)

onMounted(async () => {
  currentUser.value = await fetchCurrentUser()
  if (currentUser.value) {
    await fetchMessages(0)
    await markConversationAsRead()
    const token = getToken()
    if (token && !isConnected.value) {
      connect(token)
    }
  } else {
    toast.error('请先登录')
    loading.value = false
  }
})

watch(isConnected, (newValue) => {
  if (newValue) {
    // 等待一小段时间确保连接稳定
    setTimeout(() => {
      subscription = subscribe(`/topic/conversation/${conversationId}`, (message) => {
        // 避免重复显示当前用户发送的消息
        if (message.sender.id !== currentUser.value.id) {
          messages.value.push({
            ...message,
            src: message.sender.avatar,
            iconClick: () => {
              openUser(message.sender.id)
            },
          })
          // 实时收到消息时自动标记为已读
          markConversationAsRead()
          setTimeout(() => {
            scrollToBottom()
          }, 100)
        }
      })
    }, 500)
  }
})

onActivated(async () => {
  // This will be called every time the component is activated (navigated to)
  if (currentUser.value) {
    await fetchMessages(0)
    await markConversationAsRead()

    // 確保滾動到底部 - 使用多重延遲策略
    await nextTick()
    setTimeout(() => {
      scrollToBottom()
    }, 100)
    setTimeout(() => {
      scrollToBottom()
    }, 300)
    setTimeout(() => {
      scrollToBottom()
    }, 500)

    if (!isConnected.value) {
      const token = getToken()
      if (token) connect(token)
    }
  }
})

onDeactivated(() => {
  if (subscription) {
    subscription.unsubscribe()
    subscription = null
  }
  disconnect()
})

onUnmounted(() => {
  if (subscription) {
    subscription.unsubscribe()
    subscription = null
  }
  disconnect()
})
</script>

<style scoped>
.chat-container {
  display: flex;
  flex-direction: column;
  margin: 0 auto;
  overflow: auto;
  height: calc(100vh - var(--header-height));
  position: relative;
}

.window-controls {
  position: absolute;
  top: 10px;
  right: 10px;
  z-index: 10;
}

.control-btn {
  background: transparent;
  border: none;
  cursor: pointer;
  color: var(--text-color-primary);
  margin-left: 4px;
}

.chat-header {
  display: flex;
  position: sticky;
  top: 0;
  z-index: 100;
  align-items: center;
  padding: 10px 20px;
  border-bottom: 1px solid var(--normal-border-color);
  background-color: var(--background-color-blur);
  backdrop-filter: var(--blur-10);
}

.back-button {
  font-size: 18px;
  color: var(--text-color-primary);
  margin-right: 15px;
  cursor: pointer;
}

.participant-name {
  font-size: 18px;
  font-weight: 600;
  margin: 0;
}

.messages-list {
  overflow-y: auto;
  padding: 20px;
  padding-bottom: 100px;
  display: flex;
  flex-direction: column;
  margin-bottom: 10px;
}

.load-more-container {
  text-align: center;
}

.load-more-button {
  color: var(--primary-color);
  font-size: 12px;
  cursor: pointer;
}

.load-more-button:hover {
  text-decoration: underline;
}

.message-item {
  display: flex;
  gap: 10px;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  align-self: flex-end;
}

.message-content {
  display: flex;
  flex-direction: column;
}

.message-timestamp {
  font-size: 11px;
  color: var(--text-color-secondary);
  opacity: 0.6;
}

.message-header {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 10px;
}

.user-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-color);
}

.message-item.sent {
  align-self: flex-end;
  flex-direction: row-reverse;
}

.message-item.sent .message-timestamp {
  text-align: right;
}

/* Received messages */
.message-item.received {
  align-self: flex-start;
}

.message-item.received .message-content {
  align-items: flex-start;
}

.message-item.received .message-bubble {
  background-color: var(--bg-color-soft);
  border: 1px solid var(--border-color);
  border-bottom-left-radius: 4px;
}

.message-input-area {
  margin-left: 20px;
  margin-right: 20px;
}

.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

.error-container {
  text-align: center;
  padding: 50px;
  color: var(--text-color-secondary);
}

@media (max-width: 768px) {
  .messages-list {
    padding: 10px;
  }
}

.message-input-area {
  margin-left: 10px;
  margin-right: 10px;
}
</style>
