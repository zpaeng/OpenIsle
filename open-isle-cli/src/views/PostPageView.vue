<template>
  <div class="post-page">
    <h2>{{ post?.title }}</h2>
    <div v-if="post">{{ post.content }}</div>
    <div v-else>Loading...</div>
  </div>
</template>

<script>
import { onMounted, ref } from 'vue'
import { useRoute } from 'vue-router'

export default {
  name: 'PostPageView',
  setup() {
    const route = useRoute()
    const post = ref(null)

    onMounted(async () => {
      const id = route.params.id
      try {
        const res = await fetch(`/api/posts/${id}`)
        if (res.ok) {
          post.value = await res.json()
        }
      } catch (err) {
        console.error(err)
      }
    })

    return { post }
  }
}
</script>

<style scoped>
.post-page {
  padding: 20px;
}
</style>
