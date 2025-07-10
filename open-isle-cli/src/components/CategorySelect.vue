<template>
  <Dropdown v-model="selected" :fetch-options="fetchCategories" placeholder="选择分类">
    <template #option="{ option }">
      <div class="option-main">
        <template v-if="option.icon">
          <img v-if="isImageIcon(option.icon)" :src="option.icon" class="option-icon" />
          <i v-else :class="['option-icon', option.icon]"></i>
        </template>
        <span>{{ option.name }}</span>
        <span v-if="option.count > 0"> x {{ option.count }}</span>
      </div>
      <div v-if="option.description" class="option-desc">{{ option.description }}</div>
    </template>
  </Dropdown>
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
      const data = await res.json()
      return [{ id: '', name: '无分类' }, ...data]
    }

    const isImageIcon = icon => {
      if (!icon) return false
      return /^https?:\/\//.test(icon) || icon.startsWith('/')
    }

    const selected = computed({
      get: () => props.modelValue,
      set: v => emit('update:modelValue', v)
    })

    return { fetchCategories, selected, isImageIcon }
  }
}
</script>

<style scoped>
.option-main {
  display: flex;
  align-items: center;
  gap: 5px;
}

.option-desc {
  font-size: 12px;
  color: #666;
  margin-left: 21px;
}
</style>
