<template>
  <div v-if="chatFloating" class="chat-floating">
    <iframe :src="iframeSrc" class="chat-frame"></iframe>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const chatFloating = useState('chatFloating', () => false)
const chatPath = useState('chatPath', () => '/message-box')

const iframeSrc = computed(() =>
  chatPath.value.includes('?') ? `${chatPath.value}&float=1` : `${chatPath.value}?float=1`,
)

if (process.client) {
  window.addEventListener('message', (event) => {
    if (event.data?.type === 'maximize-chat') {
      chatFloating.value = false
      navigateTo(event.data.path || chatPath.value)
    }
  })
}
</script>

<style scoped>
.chat-floating {
  position: fixed;
  bottom: 20px;
  right: 20px;
  width: 400px;
  height: 70vh;
  max-height: 600px;
  background: var(--background-color);
  border: 1px solid var(--normal-border-color);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  z-index: 2000;
  display: flex;
  flex-direction: column;
}

.chat-frame {
  width: 100%;
  height: 100%;
  border: none;
}

@media (max-width: 500px) {
  .chat-floating {
    right: 0;
    width: 100%;
    height: 60vh;
  }
}
</style>
