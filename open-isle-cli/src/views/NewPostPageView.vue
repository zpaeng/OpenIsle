<template>
  <div class="new-post-page">
    <div class="new-post-form">
      <input class="post-title-input" v-model="title" placeholder="标题" />
      <div class="post-editor-container">
        <PostEditor v-model="content" />
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
    const submitPost = () => {
      console.log('title:', title.value)
      console.log('content:', content.value)
      // 在此处可以调用接口提交帖子
    }
    return { title, content, submitPost }
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
</style>

