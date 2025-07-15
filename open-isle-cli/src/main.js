import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/global.css'
import Toast, { POSITION } from 'vue-toastification'
import 'vue-toastification/dist/index.css'
import { useToast } from 'vue-toastification'
import { checkToken, clearToken } from './utils/auth'
import { initTheme } from './utils/theme'

// Configurable API domain and port
export const API_DOMAIN = 'http://127.0.0.1'
export const API_PORT = 8081

// export const API_DOMAIN = 'http://47.82.99.208'
// export const API_PORT = 8080

// export const API_BASE_URL = API_PORT ? `${API_DOMAIN}:${API_PORT}` : API_DOMAIN
export const API_BASE_URL = "";
export const GOOGLE_CLIENT_ID = '777830451304-nt8afkkap18gui4f9entcha99unal744.apps.googleusercontent.com'
export const GITHUB_CLIENT_ID = 'Ov23liVkO1NPAX5JyWxJ'
export const toast = useToast()

initTheme()

const app = createApp(App)
app.use(router)
app.use(Toast, { position: POSITION.TOP_RIGHT })
app.mount('#app')

checkToken().then(valid => {
  if (!valid) {
    clearToken()
  }
})
