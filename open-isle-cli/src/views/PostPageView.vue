<template>
  <div class="post-page-container">
    <div class="post-page-main-container" ref="mainContainer" @scroll="onScroll">
      <div class="article-title-container">
        <div class="article-title">请不要把互联网上的戾气带来这里！</div>
        <div class="article-info-container">
          <div class="article-info-item">
            <i class="fas fa-user"></i>
            <div class="article-info-item-text">开发调优</div>
          </div>

          <div class="article-tags-container">
            <div class="article-tag-item" v-for="tag in tags" :key="tag">
              <i class="fas fa-tag"></i>
              <div class="article-tag-item-text">{{ tag }}</div>
            </div>
          </div>
        </div>
      </div>

      <div class="info-content-container" ref="postItems">
        <div class="user-avatar-container">
          <div class="user-avatar-item">
            <img class="user-avatar-item-img" src="https://picsum.photos/200/200" alt="avatar">
          </div>
        </div>

        <div class="info-content">
          <div class="info-content-header">
            <div class="user-name">Nagisa77</div>
            <div class="post-time">{{ postTime }}</div>
          </div>
          <div class="info-content-text">
            是的，L站目前每天都有不少各色各样的佬友加入。对于一个在线社区来说，不断壮大和涌入新的血液是一件好事。

            但我每天都要问问自己，这里面有没有问题？真的完全是好事吗？在这个过程中我嗅到了一丝危险的气息：有人试图同质化这里，把这里当作互联网上另一个可以随意发泄情绪的地方！甚至试图占领舆论高地，把这里堂而皇之地变成另一个垃圾场。

            这是要万分警惕和坚决予以打击的！L站的愿景是成为新的理想型社区，让每一个一身疲惫的佬友在这里得到放松。哪怕有一刻放松手中攥紧的武器，徜徉在和谐的氛围中得到喘息和治愈。

            我和管理们都始终坚定这一点，丝毫不会放松！千里之堤溃于蚁穴，如果任由戾气蔓延，争端四起，最终这里的愿景将会完全破产。所以有病要医，不是同路人不必强行融合。任何把戾气带来这里，试图在这里建立另一个互联网垃圾场的人都是不受欢迎的，都要被驱逐出社区。

            请好好说话，友善交流！我们完全支持、鼓励友好交流分享，每个人都可以。键盘是你与人交流分享、互通有无的桥梁，不只是你谋取私利的工具，更不是肆意挥舞用来攻击的武器。

            自本公告发布之日起，我们将严肃处理以下3类发言：

            傲慢轻蔑回复。
            阴阳怪气回复。
            攻击谩骂回复。
            如有以上发言，我们将视言论破坏程度采取但不限于：删帖、临时封禁、永久封禁等举措。请各位佬友帮忙积极监督，感谢你们为共建美好社区做出的贡献！请一定一定不要把互联网上的戾气带来这里，这里就要做不一样。

            持续时间至最后一个不会好好说话的账号持有者被请出社区为止。
          </div>

          <div class="article-footer-container">
            <div class="reactions-container">
              <div class="reactions-viewer">
                <div class="reactions-viewer-item-container">
                  <div class="reactions-viewer-item">
                    🤣
                  </div>
                  <div class="reactions-viewer-item">
                    ❤️
                  </div>
                  <div class="reactions-viewer-item">
                    👏
                  </div>
                </div>
                <div class="reactions-count">1882</div>
              </div>

              <div class="make-reaction-container">
                <div class="make-reaction-item like-reaction">
                  <i class="far fa-heart"></i>
                </div>
                <div class="make-reaction-item copy-link">
                  <i class="fas fa-link"></i>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <div class="comments-container">
        <div class="info-content-container" v-for="comment in comments" :key="comment.id" ref="postItems">
          <div class="user-avatar-container">
            <div class="user-avatar-item">
              <img class="user-avatar-item-img" :src="comment.avatar" alt="avatar">
            </div>
          </div>

          <div class="info-content">
            <div class="info-content-header">
              <div class="user-name">{{ comment.userName }}</div>
              <div class="post-time">{{ comment.time }}</div>
            </div>
            <div class="info-content-text">
              {{ comment.text }}
            </div>

            <div class="article-footer-container">
              <div class="reactions-container">
                <div class="reactions-viewer">
                  <div class="reactions-viewer-item-container">
                    <div class="reactions-viewer-item">
                      🤣
                    </div>
                    <div class="reactions-viewer-item">
                      ❤️
                    </div>
                    <div class="reactions-viewer-item">
                      👏
                    </div>
                  </div>
                  <div class="reactions-count">1882</div>
                </div>

                <div class="make-reaction-container">
                  <div class="make-reaction-item like-reaction">
                    <i class="far fa-heart"></i>
                  </div>
                  <div class="make-reaction-item copy-link">
                    <i class="fas fa-link"></i>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="post-page-scroller-container">
      <div class="scroller">
        <div class="scroller-time">{{ postTime }}</div>
        <div class="scroller-middle">
          <input type="range" class="scroller-range" :max="totalPosts" :min="1" v-model.number="currentIndex"
            @input="onSliderInput" />
          <div class="scroller-index" :style="indexStyle">{{ currentIndex }}/{{ totalPosts }}</div>
        </div>
        <div class="scroller-time">{{ lastReplyTime }}</div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'

export default {
  name: 'PostPageView',
  setup() {
    const tags = ref(['AI', 'Python', 'Java'])
    const comments = ref([
      {
        id: 1,
        userName: 'Nagisa77',
        time: '3月10日',
        avatar: 'https://picsum.photos/200/200',
        text: '沙发🛋️🛋️🛋️🛋️'
      },
      {
        id: 2,
        userName: 'Nagisa77',
        time: '3月11日',
        avatar: 'https://picsum.photos/200/200',
        text: '💩💩💩💩💩'
      },
      {
        id: 3,
        userName: 'Nagisa77',
        time: '3月12日',
        avatar: 'https://picsum.photos/200/200',
        text: '是的'
      },
      {
        id: 4,
        userName: 'Nagisa77',
        time: '3月13日',
        avatar: 'https://picsum.photos/200/200',
        text: '持续时间至最后一个不会好好说话的账号持有者被请出社区为止。'
      },
      {
        id: 5,
        userName: 'Nagisa77',
        time: '3月14日',
        avatar: 'https://picsum.photos/200/200',
        text: '赞同楼主'
      },
      {
        id: 6,
        userName: 'Nagisa77',
        time: '3月15日',
        avatar: 'https://picsum.photos/200/200',
        text: '这里面有没有问题？真的完全是好事吗？在这个过程中我嗅到了一丝危险的气息'
      }
    ])
    const postTime = ref('3月10日')
    const postItems = ref([])
    const mainContainer = ref(null)
    const currentIndex = ref(1)
    const totalPosts = computed(() => comments.value.length + 1)
    const lastReplyTime = computed(() =>
      comments.value.length ? comments.value[comments.value.length - 1].time : postTime.value
    )
    const updateCurrentIndex = () => {
      const scrollTop = mainContainer.value ? mainContainer.value.scrollTop : 0
      for (let i = 0; i < postItems.value.length; i++) {
        const el = postItems.value[i]
        if (el.offsetTop + el.offsetHeight > scrollTop) {
          currentIndex.value = i + 1
          break
        }
      }
    }

    const onSliderInput = () => {
      const target = postItems.value[currentIndex.value - 1]
      if (target && mainContainer.value) {
        mainContainer.value.scrollTo({ top: target.offsetTop, behavior: 'instant' })
      }
    }

    onMounted(() => {
      updateCurrentIndex()
    })

    return {
      tags,
      comments,
      postTime,
      lastReplyTime,
      postItems,
      mainContainer,
      currentIndex,
      totalPosts,
      onSliderInput,
      onScroll: updateCurrentIndex
    }
  }
}
</script>

<style scoped>
.post-page-container {
  display: flex;
  flex-direction: row;
  height: calc(100vh - var(--header-height));
}

.post-page-main-container {
  overflow-y: auto;
  scrollbar-width: none;
  padding: 20px;
  height: calc(100% - 40px);
  width: calc(85% - 40px);
}

.post-page-scroller-container {
  display: flex;
  flex-direction: column;
  width: 15%;
}

.scroller {
  margin-top: 20px;
  margin-left: 20px;
}

.scroller-time {
  font-size: 14px;
  opacity: 0.5;
}

.scroller-middle {
  margin: 10px 0;
  margin-left: 10px;
  display: flex;
  flex-direction: row;
  gap: 8px;
}

.scroller-range {
  writing-mode: vertical-rl;
  direction: ltr;
  height: 300px;
  width: 2px;
  -webkit-appearance: none;
  background: transparent;
}

.scroller-range::-webkit-slider-runnable-track {
  width: 1px;
  height: 100%;
  background-color: var(--scroller-background-color);
}

.scroller-range::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 6px;
  height: 60px;
  right: 2px;
  border-radius: 3px;
  background-color: var(--scroller-background-color);
  cursor: pointer;
}

.scroller-range::-moz-range-track {
  width: 2px;
  height: 100%;
  background-color: #ccc;
  border-radius: 1px;
}

.scroller-range::-moz-range-thumb {
  width: 10px;
  height: 10px;
  background-color: #333;
  border-radius: 50%;
  cursor: pointer;
}

.scroller-index {
  font-size: 17px;
  font-weight: bold;
  margin-top: 10px;
}

.article-title-container {
  display: flex;
  flex-direction: column;
}

.article-title {
  font-size: 30px;
  font-weight: bold;
}

.article-info-container {
  display: flex;
  flex-direction: row;
  margin-top: 10px;
  gap: 10px;
  align-items: center;
}

.article-info-item {
  display: flex;
  flex-direction: row;
  gap: 5px;
  align-items: center;
}

.article-tags-container {
  display: flex;
  flex-direction: row;
  gap: 10px;
}

.article-tag-item {
  display: flex;
  flex-direction: row;
  gap: 5px;
  align-items: center;
}

.info-content-container {
  margin-top: 20px;
  display: flex;
  width: 100%;
  flex-direction: row;
  gap: 10px;
  border-bottom: 1px solid #e2e2e2;
}

.user-avatar-container {}

.user-avatar-item {
  width: 50px;
  height: 50px;
}

.user-avatar-item-img {
  width: 100%;
  height: 100%;
  border-radius: 50%;
}

.info-content {
  display: flex;
  width: 100%;
  flex-direction: column;
  gap: 10px;
}

.info-content-header {
  display: flex;
  flex-direction: row;
  gap: 10px;
  align-items: center;
  justify-content: space-between;
}

.user-name {
  font-size: 16px;
  font-weight: bold;
  opacity: 0.7;
}

.post-time {
  font-size: 14px;
  opacity: 0.5;
}

.info-content-text {
  font-size: 16px;
  line-height: 1.8;
  opacity: 0.7;
  width: 100%;
}

.article-footer-container {
  display: flex;
  flex-direction: row;
  gap: 10px;
  margin-top: 60px;
}

.reactions-container {
  display: flex;
  flex-direction: row;
  gap: 10px;
  align-items: center;
  width: 100%;
  justify-content: space-between;
}

.reactions-viewer {
  display: flex;
  flex-direction: row;
  gap: 20px;
  align-items: center;
}

.reactions-viewer-item-container {
  display: flex;
  flex-direction: row;
  gap: 2px;
  align-items: center;
}

.reactions-viewer-item {
  font-size: 16px;
}

.reactions-count {
  font-size: 16px;
  opacity: 0.5;
}

.make-reaction-container {
  display: flex;
  flex-direction: row;
  gap: 20px;
}

.make-reaction-item {
  cursor: pointer;
  padding: 10px;
  border-radius: 50%;
  opacity: 0.7;
}

.like-reaction {
  font-size: 20px;
  color: #ff0000;
}

.like-reaction:hover {
  background-color: #ffe2e2;
}

.copy-link {
  font-size: 20px;
}

.copy-link:hover {
  background-color: #e2e2e2;
}
</style>
