<template>
  <nav class="pos-step-sidebar">
    <ul class="step-list">
      <li :class="['step-item', { active: currentStep === 1, done: currentStep > 1 }]">
        <span class="step-icon">
          <span v-if="currentStep > 1">✔️</span>
          <span v-else>🗓️</span>
        </span>
        <span class="step-label">Chọn suất chiếu</span>
        </li>
      <li :class="['step-item', { active: currentStep === 2, done: currentStep > 2 }]">
        <span class="step-icon">
          <span v-if="currentStep > 2">✔️</span>
          <span v-else>💺</span>
        </span>
        <span class="step-label">Chọn ghế</span>
        </li>
      <li :class="['step-item', { active: currentStep === 3 }]">
        <span class="step-icon">💳</span>
        <span class="step-label">Thanh toán</span>
        </li>
      </ul>
      <div class="logout-section">
        <hr />
        <button class="signout" @click="signOut">🚪 Đăng xuất</button>
      </div>
    </nav>
  </template>
  
  <script setup>
import { useRouter, useRoute } from 'vue-router'
import { computed } from 'vue'
  const router = useRouter()
const route = useRoute()

// Xác định bước hiện tại dựa vào route
const stepMap = {
  '/pos': 1,
  '/pos/showtimes': 1,
  '/pos/seats': 2,
  '/pos/payment': 3
}
const currentStep = computed(() => {
  if (route.path.startsWith('/pos/seats')) return 2
  if (route.path.startsWith('/pos/payment')) return 3
  return 1
})
  
  function signOut() {
    localStorage.clear()
    router.push('/login')
  }
  </script>
  
  <style scoped>
.pos-step-sidebar {
    display: flex;
    flex-direction: column;
    height: 100%;
  padding: 32px 18px 18px 18px;
  background: linear-gradient(135deg, #4f8cff 0%, #6a82fb 100%);
  color: #fff;
  min-width: 220px;
    box-sizing: border-box;
  box-shadow: 4px 0 20px rgba(0,0,0,0.08);
  }
.step-list {
    list-style: none;
    padding: 0;
  margin: 0 0 40px 0;
  flex: 1;
    display: flex;
    flex-direction: column;
  gap: 32px;
}
.step-item {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 18px;
  opacity: 0.7;
  transition: all 0.2s;
  position: relative;
  }
.step-item.active {
  font-weight: bold;
  font-size: 20px;
  opacity: 1;
  }
.step-item.done .step-icon {
  color: #00e676;
}
.step-icon {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 22px;
  background: #fff;
  color: #4f8cff;
  border-radius: 50%;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  margin-right: 8px;
  border: 2px solid #fff;
  transition: background 0.2s, color 0.2s;
  }
.step-item.active .step-icon {
  background: #4f8cff;
  color: #fff;
  border: 2px solid #fff;
}
.step-label {
  flex: 1;
  white-space: nowrap;
}
  .logout-section {
    margin-top: auto;
  border-top: 1px solid rgba(255,255,255,0.2);
  padding-top: 18px;
  }
  .signout {
  background: rgba(255,255,255,0.08);
    border: none;
    border-radius: 10px;
  color: #fff;
  padding: 12px 18px;
    width: 100%;
    text-align: left;
    cursor: pointer;
    font-size: 17px;
  transition: background 0.2s;
  }
  .signout:hover {
  background: rgba(255,255,255,0.18);
    text-decoration: underline;
  }
  </style>
  