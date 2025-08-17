<template>
  <div class="point-mall-page">
    <section class="rules">
      <div class="section-title">🎉 积分规则</div>
      <div class="section-content">
        <div class="section-item" v-for="(rule, idx) in pointRules" :key="idx">{{ rule }}</div>
      </div>
    </section>

    <div class="loading-points-container" v-if="isLoading">
      <l-hatch size="28" stroke="4" speed="3.5" color="var(--primary-color)"></l-hatch>
    </div>

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
        <div
          class="goods-item-button"
          :class="{ disabled: !authState.loggedIn || point === null || point < good.cost }"
          @click="openRedeem(good)"
        >
          兑换
        </div>
      </div>
    </section>
    <RedeemPopup
      :visible="dialogVisible"
      v-model="contact"
      :loading="loading"
      @close="closeRedeem"
      @submit="submitRedeem"
    />
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { authState, fetchCurrentUser, getToken } from '~/utils/auth'
import { toast } from '~/main'
import RedeemPopup from '~/components/RedeemPopup.vue'

const config = useRuntimeConfig()
const API_BASE_URL = config.public.apiBaseUrl

const point = ref(null)
const isLoading = ref(false)

const pointRules = [
  '发帖：每天前两次，每次 30 积分',
  '评论：每天前四条评论可获 10 积分，你的帖子被评论也可获 10 积分',
  '帖子被点赞：每次 10 积分',
  '评论被点赞：每次 10 积分',
  '邀请好友加入可获得 500 积分/次，注意需要使用邀请链接注册',
]

const goods = ref([])
const dialogVisible = ref(false)
const contact = ref('')
const loading = ref(false)
const selectedGood = ref(null)

onMounted(async () => {
  isLoading.value = true
  if (authState.loggedIn) {
    const user = await fetchCurrentUser()
    point.value = user ? user.point : null
  }
  await loadGoods()
  isLoading.value = false
})

const loadGoods = async () => {
  const res = await fetch(`${API_BASE_URL}/api/point-goods`)
  if (res.ok) {
    goods.value = await res.json()
  }
}

const openRedeem = (good) => {
  if (!authState.loggedIn || point.value === null || point.value < good.cost) {
    toast.error('积分不足')
    return
  }
  selectedGood.value = good
  dialogVisible.value = true
}

const closeRedeem = () => {
  dialogVisible.value = false
}

const submitRedeem = async () => {
  if (!selectedGood.value || !contact.value) return
  loading.value = true
  const token = getToken()
  const res = await fetch(`${API_BASE_URL}/api/point-goods/redeem`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify({ goodId: selectedGood.value.id, contact: contact.value }),
  })
  if (res.ok) {
    const data = await res.json()
    point.value = data.point
    toast.success('兑换成功！')
    dialogVisible.value = false
    contact.value = ''
  } else {
    toast.error('兑换失败')
  }
  loading.value = false
}
</script>

<style scoped>
.point-mall-page {
  padding-left: 20px;
  max-width: var(--page-max-width);
  background-color: var(--background-color);
  margin: 0 auto;
}

.loading-points-container {
  margin-top: 100px;
  display: flex;
  justify-content: center;
  align-items: center;
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

.goods-item-button.disabled,
.goods-item-button.disabled:hover {
  background-color: var(--primary-color-disabled);
  cursor: not-allowed;
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
