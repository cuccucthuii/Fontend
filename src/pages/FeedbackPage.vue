<template>
  <div class="feedback-page">
    <div class="feedback-banner">
      <div class="feedback-banner-icon">⭐</div>
      <div class="feedback-banner-title">Đánh giá trải nghiệm của bạn</div>
    </div>
    <div class="feedback-content">
      <form v-if="!submitted" class="feedback-form" @submit.prevent="submitFeedback">
        <div v-if="name || email" class="feedback-user-info">
          <span v-if="name">👤 {{ name }}</span>
          <span v-if="email">📧 {{ email }}</span>
        </div>
        <div class="feedback-stars">
          <span v-for="star in 5" :key="star" class="star" :class="{active: star <= rating}" @click="setRating(star)">
            {{ star <= rating ? '★' : '☆' }}
          </span>
        </div>
        <textarea v-model="comment" placeholder="Nhận xét của bạn (không bắt buộc)" rows="4"></textarea>
        <button type="submit" :disabled="rating === 0 || loading">{{ loading ? 'Đang gửi...' : 'Gửi đánh giá' }}</button>
        <div v-if="successMsg" class="feedback-success">{{ successMsg }}</div>
        <div v-if="errorMsg" class="feedback-error">{{ errorMsg }}</div>
      </form>
      <div v-else class="feedback-thankyou">
        <div class="thankyou-icon">🎉</div>
        <div class="thankyou-title">Cảm ơn bạn đã đánh giá!</div>
        <div class="thankyou-desc">Ý kiến của bạn giúp DEV CINEMA ngày càng hoàn thiện hơn.</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import emailjs from 'emailjs-com'

const rating = ref(0)
const comment = ref('')
const loading = ref(false)
const submitted = ref(false)
const name = ref('')
const email = ref('')
const successMsg = ref('')
const errorMsg = ref('')

const route = useRoute()
onMounted(() => {
  name.value = route.query.name || ''
  email.value = route.query.email || ''
})

function setRating(star) {
  rating.value = star
}

function submitFeedback() {
  loading.value = true
  successMsg.value = ''
  errorMsg.value = ''
  emailjs.send('service_1o1drn9', 'template_6sirl0e', {
    name: name.value,
    email: email.value,
    rating: rating.value,
    comment: comment.value
  }, 'VHA_JnhQmcbYK_yDZ')
    .then(() => {
      submitted.value = true
      successMsg.value = 'Gửi đánh giá thành công! Cảm ơn bạn đã phản hồi.'
    })
    .catch(() => {
      errorMsg.value = 'Gửi đánh giá thất bại. Vui lòng thử lại.'
    })
    .finally(() => {
      loading.value = false
    })
}
</script>

<style scoped>
.feedback-page {
  max-width: 500px;
  margin: 40px auto;
  color: #fff;
}
.feedback-banner {
  background: linear-gradient(90deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  padding: 32px 0 20px 0;
  text-align: center;
  border-radius: 18px 18px 0 0;
  box-shadow: 0 4px 24px rgba(72,219,251,0.10);
  margin-bottom: 24px;
}
.feedback-banner-icon {
  font-size: 44px;
  margin-bottom: 8px;
}
.feedback-banner-title {
  font-size: 2em;
  font-weight: 900;
  letter-spacing: 2px;
  text-shadow: 0 2px 12px #232526;
}
.feedback-content {
  background: #232526;
  border-radius: 0 0 18px 18px;
  box-shadow: 0 2px 16px rgba(44,62,80,0.18);
  padding: 32px 28px;
  min-height: 220px;
}
.feedback-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
  align-items: center;
}
.feedback-stars {
  font-size: 2.2em;
  margin-bottom: 8px;
  user-select: none;
}
.star {
  cursor: pointer;
  color: #feca57;
  transition: transform 0.15s;
}
.star.active {
  transform: scale(1.2);
  color: #ffd700;
}
.feedback-form textarea {
  width: 100%;
  background: #18191a;
  color: #fff;
  border: 1.5px solid #48dbfb;
  border-radius: 8px;
  padding: 12px 14px;
  font-size: 15px;
  outline: none;
  transition: border-color 0.2s;
}
.feedback-form textarea:focus {
  border-color: #feca57;
}
.feedback-form button {
  background: linear-gradient(90deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 12px 0;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(72,219,251,0.15);
  transition: background 0.2s;
  width: 100%;
}
.feedback-form button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}
.feedback-form button:hover:not(:disabled) {
  background: linear-gradient(90deg, #667eea 0%, #48dbfb 100%);
}
.feedback-thankyou {
  text-align: center;
  padding: 32px 0;
}
.thankyou-icon {
  font-size: 48px;
  margin-bottom: 10px;
}
.thankyou-title {
  font-size: 1.5em;
  font-weight: 800;
  margin-bottom: 8px;
  color: #48dbfb;
}
.thankyou-desc {
  color: #feca57;
  font-size: 1.1em;
}
.feedback-user-info {
  margin-bottom: 10px;
  font-size: 1em;
  color: #feca57;
  display: flex;
  gap: 18px;
  align-items: center;
}
.feedback-success {
  color: #27ae60;
  margin-top: 12px;
  font-weight: 600;
}
.feedback-error {
  color: #d63031;
  margin-top: 12px;
  font-weight: 600;
}
</style> 