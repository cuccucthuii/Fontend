<template>
  <div class="admin-layout">
    <div class="admin-sidebar">
      <slot name="sidebar">
        <SidebarMenu />
      </slot>
    </div>
    <div class="admin-content">
      <router-view />
    </div>
  </div>
</template>

<script setup>
import SidebarMenu from '../components/SidebarMenu.vue'
import { onMounted } from 'vue'

onMounted(() => {
  console.log('✅ AdminLayout đã được mount!')
})
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: linear-gradient(135deg, #BFA2DB 0%, #B7E4C7 100%);
}
.admin-sidebar {
  width: 260px;
  background: linear-gradient(135deg, #BFA2DB 0%, #B7E4C7 100%);
  min-height: 100vh;
  box-shadow: 2px 0 16px rgba(191,162,219,0.08);
  z-index: 10;
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 36px;
}
.admin-logo {
  width: 120px;
  margin-bottom: 36px;
  border-radius: 18px;
  box-shadow: 0 2px 12px rgba(191,162,219,0.10);
}
.admin-content {
  flex: 1;
  background: #fff;
  min-height: 100vh;
  border-radius: 32px 0 0 32px;
  margin: 24px 0 24px 0;
  padding: 0 0 0 0;
  box-shadow: 0 4px 32px rgba(183,228,199,0.08);
  overflow: hidden;
  position: relative;
}
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