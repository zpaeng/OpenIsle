<template>
  <div class="popup" v-if="visible">
    <div class="popup-overlay" @click="close"></div>
    <div class="popup-container">
      <template v-if="icon">
        <img v-if="isImage(icon)" :src="icon" class="popup-icon-img" />
        <i v-else :class="['popup-icon', icon]"></i>
      </template>
      <div class="popup-message">{{ message }}</div>
      <div class="popup-actions">
        <div class="popup-button" @click="confirm">{{ buttonText }}</div>
        <div class="popup-close" @click="close">{{ closeText }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'BasePopup',
  props: {
    visible: { type: Boolean, default: false },
    icon: { type: String, default: '' },
    message: { type: String, default: '' },
    buttonText: { type: String, default: '前往' },
    closeText: { type: String, default: '关闭' },
    link: { type: String, default: '' }
  },
  emits: ['close'],
  setup (props, { emit }) {
    const router = useRouter()
    const close = () => emit('close')
    const confirm = () => {
      if (props.link) router.push(props.link)
      close()
    }
    const isImage = (i) => i.startsWith('http') || i.startsWith('data:image') || i.match(/\.(png|jpe?g|gif|svg)$/)
    return { close, confirm, isImage }
  }
}
</script>

<style scoped>
.popup {
  position: fixed;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 30;
}

.popup-overlay {
  position: absolute;
  inset: 0;
  backdrop-filter: blur(4px);
  -webkit-backdrop-filter: blur(4px);
  background-color: rgba(0, 0, 0, 0.3);
}

.popup-container {
  position: relative;
  z-index: 2;
  background: var(--background-color);
  padding: 20px 30px;
  border-radius: 10px;
  text-align: center;
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: center;
  max-width: 80%;
}

.popup-icon {
  font-size: 48px;
  color: var(--primary-color);
}

.popup-icon-img {
  width: 64px;
  height: 64px;
  border-radius: 50%;
  object-fit: cover;
}

.popup-button {
  background-color: var(--primary-color);
  color: #fff;
  padding: 6px 20px;
  border-radius: 6px;
  cursor: pointer;
}
.popup-button:hover {
  background-color: var(--primary-color-hover);
}

.popup-close {
  margin-top: 5px;
  font-size: 12px;
  color: var(--primary-color);
  cursor: pointer;
}
.popup-close:hover {
  text-decoration: underline;
}
</style>

