<template>
  <div v-if="floatRoute" class="message-float-window" :style="{ height: floatHeight }">
    <iframe :src="iframeSrc" frameborder="0"></iframe>

    <div class="float-actions">
      <i
        class="fas fa-chevron-down"
        v-if="floatHeight !== MINI_HEIGHT"
        title="收起至 100px"
        @click="collapseToMini"
      ></i>
      <!-- 回弹：60vh -->
      <i
        class="fas fa-chevron-up"
        v-if="floatHeight !== DEFAULT_HEIGHT"
        title="回弹至 60vh"
        @click="reboundToDefault"
      ></i>
      <!-- 全屏打开（原有逻辑） -->
      <i class="fas fa-expand" title="在页面中打开" @click="expand"></i>
    </div>
  </div>
</template>

<script setup>
const floatRoute = useState('messageFloatRoute')

const DEFAULT_HEIGHT = '60vh'
const MINI_HEIGHT = '45px'
const floatHeight = ref(DEFAULT_HEIGHT)

const iframeSrc = computed(() => {
  if (!floatRoute.value) return ''
  return floatRoute.value + (floatRoute.value.includes('?') ? '&' : '?') + 'float=1'
})

function collapseToMini() {
  floatHeight.value = MINI_HEIGHT
}

function reboundToDefault() {
  floatHeight.value = DEFAULT_HEIGHT
}

function expand() {
  if (!floatRoute.value) return
  const target = floatRoute.value
  floatRoute.value = null
  navigateTo(target)
}

// 当浮窗重新出现时，恢复默认高度
watch(
  () => floatRoute.value,
  (v) => {
    if (v) floatHeight.value = DEFAULT_HEIGHT
  },
)
</script>

<style scoped>
.message-float-window {
  position: fixed;
  bottom: 0;
  right: 0;
  width: 400px;
  /* 高度由内联样式绑定控制：60vh / 100px */
  max-height: 90vh;
  background-color: var(--background-color);
  border: 1px solid var(--normal-border-color);
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
  z-index: 2000;
  display: flex;
  flex-direction: column;
  transition: height 0.25s ease; /* 平滑过渡 */
}

.message-float-window iframe {
  width: 100%;
  flex: 1;
}

.float-actions {
  position: absolute;
  top: 4px;
  right: 8px;
  padding: 12px;
  display: flex;
  gap: 10px;
}

.float-actions i {
  cursor: pointer;
  font-size: 14px;
  opacity: 0.9;
}

.float-actions i:hover {
  opacity: 1;
}
</style>
