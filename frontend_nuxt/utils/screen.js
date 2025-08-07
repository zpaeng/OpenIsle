import { ref, computed } from 'vue'

const width = ref(0)

if (process.client) {
  width.value = window.innerWidth
  window.addEventListener('resize', () => {
    width.value = window.innerWidth
  })
}

export const isMobile = computed(() => width.value <= 768)
