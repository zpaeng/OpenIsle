<template>
  <div class="dropdown" ref="wrapper">
    <div class="dropdown-display" @click="toggle">
      <template v-if="multiple">
        <span v-if="selectedLabels.length">
          <template v-for="(label, idx) in selectedLabels" :key="label.id">
            <img v-if="label.icon" :src="label.icon" class="option-icon" />
            <span>{{ label.name }}</span>
            <span v-if="idx !== selectedLabels.length - 1">, </span>
          </template>
        </span>
        <span v-else class="placeholder">{{ placeholder }}</span>
      </template>
      <template v-else>
        <span v-if="selectedLabels.length">
          <img v-if="selectedLabels[0].icon" :src="selectedLabels[0].icon" class="option-icon" />
          <span>{{ selectedLabels[0].name }}</span>
        </span>
        <span v-else class="placeholder">{{ placeholder }}</span>
      </template>
      <i class="fas fa-caret-down dropdown-caret"></i>
    </div>
    <div v-if="open" class="dropdown-menu">
      <div class="dropdown-search">
        <i class="fas fa-search search-icon"></i>
        <input type="text" v-model="search" placeholder="搜索" />
      </div>
      <div class="dropdown-option" v-for="o in filteredOptions" :key="o.id" @click="select(o.id)" :class="{ 'selected': isSelected(o.id) }">
        <img v-if="o.icon" :src="o.icon" class="option-icon" />
        <span>{{ o.name }}</span>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, watch, onMounted, onBeforeUnmount } from 'vue'

export default {
  name: 'BaseDropdown',
  props: {
    modelValue: { type: [Array, String, Number], default: () => [] },
    placeholder: { type: String, default: '请选择' },
    multiple: { type: Boolean, default: false },
    fetchOptions: { type: Function, required: true }
  },
  emits: ['update:modelValue'],
  setup(props, { emit }) {
    const open = ref(false)
    const search = ref('')
    const options = ref([])
    const loaded = ref(false)
    const wrapper = ref(null)

    const toggle = () => {
      open.value = !open.value
    }

    const close = () => {
      open.value = false
    }

    const select = id => {
      if (props.multiple) {
        const arr = Array.isArray(props.modelValue) ? [...props.modelValue] : []
        const idx = arr.indexOf(id)
        if (idx > -1) {
          arr.splice(idx, 1)
        } else {
          arr.push(id)
        }
        emit('update:modelValue', arr)
      } else {
        emit('update:modelValue', id)
        close()
      }
    }

    const filteredOptions = computed(() => {
      if (!search.value) return options.value
      return options.value.filter(o => o.name.toLowerCase().includes(search.value.toLowerCase()))
    })

    const clickOutside = e => {
      if (wrapper.value && !wrapper.value.contains(e.target)) {
        close()
      }
    }

    watch(open, async val => {
      if (val && !loaded.value) {
        try {
          const res = await props.fetchOptions()
          options.value = Array.isArray(res) ? res : []
          loaded.value = true
        } catch {
          options.value = []
        }
      }
    })

    onMounted(() => {
      document.addEventListener('click', clickOutside)
    })

    onBeforeUnmount(() => {
      document.removeEventListener('click', clickOutside)
    })

    const selectedLabels = computed(() => {
      if (props.multiple) {
        return options.value.filter(o => (props.modelValue || []).includes(o.id))
      }
      const match = options.value.find(o => o.id === props.modelValue)
      return match ? [match] : []
    })

    const isSelected = (id) => {
      return selectedLabels.value.some(label => label.id === id)
    }

    return { open, toggle, select, search, filteredOptions, wrapper, selectedLabels, isSelected }
  }
}
</script>

<style scoped>
.dropdown {
  position: relative;
  width: 200px;
}

.dropdown-display {
  border: 1px solid #ccc;
  border-radius: 5px;
  padding: 5px 10px;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.placeholder {
  color: gray;
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid #ccc;
  z-index: 10;
  max-height: 200px;
  overflow-y: auto;
}

.dropdown-search {
  display: flex;
  align-items: center;
  padding: 5px;
  border-bottom: 1px solid #eee;
}

.dropdown-search input {
  flex: 1;
  border: none;
  outline: none;
  margin-left: 5px;
}

.dropdown-option {
  display: flex;
  align-items: center;
  padding: 10px 20px;
  gap: 5px;
  cursor: pointer;
}

.dropdown-option.selected {
  background-color: #f5f5f5;
}

.dropdown-option:hover {
  background-color: #f5f5f5;
}

.option-icon {
  width: 16px;
  height: 16px;
}
</style>
