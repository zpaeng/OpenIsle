<template>
  <div class="login-overlay">
    <i class="fa-solid fa-user login-overlay-icon"></i>
    <div class="login-overlay-text">
      请先登录，点击跳转到登录页面
    </div>

    <div class="login-overlay-button" @click="goLogin">
      登录
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
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  z-index: 15;
}

.login-overlay-icon {
  margin-right: 10px;
}

.login-overlay-button {
  padding: 4px 8px;
  border-radius: 5px;
  background-color: var(--primary-color);
  color: white;
  cursor: pointer;
  margin-left: 10px;
  cursor: pointer;
}

.login-overlay-button:hover {
  background-color: var(--primary-color-hover);
}

</style>
