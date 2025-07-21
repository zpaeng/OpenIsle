<template>
  <div class="mobile-dropdown-page">
    <div class="mobile-dropdown-header">
      <i class="fas fa-arrow-left back-icon" @click="goBack"></i>
      <div v-if="store && store.showSearch" class="search-container">
        <input
          type="text"
          v-model="search"
          placeholder="搜索"
          @input="onSearch"
        />
      </div>
    </div>
    <div class="options" ref="scrollEl">
      <div v-if="loading" class="loading-container">
        <l-hatch size="28" stroke="4" speed="3.5" color="var(--primary-color)"></l-hatch>
      </div>
      <template v-else>
        <div
          v-for="o in filteredOptions"
          :key="o.id"
          @click="select(o.id)"
          :class="['option', { selected: isSelected(o.id) }]"
        >
          <span>{{ o.name }}</span>
        </div>
      </template>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted, onBeforeUnmount } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { getDropdownStore, removeDropdownStore } from '../utils/mobileDropdown'
import { hatch } from 'ldrs'
hatch.register()

export default {
  name: 'MobileDropdownPageView',
  setup() {
    const router = useRouter()
    const route = useRoute()
    const id = route.params.id
    const store = getDropdownStore(id)
    if (!store) {
      router.back()
      return {}
    }
    const options = ref([])
    const search = ref('')
    const loading = ref(false)

    const loadOptions = async (kw = '') => {
      try {
        loading.value = true
        const res = await store.fetchOptions(kw)
        options.value = Array.isArray(res) ? res : []
      } finally {
        loading.value = false
      }
    }

    const filteredOptions = computed(() => {
      if (store.remote) return options.value
      if (!search.value) return options.value
      return options.value.filter(o => o.name.toLowerCase().includes(search.value.toLowerCase()))
    })

    const isSelected = id => {
      if (store.multiple) {
        return Array.isArray(store.value.value) && store.value.value.includes(id)
      }
      return store.value.value === id
    }

    const select = id => {
      if (store.multiple) {
        const arr = Array.isArray(store.value.value) ? [...store.value.value] : []
        const idx = arr.indexOf(id)
        if (idx > -1) arr.splice(idx, 1)
        else arr.push(id)
        store.value.value = arr
      } else {
        store.value.value = id
        router.back()
      }
    }

    const onSearch = async () => {
      if (store.remote) {
        await loadOptions(search.value)
      }
    }

    const goBack = () => {
      router.back()
    }

    onMounted(async () => {
      await loadOptions()
    })
    onBeforeUnmount(() => {
      removeDropdownStore(id)
    })

    return { store, options, search, loading, filteredOptions, isSelected, select, onSearch, goBack }
  }
}
</script>

<style scoped>
.mobile-dropdown-page {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: var(--background-color);
}
.mobile-dropdown-header {
  display: flex;
  align-items: center;
  padding: 10px;
  border-bottom: 1px solid var(--normal-border-color);
}
.back-icon {
  font-size: 20px;
  margin-right: 10px;
}
.search-container {
  flex: 1;
}
.search-container input {
  width: 100%;
  padding: 6px 8px;
  border-radius: 4px;
  border: 1px solid var(--normal-border-color);
  background-color: var(--menu-background-color);
  color: var(--text-color);
}
.options {
  flex: 1;
  overflow-y: auto;
}
.option {
  padding: 12px 16px;
  border-bottom: 1px solid var(--normal-border-color);
}
.option.selected {
  background-color: var(--menu-selected-background-color);
}
.loading-container {
  display: flex;
  justify-content: center;
  padding: 20px 0;
}
</style>
