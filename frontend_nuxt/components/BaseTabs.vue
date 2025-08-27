<template>
  <div class="base-tabs" @touchstart="onTouchStart" @touchend="onTouchEnd">
    <div
      v-for="tab in tabs"
      :key="tab.name"
      :class="[itemClass, { [activeClass]: tab.name === modelValue }]"
      @click="emit('update:modelValue', tab.name)"
    >
      <slot name="tab" :tab="tab">{{ tab.label }}</slot>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  tabs: { type: Array, required: true },
  modelValue: { type: String, required: true },
  itemClass: { type: String, default: '' },
  activeClass: { type: String, default: 'active' },
})

const emit = defineEmits(['update:modelValue'])

const startX = ref(0)

function onTouchStart(e) {
  startX.value = e.changedTouches[0].clientX
}

function onTouchEnd(e) {
  const diff = e.changedTouches[0].clientX - startX.value
  const threshold = 50
  if (Math.abs(diff) > threshold) {
    const currentIndex = props.tabs.findIndex((t) => t.name === props.modelValue)
    if (currentIndex === -1) return
    const newIndex = currentIndex + (diff < 0 ? 1 : -1)
    if (newIndex >= 0 && newIndex < props.tabs.length) {
      emit('update:modelValue', props.tabs[newIndex].name)
    }
  }
}
</script>

<style scoped>
.base-tabs {
  display: flex;
}
</style>
