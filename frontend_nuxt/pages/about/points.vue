<template>
  <div class="point-mall-page">
    <p v-if="authState.loggedIn && point !== null">我的积分：{{ point }}</p>
    <p v-else>请先登录以查看积分</p>

    <section class="rules">
      <h2>积分规则</h2>
      <ul>
        <li v-for="(rule, idx) in pointRules" :key="idx">{{ rule }}</li>
      </ul>
    </section>

    <section class="goods">
      <h2>积分兑换商品</h2>
      <ul>
        <li v-for="(good, idx) in goods" :key="idx">{{ good.name }} - {{ good.cost }} 积分</li>
      </ul>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { authState, fetchCurrentUser } from '~/utils/auth'

const point = ref(null)

const pointRules = [
  '发帖：每天前两次，每次 30 积分',
  '评论：每天前四条评论可获 10 积分，你的帖子被评论也可获 10 积分',
  '帖子被点赞：每次 10 积分',
  '评论被点赞：每次 10 积分',
]

const goods = [
  { name: 'GPT Plus for 1 month', cost: 20000 },
  { name: '奶茶', cost: 5000 },
]

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

.rules,
.goods {
  margin-top: 20px;
}
</style>
