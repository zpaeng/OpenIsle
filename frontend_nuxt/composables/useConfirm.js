import { ref } from 'vue'

const state = ref({
  visible: false,
  title: '',
  message: '',
  resolve: null,
  reject: null,
})

export const useConfirm = () => {
  const confirm = (title, message) => {
    state.value.title = title
    state.value.message = message
    state.value.visible = true
    return new Promise((resolve, reject) => {
      state.value.resolve = resolve
      state.value.reject = reject
    })
  }

  const onConfirm = () => {
    if (state.value.resolve) {
      state.value.resolve(true)
    }
    state.value.visible = false
  }

  const onCancel = () => {
    if (state.value.reject) {
      state.value.reject(false)
    }
    state.value.visible = false
  }

  return {
    confirm,
    onConfirm,
    onCancel,
    state,
  }
}