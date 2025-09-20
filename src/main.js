import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import axios from 'axios'
import { API_BASE_URL } from './services/api'
import './style.css'
import './assets/global-fonts.css'

// ✅ Set baseURL from central config
axios.defaults.baseURL = API_BASE_URL

// ✅ Gán sẵn token nếu có
const token = localStorage.getItem('token')
if (token) {
    axios.defaults.headers.common['Authorization'] = `Bearer ${token}`
}

const app = createApp(App)
app.use(router)
// Debug: list registered routes once at startup
try {
    const registeredPaths = router.getRoutes().map(r => r.path)
    console.log('[Router] Registered paths:', registeredPaths)
} catch (e) {
    console.warn('[Router] Could not list routes:', e)
}
app.mount('#app')