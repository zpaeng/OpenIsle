<template>
  <div id="app">
    <HeaderComponent @toggle-menu="menuVisible = !menuVisible" />

    <div class="main-container">
      <MenuComponent :visible="menuVisible" />
      <div class="content">
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
  flex: 1;
}
.main-container {
  display: flex;
  flex-direction: row;
  max-width: var(--page-max-width);
  margin: 0 auto;
  background-color: var(--normal-background-color);
}

</style>
