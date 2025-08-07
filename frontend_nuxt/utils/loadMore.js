import { ref, onMounted, onUnmounted, onActivated, nextTick } from 'vue'

export function useScrollLoadMore(loadMore, offset = 50) {
  const savedScrollTop = ref(0)

  const handleScroll = () => {
    if (!process.client) return
    const scrollTop = window.scrollY || document.documentElement.scrollTop
    const scrollHeight = document.documentElement.scrollHeight
    const windowHeight = window.innerHeight
    savedScrollTop.value = scrollTop
    if (scrollHeight - (scrollTop + windowHeight) <= offset) {
      loadMore()
    }
  }

  onMounted(() => {
    if (process.client) {
      window.addEventListener('scroll', handleScroll, { passive: true })
    }
  })

  onUnmounted(() => {
    if (process.client) {
      window.removeEventListener('scroll', handleScroll)
    }
  })

  onActivated(() => {
    if (process.client) {
      nextTick(() => {
        window.scrollTo({ top: savedScrollTop.value })
      })
    }
  })

  return { savedScrollTop }
}
