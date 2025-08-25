import { ref } from 'vue'
import { Client } from '@stomp/stompjs'
import SockJS from 'sockjs-client/dist/sockjs.min.js'
import { useRuntimeConfig } from '#app'

const client = ref(null)
const isConnected = ref(false)

const connect = (token) => {
  if (isConnected.value) {
    return
  }

  const config = useRuntimeConfig()
  const API_BASE_URL = config.public.apiBaseUrl
  const socketUrl = `${API_BASE_URL}/api/sockjs`

  const socket = new SockJS(socketUrl)
  const stompClient = new Client({
    webSocketFactory: () => socket,
    connectHeaders: {
      Authorization: `Bearer ${token}`,
    },
    debug: function (str) {},
    reconnectDelay: 5000,
    heartbeatIncoming: 4000,
    heartbeatOutgoing: 4000,
  })

  stompClient.onConnect = (frame) => {
    isConnected.value = true
  }

  stompClient.onStompError = (frame) => {
    console.error('WebSocket STOMP error:', frame)
  }

  stompClient.activate()
  client.value = stompClient
}

const disconnect = () => {
  if (client.value) {
    isConnected.value = false
    client.value.deactivate()
    client.value = null
  }
}

const subscribe = (destination, callback) => {
  if (!isConnected.value || !client.value || !client.value.connected) {
    return null
  }

  try {
    const subscription = client.value.subscribe(destination, (message) => {
      try {
        if (
          destination.includes('/queue/unread-count') ||
          destination.includes('/queue/channel-unread')
        ) {
          callback(message)
        } else {
          const parsedMessage = JSON.parse(message.body)
          callback(parsedMessage)
        }
      } catch (error) {
        callback(message)
      }
    })

    return subscription
  } catch (error) {
    return null
  }
}

export function useWebSocket() {
  return {
    client,
    isConnected,
    connect,
    disconnect,
    subscribe,
  }
}
