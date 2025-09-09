<template>
  <div class="message-editor-container">
    <div class="message-editor-wrapper">
      <div :id="editorId" ref="vditorElement"></div>
    </div>
    <div class="message-bottom-container">
      <div class="message-submit" :class="{ disabled: isDisabled }" @click="submit">
        <template v-if="!loading"> 发送 </template>
        <template v-else> <loading-four /> 发送中... </template>
      </div>
    </div>
  </div>
</template>

<script>
import { computed, onMounted, onUnmounted, ref, useId, watch } from 'vue'
import { clearVditorStorage } from '~/utils/clearVditorStorage'
import { themeState } from '~/utils/theme'
import {
  createVditor,
  getEditorTheme as getEditorThemeUtil,
  getPreviewTheme as getPreviewThemeUtil,
} from '~/utils/vditor'
import '~/assets/global.css'

export default {
  name: 'MessageEditor',
  emits: ['submit'],
  props: {
    editorId: {
      type: String,
      default: '',
    },
    loading: {
      type: Boolean,
      default: false,
    },
    disabled: {
      type: Boolean,
      default: false,
    },
  },
  setup(props, { emit }) {
    const vditorInstance = ref(null)
    const text = ref('')
    const editorId = ref(props.editorId)
    if (!editorId.value) {
      editorId.value = 'editor-' + useId()
    }
    const getEditorTheme = getEditorThemeUtil
    const getPreviewTheme = getPreviewThemeUtil
    const applyTheme = () => {
      if (vditorInstance.value) {
        vditorInstance.value.setTheme(getEditorTheme(), getPreviewTheme())
      }
    }

    const isDisabled = computed(() => props.loading || props.disabled || !text.value.trim())

    const submit = () => {
      if (!vditorInstance.value || isDisabled.value) return
      const value = vditorInstance.value.getValue()
      emit('submit', value, () => {
        if (!vditorInstance.value) return
        vditorInstance.value.setValue('')
        text.value = ''
      })
    }

    onMounted(() => {
      vditorInstance.value = createVditor(editorId.value, {
        placeholder: '输入消息...',
        toolbar: [
          'emoji',
          'bold',
          'italic',
          'strike',
          'link',
          '|',
          'list',
          '|',
          'line',
          'quote',
          'code',
          'inline-code',
          '|',
          'upload',
        ],
        preview: {
          actions: [],
          markdown: { toc: false },
        },
        input(value) {
          text.value = value
        },
        after() {
          if (props.loading || props.disabled) {
            vditorInstance.value.disabled()
          }
          applyTheme()
        },
      })
    })

    onUnmounted(() => {
      clearVditorStorage()
    })

    watch(
      () => props.loading,
      (val) => {
        if (!vditorInstance.value) return
        if (val) {
          vditorInstance.value.disabled()
        } else if (!props.disabled) {
          vditorInstance.value.enable()
        }
      },
    )

    watch(
      () => props.disabled,
      (val) => {
        if (!vditorInstance.value) return
        if (val) {
          vditorInstance.value.disabled()
        } else if (!props.loading) {
          vditorInstance.value.enable()
        }
      },
    )

    watch(
      () => themeState.mode,
      () => {
        applyTheme()
      },
    )

    return { submit, isDisabled, editorId }
  },
}
</script>

<style scoped>
.message-editor-container {
  border: 1px solid var(--border-color);
  border-radius: 8px;
}

.message-bottom-container {
  display: flex;
  flex-direction: row;
  justify-content: flex-end;
  padding: 10px;
  background-color: var(--bg-color-soft);
  border-top: 1px solid var(--border-color);
  border-bottom-left-radius: 8px;
  border-bottom-right-radius: 8px;
}

.message-submit {
  background-color: var(--primary-color);
  color: #fff;
  padding: 8px 16px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s ease;
}

.message-submit.disabled {
  background-color: var(--primary-color-disabled);
  opacity: 0.6;
  cursor: not-allowed;
}

.message-submit:not(.disabled):hover {
  background-color: var(--primary-color-hover);
}

.message-editor-container .vditor {
  min-height: 80px;
  max-height: 150px;
  overflow-y: auto;
}
</style>
