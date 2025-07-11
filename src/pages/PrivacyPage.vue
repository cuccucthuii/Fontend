<template>
  <div class="privacy-page">
    <!-- Hero Section -->
    <div class="hero-section">
      <div class="hero-background">
        <div class="hero-overlay"></div>
        <div class="hero-content">
          <div class="hero-icon">
            <svg width="80" height="80" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
              <path d="M12 2L3 7V12C3 16.4183 6.58172 20 11 20H13C17.4183 20 21 16.4183 21 12V7L12 2Z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
              <path d="M12 9L15 12L12 15L9 12L12 9Z" stroke="currentColor" stroke-width="2" stroke-linejoin="round"/>
            </svg>
          </div>
          <h1 class="hero-title">Chính sách bảo mật</h1>
          <p class="hero-subtitle">Cam kết bảo vệ thông tin cá nhân của bạn</p>
        </div>
      </div>
    </div>

    <!-- Content Section -->
    <div class="content-container">
      <div class="content-wrapper">
        <!-- Security Commitment Section -->
        <div class="policy-section">
          <div class="section-header">
            <div class="section-icon">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 2L3 7V12C3 16.4183 6.58172 20 11 20H13C17.4183 20 21 16.4183 21 12V7L12 2Z" stroke="currentColor" stroke-width="2"/>
                <path d="M9 12L11 14L15 10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h2>Cam kết bảo mật</h2>
          </div>
          <div class="section-content">
            <div class="policy-item">
              <div class="item-icon">🔒</div>
              <div class="item-text">
                <h3>Thu thập thông tin có chọn lọc</h3>
                <p>Chúng tôi chỉ thu thập thông tin cần thiết để phục vụ việc đặt vé và chăm sóc khách hàng.</p>
              </div>
            </div>
            <div class="policy-item">
              <div class="item-icon">🤝</div>
              <div class="item-text">
                <h3>Không chia sẻ thông tin</h3>
                <p>Thông tin của bạn sẽ không được chia sẻ cho bên thứ ba nếu không có sự đồng ý rõ ràng.</p>
              </div>
            </div>
            <div class="policy-item">
              <div class="item-icon">✏️</div>
              <div class="item-text">
                <h3>Quyền kiểm soát thông tin</h3>
                <p>Bạn có quyền yêu cầu chỉnh sửa hoặc xóa thông tin cá nhân bất cứ lúc nào.</p>
              </div>
            </div>
            <div class="policy-item">
              <div class="item-icon">🛡️</div>
              <div class="item-text">
                <h3>Bảo mật dữ liệu</h3>
                <p>Chúng tôi sử dụng các biện pháp bảo mật tiên tiến để bảo vệ dữ liệu của bạn.</p>
              </div>
            </div>
          </div>
        </div>

        <!-- Data Protection Section -->
        <div class="policy-section">
          <div class="section-header">
            <div class="section-icon">
              <svg width="32" height="32" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 17.5228 6.47715 22 12 22Z" stroke="currentColor" stroke-width="2"/>
                <path d="M9 12L11 14L15 10" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h2>Bảo vệ dữ liệu</h2>
          </div>
          <div class="protection-grid">
            <div class="protection-card">
              <div class="card-icon">🔐</div>
              <h3>Mã hóa SSL</h3>
              <p>Tất cả dữ liệu được mã hóa bằng SSL 256-bit</p>
            </div>
            <div class="protection-card">
              <div class="card-icon">🏢</div>
              <h3>Server an toàn</h3>
              <p>Dữ liệu được lưu trữ trên server có bảo mật cao</p>
            </div>
            <div class="protection-card">
              <div class="card-icon">👥</div>
              <h3>Kiểm soát truy cập</h3>
              <p>Chỉ nhân viên được ủy quyền mới có thể truy cập</p>
            </div>
          </div>
        </div>

        <!-- Contact Section -->
        <div class="contact-section">
          <div class="contact-card">
            <div class="contact-icon">
              <svg width="48" height="48" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M4 4H20C21.1 4 22 4.9 22 6V18C22 19.1 21.1 20 20 20H4C2.9 20 2 19.1 2 18V6C2 4.9 2.9 4 4 4Z" stroke="currentColor" stroke-width="2"/>
                <path d="M22 6L12 13L2 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
            </div>
            <h3>Liên hệ với chúng tôi</h3>
            <p>Mọi thắc mắc về bảo mật, vui lòng liên hệ:</p>
            <a href="mailto:devcinema@gmail.com" class="contact-email">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
                <path d="M4 4H20C21.1 4 22 4.9 22 6V18C22 19.1 21.1 20 20 20H4C2.9 20 2 19.1 2 18V6C2 4.9 2.9 4 4 4Z" stroke="currentColor" stroke-width="2"/>
                <path d="M22 6L12 13L2 6" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"/>
              </svg>
              devcinema@gmail.com
            </a>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.privacy-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #0f0f23 0%, #1a1a2e 50%, #16213e 100%);
  color: #ffffff;
}

/* Hero Section */
.hero-section {
  position: relative;
  height: 400px;
  overflow: hidden;
}

.hero-background {
  position: relative;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url('data:image/svg+xml,<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100 100"><defs><pattern id="grain" width="100" height="100" patternUnits="userSpaceOnUse"><circle cx="25" cy="25" r="1" fill="white" opacity="0.1"/><circle cx="75" cy="75" r="1" fill="white" opacity="0.1"/><circle cx="50" cy="10" r="0.5" fill="white" opacity="0.1"/><circle cx="10" cy="60" r="0.5" fill="white" opacity="0.1"/><circle cx="90" cy="40" r="0.5" fill="white" opacity="0.1"/></pattern></defs><rect width="100" height="100" fill="url(%23grain)"/></svg>');
  opacity: 0.3;
}

.hero-content {
  text-align: center;
  z-index: 1;
  position: relative;
}

.hero-icon {
  margin-bottom: 20px;
  color: #ffffff;
}

.hero-title {
  font-size: 3.5rem;
  font-weight: 800;
  margin-bottom: 16px;
  background: linear-gradient(45deg, #ffffff, #f0f8ff);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 4px 8px rgba(0,0,0,0.3);
}

.hero-subtitle {
  font-size: 1.2rem;
  color: rgba(255, 255, 255, 0.9);
  font-weight: 300;
}

/* Content Container */
.content-container {
  padding: 60px 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.content-wrapper {
  display: grid;
  gap: 40px;
}

/* Policy Section */
.policy-section {
  background: rgba(255, 255, 255, 0.05);
  border-radius: 20px;
  padding: 40px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
}

.section-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 32px;
}

.section-icon {
  color: #667eea;
  background: rgba(102, 126, 234, 0.1);
  padding: 12px;
  border-radius: 12px;
}

.section-header h2 {
  font-size: 2rem;
  font-weight: 700;
  color: #ffffff;
  margin: 0;
}

.section-content {
  display: grid;
  gap: 24px;
}

.policy-item {
  display: flex;
  gap: 20px;
  padding: 24px;
  background: rgba(255, 255, 255, 0.03);
  border-radius: 16px;
  border: 1px solid rgba(255, 255, 255, 0.05);
  transition: all 0.3s ease;
}

.policy-item:hover {
  background: rgba(255, 255, 255, 0.08);
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
}

.item-icon {
  font-size: 2rem;
  flex-shrink: 0;
}

.item-text h3 {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 8px;
  color: #667eea;
}

.item-text p {
  color: rgba(255, 255, 255, 0.8);
  line-height: 1.6;
  margin: 0;
}

/* Protection Grid */
.protection-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  margin-top: 24px;
}

.protection-card {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
  border-radius: 16px;
  padding: 32px 24px;
  text-align: center;
  border: 1px solid rgba(102, 126, 234, 0.2);
  transition: all 0.3s ease;
}

.protection-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(102, 126, 234, 0.2);
}

.card-icon {
  font-size: 3rem;
  margin-bottom: 16px;
}

.protection-card h3 {
  font-size: 1.3rem;
  font-weight: 600;
  margin-bottom: 12px;
  color: #667eea;
}

.protection-card p {
  color: rgba(255, 255, 255, 0.7);
  line-height: 1.5;
  margin: 0;
}

/* Contact Section */
.contact-section {
  text-align: center;
}

.contact-card {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.15), rgba(118, 75, 162, 0.15));
  border-radius: 20px;
  padding: 48px 32px;
  border: 1px solid rgba(102, 126, 234, 0.3);
  backdrop-filter: blur(10px);
}

.contact-icon {
  color: #667eea;
  margin-bottom: 24px;
}

.contact-card h3 {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 16px;
  color: #ffffff;
}

.contact-card p {
  font-size: 1.1rem;
  color: rgba(255, 255, 255, 0.8);
  margin-bottom: 24px;
}

.contact-email {
  display: inline-flex;
  align-items: center;
  gap: 12px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #ffffff;
  text-decoration: none;
  padding: 16px 32px;
  border-radius: 50px;
  font-weight: 600;
  font-size: 1.1rem;
  transition: all 0.3s ease;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.3);
}

.contact-email:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
  color: #ffffff;
}

/* Responsive Design */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-section {
    height: 300px;
  }
  
  .content-container {
    padding: 40px 16px;
  }
  
  .policy-section {
    padding: 24px;
  }
  
  .section-header h2 {
    font-size: 1.5rem;
  }
  
  .policy-item {
    flex-direction: column;
    text-align: center;
  }
  
  .protection-grid {
    grid-template-columns: 1fr;
  }
  
  .contact-card {
    padding: 32px 24px;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-subtitle {
    font-size: 1rem;
  }
  
  .policy-section {
    padding: 20px;
  }
  
  .contact-card {
    padding: 24px 20px;
  }
}
</style> 