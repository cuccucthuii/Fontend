<template>
    <div class="pos-layout">
      <aside :class="['sidebar', { collapsed: isCollapsed }]">
        <button class="toggle-btn" @click="toggleSidebar">
          {{ isCollapsed ? '☰' : '✖' }}
        </button>
        <POSSidebar v-if="!isCollapsed" />
      </aside>
      <main class="content">
        <router-view />
      </main>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue'
import POSSidebar from '../components/POSSidebar.vue'
  
  const isCollapsed = ref(false)
  
  function toggleSidebar() {
    isCollapsed.value = !isCollapsed.value
  }
  </script>
  
  <style scoped>
  .pos-layout {
    display: flex;
    width: 100vw;
    height: 100vh;
    overflow: hidden;
    background-color: #2c3e50;
    color: #ecf0f1;
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
  
  /* Toggle button at top */
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
    transition: background-color 0.2s ease;
  }
  
  .toggle-btn:hover {
    background-color: rgba(255, 255, 255, 0.1);
  }
  
  /* Main content area */
  .content {
    flex: 1;
    height: 100%;
    overflow-y: auto;
    padding: 20px;
    background-color: #34495e;
    box-sizing: border-box;
    transition: margin-left 0.3s ease;
  }
  </style>
  