<template>
  <div class="reason-page">
    <div class="reason-content">
      <div class="reason-title">请填写注册理由</div>
      <div class="reason-description">
        为了我们社区的良性发展，请填写注册理由，我们将根据你的理由审核你的注册, 谢谢!
      </div>
      <div class="reason-input-container">
        <BaseInput textarea rows="4" v-model="reason" placeholder="20个字以上" ></BaseInput>
        <div class="char-count">{{ reason.length }}/20</div>
      </div>
      <div v-if="error" class="error-message">{{ error }}</div>
      <div v-if="!isWaitingForRegister" class="signup-page-button-primary" @click="submit">提交</div>
      <div v-else class="signup-page-button-primary disabled">提交中...</div>
    </div>
  </div>
</template>

<script>
import BaseInput from '../components/BaseInput.vue'
import { API_BASE_URL, toast } from '../main'
import { googleAuthWithToken } from '../utils/google'

export default {
  name: 'SignupReasonPageView',
  components: { BaseInput },
  data() {
    return {
      reason: '',
      error: '',
      isGoogle: false,
      isWaitingForRegister: false,
      googleToken: ''
    }
  },
  mounted() {
    this.isGoogle = this.$route.query.google === '1'
    if (this.isGoogle) {
      this.googleToken = sessionStorage.getItem('google_id_token') || ''
      if (!this.googleToken) {
        this.$router.push('/signup')
      }
    } else if (!sessionStorage.getItem('signup_username')) {
      this.$router.push('/signup')
    }
  },
  methods: {
    async submit() {
      if (!this.reason || this.reason.length < 20) {
        this.error = '请至少输入20个字'
        return
      }
      if (this.isGoogle) {
        this.isWaitingForRegister = true
        const token = this.googleToken || sessionStorage.getItem('google_id_token')
        if (!token) {
          toast.error('Google 登录失败')
          return
        }
        await googleAuthWithToken(token, this.reason,
          () => { this.$router.push('/') },
          () => { this.error = 'Google 登录失败' }
        )
        this.isWaitingForRegister = false
        sessionStorage.removeItem('google_id_token')
        return
      }
      try {
        this.isWaitingForRegister = true
        const res = await fetch(`${API_BASE_URL}/api/auth/register`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            username: sessionStorage.getItem('signup_username'),
            email: sessionStorage.getItem('signup_email'),
            password: sessionStorage.getItem('signup_password'),
            reason: this.reason
          })
        })
        const data = await res.json()
        if (res.ok) {
          toast.success('验证码已发送，请查收邮箱')
          this.$router.push({ path: '/signup', query: { verify: 1, u: sessionStorage.getItem('signup_username') } })
        } else {
          toast.error(data.error || '发送失败')
        }
      } catch (e) {
        toast.error('发送失败')
      } finally {
        this.isWaitingForRegister = false
      }
    }
  }
}
</script>

<style scoped>
.reason-page {
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: var(--background-color);
  height: calc(100vh - var(--header-height));
}

.reason-title {
  font-size: 24px;
  font-weight: bold;
}

.reason-description {
  font-size: 14px;
}

.reason-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
  width: 400px;
}

.char-count {
  font-size: 12px;
  color: #888;
  width: 100%;
  text-align: right;
}

.error-message {
  color: red;
  font-size: 14px;
}

.signup-page-button-primary {
  background-color: var(--primary-color);
  color: white;
  padding: 10px 20px;
  border-radius: 10px;
  text-align: center;
  cursor: pointer;
}

.signup-page-button-primary:hover {
  background-color: var(--primary-color-hover);
}

.signup-page-button-primary.disabled {
  background-color: var(--primary-color-disabled);
}

.signup-page-button-primary.disabled:hover {
  cursor: not-allowed;
}
</style>
