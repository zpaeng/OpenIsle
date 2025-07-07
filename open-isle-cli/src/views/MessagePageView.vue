<template>
  <div class="message-page">
    <BaseTimeline :items="notifications">
      <template #item="{ item }">
        <div class="notif-content">
          <span class="notif-type">{{ formatType(item.type) }}</span>
          <span class="notif-time">{{ new Date(item.createdAt).toLocaleString() }}</span>
        </div>
      </template>
    </BaseTimeline>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { API_BASE_URL } from '../main'
import BaseTimeline from '../components/BaseTimeline.vue'

export default {
  name: 'MessagePageView',
  components: { BaseTimeline },
  setup() {
    const notifications = ref([])

    const iconMap = {
      POST_VIEWED: 'fas fa-eye',
      COMMENT_REPLY: 'fas fa-reply',
      REACTION: 'fas fa-thumbs-up',
      POST_REVIEWED: 'fas fa-check',
      POST_UPDATED: 'fas fa-comment-dots',
      USER_ACTIVITY: 'fas fa-user'
    }

    const fetchNotifications = async () => {
      try {
        const res = await fetch(`${API_BASE_URL}/api/notifications`)
        if (!res.ok) return
        const data = await res.json()
        notifications.value = data.map(n => ({ ...n, icon: iconMap[n.type] }))
      } catch (e) {
        console.error(e)
      }
    }

    const formatType = t => {
      switch (t) {
        case 'POST_VIEWED':
          return '帖子被查看'
        case 'COMMENT_REPLY':
          return '有人回复了你'
        case 'REACTION':
          return '有人点赞'
        case 'POST_REVIEWED':
          return '帖子审核结果'
        case 'POST_UPDATED':
          return '关注的帖子有新评论'
        case 'USER_ACTIVITY':
          return '关注的用户有新动态'
        default:
          return t
      }
    }

    onMounted(fetchNotifications)

    return { notifications, formatType }
  }
}
</script>

<style scoped>
.message-page {
  padding: 20px;
}

.notif-content {
  display: flex;
  flex-direction: column;
}

.notif-type {
  font-weight: bold;
}

.notif-time {
  font-size: 12px;
  color: gray;
}
</style>
