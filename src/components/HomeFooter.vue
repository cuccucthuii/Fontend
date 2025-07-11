<template>
<footer class="footer dark">
  <div class="footer-content">
    <div class="footer-col brand">
      <img src="/vite.svg" alt="DEV CINEMA" class="footer-logo" />
      <div class="footer-title">DEV CINEMA</div>
      <div class="footer-slogan">Trải nghiệm điện ảnh đỉnh cao, đặt vé siêu tốc!</div>
    </div>
    <div class="footer-col">
      <div class="footer-title">Liên kết nhanh</div>
      <a href="/">Trang chủ</a>
      <a href="/showtimes">Lịch chiếu</a>
      <a href="/news">Tin tức</a>
      <a href="/promotions">Khuyến mãi</a>
      <a href="/contact">Liên hệ</a>
    </div>
    <div class="footer-col">
      <div class="footer-title">Liên hệ</div>
      <div>Hotline: <a href="tel:0123456789">0123 456 789</a></div>
      <div>Email: <a href="mailto:devcinema@gmail.com">devcinema@gmail.com</a></div>
      <div class="footer-socials">
        <a href="https://facebook.com" target="_blank" class="footer-social-icon"><img src="https://upload.wikimedia.org/wikipedia/commons/6/6c/Facebook_Logo_2023.png" alt="Facebook" style="width:20px;height:20px;" /></a>
        <a href="https://instagram.com" target="_blank" class="footer-social-icon"><img src="https://upload.wikimedia.org/wikipedia/commons/a/a5/Instagram_icon.png" alt="Instagram" style="width:20px;height:20px;" /></a>
        <a href="https://tiktok.com" target="_blank" class="footer-social-icon"><img src="https://upload.wikimedia.org/wikipedia/commons/6/6f/Tiktok-logo.png" alt="TikTok" style="width:20px;height:20px;" /></a>
      </div>
    </div>
  </div>
  <div class="footer-copy">
    © 2024 DEV CINEMA. All rights reserved. | <router-link to="/terms">Điều khoản</router-link> | <router-link to="/privacy">Chính sách bảo mật</router-link>
  </div>
</footer>
</template>

<script setup>
// Không cần logic đặc biệt
</script>

<style scoped>
.footer.dark {
  background: linear-gradient(135deg, #18191a 60%, #232526 100%);
  color: #fff;
  padding: 48px 0 18px 0;
  width: 100%;
  margin-top: auto;
  box-shadow: 0 -2px 24px rgba(44,62,80,0.18);
}
.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: flex-start;
  gap: 32px;
  padding: 0 24px;
}
.footer-col {
  flex: 1 1 220px;
  min-width: 180px;
  margin-bottom: 18px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.footer-col.brand {
  align-items: flex-start;
}
.footer-logo {
  width: 48px;
  height: 48px;
  margin-bottom: 8px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(72,219,251,0.15);
}
.footer-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 8px;
  color: #48dbfb;
  letter-spacing: 1px;
}
.footer-slogan {
  font-size: 15px;
  color: #feca57;
  margin-bottom: 8px;
}
.footer-col a {
  color: #b2bec3;
  text-decoration: none;
  font-size: 15px;
  transition: color 0.2s;
}
.footer-col a:hover {
  color: #48dbfb;
  text-decoration: underline;
}
.footer-socials {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}
.footer-social-icon {
  width: 36px;
  height: 36px;
  background: #232526;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.footer-social-icon img {
  width: 20px;
  height: 20px;
  border-radius: 50%;
}
.footer-copy {
  width: 100%;
  text-align: center;
  font-size: 14px;
  color: #b2bec3;
  margin-top: 18px;
}
.footer-copy a {
  color: #48dbfb;
  text-decoration: none;
  margin: 0 6px;
}
.footer-copy a:hover {
  text-decoration: underline;
}
@media (max-width: 1200px) { .footer-content { gap: 18px; } }
@media (max-width: 900px) { .footer-content { flex-direction: column; align-items: center; gap: 12px; } }
@media (max-width: 600px) { .footer-col { min-width: 100px; } }
</style> 