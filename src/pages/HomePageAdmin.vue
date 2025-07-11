<template>
    <div class="home-page">
      <h2>✅ Admin Dashboard</h2>
      <p>Chào mừng bạn đến trang quản lý!</p>
    </div>
  </template>
  
  <script setup>
  import { useRouter } from 'vue-router'
  const router = useRouter()
  </script>
  
  <style scoped>
  .home-page {
    max-width: 600px;
    margin: 80px auto;
    text-align: center;
  }
  </style>  