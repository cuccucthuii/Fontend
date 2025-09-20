<template>
  <div class="admin-layout">
    <div class="admin-sidebar">
      <slot name="sidebar">
        <SidebarMenu />
      </slot>
    </div>
    <div class="admin-content">
      <div class="user-badge" v-if="currentUserName || currentUserRole">
        <div class="user-line">
          <span class="user-name">{{ currentUserName || 'Người dùng' }}</span>
          <span class="dot">•</span>
          <span class="user-role">{{ currentUserRole || 'user' }}</span>
        </div>
      </div>
      <router-view />
    </div>
  </div>
</template>

<script setup>
import SidebarMenu from '../components/SidebarMenu.vue'
import { onMounted, ref } from 'vue'

onMounted(() => {
  console.log('✅ AdminLayout đã được mount!')
})

const currentUserName = ref('')
const currentUserRole = ref('')
try {
  const raw = localStorage.getItem('userInfo')
  if (raw) {
    const info = JSON.parse(raw)
    currentUserName.value = info?.username || info?.name || info?.fullName || info?.email || ''
    currentUserRole.value = info?.role || ''
  }
} catch (e) { }
</script>

<style scoped>
.admin-layout {
  display: block;
  min-height: 100vh;
  background: #f5f7fa;
}
.admin-sidebar {
  width: 260px;
  background: #1f2937;
  min-height: 100vh;
  box-shadow: 2px 0 16px rgba(0,0,0,0.08);
  z-index: 100;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 36px;
  position: fixed;
  top: 0;
  left: 0;
  bottom: 0;
}
.admin-logo {
  width: 120px;
  margin-bottom: 36px;
  border-radius: 18px;
  box-shadow: 0 2px 12px rgba(191,162,219,0.10);
}
.admin-content {
  margin-left: 260px;
  background: #fff;
  min-height: 100vh;
  border-radius: 0;
  padding: 0;
  box-shadow: none;
  position: relative;
}

.user-badge {
  position: absolute;
  top: 12px;
  right: 16px;
  background: rgba(17, 24, 39, 0.8);
  color: #fff;
  padding: 8px 12px;
  border-radius: 999px;
  font-size: 13px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  z-index: 20;
}
.user-line { display: flex; align-items: center; gap: 8px; }
.user-name { color: #e5e7eb; }
.user-role { color: #93c5fd; text-transform: lowercase; }
.dot { opacity: 0.6; }
@media (max-width: 900px) {
  .admin-sidebar {
    width: 70px;
    padding-top: 18px;
  }
  .admin-logo {
    width: 48px;
    margin-bottom: 18px;
  }
  .admin-content {
    border-radius: 0;
    margin: 0;
  }
}
</style> 