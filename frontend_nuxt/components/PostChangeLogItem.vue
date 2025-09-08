<template>
  <div :id="`change-log-${log.id}`" class="change-log-container">
    <div class="change-log-text">
      <span class="change-log-user">{{ log.username }}</span>
      <span v-if="log.type === 'CONTENT'">
        变更了文章内容
        <div class="content-diff" v-html="diffHtml"></div>
      </span>
      <span v-else-if="log.type === 'TITLE'">变更了文章标题</span>
      <span v-else-if="log.type === 'CATEGORY'">变更了文章分类</span>
      <span v-else-if="log.type === 'TAG'">变更了文章标签</span>
      <span v-else-if="log.type === 'CLOSED'">
        <template v-if="log.newClosed">关闭了文章</template>
        <template v-else>重新打开了文章</template>
      </span>
      <span v-else-if="log.type === 'PINNED'">
        <template v-if="log.newPinnedAt">置顶了文章</template>
        <template v-else>取消置顶了文章</template>
      </span>
      <span v-else-if="log.type === 'FEATURED'">
        <template v-if="log.newFeatured">将文章设为精选</template>
        <template v-else>取消精选文章</template>
      </span>
    </div>
    <div class="change-log-time">{{ log.time }}</div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { html } from 'diff2html'
import { createTwoFilesPatch } from 'diff'
import 'diff2html/bundles/css/diff2html.min.css'
const props = defineProps({ log: Object })
const diffHtml = computed(() => {
  if (props.log.type === 'CONTENT') {
    const oldContent = props.log.oldContent ?? ''
    const newContent = props.log.newContent ?? ''
    const diff = createTwoFilesPatch('old', 'new', oldContent, newContent)
    return html(diff, { inputFormat: 'diff', showFiles: false, matching: 'lines' })
  }
  return ''
})
</script>

<style scoped>
.change-log-container {
  display: flex;
  flex-direction: column;
  padding-bottom: 30px;
  opacity: 0.7;
}
.change-log-user {
  font-weight: bold;
  margin-right: 4px;
}
.change-log-time {
  font-size: 12px;
  opacity: 0.6;
}

.content-diff {
  margin-top: 8px;
}
</style>
