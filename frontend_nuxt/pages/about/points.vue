<template>
  <div class="point-mall-page">
    <p v-if="authState.loggedIn && point !== null">我的积分：{{ point }}</p>
    <p v-else>请先登录以查看积分</p>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { authState, fetchCurrentUser } from '~/utils/auth'

const point = ref(null)

onMounted(async () => {
  if (authState.loggedIn) {
    const user = await fetchCurrentUser()
    point.value = user ? user.point : null
  }
})
</script>

<style scoped>
.point-mall-page {
  padding: 20px;
  max-width: var(--page-max-width);
  background-color: var(--background-color);
  margin: 0 auto;
}
</style>
