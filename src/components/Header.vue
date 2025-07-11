<template>
  <header class="header">
    <div class="header-content">
      <div class="logo">
        <span class="logo-icon">🎬</span>
        <span class="logo-text">DEV CINEMA</span>
      </div>
      <nav :class="['nav', { open: menuOpen }]">
        <router-link to="/" class="nav-link">Trang chủ</router-link>
        <router-link to="/showtimes" class="nav-link">Lịch chiếu</router-link>
        <router-link to="/news" class="nav-link">Tin tức</router-link>
        <router-link to="/prices" class="nav-link">Giá vé</router-link>
        <router-link to="/about" class="nav-link">Giới thiệu</router-link>
        <router-link to="/login" class="nav-link login-btn">Đăng nhập</router-link>
      </nav>
      <button class="menu-toggle" @click="menuOpen = !menuOpen">
        <span :class="['bar', { open: menuOpen }]"></span>
        <span :class="['bar', { open: menuOpen }]"></span>
        <span :class="['bar', { open: menuOpen }]"></span>
      </button>
    </div>
  </header>
</template>

<script setup>
import { ref } from 'vue'
const menuOpen = ref(false)
</script>

<style scoped>
.header {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  z-index: 100;
  background: rgba(24,25,26,0.98);
  box-shadow: 0 2px 16px #23252633;
  backdrop-filter: blur(8px);
}
.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 32px;
  height: 68px;
}
.logo {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 26px;
  font-weight: 900;
  color: #48dbfb;
  letter-spacing: 2px;
  text-shadow: 0 2px 12px #48dbfb33;
}
.logo-icon {
  font-size: 32px;
  filter: drop-shadow(0 2px 8px #feca57cc);
}
.logo-text {
  background: linear-gradient(90deg, #48dbfb 0%, #feca57 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.nav {
  display: flex;
  align-items: center;
  gap: 28px;
  transition: right 0.3s;
}
.nav-link {
  color: #fff;
  font-size: 17px;
  font-weight: 600;
  text-decoration: none;
  padding: 8px 0;
  position: relative;
  transition: color 0.2s;
}
.nav-link::after {
  content: '';
  display: block;
  height: 2.5px;
  width: 0;
  background: linear-gradient(90deg, #48dbfb 0%, #feca57 100%);
  border-radius: 2px;
  transition: width 0.25s;
  position: absolute;
  left: 0;
  bottom: -2px;
}
.nav-link:hover, .nav-link:focus {
  color: #48dbfb;
}
.nav-link:hover::after, .nav-link:focus::after {
  width: 100%;
}
.login-btn {
  background: linear-gradient(90deg, #48dbfb 0%, #feca57 100%);
  color: #18191a;
  border-radius: 8px;
  padding: 8px 18px;
  font-weight: 900;
  margin-left: 10px;
  box-shadow: 0 2px 8px #48dbfb33;
  transition: background 0.2s, color 0.2s;
}
.login-btn:hover {
  background: linear-gradient(90deg, #feca57 0%, #48dbfb 100%);
  color: #fff;
}
.menu-toggle {
  display: none;
  flex-direction: column;
  gap: 5px;
  background: none;
  border: none;
  cursor: pointer;
  z-index: 200;
}
.bar {
  width: 28px;
  height: 3.5px;
  background: #48dbfb;
  border-radius: 2px;
  transition: all 0.3s;
}
@media (max-width: 900px) {
  .header-content { padding: 0 10px; }
  .nav { gap: 16px; }
}
@media (max-width: 700px) {
  .nav {
    position: fixed;
    top: 68px;
    right: -100vw;
    flex-direction: column;
    background: rgba(24,25,26,0.98);
    width: 70vw;
    height: calc(100vh - 68px);
    gap: 0;
    padding: 32px 0 0 0;
    box-shadow: -2px 0 16px #23252633;
    transition: right 0.3s;
  }
  .nav.open { right: 0; }
  .nav-link { padding: 18px 32px; font-size: 19px; }
  .login-btn { margin-left: 0; }
  .menu-toggle { display: flex; }
}
</style> 