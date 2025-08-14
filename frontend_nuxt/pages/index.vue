<template>
  <div class="home-page">
    <div v-if="!isMobile" class="search-container">
      <div class="search-title">一切可能，从此刻启航</div>
      <div class="search-subtitle">
        愿你在此遇见灵感与共鸣。若有疑惑，欢迎发问，亦可在知识的海洋中搜寻答案。
      </div>
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
        <div class="topic-select-container">
          <CategorySelect v-model="selectedCategory" :options="categoryOptions" />
          <TagSelect v-model="selectedTags" :options="tagOptions" />
        </div>
      </div>
    </div>

    <div class="article-container">
      <template
        v-if="
          selectedTopic === '最新' || selectedTopic === '排行榜' || selectedTopic === '最新回复'
        "
      >
        <div class="article-header-container">
          <div class="header-item main-item">
            <div class="header-item-text">话题</div>
          </div>
          <div class="header-item avatars">
            <div class="header-item-text">参与人员</div>
          </div>
          <div class="header-item comments">
            <div class="header-item-text">回复</div>
          </div>
          <div class="header-item views">
            <div class="header-item-text">浏览</div>
          </div>
          <div class="header-item activity">
            <div class="header-item-text">活动</div>
          </div>
        </div>

        <div v-if="pendingFirst" class="loading-container">
          <l-hatch size="28" stroke="4" speed="3.5" color="var(--primary-color)"></l-hatch>
        </div>

        <div v-else-if="articles.length === 0">
          <div class="no-posts-container">
            <div class="no-posts-text">暂时没有帖子 :( 点击发帖发送第一篇相关帖子吧!</div>
          </div>
        </div>

        <div
          v-if="!pendingFirst"
          class="article-item"
          v-for="article in articles"
          :key="article.id"
        >
          <div class="article-main-container">
            <NuxtLink class="article-item-title main-item" :to="`/posts/${article.id}`">
              <i v-if="article.pinned" class="fas fa-thumbtack pinned-icon"></i>
              <i v-if="article.type === 'LOTTERY'" class="fa-solid fa-gift lottery-icon"></i>
              {{ article.title }}
            </NuxtLink>
            <div class="article-item-description main-item">
              {{ sanitizeDescription(article.description) }}
            </div>
            <div class="article-info-container main-item">
              <ArticleCategory :category="article.category" />
              <ArticleTags :tags="article.tags" />
            </div>
          </div>

          <div class="article-member-avatars-container">
            <NuxtLink
              v-for="member in article.members"
              :key="`${article.id}-${member.id}`"
              class="article-member-avatar-item"
              :to="`/users/${member.id}`"
            >
              <img class="article-member-avatar-item-img" :src="member.avatar" alt="avatar" />
            </NuxtLink>
          </div>

          <div class="article-comments main-info-text">
            {{ article.comments }}
          </div>

          <div class="article-views main-info-text">
            {{ article.views }}
          </div>

          <div class="article-time main-info-text">
            {{ article.time }}
          </div>
        </div>
      </template>
      <div v-else-if="selectedTopic === '热门'" class="placeholder-container">
        热门帖子功能开发中，敬请期待。
      </div>
      <div v-else class="placeholder-container">分类浏览功能开发中，敬请期待。</div>
      <div v-if="isLoadingMore && articles.length > 0" class="loading-container bottom-loading">
        <l-hatch size="28" stroke="4" speed="3.5" color="var(--primary-color)"></l-hatch>
      </div>
    </div>
  </div>
</template>
<script setup>
import { computed, onMounted, ref, watch } from 'vue'
import ArticleCategory from '~/components/ArticleCategory.vue'
import ArticleTags from '~/components/ArticleTags.vue'
import CategorySelect from '~/components/CategorySelect.vue'
import SearchDropdown from '~/components/SearchDropdown.vue'
import TagSelect from '~/components/TagSelect.vue'
import { getToken } from '~/utils/auth'
import { useScrollLoadMore } from '~/utils/loadMore'
import { stripMarkdown } from '~/utils/markdown'
import { useIsMobile } from '~/utils/screen'
import TimeManager from '~/utils/time'

useHead({
  title: 'OpenIsle - 全面开源的自由社区',
  meta: [
    {
      name: 'description',
      content:
        'OpenIsle 是一个开放的技术与交流社区，致力于为开发者、技术爱好者和创作者们提供一个自由、友好、包容的讨论与协作环境。我们鼓励用户在这里分享知识、交流经验、提出问题、展示作品，并共同推动技术进步与社区成长。',
    },
  ],
})

const config = useRuntimeConfig()
const API_BASE_URL = config.public.apiBaseUrl
const selectedCategory = ref('')
const selectedTags = ref([])
const route = useRoute()
const tagOptions = ref([])
const categoryOptions = ref([])

const isLoadingMore = ref(false)

const topics = ref(['最新回复', '最新', '排行榜' /*, '热门', '类别'*/])
const selectedTopic = ref(
  route.query.view === 'ranking' ? '排行榜' : route.query.view === 'latest' ? '最新' : '最新回复',
)
const articles = ref([])
const page = ref(0)
const pageSize = 10
const isMobile = useIsMobile()
const allLoaded = ref(false)

/** URL 参数 -> 本地筛选值 **/
const selectedCategorySet = (category) => {
  const c = decodeURIComponent(category)
  selectedCategory.value = isNaN(c) ? c : Number(c)
}
const selectedTagsSet = (tags) => {
  const t = Array.isArray(tags) ? tags.join(',') : tags
  selectedTags.value = t
    .split(',')
    .filter((v) => v)
    .map((v) => decodeURIComponent(v))
    .map((v) => (isNaN(v) ? v : Number(v)))
}

/** 初始化：仅在客户端首渲染时根据路由同步一次 **/
onMounted(() => {
  const { category, tags } = route.query
  if (category) selectedCategorySet(category)
  if (tags) selectedTagsSet(tags)
})

/** 路由变更时同步筛选 **/
watch(
  () => route.query,
  (query) => {
    const category = query.category
    const tags = query.tags
    category && selectedCategorySet(category)
    tags && selectedTagsSet(tags)
  },
)

/** 选项加载（分类/标签名称回填） **/
const loadOptions = async () => {
  if (selectedCategory.value && !isNaN(selectedCategory.value)) {
    try {
      const res = await fetch(`${API_BASE_URL}/api/categories/`)
      if (res.ok) categoryOptions.value = [await res.json()]
    } catch {}
  }
  if (selectedTags.value.length) {
    const arr = []
    for (const t of selectedTags.value) {
      if (!isNaN(t)) {
        try {
          const r = await fetch(`${API_BASE_URL}/api/tags/${t}`)
          if (r.ok) arr.push(await r.json())
        } catch {}
      }
    }
    tagOptions.value = arr
  }
}

/** 列表 API 路径与查询参数 **/
const baseQuery = computed(() => ({
  categoryId: selectedCategory.value || undefined,
  tagIds: selectedTags.value.length ? selectedTags.value : undefined,
}))
const listApiPath = computed(() => {
  if (selectedTopic.value === '排行榜') return '/api/posts/ranking'
  if (selectedTopic.value === '最新回复') return '/api/posts/latest-reply'
  return '/api/posts'
})
const buildUrl = ({ pageNo }) => {
  const url = new URL(`${API_BASE_URL}${listApiPath.value}`)
  url.searchParams.set('page', pageNo)
  url.searchParams.set('pageSize', pageSize)
  if (baseQuery.value.categoryId) url.searchParams.set('categoryId', baseQuery.value.categoryId)
  if (baseQuery.value.tagIds)
    for (const t of baseQuery.value.tagIds) url.searchParams.append('tagIds', t)
  return url.toString()
}
const tokenHeader = computed(() => {
  const token = getToken()
  return token ? { Authorization: `Bearer ${token}` } : {}
})

/** —— 首屏数据托管（SSR） —— **/
const asyncKey = computed(() => [
  'home:firstpage',
  selectedTopic.value,
  String(baseQuery.value.categoryId ?? ''),
  JSON.stringify(baseQuery.value.tagIds ?? []),
])
const {
  data: firstPage,
  pending: pendingFirst,
  refresh: refreshFirst,
} = await useAsyncData(
  () => asyncKey.value.join('::'),
  async () => {
    const res = await $fetch(buildUrl({ pageNo: 0 }), { headers: tokenHeader.value })
    const data = Array.isArray(res) ? res : []
    return data.map((p) => ({
      id: p.id,
      title: p.title,
      description: p.content,
      category: p.category,
      tags: p.tags || [],
      members: (p.participants || []).map((m) => ({ id: m.id, avatar: m.avatar })),
      comments: p.commentCount,
      views: p.views,
      time: TimeManager.format(
        selectedTopic.value === '最新回复' ? p.lastReplyAt || p.createdAt : p.createdAt,
      ),
      pinned: Boolean(p.pinned ?? p.pinnedAt ?? p.pinned_at),
      type: p.type,
    }))
  },
  {
    server: true,
    default: () => [],
    watch: [selectedTopic, baseQuery],
  },
)

/** 首屏/筛选变更：重置分页并灌入 firstPage **/
watch(
  firstPage,
  (data) => {
    page.value = 0
    articles.value = [...(data || [])]
    allLoaded.value = (data?.length || 0) < pageSize
  },
  { immediate: true },
)

/** —— 滚动加载更多 —— **/
let inflight = null
const fetchNextPage = async () => {
  if (allLoaded.value || pendingFirst.value || inflight) return
  const nextPage = page.value + 1
  isLoadingMore.value = true
  inflight = $fetch(buildUrl({ pageNo: nextPage }), { headers: tokenHeader.value })
    .then((res) => {
      const data = Array.isArray(res) ? res : []
      const mapped = data.map((p) => ({
        id: p.id,
        title: p.title,
        description: p.content,
        category: p.category,
        tags: p.tags || [],
        members: (p.participants || []).map((m) => ({ id: m.id, avatar: m.avatar })),
        comments: p.commentCount,
        views: p.views,
        time: TimeManager.format(
          selectedTopic.value === '最新回复' ? p.lastReplyAt || p.createdAt : p.createdAt,
        ),
        pinned: Boolean(p.pinned ?? p.pinnedAt ?? p.pinned_at),
        type: p.type,
      }))
      articles.value.push(...mapped)
      if (data.length < pageSize) {
        allLoaded.value = true
      } else {
        page.value = nextPage
      }
    })
    .finally(() => {
      inflight = null
      isLoadingMore.value = false
    })
}

/** 绑定滚动加载（避免挂载瞬间触发） **/
let initialReady = false
const loadMoreGuarded = async () => {
  if (!initialReady) return
  await fetchNextPage()
}
useScrollLoadMore(loadMoreGuarded)
watch(
  articles,
  () => {
    if (!initialReady && articles.value.length) initialReady = true
  },
  { immediate: true },
)

/** 切换分类/标签/Tab：useAsyncData 已 watch，这里只需确保 options 加载 **/
watch([selectedCategory, selectedTags], () => {
  loadOptions()
})
watch(selectedTopic, () => {
  // 仅当需要额外选项时加载
  loadOptions()
})

/** 选项首屏加载：服务端执行一次；客户端兜底 **/
if (import.meta.server) {
  await loadOptions()
}
onMounted(() => {
  if (categoryOptions.value.length === 0 && tagOptions.value.length === 0) loadOptions()
})

/** 其他工具函数 **/
const sanitizeDescription = (text) => stripMarkdown(text)
</script>

<style scoped>
.home-page {
  background-color: var(--background-color);
  display: flex;
  flex-direction: column;
  align-items: center;
  container-type: inline-size;
  container-name: home-page;
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
  position: sticky;
  top: calc(var(--header-height) + 1px);
  z-index: 10;
  background-color: var(--background-color-blur);
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 10px;
  width: 100%;
  padding: 10px 0;
}

.topic-item-container {
  margin-left: 20px;
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  flex-wrap: wrap;
}

.topic-select-container {
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
  width: 100%;
  padding-bottom: 100px;
}

.article-header-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  width: 100%;
  color: gray;
  border-bottom: 1px solid var(--normal-border-color);
  padding-bottom: 10px;
}

.article-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  width: 100%;
  border-bottom: 1px solid var(--normal-border-color);
}

.article-main-container,
.header-item.main-item {
  width: calc(60% - 20px);
  padding-left: 20px;
}

/* .article-member-avatars-container,
.header-item.avatars, */
.article-comments,
.header-item.comments,
.article-views,
.header-item.views,
.article-time,
.header-item.activity {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
}

.article-member-avatars-container,
.header-item.avatars {
  width: 20%;
}

.article-comments,
.header-item.comments {
  width: 5%;
}

.article-views,
.header-item.views {
  width: 5%;
}

.article-time,
.header-item.activity {
  width: 10%;
}

.article-item-title {
  margin-top: 10px;
  font-size: 20px;
  text-decoration: none;
  color: var(--text-color);
  max-width: 100%;
}

.article-item-title:hover {
  color: var(--primary-color);
  text-decoration: underline;
}

.pinned-icon,
.lottery-icon {
  margin-right: 4px;
  color: var(--primary-color);
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

.article-main-container {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
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

.main-info-text {
  font-size: 14px;
  opacity: 0.7;
}

@container home-page (max-width: 900px) {
  .article-main-container,
  .header-item.main-item {
    width: calc(70% - 20px);
    padding-left: 20px;
  }

  .article-member-avatars-container,
  .header-item.avatars {
    width: 10%;
  }

  .article-comments,
  .header-item.comments {
    width: 5%;
  }

  .article-views,
  .header-item.views {
    width: 5%;
  }

  .article-time,
  .header-item.activity {
    width: 10%;
  }

  .article-member-avatar-item:nth-child(n + 4) {
    display: none;
  }
}

@container home-page (max-width: 768px) {
  .article-main-container,
  .header-item.main-item {
    width: calc(70% - 20px);
    padding-left: 20px;
  }

  .article-member-avatars-container,
  .header-item.avatars {
    width: 10%;
  }

  .article-comments,
  .header-item.comments {
    display: none;
  }

  .article-views,
  .header-item.views {
    display: none;
  }

  .article-time,
  .header-item.activity {
    width: 10%;
    margin-right: 3%;
  }

  .article-header-container {
    display: none;
  }

  .article-member-avatar-item:nth-child(n + 2) {
    display: none;
  }

  .header-item-text {
    font-size: 12px;
  }

  .article-item-title {
    font-size: 16px;
    font-weight: bold;
  }

  .article-item-description {
    margin-top: 2px;
    font-size: 10px;
    max-width: 100%;
  }

  .main-info-text {
    font-size: 10px;
    opacity: 0.5;
  }

  .topic-container {
    position: initial;
  }
}
</style>
