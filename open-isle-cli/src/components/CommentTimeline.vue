<template>
  <BaseTimeline :items="timelineItems">
    <template #item="{ item }">
      <div class="comment-timeline-item">
        <CommentItem
          :comment="item"
          :level="level"
          :default-show-replies="item.openReplies"
          :show-avatar="false"
        />
        <div v-if="item.reply && item.reply.length" class="reply-list">
          <CommentTimeline :comments="item.reply" :level="level + 1" />
        </div>
      </div>
    </template>
  </BaseTimeline>
</template>

<script>
import BaseTimeline from './BaseTimeline.vue'
import CommentItem from './CommentItem.vue'

export default {
  name: 'CommentTimeline',
  components: { BaseTimeline, CommentItem },
  props: {
    comments: {
      type: Array,
      default: () => []
    },
    level: {
      type: Number,
      default: 0
    }
  },
  computed: {
    timelineItems () {
      return this.comments.map(c => Object.assign({ src: c.avatar }, c))
    }
  }
}
</script>

<style scoped>
.comment-timeline-item {
  width: 100%;
}
.reply-list {
  margin-left: 30px;
}
</style>
