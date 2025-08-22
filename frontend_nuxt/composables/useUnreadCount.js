import { ref, watch, onMounted } from 'vue';
import { useWebSocket } from './useWebSocket';
import { getToken } from '~/utils/auth';

const count = ref(0);
let isInitialized = false;
let wsSubscription = null;

export function useUnreadCount() {
  const config = useRuntimeConfig();
  const API_BASE_URL = config.public.apiBaseUrl;
  const { subscribe, isConnected, connect } = useWebSocket();

  const fetchUnreadCount = async () => {
    const token = getToken();
    if (!token) {
      count.value = 0;
      return;
    }
    try {
      const response = await fetch(`${API_BASE_URL}/api/messages/unread-count`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (response.ok) {
        const data = await response.json();
        count.value = data;
      }
    } catch (error) {
      console.error('Failed to fetch unread count:', error);
    }
  };

  const initialize = async () => {
    const token = getToken();
    if (!token) {
      count.value = 0;
      return;
    }

    // 总是获取最新的未读数量
    fetchUnreadCount();
    
    // 确保WebSocket连接
    if (!isConnected.value) {
      connect(token);
    }
    
    // 设置WebSocket监听
    await setupWebSocketListener();
  };

  const setupWebSocketListener = async () => {
    // 只有在还没有订阅的情况下才设置监听
    if (!wsSubscription) {
      
      watch(isConnected, (newValue) => {
        if (newValue && !wsSubscription) {
          const destination = `/user/queue/unread-count`;
          wsSubscription = subscribe(destination, (message) => {
            const unreadCount = parseInt(message.body, 10);
            if (!isNaN(unreadCount)) {
              count.value = unreadCount;
            }
          });
        }
      }, { immediate: true });
    }
  };

  // 自动初始化逻辑 - 确保每次调用都能获取到未读数量并设置监听
  const token = getToken();
  if (token) {
    if (!isInitialized) {
      isInitialized = true;
      initialize(); // 完整初始化，包括WebSocket监听
    } else {
      // 即使已经初始化，也要确保获取最新的未读数量并确保WebSocket监听存在
      fetchUnreadCount();
      
      // 确保WebSocket连接和监听都存在
      if (!isConnected.value) {
        connect(token);
      }
      setupWebSocketListener();
    }
  }

  return {
    count,
    fetchUnreadCount,
    initialize,
  };
}