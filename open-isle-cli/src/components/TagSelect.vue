<template>
  <Dropdown v-model="selected" :fetch-options="fetchTags" multiple placeholder="选择标签" />
</template>

<script>
import { computed } from 'vue'
import { API_BASE_URL } from '../main'
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
      return await res.json()
    }

    const selected = computed({
      get: () => props.modelValue,
      set: v => emit('update:modelValue', v)
    })

    return { fetchTags, selected }
  }
}
</script>
