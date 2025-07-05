<template>
  <header class="header">
    <div class="header-content">
      <div class="header-content-left">
        <button v-if="showMenuBtn" class="menu-btn" @click="$emit('toggle-menu')">
          <i class="fas fa-bars"></i>
        </button>
        <div class="logo-container" @click="goToHome">
          <img alt="OpenIsle" src="https://openisle-1307107697.cos.ap-guangzhou.myqcloud.com/assert/image.png"
            width="60" height="60">
          <div class="logo-text">OpenIsle</div>
        </div>
      </div>

      <div class="header-content-right">
        <div class="header-content-item-main" @click="goToLogin">登录</div>
        <div class="header-content-item-secondary" @click="goToSignup">注册</div>
      </div>
    </div>
  </header>
</template>

<script>
export default {
  name: 'HeaderComponent',
  props: {
    showMenuBtn: {
      type: Boolean,
      default: true
    }
  },
  methods: {
    goToHome() {
      this.$router.push('/')
    },
    goToLogin() {
      this.$router.push('/login')
    },
    goToSignup() {
      this.$router.push('/signup')
    }
  }
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: var(--header-height);
  background-color: var(--header-background-color);
  color: var(--header-text-color);
  border-bottom: 1px solid var(--header-border-color);
}

.logo-container {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
  cursor: pointer;
}

.header-content {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
  width: 100%;
  height: 100%;
  margin: 0 auto;
  max-width: var(--page-max-width);
}

.header-content-left {
  display: flex;
  flex-direction: row;
  align-items: center;
}

.header-content-right {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 20px;
}

.menu-btn {
  font-size: 24px;
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
  opacity: 0.4;
  margin-right: 10px;
}

.menu-btn:hover {
  opacity: 0.8;
}

.header-content-item-main {
  background-color: var(--primary-color);
  color: white;
  padding: 8px 16px;
  border-radius: 10px;
  cursor: pointer;
}

.header-content-item-main:hover {
  background-color: var(--primary-color-hover);
}

.header-content-item-secondary {
  color: var(--primary-color);
  text-decoration: underline;
  cursor: pointer;
}

</style>
