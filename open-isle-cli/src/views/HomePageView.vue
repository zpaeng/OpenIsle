<template>
  <div class="home-page">
    <div class="search-container">
      <div class="search-title">Where possible begins</div>
      <div class="search-subtitle">希望你喜欢这里。有问题，请提问，或搜索现有帖子</div>
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
      <template v-if="selectedTopic === '最新'">
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

        <div v-if="isLoadingPosts" class="loading-container">
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
      <div v-else-if="selectedTopic === '排行榜'" class="placeholder-container">
        排行榜功能开发中，敬请期待。
      </div>
      <div v-else-if="selectedTopic === '热门'" class="placeholder-container">
        热门帖子功能开发中，敬请期待。
      </div>
      <div v-else class="placeholder-container">
        分类浏览功能开发中，敬请期待。
      </div>
    </div>

  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { stripMarkdown } from '../utils/markdown'
import { API_BASE_URL } from '../main'
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
  data() {
    return {
      selectedCategory: '',
      selectedTags: [],
    }
  },
  setup() {
    const isLoadingPosts = ref(false)
    const topics = ref(['最新', '排行榜', '热门', '类别'])
    const selectedTopic = ref('最新')

    const articles = ref([])

    const fetchPosts = async () => {
      try {
        isLoadingPosts.value = true
        const res = await fetch(`${API_BASE_URL}/api/posts`)
        isLoadingPosts.value = false
        if (!res.ok) return
        const data = await res.json()
        articles.value = data.map(p => ({
          id: p.id,
          title: p.title,
          description: p.content,
          category: p.category,
          tags: p.tags || [],
          members: [],
          comments: (p.comments || []).length,
          views: p.views,
          time: new Date(p.createdAt).toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' })
        }))
      } catch (e) {
        console.error(e)
      }
    }

    onMounted(fetchPosts)

    const sanitizeDescription = (text) => stripMarkdown(text)

    return { topics, selectedTopic, articles, sanitizeDescription, isLoadingPosts }
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
