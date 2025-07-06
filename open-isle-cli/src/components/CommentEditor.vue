<template>
  <div class="comment-editor-container">
    <div :id="editorId" ref="vditorElement"></div>
    <div class="comment-bottom-container">
      <div class="comment-submit" @click="submit">发布评论</div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import Vditor from 'vditor'
import 'vditor/dist/index.css'

export default {
  name: 'CommentEditor',
  emits: ['submit'],
  props: {
    editorId: {
      type: String,
      default: () => 'editor-' + Math.random().toString(36).slice(2)
    }
  },
  setup(props, { emit }) {
    const vditorInstance = ref(null)

    const submit = () => {
      if (!vditorInstance.value) return
      const text = vditorInstance.value.getValue()
      if (!text.trim()) return
      emit('submit', text)
      vditorInstance.value.setValue('')
    }

    onMounted(() => {
      vditorInstance.value = new Vditor(props.editorId, {
        placeholder: '说点什么...',
        height: 120,
        theme: 'classic',
        preview: {
          theme: { current: 'light' },
          actions: [],
          markdown: { toc: false }
        },
        toolbar: [
          'emoji',
          'bold',
          'italic',
          'strike',
          '|',
          'list',
          'line',
          'quote',
          'code',
          'inline-code',
          '|',
          'undo',
          'redo',
          '|',
          'link',
          'image'
        ],
        toolbarConfig: { pin: true }
      })
    })

    return { submit }
  }
}
</script>

<style scoped>
.comment-editor-container {
  margin-top: 20px;
  margin-bottom: 50px;
  border: 1px solid #e2e2e2;
}
.comment-bottom-container {
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
  padding: 10px;
}
.comment-submit {
  background-color: var(--primary-color);
  color: #fff;
  padding: 10px 20px;
  border-radius: 10px;
  font-size: 14px;
  cursor: pointer;
}
.comment-submit:hover {
  background-color: var(--primary-color-hover);
}
</style>
