<template>
  <Dropdown
    v-model="selected"
    :fetch-options="fetchTags"
    multiple
    placeholder="选择标签"
    remote
  >
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
import { computed, ref } from 'vue'
import { API_BASE_URL, toast } from '../main'
import Dropdown from './Dropdown.vue'

export default {
  name: 'TagSelect',
  components: { Dropdown },
  props: {
    modelValue: { type: Array, default: () => [] },
    creatable: { type: Boolean, default: false }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const localTags = ref([])

    const isImageIcon = icon => {
      if (!icon) return false
      return /^https?:\/\//.test(icon) || icon.startsWith('/')
    }

    const fetchTags = async (kw = '') => {
      const url = new URL(`${API_BASE_URL}/api/tags`)
      if (kw) url.searchParams.set('keyword', kw)
      url.searchParams.set('limit', '10')
      let data = []
      try {
        const res = await fetch(url.toString())
        if (res.ok) {
          data = await res.json()
        }
      } catch {}

      let options = [...data, ...localTags.value]

      if (props.creatable && kw && !options.some(t => t.name.toLowerCase() === kw.toLowerCase())) {
        options.push({ id: `__create__:${kw}`, name: `创建"${kw}"` })
      }

      options = options.filter((v, i, arr) => arr.findIndex(t => t.id === v.id) === i)

      options.unshift({ id: 0, name: '无标签' })
      return options
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
          v = v.map(id => {
            if (typeof id === 'string' && id.startsWith('__create__:')) {
              const name = id.slice(11)
              const newId = `__new__:${name}`
              if (!localTags.value.find(t => t.id === newId)) {
                localTags.value.push({ id: newId, name })
              }
              return newId
            }
            return id
          })
        }
        emit('update:modelValue', v)
      }
    })

    return { fetchTags, selected, isImageIcon }
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
