<template>
  <div class="about-page">
    <BaseTabs v-model="selectedTab" :tabs="tabs">
      <template v-if="selectedTab === 'api'">
        <div class="about-api">
          <div v-if="!authState.loggedIn" class="about-api-login">
            请<NuxtLink to="/login">登录</NuxtLink>后查看 Token
          </div>
          <div v-else class="about-api-token">
            <div class="token-row">
              <span class="token-text">{{ token }}</span>
              <button class="copy-btn" @click="copyToken">复制</button>
            </div>
            <div class="token-warning">请不要将 Token 泄露给他人</div>
          </div>
          <div class="about-api-link">
            <a href="https://docs.open-isle.com" target="_blank" rel="noopener noreferrer"
              >API Playground</a
            >
          </div>
        </div>
      </template>
      <template v-else>
        <div class="about-loading" v-if="isFetching">
          <l-hatch-spinner size="100" stroke="10" speed="1" color="var(--primary-color)" />
        </div>
        <div
          v-else
          class="about-content"
          v-html="renderMarkdown(content)"
          @click="handleContentClick"
        ></div>
      </template>
    </BaseTabs>
  </div>
</template>

<script>
import { computed, onMounted, ref, watch } from 'vue'
import { useRoute, useRouter } from '#imports'
import { authState, getToken } from '~/utils/auth'
import { handleMarkdownClick, renderMarkdown } from '~/utils/markdown'
import BaseTabs from '~/components/BaseTabs.vue'
import { toast } from '~/composables/useToast'

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
      {
        key: 'api',
        label: 'API与调试',
      },
    ]
    const route = useRoute()
    const router = useRouter()
    const selectedTab = ref(tabs[0].key)
    const content = ref('')
    const token = computed(() => (authState.loggedIn ? getToken() : ''))

    const loadContent = async (file) => {
      if (!file) return
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
      const initTab = route.query.tab
      if (initTab && tabs.find((t) => t.key === initTab)) {
        selectedTab.value = initTab
        const tab = tabs.find((t) => t.key === initTab)
        if (tab && tab.file) loadContent(tab.file)
      } else {
        loadContent(tabs[0].file)
      }
    })

    watch(selectedTab, (name) => {
      const tab = tabs.find((t) => t.key === name)
      if (tab && tab.file) loadContent(tab.file)
      router.replace({ query: { ...route.query, tab: name } })
    })

    watch(
      () => route.query.tab,
      (name) => {
        if (name && name !== selectedTab.value && tabs.find((t) => t.key === name)) {
          selectedTab.value = name
        }
      },
    )

    const copyToken = async () => {
      if (import.meta.client && token.value) {
        try {
          await navigator.clipboard.writeText(token.value)
          toast.success('已复制 Token')
        } catch (e) {
          toast.error('复制失败')
        }
      }
    }

    const handleContentClick = (e) => {
      handleMarkdownClick(e)
    }

    return {
      tabs,
      selectedTab,
      content,
      renderMarkdown,
      isFetching,
      handleContentClick,
      authState,
      token,
      copyToken,
    }
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

.about-api {
  padding: 20px;
}

.token-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  word-break: break-all;
}

.copy-btn {
  padding: 4px 8px;
  cursor: pointer;
}

.token-warning {
  color: var(--danger-color);
  margin-bottom: 20px;
}

.about-api-link a {
  color: var(--primary-color);
  text-decoration: underline;
}

@media (max-width: 768px) {
  .about-tabs {
    width: 100vw;
  }
}
</style>
