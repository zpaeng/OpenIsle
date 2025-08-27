<template>
  <div class="multi-tabs">
    <div class="multi-tabs-header" :class="headerClass">
      <div
        v-for="tab in tabs"
        :key="tab.name"
        :class="[itemClass, { selected: current === tab.name }]"
        @click="select(tab.name)"
      >
        <div :class="labelClass">
          <i v-if="tab.icon" :class="tab.icon"></i>
          {{ tab.label }}
        </div>
      </div>
      <slot name="header-extra" />
    </div>
    <div class="multi-tabs-content" @touchstart="onTouchStart" @touchend="onTouchEnd">
      <slot :selected="current" />
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  tabs: { type: Array, required: true },
  modelValue: { type: String, required: true },
  headerClass: { type: String, default: '' },
  itemClass: { type: String, default: '' },
  labelClass: { type: String, default: '' },
})

const emit = defineEmits(['update:modelValue'])

const current = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val),
})

const select = (name) => {
  current.value = name
}

const startX = ref(0)
const startY = ref(0)

const onTouchStart = (e) => {
  const t = e.touches[0]
  startX.value = t.clientX
  startY.value = t.clientY
}

const onTouchEnd = (e) => {
  const t = e.changedTouches[0]
  const dx = t.clientX - startX.value
  const dy = t.clientY - startY.value
  if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > 50) {
    const idx = props.tabs.findIndex((t) => t.name === current.value)
    if (dx < 0 && idx < props.tabs.length - 1) {
      current.value = props.tabs[idx + 1].name
    } else if (dx > 0 && idx > 0) {
      current.value = props.tabs[idx - 1].name
    }
  }
}
</script>

<style scoped>
.multi-tabs-header {
  display: flex;
  flex-direction: row;
  overflow-x: auto;
  scrollbar-width: none;
}
.multi-tabs-header::-webkit-scrollbar {
  display: none;
}
.multi-tabs-content {
  width: 100%;
}
</style>
