<template>
  <NuxtImg
    v-bind="passAttrs"
    :src="src"
    :alt="alt"
    loading="lazy"
    :placeholder="placeholder"
    placeholder-class="base-image-ph"
    @load="onLoad"
    @error="onError"
    :class="['base-image', passAttrs.class, { 'is-loaded': loaded }]"
  />
</template>

<script setup>
import { computed, ref } from 'vue'
import { useAttrs } from 'vue'

const props = defineProps({
  src: { type: String, required: true },
  alt: { type: String, default: '' },
})

const attrs = useAttrs()

const passAttrs = computed(() => {
  const { placeholder, ...rest } = attrs
  return rest
})

const loaded = ref(false)
const img = useImage()

const placeholder = computed(() => {
  if (!props.src) return undefined
  return img(props.src, { w: 16, h: 16, f: 'webp', q: 20, blur: 1 })
})

function onLoad() {
  loaded.value = true
}
function onError() {
  loaded.value = true
}
</script>

<style scoped>
.base-image {
  display: block;
  transition:
    filter 0.35s ease,
    transform 0.35s ease,
    opacity 0.35s ease;
  opacity: 0.92;
}

.base-image-ph {
  filter: blur(10px) saturate(0.85);
  transform: scale(1.02);
}

.base-image.is-loaded {
  filter: none;
  transform: none;
  opacity: 1;
}
</style>
