<template>
  <div class="comment-editor-container">
    <div :id="editorId" ref="vditorElement"></div>
    <div class="comment-bottom-container">
      <div
        class="comment-submit"
        :class="{ disabled: isDisabled }"
        @click="submit"
      >
        <template v-if="!loading">
          发布评论
        </template>
        <template v-else>
          <i class="fa-solid fa-spinner fa-spin"></i> 发布中...
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, computed, watch } from 'vue'
import Vditor from 'vditor'
import 'vditor/dist/index.css'

export default {
  name: 'CommentEditor',
  emits: ['submit'],
  props: {
    editorId: {
      type: String,
      default: () => 'editor-' + Math.random().toString(36).slice(2)
    },
    loading: {
      type: Boolean,
      default: false
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  setup(props, { emit }) {
    const vditorInstance = ref(null)
    const text = ref('')

    const isDisabled = computed(() => props.loading || props.disabled || !text.value.trim())

    const submit = () => {
      if (!vditorInstance.value || isDisabled.value) return
      const value = vditorInstance.value.getValue()
      emit('submit', value)
      vditorInstance.value.setValue('')
      text.value = ''
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
        toolbarConfig: { pin: true },
       input(value) {
         text.value = value
       }
     })
      if (props.disabled || props.loading) {
        vditorInstance.value.disabled()
      }
    })

    watch(
      () => props.loading,
      val => {
        if (!vditorInstance.value) return
        if (val) {
          vditorInstance.value.disabled()
        } else if (!props.disabled) {
          vditorInstance.value.enable()
        }
      }
    )

    watch(
      () => props.disabled,
      val => {
        if (!vditorInstance.value) return
        if (val) {
          vditorInstance.value.disabled()
        } else if (!props.loading) {
          vditorInstance.value.enable()
        }
      }
    )

    return { submit, isDisabled }
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
.comment-submit.disabled {
  background-color: var(--primary-color-disabled);
  opacity: 0.5;
  cursor: not-allowed;
}
.comment-submit.disabled:hover {
  background-color: var(--primary-color-disabled);
}
.comment-submit:hover {
  background-color: var(--primary-color-hover);
}
</style>
