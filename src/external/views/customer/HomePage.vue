<template>
  <div class="homepage dark-mode">
    <nav class="main-header-bar">
      <!-- <ul class="main-header-menu">
        <li><a href="/lich-chieu">Lịch chiếu</a></li>
        <li><a href="/khuyen-mai">Khuyến mãi</a></li>
        <li><a href="/gia-ve">Giá vé</a></li>
        <li><a href="/gioi-thieu">Giới thiệu</a></li>
      </ul> -->
    </nav>
    <Header />
    <!-- Banner lớn với slider phim hot -->
    <section class="hero-banner dark">
      <div class="banner-slider">
        <transition-group name="fade" tag="div">
          <div
            v-for="(movie, idx) in hotMovies"
            :key="movie.id"
            v-show="idx === bannerIndex"
            class="banner-slide active"
          >
            <img :src="movie.poster" class="banner-img" />
            <div class="banner-overlay-gradient"></div>
            <div class="banner-content-modern">
              <h1 class="banner-title-modern">{{ movie.title }}</h1>
              <p class="banner-slogan-modern">{{ movie.genre }}</p>
            </div>
          </div>
        </transition-group>
        <button class="banner-arrow left" @click="prevBanner">‹</button>
        <button class="banner-arrow right" @click="nextBanner">›</button>
      </div>
      <!-- Dãy poster nhỏ có reflection CHÈN VÀO BANNER -->
      <transition-group
        name="thumb-fade-slide"
        tag="div"
        class="thumb-carousel"
      >
        <div
          v-for="(movie, idx) in hotMovies"
          :key="movie.id"
          class="thumb-poster-reflect"
          :class="{ active: idx === bannerIndex }"
          @mouseenter="hoveredMovie = idx"
          @mouseleave="hoveredMovie = null"
          style="position: relative"
          v-show="Math.abs(idx - bannerIndex) <= 2"
        >
          <img :src="movie.poster" class="thumb-img" />
          <img
            :src="movie.poster"
            class="thumb-reflection"
            aria-hidden="true"
          />
        </div>
      </transition-group>
    </section>
    <!-- Tabs phim lớn -->
    <section class="movie-section dark">
      <div class="movie-tabs-large">
        <button
          v-for="t in movieTabs"
          :key="t.key"
          :class="['movie-tab-large', { active: tab === t.key }]"
          @click="tab = t.key"
        >
          {{ t.label }}
        </button>
      </div>
      <div v-if="loadingMovies" class="movie-loading">Đang tải phim...</div>
      <div v-else-if="errorMovies" class="movie-error">{{ errorMovies }}</div>
      <div v-else class="movie-grid-mockup">
        <div
          v-for="movie in moviesToShow"
          :key="movie.idPhim || movie.id"
          class="movie-card-mockup dark"
        >
          <div
            class="movie-poster-wrap-mockup"
            @click="goToMovieDetail(movie)"
            style="cursor: pointer"
          >
            <img
              :src="getPosterUrl(movie.posterUrl)"
              :alt="movie.tenPhim"
              class="movie-poster-mockup"
              @error="handleImageError($event, 'poster')"
            />
            <div
              class="badge-age-mockup"
              :style="{ background: getAgeBadge(movie).color }"
            >
              {{ getAgeBadge(movie).text }}
            </div>
            <div v-if="movie.isHot" class="badge-hot-mockup">HOT</div>
          </div>
          <div class="movie-info-mockup">
            <div class="movie-title-mockup">{{ movie.tenPhim }}</div>
            <div class="movie-meta-mockup">
              Thể loại: {{ movie.theLoai ? movie.theLoai.join(", ") : "-" }}
            </div>
            <div class="movie-meta-mockup">
              Ngày khởi chiếu: {{ movie.ngayPhatHanh || "-" }}
            </div>
            <div class="movie-meta-mockup movie-duration-mobile-hide">
              Thời lượng:
              {{ movie.thoiLuong ? movie.thoiLuong + " phút" : "-" }}
            </div>
            <button class="btn-buy-mockup" @click="openCinemaModal(movie)">
              <span class="icon-ticket">🎟️</span> MUA VÉ
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- Showtime Modal -->
    <ShowtimeModal
      :visible="showShowtimeModal"
      :movie="selectedMovieForShowtime"
      :cinema="selectedCinemaForShowtime"
      @close="showShowtimeModal = false"
      @selectShowtime="handleShowtimeSelection"
    />

    <CinemaSelectModal
      :visible="showCinemaModal"
      mode="showtime"
      @close="showCinemaModal = false"
      @showtime-flow="handleCinemaSelection"
    />

    <HomeFooter />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from "vue";
import { useRouter } from "vue-router";
import { fetchMovies } from "../../services/movieService";
import { logoutUser } from "../../services/userService";
import Header from "../../components/Header.vue";
import HomeFooter from "../../components/HomeFooter.vue";
import ShowtimeModal from "../../components/ShowtimeModal.vue";
import CinemaSelectModal from "../../components/CinemaSelectModal.vue";
import { BASE_URL } from "@/constants/api";

const hotMovies = [
  {
    id: 1,
    poster:
      "https://bazaarvietnam.vn/wp-content/uploads/2023/09/van-chi-vu-cua-ngu-thu-han-va-truong-lang-hach-bat-ngo-len-song-9-1536x864.jpg",
    isHot: true,
  },
  {
    id: 2,
    poster:
      "https://thethaovanhoa.mediacdn.vn/372676912336973824/2025/1/17/2-17371062586751888118379.jpg",
    isHot: true,
  },
  {
    id: 3,
    poster:
      "https://preview.redd.it/question-any-places-for-4k-widescreen-movies-wallpapers-v0-vaec689bhjnc1.jpg?width=3840&format=pjpg&auto=webp&s=eae4360698aff41b74210106f02dccf8f3615d35",
    isHot: true,
  },
  {
    id: 4,
    poster:
      "https://i0.wp.com/centrix.software/wp-content/uploads/image-2026.png?w=1267&ssl=1",
    isHot: true,
  },
  {
    id: 5,
    poster:
      "https://i.pinimg.com/736x/d4/d6/1e/d4d61e5409b7692d6e3c9e703d3d9d28.jpg",
    isHot: true,
  },
];
const hoveredMovie = ref(null);
const bannerIndex = ref(0);
let bannerTimer = null;
function nextBanner() {
  bannerIndex.value = (bannerIndex.value + 1) % hotMovies.length;
}
function prevBanner() {
  bannerIndex.value =
    (bannerIndex.value - 1 + hotMovies.length) % hotMovies.length;
}
function startBannerAuto() {
  bannerTimer = setInterval(nextBanner, 4000);
}
function stopBannerAuto() {
  if (bannerTimer) clearInterval(bannerTimer);
}
onMounted(() => {
  startBannerAuto();
});

const router = useRouter();

// User authentication state
const isLoggedIn = ref(false);
const userInfo = ref({});

// Check login status on mount
onMounted(() => {
  const loginStatus = localStorage.getItem("isLoggedIn");
  const userInfoStr = localStorage.getItem("userInfo");

  if (loginStatus === "true" && userInfoStr) {
    try {
      isLoggedIn.value = true;
      userInfo.value = JSON.parse(userInfoStr);
    } catch (e) {
      console.error("Error parsing userInfo:", e);
      isLoggedIn.value = false;
    }
  }
});

// Logout function
async function handleLogout() {
  try {
    await logoutUser();
  } catch (error) {
    console.log("Logout error:", error);
  } finally {
    // Clear localStorage
    localStorage.removeItem("isLoggedIn");
    localStorage.removeItem("userInfo");

    // Update state
    isLoggedIn.value = false;
    userInfo.value = {};

    // Redirect to login
    router.push("/login");
  }
}

function goToBooking() {
  router.push("/booking");
}

const movies = ref([]);
const loadingMovies = ref(true);
const errorMovies = ref("");

// Showtime Modal
const showShowtimeModal = ref(false);
const selectedMovieForShowtime = ref(null);
const selectedCinemaForShowtime = ref(null);
const showCinemaModal = ref(false);
const selectedMovieForCinema = ref(null);
// Hàm load movies
async function loadMovies() {
  loadingMovies.value = true;
  try {
    const res = await fetchMovies();
    movies.value = res.data;
    console.log(movies.value[0]);
    errorMovies.value = "";
  } catch (e) {
    errorMovies.value = "Không thể tải danh sách phim.";
  } finally {
    loadingMovies.value = false;
  }
}

onMounted(async () => {
  await loadMovies();

  // Lắng nghe sự kiện khi có thay đổi phim từ admin
  window.addEventListener("moviesUpdated", async () => {
    console.log("🔄 Phát hiện thay đổi phim, đang refresh dữ liệu...");
    await loadMovies();
  });

  // Kiểm tra localStorage để refresh nếu cần
  const lastUpdate = localStorage.getItem("moviesUpdated");
  if (lastUpdate) {
    const now = Date.now();
    const timeDiff = now - parseInt(lastUpdate);
    // Nếu thời gian cập nhật gần đây (trong vòng 5 phút), refresh dữ liệu
    if (timeDiff < 5 * 60 * 1000) {
      console.log("🔄 Phát hiện cập nhật gần đây, đang refresh dữ liệu...");
      await loadMovies();
    }
  }
});
const nowShowing = computed(() =>
  movies.value.filter((m) => m.trangThai === "DANG_CHIEU")
);
const comingSoon = computed(() =>
  movies.value.filter((m) => m.trangThai === "SAP_CHIEU")
);
const specialShow = computed(() =>
  movies.value.filter((m) => {
    if (!m.status) return false;
    const s = m.status
      .toLowerCase()
      .normalize("NFD")
      .replace(/\p{Diacritic}/gu, "");
    return s.includes("suat chieu");
  })
);
function getAgeBadge(movie) {
  if (movie.age === "P") return { text: "P", color: "#27ae60" };
  if (movie.age === "T13") return { text: "T13", color: "#3498db" };
  if (movie.age === "T16") return { text: "T16", color: "#e17055" };
  if (movie.age === "T18") return { text: "T18", color: "#d63031" };
  return { text: "", color: "#636e72" };
}

const movieTabs = [
  { key: "nowShowing", label: "PHIM ĐANG CHIẾU" },
  { key: "comingSoon", label: "PHIM SẮP CHIẾU" },
];
const tab = ref("nowShowing");
const moviesToShow = computed(() => {
  if (tab.value === "nowShowing") return nowShowing.value;
  if (tab.value === "comingSoon") return comingSoon.value;
  return [];
});

function getPosterUrl(url) {
  if (!url) return "/logo.png?v=2"; // Fallback image
  if (url.startsWith("http")) return url;
  if (url.startsWith("/")) return url; // Public assets
  return (
    BASE_URL + url
  );
}

function handleImageError(event, type = "poster") {
  console.error(
    `❌ [Image Load Error] Không tải được ${type} tại: ${event.target.src}`
  );
  event.target.src = "/logo.png?v=2"; // Fallback image
  event.target.classList.add("image-error");
}

function goToMovieDetail(movie) {
  router.push(`/movie/${movie.idPhim}`);
}

// Showtime Modal Functions
function openCinemaModal(movie) {
  selectedMovieForCinema.value = movie;
  showCinemaModal.value = true;
}

function handleCinemaSelection(cinema) {
  console.log("🎬 HomePage: Cinema selected for showtime flow:", cinema);
  showCinemaModal.value = false;
  if (!selectedMovieForCinema.value) return;

  // Chuyển sang flow modal suất chiếu
  selectedMovieForShowtime.value = selectedMovieForCinema.value;
  selectedCinemaForShowtime.value = cinema;
  showShowtimeModal.value = true;
}

function handleShowtimeSelection(showtimeData) {
  console.log("🎯 HomePage: Handling showtime selection:", showtimeData);
  
  // Redirect to booking page with pre-filled data
  router.push({
    name: "Booking",
    query: {
      movieId: selectedMovieForShowtime.value.idPhim,
      cinemaId: selectedCinemaForShowtime.value.idRapChieu,
      scheduleId: showtimeData.id,
      date: showtimeData.date,
      time: showtimeData.time,
      cinema: selectedCinemaForShowtime.value.tenRapChieu,
    },
  });
}
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Barlow+Condensed:wght@700;900&display=swap");
.header,
.main-header,
.main-header.full-header.dark,
.header-container {
  background: transparent !important;
  box-shadow: none !important;
  border: none !important;
  margin: 0 !important;
  padding: 0 !important;
  position: absolute !important;
  top: 0;
  left: 0;
  right: 0;
  z-index: 10;
  color: #fff;
}
.hero-banner,
.hero-banner.dark {
  width: 100vw;
  min-height: 520px;
  position: relative;
  margin: 0 !important;
  padding: 0 !important;
  border-radius: 0 !important;
  box-shadow: none !important;
  background: url("https://bazaarvietnam.vn/wp-content/uploads/2023/09/van-chi-vu-cua-ngu-thu-han-va-truong-lang-hach-bat-ngo-len-song-9-1536x864.jpg")
    center center/cover no-repeat;
}
.homepage.dark-mode {
  background: none !important;
}
.home-banner {
  width: 100vw;
  min-height: 420px;
  background: linear-gradient(135deg, #0f2027 0%, #2c5364 100%);
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: stretch;
  justify-content: flex-start;
  padding-bottom: 32px;
}
.banner-content {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  max-width: 1200px;
  margin: 0 auto;
  padding-top: 100px;
  width: 100%;
}
.banner-left {
  flex: 1;
  color: #fff;
  z-index: 2;
}
.banner-title {
  font-size: 3rem;
  font-weight: 900;
  margin-bottom: 18px;
  color: #fff;
  text-shadow: 0 2px 12px #000a;
}
.banner-slogan {
  font-size: 1.3rem;
  color: #b6ff00;
  margin-bottom: 24px;
  text-shadow: 0 2px 8px #0006;
}
.banner-buttons {
  display: flex;
  gap: 18px;
}
.btn-primary {
  background: linear-gradient(90deg, #b6ff00 0%, #48dbfb 100%);
  color: #232526;
  border-radius: 24px;
  font-weight: 800;
  font-size: 18px;
  padding: 12px 32px;
  border: none;
  box-shadow: 0 2px 12px #48dbfb33;
  transition: background 0.3s, color 0.2s, transform 0.2s;
  cursor: pointer;
}
.btn-primary:hover {
  background: linear-gradient(90deg, #48dbfb 0%, #b6ff00 100%);
  color: #fff;
  transform: scale(1.05);
}
.btn-secondary {
  background: rgba(255, 255, 255, 0.12);
  color: #fff;
  border-radius: 24px;
  font-weight: 700;
  font-size: 18px;
  padding: 12px 32px;
  border: 1.5px solid #48dbfb;
  box-shadow: 0 2px 12px #48dbfb22;
  transition: background 0.3s, color 0.2s, transform 0.2s;
  cursor: pointer;
}
.btn-secondary:hover {
  background: #48dbfb;
  color: #232526;
  transform: scale(1.05);
}
.banner-right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1;
}
.banner-img {
  width: 380px;
  border-radius: 24px;
  box-shadow: 0 8px 32px #0006;
}
::v-deep .header {
  position: absolute !important;
  top: 0;
  left: 0;
  right: 0;
  z-index: 10;
  background: transparent !important;
  color: #fff;
  box-shadow: none !important;
}
.homepage.dark-mode {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #232526 0%, #1c1c1c 100%);
  color: #fff;
  width: 100%;
  box-sizing: border-box;
}

@media (min-width: 900px) {
  .homepage.dark-mode {
  }
}

.main-header.dark {
  background: #18191a;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.18);
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}
.header-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 16px;
  height: 64px;
  box-sizing: border-box;
}
.logo {
  font-size: 22px;
  font-weight: 800;
  color: #48dbfb;
  text-decoration: none;
  letter-spacing: 2px;
  display: flex;
  align-items: center;
}
.nav-menu {
  display: flex;
  gap: 24px;
  align-items: center;
}
.nav-link {
  color: #fff;
  text-decoration: none;
  font-size: 16px;
  font-weight: 600;
  padding: 8px 0;
  transition: color 0.2s, border-bottom 0.2s;
  border-bottom: 2px solid transparent;
}
.nav-link.active,
.nav-link:hover {
  color: #48dbfb;
  border-bottom: 2px solid #48dbfb;
}
.btn-login {
  background: linear-gradient(135deg, #667eea 0%, #48dbfb 100%);
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 10px 24px;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(72, 219, 251, 0.15);
  transition: all 0.3s;
}
.btn-login:hover {
  background: linear-gradient(135deg, #48dbfb 0%, #667eea 100%);
  transform: translateY(-2px) scale(1.04);
}
.hero-banner.dark {
  width: 100%;
  margin: 0 0 32px 0;
  position: relative;
  min-height: 520px;
}
.banner-slider {
  position: relative;
  width: 100%;
  height: 800px;
  max-height: 95vh;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
.banner-slide {
  position: absolute;
  inset: 0;
  width: 100%;
  height: 100%;
  z-index: 1;
  opacity: 0;
  transition: opacity 0.7s;
  pointer-events: none;
}
.banner-slide.active {
  opacity: 1;
  pointer-events: auto;
}
.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.banner-overlay-gradient {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    120deg,
    rgba(0, 0, 0, 0.38) 60%,
    rgba(44, 62, 80, 0.18) 100%
  );
  z-index: 2;
}
.banner-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 3;
  text-align: center;
  color: #fff;
  width: 100%;
  max-width: 600px;
  padding: 0 24px;
}
.banner-title {
  font-size: 32px;
  font-weight: 900;
  margin-bottom: 16px;
  letter-spacing: 2.5px;
  background: linear-gradient(90deg, #48dbfb 10%, #fff 60%, #667eea 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-shadow: 0 6px 32px rgba(0, 0, 0, 0.45), 0 1px 0 #fff, 0 0 8px #48dbfb;
  filter: drop-shadow(0 2px 12px #232526);
}
.banner-slogan {
  font-size: 26px;
  margin-bottom: 32px;
  font-weight: 600;
  color: #feca57;
  text-shadow: 0 2px 12px rgba(0, 0, 0, 0.35), 0 1px 0 #fff;
}
.btn-primary.big {
  font-size: 20px;
  padding: 16px 38px;
  border-radius: 16px;
  background: linear-gradient(135deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  border: none;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(72, 219, 251, 0.15);
  transition: all 0.3s;
}
.btn-primary.big:hover {
  background: linear-gradient(135deg, #667eea 0%, #48dbfb 100%);
  transform: scale(1.04);
}
.banner-nav {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: rgba(44, 62, 80, 0.45);
  color: #fff;
  border: none;
  font-size: 2.2em;
  border-radius: 50%;
  width: 48px;
  height: 48px;
  cursor: pointer;
  z-index: 4;
  transition: background 0.2s;
  box-shadow: 0 2px 8px rgba(44, 62, 80, 0.18);
}
.banner-nav.left {
  left: 32px;
}
.banner-nav.right {
  right: 32px;
}
.banner-nav:hover {
  background: rgba(44, 62, 80, 0.7);
}
.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.7s;
}
.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}

.movie-section.dark {
  background: linear-gradient(135deg, #232526 0%, #1c1c1c 100%) !important;
  margin: 0 auto 32px auto;
  max-width: 1200px;
  padding: 0 16px;
}
.section-title {
  font-size: 28px;
  font-weight: 700;
  color: #48dbfb;
  margin: 40px 0 24px 0;
  text-align: left;
  letter-spacing: 1px;
}
.movie-carousel {
  overflow-x: auto;
  padding-bottom: 8px;
}
.carousel-track {
  display: flex;
  gap: 32px;
  min-width: 100%;
}
.movie-card-pro.dark {
  background: linear-gradient(135deg, #232526 0%, #1c1c1c 100%);
  border-radius: 18px;
  box-shadow: 0 4px 24px rgba(72, 219, 251, 0.1),
    0 1.5px 8px rgba(44, 62, 80, 0.18);
  overflow: hidden;
  transition: transform 0.3s, box-shadow 0.3s;
  position: relative;
  min-width: 220px;
  max-width: 240px;
  flex: 0 0 220px;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}
.movie-card-pro.dark:hover {
  transform: scale(1.07);
  box-shadow: 0 8px 32px rgba(72, 219, 251, 0.18),
    0 2.5px 16px rgba(44, 62, 80, 0.28);
  z-index: 2;
}
.movie-hover-effect {
  position: absolute;
  inset: 0;
  background: linear-gradient(
    135deg,
    rgba(72, 219, 251, 0.12) 0%,
    rgba(102, 126, 234, 0.1) 100%
  );
  pointer-events: none;
  border-radius: 18px;
  z-index: 3;
  animation: hoverGlow 0.5s;
}
@keyframes hoverGlow {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}
.movie-poster-wrap-pro {
  position: relative;
  width: 100%;
  aspect-ratio: 2/3;
  background: #232526;
  display: flex;
  align-items: center;
  justify-content: center;
}
.movie-poster-pro {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 12px;
  transition: transform 0.3s;
}
.movie-card-pro.dark:hover .movie-poster-pro {
  transform: scale(1.06);
}
.badge-age {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 4px 10px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 700;
  color: #fff;
  z-index: 2;
  box-shadow: 0 2px 8px rgba(44, 62, 80, 0.1);
}
.badge-hot {
  position: absolute;
  top: 10px;
  right: 10px;
  background: linear-gradient(135deg, #ff7675 0%, #fd79a8 100%);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  border-radius: 8px;
  padding: 4px 10px;
  z-index: 2;
}
.badge-special {
  position: absolute;
  bottom: 10px;
  left: 10px;
  background: linear-gradient(135deg, #48dbfb 0%, #0abde3 100%);
  color: #fff;
  font-size: 12px;
  font-weight: 700;
  border-radius: 8px;
  padding: 4px 10px;
  z-index: 2;
}
.movie-info-pro {
  width: 100%;
  padding: 16px 12px 18px 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
}
.movie-title-pro {
  font-size: 17px;
  font-weight: 800;
  color: #fff;
  margin-bottom: 6px;
  text-align: center;
}
.movie-meta-pro {
  font-size: 14px;
  color: #b2bec3;
  margin-bottom: 14px;
  text-align: center;
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  justify-content: center;
}
.btn-buy-pro {
  width: 100%;
  background: linear-gradient(135deg, #48dbfb 0%, #667eea 100%);
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 12px 0;
  font-size: 15px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(72, 219, 251, 0.15);
  transition: all 0.3s;
  margin-top: 4px;
}
.btn-buy-pro:hover {
  background: linear-gradient(135deg, #667eea 0%, #48dbfb 100%);
  transform: translateY(-2px) scale(1.04);
}
.footer.dark {
  background: linear-gradient(135deg, #18191a 60%, #232526 100%);
  color: #fff;
  padding: 48px 0 18px 0;
  width: 100%;
  margin-top: auto;
  box-shadow: 0 -2px 24px rgba(44, 62, 80, 0.18);
}
.footer-content {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: flex-start;
  gap: 32px;
  padding: 0 24px;
}
.footer-col {
  flex: 1 1 220px;
  min-width: 180px;
  margin-bottom: 18px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.footer-col.brand {
  align-items: center;
}
.footer-logo {
  height: 56px;
  width: auto;
  display: block;
  margin-bottom: 0;
  filter: drop-shadow(0 2px 8px #feca57cc);
}
.footer-slogan {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}
.footer-cert {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}
.cert-img {
  height: 48px;
  width: auto;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(44, 62, 80, 0.1);
}
.cert-label {
  display: flex;
  flex-direction: column;
  font-size: 15px;
  color: #2196f3;
  font-weight: 700;
  text-align: left;
}
.cert-title {
  font-size: 16px;
  font-weight: 900;
  color: #2196f3;
}
.cert-sub {
  font-size: 13px;
  color: #2196f3;
}
.footer-col.contact {
  min-width: 260px;
}
.company-name {
  font-size: 17px;
  font-weight: 900;
  color: #232946;
  margin: 8px 0 4px 0;
  text-transform: uppercase;
}
.company-info {
  font-size: 14px;
  color: #232946;
  margin-bottom: 8px;
  line-height: 1.6;
}
.footer-col a {
  color: #b2bec3;
  text-decoration: none;
  font-size: 15px;
  transition: color 0.2s;
}
.footer-col a:hover {
  color: #48dbfb;
  text-decoration: underline;
}
.footer-socials {
  display: flex;
  gap: 12px;
  margin-top: 8px;
}
.footer-social-icon {
  width: 36px;
  height: 36px;
  background: #232526;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  font-size: 20px;
  transition: background 0.2s, color 0.2s;
  box-shadow: 0 2px 8px rgba(72, 219, 251, 0.1);
}
.footer-social-icon:hover {
  background: #48dbfb;
  color: #232526;
}
.footer-copy {
  width: 100%;
  text-align: center;
  font-size: 14px;
  color: #b2bec3;
  margin-top: 18px;
}
.footer-copy a {
  color: #48dbfb;
  text-decoration: none;
  margin: 0 6px;
}
.footer-copy a:hover {
  text-decoration: underline;
}
@media (max-width: 1200px) {
  .carousel-track {
    gap: 18px;
  }
  .footer-content {
    gap: 18px;
  }
}
@media (max-width: 900px) {
  .carousel-track {
    gap: 12px;
  }
  .footer-content {
    flex-direction: column;
    align-items: center;
    gap: 12px;
  }
  .banner-slider {
    height: 260px;
  }
  .banner-title {
    font-size: 28px;
  }
  .banner-slogan {
    font-size: 17px;
  }
}
@media (max-width: 600px) {
  .carousel-track {
    gap: 8px;
  }
  .footer-col {
    min-width: 100px;
  }
  .banner-slider {
    height: 140px;
  }
  .banner-title {
    font-size: 16px;
  }
  .banner-slogan {
    font-size: 12px;
  }
}
.full-header {
  width: 100%;
  background: #18191a;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.18);
  position: sticky;
  top: 0;
  z-index: 100;
}
.header-container {
  max-width: 1400px;
  margin: 0 auto;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  height: 72px;
  box-sizing: border-box;
}
.header-left .logo {
  font-size: 26px;
  font-weight: 900;
  color: #48dbfb;
  text-decoration: none;
  letter-spacing: 2px;
  display: flex;
  align-items: center;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}
.header-menu {
  display: flex;
  gap: 32px;
  align-items: center;
}
.menu-link {
  color: #fff;
  text-decoration: none;
  font-size: 17px;
  font-weight: 700;
  padding: 8px 0;
  transition: color 0.2s, border-bottom 0.2s;
  border-bottom: 2px solid transparent;
  letter-spacing: 0.5px;
  font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}
.menu-link.active,
.menu-link:hover {
  color: #48dbfb;
  border-bottom: 2px solid #48dbfb;
}
.header-right {
  display: flex;
  align-items: center;
  gap: 18px;
}
.hotline {
  color: #feca57;
  font-weight: 700;
  font-size: 15px;
}
.hotline a {
  color: #feca57;
  text-decoration: none;
}
.header-socials {
  display: flex;
  gap: 10px;
}
.header-social-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background: #fff;
  object-fit: cover;
  box-shadow: 0 2px 8px rgba(72, 219, 251, 0.15);
  transition: filter 0.2s;
}
.header-social-icon:hover {
  filter: brightness(0.8);
}
@media (max-width: 1100px) {
  .header-menu {
    gap: 16px;
  }
  .header-container {
    padding: 0 8px;
  }
}
@media (max-width: 900px) {
  .header-container {
    flex-direction: column;
    height: auto;
    gap: 8px;
    padding: 0 2px;
  }
  .header-menu {
    flex-wrap: wrap;
    gap: 8px;
    justify-content: center;
  }
  .header-left .logo {
    font-size: 18px;
  }
}
.movie-loading,
.movie-error {
  color: #feca57;
  text-align: center;
  font-size: 18px;
  margin: 32px 0;
}
@media (max-width: 900px) {
  .movie-grid {
    gap: 16px;
  }
}
@media (max-width: 600px) {
  .movie-grid {
    gap: 8px;
    grid-template-columns: 1fr;
  }
}
.movie-grid-mockup {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 72px 56px;
  padding: 0 40px;
  /* Lùi xuống dưới một chút */
  margin-top: 32px;
}

.movie-card-mockup.dark {
  background: linear-gradient(135deg, #232526 0%, #1c1c1c 100%);
  border-radius: 22px;
  box-shadow: 0 8px 32px rgba(72, 219, 251, 0.1),
    0 2px 12px rgba(44, 62, 80, 0.18);
  overflow: hidden;
  transition: box-shadow 0.25s, transform 0.25s;
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  min-width: 0;
  max-width: 320px;
  margin: 0 auto;
  background-clip: padding-box;
}
.movie-card-mockup.dark:hover {
  transform: translateY(-12px) scale(1.05);
  box-shadow: 0 20px 48px rgba(72, 219, 251, 0.18),
    0 8px 32px rgba(44, 62, 80, 0.28);
  z-index: 3;
}
.movie-poster-wrap-mockup {
  position: relative;
  width: 100%;
  aspect-ratio: 3/4;
  min-height: 320px;
  max-height: 420px;
  background: #232526;
  display: flex;
  align-items: center;
  justify-content: center;
}
.movie-poster-mockup {
  width: 100%;
  height: 100%;
  min-height: 320px;
  max-height: 420px;
  object-fit: cover;
  border-radius: 18px;
  transition: transform 0.3s;
  box-shadow: 0 2px 12px rgba(44, 62, 80, 0.12);
}
.movie-card-mockup.dark:hover .movie-poster-mockup {
  transform: scale(1.06);
  box-shadow: 0 8px 32px rgba(72, 219, 251, 0.18);
}

.image-error {
  opacity: 0.6;
  filter: grayscale(50%);
  border: 2px dashed #e74c3c;
}
.badge-age-mockup {
  position: absolute;
  top: 10px;
  left: 10px;
  padding: 4px 12px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 700;
  color: #fff;
  z-index: 2;
  box-shadow: 0 2px 8px rgba(44, 62, 80, 0.1);
  border: 2px solid #fff2;
}
.badge-hot-mockup {
  position: absolute;
  top: 10px;
  right: 10px;
  background: linear-gradient(135deg, #ff7675 0%, #fd79a8 100%);
  color: #fff;
  font-size: 13px;
  font-weight: 700;
  border-radius: 8px;
  padding: 4px 12px;
  z-index: 2;
  border: 2px solid #fff2;
}
.movie-info-mockup {
  width: 100%;
  padding: 18px 14px 18px 14px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
}
.movie-title-mockup {
  font-size: 18px;
  font-weight: 900;
  color: #2196f3;
  margin-bottom: 2px;
  text-align: left;
  transition: text-decoration 0.2s;
  cursor: pointer;
}
.movie-title-mockup:hover {
  text-decoration: underline;
}
.movie-meta-mockup {
  font-size: 14px;
  color: #b2bec3;
  margin-bottom: 0;
}
.btn-buy-mockup {
  width: 100%;
  background: linear-gradient(135deg, #2196f3 0%, #48dbfb 100%);
  color: #fff;
  border: none;
  border-radius: 10px;
  padding: 12px 0;
  font-size: 15px;
  font-weight: 800;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(72, 219, 251, 0.15);
  transition: all 0.2s;
  margin-top: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
}
.btn-buy-mockup:hover {
  background: linear-gradient(135deg, #48dbfb 0%, #2196f3 100%);
  transform: translateY(-2px) scale(1.04);
  box-shadow: 0 6px 18px rgba(72, 219, 251, 0.18);
}
.icon-ticket {
  font-size: 18px;
  margin-right: 2px;
}
.movie-loading,
.movie-error {
  color: #feca57;
  text-align: center;
  font-size: 18px;
  margin: 32px 0;
}
@media (max-width: 1200px) {
  .movie-grid-mockup {
    grid-template-columns: repeat(2, 1fr);
  }
}
@media (max-width: 900px) {
  .movie-grid-mockup {
    gap: 24px;
    grid-template-columns: 1fr;
    padding: 0 8px;
  }
}
@media (max-width: 600px) {
  .movie-grid-mockup {
    gap: 8px;
    grid-template-columns: 1fr;
    padding: 0 4px;
  }
}

.movie-tabs-large {
  display: flex;
  gap: 14px;
  justify-content: center;
  margin-bottom: 14px;
  border-bottom: none;
  background: transparent;
  flex-wrap: nowrap;
  /* Lùi xuống dưới một chút */
  margin-top: 32px;
}
.movie-tab-large {
  background: none;
  border: none;
  color: #fff;
  font-size: 22px;
  font-weight: 700;
  letter-spacing: 1px;
  padding: 6px 12px 4px 12px;
  text-transform: uppercase;
  cursor: pointer;
  transition: color 0.2s;
  position: relative;
  outline: none;
  margin: 0;
  white-space: nowrap;
}
.movie-tab-large.active {
  color: #48dbfb;
}
.movie-tab-large.active::after {
  display: none !important;
}
.footer-content-custom {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  align-items: flex-start;
  gap: 32px;
  padding: 32px 24px 0 24px;
  background: linear-gradient(90deg, #232526 0%, #1c1c1c 100%);
  border-radius: 0 0 18px 18px;
}
.footer-col-custom {
  flex: 1 1 220px;
  min-width: 180px;
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 12px;
  color: #e3eaf2;
  font-family: "Montserrat", "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}
.footer-col-custom.logo-col {
  align-items: center;
  justify-content: flex-start;
}
.footer-logo-custom {
  height: 72px;
  width: auto;
  display: block;
  margin: 0 auto 10px auto;
  filter: drop-shadow(0 6px 24px #48dbfbcc) drop-shadow(0 2px 8px #feca57cc);
}
.company-name {
  font-size: 17px;
  font-weight: 900;
  color: #48dbfb;
  margin: 8px 0 4px 0;
  text-transform: uppercase;
  text-align: center;
  width: 100%;
}
.footer-title-custom {
  font-size: 20px;
  font-weight: 800;
  color: #48dbfb;
  margin-bottom: 10px;
  letter-spacing: 1px;
  text-transform: uppercase;
}
.footer-menu-link {
  color: #fff;
  font-size: 16px;
  font-weight: 700;
  text-decoration: none;
  margin: 6px 0;
  display: block;
  transition: color 0.2s;
  font-family: "Montserrat", "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}
.footer-menu-link:hover {
  color: #48dbfb;
  text-decoration: underline;
}
.footer-social-icons {
  display: flex;
  gap: 18px;
  margin-bottom: 12px;
}
.footer-social-link img {
  width: 36px;
  height: 36px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(72, 219, 251, 0.1);
  transition: transform 0.2s, box-shadow 0.2s;
}
.footer-social-link img:hover {
  transform: scale(1.12) rotate(-6deg);
  box-shadow: 0 4px 16px #48dbfb44;
}
.footer-cert {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 8px;
}
.cert-img {
  height: 44px;
  width: auto;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(44, 62, 80, 0.1);
}
.cert-label {
  display: flex;
  flex-direction: column;
  font-size: 15px;
  color: #2196f3;
  font-weight: 700;
  text-align: left;
}
.cert-title {
  font-size: 16px;
  font-weight: 900;
  color: #2196f3;
}
.cert-sub {
  font-size: 13px;
  color: #2196f3;
}
.footer-col-custom.contact {
  align-items: flex-start;
}
.company-info {
  font-size: 14px;
  color: #e3eaf2;
  margin-bottom: 8px;
  line-height: 1.6;
}
.footer-copyright-custom {
  text-align: center;
  color: #b2bec3;
  font-size: 14px;
  margin-top: 24px;
  letter-spacing: 1px;
  font-family: "Montserrat", "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
}
@media (max-width: 1100px) {
  .footer-content-custom {
    gap: 18px;
    padding: 24px 8px 0 8px;
  }
}
@media (max-width: 900px) {
  .footer-content-custom {
    flex-direction: column;
    align-items: center;
    gap: 18px;
    padding: 24px 8px 0 8px;
  }
  .footer-col-custom {
    min-width: 160px;
    width: 100%;
    align-items: center;
    text-align: center;
  }
  .footer-col-custom.contact {
    align-items: center;
    text-align: center;
  }
  .footer-col-custom.menu {
    align-items: center;
    text-align: center;
  }
}
.banner-arrow {
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  color: #fff;
  border: none;
  font-size: 4.5em;
  width: 64px;
  height: 64px;
  cursor: pointer;
  z-index: 4;
  transition: color 0.2s, background 0.2s;
  outline: none;
  box-shadow: none;
}
.banner-arrow.left {
  left: 40px;
}
.banner-arrow.right {
  right: 40px;
}

.banner-content-modern {
  position: absolute;
  top: 30%;
  left: 5%;
  z-index: 3;
  text-align: left;
  color: #fff;
  max-width: 600px;
  padding: 32px 32px 32px 32px;
  background: rgba(0, 0, 0, 0.28);
  border-radius: 18px;
  box-shadow: 0 4px 32px #0006;
}
.banner-title-modern {
  font-family: "Barlow Condensed", "Montserrat", Arial, sans-serif;
  font-size: 3.2rem;
  font-weight: 900;
  letter-spacing: 2px;
  color: #fff;
  text-shadow: 0 6px 32px #000a, 0 1px 0 #fff, 0 0 8px #48dbfb;
  margin-bottom: 12px;
}
.banner-slogan-modern {
  font-size: 1.5rem;
  color: #feca57;
  font-weight: 700;
  margin-bottom: 24px;
  text-shadow: 0 2px 12px #000a;
}
.banner-btn-modern {
  font-size: 1.2rem;
  font-weight: 800;
  padding: 14px 38px;
  border-radius: 24px;
  background: linear-gradient(90deg, #b6ff00 0%, #48dbfb 100%);
  color: #232526;
  border: none;
  box-shadow: 0 2px 12px #48dbfb33;
  cursor: pointer;
  transition: background 0.3s, color 0.2s, transform 0.2s;
}
.banner-btn-modern:hover {
  background: linear-gradient(90deg, #48dbfb 0%, #b6ff00 100%);
  color: #fff;
  transform: scale(1.05);
}
.banner-poster-reflect {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 100%;
  max-width: 700px;
  margin: 0 auto;
  z-index: 2;
}
.banner-poster-reflect .banner-img {
  display: block;
  width: 100%;
  border-radius: 24px 24px 0 0;
  box-shadow: 0 4px 32px #0006;
}
.banner-poster-reflect .reflection {
  width: 100%;
  height: 60px;
  object-fit: cover;
  margin-top: -8px;
  transform: scaleY(-1);
  opacity: 0.22;
  filter: blur(2.5px);
  border-radius: 0 0 24px 24px;
  pointer-events: none;
  z-index: 1;
}
.thumb-carousel {
  display: flex;
  justify-content: center;
  align-items: flex-end;
  gap: 32px;
  margin: 0;
  padding-bottom: 24px;
  background: rgba(24, 25, 26, 0.55);
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 5;
  pointer-events: none;
  backdrop-filter: blur(16px) saturate(1.2);
  border-radius: 0 0 32px 32px;
  box-shadow: 0 8px 48px #000a;
}
.thumb-poster-reflect {
  display: flex;
  flex-direction: column;
  align-items: center;
  width: 220px;
  margin: 0 16px;
  pointer-events: auto;
  border-radius: 28px;
  transition: transform 0.22s, box-shadow 0.22s, border 0.22s;
}
.thumb-poster-reflect:hover .thumb-img {
  transform: scale(1.08);
  box-shadow: 0 8px 32px #48dbfbcc, 0 0 24px #fff, 0 2px 12px #fff8;
  border: 3px solid #fff;
  filter: drop-shadow(0 0 12px #48dbfbcc);
  z-index: 2;
}
.thumb-poster-reflect.active .thumb-img {
  border: none;
  outline: none;
  box-shadow: none;
  filter: none;
  transform: scale(1.12);
  z-index: 3;
  border-radius: 28px;
  transition: all 0.25s;
  background: rgba(30, 30, 30, 0.45);
  backdrop-filter: blur(8px);
}
.thumb-img {
  width: 100%;
  border-radius: 28px;
  box-shadow: 0 4px 24px #0006;
  transition: transform 0.22s, box-shadow 0.22s, border 0.22s;
}
.thumb-reflection {
  width: 100%;
  height: 40px;
  object-fit: cover;
  margin-top: -8px;
  transform: scaleY(-1);
  opacity: 0.22;
  filter: blur(2.5px);
  border-radius: 0 0 18px 18px;
  pointer-events: none;
}

/* Hiệu ứng chuyển động cho poster nhỏ */
.thumb-fade-slide-enter-active,
.thumb-fade-slide-leave-active {
  transition: all 0.35s cubic-bezier(0.4, 1.3, 0.6, 1);
}
.thumb-fade-slide-enter-from {
  opacity: 0;
  transform: translateY(40px) scale(0.92);
}
.thumb-fade-slide-leave-to {
  opacity: 0;
  transform: translateY(-40px) scale(0.92);
}
</style>

<style>
html {
  overflow-y: scroll !important;
  height: 100%;
  background: #18191a !important;
}

body {
  overflow-y: scroll !important;
  margin: 0;
  padding: 0;
  min-height: 100vh;
  height: auto;
  background: #18191a !important;
}

#app {
  min-height: 100vh;
  height: auto;
}

/* User Menu Styles */
.user-menu {
  display: flex;
  align-items: center;
  margin-left: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-name {
  color: #fff;
  font-size: 14px;
  font-weight: 600;
  background: rgba(255, 255, 255, 0.1);
  padding: 8px 12px;
  border-radius: 8px;
  backdrop-filter: blur(10px);
}

.logout-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #ff6b6b, #ee5a52);
  color: white;
  border: none;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
}

.logout-btn:hover {
  background: linear-gradient(135deg, #ff5252, #d32f2f);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.4);
}

.logout-icon {
  font-size: 14px;
}

.login-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  background: linear-gradient(135deg, #2196f3, #48dbfb);
  color: white;
  text-decoration: none;
  border-radius: 8px;
  padding: 8px 12px;
  font-size: 13px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(33, 150, 243, 0.3);
}

.login-btn:hover {
  background: linear-gradient(135deg, #1976d2, #2196f3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(33, 150, 243, 0.4);
}

.login-icon {
  font-size: 14px;
}

/* Responsive for user menu */
@media (max-width: 768px) {
  .user-menu {
    margin-left: 10px;
  }

  .user-info {
    flex-direction: column;
    gap: 8px;
  }

  .user-name {
    font-size: 12px;
    padding: 6px 8px;
  }

  .logout-btn,
  .login-btn {
    font-size: 12px;
    padding: 6px 8px;
  }
}

@media (max-width: 600px) {
  .movie-duration-mobile-hide {
    display: none;
  }
}
</style>
