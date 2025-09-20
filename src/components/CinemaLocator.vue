<template>
  <section class="cinema-locator">
    <div class="locator-card">
      <div class="locator-header">
        <h3>Rạp chiếu</h3>
        <div class="controls">
          <select v-model="selectedCity">
            <option value="">Tất cả tỉnh/thành</option>
            <option v-for="c in cities" :key="c" :value="c">{{ c }}</option>
          </select>
          <div class="search-box">
            <input v-model="keyword" type="text" placeholder="Tìm theo tên rạp..." />
          </div>
          <button class="btn-nearby" @click="locateMe">Gần bạn</button>
        </div>
        <div class="status-row" v-if="statusMsg">
          <div class="status-card">
            <div class="status-icon">📍</div>
            <span class="status-text">{{ statusMsg }}</span>
          </div>
        </div>
      </div>

      <div class="locator-body" v-if="!loading">
        <ul class="cinema-list">
          <li v-for="b in pagedBranches" :key="b.id || b.idRap || b.maRap" class="cinema-item">
            <div class="logo-wrap"><img src="/logo.png?v=2" alt="logo" /></div>
            <div class="cinema-info">
              <div class="name" :title="b.diaChi || b.address || ''">{{ b.tenRapChieu || b.tenRap || b.ten || b.name }}</div>
              <div class="address-row">
                <a v-if="b.diaChi || b.address || parseLatLng(b)" class="address-text link" href="#" @click.prevent="openInMaps(b)">
                  {{ (b.diaChi || b.address) ? (b.diaChi || b.address) : 'Mở bản đồ' }}
                </a>
                <button v-if="b.diaChi || b.address" class="btn-copy" @click="copyAddress(b)">Sao chép</button>
              </div>
            </div>
            <div v-if="b.__distanceKm != null" class="distance">{{ b.__distanceKm.toFixed(1) }} km</div>
            <button class="btn-link" @click="goToShowtimes(b)">Xem lịch</button>
          </li>
        </ul>
        <div v-if="displayBranches.length === 0" class="empty">Không tìm thấy rạp phù hợp.</div>

        <div v-if="totalPages > 1" class="pagination">
          <button class="page-btn" :disabled="currentPage === 1" @click="currentPage--">Trước</button>
          <span class="page-info">Trang {{ currentPage }} / {{ totalPages }}</span>
          <button class="page-btn" :disabled="currentPage === totalPages" @click="currentPage++">Sau</button>
        </div>
      </div>
      <div class="loading" v-else>
        <div class="spinner"></div>
        Đang tải rạp chiếu...
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { fetchBranches, getNearbyBranches } from '@/services/branchService'

const router = useRouter()
const branches = ref([])
const loading = ref(true)
const selectedCity = ref('')
const keyword = ref('')
const statusMsg = ref('')
const currentPage = ref(1)
const pageSize = ref(10)

const cities = computed(() => {
  const names = new Set()
  branches.value.forEach(b => {
    const city = b.thanhPho || b.tinhThanh || b.city || extractCityFromAddress(b.diaChi || b.address)
    if (city) names.add(city)
  })
  return Array.from(names).sort()
})

const filteredBranches = computed(() => {
  const key = keyword.value.trim().toLowerCase()
  return branches.value.filter(b => {
    const name = (b.tenRap || b.ten || b.name || '').toLowerCase()
    const city = (b.thanhPho || b.tinhThanh || b.city || extractCityFromAddress(b.diaChi || b.address) || '').toLowerCase()
    const okCity = !selectedCity.value || city === selectedCity.value.toLowerCase()
    const okName = !key || name.includes(key)
    return okCity && okName
  })
})

const displayBranches = computed(() => filteredBranches.value.slice().sort((a, b) => {
  // Nếu có khoảng cách, sắp xếp theo khoảng cách, nếu không giữ nguyên
  const da = a.__distanceKm ?? Number.POSITIVE_INFINITY
  const db = b.__distanceKm ?? Number.POSITIVE_INFINITY
  return da - db
}))

const totalPages = computed(() => Math.max(1, Math.ceil(displayBranches.value.length / pageSize.value)))
const pagedBranches = computed(() => {
  if (currentPage.value > totalPages.value) currentPage.value = totalPages.value
  const start = (currentPage.value - 1) * pageSize.value
  return displayBranches.value.slice(start, start + pageSize.value)
})

function extractCityFromAddress(addr) {
  if (!addr) return ''
  // Lấy phần cuối cùng sau dấu phẩy làm gợi ý thành phố
  const parts = addr.split(',').map(s => s.trim())
  return parts[parts.length - 1] || ''
}

async function load() {
  loading.value = true
  try {
    const res = await fetchBranches()
    const list = Array.isArray(res?.data) ? res.data : (res?.data?.content || [])
    // Dùng dữ liệu tọa độ thật từ BE, không còn chèn tọa độ mẫu
    branches.value = list
  } catch (e) {
    branches.value = []
  } finally {
    loading.value = false
  }
}

function goToShowtimes(branch) {
  router.push({ path: '/showtimes', query: { branchId: branch.id || branch.idRap || branch.maRap } })
}

async function copyAddress(branch) {
  const text = branch.diaChi || branch.address || ''
  if (!text) return
  try {
    await navigator.clipboard.writeText(text)
  } catch (_) {}
}

function mapsUrlForBranch(branch) {
  const ll = parseLatLng(branch)
  if (ll) {
    return `https://www.google.com/maps?q=${ll.lat},${ll.lng}`
  }
  const query = encodeURIComponent(branch.diaChi || branch.address || (branch.tenRapChieu || branch.tenRap || branch.ten || branch.name || ''))
  return `https://www.google.com/maps/search/?api=1&query=${query}`
}

function openInMaps(branch) {
  const url = mapsUrlForBranch(branch)
  window.open(url, '_blank')
}


function haversineKm(lat1, lon1, lat2, lon2) {
  const toRad = (v) => (v * Math.PI) / 180
  const R = 6371
  const dLat = toRad(lat2 - lat1)
  const dLon = toRad(lon2 - lon1)
  const a = Math.sin(dLat/2)**2 + Math.cos(toRad(lat1))*Math.cos(toRad(lat2))*Math.sin(dLon/2)**2
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
  return R * c
}

function parseLatLng(branch) {
  // Ưu tiên trường lat/lng, fallback cố thử parse từ địa chỉ (không đảm bảo)
  const lat = branch.lat || branch.latitude
  const lng = branch.lng || branch.longitude
  if (typeof lat === 'number' && typeof lng === 'number') return { lat, lng }
  
  // Fallback: thử parse từ địa chỉ (rất cơ bản, không chính xác)
  const address = branch.diaChi || branch.address || ''
  if (address) {
    // Tìm pattern tọa độ trong địa chỉ (ví dụ: "10.762622, 106.660172")
    const coordMatch = address.match(/(\d+\.?\d*),\s*(\d+\.?\d*)/)
    if (coordMatch) {
      const lat = parseFloat(coordMatch[1])
      const lng = parseFloat(coordMatch[2])
      if (!isNaN(lat) && !isNaN(lng) && lat >= -90 && lat <= 90 && lng >= -180 && lng <= 180) {
        return { lat, lng }
      }
    }
  }
  
  return null
}

function locateMe() {
  if (!navigator.geolocation) { statusMsg.value = 'Thiết bị không hỗ trợ định vị.'; return }
  statusMsg.value = 'Đang lấy vị trí của bạn...'
  navigator.geolocation.getCurrentPosition(async (pos) => {
    const { latitude, longitude } = pos.coords
    statusMsg.value = 'Đang tìm rạp gần bạn...'
    
    // Thử gọi BE để lấy danh sách đã sắp xếp theo khoảng cách
    try {
      const res = await getNearbyBranches({ lat: latitude, lng: longitude, radiusKm: 25, limit: 50 })
      const list = Array.isArray(res?.data) ? res.data : (res?.data?.content || [])
      statusMsg.value = list.length ? `Tìm thấy ${list.length} rạp gần bạn` : 'Không tìm thấy rạp trong bán kính 25km'
      // Map distanceKm từ BE sang __distanceKm để giữ logic hiển thị/sort hiện tại
      branches.value = list.map(b => ({ ...b, __distanceKm: typeof b.distanceKm === 'number' ? b.distanceKm : null }))
      if (list.length) return
    } catch (e) {
      statusMsg.value = 'Không gọi được API gần bạn, đang tính khoảng cách trên máy...'
    }

    // Fallback: tính khoảng cách phía client
    let withCoords = 0
    branches.value = branches.value.map(b => {
      const ll = parseLatLng(b)
      if (ll) {
        withCoords++
        const d = haversineKm(latitude, longitude, ll.lat, ll.lng)
        return { ...b, __distanceKm: d }
      }
      return { ...b, __distanceKm: null }
    })
    if (withCoords === 0) {
      statusMsg.value = 'Không có rạp nào có tọa độ. Vui lòng thêm latitude/longitude vào database hoặc tọa độ vào địa chỉ.'
      return
    }
    // Nếu không thấy trong 25km, tăng bán kính lên 100km để nới lỏng
    const found = branches.value.filter(b => typeof b.__distanceKm === 'number' && b.__distanceKm <= 25)
    if (!found.length) {
      const found100 = branches.value.filter(b => typeof b.__distanceKm === 'number' && b.__distanceKm <= 100)
      if (found100.length) {
        statusMsg.value = `Không có rạp trong 25km. Hiển thị ${found100.length} rạp trong 100km.`
        branches.value = found100
      } else {
        statusMsg.value = 'Không tìm thấy rạp gần bạn.'
      }
    } else {
      statusMsg.value = `Tìm thấy ${found.length} rạp trong 25km.`
      branches.value = found
    }
  }, (error) => {
    statusMsg.value = `Không lấy được vị trí: ${error.message}`
  })
}

onMounted(load)
</script>

<style scoped>
.cinema-locator {
  max-width: 1200px;
  margin: 28px auto 40px auto;
  padding: 0 16px;
}
.locator-card {
  background: #232526;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0,0,0,.25);
  overflow: hidden;
}
.locator-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px;
  background: linear-gradient(135deg, #232526 0%, #1c1c1c 100%);
  color: #fff;
}
.locator-header h3 { font-size: 18px; font-weight: 900; margin: 0; }
.controls { display: flex; gap: 12px; align-items: center; }
.controls select, .controls input {
  background: #2b2d31; color: #fff; border: 1px solid #ffffff22; border-radius: 10px; padding: 10px 12px; outline: none;
}
.btn-nearby { background: transparent; color: #ff2d83; border: 1px solid #ff2d8366; border-radius: 10px; padding: 10px 12px; cursor: pointer; }
.btn-nearby:hover { background: #ff2d8322; }
.status-row { margin-top: 12px; }
.status-card {
  display: flex;
  align-items: center;
  gap: 8px;
  background: linear-gradient(135deg, #2b2d31 0%, #1e1e1e 100%);
  border: 1px solid #48dbfb33;
  border-radius: 12px;
  padding: 12px 16px;
  box-shadow: 0 4px 12px rgba(72, 219, 251, 0.1);
}
.status-icon { font-size: 16px; }
.status-text { 
  color: #48dbfb; 
  font-size: 14px; 
  font-weight: 600;
  text-shadow: 0 1px 2px rgba(0,0,0,0.3);
}
.locator-body { padding: 8px 8px 12px 8px; }
.cinema-list { list-style: none; margin: 0; padding: 0; }
.cinema-item {
  display: flex; align-items: center; gap: 12px; padding: 12px; border-bottom: 1px solid #ffffff0f; color: #fff;
}
.cinema-item:hover { background: #2b2d3122; }
.logo-wrap img { width: 28px; height: 28px; border-radius: 6px; }
.cinema-info { flex: 1; }
.cinema-info .name { font-weight: 800; margin-bottom: 4px; color: #48dbfb; }
.cinema-info .address { font-size: 13px; color: #b2bec3; }
.distance { color: #ffd166; font-weight: 700; margin-right: 8px; }
.address-row { display:flex; gap:8px; align-items:center; }
.address-text { font-size: 13px; color: #b2bec3; flex:1; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.address-text.link { color: #81ecec; text-decoration: none; cursor: pointer; }
.address-text.link:hover { text-decoration: underline; }
.btn-copy { background: transparent; color: #48dbfb; border: 1px solid #48dbfb55; border-radius: 8px; padding: 4px 8px; cursor: pointer; }
.btn-copy:hover { background: #48dbfb22; }
.btn-link { background: transparent; color: #ffd166; border: 1px solid #ffd16666; border-radius: 10px; padding: 8px 12px; cursor: pointer; }
.btn-link:hover { background: #ffd16622; }
.loading { color: #fff; padding: 24px; display: flex; align-items: center; gap: 10px; }
.spinner { width: 18px; height: 18px; border-radius: 50%; border: 2px solid #fff; border-right-color: transparent; animation: spin .8s linear infinite; }
@keyframes spin { to { transform: rotate(360deg); } }
.empty { color: #b2bec3; padding: 16px; text-align: center; }

.pagination { display:flex; align-items:center; justify-content:center; gap:12px; padding: 10px 0 4px; }
.page-btn { background: #2b2d31; color: #fff; border: 1px solid #ffffff22; border-radius: 10px; padding: 8px 12px; cursor: pointer; }
.page-btn:disabled { opacity: .4; cursor: not-allowed; }
.page-info { color: #b2bec3; font-size: 13px; }
</style>


