<template>
  <div class="layout">
    <Header />
    <aside :class="['sidebar', { collapsed: isCollapsed }]">
      <button class="toggle-btn" @click="toggleSidebar">
        {{ isCollapsed ? '☰' : '✖' }}
      </button>
      <SidebarMenu v-if="!isCollapsed" />
    </aside>
    <main class="content">
      <router-view />
    </main>
    <HomeFooter />
  </div>
</template>

<script setup>
import { ref } from 'vue'
import SidebarMenu from '../components/layout/SidebarMenu.vue'
import HomeFooter from '../components/layout/HomeFooter.vue'
import Header from '../components/layout/Header.vue'

const isCollapsed = ref(false)

function toggleSidebar() {
  isCollapsed.value = !isCollapsed.value
}
</script>

<style scoped>
.layout {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

/* Sidebar base style */
.sidebar {
  width: 260px;
  min-width: 260px;
  height: 100%;
  background-color: #2c3e50;
  color: #ecf0f1;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  overflow: hidden;
  transition: width 0.3s ease;
  position: relative;
}

/* Collapsed sidebar style */
.sidebar.collapsed {
  width: 60px;
  min-width: 60px;
  padding: 0;
}

/* Toggle button */
.toggle-btn {
  background: none;
  border: none;
  color: #ecf0f1;
  font-size: 24px;
  padding: 12px 20px;
  cursor: pointer;
  text-align: left;
  width: 100%;
  border-bottom: 1px solid #7f8c8d;
}

/* Content area */
.content {
  flex: 1;
  height: 100%;
  padding: 20px;
  background-color: #f8f9fa;
  box-sizing: border-box;
  overflow-y: auto;
  transition: margin-left 0.3s ease;
}
::v-deep .content {
  background: transparent !important;
}
</style>
