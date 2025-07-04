<template>
  <div class="post-editor-container">
    <div :id="editorId" ref="vditorElement"></div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import Vditor from 'vditor'
import 'vditor/dist/index.css'

export default {
  name: 'PostEditor',
  emits: ['update:modelValue'],
  props: {
    modelValue: {
      type: String,
      default: ''
    },
    editorId: {
      type: String,
      default: () => 'post-editor-' + Math.random().toString(36).slice(2)
    }
  },
  setup(props, { emit }) {
    const vditorInstance = ref(null)

    onMounted(() => {
      vditorInstance.value = new Vditor(props.editorId, {
        placeholder: '请输入正文...',
        height: 400,
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
        toolbarConfig: { pin: true },
        cache: { enable: false },
        input(value) {
          emit('update:modelValue', value)
        },
        after() {
          vditorInstance.value.setValue(props.modelValue)
        }
      })
    })

    return {}
  }
}
</script>

<style scoped>
.post-editor-container {
  border: 1px solid #e2e2e2;
}
</style>

