import { ref, computed } from 'vue'

const width = ref(0)
const isClient = ref(false)

// 检测移动设备的用户代理字符串
const isMobileUserAgent = () => {
  if (typeof navigator === 'undefined') return false
  
  const userAgent = navigator.userAgent.toLowerCase()
  const mobileKeywords = [
    'android', 'iphone', 'ipad', 'ipod', 'blackberry', 'windows phone',
    'mobile', 'tablet', 'opera mini', 'iemobile'
  ]
  
  return mobileKeywords.some(keyword => userAgent.includes(keyword))
}

// 客户端初始化
if (typeof window !== 'undefined') {
  isClient.value = true
  width.value = window.innerWidth
  window.addEventListener('resize', () => {
    width.value = window.innerWidth
  })
}

// 服务端和客户端的移动设备检测
export const isMobile = computed(() => {
  if (isClient.value) {
    // 客户端：优先使用窗口宽度，如果窗口宽度不可用则使用用户代理
    return width.value > 0 ? width.value <= 768 : isMobileUserAgent()
  } else {
    // 服务端：使用用户代理字符串
    return isMobileUserAgent()
  }
})
