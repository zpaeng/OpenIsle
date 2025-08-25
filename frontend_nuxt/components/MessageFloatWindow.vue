<template>
  <div v-if="floatRoute" class="message-float-window">
    <iframe :src="iframeSrc" frameborder="0"></iframe>
    <div class="float-actions">
      <i class="fas fa-expand" @click="expand"></i>
    </div>
  </div>
</template>

<script setup>
const floatRoute = useState('messageFloatRoute')

const iframeSrc = computed(() => {
  if (!floatRoute.value) return ''
  return floatRoute.value + (floatRoute.value.includes('?') ? '&' : '?') + 'float=1'
})

function expand() {
  if (!floatRoute.value) return
  const target = floatRoute.value
  floatRoute.value = null
  navigateTo(target)
}
</script>

<style scoped>
.message-float-window {
  position: fixed;
  bottom: 0;
  right: 0;
  width: 400px;
  height: 60vh;
  /* height: 80px; */
  max-height: 90vh;
  background-color: var(--background-color);
  border: 1px solid var(--normal-border-color);
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  z-index: 2000;
  display: flex;
  flex-direction: column;
}

.message-float-window iframe {
  width: 100%;
  flex: 1;
}

.float-actions {
  position: absolute;
  top: 4px;
  right: 8px;
  padding: 6px;
}

.float-actions i {
  cursor: pointer;
}
</style>
