<template>
  <!-- Cinema Selection Modal -->
  <CinemaSelectModal
    :visible="showCinemaModal"
    @close="closeCinemaModal"
    @cinema-selected="onCinemaSelected"
  />

  <!-- Showtime Modal -->
  <ShowtimeModal
    v-if="selectedCinema"
    :movie="selectedMovie"
    :cinema="selectedCinema"
    :visible="showShowtimeModal"
    @close="closeShowtimeModal"
    @selectShowtime="onShowtimeSelected"
  />
</template>

<script setup>
import { ref } from 'vue'
import CinemaSelectModal from './CinemaSelectModal.vue'
import ShowtimeModal from './ShowtimeModal.vue'

const props = defineProps({
  movie: { type: Object, default: null }
})

const emit = defineEmits(['selectShowtime', 'close'])

// State management
const selectedMovie = ref(props.movie)
const selectedCinema = ref(null)
const showCinemaModal = ref(false)
const showShowtimeModal = ref(false)

// Public methods to start the flow
function startBookingFlow(movie) {
  selectedMovie.value = movie
  selectedCinema.value = null
  showCinemaModal.value = true
  showShowtimeModal.value = false
}

// Cinema modal handlers
function closeCinemaModal() {
  showCinemaModal.value = false
  emit('close')
}

function onCinemaSelected(cinema) {
  selectedCinema.value = cinema
  showCinemaModal.value = false
  showShowtimeModal.value = true
}

// Showtime modal handlers
function closeShowtimeModal() {
  showShowtimeModal.value = false
  // Optionally go back to cinema selection
  // showCinemaModal.value = true
  emit('close')
}

function onShowtimeSelected(showtimeData) {
  emit('selectShowtime', {
    movie: selectedMovie.value,
    cinema: selectedCinema.value,
    showtime: showtimeData
  })
  closeShowtimeModal()
}

// Expose methods for parent components
defineExpose({
  startBookingFlow,
  closeCinemaModal,
  closeShowtimeModal
})
</script>

<style scoped>
/* No additional styles needed - modals handle their own styling */
</style>
