<template>
  <HomeHeader />
  <div class="contact-page">
    <!-- Hero Section -->
    <div class="contact-hero">
      <img src="https://images.unsplash.com/photo-1557804506-669a67965ba0?auto=format&fit=crop&w=1200&q=80" alt="Contact Banner" class="contact-hero-img" />
      <div class="contact-hero-content">
        <h1>Liên Hệ Với Chúng Tôi</h1>
        <p>Chúng tôi luôn sẵn sàng lắng nghe và hỗ trợ bạn mọi lúc!</p>
      </div>
    </div>

    <!-- Contact Info Cards -->
    <div class="contact-cards">
      <div class="contact-card">
        <div class="contact-card-icon">📞</div>
        <h3>Hotline</h3>
        <p>0123 456 789</p>
        <span>Hỗ trợ 24/7</span>
      </div>
      <div class="contact-card">
        <div class="contact-card-icon">✉️</div>
        <h3>Email</h3>
        <p>devcinema@gmail.com</p>
        <span>Phản hồi nhanh</span>
      </div>
      <div class="contact-card">
        <div class="contact-card-icon">📍</div>
        <h3>Địa chỉ</h3>
        <p>FPT Polytechnic Hà Nội</p>
        <span>Nam Từ Liêm, Hà Nội</span>
      </div>
    </div>

    <!-- Main Content -->
    <div class="contact-main">
      <div class="contact-info-section">
        <h2>Thông Tin Liên Hệ</h2>
        <div class="contact-details">
          <div class="contact-detail">
            <div class="contact-detail-icon">🏢</div>
            <div>
              <h4>Trụ sở chính</h4>
              <p>Trường CD FPT Polytechnic, Phố Trịnh Văn Bô, Nam Từ Liêm, Hà Nội</p>
            </div>
          </div>
          <div class="contact-detail">
            <div class="contact-detail-icon">🕒</div>
            <div>
              <h4>Giờ làm việc</h4>
              <p>Thứ 2 - Chủ nhật: 8:00 - 23:00</p>
            </div>
          </div>
          <div class="contact-detail">
            <div class="contact-detail-icon">💬</div>
            <div>
              <h4>Hỗ trợ khách hàng</h4>
              <p>Hotline: 0123 456 789 | Email: support@devcinema.com</p>
            </div>
          </div>
        </div>
        
        <div class="social-media">
          <h3>Theo dõi chúng tôi</h3>
          <div class="social-links">
            <a href="https://facebook.com" target="_blank" class="social-link facebook">
              <i class="fab fa-facebook-f"></i>
              <span>Facebook</span>
            </a>
            <a href="https://instagram.com" target="_blank" class="social-link instagram">
              <i class="fab fa-instagram"></i>
              <span>Instagram</span>
            </a>
            <a href="https://tiktok.com" target="_blank" class="social-link tiktok">
              <i class="fab fa-tiktok"></i>
              <span>TikTok</span>
            </a>
            <a href="https://youtube.com" target="_blank" class="social-link youtube">
              <i class="fab fa-youtube"></i>
              <span>YouTube</span>
            </a>
          </div>
        </div>
      </div>

      <div class="contact-form-section">
        <h2>Gửi Tin Nhắn</h2>
        <form class="contact-form" @submit.prevent="sendEmail">
          <div class="form-group">
            <label>Họ và tên *</label>
            <input v-model="form.name" type="text" required />
          </div>
          <div class="form-group">
            <label>Email *</label>
            <input v-model="form.email" type="email" required />
          </div>
          <div class="form-group">
            <label>Số điện thoại</label>
            <input v-model="form.phone" type="tel" />
          </div>
          <div class="form-group">
            <label>Tiêu đề *</label>
            <input v-model="form.title" type="text" required />
          </div>
          <div class="form-group">
            <label>Nội dung *</label>
            <textarea v-model="form.message" rows="6" required></textarea>
          </div>
          <button type="submit" :disabled="loading" class="submit-btn">
            <span v-if="loading">Đang gửi...</span>
            <span v-else>Gửi tin nhắn</span>
          </button>
        </form>
        
        <div v-if="successMsg" class="message success">
          <div class="message-icon">✅</div>
          <div class="message-content">{{ successMsg }}</div>
        </div>
        <div v-if="errorMsg" class="message error">
          <div class="message-icon">❌</div>
          <div class="message-content">{{ errorMsg }}</div>
        </div>
      </div>
    </div>

    <!-- Map Section -->
    <div class="map-section">
      <h2>Vị Trí Của Chúng Tôi</h2>
      <div class="map-container">
        <iframe 
          src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d3723.8638060211256!2d105.74468151089454!3d21.03813478737556!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x313455e940879933%3A0xcf10b34e9f1a03df!2zVHLGsOG7nW5nIENhbyDEkeG6s25nIEZQVCBQb2x5dGVjaG5pYw!5e0!3m2!1svi!2s!4v1751900364473!5m2!1svi!2s"" 
          width="100%" 
          height="400" 
          style="border:0;" 
          allowfullscreen="" 
          loading="lazy">
        </iframe>
      </div>
    </div>
  </div>
  <HomeFooter />
</template>

<script setup>
import { ref } from 'vue'
import emailjs from 'emailjs-com'
import HomeHeader from '@/components/HomeHeader.vue'
import HomeFooter from '@/components/HomeFooter.vue'

const form = ref({ 
  name: '', 
  email: '', 
  phone: '', 
  title: '', 
  message: '' 
})
const loading = ref(false)
const successMsg = ref('')
const errorMsg = ref('')

function sendEmail() {
  loading.value = true
  successMsg.value = ''
  errorMsg.value = ''
  
  emailjs.send('service_1o1drn9', 'template_0ytqpq7', {
    name: form.value.name,
    email: form.value.email,
    phone: form.value.phone,
    title: form.value.title,
    message: form.value.message
  }, 'VHA_JnhQmcbYK_yDZ')
    .then(() => {
      successMsg.value = 'Cảm ơn bạn đã liên hệ! Chúng tôi sẽ phản hồi trong thời gian sớm nhất.'
      form.value = { name: '', email: '', phone: '', title: '', message: '' }
    })
    .catch(() => {
      errorMsg.value = 'Có lỗi xảy ra khi gửi tin nhắn. Vui lòng thử lại sau.'
    })
    .finally(() => {
      loading.value = false
    })
}
</script>

<style scoped>
.contact-page {
  min-height: 100vh;
  background: #18191a;
  color: #fff;
}

/* Hero Section */
.contact-hero {
  position: relative;
  width: 100%;
  height: 400px;
  overflow: hidden;
  margin-bottom: 60px;
}

.contact-hero-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: brightness(0.6);
}

.contact-hero-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: #fff;
  z-index: 2;
}

.contact-hero-content h1 {
  font-size: 3.5rem;
  font-weight: 900;
  margin-bottom: 16px;
  color: #48dbfb;
  text-shadow: 0 4px 20px rgba(0,0,0,0.8);
}

.contact-hero-content p {
  font-size: 1.3rem;
  color: #feca57;
  max-width: 600px;
  margin: 0 auto;
}

/* Contact Cards */
.contact-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 24px;
  max-width: 1200px;
  margin: 0 auto 60px;
  padding: 0 20px;
}

.contact-card {
  background: linear-gradient(135deg, #232526 0%, #414345 100%);
  border-radius: 20px;
  padding: 32px 24px;
  text-align: center;
  box-shadow: 0 8px 32px rgba(72,219,251,0.15);
  border: 1px solid rgba(72,219,251,0.1);
  transition: all 0.3s ease;
}

.contact-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 16px 48px rgba(72,219,251,0.25);
  border-color: rgba(72,219,251,0.3);
}

.contact-card-icon {
  font-size: 3rem;
  margin-bottom: 16px;
}

.contact-card h3 {
  font-size: 1.4rem;
  font-weight: 700;
  color: #48dbfb;
  margin-bottom: 8px;
}

.contact-card p {
  font-size: 1.1rem;
  font-weight: 600;
  margin-bottom: 8px;
  color: #fff;
}

.contact-card span {
  font-size: 0.9rem;
  color: #b2bec3;
}

/* Main Content */
.contact-main {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 60px;
  max-width: 1200px;
  margin: 0 auto 60px;
  padding: 0 20px;
}

.contact-info-section h2,
.contact-form-section h2 {
  font-size: 2rem;
  font-weight: 800;
  color: #48dbfb;
  margin-bottom: 32px;
  text-align: center;
}

/* Contact Details */
.contact-details {
  display: flex;
  flex-direction: column;
  gap: 24px;
  margin-bottom: 40px;
}

.contact-detail {
  display: flex;
  gap: 16px;
  align-items: flex-start;
  padding: 20px;
  background: rgba(72,219,251,0.05);
  border-radius: 16px;
  border: 1px solid rgba(72,219,251,0.1);
}

.contact-detail-icon {
  font-size: 1.5rem;
  flex-shrink: 0;
}

.contact-detail h4 {
  font-size: 1.1rem;
  font-weight: 700;
  color: #48dbfb;
  margin-bottom: 8px;
}

.contact-detail p {
  color: #b2bec3;
  line-height: 1.6;
}

/* Social Media */
.social-media h3 {
  font-size: 1.3rem;
  font-weight: 700;
  color: #48dbfb;
  margin-bottom: 20px;
  text-align: center;
}

.social-links {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.social-link {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-radius: 12px;
  text-decoration: none;
  color: #fff;
  font-weight: 600;
  transition: all 0.3s ease;
}

.social-link.facebook { background: linear-gradient(135deg, #1877f2 0%, #0d6efd 100%); }
.social-link.instagram { background: linear-gradient(135deg, #e4405f 0%, #f77737 100%); }
.social-link.tiktok { background: linear-gradient(135deg, #000000 0%, #25f4ee 100%); }
.social-link.youtube { background: linear-gradient(135deg, #ff0000 0%, #cc0000 100%); }

.social-link:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0,0,0,0.3);
}

.social-link i {
  font-size: 1.2rem;
}

/* Contact Form */
.contact-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-group label {
  font-weight: 600;
  color: #48dbfb;
  font-size: 0.95rem;
}

.form-group input,
.form-group textarea {
  background: #232526;
  border: 2px solid rgba(72,219,251,0.2);
  border-radius: 12px;
  padding: 16px;
  color: #fff;
  font-size: 1rem;
  outline: none;
  transition: all 0.3s ease;
}

.form-group input:focus,
.form-group textarea:focus {
  border-color: #48dbfb;
  box-shadow: 0 0 0 4px rgba(72,219,251,0.1);
}

.form-group textarea {
  resize: vertical;
  min-height: 120px;
}

.submit-btn {
  background: linear-gradient(135deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  padding: 16px 32px;
  font-size: 1.1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  margin-top: 8px;
}

.submit-btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(72,219,251,0.3);
}

.submit-btn:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

/* Messages */
.message {
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 16px;
  border-radius: 12px;
  margin-top: 20px;
}

.message.success {
  background: rgba(39, 174, 96, 0.1);
  border: 1px solid rgba(39, 174, 96, 0.3);
  color: #27ae60;
}

.message.error {
  background: rgba(214, 48, 49, 0.1);
  border: 1px solid rgba(214, 48, 49, 0.3);
  color: #d63031;
}

.message-icon {
  font-size: 1.2rem;
  flex-shrink: 0;
}

.message-content {
  font-weight: 600;
}

/* Map Section */
.map-section {
  max-width: 1200px;
  margin: 0 auto 60px;
  padding: 0 20px;
}

.map-section h2 {
  font-size: 2rem;
  font-weight: 800;
  color: #48dbfb;
  margin-bottom: 32px;
  text-align: center;
}

.map-container {
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 8px 32px rgba(72,219,251,0.15);
  border: 1px solid rgba(72,219,251,0.1);
}

/* Responsive */
@media (max-width: 768px) {
  .contact-hero-content h1 {
    font-size: 2.5rem;
  }
  
  .contact-hero-content p {
    font-size: 1.1rem;
  }
  
  .contact-main {
    grid-template-columns: 1fr;
    gap: 40px;
  }
  
  .social-links {
    grid-template-columns: 1fr;
  }
  
  .contact-cards {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 480px) {
  .contact-hero {
    height: 300px;
  }
  
  .contact-hero-content h1 {
    font-size: 2rem;
  }
  
  .contact-card {
    padding: 24px 16px;
  }
}
</style> 
