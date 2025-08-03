<template>
  <div class="google-callback-page">
    <l-hatch size="28" stroke="4" speed="3.5" color="var(--primary-color)"></l-hatch>
    <div class="google-callback-page-text">Magic is happening...</div>
  </div>
</template>

<script>
import { googleAuthWithToken } from '../utils/google'
import { hatch } from 'ldrs'
hatch.register()

export default {
  name: 'GoogleCallbackPageView',
  async mounted() {
    const hash = new URLSearchParams(window.location.hash.substring(1))
    const idToken = hash.get('id_token')
    if (idToken) {
      await googleAuthWithToken(idToken, () => {
        this.$router.push('/')
      }, token => {
        this.$router.push('/signup-reason?token=' + token)
      })
    } else {
      this.$router.push('/login')
    }
  }
}
</script>

<style scoped>
.google-callback-page {
  background-color: var(--background-color);
  height: calc(100vh - var(--header-height));
  padding-top: var(--header-height);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
}

.google-callback-page-text {
  margin-top: 25px;
  font-size: 16px;
  color: var(--primary-color);
  font-weight: bold;
}
</style>
