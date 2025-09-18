<!-- TrailerModal.vue -->
<template>
  <div v-if="show" class="modal-backdrop" @click.self="close">
    <div class="modal-content" role="dialog" aria-modal="true" aria-label="Trailer phim">
      <button class="btn-close" @click="close" aria-label="Đóng modal">&times;</button>
      <div class="video-wrapper">
        <iframe
          v-if="embedUrl"
          :src="embedUrl"
          frameborder="0"
          allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture"
          allowfullscreen
        ></iframe>
        <p v-else>Trailer không tồn tại.</p>
      </div>
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
  background: #18191a;
  border-radius: 12px;
  padding: 0;
  position: relative;
  max-width: 90vw;
  width: auto;
  box-shadow: 0 0 15px rgba(0,0,0,0.5);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.video-wrapper {
  width: 90vw;
  max-width: 800px;
  aspect-ratio: 16 / 9;
  background: #000;
  border-radius: 12px;
  overflow: hidden;
  margin: 0;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-close {
  position: absolute;
  top: 10px;
  right: 18px;
  background: none;
  border: none;
  font-size: 32px;
  cursor: pointer;
  color: #fff;
  z-index: 2;
  text-shadow: 0 2px 8px #000a;
}

.btn-close:hover {
  color: #feca57;
}

iframe {
  display: block;
  border-radius: 6px;
  width: 100%;
  height: 100%;
  border: none;
  background: #000;
}
</style>
