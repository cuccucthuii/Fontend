<template>
  <footer class="footer">
    <div class="footer-content">
      <div class="footer-brand">
        <span class="footer-logo">🎬</span>
        <span class="footer-name">DEV CINEMA</span>
        <span class="footer-slogan">Trải nghiệm điện ảnh đỉnh cao</span>
      </div>
      <div class="footer-info">
        <div class="footer-contact">
          <span class="contact-item"><span class="icon">📍</span> 123 Đường Phim, TP. Điện Ảnh</span>
          <span class="contact-item"><span class="icon">☎️</span> 0123 456 789</span>
          <span class="contact-item"><span class="icon">✉️</span> support@devcinema.vn</span>
        </div>
        <div class="footer-social">
          <a href="#" class="social-link" title="Facebook"><span class="icon">🌐</span></a>
          <a href="#" class="social-link" title="YouTube"><span class="icon">▶️</span></a>
          <a href="#" class="social-link" title="Instagram"><span class="icon">📸</span></a>
        </div>
      </div>
    </div>
    <div class="footer-copyright">
      © {{ new Date().getFullYear() }} DEV CINEMA. All rights reserved.
    </div>
  </footer>
</template>

<script setup></script>

<style scoped>
.footer {
  background: linear-gradient(90deg, #232526 0%, #1c1c1c 100%);
  color: #fff;
  padding: 36px 0 12px 0;
  font-size: 16px;
  box-shadow: 0 -2px 16px #23252633;
}
.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-wrap: wrap;
  align-items: flex-start;
  justify-content: space-between;
  gap: 24px;
  padding: 0 32px;
}
.footer-brand {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 8px;
}
.footer-logo {
  font-size: 32px;
  filter: drop-shadow(0 2px 8px #feca57cc);
}
.footer-name {
  font-size: 22px;
  font-weight: 900;
  background: linear-gradient(90deg, #48dbfb 0%, #feca57 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.footer-slogan {
  color: #feca57;
  font-size: 15px;
  font-weight: 600;
  margin-top: 2px;
}
.footer-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
  align-items: flex-end;
}
.footer-contact {
  display: flex;
  flex-direction: column;
  gap: 4px;
  color: #b2bec3;
  font-size: 15px;
}
.contact-item .icon {
  margin-right: 6px;
  color: #48dbfb;
  font-size: 17px;
}
.footer-social {
  display: flex;
  gap: 16px;
  margin-top: 8px;
}
.social-link {
  color: #fff;
  font-size: 22px;
  transition: color 0.2s, transform 0.2s;
  text-decoration: none;
}
.social-link:hover {
  color: #48dbfb;
  transform: scale(1.18) rotate(-8deg);
}
.footer-copyright {
  text-align: center;
  color: #b2bec3;
  font-size: 14px;
  margin-top: 24px;
  letter-spacing: 1px;
}
@media (max-width: 700px) {
  .footer-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 18px;
    padding: 0 10px;
  }
  .footer-info { align-items: flex-start; }
}
</style> 