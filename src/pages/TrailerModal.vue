<!-- TrailerModal.vue -->
<template>
  <div v-if="show" class="modal-backdrop" @click.self="close">
    <div class="modal-content" role="dialog" aria-modal="true" aria-label="Trailer phim">
      <button class="btn-close" @click="close" aria-label="Đóng modal">&times;</button>
      <iframe
        v-if="embedUrl"
        :src="embedUrl"
        frameborder="0"
        allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
        allowfullscreen
        width="800"
        height="450"
      ></iframe>
      <p v-else>Trailer không tồn tại.</p>
    </div>
  </div>
</template>

<script setup>
import { computed, defineProps, defineEmits } from 'vue'

const props = defineProps({
  show: Boolean,
  url: String,
})

const emit = defineEmits(['close'])

// Chuyển link YouTube thành embed URL (dạng iframe)
const embedUrl = computed(() => {
  if (!props.url) return ''
  const match = props.url.match(/(?:v=|youtu\.be\/)([^&]+)/)
  const videoId = match ? match[1] : ''
  return videoId ? `https://www.youtube.com/embed/${videoId}?autoplay=1` : ''
})

function close() {
  emit('close')
}
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.65);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.modal-content {
  background: white;
  border-radius: 8px;
  padding: 12px;
  position: relative;
  max-width: 90vw;
  max-height: 90vh;
  box-shadow: 0 0 15px rgba(0,0,0,0.3);
}

.btn-close {
  position: absolute;
  top: 6px;
  right: 10px;
  background: none;
  border: none;
  font-size: 30px;
  cursor: pointer;
  color: #444;
}

.btn-close:hover {
  color: #000;
}

iframe {
  display: block;
  border-radius: 6px;
  max-width: 100%;
}
</style>
