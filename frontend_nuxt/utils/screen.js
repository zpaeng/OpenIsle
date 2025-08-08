import { ref, computed } from 'vue'

const width = ref(0)
const isClient = ref(false)

if (typeof window !== 'undefined') {
  isClient.value = true
  width.value = window.innerWidth
  window.addEventListener('resize', () => {
    width.value = window.innerWidth
  })
}

export const isMobile = computed(() => isClient.value && width.value <= 768)
