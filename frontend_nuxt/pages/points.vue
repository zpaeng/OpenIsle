<template>
  <div class="point-mall-page">
    <section class="rules">
      <div class="section-title">🎉 积分规则</div>
      <div class="section-content">
        <div class="section-item" v-for="(rule, idx) in pointRules" :key="idx">{{ rule }}</div>
      </div>
    </section>

    <div class="point-info">
      <p v-if="authState.loggedIn && point !== null">
        <span><i class="fas fa-coins coin-icon"></i></span>我的积分：<span class="point-value">{{
          point
        }}</span>
      </p>
    </div>

    <section class="goods">
      <div class="goods-item" v-for="(good, idx) in goods" :key="idx">
        <img class="goods-item-image" :src="good.image" alt="good.name" />
        <div class="goods-item-name">{{ good.name }}</div>
        <div class="goods-item-cost">
          <i class="fas fa-coins"></i>
          {{ good.cost }} 积分
        </div>
        <div class="goods-item-button">兑换</div>
      </div>
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
  {
    name: 'GPT Plus 1 个月',
    cost: 20000,
    image: 'https://openisle-1307107697.cos.ap-guangzhou.myqcloud.com/assert/icons/chatgpt.png',
  },
  {
    name: '奶茶',
    cost: 5000,
    image: 'https://openisle-1307107697.cos.ap-guangzhou.myqcloud.com/assert/icons/coffee.png',
  },
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
  padding-left: 20px;
  max-width: var(--page-max-width);
  background-color: var(--background-color);
  margin: 0 auto;
}

.point-info {
  font-size: 18px;
}

.point-value {
  font-weight: bold;
  color: var(--primary-color);
}

.coin-icon {
  margin-right: 5px;
}

.rules,
.goods {
  margin-top: 20px;
}

.goods {
  display: flex;
  gap: 10px;
}

.goods-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  border: 1px solid var(--normal-border-color);
  border-radius: 10px;
  overflow: hidden;
}

.goods-item-name {
  font-size: 20px;
  font-weight: bold;
}

.goods-item-image {
  width: 200px;
  height: 200px;
  border-bottom: 1px solid var(--normal-border-color);
}

.goods-item-cost {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  opacity: 0.7;
}

.goods-item-button {
  background-color: var(--primary-color);
  color: white;
  padding: 7px 10px;
  border-radius: 10px;
  width: calc(100% - 40px);
  text-align: center;
  cursor: pointer;
  margin-bottom: 10px;
}

.goods-item-button:hover {
  background-color: var(--primary-color-hover);
}

.section-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 10px;
}

.section-content {
  display: flex;
  flex-direction: column;
  font-size: 14px;
  opacity: 0.7;
}
</style>
