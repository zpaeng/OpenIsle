<template>
  <div class="home-page">
    <div class="search-container">
      <div class="search-title">Where possible begins</div>
      <div class="search-subtitle">希望你喜欢这里。有问题，请提问，或搜索现有帖子</div>
      <div class="search-input">
        <i class="search-input-icon fas fa-search"></i>
        <input type="text" placeholder="Search">
      </div>
    </div>


  <div class="topic-container">
      <div class="topic-item-container">
        <div
          v-for="topic in topics"
          :key="topic"
          class="topic-item"
          :class="{ selected: topic === selectedTopic }"
        >
          {{ topic }}
        </div>
      </div>
    </div>

    <div class="article-container">
      <div
        class="article-item"
        v-for="article in articles"
        :key="article.id"
      >
        <div class="article-main-container">
          <router-link
            class="article-item-title"
            :to="`/posts/${article.id}`"
          >
            {{ article.title }}
          </router-link>
          <div class="article-item-description">{{ article.description }}</div>
          <div class="article-info-container">
            <div class="article-info-item">
              <i class="fas fa-user"></i>
              <div class="article-info-item-text">{{ article.category }}</div>
            </div>

            <div class="article-tags-container">
              <div
                class="article-tag-item"
                v-for="tag in article.tags"
                :key="tag"
              >
                <i class="fas fa-tag"></i>
                <div class="article-tag-item-text">{{ tag }}</div>
              </div>
            </div>
          </div>
        </div>

        <div class="article-member-avatars-container">
          <div
            class="article-member-avatar-item"
            v-for="(avatar, idx) in article.members"
            :key="idx"
          >
            <img
              class="article-member-avatar-item-img"
              :src="avatar"
              alt="avatar"
            >
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
    </div>

  </div>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'HomePageView',
  setup() {
    const topics = ref(['最新', '排行榜', '热门', '类别'])
    const selectedTopic = ref('最新')

    const baseAvatar = 'https://picsum.photos/200/200'
    const articles = ref([
      {
        id: 1,
        title: '各位佬，科研项目python语言适合什么ai编程？',
        description:
          '是的，L站目前每天都有不少各色各样的佬友加入。对于一个在线社区来说，不断壮大和涌入新的血液是一件好事。但我每天都要问问自己，这里面有没有问题？真的完全是好事吗？在这个过程中我嗅到了一丝危险的气息：有人试图同质化这里，把这里当作互联网上另⋯阅读更多',
        category: '开发调优',
        tags: ['AI', 'Python', 'Java'],
        members: Array(5).fill(baseAvatar),
        comments: 139,
        views: '2.4k',
        time: '23小时前'
      },
      {
        id: 2,
        title: '各位佬，科研项目python语言适合什么ai编程？',
        description:
          '是的，L站目前每天都有不少各色各样的佬友加入。对于一个在线社区来说，不断壮大和涌入新的血液是一件好事。但我每天都要问问自己，这里面有没有问题？真的完全是好事吗？在这个过程中我嗅到了一丝危险的气息：有人试图同质化这里，把这里当作互联网上另⋯阅读更多',
        category: '开发调优',
        tags: ['AI', 'Python', 'Java'],
        members: Array(5).fill(baseAvatar),
        comments: 139,
        views: '2.4k',
        time: '23小时前'
      },
      {
        id: 3,
        title: '各位佬，科研项目python语言适合什么ai编程？',
        description:
          '是的，L站目前每天都有不少各色各样的佬友加入。对于一个在线社区来说，不断壮大和涌入新的血液是一件好事。但我每天都要问问自己，这里面有没有问题？真的完全是好事吗？在这个过程中我嗅到了一丝危险的气息：有人试图同质化这里，把这里当作互联网上另⋯阅读更多',
        category: '开发调优',
        tags: ['AI', 'Python', 'Java'],
        members: Array(5).fill(baseAvatar),
        comments: 139,
        views: '2.4k',
        time: '23小时前'
      },
      {
        id: 4,
        title: '各位佬，科研项目python语言适合什么ai编程？',
        description:
          '是的，L站目前每天都有不少各色各样的佬友加入。对于一个在线社区来说，不断壮大和涌入新的血液是一件好事。但我每天都要问问自己，这里面有没有问题？真的完全是好事吗？在这个过程中我嗅到了一丝危险的气息：有人试图同质化这里，把这里当作互联网上另⋯阅读更多',
        category: '开发调优',
        tags: ['AI', 'Python', 'Java'],
        members: Array(5).fill(baseAvatar),
        comments: 139,
        views: '2.4k',
        time: '23小时前'
      },
      {
        id: 5,
        title: '各位佬，科研项目python语言适合什么ai编程？',
        description:
          '是的，L站目前每天都有不少各色各样的佬友加入。对于一个在线社区来说，不断壮大和涌入新的血液是一件好事。但我每天都要问问自己，这里面有没有问题？真的完全是好事吗？在这个过程中我嗅到了一丝危险的气息：有人试图同质化这里，把这里当作互联网上另⋯阅读更多',
        category: '开发调优',
        tags: ['AI', 'Python', 'Java'],
        members: Array(5).fill(baseAvatar),
        comments: 139,
        views: '2.4k',
        time: '23小时前'
      }
    ])

    return { topics, selectedTopic, articles }
  }
}
</script>

<style scoped>
.home-page {
  background-color: white;
  color: black;
  height: calc(100vh - var(--header-height));
  display: flex;
  flex-direction: column;
  align-items: center;
  overflow-y: auto;
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

.search-input {
  display: flex;
  align-items: center;

  border: 1px solid lightgray;
  border-radius: 10px;
  padding: 10px;

  width: 100%;
  max-width: 600px;
  margin-top: 20px;
}

.search-input input {
  border: none;
  outline: none;
  font-size: 16px;
  width: 100%;
  margin-left: 10px;
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

.article-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  width: 100%;
  justify-content: space-between;
  border-bottom: 1px solid lightgray;
}

.article-main-container {
  margin-left: 20px;
  padding: 10px 0;
  width: 60%;
}

.article-item-title {
  font-size: 20px;
  text-decoration: none;  
  color: black;
}

.article-item-title:hover {
  color: var(--primary-color);
  text-decoration: underline;
}

.article-item-description {
  margin-top: 10px;
  font-size: 14px;
  color: gray;
}

.article-info-container {
  margin-top: 10px;
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 10px;
}

.article-info-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 10px;
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

.article-comments {
  margin-left: 20px;
}

.article-views {
  margin-left: 20px;
}

.article-time {
  margin-left: 20px;
  margin-right: 20px;
}


</style>
