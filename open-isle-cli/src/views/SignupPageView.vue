<template>
  <div class="signup-page">
    <div class="signup-page-content">
      <div class="signup-page-header">
        <div class="signup-page-header-title">
          Welcome :)
        </div>
      </div>

      <div v-if="emailStep === 0" class="email-signup-page-content">
        <div class="signup-page-input">
          <i class="signup-page-input-icon fas fa-envelope"></i>
          <input
            class="signup-page-input-text"
            v-model="email"
            type="text"
            placeholder="邮箱"
          >
        </div>

        <div class="signup-page-input">
          <i class="signup-page-input-icon fas fa-user"></i>
          <input
            class="signup-page-input-text"
            v-model="username"
            type="text"
            placeholder="用户名"
          >
        </div>

        <div class="signup-page-input">
          <i class="signup-page-input-icon fas fa-lock"></i>
          <input
            class="signup-page-input-text"
            v-model="password"
            type="password"
            placeholder="密码"
          >
        </div>

        <div class="signup-page-input">
          <i class="signup-page-input-icon fas fa-user"></i>
          <input
            class="signup-page-input-text"
            v-model="nickname"
            type="text"
            placeholder="昵称 (可选)"
          >
        </div>

        <div class="signup-page-button-primary" @click="sendVerification">
          <div class="signup-page-button-text">验证邮箱</div>
        </div>

        <div class="signup-page-button-secondary">已经有账号？ <a class="signup-page-button-secondary-link"
            href="/login">登录</a></div>
      </div>

      <div v-if="emailStep === 1" class="email-signup-page-content">
        <div class="signup-page-input">
          <i class="signup-page-input-icon fas fa-envelope"></i>
          <input
            class="signup-page-input-text"
            v-model="code"
            type="text"
            placeholder="邮箱验证码"
          >
        </div>
        <div class="signup-page-button-primary" @click="verifyCode">
          <div class="signup-page-button-text">注册</div>
        </div>
      </div>
    </div>

    <div class="other-signup-page-content">
      <div class="signup-page-button">
        <img class="signup-page-button-icon" src="../assets/icons/google.svg" alt="Google Logo" />
        <div class="signup-page-button-text">Google 注册</div>
      </div>
    </div>
  </div>
</template>

<script>
import { API_BASE_URL } from '../main'
export default {
  name: 'SignupPageView',

  data() {
    return {
      emailStep: 0,
      email: '',
      username: '',
      password: '',
      nickname: '',
      code: ''
    }
  },
  methods: {
    async sendVerification() {
      try {
        const res = await fetch(`${API_BASE_URL}/api/auth/register`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            username: this.username,
            email: this.email,
            password: this.password
          })
        })
        const data = await res.json()
        if (res.ok) {
          this.emailStep = 1
          this.$toast.success('验证码已发送，请查看邮箱')
        } else {
          this.$toast.error(data.error || '发送失败')
        }
      } catch (e) {
        this.$toast.error('发送失败')
      }
    },
    async verifyCode() {
      try {
        const res = await fetch(`${API_BASE_URL}/api/auth/verify`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({ username: this.username, code: this.code })
        })
        const data = await res.json()
        if (res.ok) {
          this.$toast.success('注册成功，请登录')
          this.$router.push('/login')
        } else {
          this.$toast.error(data.error || '注册失败')
        }
      } catch (e) {
        this.$toast.error('注册失败')
      }
    }
  }
}
</script>

<style scoped>
.signup-page {
  margin-top: 100px;
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  height: 100%;
  width: 100%;
}

.signup-page-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: calc(40% - 120px);
  border-right: 1px solid #ccc;
  padding-right: 120px;
}

.signup-page-header-title {
  font-family: 'Pacifico', 'Comic Sans MS', cursive, 'Roboto', sans-serif;
  font-size: 42px;
  font-weight: bold;
  width: 100%;
  opacity: 0.75;
}

.signup-page-header {
  font-size: 42px;
  font-weight: bold;
  width: 100%;
}

.email-signup-page-content {
  margin-top: 40px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.signup-page-input {
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

.signup-page-input-icon {
  opacity: 0.5;
  font-size: 16px;
}

.signup-page-input-text {
  border: none;
  outline: none;
  width: 100%;
  font-size: 16px;
}

.other-signup-page-content {
  margin-left: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 30%;
}

.signup-page-button-primary {
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

.signup-page-button-primary:hover {
  background-color: var(--primary-color-hover);
}

.signup-page-button {
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

.signup-page-button:hover {
  background-color: #e0e0e0;
}

.signup-page-button-icon {
  width: 20px;
  height: 20px;
}

.signup-page-button-text {
  font-size: 16px;
}

.signup-page-button-secondary {
  margin-top: 20px;
  font-size: 16px;
  opacity: 0.7;
}

.signup-page-button-secondary-link {
  color: var(--primary-color);
}
</style>