<template>
  <div v-if="state.open" class="message-float">
    <iframe :src="frameSrc" class="message-frame" />
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useMessageFloat } from '~/composables/useMessageFloat'

const state = useMessageFloat()

const frameSrc = computed(() => {
  const base = state.value.path || '/message-box'
  return base + (base.includes('?') ? '&' : '?') + 'float=1'
})
</script>

<style scoped>
.message-float {
  position: fixed;
  bottom: 0;
  right: 0;
  width: 380px;
  height: 500px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.2);
  z-index: 2000;
  background: var(--background-color);
}

.message-frame {
  width: 100%;
  height: 100%;
  border: none;
}

@media (max-width: 600px) {
  .message-float {
    width: 100%;
    height: 100%;
    right: 0;
  }
}
</style>
