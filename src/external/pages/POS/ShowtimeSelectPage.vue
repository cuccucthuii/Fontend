<template>
    <div class="showtime-page">
      <!-- Header -->
      <div class="page-header">
        <div class="header-content">
          <h1 class="page-title">
            <span class="header-icon">🕒</span>
            Chọn Suất Chiếu Cho Khách
          </h1>
          <p class="page-subtitle">
            Tìm kiếm và chọn suất chiếu để tiếp tục bán vé tại quầy
          </p>
        </div>
      </div>
  
      <!-- Filters -->
      <div class="filters-section">
        <div class="filter-group">
          <label class="filter-label">
            🔍 Tìm phim / phòng
          </label>
          <input v-model="searchQuery" class="filter-input" placeholder="Nhập tên phim hoặc phòng..." />
        </div>
        <div class="filter-group">
          <label class="filter-label">
            📅 Ngày chiếu
          </label>
          <input type="date" v-model="selectedDate" class="filter-input" />
        </div>
        <div class="filter-actions">
          <button class="btn btn-clear-filters" @click="clearFilters" v-if="hasActiveFilters">
            🔄 Xóa bộ lọc
          </button>
        </div>
      </div>
  
      <!-- Table -->
      <div class="table-container">
        <table v-if="filteredSchedules.length">
          <thead>
            <tr>
              <th>Ngày</th>
              <th>Giờ</th>
              <th>Phim</th>
              <th>Phòng</th>
              <th></th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="sch in filteredSchedules" :key="sch.idSuatChieu">
              <td>{{ formatDate(sch.ngayChieu) }}</td>
              <td>{{ sch.gioChieu }}</td>
              <td>{{ sch.tenPhim }}</td>
              <td>{{ sch.tenPhongChieu }}</td>
              <td>
                <button class="btn btn-primary" @click="selectSchedule(sch)">➕ Chọn suất</button>
              </td>
            </tr>
          </tbody>
        </table>
        <p v-else class="loading-state">
          Không có suất chiếu phù hợp.
        </p>
      </div>
    </div>
  </template>
  
  <script setup>
  import { ref, computed, onMounted } from 'vue'
  import { useRouter } from 'vue-router'
  import { fetchSchedules } from '../../services/scheduleService'
  
  const router = useRouter()
  const schedules = ref([])
  const searchQuery = ref('')
  const selectedDate = ref('')
  
  onMounted(async () => {
    try {
      const res = await fetchSchedules()
      if (Array.isArray(res.data)) {
        schedules.value = res.data
      } else if (Array.isArray(res.data.content)) {
        schedules.value = res.data.content
      }
    } catch (e) {
      console.error('❌ Error loading schedules:', e)
    }
  })
  
  const filteredSchedules = computed(() => {
    return schedules.value.filter(s => {
      const q = searchQuery.value.toLowerCase()
      const matchesText =
        (s.tenPhim || '').toLowerCase().includes(q) ||
        (s.tenPhongChieu || '').toLowerCase().includes(q)
      const matchesDate = !selectedDate.value || s.ngayChieu === selectedDate.value
      return matchesText && matchesDate
    })
  })
  
  const hasActiveFilters = computed(() => searchQuery.value || selectedDate.value)
  
  function clearFilters() {
    searchQuery.value = ''
    selectedDate.value = ''
  }
  
  function selectSchedule(sch) {
  console.log('🟢 Clicked schedule:', sch);
  console.log('🟢 Using idSuatChieu:', sch.idSuatChieu);
  router.push({
    name: 'POSSeats',
    params: { scheduleId: sch.idSuatChieu }
  });
}
//hung109-117
  
  function formatDate(dateStr) {
    if (!dateStr) return ''
    const [y, m, d] = dateStr.split('-')
    return `${d}/${m}/${y}`
  }

  </script>
  
  <style scoped>
  .showtime-page {
    font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    min-height: 100vh;
    padding: 32px;
  }
  
  .page-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 32px;
    background: white;
    padding: 24px 32px;
    border-radius: 16px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  }
  
  .header-content {
    flex: 1;
  }
  
  .page-title {
    display: flex;
    align-items: center;
    gap: 12px;
    margin: 0 0 8px 0;
    font-size: 28px;
    font-weight: 700;
    color: #2c3e50;
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
  }
  
  .header-icon {
    font-size: 32px;
  }
  
  .page-subtitle {
    margin: 0;
    color: #7f8c8d;
    font-size: 16px;
    font-weight: 400;
  }
  
  .filters-section {
    display: flex;
    gap: 1.5rem;
    align-items: end;
    flex-wrap: wrap;
    background: linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%);
    padding: 1.5rem;
    border-radius: 1rem;
    border: 1px solid #e2e8f0;
    box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
    margin-bottom: 1.5rem;
  }
  
  .filter-group {
    display: flex;
    flex-direction: column;
    gap: 0.75rem;
    min-width: 200px;
    flex: 1;
  }
  
  .filter-label {
    font-size: 0.875rem;
    font-weight: 700;
    color: #1e293b;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    margin-bottom: 0.25rem;
    display: flex;
    align-items: center;
    gap: 0.5rem;
  }
  
  .filter-input {
    width: 100%;
    padding: 0.875rem 1rem;
    border: 2px solid #e2e8f0;
    border-radius: 0.75rem;
    font-size: 0.875rem;
    background: white;
    color: #374151;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }
  
  .filter-input:focus {
    outline: none;
    border-color: #3b82f6;
    box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.1);
    transform: translateY(-1px);
  }
  
  .filter-actions {
    display: flex;
    gap: 1rem;
    align-items: end;
    flex-wrap: wrap;
  }
  
  .table-container {
    width: 100%;
    overflow-x: auto;
  }
  
  table {
    width: 100%;
    min-width: 800px;
    border-collapse: collapse;
    background-color: #fff;
    table-layout: auto;
  }
  
  th, td {
    border: 1px solid #ddd;
    padding: 10px 14px;
    text-align: left;
    color: #333;
    white-space: nowrap;
  }
  
  th {
    background-color: #f5f5f5;
    font-size: 13px;
    text-transform: uppercase;
    letter-spacing: 0.5px;
    font-weight: 600;
  }
  
  tr:nth-child(even) {
    background-color: #f9f9f9;
  }
  
  .loading-state {
    text-align: center;
    padding: 60px 20px;
    color: #7f8c8d;
  }
  
  .btn {
    display: inline-flex;
    align-items: center;
    gap: 8px;
    padding: 10px 16px;
    border: none;
    border-radius: 12px;
    font-size: 14px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s ease;
    background: white;
  }
  
  .btn-primary {
    background: linear-gradient(135deg, #10b981 0%, #059669 100%);
    color: white;
    box-shadow: 0 4px 15px rgba(16, 185, 129, 0.4);
  }
  
  .btn-clear-filters {
    background: linear-gradient(135deg, #6c757d 0%, #495057 100%);
    color: white;
    box-shadow: 0 4px 15px rgba(108, 117, 125, 0.4);
  }
  </style>
  