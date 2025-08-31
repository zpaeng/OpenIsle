<template>
  <div class="post-poll-container">
    <div class="poll-top-container">
      <div class="poll-options-container">
        <div v-if="showPollResult || pollEnded || hasVoted">
          <div v-for="(opt, idx) in poll.options" :key="idx" class="poll-option-result">
            <div class="poll-option-info-container">
              <div class="poll-option-text">{{ opt }}</div>
              <div class="poll-option-progress-info">
                {{ pollPercentages[idx] }}% ({{ pollVotes[idx] || 0 }}人已投票)
              </div>
            </div>
            <div class="poll-option-progress">
              <div
                class="poll-option-progress-bar"
                :style="{ width: pollPercentages[idx] + '%' }"
              ></div>
            </div>
            <div class="poll-participants">
              <BaseImage
                v-for="p in pollOptionParticipants[idx] || []"
                :key="p.id"
                class="poll-participant-avatar"
                :src="p.avatar"
                alt="avatar"
                @click="gotoUser(p.id)"
              />
            </div>
          </div>
        </div>
        <div v-else>
          <div
            v-for="(opt, idx) in poll.options"
            :key="idx"
            class="poll-option"
            @click="voteOption(idx)"
          >
            <input type="radio" :checked="false" name="poll-option" class="poll-option-input" />
            <span class="poll-option-text">{{ opt }}</span>
          </div>
        </div>
      </div>
      <div class="poll-info">
        <div class="total-votes">{{ pollParticipants.length }}</div>
        <div class="total-votes-title">投票人</div>
      </div>
    </div>
    <div class="poll-bottom-container">
      <div
        v-if="showPollResult && !pollEnded && !hasVoted"
        class="poll-option-button"
        @click="showPollResult = false"
      >
        <i class="fas fa-chevron-left"></i> 投票
      </div>
      <div
        v-else-if="!pollEnded && !hasVoted"
        class="poll-option-button"
        @click="showPollResult = true"
      >
        <i class="fas fa-chart-bar"></i> 结果
      </div>

      <div class="poll-left-time">
        <div class="poll-left-time-title">离结束还有</div>
        <div class="poll-left-time-value">{{ countdown }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onBeforeUnmount } from 'vue'
import { toast } from '~/main'
import { getToken, authState } from '~/utils/auth'

const props = defineProps({
  poll: {
    type: Object,
    required: true,
  },
  postId: {
    type: [String, Number],
    required: true,
  },
})

const emit = defineEmits(['refresh'])

const config = useRuntimeConfig()
const API_BASE_URL = config.public.apiBaseUrl

const showPollResult = ref(false)

const pollParticipants = computed(() => props.poll?.participants || [])
const pollOptionParticipants = computed(() => props.poll?.optionParticipants || {})
const pollVotes = computed(() => props.poll?.votes || {})
const totalPollVotes = computed(() => Object.values(pollVotes.value).reduce((a, b) => a + b, 0))
const pollPercentages = computed(() =>
  props.poll
    ? props.poll.options.map((_, idx) => {
        const c = pollVotes.value[idx] || 0
        return totalPollVotes.value ? ((c / totalPollVotes.value) * 100).toFixed(1) : 0
      })
    : [],
)
const pollEnded = computed(() => {
  if (!props.poll || !props.poll.endTime) return false
  return new Date(props.poll.endTime).getTime() <= Date.now()
})
const hasVoted = computed(() => {
  if (!authState.loggedIn) return false
  return pollParticipants.value.some((p) => p.id === Number(authState.userId))
})

watch([hasVoted, pollEnded], ([voted, ended]) => {
  if (voted || ended) showPollResult.value = true
})

const countdown = ref('00:00:00')
let countdownTimer = null

const updateCountdown = () => {
  if (!props.poll || !props.poll.endTime) {
    countdown.value = '00:00:00'
    return
  }
  const diff = new Date(props.poll.endTime).getTime() - Date.now()
  if (diff <= 0) {
    countdown.value = '00:00:00'
    if (countdownTimer) {
      clearInterval(countdownTimer)
      countdownTimer = null
    }
    return
  }
  const h = String(Math.floor(diff / 3600000)).padStart(2, '0')
  const m = String(Math.floor((diff % 3600000) / 60000)).padStart(2, '0')
  const s = String(Math.floor((diff % 60000) / 1000)).padStart(2, '0')
  countdown.value = `${h}:${m}:${s}`
}

const startCountdown = () => {
  if (!import.meta.client) return
  if (countdownTimer) clearInterval(countdownTimer)
  updateCountdown()
  countdownTimer = setInterval(updateCountdown, 1000)
}

watch(
  () => props.poll?.endTime,
  () => {
    if (props.poll && props.poll.endTime) {
      startCountdown()
    }
  },
  { immediate: true },
)

onBeforeUnmount(() => {
  if (countdownTimer) clearInterval(countdownTimer)
})

const gotoUser = (id) => navigateTo(`/users/${id}`, { replace: true })

const voteOption = async (idx) => {
  const token = getToken()
  if (!token) {
    toast.error('请先登录')
    return
  }
  const res = await fetch(`${API_BASE_URL}/api/posts/${props.postId}/poll/vote?option=${idx}`, {
    method: 'POST',
    headers: { Authorization: `Bearer ${token}` },
  })
  const data = await res.json().catch(() => ({}))
  if (res.ok) {
    toast.success('投票成功')
    emit('refresh')
    showPollResult.value = true
  } else {
    toast.error(data.error || '操作失败')
  }
}
</script>

<style scoped>
.poll-option-button {
  color: var(--text-color);
  padding: 5px 10px;
  border-radius: 8px;
  background-color: var(--poll-option-button-background-color);
  cursor: pointer;
  width: fit-content;
}

.poll-top-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  border-bottom: 1px solid var(--normal-border-color);
}

.poll-options-container {
  display: flex;
  flex-direction: column;
  overflow-y: auto;
  flex: 4;
}

.poll-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  min-height: 100px;
}

.total-votes {
  font-size: 40px;
  font-weight: bold;
  opacity: 0.8;
}

.total-votes-title {
  font-size: 18px;
  opacity: 0.5;
}

.poll-option {
  margin-bottom: 10px;
  margin-right: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.poll-option-result {
  margin-bottom: 10px;
  margin-right: 10px;
  gap: 5px;
  display: flex;
  flex-direction: column;
}

.poll-option-input {
  margin-right: 10px;
  width: 18px;
  height: 18px;
  accent-color: var(--primary-color);
  border-radius: 50%;
  border: 2px solid var(--primary-color);
}

.poll-option-text {
  font-size: 18px;
}

.poll-bottom-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: space-between;
}

.poll-left-time {
  display: flex;
  flex-direction: row;
}

.poll-left-time-title {
  font-size: 13px;
  opacity: 0.7;
  margin-right: 5px;
}

.poll-left-time-value {
  font-size: 13px;
  font-weight: bold;
  color: var(--primary-color);
}

.post-poll-container {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  background-color: var(--lottery-background-color);
  border-radius: 10px;
  padding: 10px;
}

.poll-question {
  font-weight: bold;
  margin-bottom: 10px;
}

.poll-option-progress {
  position: relative;
  background-color: rgb(187, 187, 187);
  height: 20px;
  border-radius: 5px;
  overflow: hidden;
}

.poll-option-progress-bar {
  background-color: var(--primary-color);
  height: 100%;
}

.poll-option-info-container {
  display: flex;
  flex-direction: row;
  justify-content: space-between;
}

.poll-option-progress-info {
  font-size: 12px;
  line-height: 20px;
  color: var(--text-color);
}

.poll-vote-button {
  margin-top: 5px;
  color: var(--primary-color);
  cursor: pointer;
  width: fit-content;
}

.poll-participants {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
}

.poll-participant-avatar {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  cursor: pointer;
}
</style>
