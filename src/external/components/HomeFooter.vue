<template>
<footer class="main-footer">
  <div class="footer-content">
    <div class="footer-col brand">
      <img src="/logo.png?v=2" alt="DEV CINEMA" class="footer-logo-img" />
      <div class="footer-slogan"> Code cảm xúc, render trải nghiệm!</div>
    </div>
    <div class="footer-col quick-links">
      <div class="footer-title">Liên kết nhanh</div>
      <a href="/">Trang chủ</a>
      <a href="/showtimes">Lịch chiếu</a>
      <a href="/news">Tin tức</a>
      <a href="/promotions">Khuyến mãi</a>
      <a href="/contact">Liên hệ</a>
    </div>
    <div class="footer-col contact">
      <div class="footer-title">Liên hệ</div>
      <div class="footer-contact">Hotline: <span>0123 456 789</span></div>
      <div class="footer-contact">Email: <span>devcinema@gmail.com</span></div>
      <div class="footer-socials">
        <a href="https://facebook.com" target="_blank" class="footer-social-icon"><img src="https://upload.wikimedia.org/wikipedia/commons/6/6c/Facebook_Logo_2023.png" alt="Facebook" /></a>
        <a href="https://instagram.com" target="_blank" class="footer-social-icon"><img src="https://upload.wikimedia.org/wikipedia/commons/a/a5/Instagram_icon.png" alt="Instagram" /></a>
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
.main-footer {
  width: 100vw;
  max-width: 100vw;
  background: linear-gradient(135deg, #18191a 60%, #232526 100%);
  color: #fff;
  padding: 0 0 0 0;
  margin: 0;
  box-shadow: 0 -2px 24px rgba(44,62,80,0.18);
}
.footer-content {
  width: 100vw;
  max-width: 100vw;
  margin: 0;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: flex-start;
  gap: 0;
  padding: 32px 0 0 0;
}
.footer-col {
  flex: 1 1 0;
  min-width: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 0 0;
}
.footer-col.brand {
  align-items: center;
  justify-content: flex-start;
}
.footer-logo-img {
  height: 56px;
  width: auto;
  display: inline-block;
  vertical-align: middle;
  margin-bottom: 6px;
}
.footer-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 6px;
  color: #48dbfb;
  letter-spacing: 1px;
  text-align: center;
}
.footer-slogan {
  font-size: 17px;
  color: #feca57;
  margin-top: 18px;
  margin-bottom: 8px;
  text-align: center;
}
.footer-col.quick-links {
  align-items: flex-start;
  justify-content: flex-start;
  padding-left: 40px;
}
.footer-col.quick-links .footer-title {
  text-align: left;
}
.footer-col.quick-links a {
  color: #fff;
  text-decoration: none;
  font-size: 16px;
  margin-bottom: 4px;
  transition: color 0.2s;
}
.footer-col.quick-links a:hover {
  color: #48dbfb;
  text-decoration: underline;
}
.footer-col.contact {
  align-items: flex-start;
  justify-content: flex-start;
  padding-left: 40px;
}
.footer-col.contact .footer-title {
  text-align: left;
}
.footer-contact {
  color: #fff;
  font-size: 16px;
  margin-bottom: 4px;
}
.footer-contact span {
  color: #b2bec3;
}
.footer-socials {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}
.footer-social-icon {
  width: 38px;
  height: 38px;
  background: #232526;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 8px #48dbfb55;
  transition: box-shadow 0.2s;
}
.footer-social-icon img {
  width: 22px;
  height: 22px;
  border-radius: 50%;
}
.footer-social-icon:hover {
  box-shadow: 0 0 16px #48dbfb;
}
.footer-copy {
  width: 100vw;
  text-align: center;
  font-size: 15px;
  color: #b2bec3;
  margin-top: 18px;
  padding-bottom: 12px;
}
.footer-copy a {
  color: #48dbfb;
  text-decoration: none;
  margin: 0 6px;
}
.footer-copy a:hover {
  text-decoration: underline;
}
@media (max-width: 1200px) {
  .footer-content { gap: 0; }
}
@media (max-width: 900px) {
  .footer-content {
    flex-direction: column;
    align-items: center;
    gap: 12px;
    padding: 24px 0 0 0;
  }
  .footer-col.quick-links, .footer-col.contact {
    align-items: center;
    padding-left: 0;
  }
  .footer-col.quick-links .footer-title, .footer-col.contact .footer-title {
    text-align: center;
  }
}
@media (max-width: 600px) {
  .footer-logo { width: 44px; height: 44px; }
  .footer-title { font-size: 17px; }
  .footer-slogan { font-size: 13px; }
  .footer-contact, .footer-col.quick-links a { font-size: 13px; }
  .footer-social-icon { width: 28px; height: 28px; }
  .footer-social-icon img { width: 16px; height: 16px; }
}
</style> 