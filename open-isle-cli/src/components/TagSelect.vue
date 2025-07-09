<template>
  <Dropdown v-model="selected" :fetch-options="fetchTags" multiple placeholder="选择标签" />
</template>

<script>
import { computed } from 'vue'
import { API_BASE_URL, toast } from '../main'
import Dropdown from './Dropdown.vue'

export default {
  name: 'TagSelect',
  components: { Dropdown },
  props: {
    modelValue: { type: Array, default: () => [] }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const fetchTags = async () => {
      const res = await fetch(`${API_BASE_URL}/api/tags`)
      if (!res.ok) return []
      const data = await res.json()
      return [{ id: 0, name: '无标签' }, ...data]
    }

    const selected = computed({
      get: () => props.modelValue,
      set: v => {
        if (Array.isArray(v)) {
          if (v.includes(0)) {
            emit('update:modelValue', [])
            return
          }
          if (v.length > 2) {
            toast.error('最多选择两个标签')
            return
          }
        }
        emit('update:modelValue', v)
      }
    })

    return { fetchTags, selected }
  }
}
</script>
