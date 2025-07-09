<template>
  <div
    class="info-content-container"
    :id="'comment-' + comment.id"
    :style="{
      ...(level > 0 ? { /*borderLeft: '1px solid #e0e0e0', */borderBottom: 'none' } : {})
    }"
  >
    <!-- <div class="user-avatar-container">
      <div class="user-avatar-item">
        <img class="user-avatar-item-img" :src="comment.avatar" alt="avatar" />
      </div>
    </div>   -->
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
      <CommentEditor v-if="showEditor" @submit="submitReply" :loading="isWaitingForReply" />
      <div
        v-if="comment.reply && comment.reply.length"
        class="reply-toggle"
        @click="toggleReplies"
      >
        <i v-if="showReplies" class="fas fa-chevron-up reply-toggle-icon"></i> 
        <i v-else class="fas fa-chevron-down reply-toggle-icon"></i> 
        
        {{ comment.reply.length }}条回复
      </div>
      <div v-if="showReplies" class="reply-list">
        <BaseTimeline :items="comment.reply" >
          <template #item="{ item }">
            <CommentItem
              :key="item.id"
              :comment="item"
              :level="level + 1"
              :default-show-replies="item.openReplies"
            />
          </template>
        </BaseTimeline>
        <!-- <CommentItem
          v-for="r in comment.reply"
          :key="r.id"
          :comment="r"
          :level="level + 1"
          :default-show-replies="r.openReplies"
        /> -->
      </div>
    </div>
  </div>
</template>

<script>
import { ref, watch } from 'vue'
import { useRouter } from 'vue-router'
import CommentEditor from './CommentEditor.vue'
import { renderMarkdown } from '../utils/markdown'
import BaseTimeline from './BaseTimeline.vue'
import { API_BASE_URL, toast } from '../main'
import { getToken } from '../utils/auth'
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
    },
    defaultShowReplies: {
      type: Boolean,
      default: false
    }
  },
  setup(props) {
    const router = useRouter()
    const showReplies = ref(props.defaultShowReplies)
    watch(
      () => props.defaultShowReplies,
      (val) => {
        showReplies.value = val
      }
    )
    const showEditor = ref(false)
    const isWaitingForReply = ref(false)
    const toggleReplies = () => {
      showReplies.value = !showReplies.value
    }
    const toggleEditor = () => {
      showEditor.value = !showEditor.value
    }
    const submitReply = async (text) => {
      if (!text.trim()) return
      isWaitingForReply.value = true
      const token = getToken()
      if (!token) {
        toast.error('请先登录')
        isWaitingForReply.value = false
        return
      }
      try {
        const res = await fetch(`${API_BASE_URL}/api/comments/${props.comment.id}/replies`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
          body: JSON.stringify({ content: text })
        })
        if (res.ok) {
          const data = await res.json()
          const replyList = props.comment.reply || (props.comment.reply = [])
          replyList.push({
            id: data.id,
            userName: data.author.username,
            time: new Date(data.createdAt).toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' }),
            avatar: data.author.avatar,
            text: data.content,
            reply: (data.replies || []).map(r => ({
              id: r.id,
              userName: r.author.username,
              time: new Date(r.createdAt).toLocaleDateString('zh-CN', { month: 'numeric', day: 'numeric' }),
              avatar: r.author.avatar,
              text: r.content,
              reply: [],
              openReplies: false,
              src: r.author.avatar,
              iconClick: () => router.push(`/users/${r.author.id}`)
            })),
            openReplies: false,
            src: data.author.avatar,
            iconClick: () => router.push(`/users/${data.author.id}`)
          })
          showEditor.value = false
          toast.success('回复成功')
        } else {
          toast.error('回复失败')
        }
      } catch (e) {
        toast.error('回复失败')
      } finally {
        isWaitingForReply.value = false
      }
    }
    const copyCommentLink = () => {
      const link = `${location.origin}${location.pathname}#comment-${props.comment.id}`
      navigator.clipboard.writeText(link).then(() => {
        toast.success('已复制')
      })
    }
    return { showReplies, toggleReplies, showEditor, toggleEditor, submitReply, copyCommentLink, renderMarkdown, isWaitingForReply }
  }
}
CommentItem.components = { CommentItem, CommentEditor, BaseTimeline }
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

.reply-toggle-icon {
  margin-right: 5px;
}

@keyframes highlight {
  from { background-color: yellow; }
  to { background-color: transparent; }
}

</style>
