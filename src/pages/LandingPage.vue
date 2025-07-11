<template>
  <div class="landing-page">
    <!-- Header -->
    <header class="landing-header">
      <div class="header-container">
        <div class="logo">
          <h1>🎬 DEV CINEMA</h1>
        </div>
        <nav class="nav-menu">
          <a href="#about" class="nav-link">Giới thiệu</a>
          <a href="#movies" class="nav-link">Phim</a>
          <a href="#contact" class="nav-link">Liên hệ</a>
        </nav>
        <div class="auth-buttons">
          <router-link to="/login" class="btn-login">Đăng nhập</router-link>
          <router-link to="/login" class="btn-register">Đăng ký</router-link>
        </div>
      </div>
    </header>

    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">Trải nghiệm điện ảnh đỉnh cao</h1>
        <p class="hero-subtitle">Đặt vé siêu tốc, xem phim chất lượng cao với DEV CINEMA</p>
        <div class="hero-buttons">
          <router-link to="/login" class="btn-primary">Bắt đầu ngay</router-link>
          <a href="#about" class="btn-secondary">Tìm hiểu thêm</a>
        </div>
      </div>
      <div class="hero-image">
        <img src="https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=2070&q=80" alt="Cinema" />
      </div>
    </section>

    <!-- Features Section -->
    <section class="features-section">
      <div class="container">
        <h2 class="section-title">Tại sao chọn DEV CINEMA?</h2>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">🎟️</div>
            <h3>Đặt vé dễ dàng</h3>
            <p>Chỉ vài cú click, bạn đã có vé xem phim yêu thích</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🎬</div>
            <h3>Phim chất lượng cao</h3>
            <p>Hệ thống chiếu phim 4K, âm thanh Dolby Atmos</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">💰</div>
            <h3>Giá cả hợp lý</h3>
            <p>Nhiều ưu đãi, khuyến mãi hấp dẫn</p>
          </div>
          <div class="feature-card">
            <div class="feature-icon">📍</div>
            <h3>Nhiều chi nhánh</h3>
            <p>Hệ thống rạp phủ khắp các quận huyện</p>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta-section">
      <div class="container">
        <h2>Sẵn sàng trải nghiệm?</h2>
        <p>Đăng ký ngay để được hưởng nhiều ưu đãi đặc biệt</p>
        <router-link to="/login" class="btn-cta">Đăng ký miễn phí</router-link>
      </div>
    </section>

    <!-- Footer -->
    <footer class="landing-footer">
      <div class="container">
        <div class="footer-content">
          <div class="footer-col">
            <h3>🎬 DEV CINEMA</h3>
            <p>Trải nghiệm điện ảnh đỉnh cao, đặt vé siêu tốc!</p>
          </div>
          <div class="footer-col">
            <h4>Liên kết</h4>
            <a href="#about">Giới thiệu</a>
            <a href="#movies">Phim</a>
            <a href="#contact">Liên hệ</a>
          </div>
          <div class="footer-col">
            <h4>Liên hệ</h4>
            <p>Hotline: 0123 456 789</p>
            <p>Email: devcinema@gmail.com</p>
          </div>
        </div>
        <div class="footer-bottom">
          <p>&copy; 2024 DEV CINEMA. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script setup>
// No additional logic needed for landing page
</script>

<style scoped>
.landing-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

/* Header */
.landing-header {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  z-index: 1000;
  padding: 1rem 0;
}

.header-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.logo h1 {
  margin: 0;
  font-size: 1.8rem;
  font-weight: 700;
}

.nav-menu {
  display: flex;
  gap: 2rem;
}

.nav-link {
  color: white;
  text-decoration: none;
  font-weight: 500;
  transition: opacity 0.3s;
}

.nav-link:hover {
  opacity: 0.8;
}

.auth-buttons {
  display: flex;
  gap: 1rem;
}

.btn-login, .btn-register {
  padding: 0.5rem 1.5rem;
  border-radius: 8px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-login {
  color: white;
  border: 2px solid white;
}

.btn-login:hover {
  background: white;
  color: #667eea;
}

.btn-register {
  background: white;
  color: #667eea;
}

.btn-register:hover {
  background: rgba(255, 255, 255, 0.9);
  transform: translateY(-2px);
}

/* Hero Section */
.hero-section {
  padding: 8rem 2rem 4rem;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  max-width: 1200px;
  margin: 0 auto;
  align-items: center;
  min-height: 100vh;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.hero-buttons {
  display: flex;
  gap: 1rem;
}

.btn-primary, .btn-secondary {
  padding: 1rem 2rem;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s;
}

.btn-primary {
  background: linear-gradient(135deg, #ff6b6b, #ee5a52);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 107, 107, 0.3);
}

.btn-secondary {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  border: 2px solid rgba(255, 255, 255, 0.3);
}

.btn-secondary:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.hero-image img {
  width: 100%;
  height: 400px;
  object-fit: cover;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0, 0, 0, 0.3);
}

/* Features Section */
.features-section {
  padding: 4rem 2rem;
  background: rgba(255, 255, 255, 0.05);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.section-title {
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 3rem;
  font-weight: 700;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.feature-card {
  background: rgba(255, 255, 255, 0.1);
  padding: 2rem;
  border-radius: 16px;
  text-align: center;
  backdrop-filter: blur(10px);
  transition: transform 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.feature-card h3 {
  font-size: 1.3rem;
  margin-bottom: 1rem;
  font-weight: 600;
}

.feature-card p {
  opacity: 0.9;
  line-height: 1.6;
}

/* CTA Section */
.cta-section {
  padding: 4rem 2rem;
  text-align: center;
}

.cta-section h2 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  font-weight: 700;
}

.cta-section p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.btn-cta {
  display: inline-block;
  background: linear-gradient(135deg, #ff6b6b, #ee5a52);
  color: white;
  padding: 1rem 2.5rem;
  border-radius: 12px;
  text-decoration: none;
  font-weight: 600;
  font-size: 1.1rem;
  transition: all 0.3s;
}

.btn-cta:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(255, 107, 107, 0.3);
}

/* Footer */
.landing-footer {
  background: rgba(0, 0, 0, 0.3);
  padding: 3rem 2rem 1rem;
}

.footer-content {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 2rem;
}

.footer-col h3, .footer-col h4 {
  margin-bottom: 1rem;
  font-weight: 600;
}

.footer-col a {
  color: white;
  text-decoration: none;
  display: block;
  margin-bottom: 0.5rem;
  opacity: 0.8;
  transition: opacity 0.3s;
}

.footer-col a:hover {
  opacity: 1;
}

.footer-bottom {
  text-align: center;
  padding-top: 2rem;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  opacity: 0.8;
}

/* Responsive */
@media (max-width: 768px) {
  .hero-section {
    grid-template-columns: 1fr;
    text-align: center;
    padding: 6rem 1rem 2rem;
  }
  
  .hero-title {
    font-size: 2.5rem;
  }
  
  .nav-menu {
    display: none;
  }
  
  .auth-buttons {
    gap: 0.5rem;
  }
  
  .btn-login, .btn-register {
    padding: 0.4rem 1rem;
    font-size: 0.9rem;
  }
}
</style> 