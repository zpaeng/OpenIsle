<template>
  <div class="article-tags-container">
    <div
      class="article-info-item"
      v-for="tag in tags"
      :key="tag.id || tag.name"
    >
      <img
        v-if="tag.smallIcon"
        class="article-info-item-img"
        :src="tag.smallIcon"
        :alt="tag.name"
      />
      <div class="article-info-item-text">{{ tag.name }}</div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ArticleTags',
  props: {
    tags: { type: Array, default: () => [] }
  }
}
</script>

<style scoped>  

.article-tags-container {
  display: flex;
  flex-direction: row;
  gap: 10px;
}

</style>

