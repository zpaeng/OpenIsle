<template>
  <div
    class="info-content-container"
    :id="'comment-' + comment.id"
    :style="{
      ...(level > 0 ? { /*borderLeft: '1px solid #e0e0e0', */borderBottom: 'none' } : {})
    }"
  >
    <div class="user-avatar-container">
      <div class="user-avatar-item">
        <img class="user-avatar-item-img" :src="comment.avatar" alt="avatar" />
      </div>
    </div>  
    <div class="info-content">
      <div class="info-content-header">
        <div class="user-name">{{ comment.userName }}</div>
        <div class="post-time">{{ comment.time }}</div>
      </div>
      <div class="info-content-text" v-html="renderMarkdown(comment.text)"></div>
      <div class="article-footer-container">
        <div class="reactions-container">
          <div class="reactions-viewer">
            <div class="reactions-viewer-item-container">
              <div class="reactions-viewer-item">🤣</div>
              <div class="reactions-viewer-item">❤️</div>
              <div class="reactions-viewer-item">👏</div>
            </div>
            <div class="reactions-count">1882</div>
          </div>
          <div class="make-reaction-container">
          <div class="make-reaction-item comment-reaction" @click="toggleEditor">
            <i class="far fa-comment"></i>
          </div>
            <div class="make-reaction-item like-reaction">
              <i class="far fa-heart"></i>
            </div>
            <div class="make-reaction-item copy-link" @click="copyCommentLink">
              <i class="fas fa-link"></i>
            </div>
          </div>
        </div>
      </div>
      <CommentEditor v-if="showEditor" @submit="submitReply" />
      <div v-if="comment.reply && comment.reply.length" class="reply-toggle" @click="toggleReplies">
        {{ comment.reply.length }}条回复
      </div>
      <div v-if="showReplies" class="reply-list">
        <CommentItem
          v-for="r in comment.reply"
          :key="r.id"
          :comment="r"
          :level="level + 1"
        />
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import CommentEditor from './CommentEditor.vue'
import { renderMarkdown } from '../utils/markdown'
const CommentItem = {
  name: 'CommentItem',
  props: {
    comment: {
      type: Object,
      required: true
    },
    level: {
      type: Number,
      default: 0
    }
  },
  setup(props) {
    const showReplies = ref(false)
    const showEditor = ref(false)
    const toggleReplies = () => {
      showReplies.value = !showReplies.value
    }
    const toggleEditor = () => {
      showEditor.value = !showEditor.value
    }
    const submitReply = (text) => {
      if (!text.trim()) return
      const replyList = props.comment.reply || (props.comment.reply = [])
      replyList.push({
        id: Date.now(),
        userName: '你',
        time: new Date().toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' }),
        avatar: 'https://picsum.photos/200/200',
        text,
        reply: []
      })
      showEditor.value = false
    }
    const copyCommentLink = () => {
      const link = `${location.origin}${location.pathname}#comment-${props.comment.id}`
      navigator.clipboard.writeText(link)
    }
    return { showReplies, toggleReplies, showEditor, toggleEditor, submitReply, copyCommentLink, renderMarkdown }
  }
}
CommentItem.components = { CommentItem, CommentEditor }
export default CommentItem
</script>

<style scoped>
.reply-toggle {
  cursor: pointer;
  color: var(--primary-color);
  user-select: none;
}

.reply-list {
}

.comment-reaction {
  color: var(--primary-color);
}

.comment-reaction:hover {
  background-color: lightgray;
}

.comment-highlight {
  animation: highlight 2s;
}

@keyframes highlight {
  from { background-color: yellow; }
  to { background-color: transparent; }
}

</style>
