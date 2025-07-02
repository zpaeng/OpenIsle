<template>
  <header class="header">
    <div class="logo">🌴</div>
    <button class="menu-btn" @click="$emit('toggle-menu')">☰</button>
  </header>
</template>

<script>
export default {
  name: 'HeaderComponent'
}
</script>

<style scoped>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  background-color: #42b983;
  color: white;
}
.menu-btn {
  font-size: 24px;
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
}
</style>
