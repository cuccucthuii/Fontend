<template>
  <div class="admin-layout">
    <div class="admin-sidebar">
      <slot name="sidebar">
        <!-- Sidebar mặc định hoặc import component sidebar admin ở đây -->
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
</script>

<style scoped>
.admin-layout {
  display: flex;
  min-height: 100vh;
  background: #18191a;
}
.admin-sidebar {
  width: 260px;
  background: #232946;
  min-height: 100vh;
  box-shadow: 2px 0 8px rgba(0,0,0,0.04);
  z-index: 10;
}
.admin-content {
  flex: 1;
  background: #f7f7f7;
  min-height: 100vh;
  padding: 0;
}
</style> 