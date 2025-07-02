<template>
  <div id="app">
    <HeaderComponent @toggle-menu="menuVisible = !menuVisible" />

    <div class="main-container">
      <MenuComponent :visible="menuVisible" />
      <div class="content" :class="{ 'with-menu': menuVisible }">
        <router-view />
      </div>
    </div>
  </div>
</template>

<script>
import HeaderComponent from './components/HeaderComponent.vue'
import MenuComponent from './components/MenuComponent.vue'

export default {
  name: 'App',
  components: { HeaderComponent, MenuComponent },
  data() {
    return { menuVisible: true }
  }
}
</script>

<style>
.content {
  transition: margin-left 0.3s ease;
}

.main-container {
  display: flex;
  justify-content: center;
  flex-direction: row;
  max-width: var(--page-max-width);
  margin-left: auto;
  margin-right: auto;
  background-color: var(--normal-background-color);
}

</style>
