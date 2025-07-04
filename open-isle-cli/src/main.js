import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/global.css'
import Toast, { POSITION } from 'vue-toastification'
import 'vue-toastification/dist/index.css'
import { useToast } from 'vue-toastification'

// Configurable API domain and port
export const API_DOMAIN = 'http://127.0.0.1'
export const API_PORT = 8081
export const API_BASE_URL = API_PORT ? `${API_DOMAIN}:${API_PORT}` : API_DOMAIN
export const toast = useToast()

const app = createApp(App)
app.use(router)
app.use(Toast, { position: POSITION.TOP_RIGHT })
app.mount('#app')
