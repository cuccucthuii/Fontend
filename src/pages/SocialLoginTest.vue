<template>
  <div class="social-test-page">
    <div class="test-container">
      <div class="test-header">
        <h1>🔐 Social Login Test</h1>
        <p>Kiểm tra tính năng đăng nhập bằng Google và Facebook</p>
      </div>
      
      <div class="test-content">
        <!-- Configuration Status -->
        <div class="config-status">
          <h3>⚙️ Configuration Status</h3>
          <div class="status-grid">
            <div class="status-item" :class="{ configured: isGoogleConfigured }">
              <div class="status-icon">🔍</div>
              <div class="status-info">
                <h4>Google OAuth</h4>
                <p>{{ isGoogleConfigured ? '✅ Configured' : '❌ Not configured' }}</p>
                <small v-if="!isGoogleConfigured">VITE_GOOGLE_CLIENT_ID not set</small>
              </div>
            </div>
            <div class="status-item" :class="{ configured: isFacebookConfigured }">
              <div class="status-icon">🔍</div>
              <div class="status-info">
                <h4>Facebook OAuth</h4>
                <p>{{ isFacebookConfigured ? '✅ Configured' : '❌ Not configured' }}</p>
                <small v-if="!isFacebookConfigured">VITE_FACEBOOK_APP_ID not set</small>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Test Buttons -->
        <div class="test-buttons">
          <h3>🧪 Test Social Login</h3>
          <div class="button-grid">
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
        
        <!-- Test Results -->
        <div class="test-results" v-if="testResult">
          <h3>📊 Test Results</h3>
          <div class="result-card" :class="testResult.type">
            <div class="result-header">
              <span class="result-icon">{{ testResult.type === 'success' ? '✅' : '❌' }}</span>
              <h4>{{ testResult.title }}</h4>
            </div>
            <div class="result-content">
              <p>{{ testResult.message }}</p>
              <div v-if="testResult.data" class="result-data">
                <h5>Response Data:</h5>
                <pre>{{ JSON.stringify(testResult.data, null, 2) }}</pre>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Instructions -->
        <div class="instructions">
          <h3>📋 Setup Instructions</h3>
          <div class="instruction-list">
            <div class="instruction">
              <span class="step">1</span>
              <div class="step-content">
                <h4>Create .env file</h4>
                <p>Copy <code>env.example</code> to <code>.env</code> and add your credentials</p>
              </div>
            </div>
            <div class="instruction">
              <span class="step">2</span>
              <div class="step-content">
                <h4>Google Setup</h4>
                <p>Get Client ID from <a href="https://console.cloud.google.com/" target="_blank">Google Cloud Console</a></p>
              </div>
            </div>
            <div class="instruction">
              <span class="step">3</span>
              <div class="step-content">
                <h4>Facebook Setup</h4>
                <p>Get App ID from <a href="https://developers.facebook.com/" target="_blank">Facebook Developers</a></p>
              </div>
            </div>
            <div class="instruction">
              <span class="step">4</span>
              <div class="step-content">
                <h4>Backend API</h4>
                <p>Ensure backend implements social login endpoints</p>
              </div>
            </div>
          </div>
        </div>
        
        <!-- Quick Links -->
        <div class="quick-links">
          <h3>🔗 Quick Links</h3>
          <div class="links-grid">
            <a href="/login" class="link-card">
              <span class="link-icon">🔑</span>
              <span class="link-text">Go to Login Page</span>
            </a>
            <a href="/register" class="link-card">
              <span class="link-icon">📝</span>
              <span class="link-text">Go to Register Page</span>
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import GoogleLoginButton from '../components/GoogleLoginButton.vue'
import FacebookLoginButton from '../components/FacebookLoginButton.vue'

const testResult = ref(null)

// Check configuration
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
    title: 'Social Login Successful! 🎉',
    message: 'Đăng nhập thành công! Dữ liệu trả về từ server:',
    data: result
  }
}

const handleTestError = (error) => {
  testResult.value = {
    type: 'error',
    title: 'Social Login Failed ❌',
    message: error,
    data: null
  }
}
</script>

<style scoped>
.social-test-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;
  font-family: 'Montserrat', 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.test-container {
  max-width: 1000px;
  margin: 0 auto;
  background: white;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.test-header {
  background: linear-gradient(135deg, #BFA2DB 0%, #8B5FBF 100%);
  color: white;
  padding: 40px;
  text-align: center;
}

.test-header h1 {
  margin: 0 0 10px 0;
  font-size: 32px;
  font-weight: 700;
}

.test-header p {
  margin: 0;
  font-size: 18px;
  opacity: 0.9;
}

.test-content {
  padding: 40px;
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.config-status h3,
.test-buttons h3,
.test-results h3,
.instructions h3,
.quick-links h3 {
  color: #8B5FBF;
  margin-bottom: 20px;
  font-size: 24px;
  font-weight: 600;
}

.status-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 15px;
  padding: 20px;
  border-radius: 12px;
  background: #f8f9fa;
  border: 2px solid #e0e0e0;
  transition: all 0.3s ease;
}

.status-item.configured {
  background: #e8f5e8;
  border-color: #4caf50;
}

.status-icon {
  font-size: 24px;
}

.status-info h4 {
  margin: 0 0 5px 0;
  color: #333;
  font-size: 18px;
  font-weight: 600;
}

.status-info p {
  margin: 0 0 5px 0;
  font-weight: 500;
}

.status-info small {
  color: #666;
  font-size: 12px;
}

.button-grid {
  display: flex;
  flex-direction: column;
  gap: 15px;
  max-width: 400px;
}

.result-card {
  border-radius: 12px;
  padding: 20px;
  border: 2px solid;
}

.result-card.success {
  background: #e8f5e8;
  border-color: #4caf50;
  color: #2e7d32;
}

.result-card.error {
  background: #ffebee;
  border-color: #f44336;
  color: #c62828;
}

.result-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}

.result-icon {
  font-size: 24px;
}

.result-header h4 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
}

.result-content p {
  margin: 0 0 15px 0;
  line-height: 1.5;
}

.result-data {
  background: rgba(0, 0, 0, 0.05);
  padding: 15px;
  border-radius: 8px;
  margin-top: 15px;
}

.result-data h5 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 600;
}

.result-data pre {
  margin: 0;
  font-size: 12px;
  overflow-x: auto;
  white-space: pre-wrap;
  word-break: break-word;
}

.instruction-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.instruction {
  display: flex;
  gap: 20px;
  align-items: flex-start;
}

.step {
  background: linear-gradient(135deg, #BFA2DB 0%, #8B5FBF 100%);
  color: white;
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 700;
  font-size: 18px;
  flex-shrink: 0;
}

.step-content h4 {
  margin: 0 0 8px 0;
  color: #8B5FBF;
  font-size: 18px;
  font-weight: 600;
}

.step-content p {
  margin: 0;
  color: #666;
  line-height: 1.5;
}

.step-content code {
  background: #f0f0f0;
  padding: 4px 8px;
  border-radius: 4px;
  font-family: 'Courier New', monospace;
  font-size: 14px;
}

.step-content a {
  color: #8B5FBF;
  text-decoration: none;
  font-weight: 600;
}

.step-content a:hover {
  text-decoration: underline;
}

.links-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 15px;
}

.link-card {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 20px;
  background: linear-gradient(135deg, #BFA2DB 0%, #8B5FBF 100%);
  color: white;
  text-decoration: none;
  border-radius: 12px;
  transition: all 0.3s ease;
  font-weight: 600;
}

.link-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(191, 162, 219, 0.3);
}

.link-icon {
  font-size: 24px;
}

.link-text {
  font-size: 16px;
}

@media (max-width: 768px) {
  .test-content {
    padding: 20px;
  }
  
  .test-header {
    padding: 30px 20px;
  }
  
  .test-header h1 {
    font-size: 24px;
  }
  
  .status-grid {
    grid-template-columns: 1fr;
  }
  
  .instruction {
    flex-direction: column;
    gap: 15px;
  }
  
  .links-grid {
    grid-template-columns: 1fr;
  }
}
</style>
