<template>
  <div class="new-post-page">
    <div class="new-post-form">
      <input class="post-title-input" v-model="title" placeholder="标题" />
      <div class="post-editor-container">
        <PostEditor v-model="content" />
      </div>
      <div class="post-options">
        <select class="category-select" v-model="selectedCategory">
          <option disabled value="">请选择分类</option>
          <option v-for="c in categories" :key="c.id" :value="c.id">{{ c.name }}</option>
        </select>
        <div class="tag-select">
          <label class="tag-item" v-for="t in tags" :key="t.id">
            <input type="checkbox" :value="t.id" v-model="selectedTags" /> {{ t.name }}
          </label>
        </div>
      </div>
      <div class="post-submit" @click="submitPost">发布</div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import PostEditor from '../components/PostEditor.vue'

export default {
  name: 'NewPostPageView',
  components: { PostEditor },
  setup() {
    const title = ref('')
    const content = ref('')
    const categories = ref([
      { id: 1, name: '闲聊' },
      { id: 2, name: '技术' }
    ])
    const tags = ref([
      { id: 1, name: 'Java' },
      { id: 2, name: 'Python' },
      { id: 3, name: 'AI' }
    ])
    const selectedCategory = ref('')
    const selectedTags = ref([])
    const submitPost = () => {
      console.log('title:', title.value)
      console.log('content:', content.value)
      console.log('category:', selectedCategory.value)
      console.log('tags:', selectedTags.value)
      // 在此处可以调用接口提交帖子
    }
    return { title, content, categories, tags, selectedCategory, selectedTags, submitPost }
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

.post-submit {
  margin-left: 20px;
  margin-top: 20px;
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

.post-options {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.category-select {
  padding: 5px 10px;
  border-radius: 5px;
}

.tag-select {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-item {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>

