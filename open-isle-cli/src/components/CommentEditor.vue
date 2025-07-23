<template>
  <div class="comment-editor-container">
    <div class="comment-editor-wrapper">      
      <div :id="editorId" ref="vditorElement"></div>
      <LoginOverlay v-if="showLoginOverlay" />
    </div>
    <div class="comment-bottom-container">
      <div class="comment-submit" :class="{ disabled: isDisabled }" @click="submit">
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
import { themeState } from '../utils/theme'
import 'vditor/dist/index.css'
import LoginOverlay from './LoginOverlay.vue'
import { isMobile } from '../utils/screen'
import { API_BASE_URL } from '../main'
import { getToken } from '../utils/auth'

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
    },
    showLoginOverlay: {
      type: Boolean,
      default: false
    }
  },
  components: { LoginOverlay },
  setup(props, { emit }) {
    const vditorInstance = ref(null)
    const text = ref('')
    const getEditorTheme = () =>
      document.documentElement.dataset.theme === 'dark' ? 'dark' : 'classic'
    const getPreviewTheme = () =>
      document.documentElement.dataset.theme === 'dark' ? 'dark' : 'light'
    const applyTheme = () => {
      if (vditorInstance.value) {
        vditorInstance.value.setTheme(getEditorTheme(), getPreviewTheme())
      }
    }

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
        height: isMobile.value ? 'auto' : 200,
        theme: getEditorTheme(),
        preview: {
          actions: [],
          markdown: { toc: false },
          theme: { current: getPreviewTheme() }
        },
        cdn: 'https://openisle-1307107697.cos.ap-guangzhou.myqcloud.com/assert/vditor',
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
          'upload'
        ],
        upload: {
          fieldName: 'file',
          url: `${API_BASE_URL}/api/upload`,
          accept: 'image/*,video/*',
          multiple: false,
          headers: { Authorization: `Bearer ${getToken()}` },
          format(files, responseText) {
            const res = JSON.parse(responseText)
            if (res.code === 0) {
              return JSON.stringify({
                code: 0,
                msg: '',
                data: {
                  errFiles: [],
                  succMap: { [files[0].name]: res.data.url }
                }
              })
            } else {
              return JSON.stringify({
                code: 1,
                msg: '上传失败',
                data: { errFiles: files.map(f => f.name), succMap: {} }
              })
            }
          }
        },
        toolbarConfig: { pin: true },
        input(value) {
          text.value = value
        },
        after() {
          if (props.loading || props.disabled) {
            vditorInstance.value.disabled()
          }
          applyTheme()
        }
      })
      // applyTheme()
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

    watch(
      () => themeState.mode,
      () => {
        applyTheme()
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
}

.comment-bottom-container {
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
  padding-top: 10px;
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

@media (max-width: 768px) {
  .comment-editor-container {
    margin-bottom: 10px;
  }
}
</style>
