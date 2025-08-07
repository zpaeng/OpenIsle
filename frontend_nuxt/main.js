export const API_BASE_URL = 'http://www.open-isle.com'
// export const API_BASE_URL = 'http://127.0.0.1:8081'
export const GOOGLE_CLIENT_ID = '777830451304-nt8afkkap18gui4f9entcha99unal744.apps.googleusercontent.com'
export const GITHUB_CLIENT_ID = 'Ov23liVkO1NPAX5JyWxJ'
export const DISCORD_CLIENT_ID = '1394985417044000779'
export const TWITTER_CLIENT_ID = 'ZTRTU05KSk9KTTJrTTdrVC1tc1E6MTpjaQ'

// 导入真实的 toast 功能
import { useToast } from 'vue-toastification'

// 创建一个全局的 toast 实例
let toastInstance = null

// 初始化 toast 实例的函数
export const initToast = () => {
  if (!toastInstance) {
    toastInstance = useToast()
  }
  return toastInstance
}

// 导出 toast 对象，提供 success 和 error 方法
export const toast = {
  success: (message) => {
    const toast = initToast()
    toast.success(message)
  },
  error: (message) => {
    const toast = initToast()
    toast.error(message)
  }
}