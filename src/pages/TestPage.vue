<template>
  <div style="padding: 50px; background: green; color: white; font-size: 24px; min-height: 100vh;">
    <h1>TEST PAGE - Nếu bạn thấy trang này màu xanh thì component hoạt động!</h1>
    <p>Đây là trang test để kiểm tra xem Vue component có hoạt động không</p>
    <button @click="testClick" style="background: yellow; color: black; padding: 10px 20px; border: none; border-radius: 5px; margin: 20px 0;">
      Click để test
    </button>
    <p v-if="clicked" style="color: yellow;">✅ Button đã được click!</p>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const clicked = ref(false)

function testClick() {
  clicked.value = true
  console.log('✅ Test button clicked!')
}
</script> 