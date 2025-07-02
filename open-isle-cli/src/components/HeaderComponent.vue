<template>
  <header class="header">
    <div class="header-content">
      <button class="menu-btn" @click="$emit('toggle-menu')">
        <i class="fas fa-bars"></i> 
      </button>
      <div class="logo-container">
        <img alt="OpenIsle" src="https://openisle-1307107697.cos.ap-guangzhou.myqcloud.com/assert/image.png" width="60" height="60">
        <div class="logo-text">OpenIsle</div>
      </div>
    </div>
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
  border-bottom: 1px solid var(--header-border-color);
}

.logo-container {
  display: flex;
  align-items: center;
  font-size: 20px;
  font-weight: bold;
}

.header-content {
  display: flex;
  flex-direction: row;
  align-items: center;
  width: 100%;
  height: 100%;
  margin: 0 auto;
  max-width: var(--page-max-width);
}

.menu-btn {
  font-size: 24px;
  background: none;
  border: none;
  color: inherit;
  cursor: pointer;
  opacity: 0.4;
  margin-right: 10px;
}

.menu-btn:hover {
  opacity: 0.8;
}

</style>
