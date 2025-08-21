<template>
  <div class="messages-container">
    <div class="messages-header">
      <h1 class="messages-title">站内信</h1>
    </div>
    
    <div v-if="loading" class="loading-container">
      <div class="loading-text">加载中...</div>
    </div>
    
    <div v-else-if="error" class="error-container">
      <div class="error-text">{{ error }}</div>
    </div>
    
    <div v-else-if="conversations.length === 0" class="empty-container">
      <div class="empty-text">暂无会话</div>
    </div>
    
    <div v-else class="conversations-list">
      <div
        v-for="convo in conversations"
        :key="convo.id"
        class="conversation-item"
        @click="goToConversation(convo.id)"
      >
        <div class="conversation-avatar">
          <img
            :src="getOtherParticipant(convo)?.avatar || '/default-avatar.svg'"
            :alt="getOtherParticipant(convo)?.username || '用户'"
            class="avatar-img"
            @error="handleAvatarError"
          />
        </div>
        
        <div class="conversation-content">
          <div class="conversation-header">
            <div class="participant-name">
              {{ getOtherParticipant(convo)?.username || '未知用户' }}
            </div>
            <div class="message-time">
              {{ formatTime(convo.lastMessage?.createdAt || convo.createdAt) }}
            </div>
          </div>
          
          <div class="last-message-row">
             <div class="last-message">
               {{ convo.lastMessage ? convo.lastMessage.content : '暂无消息' }}
             </div>
             <div v-if="convo.unreadCount > 0" class="unread-count-badge">
               {{ convo.unreadCount }}
             </div>
           </div>
          </div>
        </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, onActivated } from 'vue';
import { useRouter } from 'vue-router';
import { getToken, fetchCurrentUser } from '~/utils/auth'
import { toast } from '~/main'
import { formatMessageTime } from '~/utils/messageTime'
import { useWebSocket } from '~/composables/useWebSocket';
import { useUnreadCount } from '~/composables/useUnreadCount';

const config = useRuntimeConfig()
const conversations = ref([]);
const loading = ref(true);
const error = ref(null);
const router = useRouter();
const currentUser = ref(null);
const API_BASE_URL = config.public.apiBaseUrl
const { connect, disconnect, subscribe, isConnected } = useWebSocket();
const { fetchUnreadCount: refreshGlobalUnreadCount } = useUnreadCount();
let subscription = null;

async function fetchConversations() {
  const token = getToken();
   if (!token) {
    toast.error('请先登录');
    return;
  }
  try {
    const response = await fetch(`${API_BASE_URL}/api/messages/conversations`, {
      method: 'GET',
      headers: { Authorization: `Bearer ${token}` },
    });
    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }
    const data = await response.json();
    conversations.value = data;
  } catch (e) {
    error.value = '无法加载会话列表。';
  } finally {
    loading.value = false;
  }
}

// 获取对话中的另一个参与者（非当前用户）
function getOtherParticipant(conversation) {
  if (!currentUser.value || !conversation.participants) return null
  return conversation.participants.find(p => p.id !== currentUser.value.id)
}

// 格式化时间
function formatTime(timeString) {
  if (!timeString) return ''
  return formatMessageTime(timeString)
}

// 头像加载失败处理
function handleAvatarError(event) {
  event.target.src = '/default-avatar.svg'
}

onActivated(async () => {
  loading.value = true;
  currentUser.value = await fetchCurrentUser();
  
  if (currentUser.value) {
    await fetchConversations();
    refreshGlobalUnreadCount(); // Refresh global count when entering the list
    const token = getToken();
    if (token && !isConnected.value) {
      connect(token);
    }
  } else {
    loading.value = false;
  }
});

watch(isConnected, (newValue) => {
  
  if (newValue && currentUser.value) {
    const destination = `/topic/user/${currentUser.value.id}/messages`;
    
    // 清理旧的订阅
    if (subscription) {
      subscription.unsubscribe();
    }
    
    subscription = subscribe(destination, (message) => {
      fetchConversations();
    });
    
  }
});

onUnmounted(() => {
  if (subscription) {
    subscription.unsubscribe();
  }
  disconnect();
});

function goToConversation(id) {
  router.push(`/messages/${id}`);
}
</script>

<style scoped>
.messages-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.messages-header {
  margin-bottom: 24px;
}

.messages-title {
  font-size: 28px;
  font-weight: 600;
  color: #1a1a1a;
  margin: 0;
}

.loading-container, .error-container, .empty-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 200px;
}

.loading-text, .error-text, .empty-text {
  font-size: 16px;
  color: #666;
}

.error-text {
  color: #e53e3e;
}

.conversations-list {
  background: #fff;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  overflow: hidden;
  max-height: 600px;
  overflow-y: auto;
}

/* 美化滚动条 */
.conversations-list::-webkit-scrollbar {
  width: 6px;
}

.conversations-list::-webkit-scrollbar-track {
  background: #f1f5f9;
  border-radius: 3px;
}

.conversations-list::-webkit-scrollbar-thumb {
  background: #cbd5e1;
  border-radius: 3px;
}

.conversations-list::-webkit-scrollbar-thumb:hover {
  background: #94a3b8;
}

.conversation-item {
  display: flex;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #f7fafc;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.conversation-item:last-child {
  border-bottom: none;
}

.conversation-item:hover {
  background-color: #f7fafc;
}

.conversation-avatar {
  flex-shrink: 0;
  margin-right: 16px;
}

.avatar-img {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #e2e8f0;
}

.conversation-content {
  flex: 1;
  min-width: 0;
}

.conversation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.participant-name {
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
  truncate: true;
}

.message-time {
  font-size: 12px;
  color: #a0aec0;
  flex-shrink: 0;
  margin-left: 12px;
}

.last-message-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.last-message {
  font-size: 14px;
  color: #4a5568;
  line-height: 1.4;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  flex-grow: 1;
  padding-right: 10px; /* Add some space between message and badge */
}

.unread-count-badge {
  background-color: #f56c6c;
  color: white;
  font-size: 12px;
  font-weight: bold;
  padding: 2px 8px;
  border-radius: 12px;
  line-height: 1.5;
  flex-shrink: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .messages-container {
    padding: 16px 12px;
  }
  
  .messages-title {
    font-size: 24px;
  }
  
  .conversations-list {
    max-height: 500px;
  }
  
  .conversation-item {
    padding: 12px 16px;
  }
  
  .avatar-img {
    width: 40px;
    height: 40px;
  }
  
  .participant-name {
    font-size: 15px;
  }
  
  .message-time {
    font-size: 11px;
  }
  
  .last-message {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .messages-container {
    padding: 12px 8px;
  }
  
  .conversations-list {
    max-height: 400px;
  }
  
  .conversation-item {
    padding: 10px 12px;
  }
  
  .avatar-img {
    width: 36px;
    height: 36px;
  }
  
  .conversation-avatar {
    margin-right: 12px;
  }
}

/* 大屏幕设备 */
@media (min-width: 1024px) {
  .conversations-list {
    max-height: 700px;
  }
}
</style>