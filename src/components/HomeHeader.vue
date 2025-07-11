<template>
<header class="main-header full-header dark">
  <div class="header-container">
    <div class="header-left">
      <router-link to="/" class="logo">🎬 DEV CINEMA</router-link>
    </div>
    <nav class="header-menu">
      <router-link to="/" class="menu-link" exact-active-class="active">Trang chủ</router-link>
      <router-link to="/showtimes" class="menu-link" active-class="active">Lịch chiếu</router-link>
      <router-link to="/news" class="menu-link" active-class="active">Tin tức</router-link>
      <router-link to="/promotions" class="menu-link" active-class="active">Khuyến mãi</router-link>
      <router-link to="/prices" class="menu-link" active-class="active">Giá vé</router-link>
      <router-link to="/about" class="menu-link" active-class="active">Giới thiệu</router-link>
      <router-link to="/contact" class="menu-link" active-class="active">Liên hệ</router-link>
    </nav>
    <div class="header-right">
      <span class="hotline">Hotline: <a href="tel:0123456789">0123 456 789</a></span>
      <div class="header-socials">
        <a href="https://facebook.com" target="_blank"><img src="https://upload.wikimedia.org/wikipedia/commons/6/6c/Facebook_Logo_2023.png" class="header-social-icon" /></a>
        <a href="https://instagram.com" target="_blank"><img src="https://upload.wikimedia.org/wikipedia/commons/a/a5/Instagram_icon.png" class="header-social-icon" /></a>
        <a href="https://tiktok.com" target="_blank"><img src="https://upload.wikimedia.org/wikipedia/commons/6/6f/Tiktok-logo.png" class="header-social-icon" /></a>
      </div>
    </div>
  </div>
</header>
</template>

<script setup>
// Không cần logic đặc biệt
</script>

<style scoped>
.main-header.full-header.dark {
  width: 100%;
  background: #18191a;
  box-shadow: 0 2px 12px rgba(0,0,0,0.18);
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 72px;
  box-sizing: border-box;
}
.header-left .logo {
  font-size: 26px;
  font-weight: 900;
  color: #48dbfb;
  text-decoration: none;
  letter-spacing: 2px;
  display: flex;
  align-items: center;
}
.header-menu {
  display: flex;
  gap: 32px;
  align-items: center;
}
.menu-link {
  color: #fff;
  text-decoration: none;
  font-size: 17px;
  font-weight: 700;
  padding: 8px 0;
  transition: color 0.2s, border-bottom 0.2s;
  border-bottom: 2px solid transparent;
  letter-spacing: 0.5px;
}
.menu-link.active, .menu-link:hover {
  color: #48dbfb;
  border-bottom: 2px solid #48dbfb;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 18px;
}
.hotline {
  color: #feca57;
  font-weight: 700;
  font-size: 15px;
}
.hotline a { color: #feca57; text-decoration: none; }
.header-socials {
  display: flex;
  gap: 10px;
}
.header-social-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #fff;
  object-fit: cover;
  box-shadow: 0 2px 8px rgba(72,219,251,0.15);
  transition: filter 0.2s;
}
.header-social-icon:hover { filter: brightness(0.8); }
@media (max-width: 1100px) {
  .header-menu { gap: 16px; }
  .header-container { padding: 0 8px; }
}
@media (max-width: 900px) {
  .header-container { flex-direction: column; height: auto; gap: 8px; padding: 0 2px; }
  .header-menu { flex-wrap: wrap; gap: 8px; justify-content: center; }
  .header-left .logo { font-size: 18px; }
}
</style> 