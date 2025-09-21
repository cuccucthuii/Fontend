<template>
  <svg v-if="type === 'VIP'" :class="['seat-svg', status]" width="32" height="32" viewBox="0 0 32 32" fill="none">
    <rect x="4" y="10" width="24" height="14" rx="6" :fill="statusColor"/>
    <rect x="8" y="24" width="16" height="4" rx="2" :fill="statusColor"/>
    <rect x="10" y="6" width="12" height="6" rx="3" fill="#fff" fill-opacity="0.7"/>
    <rect x="12" y="2" width="8" height="4" rx="2" fill="#fff" fill-opacity="0.9"/>
  </svg>
  <svg v-else :class="['seat-svg', status]" width="32" height="32" viewBox="0 0 32 32" fill="none">
    <rect x="4" y="10" width="24" height="14" rx="6" :fill="statusColor"/>
    <rect x="8" y="24" width="16" height="4" rx="2" :fill="statusColor"/>
    <rect x="10" y="6" width="12" height="6" rx="3" fill="#fff" fill-opacity="0.7"/>
  </svg>
</template>
<script setup>
import { computed } from 'vue'
const props = defineProps({
  type: { type: String, default: 'THUONG' },
  status: { type: String, default: 'available' } // available, selected, booked
})
const statusColor = computed(() => {
  if (props.status === 'booked') return '#b0b0b0'
  if (props.status === 'selected') return '#4f8cff'
  if (props.type === 'VIP') return '#ffd600'
  return '#48dbfb'
})
</script>
<style scoped>
.seat-svg {
  width: 32px;
  height: 32px;
  transition: filter 0.2s;
  filter: drop-shadow(0 2px 6px rgba(0,0,0,0.08));
}
.seat-svg.selected {
  filter: drop-shadow(0 0 8px #4f8cff);
}
.seat-svg.booked {
  opacity: 0.5;
}
</style> 