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
        <div class="notif-content" :class="{ read: item.read }">
          <span v-if="!item.read" class="unread-dot"></span>
          <span class="notif-type">
            <template v-if="item.type === 'COMMENT_REPLY' && item.parentComment">
              <div class="notif-content-container">
                <span class="notif-user">{{ item.comment.author.username }} </span> 对我的评论
                <span>
                  <router-link class="notif-content-text" @click="markRead(item.id)"
                    :to="`/posts/${item.post.id}#comment-${item.parentComment.id}`">
                    {{ sanitizeDescription(item.parentComment.content) }}
                  </router-link>
                </span> 回复了 <span>
                  <router-link class="notif-content-text" @click="markRead(item.id)" :to="`/posts/${item.post.id}#comment-${item.comment.id}`">
                    {{ sanitizeDescription(item.comment.content) }}
                  </router-link>
                </span>
              </div>
            </template>
            <template v-else-if="item.type === 'COMMENT_REPLY' && !item.parentComment">
              <div class="notif-content-container">
                <span class="notif-user">{{ item.comment.author.username }} </span> 对我的文章
                <span>
                  <router-link class="notif-content-text" @click="markRead(item.id)" :to="`/posts/${item.post.id}`">
                    {{ sanitizeDescription(item.post.title) }}
                  </router-link>
                </span> 回复了 <span>
                  <router-link class="notif-content-text" @click="markRead(item.id)" :to="`/posts/${item.post.id}#comment-${item.comment.id}`">
                    {{ sanitizeDescription(item.comment.content) }}
                  </router-link>
                </span>
              </div>
            </template>
            <template v-else-if="item.type === 'REACTION' && item.post && !item.comment">
              <div class="notif-content-container">
                <span class="notif-user">{{ item.fromUser.username }} </span> 对我的文章
                <span>
                  <router-link class="notif-content-text" @click="markRead(item.id)" :to="`/posts/${item.post.id}`">
                    {{ sanitizeDescription(item.post.title) }}
                  </router-link>
                </span>
                进行了表态
              </div>
            </template>
            <template v-else-if="item.type === 'REACTION' && item.comment">
              <div class="notif-content-container">
                <span class="notif-user">{{ item.fromUser.username }} </span> 对我的评论
                <span>
                  <router-link class="notif-content-text" @click="markRead(item.id)" :to="`/posts/${item.post.id}#comment-${item.comment.id}`">
                    {{ sanitizeDescription(item.comment.content) }}
                  </router-link>
                </span>
                进行了表态
              </div>
            </template>
            <template v-else-if="item.type === 'POST_VIEWED'">
              <div class="notif-content-container">
                <router-link class="notif-content-text" @click="markRead(item.id)" :to="`/users/${item.fromUser.id}`">
                  {{ item.fromUser.username }}
                </router-link>
                查看了您的帖子
                <router-link class="notif-content-text" @click="markRead(item.id)" :to="`/posts/${item.post.id}`">
                  {{ sanitizeDescription(item.post.title) }}
                </router-link>
              </div>
            </template>
            <template v-else-if="item.type === 'POST_UPDATED'">
              <div class="notif-content-container">
                您关注的帖子
                <router-link class="notif-content-text" @click="markRead(item.id)" :to="`/posts/${item.post.id}`">
                  {{ sanitizeDescription(item.post.title) }}
                </router-link>
                下面有新评论
                <router-link class="notif-content-text" @click="markRead(item.id)" :to="`/posts/${item.post.id}#comment-${item.comment.id}`">
                  {{ sanitizeDescription(item.comment.content) }}
                </router-link>
              </div>
            </template>
            <template v-else-if="item.type === 'USER_FOLLOWED'">
              <div class="notif-content-container">
                <router-link class="notif-content-text" @click="markRead(item.id)" :to="`/users/${item.fromUser.id}`">
                  {{ item.fromUser.username }}
                </router-link>
                开始关注你了
              </div>
            </template>
            <template v-else-if="item.type === 'USER_UNFOLLOWED'">
              <div class="notif-content-container">
                <router-link class="notif-content-text" @click="markRead(item.id)" :to="`/users/${item.fromUser.id}`">
                  {{ item.fromUser.username }}
                </router-link>
                取消关注你了
              </div>
            </template>
            <template v-else-if="item.type === 'FOLLOWED_POST'">
              <div class="notif-content-container">
                你关注的
                <router-link class="notif-content-text" @click="markRead(item.id)" :to="`/users/${item.fromUser.id}`">
                  {{ item.fromUser.username }}
                </router-link>
                发布了文章
                <router-link class="notif-content-text" @click="markRead(item.id)" :to="`/posts/${item.post.id}`">
                  {{ sanitizeDescription(item.post.title) }}
                </router-link>
              </div>
            </template>
            <template v-else-if="item.type === 'POST_SUBSCRIBED'">
              <div class="notif-content-container">
                <router-link class="notif-content-text" @click="markRead(item.id)" :to="`/users/${item.fromUser.id}`">
                  {{ item.fromUser.username }}
                </router-link>
                订阅了你的文章
                <router-link class="notif-content-text" @click="markRead(item.id)" :to="`/posts/${item.post.id}`">
                  {{ sanitizeDescription(item.post.title) }}
                </router-link>
              </div>
            </template>
            <template v-else-if="item.type === 'POST_UNSUBSCRIBED'">
              <div class="notif-content-container">
                <router-link class="notif-content-text" @click="markRead(item.id)" :to="`/users/${item.fromUser.id}`">
                  {{ item.fromUser.username }}
                </router-link>
                取消订阅了你的文章
                <router-link class="notif-content-text" @click="markRead(item.id)" :to="`/posts/${item.post.id}`">
                  {{ sanitizeDescription(item.post.title) }}
                </router-link>
              </div>
            </template>
            <template v-else>
              <div class="notif-content-container">
                {{ formatType(item.type) }}
              </div>
            </template>
          </span>
          <span class="notif-time">{{ TimeManager.format(item.createdAt) }}</span>
        </div>
      </template>
    </BaseTimeline>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { API_BASE_URL } from '../main'
import BaseTimeline from '../components/BaseTimeline.vue'
import { getToken } from '../utils/auth'
import { markNotificationsRead } from '../utils/notification'
import { toast } from '../main'
import { stripMarkdown } from '../utils/markdown'
import TimeManager from '../utils/time'
import { hatch } from 'ldrs'
hatch.register()

export default {
  name: 'MessagePageView',
  components: { BaseTimeline },
  setup() {
    const router = useRouter()
    const notifications = ref([])
    const isLoadingMessage = ref(false)

    const markRead = async id => {
      if (!id) return
      const ok = await markNotificationsRead([id])
      if (ok) {
        const n = notifications.value.find(n => n.id === id)
        if (n) n.read = true
      }
    }

    const iconMap = {
      POST_VIEWED: 'fas fa-eye',
      COMMENT_REPLY: 'fas fa-reply',
      POST_REVIEWED: 'fas fa-check',
      POST_UPDATED: 'fas fa-comment-dots',
      USER_ACTIVITY: 'fas fa-user',
      FOLLOWED_POST: 'fas fa-feather-alt',
      USER_FOLLOWED: 'fas fa-user-plus',
      USER_UNFOLLOWED: 'fas fa-user-minus',
      POST_SUBSCRIBED: 'fas fa-bookmark',
      POST_UNSUBSCRIBED: 'fas fa-bookmark'
    }

    const reactionEmojiMap = {
      LIKE: '❤️',
      DISLIKE: '👎',
      RECOMMEND: '👏',
      ANGRY: '😡'
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

          for (const n of data) {
            if (n.type === 'COMMENT_REPLY') {
              notifications.value.push({
                ...n,
                src: n.comment.author.avatar,
                iconClick: () => {
                  markRead(n.id)
                  router.push(`/users/${n.comment.author.id}`)
                }
              })
            } else if (n.type === 'REACTION') {
              notifications.value.push({
                ...n,
                emoji: reactionEmojiMap[n.reactionType],
                iconClick: () => {
                  if (n.fromUser) {
                    markRead(n.id)
                    router.push(`/users/${n.fromUser.id}`)
                  }
                }
              })
            } else if (n.type === 'POST_VIEWED') {
              notifications.value.push({
                ...n,
                src: n.fromUser ? n.fromUser.avatar : null,
                icon: n.fromUser ? undefined : iconMap[n.type],
                iconClick: () => {
                  if (n.fromUser) {
                    markRead(n.id)
                    router.push(`/users/${n.fromUser.id}`)
                  }
                }
              })
            } else if (n.type === 'POST_UPDATED') {
              notifications.value.push({
                ...n,
                src: n.comment.author.avatar,
                iconClick: () => {
                  markRead(n.id)
                  router.push(`/users/${n.comment.author.id}`)
                }
              })
            } else if (n.type === 'USER_FOLLOWED' || n.type === 'USER_UNFOLLOWED') {
              notifications.value.push({
                ...n,
                icon: iconMap[n.type],
                iconClick: () => {
                  if (n.fromUser) {
                    markRead(n.id)
                    router.push(`/users/${n.fromUser.id}`)
                  }
                }
              })
            } else if (n.type === 'FOLLOWED_POST') {
              notifications.value.push({
                ...n,
                icon: iconMap[n.type],
                iconClick: () => {
                  if (n.post) {
                    markRead(n.id)
                    router.push(`/posts/${n.post.id}`)
                  }
                }
              })
            } else if (n.type === 'POST_SUBSCRIBED' || n.type === 'POST_UNSUBSCRIBED') {
              notifications.value.push({
                ...n,
                icon: iconMap[n.type],
                iconClick: () => {
                  if (n.post) {
                    markRead(n.id)
                    router.push(`/posts/${n.post.id}`)
                  }
                }
              })
            } else {
              notifications.value.push({
                ...n,
                icon: iconMap[n.type],
              })
            }
          }
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
        case 'FOLLOWED_POST':
          return '关注的用户发布了新文章'
        case 'POST_SUBSCRIBED':
          return '有人订阅了你的文章'
        case 'POST_UNSUBSCRIBED':
          return '有人取消订阅你的文章'
        case 'USER_FOLLOWED':
          return '有人关注了你'
        case 'USER_UNFOLLOWED':
          return '有人取消关注你'
        case 'USER_ACTIVITY':
          return '关注的用户有新动态'
        default:
          return t
      }
    }

    onMounted(fetchNotifications)

    return { notifications, formatType, sanitizeDescription, isLoadingMessage, markRead, TimeManager }
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
  background-color: var(--background-color);
  padding: 20px;
  height: calc(100vh - var(--header-height) - 40px);
  overflow-y: auto;
}

.notif-content {
  display: flex;
  flex-direction: column;
  margin-bottom: 30px;
  position: relative;
}

.notif-content.read {
  opacity: 0.7;
}

.unread-dot {
  position: absolute;
  left: -10px;
  top: 4px;
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #ff4d4f;
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
