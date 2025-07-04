import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './assets/global.css'
import Toast, { POSITION } from 'vue-toastification'
import 'vue-toastification/dist/index.css'

// Configurable API domain and port
export const API_DOMAIN =
  process.env.VUE_APP_API_DOMAIN ||
  `${window.location.protocol}//${window.location.hostname}`
export const API_PORT = process.env.VUE_APP_API_PORT || window.location.port
export const API_BASE_URL = API_PORT ? `${API_DOMAIN}:${API_PORT}` : API_DOMAIN

const app = createApp(App)
app.use(router)
app.use(Toast, { position: POSITION.TOP_RIGHT })
app.mount('#app')
