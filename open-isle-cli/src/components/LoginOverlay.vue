<template>
  <div class="login-overlay" @click="goLogin">
    <div class="login-overlay-text">
      请先登录，点击跳转到登录页面
    </div>
  </div>
</template>

<script>
import { useRouter } from 'vue-router'

export default {
  name: 'LoginOverlay',
  setup() {
    const router = useRouter()
    const goLogin = () => {
      router.push('/login')
    }
    return { goLogin }
  }
}
</script>

<style scoped>
.login-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  cursor: pointer;
  z-index: 15;
}
</style>
