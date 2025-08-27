<template>
  <NuxtImg
    v-bind="attrs"
    :src="src"
    :alt="alt"
    loading="lazy"
    :placeholder="placeholder"
    placeholder-class="base-image-ph"
    @load="onLoad"
    :class="['base-image', attrs.class, { 'is-loaded': loaded }]"
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
const loaded = ref(false)
const img = useImage()
const placeholder = computed(() => img(props.src, { w: 16, h: 16, f: 'webp', q: 40, blur: 2 }))

function onLoad() {
  loaded.value = true
}
</script>

<style scoped>
.base-image {
  opacity: 0;
  transition: opacity 0.25s;
}
.base-image.is-loaded {
  opacity: 1;
}
:deep(img.base-image-ph) {
  filter: blur(10px);
  transform: scale(1.03);
}
</style>
