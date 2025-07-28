<template>
  <div class="milk-tea-page">
    <div class="milk-tea-status">
      <div class="status-title">已兑换奶茶人数</div>
      <ProgressBar :value="info.level1Count" :max="50" />
      <div class="status-text">{{ info.level1Count }} / 50</div>
    </div>
    <div v-if="user" class="user-level">
      <LevelProgress :exp="user.experience" :current-level="user.currentLevel" :next-exp="user.nextLevelExp" />
    </div>
    <LoginOverlay v-else />
    <div v-if="user && user.currentLevel >= 1 && !info.ended" class="redeem-button" @click="openDialog">
      兑换
    </div>
    <div v-if="dialogVisible" class="redeem-dialog">
      <div class="redeem-dialog-overlay" @click="closeDialog"></div>
      <div class="redeem-dialog-content">
        <input v-model="contact" class="redeem-input" placeholder="联系方式" />
        <div class="redeem-actions">
          <button @click="submitRedeem" :disabled="loading">提交</button>
          <button @click="closeDialog">取消</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import ProgressBar from '../components/ProgressBar.vue'
import LevelProgress from '../components/LevelProgress.vue'
import LoginOverlay from '../components/LoginOverlay.vue'
import { API_BASE_URL, toast } from '../main'
import { getToken, fetchCurrentUser } from '../utils/auth'

export default {
  name: 'MilkTeaActivityPageView',
  components: { ProgressBar, LevelProgress, LoginOverlay },
  data () {
    return {
      info: { level1Count: 0, ended: false },
      user: null,
      dialogVisible: false,
      contact: '',
      loading: false
    }
  },
  async mounted () {
    await this.loadInfo()
    this.user = await fetchCurrentUser()
  },
  methods: {
    async loadInfo () {
      const res = await fetch(`${API_BASE_URL}/api/activities/milk-tea`)
      if (res.ok) {
        this.info = await res.json()
      }
    },
    openDialog () {
      this.dialogVisible = true
    },
    closeDialog () {
      this.dialogVisible = false
    },
    async submitRedeem () {
      if (!this.contact) return
      this.loading = true
      const token = getToken()
      const res = await fetch(`${API_BASE_URL}/api/activities/milk-tea/redeem`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          Authorization: `Bearer ${token}`
        },
        body: JSON.stringify({ contact: this.contact })
      })
      if (res.ok) {
        toast.success('兑换成功！')
        this.dialogVisible = false
        await this.loadInfo()
      } else {
        toast.error('兑换失败')
      }
      this.loading = false
    }
  }
}
</script>

<style scoped>
.milk-tea-page {
  padding: 20px;
  background-color: var(--background-color);
  height: calc(100vh - var(--header-height) - 40px);
}

.milk-tea-status {
  margin-bottom: 20px;
}

.status-title {
  font-weight: bold;
  margin-bottom: 4px;
}

.status-text {
  font-size: 14px;
  margin-top: 4px;
}

.redeem-button {
  margin-top: 20px;
  padding: 6px 12px;
  border-radius: 4px;
  background-color: var(--primary-color);
  color: white;
  width: fit-content;
  cursor: pointer;
}

.redeem-button:hover {
  background-color: var(--primary-color-hover);
}

.redeem-dialog {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 20;
}

.redeem-dialog-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  backdrop-filter: blur(3px);
  -webkit-backdrop-filter: blur(3px);
}

.redeem-dialog-content {
  position: relative;
  z-index: 2;
  background-color: var(--background-color);
  padding: 20px;
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 250px;
}

.redeem-actions {
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
  gap: 10px;
}

.redeem-input {
  padding: 6px;
  border: 1px solid var(--normal-border-color);
  border-radius: 4px;
}
</style>
