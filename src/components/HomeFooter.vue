<template>
  <footer class="home-footer">
    <div class="footer-container">
      <!-- Main Footer Content -->
      <div class="footer-main">
        <div class="footer-section">
          <div class="footer-logo">
            <h3>DevCinema</h3>
            <p>Hệ thống đặt vé xem phim hiện đại</p>
          </div>
        </div>
        
        <div class="footer-section">
          <h4>Liên kết nhanh</h4>
          <ul class="footer-links">
            <li><router-link to="/">Trang chủ</router-link></li>
            <li><router-link to="/movies">Phim đang chiếu</router-link></li>
            <li><router-link to="/upcoming">Phim sắp chiếu</router-link></li>
            <li><router-link to="/cinemas">Rạp chiếu</router-link></li>
            <li><router-link to="/promotions">Khuyến mãi</router-link></li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h4>Hỗ trợ</h4>
          <ul class="footer-links">
            <li><router-link to="/contact">Liên hệ</router-link></li>
            <li><router-link to="/faq">Câu hỏi thường gặp</router-link></li>
            <li><router-link to="/terms">Điều khoản sử dụng</router-link></li>
            <li><router-link to="/privacy">Chính sách bảo mật</router-link></li>
            <li><router-link to="/refund">Chính sách hoàn vé</router-link></li>
          </ul>
        </div>
        
        <div class="footer-section">
          <h4>Kết nối với chúng tôi</h4>
          <div class="social-links">
            <a href="#" class="social-link facebook" aria-label="Facebook">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
              </svg>
            </a>
            <a href="#" class="social-link instagram" aria-label="Instagram">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 2.163c3.204 0 3.584.012 4.85.07 3.252.148 4.771 1.691 4.919 4.919.058 1.265.069 1.645.069 4.849 0 3.205-.012 3.584-.069 4.849-.149 3.225-1.664 4.771-4.919 4.919-1.266.058-1.644.07-4.85.07-3.204 0-3.584-.012-4.849-.07-3.26-.149-4.771-1.699-4.919-4.92-.058-1.265-.07-1.644-.07-4.849 0-3.204.013-3.583.07-4.849.149-3.227 1.664-4.771 4.919-4.919 1.266-.057 1.645-.069 4.849-.069zm0-2.163c-3.259 0-3.667.014-4.947.072-4.358.2-6.78 2.618-6.98 6.98-.059 1.281-.073 1.689-.073 4.948 0 3.259.014 3.668.072 4.948.2 4.358 2.618 6.78 6.98 6.98 1.281.058 1.689.072 4.948.072 3.259 0 3.668-.014 4.948-.072 4.354-.2 6.782-2.618 6.979-6.98.059-1.28.073-1.689.073-4.948 0-3.259-.014-3.667-.072-4.947-.196-4.354-2.617-6.78-6.979-6.98-1.281-.059-1.69-.073-4.949-.073zm0 5.838c-3.403 0-6.162 2.759-6.162 6.162s2.759 6.163 6.162 6.163 6.162-2.759 6.162-6.163c0-3.403-2.759-6.162-6.162-6.162zm0 10.162c-2.209 0-4-1.79-4-4 0-2.209 1.791-4 4-4s4 1.791 4 4c0 2.21-1.791 4-4 4zm6.406-11.845c-.796 0-1.441.645-1.441 1.44s.645 1.44 1.441 1.44c.795 0 1.439-.645 1.439-1.44s-.644-1.44-1.439-1.44z"/>
              </svg>
            </a>
            <a href="#" class="social-link youtube" aria-label="YouTube">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                <path d="M23.498 6.186a3.016 3.016 0 0 0-2.122-2.136C19.505 3.545 12 3.545 12 3.545s-7.505 0-9.377.505A3.017 3.017 0 0 0 .502 6.186C0 8.07 0 12 0 12s0 3.93.502 5.814a3.016 3.016 0 0 0 2.122 2.136c1.871.505 9.376.505 9.376.505s7.505 0 9.377-.505a3.015 3.015 0 0 0 2.122-2.136C24 15.93 24 12 24 12s0-3.93-.502-5.814zM9.545 15.568V8.432L15.818 12l-6.273 3.568z"/>
              </svg>
            </a>
            <a href="#" class="social-link tiktok" aria-label="TikTok">
              <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12.525.02c1.31-.02 2.61-.01 3.91-.02.08 1.53.63 3.09 1.75 4.17 1.12 1.11 2.7 1.62 4.24 1.79v4.03c-1.44-.05-2.89-.35-4.2-.97-.57-.26-1.1-.59-1.62-.93-.01 2.92.01 5.84-.02 8.75-.08 1.4-.54 2.79-1.35 3.94-1.31 1.92-3.58 3.17-5.91 3.21-1.43.08-2.86-.31-4.08-1.03-2.02-1.19-3.44-3.37-3.65-5.71-.02-.5-.03-1-.01-1.49.18-1.9 1.12-3.72 2.58-4.96 1.66-1.44 3.98-2.13 6.15-1.72.02 1.48-.04 2.96-.04 4.44-.99-.32-2.15-.23-3.02.37-.63.41-1.11 1.04-1.36 1.75-.21.51-.15 1.07-.14 1.61.24 1.64 1.82 3.02 3.5 2.87 1.12-.01 2.19-.66 2.77-1.61.19-.33.4-.67.41-1.06.1-1.79.06-3.57.07-5.36.01-4.03-.01-8.05.02-12.07z"/>
              </svg>
            </a>
          </div>
          
          <div class="contact-info">
            <div class="contact-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                <path d="M20 4H4c-1.1 0-1.99.9-1.99 2L2 18c0 1.1.9 2 2 2h16c1.1 0 2-.9 2-2V6c0-1.1-.9-2-2-2zm0 4l-8 5-8-5V6l8 5 8-5v2z"/>
              </svg>
              <span>support@devcinema.com</span>
            </div>
            <div class="contact-item">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                <path d="M6.62 10.79c1.44 2.83 3.76 5.14 6.59 6.59l2.2-2.2c.27-.27.67-.36 1.02-.24 1.12.37 2.33.57 3.57.57.55 0 1 .45 1 1V20c0 .55-.45 1-1 1-9.39 0-17-7.61-17-17 0-.55.45-1 1-1h3.5c.55 0 1 .45 1 1 0 1.25.2 2.45.57 3.57.11.35.03.74-.25 1.02l-2.2 2.2z"/>
              </svg>
              <span>1900 1234</span>
            </div>
          </div>
        </div>
      </div>
      
      <!-- Newsletter Subscription -->
      <div class="newsletter-section">
        <div class="newsletter-content">
          <h4>Đăng ký nhận thông tin</h4>
          <p>Nhận thông tin về phim mới, khuyến mãi và sự kiện đặc biệt</p>
          <form @submit.prevent="handleNewsletterSubmit" class="newsletter-form">
            <input
              v-model="newsletterEmail"
              type="email"
              placeholder="Nhập email của bạn"
              required
              class="newsletter-input"
            />
            <button type="submit" class="newsletter-btn" :disabled="isSubmitting">
              {{ isSubmitting ? 'Đang gửi...' : 'Đăng ký' }}
            </button>
          </form>
        </div>
      </div>
      
      <!-- Footer Bottom -->
      <div class="footer-bottom">
        <div class="footer-bottom-content">
          <div class="copyright">
            <p>&copy; {{ currentYear }} DevCinema. Tất cả quyền được bảo lưu.</p>
          </div>
          <div class="payment-methods">
            <span>Phương thức thanh toán:</span>
            <div class="payment-icons">
              <div class="payment-icon visa">VISA</div>
              <div class="payment-icon mastercard">MC</div>
              <div class="payment-icon momo">MoMo</div>
              <div class="payment-icon zalopay">ZaloPay</div>
              <div class="payment-icon vnpay">VNPay</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { ref, computed } from 'vue'

// Reactive data
const newsletterEmail = ref('')
const isSubmitting = ref(false)

// Computed
const currentYear = computed(() => new Date().getFullYear())

// Methods
const handleNewsletterSubmit = async () => {
  if (!newsletterEmail.value) return
  
  isSubmitting.value = true
  
  try {
    // TODO: Implement newsletter subscription API
    console.log('Newsletter subscription:', newsletterEmail.value)
    
    // Simulate API call
    await new Promise(resolve => setTimeout(resolve, 1000))
    
    // Success
    alert('Đăng ký nhận thông tin thành công!')
    newsletterEmail.value = ''
    
  } catch (error) {
    console.error('Newsletter subscription error:', error)
    alert('Có lỗi xảy ra, vui lòng thử lại!')
  } finally {
    isSubmitting.value = false
  }
}
</script>

<style scoped>
.home-footer {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  color: white;
  margin-top: auto;
}

.footer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.footer-main {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 40px;
  padding: 60px 0 40px;
}

.footer-section h4 {
  font-size: 1.2rem;
  font-weight: 600;
  margin-bottom: 20px;
  color: #f8fafc;
}

.footer-logo h3 {
  font-size: 1.8rem;
  font-weight: 700;
  margin-bottom: 8px;
  background: linear-gradient(45deg, #3b82f6, #8b5cf6);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.footer-logo p {
  color: #94a3b8;
  font-size: 0.9rem;
}

.footer-links {
  list-style: none;
  padding: 0;
  margin: 0;
}

.footer-links li {
  margin-bottom: 12px;
}

.footer-links a {
  color: #cbd5e1;
  text-decoration: none;
  transition: color 0.3s ease;
  font-size: 0.9rem;
}

.footer-links a:hover {
  color: #3b82f6;
}

.social-links {
  display: flex;
  gap: 12px;
  margin-bottom: 24px;
}

.social-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
  color: #cbd5e1;
  text-decoration: none;
  transition: all 0.3s ease;
}

.social-link:hover {
  background: rgba(255, 255, 255, 0.2);
  color: white;
  transform: translateY(-2px);
}

.social-link.facebook:hover {
  background: #1877f2;
}

.social-link.instagram:hover {
  background: linear-gradient(45deg, #f09433, #e6683c, #dc2743, #cc2366, #bc1888);
}

.social-link.youtube:hover {
  background: #ff0000;
}

.social-link.tiktok:hover {
  background: #000000;
}

.contact-info {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #cbd5e1;
  font-size: 0.9rem;
}

.contact-item svg {
  color: #3b82f6;
}

.newsletter-section {
  background: rgba(255, 255, 255, 0.05);
  padding: 40px 0;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.newsletter-content {
  text-align: center;
  max-width: 500px;
  margin: 0 auto;
}

.newsletter-content h4 {
  font-size: 1.4rem;
  font-weight: 600;
  margin-bottom: 8px;
  color: #f8fafc;
}

.newsletter-content p {
  color: #94a3b8;
  margin-bottom: 24px;
  font-size: 0.9rem;
}

.newsletter-form {
  display: flex;
  gap: 12px;
  max-width: 400px;
  margin: 0 auto;
}

.newsletter-input {
  flex: 1;
  padding: 12px 16px;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.1);
  color: white;
  font-size: 0.9rem;
}

.newsletter-input::placeholder {
  color: #94a3b8;
}

.newsletter-input:focus {
  outline: none;
  border-color: #3b82f6;
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
}

.newsletter-btn {
  padding: 12px 24px;
  background: linear-gradient(45deg, #3b82f6, #8b5cf6);
  color: white;
  border: none;
  border-radius: 8px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  white-space: nowrap;
}

.newsletter-btn:hover:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.newsletter-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.footer-bottom {
  padding: 24px 0;
}

.footer-bottom-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 20px;
}

.copyright p {
  color: #94a3b8;
  font-size: 0.9rem;
  margin: 0;
}

.payment-methods {
  display: flex;
  align-items: center;
  gap: 12px;
  color: #94a3b8;
  font-size: 0.9rem;
}

.payment-icons {
  display: flex;
  gap: 8px;
}

.payment-icon {
  padding: 4px 8px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 4px;
  font-size: 0.8rem;
  font-weight: 500;
  color: #cbd5e1;
}

.payment-icon.visa {
  background: #1a1f71;
  color: white;
}

.payment-icon.mastercard {
  background: #eb001b;
  color: white;
}

.payment-icon.momo {
  background: #d82d8b;
  color: white;
}

.payment-icon.zalopay {
  background: #0068ff;
  color: white;
}

.payment-icon.vnpay {
  background: #0052a5;
  color: white;
}

/* Responsive Design */
@media (max-width: 768px) {
  .footer-main {
    grid-template-columns: 1fr;
    gap: 30px;
    padding: 40px 0 30px;
  }
  
  .newsletter-form {
    flex-direction: column;
  }
  
  .footer-bottom-content {
    flex-direction: column;
    text-align: center;
  }
  
  .payment-methods {
    flex-direction: column;
    gap: 8px;
  }
}

@media (max-width: 480px) {
  .footer-container {
    padding: 0 16px;
  }
  
  .footer-main {
    padding: 30px 0 20px;
  }
  
  .newsletter-section {
    padding: 30px 0;
  }
  
  .social-links {
    justify-content: center;
  }
}
</style>
