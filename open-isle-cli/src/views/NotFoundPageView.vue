<template>
  <div class="not-found-page">
    <h1>404 - 页面不存在</h1>
    <p>你访问的页面不存在或已被删除。</p>
    <router-link to="/">返回首页</router-link>
  </div>
</template>

<script>
export default {
  name: 'NotFoundPageView'
}
</script>

<style scoped>
.not-found-page {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: calc(100vh - var(--header-height));
  text-align: center;
}

.not-found-page h1 {
  margin-bottom: 20px;
}

.not-found-page a {
  color: var(--primary-color);
  text-decoration: underline;
}
</style>
