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
        <div class="summary-divider">
          <div class="hot-reply">
            <div class="summary-title">热门回复</div>
            <BaseTimeline :items="hotReplies" />
          </div>
          <div class="hot-topic">
            <div class="summary-title">热门话题</div>
            <BaseTimeline :items="hotPosts" />
          </div>
        </div>
      </div>

      <div v-else class="profile-timeline">
        <BaseTimeline :items="timelineItems" />
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { API_BASE_URL } from '../main'
import BaseTimeline from '../components/BaseTimeline.vue'
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
      return new Date(d).toLocaleDateString('zh-CN', { year: 'numeric', month: 'numeric', day: 'numeric' })
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
            content: p.title
          }))
        }

        res = await fetch(`${API_BASE_URL}/api/users/${username}/hot-replies`)
        if (res.ok) {
          const data = await res.json()
          hotReplies.value = data.map(c => ({
            icon: 'fas fa-comment',
            content: c.content
          }))
        }

        const postsRes = await fetch(`${API_BASE_URL}/api/users/${username}/posts?limit=50`)
        const repliesRes = await fetch(`${API_BASE_URL}/api/users/${username}/replies?limit=50`)
        const posts = postsRes.ok ? await postsRes.json() : []
        const replies = repliesRes.ok ? await repliesRes.json() : []
        const mapped = [
          ...posts.map(p => ({ icon: 'fas fa-book', content: p.title, createdAt: p.createdAt })),
          ...replies.map(r => ({ icon: 'fas fa-comment', content: r.content, createdAt: r.createdAt }))
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
    return { user, hotPosts, hotReplies, timelineItems, isLoading, selectedTab, formatDate }
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
</style>
