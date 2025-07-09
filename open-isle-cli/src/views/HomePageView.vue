<template>
  <div class="home-page" @scroll="handleScroll">
    <div class="search-container">
      <div class="search-title">一切可能，从此刻启航</div>
      <div class="search-subtitle">愿你在此遇见灵感与共鸣。若有疑惑，欢迎发问，亦可在知识的海洋中搜寻答案。</div>
      <SearchDropdown />
    </div>


    <div class="topic-container">
      <div class="topic-item-container">
        <div
          v-for="topic in topics"
          :key="topic"
          class="topic-item"
          :class="{ selected: topic === selectedTopic }"
          @click="selectedTopic = topic"
        >
          {{ topic }}
        </div>
        <CategorySelect v-model="selectedCategory" />
        <TagSelect v-model="selectedTags" />
      </div>
    </div>

    <div class="article-container">
      <template v-if="selectedTopic === '最新' || selectedTopic === '排行榜'">
        <div class="header-container">
          <div class="header-item main-item">
            <div class="header-item-text">话题</div>
          </div>
          <div class="header-item avatars">
            <div class="header-item-text">参与人员</div>
          </div>
          <div class="header-item">
            <div class="header-item-text">回复</div>
          </div>
          <div class="header-item">
            <div class="header-item-text">浏览</div>
          </div>
          <div class="header-item">
            <div class="header-item-text">活动</div>
          </div>
        </div>

        <div v-if="isLoadingPosts && articles.length === 0" class="loading-container">
          <l-hatch size="28" stroke="4" speed="3.5" color="var(--primary-color)"></l-hatch>
        </div>

        <div v-else-if="articles.length === 0">
          <div class="no-posts-container">
            <div class="no-posts-text">暂时没有帖子 :( 点击发帖发送第一篇相关帖子吧!</div>
          </div>
        </div>

        <div class="article-item" v-for="article in articles" :key="article.id">
          <div class="article-main-container">
            <router-link class="article-item-title main-item" :to="`/posts/${article.id}`">
              {{ article.title }}
            </router-link>
            <div class="article-item-description main-item">{{ sanitizeDescription(article.description) }}</div>
            <div class="article-info-container main-item">
              <ArticleTags :tags="[article.category]" />
              <ArticleTags :tags="article.tags" />
            </div>
          </div>

          <div class="article-member-avatars-container">
            <div class="article-member-avatar-item" v-for="(avatar, idx) in article.members" :key="idx">
              <img class="article-member-avatar-item-img" :src="avatar" alt="avatar">
            </div>
          </div>
          <div class="article-comments">
            {{ article.comments }}
          </div>
          <div class="article-views">
            {{ article.views }}
          </div>
          <div class="article-time">
            {{ article.time }}
          </div>
        </div>
      </template>
      <div v-else-if="selectedTopic === '热门'" class="placeholder-container">
        热门帖子功能开发中，敬请期待。
      </div>
      <div v-else class="placeholder-container">
        分类浏览功能开发中，敬请期待。
      </div>
      <div v-if="isLoadingPosts && articles.length > 0" class="loading-container bottom-loading">
        <l-hatch size="28" stroke="4" speed="3.5" color="var(--primary-color)"></l-hatch>
      </div>
    </div>

  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import { stripMarkdown } from '../utils/markdown'
import { API_BASE_URL } from '../main'
import TimeManager from '../utils/time'
import CategorySelect from '../components/CategorySelect.vue'
import TagSelect from '../components/TagSelect.vue'
import ArticleTags from '../components/ArticleTags.vue'
import SearchDropdown from '../components/SearchDropdown.vue'
import { hatch } from 'ldrs'
hatch.register()


export default {
  name: 'HomePageView',
  components: {
    CategorySelect,
    TagSelect,
    ArticleTags,
    SearchDropdown
  },
  setup() {
    const selectedCategory = ref('')
    const selectedTags = ref([])
    const isLoadingPosts = ref(false)
    const topics = ref(['最新', '排行榜' /*, '热门', '类别'*/])
    const selectedTopic = ref('最新')

    const articles = ref([])
    const page = ref(0)
    const pageSize = 5
    const allLoaded = ref(false)

    const buildUrl = () => {
      let url = `${API_BASE_URL}/api/posts?page=${page.value}&pageSize=${pageSize}`
      if (selectedCategory.value) {
        url += `&categoryId=${selectedCategory.value}`
      }
      if (selectedTags.value.length) {
        selectedTags.value.forEach(t => {
          url += `&tagIds=${t}`
        })
      }
      return url
    }

    const buildRankUrl = () => {
      let url = `${API_BASE_URL}/api/posts/ranking?page=${page.value}&pageSize=${pageSize}`
      if (selectedCategory.value) {
        url += `&categoryId=${selectedCategory.value}`
      }
      if (selectedTags.value.length) {
        selectedTags.value.forEach(t => {
          url += `&tagIds=${t}`
        })
      }
      return url
    }

    const fetchPosts = async (reset = false) => {
      if (reset) {
        page.value = 0
        allLoaded.value = false
        articles.value = []
      }
      if (isLoadingPosts.value || allLoaded.value) return
      try {
        isLoadingPosts.value = true
        const res = await fetch(buildUrl())
        isLoadingPosts.value = false
        if (!res.ok) return
        const data = await res.json()
        articles.value.push(
          ...data.map(p => ({
            id: p.id,
            title: p.title,
            description: p.content,
            category: p.category,
            tags: p.tags || [],
            members: [],
            comments: (p.comments || []).length,
            views: p.views,
            time: TimeManager.format(p.createdAt)
          }))
        )
        if (data.length < pageSize) {
          allLoaded.value = true
        } else {
          page.value += 1
        }
      } catch (e) {
        console.error(e)
      }
    }

    const fetchRanking = async (reset = false) => {
      if (reset) {
        page.value = 0
        allLoaded.value = false
        articles.value = []
      }
      if (isLoadingPosts.value || allLoaded.value) return
      try {
        isLoadingPosts.value = true
        const res = await fetch(buildRankUrl())
        isLoadingPosts.value = false
        if (!res.ok) return
        const data = await res.json()
        articles.value.push(
          ...data.map(p => ({
            id: p.id,
            title: p.title,
            description: p.content,
            category: p.category,
            tags: p.tags || [],
            members: [],
            comments: (p.comments || []).length,
            views: p.views,
            time: TimeManager.format(p.createdAt)
          }))
        )
        if (data.length < pageSize) {
          allLoaded.value = true
        } else {
          page.value += 1
        }
      } catch (e) {
        console.error(e)
      }
    }

    const handleScroll = (e) => {
      const el = e.target
      if (el.scrollHeight - el.scrollTop <= el.clientHeight + 50) {
        if (selectedTopic.value === '排行榜') {
          fetchRanking()
        } else {
          fetchPosts()
        }
      }
    }

    onMounted(() => {
      if (selectedTopic.value === '排行榜') {
        fetchRanking()
      } else {
        fetchPosts()
      }
    })

    watch([selectedCategory, selectedTags], () => {
      if (selectedTopic.value === '最新') {
        fetchPosts(true)
      } else if (selectedTopic.value === '排行榜') {
        fetchRanking(true)
      }
    })

    watch(selectedTopic, () => {
      if (selectedTopic.value === '排行榜') {
        fetchRanking(true)
      } else {
        fetchPosts(true)
      }
    })

    const sanitizeDescription = (text) => stripMarkdown(text)

    return { topics, selectedTopic, articles, sanitizeDescription, isLoadingPosts, handleScroll, selectedCategory, selectedTags }
  }
}
</script>

<style scoped>
.home-page {
  background-color: var(--background-color);
  height: calc(100vh - var(--header-height));
  display: flex;
  flex-direction: column;
  align-items: center;
  overflow-y: auto;
  /* width variables shared between header and article rows */
  --main-width: 60%;
  --avatars-width: 20%;
  --comments-width: 5%;
  --views-width: 5%;
  --activity-width: 10%;
}

.search-container {
  margin-top: 100px;
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.search-title {
  font-size: 32px;
  font-weight: bold;
}

.search-subtitle {
  font-size: 16px;
}


.loading-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.bottom-loading {
  height: 100px;
}

.no-posts-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

.no-posts-text {
  font-size: 14px;
  opacity: 0.7;
}

.topic-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 20px 0;
}

.topic-item-container {
  margin-left: 20px;
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 10px;
}

.topic-item {
  padding: 2px 10px;
  cursor: pointer;
}

.topic-item.selected {
  color: var(--primary-color);
  border-bottom: 2px solid var(--primary-color);
}

.article-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  width: 100%;
}

.header-container {
  display: grid;
  grid-template-columns: var(--main-width) var(--avatars-width) var(--comments-width) var(--views-width) var(--activity-width);
  align-items: center;
  width: 100%;
  color: gray;
  border-bottom: 1px solid lightgray;
  padding-bottom: 10px;
}

.article-item {
  display: grid;
  grid-template-columns: var(--main-width) var(--avatars-width) var(--comments-width) var(--views-width) var(--activity-width);
  align-items: center;
  width: 100%;
  border-bottom: 1px solid lightgray;
}

.header-item.avatars {
  margin-left: 20px;
}

.main-item {
  padding-left: 20px;
}

.article-item-title {
  font-size: 20px;
  text-decoration: none;
  color: var(--text-color);
  line-clamp: 1;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.article-item-title:hover {
  color: var(--primary-color);
  text-decoration: underline;
}

.article-item-description {
  margin-top: 10px;
  font-size: 14px;
  color: gray;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.article-info-container {
  margin-top: 10px;
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.article-tags-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 10px;
}

.article-tag-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 10px;
}

.article-member-avatars-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 3px;
  margin-left: 20px;
}

.article-member-avatar-item {
  width: 25px;
  height: 25px;
  border-radius: 50%;
  overflow: hidden;
}

.article-member-avatar-item-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.placeholder-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
  font-size: 16px;
  opacity: 0.7;
}
</style>
