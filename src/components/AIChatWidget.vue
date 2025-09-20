<template>
  <div class="ai-chat-wrapper" :class="{ open: isOpen }">
    <button class="chat-toggle" @click="toggle">
      <img src="/logo.png?v=2" alt="Assistant" class="chat-avatar" />
      <span class="badge" v-if="!isOpen && unreadCount > 0">{{ unreadCount }}</span>
    </button>

    <!-- Greeting bubbles when closed -->
    <transition name="greet-fade">
      <div v-if="!isOpen && showGreeting1" class="greet-bubble" style="bottom: 92px; right: 12px;">
        <img src="/logo.png?v=2" class="greet-icon" alt="bee" />
        <div class="greet-text">Xin chào! Mình là trợ lý <b>Bee</b> của <b>DevCinema</b></div>
        <button class="greet-close" @click="showGreeting1 = false">×</button>
      </div>
    </transition>
    <transition name="greet-fade">
      <div v-if="!isOpen && showGreeting2" class="greet-bubble" style="bottom: 150px; right: 12px;">
        <img src="/logo.png?v=2" class="greet-icon" alt="bee" />
        <div class="greet-text">Bạn cần mình hỗ trợ gì không nè?</div>
        <button class="greet-close" @click="showGreeting2 = false">×</button>
      </div>
    </transition>

    <transition name="chat-slide">
      <div v-if="isOpen" class="chat-panel">
        <div class="chat-header">
          <div class="header-left">
            <img src="/logo.png?v=2" alt="Assistant" class="header-avatar" />
            <div class="header-info">
              <div class="title">Trợ lý Bee</div>
              <div class="subtitle">Hỗ trợ 24/7</div>
            </div>
          </div>
          <button class="btn-icon" @click="toggle">×</button>
        </div>

        <div ref="scrollArea" class="chat-body">
          <div v-for="(m, i) in messages" :key="i" :class="['msg', m.role]">
            <img v-if="m.role==='assistant'" src="/logo.png?v=2" class="msg-avatar" alt="assistant" />
            <div class="bubble" v-html="m.html"></div>
          </div>
          <div v-if="isTyping" class="msg assistant">
            <img src="/logo.png?v=2" class="msg-avatar" alt="assistant" />
            <div class="bubble">Đang soạn…</div>
          </div>
        </div>

        <form class="chat-input" @submit.prevent="send">
          <input v-model="draft" type="text" placeholder="Nhập câu hỏi của bạn..." />
          <button class="btn-send" :disabled="loading">
            <span v-if="!loading">Gửi</span>
            <span v-else class="spinner"></span>
          </button>
        </form>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue'
import { createRoom, getCustomerRooms, getRoomMessages, sendMessage as sendChatMessage, openAIStream } from '@/services/chatService'
import { API_BASE_URL } from '@/services/api'
import { isComplexQuestion, handleComplexQuestion } from '@/services/aiIntelligenceService'

const isOpen = ref(false)
const draft = ref('')
const loading = ref(false)
const unreadCount = ref(1)
const scrollArea = ref(null)
const showGreeting1 = ref(false)
const showGreeting2 = ref(false)
const isTyping = ref(false)
const roomId = ref(null)
let sse = null

const messages = ref([
  { role: 'assistant', html: 'Xin chào! Mình là trợ lý <b>Bee</b>. Mình có thể giúp gì?' }
])

function toggle() {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    unreadCount.value = 0
    nextTick(() => scrollToBottom())
    if (!roomId.value) {
      initChat()
    }
  }
}

function scrollToBottom() {
  if (scrollArea.value) {
    scrollArea.value.scrollTop = scrollArea.value.scrollHeight
  }
}

async function send() {
  const text = draft.value.trim()
  if (!text || loading.value) return
  messages.value.push({ role: 'user', html: escapeHtml(text) })
  draft.value = ''
  loading.value = true
  nextTick(() => scrollToBottom())

  try {
    // Kiểm tra câu hỏi phức tạp trước
    const complexCheck = isComplexQuestion(text)
    
    if (complexCheck.isComplex) {
      try {
        // Thử xử lý với dữ liệu thật trước
        const intelligentResponse = await handleComplexQuestion(text, complexCheck.category)
        
        if (intelligentResponse) {
          // Có dữ liệu thật, trả về response thông minh
          messages.value.push({ role: 'assistant', html: intelligentResponse })
          loading.value = false
          nextTick(() => scrollToBottom())
          return
        }
        // Không có dữ liệu thật, fallback sang AI
      } catch (error) {
        console.error('Error in intelligent response:', error)
        // Fallback sang AI nếu có lỗi
      }
    }
    
    // Xử lý câu hỏi đơn giản hoặc fallback
    const quickReply = generateQuickReply(text)
    if (quickReply && quickReply !== 'Mình đã ghi nhận câu hỏi và sẽ hỗ trợ bạn ngay.') {
      messages.value.push({ role: 'assistant', html: quickReply })
      loading.value = false
      nextTick(() => scrollToBottom())
      return
    }
    
    // Gửi lên AI backend
    if (!roomId.value) {
      await initChat()
    }
    await sendChatMessage({ roomId: roomId.value, messageContent: text, messageType: 'TEXT' })
    startAIStream(text)
  } catch (e) {
    messages.value.push({ role: 'assistant', html: 'Xin lỗi, hiện chưa phản hồi được. Vui lòng thử lại sau.' })
  } finally {
    loading.value = false
    nextTick(() => scrollToBottom())
  }
}

function escapeHtml(str) {
  return str
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
}

function generateQuickReply(text) {
  const lower = text.toLowerCase()
  
  // Câu hỏi về giá vé
  if (lower.includes('giá vé') || lower.includes('ticket') || lower.includes('bao nhiêu tiền') || lower.includes('giá bao nhiêu')) {
    return '💰 <strong>Thông tin giá vé:</strong><br>• Vé 2D: 50,000 VNĐ<br>• Vé 3D: 80,000 VNĐ<br>• Combo bắp + nước: 60,000 VNĐ<br><br>💡 Xem chi tiết tại <a href="/ticket-price" class="link">Trang giá vé</a> nhé!'
  }
  
  // Câu hỏi về lịch chiếu
  if (lower.includes('lịch chiếu') || lower.includes('showtime') || lower.includes('khi nào chiếu') || lower.includes('mấy giờ chiếu')) {
    return '⏰ <strong>Lịch chiếu phim:</strong><br>• Suất sáng: 9:00 - 12:00<br>• Suất chiều: 13:00 - 17:00<br>• Suất tối: 18:00 - 22:00<br><br>🎬 Xem lịch chiếu chi tiết tại <a href="/showtimes" class="link">Lịch chiếu</a>!'
  }
  
  // Câu hỏi về đặt vé
  if (lower.includes('đặt vé') || lower.includes('mua vé') || lower.includes('booking') || lower.includes('mua vé online')) {
    return '🎫 <strong>Đặt vé nhanh:</strong><br>1. Chọn phim và suất chiếu<br>2. Chọn ghế ngồi<br>3. Thanh toán online<br>4. Nhận vé qua email/SMS<br><br>🚀 <a href="/booking" class="link">Đặt vé ngay</a>!'
  }
  
  // Câu hỏi về rạp chiếu
  if (lower.includes('rạp') || lower.includes('cinema') || lower.includes('địa chỉ') || lower.includes('rạp nào')) {
    return '🏢 <strong>Hệ thống rạp:</strong><br>• Rạp DevCinema 1: 123 Nguyễn Huệ, Q1<br>• Rạp DevCinema 2: 456 Lê Lợi, Q3<br>• Rạp DevCinema 3: 789 Võ Văn Tần, Q1<br><br>📍 Tìm rạp gần bạn tại <a href="/" class="link">Trang chủ</a>!'
  }
  
  // Câu hỏi về rạp gần nhất
  if (lower.includes('gần tôi') || lower.includes('gần nhất') || lower.includes('nearby')) {
    return '📍 <strong>Tìm rạp gần bạn:</strong><br>Mình đang tìm rạp gần nhất...<br><br>💡 <strong>Lưu ý:</strong><br>• Cho phép truy cập vị trí để tìm chính xác<br>• Hoặc bấm nút "Gần bạn" trên <a href="/" class="link">Trang chủ</a><br>• Hoặc cho mình biết bạn ở khu vực nào nhé!'
  }
  
  // Câu hỏi về phim
  if (lower.includes('phim gì') || lower.includes('phim nào') || lower.includes('movie') || lower.includes('phim đang chiếu')) {
    return '🎬 <strong>Phim đang chiếu:</strong><br>• Nhà Bà Nữ (Tâm lý, Gia đình)<br>• Frozen 2 (Hoạt hình, Gia đình)<br>• Avengers: Endgame (Hành động, Khoa học viễn tưởng)<br><br>🎭 Xem đầy đủ tại <a href="/movies" class="link">Danh sách phim</a>!'
  }
  
  // Câu hỏi so sánh phim (fallback)
  if (lower.includes('so sánh') || lower.includes('khác nhau') || lower.includes('giống nhau')) {
    return '🔍 <strong>So sánh phim:</strong><br>Mình có thể giúp bạn so sánh các phim đang chiếu!<br><br>💡 <strong>Gợi ý:</strong><br>• Nhà Bà Nữ (Tâm lý, Gia đình)<br>• Frozen 2 (Hoạt hình, Gia đình)<br>• Avengers: Endgame (Hành động, Khoa học viễn tưởng)<br><br>🎬 Xem danh sách đầy đủ tại <a href="/movies" class="link">Trang phim</a> để chọn phim so sánh!'
  }
  
  // Câu hỏi về hỗ trợ
  if (lower.includes('giúp') || lower.includes('help') || lower.includes('hỗ trợ') || lower.includes('làm sao')) {
    return '🤖 <strong>Mình có thể giúp bạn:</strong><br>• 🎬 Tìm phim và lịch chiếu<br>• 🎫 Hướng dẫn đặt vé<br>• 🏢 Tìm rạp gần bạn<br>• 💰 Thông tin giá vé<br>• 🎭 Gợi ý phim phù hợp<br>• 📞 Liên hệ hỗ trợ'
  }
  
  // Câu hỏi về liên hệ
  if (lower.includes('liên hệ') || lower.includes('contact') || lower.includes('hotline') || lower.includes('số điện thoại')) {
    return '📞 <strong>Thông tin liên hệ:</strong><br>• Hotline: 1900-xxxx<br>• Email: support@devcinema.com<br>• Facebook: DevCinema Official<br>• Zalo: DevCinema Support<br><br>💬 Chat trực tiếp với nhân viên 24/7!'
  }
  
  // Câu hỏi về khuyến mãi
  if (lower.includes('khuyến mãi') || lower.includes('promotion') || lower.includes('giảm giá') || lower.includes('ưu đãi')) {
    return '🎉 <strong>Khuyến mãi hiện tại:</strong><br>• Giảm 20% combo bắp + nước cuối tuần<br>• Mua 2 vé tặng 1 vé thứ 4<br>• Ví MoMo hoàn tiền 10% (tối đa 30K)<br><br>💡 Xem thêm tại <a href="/promotions" class="link">Trang khuyến mãi</a>!'
  }
  
  // Câu hỏi về thời gian mở cửa
  if (lower.includes('mở cửa') || lower.includes('giờ mở') || lower.includes('thời gian') || lower.includes('khi nào mở')) {
    return '🕐 <strong>Giờ mở cửa:</strong><br>• Thứ 2 - Chủ nhật: 8:00 - 23:00<br>• Lễ, Tết: 9:00 - 22:00<br>• Suất chiếu đầu tiên: 9:00<br>• Suất chiếu cuối: 22:00<br><br>🎬 Rạp mở cửa sớm để phục vụ khách hàng!'
  }
  
  // Câu hỏi về dịch vụ
  if (lower.includes('dịch vụ') || lower.includes('service') || lower.includes('có gì') || lower.includes('tiện ích')) {
    return '🌟 <strong>Dịch vụ tại rạp:</strong><br>• 🍿 Bắp rang, nước uống<br>• 🎮 Khu vui chơi trẻ em<br>• 🅿️ Bãi đỗ xe miễn phí<br>• ♿ Thang máy, lối đi cho người khuyết tật<br>• 📱 WiFi miễn phí<br>• 🎁 Quầy bán đồ lưu niệm'
  }
  
  return 'Mình đã ghi nhận câu hỏi và sẽ hỗ trợ bạn ngay.'
}

onMounted(() => {
  // Luôn hiển thị chào một lần sau khi tải trang (mỗi lần reload)
  if (!isOpen.value) {
    unreadCount.value = 1
    // Hiện bóng 1 trước
    showGreeting1.value = true
    // Sau 2.5s: ẩn bóng 1 rồi hiện bóng 2
    setTimeout(() => {
      showGreeting1.value = false
      showGreeting2.value = true
    }, 2500)
    // Ẩn bóng 2 sau thêm 3s
    setTimeout(() => { showGreeting2.value = false }, 5500)
  }
})

function getCustomerId() {
  try {
    const raw = localStorage.getItem('userInfo')
    if (!raw) return null
    const obj = JSON.parse(raw)
    return obj?.id || obj?.user?.id || obj?.userId || obj?.customerId || null
  } catch (_) { return null }
}

async function initChat() {
  const customerId = getCustomerId()
  if (!customerId) {
    return
  }
  try {
    const res = await createRoom({ customerId, roomName: 'Hỗ trợ khách hàng' })
    roomId.value = res?.data?.roomId || res?.data?.id || res?.data
  } catch (err) {
    try {
      const list = await getCustomerRooms(customerId)
      const room = Array.isArray(list?.data) ? list.data[0] : list?.data?.[0] || list?.data
      roomId.value = room?.roomId || room?.id || room
    } catch (_) {}
  }
  if (roomId.value) {
    await loadHistory()
  }
}

async function loadHistory() {
  try {
    const res = await getRoomMessages(roomId.value, { limit: 30 })
    const items = Array.isArray(res?.data) ? res.data : res?.data?.content || []
    messages.value = items.map(m => ({
      role: m?.senderRole === 'USER' || m?.sender === 'USER' || m?.role === 'user' ? 'user' : 'assistant',
      html: escapeHtml(m?.messageContent || m?.content || '')
    }))
    if (messages.value.length === 0) {
      messages.value.push({ role: 'assistant', html: 'Xin chào! Mình là trợ lý <b>Bee</b> của <b>DevCinema</b>. Bạn muốn xem phim nào hôm nay?' })
    }
    nextTick(() => scrollToBottom())
  } catch (_) {}
}

function startAIStream(userText) {
  const customerId = getCustomerId()
  if (!roomId.value || !customerId) {
    const reply = generateQuickReply(userText)
    messages.value.push({ role: 'assistant', html: reply })
    return
  }
  if (sse) {
    try { sse.close() } catch (_) {}
    sse = null
  }
  try {
    sse = openAIStream({ roomId: roomId.value, messageContent: userText, customerId })
    isTyping.value = true
    sse.addEventListener('typing', () => {
      isTyping.value = true
    })
    sse.addEventListener('message', (evt) => {
      const data = evt?.data || ''
      const text = safeParseMessageData(data)
      const html = text ? escapeHtml(text) : 'Mình đã ghi nhận câu hỏi và sẽ hỗ trợ bạn ngay.'
      messages.value.push({ role: 'assistant', html })
      isTyping.value = false
      try { sse.close() } catch (_) {}
      sse = null
      nextTick(() => scrollToBottom())
    })
    sse.onerror = () => {
      isTyping.value = false
      try { sse.close() } catch (_) {}
      sse = null
    }
  } catch (_) {
    isTyping.value = false
  }
}

function safeParseMessageData(data) {
  try {
    const obj = JSON.parse(data)
    return obj?.content || obj?.message || data
  } catch (_) {
    return data
  }
}
</script>

<style scoped>
.ai-chat-wrapper {
  position: fixed;
  right: 20px;
  bottom: 20px;
  z-index: 4000;
}

.chat-toggle {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
  box-shadow: 0 8px 24px rgba(0,0,0,.2);
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  position: relative;
}

.chat-avatar { width: 32px; height: 32px; border-radius: 50%; object-fit: cover; }
.badge { position: absolute; top: -6px; right: -6px; background: #e50914; color: #fff; width: 20px; height: 20px; border-radius: 50%; font-size: 12px; display: flex; align-items: center; justify-content: center; font-weight: 700; }

.chat-panel {
  width: 320px;
  height: 420px;
  background: #1e1f22;
  color: #fff;
  border-radius: 16px;
  box-shadow: 0 16px 48px rgba(0,0,0,.35);
  overflow: hidden;
}

.chat-header {
  height: 60px;
  padding: 10px 12px;
  background: linear-gradient(135deg, #ff9a9e 0%, #fad0c4 100%);
  color: #2d3436;
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.header-left { display: flex; align-items: center; gap: 10px; }
.header-avatar { width: 36px; height: 36px; border-radius: 50%; object-fit: cover; }
.header-info .title { font-weight: 900; font-size: 14px; }
.header-info .subtitle { font-size: 12px; opacity: .8; }
.btn-icon { background: transparent; border: none; font-size: 24px; cursor: pointer; color: #2d3436; }

.chat-body {
  height: 300px;
  padding: 12px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.msg { display: flex; gap: 8px; }
.msg.user { justify-content: flex-end; }
.msg .bubble { max-width: 75%; padding: 10px 12px; border-radius: 14px; line-height: 1.35; font-size: 14px; }
.msg.assistant .bubble { 
  background: #2b2d31; 
  border: 1px solid #ffffff22; 
  word-wrap: break-word;
  overflow-wrap: break-word;
}
.msg.user .bubble { background: #5c7cfa; color: #fff; }
.msg-avatar { width: 24px; height: 24px; border-radius: 50%; align-self: flex-end; }
.link { 
  color: #ffd166; 
  text-decoration: underline; 
  font-weight: 600;
  transition: color 0.2s ease;
}
.link:hover { color: #ffed4e; }

.chat-input { height: 60px; display: flex; gap: 8px; padding: 8px; background: #1a1b1e; border-top: 1px solid #ffffff11; }
.chat-input input { flex: 1; background: #2b2d31; border: 1px solid #ffffff22; color: #fff; border-radius: 10px; padding: 10px 12px; outline: none; }
.btn-send { width: 80px; border: none; background: #ff758c; color: #fff; border-radius: 10px; font-weight: 800; cursor: pointer; }
.btn-send:disabled { opacity: .6; cursor: not-allowed; }
.spinner { width: 18px; height: 18px; border: 2px solid #fff; border-right-color: transparent; border-radius: 50%; display: inline-block; animation: spin .8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }

.chat-slide-enter-from { opacity: 0; transform: translateY(12px); }
.chat-slide-enter-active, .chat-slide-leave-active { transition: all .18s ease; }
.chat-slide-leave-to { opacity: 0; transform: translateY(12px); }
.greet-bubble {
  position: fixed;
  max-width: 240px;
  background: linear-gradient(135deg, #ffd6d8 0%, #ffefef 100%);
  color: #2d3436;
  padding: 10px 32px 10px 40px;
  border-radius: 14px;
  box-shadow: 0 8px 24px rgba(0,0,0,.15);
  z-index: 1001;
}
.greet-icon {
  position: absolute; left: 8px; top: 8px; width: 24px; height: 24px; border-radius: 50%; object-fit: cover;
}
.greet-text { font-size: 13px; font-weight: 600; }
.greet-close { position: absolute; top: 2px; right: 6px; border: none; background: transparent; font-size: 16px; color: #555; cursor: pointer; }
.greet-fade-enter-from, .greet-fade-leave-to { opacity: 0; transform: translateY(6px); }
.greet-fade-enter-active, .greet-fade-leave-active { transition: all .18s ease; }

</style>


