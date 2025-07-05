<template>
  <div class="login-page">
    <div class="login-page-content">
      <div class="login-page-header">
        <div class="login-page-header-title">
          Welcome :)
        </div>
      </div>

      <div class="email-login-page-content">
        <div class="login-page-input">
          <i class="login-page-input-icon fas fa-envelope"></i>
          <input
            class="login-page-input-text"
            v-model="username"
            type="text"
            placeholder="邮箱/用户名"
          >
        </div>

        <div class="login-page-input">
          <i class="login-page-input-icon fas fa-lock"></i>
          <input
            class="login-page-input-text"
            v-model="password"
            type="password"
            placeholder="密码"
          >
        </div>


        <div v-if="!isWaitingForLogin" class="login-page-button-primary" @click="submitLogin">
          <div class="login-page-button-text">登录</div>
        </div>

        <div v-else class="login-page-button-primary disabled">
          <div class="login-page-button-text">
            <i class="fas fa-spinner fa-spin"></i>
            登录中...
          </div>
        </div>

        <div class="login-page-button-secondary">没有账号？ <a class="login-page-button-secondary-link" href="/signup">注册</a>
        </div>
      </div>
    </div>

    <div class="other-login-page-content">
      <div class="login-page-button">
        <img class="login-page-button-icon" src="../assets/icons/google.svg" alt="Google Logo" />
        <div class="login-page-button-text">Google 登录</div>
      </div>
    </div>
  </div>
</template>

<script>
import { API_BASE_URL, toast } from '../main'
import { setToken } from '../utils/auth'
export default {
  name: 'LoginPageView',
  data() {
    return {
      username: '',
      password: '',
      isWaitingForLogin: false
    }
  },
  methods: {
    async submitLogin() {
      try {
        this.isWaitingForLogin = true
        const res = await fetch(`${API_BASE_URL}/api/auth/login`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ username: this.username, password: this.password })
        })
        this.isWaitingForLogin = false
        const data = await res.json()
        if (res.ok && data.token) {
          setToken(data.token)
          toast.success('登录成功')
          this.$router.push('/')
        } else {
          toast.error(data.error || '登录失败')
        }
      } catch (e) {
        toast.error('登录失败')
      }
    }
  }
}
</script>

<style scoped>
.login-page {
  margin-top: 100px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
}

.login-page-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: calc(40% - 120px);
  border-right: 1px solid #ccc;
  padding-right: 120px;
}

.login-page-header-title {
  font-family: 'Pacifico', 'Comic Sans MS', cursive, 'Roboto', sans-serif;
  font-size: 42px;
  font-weight: bold;
  width: 100%;
  opacity: 0.75;
}

.login-page-header {
  font-size: 42px;
  font-weight: bold;
  width: 100%;
}

.email-login-page-content {
  margin-top: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.login-page-input {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  width: calc(100% - 40px);
  padding: 15px 20px;
  border-radius: 10px;
  border: 1px solid #ccc;
  gap: 10px;
  margin-bottom: 20px;
}

.login-page-input-icon {
  opacity: 0.5;
  font-size: 16px;
}

.login-page-input-text {
  border: none;
  outline: none;
  width: 100%;
  font-size: 16px;
}

.other-login-page-content {
  margin-left: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 30%;
}

.login-page-button-primary {
  margin-top: 20px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  width: calc(100% - 40px);
  background-color: var(--primary-color);
  color: white;
  padding: 10px 20px;
  border-radius: 10px;
  cursor: pointer;
  gap: 10px;
}

.login-page-button-primary:hover {
  background-color: var(--primary-color-hover);
}

.login-page-button-primary.disabled {
  background-color: var(--primary-color-disabled);
  opacity: 0.5;
  cursor: not-allowed;
}

.login-page-button-primary.disabled:hover {
  background-color: var(--primary-color-disabled);
}

.login-page-button {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  padding: 10px 20px;
  background-color: var(--normal-background-color);
  border: 1px solid #ccc;
  border-radius: 10px;
  cursor: pointer;
  gap: 10px;
}

.login-page-button:hover {
  background-color: #e0e0e0;
}

.login-page-button-icon {
  width: 20px;
  height: 20px;
}

.login-page-button-text {
  font-size: 16px;
}

.login-page-button-secondary {
  margin-top: 20px;
  font-size: 16px;
  opacity: 0.7;
}

.login-page-button-secondary-link {
  color: var(--primary-color);
}
</style>