<template>
  <button 
    @click="handleGoogleLogin" 
    :disabled="isLoading"
    class="google-login-btn"
    :class="{ loading: isLoading }"
  >
    <div class="btn-content">
      <div class="btn-icon" v-if="!isLoading">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
          <path d="M22.56 12.25c0-.78-.07-1.53-.2-2.25H12v4.26h5.92c-.26 1.37-1.04 2.53-2.21 3.31v2.77h3.57c2.08-1.92 3.28-4.74 3.28-8.09z" fill="#4285F4"/>
          <path d="M12 23c2.97 0 5.46-.98 7.28-2.66l-3.57-2.77c-.98.66-2.23 1.06-3.71 1.06-2.86 0-5.29-1.93-6.16-4.53H2.18v2.84C3.99 20.53 7.7 23 12 23z" fill="#34A853"/>
          <path d="M5.84 14.09c-.22-.66-.35-1.36-.35-2.09s.13-1.43.35-2.09V7.07H2.18C1.43 8.55 1 10.22 1 12s.43 3.45 1.18 4.93l2.85-2.22.81-.62z" fill="#FBBC05"/>
          <path d="M12 5.38c1.62 0 3.06.56 4.21 1.64l3.15-3.15C17.45 2.09 14.97 1 12 1 7.7 1 3.99 3.47 2.18 7.07l3.66 2.84c.87-2.6 3.3-4.53 6.16-4.53z" fill="#EA4335"/>
        </svg>
      </div>
      <div class="btn-spinner" v-if="isLoading">
        <div class="spinner"></div>
      </div>
      <span class="btn-text">
        {{ isLoading ? 'Đang đăng nhập...' : 'Đăng nhập với Google' }}
      </span>
    </div>
  </button>
</template>

<script setup>
import { ref } from 'vue'
import { googleAuthHelper } from '../services/socialAuthService'
import { socialAuthService } from '../services/socialAuthService'

const emit = defineEmits(['success', 'error'])

const isLoading = ref(false)

// Google Client ID - sẽ được cấu hình từ environment variables
const GOOGLE_CLIENT_ID = import.meta.env.VITE_GOOGLE_CLIENT_ID || 'your-google-client-id'

// Function to get user info from Google
async function getUserInfoFromGoogle(accessToken) {
  try {
    const response = await fetch(`https://www.googleapis.com/oauth2/v2/userinfo?access_token=${accessToken}`)
    if (!response.ok) {
      throw new Error('Failed to fetch user info from Google')
    }
    return await response.json()
  } catch (error) {
    console.error('Error fetching user info from Google:', error)
    throw error
  }
}


const handleGoogleLogin = async () => {
  if (isLoading.value) return
  
  isLoading.value = true
  
  try {
    // Load Google API if not already loaded
    await googleAuthHelper.loadGoogleAPI(GOOGLE_CLIENT_ID)
    
    // Get access token
    const accessToken = await googleAuthHelper.initGoogleAuth(GOOGLE_CLIENT_ID)
    
    // Get user info from Google using the access token
    console.log('Getting user info from Google with token:', accessToken)
    const userInfo = await getUserInfoFromGoogle(accessToken)
    console.log('Google user info received:', userInfo)
    
    // Create user object with real Google data
    const googleUser = {
      username: userInfo.name || userInfo.email.split('@')[0],
      email: userInfo.email,
      role: 'user',
      token: accessToken,
      avatar: userInfo.picture || '/logo.png?v=2',
      provider: 'google',
      fullName: userInfo.name,
      userId: userInfo.id
    }
    
    console.log('Final google user object:', googleUser)
    
    // Emit success event with real Google user data
    emit('success', googleUser)
    
  } catch (error) {
    console.error('Google login failed:', error)
    emit('error', error.message || 'Đăng nhập Google thất bại')
  } finally {
    isLoading.value = false
  }
}
</script>

<style scoped>
.google-login-btn {
  width: 100%;
  height: 48px;
  background: #ffffff;
  border: 2px solid #e0e0e0;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  font-family: 'Montserrat', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  font-weight: 600;
  font-size: 15px;
  color: #333;
  position: relative;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.google-login-btn:hover:not(:disabled) {
  border-color: #4285f4;
  box-shadow: 0 4px 16px rgba(66, 133, 244, 0.2);
  transform: translateY(-1px);
}

.google-login-btn:active:not(:disabled) {
  transform: translateY(0);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.google-login-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.btn-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.btn-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
}

.btn-spinner {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 20px;
  height: 20px;
}

.spinner {
  width: 16px;
  height: 16px;
  border: 2px solid #e0e0e0;
  border-top: 2px solid #4285f4;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.btn-text {
  font-weight: 600;
  letter-spacing: 0.3px;
}

.loading .btn-text {
  color: #666;
}

/* Responsive */
@media (max-width: 480px) {
  .google-login-btn {
    height: 44px;
    font-size: 14px;
  }
  
  .btn-content {
    gap: 10px;
  }
}
</style>
