<template>
  <div class="comment-editor-container">
    <div class="comment-editor-wrapper">
      <BaseInput textarea rows="3" v-model="text" placeholder="说点什么..." />
      <LoginOverlay v-if="showLoginOverlay" />
    </div>
    <div class="comment-bottom-container">
      <div class="comment-submit" :class="{ disabled: isDisabled }" @click="submit">
        <template v-if="!loading">
          确认
        </template>
        <template v-else>
          <i class="fa-solid fa-spinner fa-spin"></i> 发布中...
        </template>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed } from 'vue'
import BaseInput from './BaseInput.vue'
import LoginOverlay from './LoginOverlay.vue'

export default {
  name: 'SimpleCommentEditor',
  components: { BaseInput, LoginOverlay },
  emits: ['submit'],
  props: {
    loading: { type: Boolean, default: false },
    disabled: { type: Boolean, default: false },
    showLoginOverlay: { type: Boolean, default: false }
  },
  setup(props, { emit }) {
    const text = ref('')
    const isDisabled = computed(() => props.loading || props.disabled || !text.value.trim())
    const submit = () => {
      if (isDisabled.value) return
      emit('submit', text.value)
      text.value = ''
    }
    return { text, submit, isDisabled }
  }
}
</script>

<style scoped>
.comment-editor-container {
  margin-top: 20px;
  margin-bottom: 50px;
}

.comment-editor-wrapper {
  position: relative;
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
</style>

