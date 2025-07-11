<template>
  <div class="reactions-container">
    <div class="reactions-viewer">
      <div class="reactions-viewer-item-container" @click="openPanel" @mouseenter="cancelHide"
        @mouseleave="scheduleHide">
        <template v-if="displayedReactions.length">
          <div v-for="r in displayedReactions" :key="r.type" class="reactions-viewer-item">{{ iconMap[r.type] }}</div>
          <div class="reactions-count">{{ totalCount }}</div>
        </template>
        <div v-else class="reactions-viewer-item placeholder">
          <i class="far fa-smile"></i>
          <span class="reactions-viewer-item-placeholder-text">点击以表态</span>
        </div>
      </div>
    </div>
    <div class="make-reaction-container">
      <div class="make-reaction-item like-reaction" @click="toggleReaction('LIKE')"
        :class="{ selected: userReacted('LIKE') }">
        <i class="far fa-heart"></i>
        <span class="reactions-count" v-if="likeCount">{{ likeCount }}</span>
      </div>
      <slot></slot>
    </div>
    <div v-if="panelVisible" class="reactions-panel" @mouseenter="cancelHide" @mouseleave="scheduleHide">
      <div v-for="t in panelTypes" :key="t" class="reaction-option" @click="toggleReaction(t)"
        :class="{ selected: userReacted(t) }">
        {{ iconMap[t] }}<span v-if="counts[t]">{{ counts[t] }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch, onMounted } from 'vue'
import { API_BASE_URL, toast } from '../main'
import { getToken, authState } from '../utils/auth'

let cachedTypes = null
const fetchTypes = async () => {
  if (cachedTypes) return cachedTypes
  try {
    const token = getToken()
    const res = await fetch(`${API_BASE_URL}/api/reaction-types`, {
      headers: { Authorization: `Bearer ${token}` }
    })
    if (res.ok) {
      cachedTypes = await res.json()
    } else {
      cachedTypes = []
    }
  } catch {
    cachedTypes = []
  }
  return cachedTypes
}

const iconMap = {
  LIKE: '❤️',
  DISLIKE: '👎',
  RECOMMEND: '👏',
  ANGRY: '😡'
}

export default {
  name: 'ReactionsGroup',
  props: {
    modelValue: { type: Array, default: () => [] },
    contentType: { type: String, required: true },
    contentId: { type: [Number, String], required: true }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const reactions = ref(props.modelValue)
    watch(() => props.modelValue, v => reactions.value = v)

    const reactionTypes = ref([])
    onMounted(async () => {
      reactionTypes.value = await fetchTypes()
    })

    const counts = computed(() => {
      const c = {}
      for (const r of reactions.value) {
        c[r.type] = (c[r.type] || 0) + 1
      }
      return c
    })

    const totalCount = computed(() => Object.values(counts.value).reduce((a, b) => a + b, 0))
    const likeCount = computed(() => counts.value['LIKE'] || 0)

    const userReacted = type => reactions.value.some(r => r.type === type && r.user === authState.username)

    const displayedReactions = computed(() => {
      return Object.entries(counts.value)
        .sort((a, b) => b[1] - a[1])
        .slice(0, 3)
        .map(([type]) => ({ type }))
    })

    const panelTypes = computed(() => reactionTypes.value.filter(t => t !== 'LIKE'))

    const panelVisible = ref(false)
    let hideTimer = null
    const openPanel = () => {
      clearTimeout(hideTimer)
      panelVisible.value = true
    }
    const scheduleHide = () => {
      clearTimeout(hideTimer)
      hideTimer = setTimeout(() => { panelVisible.value = false }, 2000)
    }
    const cancelHide = () => {
      clearTimeout(hideTimer)
    }

    const toggleReaction = async (type) => {
      const token = getToken()
      if (!token) {
        toast.error('请先登录')
        return
      }
      try {
        const url = props.contentType === 'post'
          ? `${API_BASE_URL}/api/posts/${props.contentId}/reactions`
          : `${API_BASE_URL}/api/comments/${props.contentId}/reactions`
        const res = await fetch(url, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
          body: JSON.stringify({ type })
        })
        if (res.ok) {
          if (res.status === 204) {
            const idx = reactions.value.findIndex(r => r.type === type && r.user === authState.username)
            if (idx > -1) reactions.value.splice(idx, 1)
          } else {
            const data = await res.json()
            reactions.value.push(data)
          }
          emit('update:modelValue', reactions.value)
        } else {
          toast.error('操作失败')
        }
      } catch (e) {
        toast.error('操作失败')
      }
    }

    return {
      iconMap,
      counts,
      totalCount,
      likeCount,
      displayedReactions,
      panelTypes,
      panelVisible,
      openPanel,
      scheduleHide,
      cancelHide,
      toggleReaction,
      userReacted
    }
  }
}
</script>

<style>
.reactions-container {
  position: relative;
  display: flex;
  flex-direction: row;
  gap: 10px;
  align-items: center;
  width: 100%;
  justify-content: space-between;
}

.reactions-viewer {
  display: flex;
  flex-direction: row;
  gap: 20px;
  align-items: center;
}

.reactions-viewer-item-container {
  display: flex;
  flex-direction: row;
  gap: 2px;
  align-items: center;
  cursor: pointer;
  color: #a2a2a2;
}

.reactions-viewer-item {
  font-size: 16px;
}

.reactions-viewer-item.placeholder {
  opacity: 0.5;
  display: flex;
  flex-direction: row;
  align-items: center;
}

.reactions-viewer-item-placeholder-text {
  font-size: 14px;
  padding-left: 5px;
}

.make-reaction-container {
  display: flex;
  flex-direction: row;
  gap: 10px;
}

.make-reaction-item {
  cursor: pointer;
  padding: 10px;
  opacity: 0.5;
  border-radius: 8px;
  font-size: 20px;
}

.like-reaction {
  color: #ff0000;
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 5px;
}

.make-reaction-item:hover {
  background-color: #ffe2e2;
}

.reactions-count {
  font-size: 16px;
  font-weight: bold;
}

.reactions-panel {
  position: absolute;
  bottom: 40px;
  left: -20px;
  background-color: var(--background-color);
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 5px;
  display: flex;
  flex-direction: row;
  gap: 5px;
  z-index: 10;
}

.reaction-option {
  cursor: pointer;
  padding: 5px;
  border-radius: 4px;
}

.reaction-option.selected {
  background-color: #e2e2e2;
}
</style>
