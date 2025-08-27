<template>
  <div class="about-page">
    <BaseTabs v-model="selectedTab" :tabs="tabs">
      <div class="about-loading" v-if="isFetching">
        <l-hatch-spinner size="100" stroke="10" speed="1" color="var(--primary-color)" />
      </div>
      <div
        v-else
        class="about-content"
        v-html="renderMarkdown(content)"
        @click="handleContentClick"
      ></div>
    </BaseTabs>
  </div>
</template>

<script>
import { onMounted, ref, watch } from 'vue'
import { handleMarkdownClick, renderMarkdown } from '~/utils/markdown'
import BaseTabs from '~/components/BaseTabs.vue'

export default {
  name: 'AboutPageView',
  setup() {
    const isFetching = ref(false)
    const tabs = [
      {
        key: 'about',
        label: '关于',
        file: 'https://openisle-1307107697.cos.ap-guangzhou.myqcloud.com/assert/about/about.md',
      },
      {
        key: 'agreement',
        label: '用户协议',
        file: 'https://openisle-1307107697.cos.ap-guangzhou.myqcloud.com/assert/about/agreement.md',
      },
      {
        key: 'guideline',
        label: '创作准则',
        file: 'https://openisle-1307107697.cos.ap-guangzhou.myqcloud.com/assert/about/guideline.md',
      },
      {
        key: 'privacy',
        label: '隐私政策',
        file: 'https://openisle-1307107697.cos.ap-guangzhou.myqcloud.com/assert/about/privacy.md',
      },
    ]
    const selectedTab = ref(tabs[0].key)
    const content = ref('')

    const loadContent = async (file) => {
      try {
        isFetching.value = true
        const res = await fetch(file)
        if (res.ok) {
          content.value = await res.text()
        } else {
          content.value = '# 内容加载失败'
        }
      } catch (e) {
        content.value = '# 内容加载失败'
      } finally {
        isFetching.value = false
      }
    }

    onMounted(() => {
      loadContent(tabs[0].file)
    })

    watch(selectedTab, (name) => {
      const tab = tabs.find((t) => t.key === name)
      if (tab) loadContent(tab.file)
    })

    const handleContentClick = (e) => {
      handleMarkdownClick(e)
    }

    return { tabs, selectedTab, content, renderMarkdown, isFetching, handleContentClick }
  },
}
</script>

<style scoped>
.about-page {
  max-width: var(--page-max-width);
  background-color: var(--background-color);
  margin: 0 auto;
}

.about-content {
  line-height: 1.6;
  padding: 20px;
}

.about-loading {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 200px;
}

@media (max-width: 768px) {
  .about-tabs {
    width: 100vw;
  }
}
</style>
