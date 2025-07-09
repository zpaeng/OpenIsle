<template>
  <div class="about-page">
    <div class="about-tabs">
      <div
        v-for="tab in tabs"
        :key="tab.name"
        :class="['about-tabs-item', { selected: selectedTab === tab.name }]"
        @click="selectTab(tab.name)"
      >
        <div class="about-tabs-item-label">{{ tab.label }}</div>
      </div>
    </div>
    <div class="about-content" v-html="renderMarkdown(content)"></div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import { renderMarkdown } from '../utils/markdown'

export default {
  name: 'AboutPageView',
  setup() {
    const tabs = [
      { name: 'about', label: '关于', file: '/about/about.md' },
      { name: 'agreement', label: '用户协议', file: '/about/agreement.md' },
      { name: 'guideline', label: '创作准则', file: '/about/guideline.md' },
      { name: 'privacy', label: '隐私政策', file: '/about/privacy.md' }
    ]
    const selectedTab = ref(tabs[0].name)
    const content = ref('')

    const loadContent = async (file) => {
      try {
        const res = await fetch(file)
        if (res.ok) {
          content.value = await res.text()
        } else {
          content.value = '# 内容加载失败'
        }
      } catch (e) {
        content.value = '# 内容加载失败'
      }
    }

    const selectTab = (name) => {
      selectedTab.value = name
      const tab = tabs.find(t => t.name === name)
      if (tab) loadContent(tab.file)
    }

    onMounted(() => {
      loadContent(tabs[0].file)
    })

    return { tabs, selectedTab, content, renderMarkdown, selectTab }
  }
}
</script>

<style scoped>
.about-page {
  padding: 20px;
  max-width: var(--page-max-width);
  background-color: var(--background-color);
  margin: 0 auto;
  height: calc(100vh - var(--header-height) - 40px);
  overflow-y: auto;
}

.about-tabs {
  display: flex;
  flex-direction: row;
  border-bottom: 1px solid #e0e0e0;
  margin-bottom: 20px;
}

.about-tabs-item {
  padding: 10px 20px;
  cursor: pointer;
}

.about-tabs-item.selected {
  color: var(--primary-color);
  border-bottom: 2px solid var(--primary-color);
}

.about-content {
  line-height: 1.6;
}
</style>
