import { ref, computed, onUnmounted } from 'vue'
import { useRequestHeaders, useRequestEvent } from 'nuxt/app'

export const useIsMobile = () => {
  const width = ref(0)
  const isClient = ref(false)

  const isMobileUserAgent = () => {
    let userAgent = ''
    let mobileHint = ''

    if (typeof navigator !== 'undefined') {
      userAgent = navigator.userAgent.toLowerCase()
    } else {
      const event = useRequestEvent()
      const headers = event?.node?.req?.headers || useRequestHeaders()
      userAgent = (headers['user-agent'] || '').toLowerCase()
      mobileHint = (headers['sec-ch-ua-mobile'] || '').toLowerCase()
    }

    const mobileKeywords = [
      'android', 'iphone', 'ipad', 'ipod', 'blackberry', 'windows phone',
      'mobile', 'tablet', 'opera mini', 'iemobile'
    ]

    return mobileHint.includes('?1') || mobileKeywords.some(keyword => userAgent.includes(keyword))
  }

  if (typeof window !== 'undefined') {
    isClient.value = true
    const updateWidth = () => {
      width.value = window.innerWidth
    }
    updateWidth()
    window.addEventListener('resize', updateWidth)
    onUnmounted(() => {
      window.removeEventListener('resize', updateWidth)
    })
  }

  return computed(() => {
    if (isClient.value) {
      return width.value > 0 ? width.value <= 768 : isMobileUserAgent()
    }

    return isMobileUserAgent()
  })
}

