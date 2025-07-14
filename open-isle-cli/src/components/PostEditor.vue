<template>
  <div class="post-editor-container">
    <div :id="editorId" ref="vditorElement"></div>
    <div v-if="loading" class="editor-loading-overlay">
      <l-hatch size="28" stroke="4" speed="3.5" color="var(--primary-color)"></l-hatch>
    </div>
  </div>
</template>

<script>
import { ref, onMounted, watch } from 'vue'
import Vditor from 'vditor'
import 'vditor/dist/index.css'
import { API_BASE_URL } from '../main'
import { getToken } from '../utils/auth'
import { hatch } from 'ldrs'
hatch.register()


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

    watch(
      () => props.loading,
      val => {
        if (val) {
          vditorInstance.value.disabled()
        } else {
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
      () => props.modelValue,
      val => {
        if (vditorInstance.value && vditorInstance.value.getValue() !== val) {
          vditorInstance.value.setValue(val)
        }
      }
    )

    onMounted(() => {
      vditorInstance.value = new Vditor(props.editorId, {
        placeholder: '请输入正文...',
        height: 450,
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
        cache: { enable: false },
        input(value) {
          emit('update:modelValue', value)
        },
        after() {
          vditorInstance.value.setValue(props.modelValue)
          if (props.loading || props.disabled) {
            vditorInstance.value.disabled()
          }
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
  position: relative;
}

.editor-loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(255, 255, 255, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  pointer-events: all;
  z-index: 10;
}
</style>
