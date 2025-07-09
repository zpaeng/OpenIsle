<template>
  <div class="profile-page">
    <div v-if="isLoading" class="loading-page">
      <l-hatch size="28" stroke="4" speed="3.5" color="var(--primary-color)" />
    </div>

    <div v-else>
      <div class="profile-page-header">
        <div class="profile-page-header-avatar">
          <img :src="user.avatar" alt="avatar" class="profile-page-header-avatar-img" />
        </div>
        <div class="profile-page-header-user-info">
          <div class="profile-page-header-user-info-name">{{ user.username }}</div>
          <div class="profile-page-header-user-info-description">{{ user.introduction }}</div>
        </div>
      </div>

      <div class="profile-info">
        <div class="profile-info-item">
          <div class="profile-info-item-label">加入时间:</div>
          <div class="profile-info-item-value">{{ formatDate(user.createdAt) }}</div>
        </div>
        <div class="profile-info-item">
          <div class="profile-info-item-label">最后发帖时间:</div>
          <div class="profile-info-item-value">{{ formatDate(user.lastPostTime) }}</div>
        </div>
        <div class="profile-info-item">
          <div class="profile-info-item-label">浏览量:</div>
          <div class="profile-info-item-value">{{ user.totalViews }}</div>
        </div>
      </div>

      <div class="profile-tabs">
        <div :class="['profile-tabs-item', { selected: selectedTab === 'summary' }]" @click="selectedTab = 'summary'">
          <i class="fas fa-chart-line"></i>
          <div class="profile-tabs-item-label">总结</div>
        </div>
        <div :class="['profile-tabs-item', { selected: selectedTab === 'timeline' }]" @click="selectedTab = 'timeline'">
          <i class="fas fa-clock"></i>
          <div class="profile-tabs-item-label">时间线</div>
        </div>
      </div>

      <div v-if="selectedTab === 'summary'" class="profile-summary">
        <div class="total-summary">
          <div class="summary-title">统计信息</div>
          <div class="total-summary-content">
            <div class="total-summary-item">
              <div class="total-summary-item-label">访问天数</div>
              <div class="total-summary-item-value">0</div>
            </div>
            <div class="total-summary-item">
              <div class="total-summary-item-label">已读帖子</div>
              <div class="total-summary-item-value">165k</div>
            </div>
            <div class="total-summary-item">
              <div class="total-summary-item-label">已送出</div>
              <div class="total-summary-item-value">165k</div>
            </div>
            <div class="total-summary-item">
              <div class="total-summary-item-label">已收到</div>
              <div class="total-summary-item-value">165k</div>
            </div>
          </div>
        </div>
        <div class="summary-divider">
          <div class="hot-reply">
            <div class="summary-title">热门回复</div>
            <div class="summary-content" v-if="hotReplies.length > 0">
              <BaseTimeline :items="hotReplies">
                <template #item="{ item }">
                  在
                  <router-link
                    :to="`/posts/${item.comment.post.id}`"
                    class="timeline-link"
                  >
                    {{ item.comment.post.title }}
                  </router-link>
                  <template v-if="item.comment.parentComment">
                    下对
                    <router-link
                      :to="`/posts/${item.comment.post.id}#comment-${item.comment.parentComment.id}`"
                      class="timeline-link"
                    >
                      {{ item.comment.parentComment.content }}
                    </router-link>
                    回复了
                  </template>
                  <template v-else>
                    下评论了
                  </template>
                  <router-link
                    :to="`/posts/${item.comment.post.id}#comment-${item.comment.id}`"
                    class="timeline-link"
                  >
                    {{ item.comment.content }}
                  </router-link>
                  <div class="timeline-date">
                    {{ formatDate(item.comment.createdAt) }}
                  </div>
                </template>
              </BaseTimeline>
            </div>
            <div v-else>
              <div class="summary-empty">暂无热门回复</div>
            </div>
          </div>
          <div class="hot-topic">
            <div class="summary-title">热门话题</div>
            <div class="summary-content" v-if="hotPosts.length > 0">
              <BaseTimeline :items="hotPosts">
                <template #item="{ item }">
                  <router-link
                    :to="`/posts/${item.post.id}`"
                    class="timeline-link"
                  >
                    {{ item.post.title }}
                  </router-link>
                  <div class="timeline-snippet">
                    {{ stripMarkdown(item.post.snippet) }}
                  </div>
                  <div class="timeline-date">
                    {{ formatDate(item.post.createdAt) }}
                  </div>
                </template>
              </BaseTimeline>
            </div>
            <div v-else>
              <div class="summary-empty">暂无热门话题</div>
            </div>
          </div>
        </div>
      </div>

      <div v-else class="profile-timeline">
        <BaseTimeline :items="timelineItems">
          <template #item="{ item }">
            <template v-if="item.type === 'post'">
              发布了文章
              <router-link :to="`/posts/${item.post.id}`" class="timeline-link">
                {{ item.post.title }}
              </router-link>
              <div class="timeline-date">{{ formatDate(item.createdAt) }}</div>
            </template>
            <template v-else-if="item.type === 'comment'">
              在
              <router-link :to="`/posts/${item.comment.post.id}`" class="timeline-link">
                {{ item.comment.post.title }}
              </router-link>
              下评论了
              <router-link :to="`/posts/${item.comment.post.id}#comment-${item.comment.id}`" class="timeline-link">
                {{ item.comment.content }}
              </router-link>
              <div class="timeline-date">{{ formatDate(item.createdAt) }}</div>
            </template>
            <template v-else-if="item.type === 'reply'">
              在
              <router-link :to="`/posts/${item.comment.post.id}`" class="timeline-link">
                {{ item.comment.post.title }}
              </router-link>
              下对
              <router-link :to="`/posts/${item.comment.post.id}#comment-${item.comment.parentComment.id}`" class="timeline-link">
                {{ item.comment.parentComment.content }}
              </router-link>
              回复了
              <router-link :to="`/posts/${item.comment.post.id}#comment-${item.comment.id}`" class="timeline-link">
                {{ item.comment.content }}
              </router-link>
              <div class="timeline-date">{{ formatDate(item.createdAt) }}</div>
            </template>
          </template>
        </BaseTimeline>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { API_BASE_URL } from '../main'
import BaseTimeline from '../components/BaseTimeline.vue'
import { stripMarkdown } from '../utils/markdown'
import TimeManager from '../utils/time'
import { hatch } from 'ldrs'
hatch.register()

export default {
  name: 'ProfileView',
  components: { BaseTimeline },
  setup() {
    const route = useRoute()
    const username = route.params.id

    const user = ref({})
    const hotPosts = ref([])
    const hotReplies = ref([])
    const timelineItems = ref([])
    const isLoading = ref(false)
    const selectedTab = ref('summary')

    const formatDate = (d) => {
      if (!d) return ''
      return TimeManager.format(d)
    }

    const fetchData = async () => {
      try {
        isLoading.value = true
        let res = await fetch(`${API_BASE_URL}/api/users/${username}`)
        if (res.ok) user.value = await res.json()

        res = await fetch(`${API_BASE_URL}/api/users/${username}/hot-posts`)
        if (res.ok) {
          const data = await res.json()
          hotPosts.value = data.map(p => ({
            icon: 'fas fa-book',
            post: p
          }))
        }

        res = await fetch(`${API_BASE_URL}/api/users/${username}/hot-replies`)
        if (res.ok) {
          const data = await res.json()
          hotReplies.value = data.map(c => ({
            icon: 'fas fa-comment',
            comment: c
          }))
        }

        const postsRes = await fetch(`${API_BASE_URL}/api/users/${username}/posts?limit=50`)
        const repliesRes = await fetch(`${API_BASE_URL}/api/users/${username}/replies?limit=50`)
        const posts = postsRes.ok ? await postsRes.json() : []
        const replies = repliesRes.ok ? await repliesRes.json() : []
        const mapped = [
          ...posts.map(p => ({
            type: 'post',
            icon: 'fas fa-book',
            post: p,
            createdAt: p.createdAt
          })),
          ...replies.map(r => ({
            type: r.parentComment ? 'reply' : 'comment',
            icon: 'fas fa-comment',
            comment: r,
            createdAt: r.createdAt
          }))
        ]
        mapped.sort((a, b) => new Date(b.createdAt) - new Date(a.createdAt))
        timelineItems.value = mapped
      } catch (e) {
        console.error(e)
      } finally {
        isLoading.value = false
      }
    }

    onMounted(fetchData)
    return { user, hotPosts, hotReplies, timelineItems, isLoading, selectedTab, formatDate, stripMarkdown }
  }
}
</script>

<style scoped>
.loading-page {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 300px;
}
.profile-page {
  background-color: var(--background-color);
  height: calc(100vh - var(--header-height));
  overflow-y: auto;
}
.profile-page-header {
  display: flex;
  align-items: center;
  padding: 20px;
}
.profile-page-header-avatar-img {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background-color: lightblue;
  object-fit: cover;
}
.profile-page-header-user-info {
  margin-left: 20px;
}
.profile-page-header-user-info-name {
  font-size: 24px;
  font-weight: bold;
}
.profile-page-header-user-info-description {
  font-size: 20px;
  color: #666;
}
.profile-info {
  display: flex;
  flex-direction: row;
  padding: 0 20px;
  gap: 20px;
  border-top: 1px solid #e0e0e0;
  border-bottom: 1px solid #e0e0e0;
}
.profile-info-item {
  display: flex;
  flex-direction: row;
  gap: 5px;
  align-items: center;
  padding: 10px 0;
}
.profile-info-item-label {
  font-size: 14px;
  opacity: 0.7;
}
.profile-info-item-value {
  font-size: 14px;
}
.profile-tabs {
  display: flex;
  flex-direction: row;
  padding: 0 20px;
  border-bottom: 1px solid #e0e0e0;
}
.profile-tabs-item {
  display: flex;
  flex-direction: row;
  gap: 10px;
  align-items: center;
  justify-content: center;
  padding: 10px 0;
  width: 200px;
  cursor: pointer;
}
.profile-tabs-item.selected {
  color: var(--primary-color);
  border-bottom: 2px solid var(--primary-color);
}
.profile-summary {
  display: flex;
  flex-direction: column;
  padding: 20px;
  gap: 20px;
}
.summary-title {
  font-size: 20px;
  font-weight: bold;
}
.total-summary {
  width: 100%;
}
.total-summary-content {
  display: flex;
  flex-direction: row;
  gap: 20px;
}
.total-summary-item {
  display: flex;
  flex-direction: row;
  gap: 10px;
  align-items: center;
  padding: 10px 0;
}
.total-summary-item-label {
  font-size: 18px;
  opacity: 0.7;
}
.total-summary-item-value {
  font-size: 24px;
  font-weight: bold;
}
.summary-divider {
  margin-top: 20px;
  display: flex;
  flex-direction: row;
  gap: 20px;
  width: 100%;
}
.hot-reply,
.hot-topic {
  width: 50%;
}
.profile-timeline {
  padding: 20px;
}

.timeline-date {
  font-size: 12px;
  color: gray;
  margin-top: 5px;
}

.timeline-snippet {
  font-size: 14px;
  color: #666;
  margin-top: 5px;
}

.timeline-link {
  font-weight: bold;
  color: var(--primary-color);
  text-decoration: none;
}

.timeline-link:hover {
  text-decoration: underline;
}

.summary-empty {
  margin-top: 10px;
  font-size: 14px;
  opacity: 0.5;
}

.summary-content {
  margin-top: 10px;
}

</style>
