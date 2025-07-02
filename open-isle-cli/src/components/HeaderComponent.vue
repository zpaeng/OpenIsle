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
  height: var(--header-height);
  background-color: var(--header-background-color);
  color: var(--header-text-color);
}
.menu-btn {
  font-size: 24px;
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
}
</style>
