<template>
  <div class="loading">GitHub 登录中...</div>
</template>

<script>
import { githubExchange } from '../utils/github'

export default {
  name: 'GithubCallbackPageView',
  async mounted() {
    const url = new URL(window.location.href)
    const code = url.searchParams.get('code')
    const state = url.searchParams.get('state')
    const result = await githubExchange(code, state, '')
    if (result === 'NEED_REASON') {
      this.$router.push('/signup-reason?github=1')
    } else {
      this.$router.push('/')
    }
  }
}
</script>

<style scoped>
.loading {
  height: calc(100vh - var(--header-height));
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
}
</style>
