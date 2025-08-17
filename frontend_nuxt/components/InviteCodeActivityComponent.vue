<template>
  <div class="invite-code-activity">
    <div class="invite-code-description">
      <div class="invite-code-description-title">
        <i class="fas fa-info-circle"></i>
        <span class="invite-code-description-title-text">邀请规则说明</span>
      </div>
      <div class="invite-code-description-content">
        <p>邀请好友注册并登录，每次可以获得500积分</p>
        <p>邀请链接的有效期为1个月</p>
        <p>每一个邀请链接的邀请人数上限为3人</p>
        <p>通过邀请链接注册，无需注册审核</p>
        <p>每人每天仅能生产3个邀请链接</p>
      </div>
    </div>

    <div v-if="inviteCode" class="invite-code-link-content">
      <p>
        邀请链接：https://openisle.com/signup?invite_token=1234567890
        <span> <i class="fas fa-copy copy-icon"></i> </span>
      </p>
    </div>

    <div class="generate-button">生成邀请链接</div>
  </div>
</template>

<script setup>
import { fetchCurrentUser } from '~/utils/auth'

const user = ref(null)
const isLoadingUser = ref(true)

onMounted(async () => {
  isLoadingUser.value = true
  user.value = await fetchCurrentUser()
  isLoadingUser.value = false
})
</script>

<style scoped>
.invite-code-description-title-text {
  font-size: 14px;
  font-weight: bold;
  margin-left: 5px;
}

.invite-code-description-content {
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

.invite-code-activity {
  margin-top: 20px;
  padding: 20px;
}

.generate-button {
  margin-top: 20px;
  background-color: var(--primary-color);
  color: #fff;
  padding: 8px 16px;
  border-radius: 10px;
  width: fit-content;
  cursor: pointer;
}

.generate-button:hover {
  background-color: var(--primary-color-hover);
}

.generate-button.disabled {
  background-color: var(--primary-color-disabled);
  cursor: not-allowed;
}

.generate-button.disabled:hover {
  background-color: var(--primary-color-disabled);
}

.invite-code-status-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 30px;
  margin-top: 20px;
}

.invite-code-status {
  display: flex;
  flex-direction: column;
  gap: 4px;
  margin-top: 10px;
  font-size: 14px;
}

.user-level-text {
  opacity: 0.8;
  font-size: 12px;
  color: var(--primary-color);
}

.invite-code-link-content {
  margin-top: 20px;
  font-size: 12px;
  opacity: 0.8;
}

.copy-icon {
  cursor: pointer;
  margin-left: 5px;
}

@media screen and (max-width: 768px) {
  .invite-code-status-container {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
}
</style>
