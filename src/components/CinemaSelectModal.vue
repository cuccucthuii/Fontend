<template>
  <div v-if="visible" class="modal-overlay" @click.self="close">
    <div class="modal-content">
      <h2 class="modal-title">Chọn rạp</h2>
      <select
        v-model="selectedCinemaId"
        placeholder="Chọn rạp chiếu phim"
        style="width: 100%; padding: 10px; border-radius: 5px; border: 1px solid #ddd; background: white; color: #333;"
        :disabled="loading"
      >
        <option value="" disabled>Chọn rạp chiếu phim</option>
        <option v-for="option in cinemaOptions" :key="option.value" :value="option.value">
          {{ option.label }}
        </option>
      </select>
      <div class="modal-actions">
        <button
          @click="confirmSelection"
          class="confirm-button"
          :disabled="!selectedCinemaId"
        >
          Xác nhận
        </button>
        <button @click="close" class="close-button">Đóng</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from "vue";
import { getAllCinemas } from "../services/cinemaService";

const props = defineProps({
  visible: {
    type: Boolean,
    required: true,
  },
  mode: {
    type: String,
    default: 'booking',
    validator: value => ['booking', 'showtime'].includes(value)
  },
});

// Debug: Watch visible changes
watch(() => props.visible, (newVal) => {
  console.log('🎬 CinemaSelectModal visible changed to:', newVal)
}, { immediate: true });

const emit = defineEmits(["close", "cinema-selected", "showtime-flow"]);

console.log("🔧 CinemaSelectModal setup - props.mode:", props.mode);

const cinemas = ref([]);
const loading = ref(false);
const error = ref(null);
const selectedCinemaId = ref(null);

    const fetchCinemas = async () => {
      loading.value = true;
      error.value = null;
      try {
        console.log("🎬 CinemaSelectModal: Fetching cinemas...");
        // The API returns the data directly, not nested under a `data` property
        const data = await getAllCinemas();
        console.log("🎬 CinemaSelectModal: Received data:", data);
        if (Array.isArray(data)) {
          cinemas.value = data;
          console.log("🎬 CinemaSelectModal: Set cinemas to:", cinemas.value.length, "items");
        } else {
          console.error("getAllCinemas did not return an array:", data);
          cinemas.value = []; // Fallback to an empty array
        }
      } catch (err) {
        console.error("🎬 CinemaSelectModal: Error fetching cinemas:", err);
        error.value = "Không thể tải danh sách rạp.";
        cinemas.value = []; // Ensure cinemas is an array on error
      } finally {
        loading.value = false;
        console.log("🎬 CinemaSelectModal: Loading finished");
      }
    };

    onMounted(fetchCinemas);

    // Debug: Watch mode changes
    watch(() => props.mode, (newMode) => {
      console.log("🔧 CinemaSelectModal mode changed to:", newMode);
    }, { immediate: true });

    // Debug: Watch visible changes  
    watch(() => props.visible, (newVisible) => {
      console.log("🔧 CinemaSelectModal visible changed to:", newVisible, "mode:", props.mode);
    });

    const cinemaOptions = computed(() => {
      if (!Array.isArray(cinemas.value)) {
        console.log("🎬 cinemaOptions: cinemas.value is not an array:", cinemas.value);
        return [];
      }
      const options = cinemas.value.map((cinema) => ({
        value: cinema.idRapChieu,
        label: cinema.tenRapChieu,
      }));
      console.log("🎬 cinemaOptions computed:", options);
      return options;
    });

    const confirmSelection = () => {
      console.log("🎬 Confirm selection clicked, selectedCinemaId:", selectedCinemaId.value);
      if (selectedCinemaId.value) {
        const selectedCinema = cinemas.value.find(
          (c) => c.idRapChieu === selectedCinemaId.value
        );
        if (selectedCinema) {
          console.log("🎬 Cinema selected, mode:", props.mode);
          
          if (props.mode === 'showtime') {
            // Cho flow showtime: emit event riêng để mở modal suất chiếu
            emit("showtime-flow", selectedCinema);
          } else {
            // Cho flow booking truyền thống: emit cinema-selected 
            emit("cinema-selected", selectedCinema);
          }
        }
      }
    };

    const close = () => {
      emit("close");
    };

</script>

<style scoped>
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.7);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background-color: #2c3e50;
  padding: 25px;
  border-radius: 10px;
  width: 90%;
  max-width: 450px;
  color: white;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.5);
}

.modal-title {
  margin-top: 0;
  margin-bottom: 20px;
  font-size: 1.5em;
  text-align: center;
}

.modal-actions {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.confirm-button,
.close-button {
  padding: 10px 20px;
  border-radius: 5px;
  border: none;
  cursor: pointer;
  font-weight: bold;
}

.confirm-button {
  background-color: #3498db;
  color: white;
}

.confirm-button:disabled {
  background-color: #555;
  cursor: not-allowed;
}

.close-button {
  background-color: #95a5a6;
  color: #2c3e50;
}
</style>
