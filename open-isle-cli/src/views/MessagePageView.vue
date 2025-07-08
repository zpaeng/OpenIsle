<template>
  <div class="message-page">
    <div v-if="isLoadingMessage" class="loading-message">
      <l-hatch size="28" stroke="4" speed="3.5" color="var(--primary-color)"></l-hatch>
    </div>

    <div v-else-if="notifications.length === 0" class="no-message">
      <i class="fas fa-inbox no-message-icon"></i>
      <div class="no-message-text">
        暂时没有消息 :)
      </div>
    </div>

    <BaseTimeline :items="notifications">
      <template #item="{ item }">
        <div class="notif-content">
          <span class="notif-type">
            <template v-if="item.type === 'COMMENT_REPLY' && item.parentComment">
              <div class="notif-content-container">
                <span class="notif-user">{{ item.comment.author.username }} </span> 对我的评论
                <span>
                  <router-link class="notif-content-text"
                    :to="`/posts/${item.post.id}#comment-${item.parentComment.id}`">
                    {{ sanitizeDescription(item.parentComment.content) }}
                  </router-link>
                </span> 回复了 <span>
                  <router-link class="notif-content-text" :to="`/posts/${item.post.id}#comment-${item.comment.id}`">
                    {{ sanitizeDescription(item.comment.content) }}
                  </router-link>
                </span>
              </div>
            </template>
            <template v-else-if="item.type === 'COMMENT_REPLY' && !item.parentComment">
              <div class="notif-content-container">
                <span class="notif-user">{{ item.comment.author.username }} </span> 对我的文章
                <span>
                  <router-link class="notif-content-text" :to="`/posts/${item.post.id}`">
                    {{ sanitizeDescription(item.post.title) }}
                  </router-link>
                </span> 回复了 <span>
                  <router-link class="notif-content-text" :to="`/posts/${item.post.id}#comment-${item.comment.id}`">
                    {{ sanitizeDescription(item.comment.content) }}
                  </router-link>
                </span>
              </div>
            </template>
            <template v-else>
              <div class="notif-content-container">
                {{ formatType(item.type) }}
              </div>
            </template>
          </span>
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
import { getToken } from '../utils/auth'
import { toast } from '../main'
import { stripMarkdown } from '../utils/markdown'
import { hatch } from 'ldrs'
hatch.register()

export default {
  name: 'MessagePageView',
  components: { BaseTimeline },
  setup() {
    const notifications = ref([])
    const isLoadingMessage = ref(false)

    const iconMap = {
      POST_VIEWED: 'fas fa-eye',
      COMMENT_REPLY: 'fas fa-reply',
      REACTION: 'fas fa-thumbs-up',
      POST_REVIEWED: 'fas fa-check',
      POST_UPDATED: 'fas fa-comment-dots',
      USER_ACTIVITY: 'fas fa-user'
    }

    const sanitizeDescription = (text) => {
      return stripMarkdown(text)
    }

    const fetchNotifications = async () => {
      try {
        const token = getToken()
        if (!token) {
          toast.error('请先登录')
          return
        }
        isLoadingMessage.value = true
        const res = await fetch(`${API_BASE_URL}/api/notifications`, {
          headers: {
            Authorization: `Bearer ${token}`
          }
        })
        isLoadingMessage.value = false
        if (!res.ok) {
          toast.error('获取通知失败')
          return
        }
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

    return { notifications, formatType, sanitizeDescription, isLoadingMessage }
  }
}
</script>

<style scoped>
.loading-message {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}

.no-message {
  display: flex;
  flex-direction: row;
  gap: 10px;
  justify-content: center;
  align-items: center;
  height: 300px;
  opacity: 0.5;
}

.no-message-text {
  font-size: 16px;
  color: var(--text-color);
}

.message-page {
  padding: 20px;
}

.notif-content {
  display: flex;
  flex-direction: column;
  margin-bottom: 30px;
}

.notif-type {
  font-weight: bold;
}

.notif-time {
  font-size: 12px;
  color: gray;
}

.notif-content-container {
  color: rgb(140, 140, 140);
  font-weight: normal;
  font-size: 14px;
  opacity: 0.8;
}

.notif-content-text {
  font-weight: bold;
  color: var(--primary-color) !important;
  text-decoration: none !important;
}

.notif-content-text:hover {
  color: var(--primary-color) !important;
  text-decoration: underline !important;
}

.notif-user {
  font-weight: bold;
  color: var(--text-color);
}
</style>
