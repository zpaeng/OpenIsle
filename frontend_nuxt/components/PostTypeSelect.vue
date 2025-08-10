<template>
  <Dropdown v-model="selected" :fetch-options="fetchOptions" placeholder="帖子类型" :initial-options="options" />
</template>

<script>
import { computed } from 'vue'
import Dropdown from '~/components/Dropdown.vue'

export default {
  name: 'PostTypeSelect',
  components: { Dropdown },
  props: { modelValue: { type: String, default: 'NORMAL' } },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const options = [
      { id: 'NORMAL', name: '普通帖' },
      { id: 'LOTTERY', name: '抽奖贴' }
    ]
    const fetchOptions = async () => options
    const selected = computed({
      get: () => props.modelValue,
      set: v => emit('update:modelValue', v)
    })
    return { selected, fetchOptions, options }
  }
}
</script>
