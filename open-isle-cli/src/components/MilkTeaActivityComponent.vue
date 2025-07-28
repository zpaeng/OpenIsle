<template>
  <div class="milk-tea-activity">
    <div class="milk-tea-description">
      <div class="milk-tea-description-title">
        <i class="fas fa-info-circle"></i>
        <span class="milk-tea-description-title-text">升级规则说明</span>
      </div>
      <div class="milk-tea-description-content">
        <p>回复帖子每次10exp，最多3次每天</p>
        <p>发布帖子每次30exp，最多1次每天</p>  
        <p>发表情每次5exp，最多3次每天</p>  
      </div>
    </div>
    <div class="milk-tea-status-container">
    <div class="milk-tea-status">
      <div class="status-title">🔥 已兑换奶茶人数</div>
      <ProgressBar :value="info.level1Count" :max="50" />
      <div class="status-text">当前 {{ info.level1Count }} / 50</div>
    </div>
      <div v-if="user" class="user-level">
        <LevelProgress :exp="user.experience" :current-level="user.currentLevel" :next-exp="user.nextLevelExp" />
      </div>
    </div>
    <div v-if="user && user.currentLevel >= 1 && !info.ended" class="redeem-button" @click="openDialog">兑换</div>
    <div v-else class="redeem-button disabled">兑换</div> 
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
import { API_BASE_URL, toast } from '../main'
import { getToken, fetchCurrentUser } from '../utils/auth'

export default {
  name: 'MilkTeaActivityComponent',
  components: { ProgressBar, LevelProgress },
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
.milk-tea-description-title-text {
  font-size: 14px;
  font-weight: bold;
  margin-left: 5px;
}

.milk-tea-description-content {
  font-size: 12px;
  opacity: 0.8;
}

.status-title {
  font-weight: bold;
}

.status-text {
  font-size: 12px;
  opacity: 0.8;
}

.milk-tea-activity {
  margin-top: 20px;
  padding: 20px;
}

.redeem-button {
  margin-top: 20px;
  background-color: var(--primary-color);
  color: #fff;
  padding: 10px 20px;
  border-radius: 10px;
  width: fit-content;
  cursor: pointer;
}

.redeem-button:hover {
  background-color: var(--primary-color-hover);
}

.redeem-button.disabled {
  background-color: var(--primary-color-disabled);
  cursor: not-allowed;
}

.redeem-button.disabled:hover {
  background-color: var(--primary-color-disabled);
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

.milk-tea-status-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 30px;
  margin-top: 20px;
}

.milk-tea-status {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-top: 10px;
  font-size: 14px;
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
