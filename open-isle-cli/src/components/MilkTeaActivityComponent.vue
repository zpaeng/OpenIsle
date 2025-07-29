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
    <BasePopup :visible="dialogVisible" @close="closeDialog">
        <div class="redeem-dialog-content">
          <BaseInput textarea="" rows="5" v-model="contact" placeholder="联系方式 (手机号/Email/微信/instagram/telegram等, 务必注明来源)" />
          <div class="redeem-actions">
            <div class="redeem-submit-button" @click="submitRedeem" :disabled="loading">提交</div>
            <div class="redeem-cancel-button" @click="closeDialog">取消</div>
          </div>
        </div>
      </BasePopup>
  </div>
</template>

<script>
import ProgressBar from '../components/ProgressBar.vue'
import LevelProgress from '../components/LevelProgress.vue'
import BaseInput from './BaseInput.vue'
import BasePopup from './BasePopup.vue'
import { API_BASE_URL, toast } from '../main'
import { getToken, fetchCurrentUser } from '../utils/auth'

export default {
  name: 'MilkTeaActivityComponent',
  components: { ProgressBar, LevelProgress, BaseInput, BasePopup },
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
  padding: 8px 16px;
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
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  gap: 10px;
  min-width: 400px;
}

.redeem-actions {
  margin-top: 10px;
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
  gap: 20px;
  align-items: center;
}

.redeem-submit-button {
  background-color: var(--primary-color);
  color: #fff;
  padding: 10px 20px;
  border-radius: 10px;
  cursor: pointer;
}
.redeem-submit-button:disabled {
  background-color: var(--primary-color-disabled);
  cursor: not-allowed;
}
.redeem-submit-button:hover {
  background-color: var(--primary-color-hover);
}
.redeem-submit-button:disabled:hover {
  background-color: var(--primary-color-disabled);
}
.redeem-cancel-button {
  color: var(--primary-color);
  border-radius: 10px;
  cursor: pointer;
}
.redeem-cancel-button:hover {
  text-decoration: underline;
}


@media screen and (max-width: 768px) {
  .milk-tea-status-container { 
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .redeem-dialog-content {
    min-width: 300px;
  }
}

</style>

