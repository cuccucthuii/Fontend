<template>
  <div v-if="visible" class="modal-backdrop" @click.self="$emit('close')">
    <div class="modal-content">
      <button class="modal-close" @click="$emit('close')">×</button>
      <div v-if="showTrailer && youtubeId" class="modal-trailer-view">
        <iframe
          :src="`https://www.youtube.com/embed/${youtubeId}?autoplay=1`"
          frameborder="0"
          allow="autoplay; encrypted-media"
          allowfullscreen
        ></iframe>
        <button class="modal-back-btn" @click="showTrailer = false">
          ← Quay lại
        </button>
      </div>
      <div v-else class="modal-body">
        <div class="modal-poster">
          <img :src="getPosterUrl(movie.posterUrl)" :alt="movie.tenPhim" />
        </div>
        <div class="modal-info">
          <div class="modal-title-row">
            <h2 class="modal-title">{{ movie.tenPhim }}</h2>
            <span v-if="movie.dinhDang" class="modal-format">{{
              movie.dinhDang
            }}</span>
          </div>
          <div class="modal-meta">
            <span v-if="movie.theLoai && movie.theLoai.length">{{
              movie.theLoai.join(", ")
            }}</span>
            <span v-if="movie.quocGia"> • {{ movie.quocGia }}</span>
            <span v-if="movie.thoiLuong"> • {{ movie.thoiLuong }} phút</span>
          </div>
          <div class="modal-meta">
            <span v-if="movie.daoDien && movie.daoDien.length"
              >Đạo diễn: {{ movie.daoDien.join(", ") }}</span
            >
          </div>
          <div class="modal-meta">
            <span v-if="movie.dienVien && movie.dienVien.length"
              >Diễn viên: {{ movie.dienVien.join(", ") }}</span
            >
          </div>
          <div class="modal-meta">
            <span>Khởi chiếu: {{ movie.ngayPhatHanh || "-" }}</span>
          </div>
          <div class="modal-desc">{{ movie.moTa }}</div>
          <div class="modal-actions">
            <button
              v-if="movie.trailerUrl && youtubeId"
              class="modal-btn-trailer"
              @click="showTrailer = true"
            >
              Xem trailer
            </button>
            <button class="modal-btn-buy" @click="openCinemaModal">
              Mua vé
            </button>
          </div>
        </div>
      </div>
    </div>
    <CinemaSelectModal
      :visible="showCinemaModal"
      @close="showCinemaModal = false"
      @cinema-selected="handleCinemaSelection"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch } from "vue";
import CinemaSelectModal from "./CinemaSelectModal.vue";
import { useRouter } from "vue-router";
const props = defineProps({
  movie: { type: Object, required: true },
  visible: { type: Boolean, default: false },
});
const emit = defineEmits(["close"]);
const showTrailer = ref(false);
const showCinemaModal = ref(false);
const router = useRouter();

function openCinemaModal() {
  showCinemaModal.value = true;
}

function handleCinemaSelection(cinema) {
  showCinemaModal.value = false;
  if (!props.movie) return;

  router.push({
    name: "Booking",
    query: {
      movieId: props.movie.idPhim,
      cinemaId: cinema.id,
    },
  });
}

watch(
  () => props.visible,
  (val) => {
    if (!val) showTrailer.value = false;
  }
);

function getPosterUrl(url) {
  if (!url) return "";
  if (url.startsWith("http")) return url;
  return (
    "" + url
  );
}

const youtubeId = computed(() => {
  if (!props.movie || !props.movie.trailerUrl) return "";
  // Hỗ trợ cả dạng full URL và share URL
  const url = props.movie.trailerUrl;
  const match = url.match(/(?:v=|youtu.be\/|embed\/)([\w-]{11})/);
  return match ? match[1] : "";
});
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(20, 24, 31, 0.85);
  z-index: 9999;
  display: flex;
  align-items: center;
  justify-content: center;
  animation: fadeIn 0.3s;
}
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
.modal-content {
  background: linear-gradient(135deg, #232526 0%, #1c1c1c 100%);
  border-radius: 22px;
  box-shadow: 0 8px 48px rgba(72, 219, 251, 0.18),
    0 2px 12px rgba(44, 62, 80, 0.28);
  max-width: 900px;
  width: 95vw;
  padding: 0;
  position: relative;
  overflow: hidden;
  animation: modalPop 0.25s;
}
@keyframes modalPop {
  from {
    transform: scale(0.92);
    opacity: 0;
  }
  to {
    transform: scale(1);
    opacity: 1;
  }
}
.modal-close {
  position: absolute;
  top: 18px;
  right: 24px;
  font-size: 2.2rem;
  color: #fff;
  background: none;
  border: none;
  cursor: pointer;
  z-index: 2;
  transition: color 0.2s;
}
.modal-close:hover {
  color: #48dbfb;
}
.modal-body {
  display: flex;
  gap: 32px;
  padding: 40px 32px 32px 32px;
  align-items: flex-start;
}
.modal-poster img {
  width: 240px;
  height: 340px;
  object-fit: cover;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(72, 219, 251, 0.1);
  background: #232526;
}
.modal-info {
  flex: 1;
  color: #fff;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.modal-title-row {
  display: flex;
  align-items: center;
  gap: 12px;
}
.modal-title {
  font-size: 2.2rem;
  font-weight: 900;
  color: #48dbfb;
  margin: 0;
}
.modal-format {
  background: #232526;
  color: #fff;
  border: 2px solid #48dbfb;
  border-radius: 8px;
  padding: 2px 12px;
  font-size: 1rem;
  font-weight: 700;
}
.modal-meta {
  font-size: 1.1rem;
  color: #b2bec3;
  margin-bottom: 0;
}
.modal-desc {
  font-size: 1.1rem;
  color: #feca57;
  margin: 12px 0 0 0;
  line-height: 1.5;
}
.modal-actions {
  margin-top: 18px;
}
.modal-btn-trailer {
  display: inline-block;
  background: none;
  border: 2px solid #48dbfb;
  color: #48dbfb;
  border-radius: 10px;
  padding: 10px 28px;
  font-size: 1.1rem;
  font-weight: 700;
  cursor: pointer;
  text-decoration: none;
  transition: background 0.2s, color 0.2s;
}
.modal-btn-trailer:hover {
  background: #48dbfb;
  color: #232526;
}

.modal-btn-buy {
  display: inline-block;
  background: #48dbfb;
  border: 2px solid #48dbfb;
  color: #232526;
  border-radius: 10px;
  padding: 10px 28px;
  font-size: 1.1rem;
  font-weight: 700;
  cursor: pointer;
  text-decoration: none;
  transition: background 0.2s, color 0.2s;
  margin-left: 10px;
}

.modal-btn-buy:hover {
  background: none;
  color: #48dbfb;
}
.modal-trailer-view {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 32px 0 24px 0;
  min-width: 350px;
  min-height: 350px;
}
.modal-trailer-view iframe {
  width: 720px;
  height: 405px;
  max-width: 90vw;
  max-height: 60vh;
  border-radius: 16px;
  box-shadow: 0 4px 24px rgba(72, 219, 251, 0.1);
  background: #232526;
  margin-bottom: 18px;
}
.modal-back-btn {
  background: none;
  border: 2px solid #48dbfb;
  color: #48dbfb;
  border-radius: 10px;
  padding: 8px 22px;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: background 0.2s, color 0.2s;
}
.modal-back-btn:hover {
  background: #48dbfb;
  color: #232526;
}
@media (max-width: 900px) {
  .modal-body {
    flex-direction: column;
    align-items: center;
    gap: 18px;
    padding: 24px 8px 18px 8px;
  }
  .modal-poster img {
    width: 180px;
    height: 260px;
  }
  .modal-title {
    font-size: 1.3rem;
  }
  .modal-trailer-view iframe {
    width: 98vw;
    height: 38vw;
    min-width: 220px;
    min-height: 120px;
  }
}
</style>
