<template>
  <div class="social-login-demo">
    <div class="demo-header">
      <h2>🔐 Social Login Demo</h2>
      <p>Test tính năng đăng nhập bằng Google và Facebook</p>
    </div>
    
    <div class="demo-content">
      <div class="config-section">
        <h3>⚙️ Configuration Status</h3>
        <div class="config-items">
          <div class="config-item" :class="{ configured: isGoogleConfigured }">
            <span class="config-icon">🔍</span>
            <span class="config-text">Google Client ID</span>
            <span class="config-status">{{ isGoogleConfigured ? '✅ Configured' : '❌ Not configured' }}</span>
          </div>
          <div class="config-item" :class="{ configured: isFacebookConfigured }">
            <span class="config-icon">🔍</span>
            <span class="config-text">Facebook App ID</span>
            <span class="config-status">{{ isFacebookConfigured ? '✅ Configured' : '❌ Not configured' }}</span>
          </div>
        </div>
      </div>
      
      <div class="test-section">
        <h3>🧪 Test Social Login</h3>
        <div class="test-buttons">
          <GoogleLoginButton 
            @success="handleTestSuccess" 
            @error="handleTestError" 
          />
          <FacebookLoginButton 
            @success="handleTestSuccess" 
            @error="handleTestError" 
          />
        </div>
      </div>
      
      <div class="result-section" v-if="testResult">
        <h3>📊 Test Result</h3>
        <div class="result-content" :class="testResult.type">
          <div class="result-icon">
            {{ testResult.type === 'success' ? '✅' : '❌' }}
          </div>
          <div class="result-text">
            <strong>{{ testResult.title }}</strong>
            <p>{{ testResult.message }}</p>
            <pre v-if="testResult.data" class="result-data">{{ JSON.stringify(testResult.data, null, 2) }}</pre>
          </div>
        </div>
      </div>
      
      <div class="instructions-section">
        <h3>📋 Setup Instructions</h3>
        <div class="instructions">
          <div class="instruction-item">
            <span class="step-number">1</span>
            <div class="step-content">
              <h4>Create .env file</h4>
              <p>Copy <code>env.example</code> to <code>.env</code> and configure your credentials</p>
            </div>
          </div>
          <div class="instruction-item">
            <span class="step-number">2</span>
            <div class="step-content">
              <h4>Google OAuth Setup</h4>
              <p>Create Google Cloud project and get Client ID</p>
            </div>
          </div>
          <div class="instruction-item">
            <span class="step-number">3</span>
            <div class="step-content">
              <h4>Facebook OAuth Setup</h4>
              <p>Create Facebook App and get App ID</p>
            </div>
          </div>
          <div class="instruction-item">
            <span class="step-number">4</span>
            <div class="step-content">
              <h4>Backend API</h4>
              <p>Ensure backend implements social login endpoints</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import GoogleLoginButton from './GoogleLoginButton.vue'
import FacebookLoginButton from './FacebookLoginButton.vue'

const testResult = ref(null)

// Check if credentials are configured
const isGoogleConfigured = computed(() => {
  const clientId = import.meta.env.VITE_GOOGLE_CLIENT_ID
  return clientId && clientId !== 'your-google-client-id-here' && clientId.length > 10
})

const isFacebookConfigured = computed(() => {
  const appId = import.meta.env.VITE_FACEBOOK_APP_ID
  return appId && appId !== 'your-facebook-app-id-here' && appId.length > 5
})

const handleTestSuccess = (result) => {
  testResult.value = {
    type: 'success',
    title: 'Social Login Successful!',
    message: 'Đăng nhập thành công. Dữ liệu trả về từ server:',
    data: result
  }
}

const handleTestError = (error) => {
  testResult.value = {
    type: 'error',
    title: 'Social Login Failed',
    message: error,
    data: null
  }
}
</script>

<style scoped>
.social-login-demo {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
  font-family: 'Montserrat', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.demo-header {
  text-align: center;
  margin-bottom: 32px;
}

.demo-header h2 {
  color: #8B5FBF;
  margin-bottom: 8px;
  font-size: 28px;
  font-weight: 700;
}

.demo-header p {
  color: #666;
  font-size: 16px;
}

.demo-content {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.config-section,
.test-section,
.result-section,
.instructions-section {
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  border: 1px solid #e0e0e0;
}

.config-section h3,
.test-section h3,
.result-section h3,
.instructions-section h3 {
  color: #8B5FBF;
  margin-bottom: 16px;
  font-size: 20px;
  font-weight: 600;
}

.config-items {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.config-item {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 12px;
  border-radius: 8px;
  background: #f8f9fa;
  border: 1px solid #e0e0e0;
  transition: all 0.3s ease;
}

.config-item.configured {
  background: #e8f5e8;
  border-color: #4caf50;
}

.config-icon {
  font-size: 18px;
}

.config-text {
  flex: 1;
  font-weight: 500;
  color: #333;
}

.config-status {
  font-weight: 600;
  font-size: 14px;
}

.test-buttons {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.result-content {
  display: flex;
  gap: 16px;
  padding: 16px;
  border-radius: 8px;
  border: 1px solid;
}

.result-content.success {
  background: #e8f5e8;
  border-color: #4caf50;
  color: #2e7d32;
}

.result-content.error {
  background: #ffebee;
  border-color: #f44336;
  color: #c62828;
}

.result-icon {
  font-size: 24px;
  flex-shrink: 0;
}

.result-text {
  flex: 1;
}

.result-text strong {
  display: block;
  margin-bottom: 8px;
  font-size: 16px;
}

.result-text p {
  margin-bottom: 12px;
  line-height: 1.5;
}

.result-data {
  background: rgba(0, 0, 0, 0.05);
  padding: 12px;
  border-radius: 4px;
  font-size: 12px;
  overflow-x: auto;
  white-space: pre-wrap;
  word-break: break-word;
}

.instructions {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.instruction-item {
  display: flex;
  gap: 16px;
  align-items: flex-start;
}

.step-number {
  background: linear-gradient(135deg, #BFA2DB 0%, #8B5FBF 100%);
  color: white;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 14px;
  flex-shrink: 0;
}

.step-content h4 {
  color: #8B5FBF;
  margin-bottom: 4px;
  font-size: 16px;
  font-weight: 600;
}

.step-content p {
  color: #666;
  line-height: 1.5;
  margin: 0;
}

.step-content code {
  background: #f0f0f0;
  padding: 2px 6px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 14px;
}

@media (max-width: 768px) {
  .social-login-demo {
    padding: 16px;
  }
  
  .config-section,
  .test-section,
  .result-section,
  .instructions-section {
    padding: 16px;
  }
  
  .result-content {
    flex-direction: column;
    gap: 12px;
  }
}
</style>
