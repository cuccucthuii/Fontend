<script setup>
import HomeFooter from '@/components/HomeFooter.vue'
import Header from '@/components/Header.vue'
</script>

<template>
  <Header />
  <div class="about-page">
    <!-- Hero Section -->
    <section class="hero-section">
      <div class="hero-background">
        <img src="https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?auto=format&fit=crop&w=1920&q=80" alt="Cinema Background" />
        <div class="hero-overlay"></div>
      </div>
      <div class="hero-content">
        <div class="container">
          <h1 class="hero-title">DEV CINEMA</h1>
          <p class="hero-subtitle">Nơi cảm xúc thăng hoa, nơi ước mơ được thực hiện</p>
          <div class="hero-stats">
            <div class="stat-item">
              <span class="stat-number">10+</span>
              <span class="stat-label">Rạp chiếu phim</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">100K+</span>
              <span class="stat-label">Khách hàng</span>
            </div>
            <div class="stat-item">
              <span class="stat-number">10+</span>
              <span class="stat-label">Năm kinh nghiệm</span>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Story Section -->
    <section class="story-section">
      <div class="container">
        <div class="story-content">
          <div class="story-text">
            <h2>Câu chuyện của chúng tôi</h2>
            <p class="story-lead">Từ một ước mơ nhỏ đến hệ thống rạp chiếu phim hàng đầu Việt Nam</p>
            <p>DEV CINEMA được thành lập với sứ mệnh mang đến trải nghiệm điện ảnh tuyệt vời nhất cho mọi khách hàng. Chúng tôi không chỉ chiếu phim, mà còn lan tỏa cảm xúc, kết nối cộng đồng yêu điện ảnh.</p>
            <p>Với hơn 10 năm kinh nghiệm trong lĩnh vực giải trí, chúng tôi tự hào là đối tác tin cậy của các hãng phim lớn và được hàng triệu khách hàng yêu mến.</p>
          </div>
          <div class="story-image">
            <img src="https://images.unsplash.com/photo-1517602302552-471fe67acf66?auto=format&fit=crop&w=600&q=80" alt="Cinema Story" />
          </div>
        </div>
      </div>
    </section>

    <!-- Values Section -->
    <section class="values-section">
      <div class="container">
        <h2 class="section-title">Giá trị cốt lõi</h2>
        <div class="values-grid">
          <div class="value-card">
            <div class="value-icon">🎬</div>
            <h3>Chất lượng đỉnh cao</h3>
            <p>Hình ảnh 4K sắc nét, âm thanh Dolby Atmos sống động, không gian sang trọng với ghế ngồi cao cấp.</p>
          </div>
          <div class="value-card">
            <div class="value-icon">💡</div>
            <h3>Đổi mới sáng tạo</h3>
            <p>Không ngừng cải tiến công nghệ, tổ chức sự kiện đặc biệt, tuần phim và giao lưu nghệ sĩ.</p>
          </div>
          <div class="value-card">
            <div class="value-icon">🤝</div>
            <h3>Khách hàng là trung tâm</h3>
            <p>Dịch vụ tận tâm 24/7, ưu đãi hấp dẫn, hỗ trợ nhanh chóng và chuyên nghiệp.</p>
          </div>
          <div class="value-card">
            <div class="value-icon">🌟</div>
            <h3>Cộng đồng yêu điện ảnh</h3>
            <p>Kết nối, chia sẻ đam mê, xây dựng môi trường văn minh và thân thiện.</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Team Section -->
    <section class="team-section">
      <div class="container">
        <h2 class="section-title">Đội ngũ lãnh đạo</h2>
        <div class="team-grid">
          <div class="team-member">
            <div class="member-image">
              <img src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?auto=format&fit=crop&w=400&q=80" alt="CEO" />
            </div>
            <div class="member-info">
              <h3>Phan Chí Kiên</h3>
              <p class="member-role">CEO & Founder</p>
              <p class="member-desc">Với kinh nghiệm trong lĩnh vực giải trí và công nghệ, Kiên đã xây dựng DEV CINEMA từ một ý tưởng nhỏ thành hệ thống rạp chiếu phim hàng đầu.</p>
            </div>
          </div>
          <div class="team-member">
            <div class="member-image">
              <img src="https://images.unsplash.com/photo-1494790108755-2616b612b786?auto=format&fit=crop&w=400&q=80" alt="CMO" />
            </div>
            <div class="member-info">
              <h3>Nguyễn Minh Anh</h3>
              <p class="member-role">CMO - Marketing</p>
              <p class="member-desc">Minh Anh với khả năng sáng tạo xuất sắc, đã xây dựng thương hiệu DEV CINEMA thành một trong những thương hiệu được yêu thích nhất.</p>
            </div>
          </div>
          <div class="team-member">
            <div class="member-image">
              <img src="https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?auto=format&fit=crop&w=400&q=80" alt="CTO" />
            </div>
            <div class="member-info">
              <h3>Đỗ Lan Anh</h3>
              <p class="member-role">CTO - Công nghệ</p>
              <p class="member-desc">Lan Anh đã phát triển hệ thống quản lý và ứng dụng di động hiện đại cho DEV CINEMA.</p>
            </div>
          </div>
          <div class="team-member">
            <div class="member-image">
              <img src="https://images.unsplash.com/photo-1438761681033-6461ffad8d80?auto=format&fit=crop&w=400&q=80" alt="CFO" />
            </div>
            <div class="member-info">
              <h3>Đinh Trí Nhật Hùng</h3>
              <p class="member-role">CFO - Tài chính</p>
              <p class="member-desc">Hùng đã xây dựng chiến lược tài chính vững mạnh và quản lý hiệu quả nguồn lực cho DEV CINEMA.</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Facilities Section -->
    <section class="facilities-section">
      <div class="container">
        <h2 class="section-title">Cơ sở vật chất hiện đại</h2>
        <div class="facilities-grid">
          <div class="facility-card">
            <img src="https://cdn.dealtoday.vn/Rap-chieu-phim-Lotte-Cinema-01_14112024152553.png" alt="Premium Seats" />
            <div class="facility-content">
              <h3>Ghế ngồi cao cấp</h3>
              <p>Ghế ngồi được thiết kế ergonomic với chất liệu cao cấp, có thể điều chỉnh và massage, mang đến trải nghiệm thoải mái tối đa.</p>
            </div>
          </div>
          <div class="facility-card">
            <img src="https://evgroup.vn/wp-content/uploads/2024/04/thiet_bi_rap_phim_06.jpg" alt="Sound System" />
            <div class="facility-content">
              <h3>Hệ thống âm thanh Dolby Atmos</h3>
              <p>Âm thanh vòm 3D với 64 kênh độc lập, tạo ra trải nghiệm âm thanh sống động và chân thực như đang ở trong phim.</p>
            </div>
          </div>
          <div class="facility-card">
            <img src="https://manhinhledsmd.com/wp-content/uploads/2023/12/man-hinh-led-cinema.jpg" alt="4K Projection" />
            <div class="facility-content">
              <h3>Màn hình 4K Ultra HD</h3>
              <p>Hình ảnh sắc nét với độ phân giải 4K, màu sắc chân thực và độ tương phản cao, mang đến trải nghiệm thị giác tuyệt vời.</p>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Awards Section -->
    <section class="awards-section">
      <div class="container">
        <h2 class="section-title">Thành tựu và giải thưởng</h2>
        <div class="awards-grid">
          <div class="award-item">
            <div class="award-icon">🏆</div>
            <h3>Rạp chiếu phim tốt nhất 2023</h3>
            <p>Giải thưởng từ Hiệp hội Điện ảnh Việt Nam</p>
          </div>
          <div class="award-item">
            <div class="award-icon">⭐</div>
            <h3>Top 10 thương hiệu uy tín</h3>
            <p>Bình chọn từ người tiêu dùng Việt Nam</p>
          </div>
          <div class="award-item">
            <div class="award-icon">🎯</div>
            <h3>Dịch vụ khách hàng xuất sắc</h3>
            <p>Chứng nhận ISO 9001:2015</p>
          </div>
          <div class="award-item">
            <div class="award-icon">🚀</div>
            <h3>Doanh nghiệp tăng trưởng nhanh</h3>
            <p>Top 50 doanh nghiệp Việt Nam</p>
          </div>
        </div>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta-section">
      <div class="container">
        <div class="cta-content">
          <h2>Trải nghiệm điện ảnh đỉnh cao ngay hôm nay</h2>
          <p>Đặt vé xem phim và khám phá thế giới điện ảnh tuyệt vời tại DEV CINEMA</p>
          <router-link to="/" class="cta-button">Đặt vé ngay</router-link>
        </div>
      </div>
    </section>
  </div>
  <HomeFooter />
</template>

<style scoped>
.about-page {
  min-height: 100vh;
  background: #0a0a0a;
  color: #ffffff;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

/* Hero Section */
.hero-section {
  position: relative;
  height: 100vh;
  display: flex;
  align-items: center;
  overflow: hidden;
}

.hero-background {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
}

.hero-background img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.hero-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, rgba(0,0,0,0.7) 0%, rgba(72,219,251,0.3) 100%);
  z-index: 2;
}

.hero-content {
  position: relative;
  z-index: 3;
  width: 100%;
  text-align: center;
}

.hero-title {
  font-size: 32px;
  font-weight: 900;
  margin-bottom: 18px;
  color: #48dbfb;
  letter-spacing: 2px;
}

.hero-subtitle {
  font-size: 1.5rem;
  margin-bottom: 3rem;
  color: #ffffff;
  font-weight: 300;
}

.hero-stats {
  display: flex;
  justify-content: center;
  gap: 4rem;
  margin-top: 3rem;
}

.stat-item {
  text-align: center;
}

.stat-number {
  display: block;
  font-size: 3rem;
  font-weight: 900;
  color: #48dbfb;
  margin-bottom: 0.5rem;
}

.stat-label {
  font-size: 1rem;
  color: #cccccc;
  text-transform: uppercase;
  letter-spacing: 1px;
}

/* Story Section */
.story-section {
  padding: 6rem 0;
  background: #111111;
}

.story-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 4rem;
  align-items: center;
}

.story-text h2 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  color: #48dbfb;
}

.story-lead {
  font-size: 1.3rem;
  color: #feca57;
  margin-bottom: 1.5rem;
  font-weight: 600;
}

.story-text p {
  font-size: 1.1rem;
  line-height: 1.8;
  margin-bottom: 1rem;
  color: #cccccc;
}

.story-image img {
  width: 100%;
  border-radius: 20px;
  box-shadow: 0 20px 40px rgba(0,0,0,0.3);
}

/* Values Section */
.values-section {
  padding: 6rem 0;
  background: #0a0a0a;
}

.section-title {
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 4rem;
  color: #48dbfb;
}

.values-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
}

.value-card {
  background: linear-gradient(135deg, #1a1a1a 0%, #2a2a2a 100%);
  padding: 2.5rem;
  border-radius: 20px;
  text-align: center;
  border: 1px solid #333333;
  transition: all 0.3s ease;
}

.value-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 20px 40px rgba(72,219,251,0.2);
  border-color: #48dbfb;
}

.value-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.value-card h3 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: #ffffff;
}

.value-card p {
  color: #cccccc;
  line-height: 1.6;
}

/* Team Section */
.team-section {
  padding: 6rem 0;
  background: #111111;
}

.team-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 2rem;
}

.team-member {
  background: linear-gradient(135deg, #1a1a1a 0%, #2a2a2a 100%);
  border-radius: 20px;
  overflow: hidden;
  border: 1px solid #333333;
  transition: all 0.3s ease;
}

.team-member:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(72,219,251,0.2);
}

.member-image {
  height: 250px;
  overflow: hidden;
}

.member-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.team-member:hover .member-image img {
  transform: scale(1.1);
}

.member-info {
  padding: 2rem;
}

.member-info h3 {
  font-size: 1.5rem;
  margin-bottom: 0.5rem;
  color: #ffffff;
}

.member-role {
  color: #feca57;
  font-weight: 600;
  margin-bottom: 1rem;
}

.member-desc {
  color: #cccccc;
  line-height: 1.6;
}

/* Facilities Section */
.facilities-section {
  padding: 6rem 0;
  background: #0a0a0a;
}

.facilities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 3rem;
}

.facility-card {
  background: linear-gradient(135deg, #1a1a1a 0%, #2a2a2a 100%);
  border-radius: 20px;
  overflow: hidden;
  border: 1px solid #333333;
  transition: all 0.3s ease;
}

.facility-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(72,219,251,0.2);
}

.facility-card img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.facility-content {
  padding: 2rem;
}

.facility-content h3 {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: #ffffff;
}

.facility-content p {
  color: #cccccc;
  line-height: 1.6;
}

/* Awards Section */
.awards-section {
  padding: 6rem 0;
  background: #111111;
}

.awards-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.award-item {
  background: linear-gradient(135deg, #1a1a1a 0%, #2a2a2a 100%);
  padding: 2.5rem;
  border-radius: 20px;
  text-align: center;
  border: 1px solid #333333;
  transition: all 0.3s ease;
}

.award-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 20px 40px rgba(72,219,251,0.2);
  border-color: #48dbfb;
}

.award-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.award-item h3 {
  font-size: 1.3rem;
  margin-bottom: 1rem;
  color: #ffffff;
}

.award-item p {
  color: #cccccc;
  line-height: 1.6;
}

/* CTA Section */
.cta-section {
  padding: 6rem 0;
  background: linear-gradient(135deg, #48dbfb 0%, #feca57 100%);
  text-align: center;
}

.cta-content h2 {
  font-size: 2.5rem;
  margin-bottom: 1rem;
  color: #0a0a0a;
}

.cta-content p {
  font-size: 1.2rem;
  margin-bottom: 2rem;
  color: #0a0a0a;
}

.cta-button {
  display: inline-block;
  background: #0a0a0a;
  color: #ffffff;
  padding: 1rem 2.5rem;
  border-radius: 50px;
  text-decoration: none;
  font-weight: 600;
  font-size: 1.1rem;
  transition: all 0.3s ease;
  border: 2px solid #0a0a0a;
}

.cta-button:hover {
  background: transparent;
  color: #0a0a0a;
  transform: translateY(-2px);
}

/* Responsive Design */
@media (max-width: 768px) {
  .hero-title {
    font-size: 3rem;
  }
  
  .hero-subtitle {
    font-size: 1.2rem;
  }
  
  .hero-stats {
    flex-direction: column;
    gap: 2rem;
  }
  
  .story-content {
    grid-template-columns: 1fr;
    gap: 2rem;
  }
  
  .story-text h2 {
    font-size: 2rem;
  }
  
  .section-title {
    font-size: 2rem;
  }
  
  .team-grid {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .facilities-grid {
    grid-template-columns: 1fr;
  }
  
  .values-grid,
  .awards-grid {
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  }
  
  .container {
    padding: 0 15px;
  }
}

@media (max-width: 480px) {
  .hero-title {
    font-size: 2.5rem;
  }
  
  .hero-subtitle {
    font-size: 1rem;
  }
  
  .stat-number {
    font-size: 2rem;
  }
  
  .value-card,
  .award-item {
    padding: 2rem;
  }
  
  .cta-content h2 {
    font-size: 2rem;
  }
}
</style> 