<template>
  <div class="search-dropdown">
    <Dropdown v-model="selected" :fetch-options="fetchResults" remote menu-class="search-menu"
      option-class="search-option" :show-search="false">
      <template #display="{ toggle, setSearch }">
        <div class="search-input" @click="toggle">
          <i class="search-input-icon fas fa-search"></i>
          <input type="text" v-model="keyword" placeholder="Search" @focus="toggle" @input="setSearch(keyword)" />
        </div>
      </template>
      <template #option="{ option }">
        <i :class="['result-icon', iconMap[option.type] || 'fas fa-question']"></i>
        <div class="result-body">
          <div class="result-main" v-html="highlight(option.text)"></div>
          <div v-if="option.subText" class="result-sub" v-html="highlight(option.subText)"></div>
          <div v-if="option.extra" class="result-extra" v-html="highlight(option.extra)"></div>
        </div>
      </template>
    </Dropdown>
  </div>
</template>

<script>
import { ref } from 'vue'
import Dropdown from './Dropdown.vue'
import { API_BASE_URL } from '../main'

export default {
  name: 'SearchDropdown',
  components: { Dropdown },
  setup() {
    const keyword = ref('')
    const selected = ref(null)

    const fetchResults = async (kw) => {
      if (!kw) return []
      const res = await fetch(`${API_BASE_URL}/api/search/global?keyword=${encodeURIComponent(kw)}`)
      if (!res.ok) return []
      const data = await res.json()
      return data.map(r => ({
        id: r.id,
        text: r.text,
        type: r.type,
        subText: r.subText,
        extra: r.extra
      }))
    }

    const highlight = (text) => {
      if (!keyword.value) return text
      const reg = new RegExp(keyword.value, 'gi')
      return text.replace(reg, m => `<span class="highlight">${m}</span>`)
    }

    const iconMap = {
      user: 'fas fa-user',
      post: 'fas fa-file-alt',
      comment: 'fas fa-comment'
    }

    return { keyword, selected, fetchResults, highlight, iconMap }
  }
}
</script>

<style scoped>
.search-dropdown {
  margin-top: 20px;
  width: 500px;
}

.search-input {
  padding: 10px;
  display: flex;
  align-items: center;
  width: 100%;
}

.search-input input {
  border: none;
  outline: none;
  width: 100%;
  margin-left: 10px;
  font-size: 16px;
}

.search-menu {
  width: 100%;
  max-width: 600px;
}

.search-option {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 10px 20px;
}

.highlight {
  color: var(--primary-color);
}

.result-icon {
  opacity: 0.6;
}

.result-body {
  display: flex;
  flex-direction: column;
}

.result-main {
  font-weight: bold;
}

.result-sub,
.result-extra {
  font-size: 12px;
  color: #666;
}
</style>
