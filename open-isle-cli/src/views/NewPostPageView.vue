<template>
  <div class="new-post-page">
    <div class="new-post-form">
      <input class="post-title-input" v-model="title" placeholder="标题" />
      <div class="post-editor-container">
        <PostEditor v-model="content" />
      </div>
      <div class="post-options">
        <div class="post-options-left">
          <CategorySelect v-model="selectedCategory" />
          <TagSelect v-model="selectedTags" />
        </div>
        <div class="post-options-right">
          <div class="post-draft" @click="saveDraft">存草稿</div>
          <div class="post-submit" @click="submitPost">发布</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import PostEditor from '../components/PostEditor.vue'
import CategorySelect from '../components/CategorySelect.vue'
import TagSelect from '../components/TagSelect.vue'
import { API_BASE_URL, toast } from '../main'
import { getToken } from '../utils/auth'

export default {
  name: 'NewPostPageView',
  components: { PostEditor, CategorySelect, TagSelect },
  setup() {
    const title = ref('')
    const content = ref('')
    const selectedCategory = ref('')
    const selectedTags = ref([])

    const loadDraft = async () => {
      const token = getToken()
      if (!token) return
      try {
        const res = await fetch(`${API_BASE_URL}/api/drafts/me`, {
          headers: { Authorization: `Bearer ${token}` }
        })
        if (res.ok && res.status !== 204) {
          const data = await res.json()
          title.value = data.title || ''
          content.value = data.content || ''
          selectedCategory.value = data.categoryId || ''
          selectedTags.value = data.tagIds || []
        }
      } catch (e) {}
    }

    onMounted(loadDraft)

    const saveDraft = async () => {
      const token = getToken()
      if (!token) {
        toast.error('请先登录')
        return
      }
      try {
        const res = await fetch(`${API_BASE_URL}/api/drafts`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
          },
          body: JSON.stringify({
            title: title.value,
            content: content.value,
            categoryId: selectedCategory.value || null,
            tagIds: selectedTags.value
          })
        })
        if (res.ok) {
          toast.success('草稿已保存')
        } else {
          toast.error('保存失败')
        }
      } catch (e) {
        toast.error('保存失败')
      }
    }
    const submitPost = async () => {
      if (!title.value.trim()) {
        toast.error('标题不能为空')
        return
      }
      if (!content.value.trim()) {
        toast.error('内容不能为空')
        return
      }
      if (!selectedCategory.value) {
        toast.error('请选择分类')
        return
      }
      if (selectedTags.value.length === 0) {
        toast.error('请选择标签')
        return
      }
      try {
        const token = getToken()
        const res = await fetch(`${API_BASE_URL}/api/posts`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            Authorization: `Bearer ${token}`
          },
          body: JSON.stringify({
            title: title.value,
            content: content.value,
            categoryId: selectedCategory.value,
            tagIds: selectedTags.value
          })
        })
        const data = await res.json()
        if (res.ok) {
          toast.success('发布成功')
          if (data.id) {
            window.location.href = `/posts/${data.id}`
          }
        } else {
          toast.error(data.error || '发布失败')
        }
      } catch (e) {
        toast.error('发布失败')
      }
    }
    return { title, content, selectedCategory, selectedTags, submitPost, saveDraft }
  }
}
</script>

<style scoped>
.new-post-page {
  display: flex;
  justify-content: center;
}

.new-post-form {
  width: 100%;
}

.post-title-input {
  font-size: 18px;
  border: none;
  outline: none;
  padding-top: 20px;
  padding-bottom: 20px;
  background-color: transparent;
  font-size: 42px;
  font-weight: bold;
}

.post-draft {
  margin-left: 20px;
  color: var(--primary-color);
  padding: 10px 20px;
  border-radius: 10px;
  cursor: pointer;
  text-decoration: underline;
}

.post-submit {
  background-color: var(--primary-color);
  color: #fff;
  padding: 10px 20px;
  border-radius: 10px;
  width: fit-content;
  cursor: pointer;
}

.post-submit:hover {
  background-color: var(--primary-color-hover);
}

.post-options-left {
  display: flex;
  gap: 10px;
}

.post-options-right {
  display: flex;
  gap: 10px;
}

.post-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-left: 20px;
  margin-top: 20px;
}

</style>

