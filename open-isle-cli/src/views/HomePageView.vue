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
        <div v-for="topic in topics" :key="topic" class="topic-item" :class="{ selected: topic === selectedTopic }">
          {{ topic }}
        </div>
      </div>
    </div>

    <div class="article-container">
      <div class="header-container">
        <div class="header-item main">
          话题
        </div>
        <div class="header-item avatars">
          参与人员
        </div>
        <div class="header-item comments">
          回复
        </div>
        <div class="header-item views">
          浏览 
        </div>
        <div class="header-item activity">
          活动
        </div>
      </div>

      <div class="article-item" v-for="article in articles" :key="article.id">
        <div class="article-main-container">
          <router-link class="article-item-title" :to="`/posts/${article.id}`">
            {{ article.title }}
          </router-link>
          <div class="article-item-description">{{ sanitizeDescription(article.description) }}</div>
          <div class="article-info-container">
            <div class="article-info-item">
              <i class="fas fa-user"></i>
              <div class="article-info-item-text">{{ article.category }}</div>
            </div>

            <div class="article-tags-container">
              <div class="article-tag-item" v-for="tag in article.tags" :key="tag">
                <i class="fas fa-tag"></i>
                <div class="article-tag-item-text">{{ tag }}</div>
              </div>
            </div>
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
    </div>

  </div>
</template>

<script>
import { ref } from 'vue'
import { stripMarkdown } from '../utils/markdown'

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
          ref(`# 📢 社区公告

是的，L 站目前每天都有不少各色各样的佬友加入。对于一个在线社区来说，不断壮大和涌入新的血液是一件好事。

但我每天都要问问自己：**这里面有没有问题？真的完全是好事吗？**
在这个过程中我嗅到了一丝危险的气息——有人试图**同质化**这里，把这里当作互联网上**另一个可以随意发泄情绪**的地方！甚至试图占领舆论高地，把这里堂而皇之地变成**另一个垃圾场**。

> 这是要万分警惕并坚决予以打击的！

L 站的愿景是成为新的**理想型社区**，让每一个一身疲惫的佬友在这里得到放松。哪怕只有一刻能放松手中攥紧的武器，徜徉在和谐的氛围中得到喘息与治愈。

我和管理团队始终**坚定这一点，丝毫不会放松**！
千里之堤，溃于蚁穴——如果任由戾气蔓延、争端四起，最终这里的愿景将会完全破产。**有病要医，不是同路人不必强行融合。**任何把戾气带来这里、试图在此建立另一个互联网垃圾场的人，**都是不受欢迎的，都要被驱逐出社区。**

请好好说话，友善交流！我们完全支持并鼓励友好交流与分享，每个人都可以。**键盘**是你与人沟通、互通有无的桥梁，不只是你谋取私利的工具，更不是肆意挥舞用来攻击的武器。

---

## 🚫 自本公告发布之日起，我们将严肃处理以下 3 类发言：

1. **傲慢轻蔑回复**
2. **阴阳怪气回复**
3. **攻击谩骂回复**

如有以上发言，我们将视言论破坏程度采取（但不限于）**删帖、临时封禁、永久封禁**等举措。

> 请各位佬友积极监督，感谢你们为共建美好社区做出的贡献！
> **请一定一定不要把互联网上的戾气带来这里，这里就要做不一样。**

**持续时间：** *直至最后一个不会好好说话的账号持有者被请出社区为止。*`),
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
          'rt',
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

    const sanitizeDescription = (text) => stripMarkdown(text)

    return { topics, selectedTopic, articles, sanitizeDescription }
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

.header-container {
  display: flex;
  flex-direction: row;
  align-items: center;
  width: 100%;
  justify-content: space-between;
}

.article-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  width: 100%;
  justify-content: space-between;
  border-bottom: 1px solid lightgray;
}

.header-item {

}

.header-item.main {
  width: 60%;
  margin-left: 20px;
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

.header-item.avatars {
}

.header-item.comments {
  margin-left: 20px;
}

.header-item.views {
  margin-left: 20px;
}

.header-item.activity {
  margin-left: 20px;
  margin-right: 20px;
}
</style>
