<template>
  <Dropdown v-model="selected" :fetch-options="fetchCategories" placeholder="选择分类" />
</template>

<script>
import { computed } from 'vue'
import { API_BASE_URL } from '../main'
import Dropdown from './Dropdown.vue'

export default {
  name: 'CategorySelect',
  components: { Dropdown },
  props: {
    modelValue: { type: [String, Number], default: '' }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const fetchCategories = async () => {
      const res = await fetch(`${API_BASE_URL}/api/categories`)
      if (!res.ok) return []
      return await res.json()
    }

    const selected = computed({
      get: () => props.modelValue,
      set: v => emit('update:modelValue', v)
    })

    return { fetchCategories, selected }
  }
}
</script>
