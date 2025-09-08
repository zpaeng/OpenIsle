<template>
  <div :id="`change-log-${log.id}`" class="change-log-container">
    <div class="change-log-text">
      <BaseImage
        v-if="log.userAvatar"
        class="change-log-avatar"
        :src="log.userAvatar"
        alt="avatar"
      />
      <span v-if="log.username" class="change-log-user">{{ log.username }}</span>
      <span v-if="log.type === 'CONTENT'">变更了文章内容</span>
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
      <span v-else-if="log.type === 'VOTE_RESULT'">投票已出结果</span>
      <span v-else-if="log.type === 'LOTTERY_RESULT'">抽奖已开奖</span>
    </div>
    <div class="change-log-time">{{ log.time }}</div>
    <div
      v-if="log.type === 'CONTENT' || log.type === 'TITLE'"
      class="content-diff"
      v-html="diffHtml"
    ></div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { html } from 'diff2html'
import { createTwoFilesPatch } from 'diff'
import { useIsMobile } from '~/utils/screen'
import 'diff2html/bundles/css/diff2html.min.css'
import BaseImage from '~/components/BaseImage.vue'
const props = defineProps({
  log: Object,
  title: String,
})

const diffHtml = computed(() => {
  const isMobile = useIsMobile()
  if (props.log.type === 'CONTENT') {
    const oldContent = props.log.oldContent ?? ''
    const newContent = props.log.newContent ?? ''
    const diff = createTwoFilesPatch(props.title, props.title, oldContent, newContent)
    return html(diff, {
      inputFormat: 'diff',
      showFiles: false,
      matching: 'lines',
      drawFileList: false,
      outputFormat: isMobile.value ? 'line-by-line' : 'side-by-side',
    })
  } else if (props.log.type === 'TITLE') {
    const oldTitle = props.log.oldTitle ?? ''
    const newTitle = props.log.newTitle ?? ''
    const diff = createTwoFilesPatch(oldTitle, newTitle, '', '')
    return html(diff, {
      inputFormat: 'diff',
      showFiles: false,
      matching: 'lines',
      drawFileList: false,
      outputFormat: isMobile.value ? 'line-by-line' : 'side-by-side',
    })
  }
  return ''
})
</script>

<style scoped>
.change-log-container {
  display: flex;
  flex-direction: column;
  /* padding-bottom: 30px; */
  opacity: 0.7;
  font-size: 14px;
}
.change-log-text {
  display: flex;
  align-items: center;
}
.change-log-user {
  font-weight: bold;
  margin-right: 4px;
}
.change-log-avatar {
  width: 20px;
  height: 20px;
  border-radius: 50%;
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
