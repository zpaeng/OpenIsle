<template>
  <div id="app">
    <HeaderComponent @toggle-menu="menuVisible = !menuVisible" />
    <MenuComponent :visible="menuVisible" />
    <div class="content" :class="{ 'with-menu': menuVisible }">
      <router-view />
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
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
}
.content {
  margin-left: 0;
  transition: margin-left 0.3s ease;
  padding-top: 60px;
}
.content.with-menu {
  margin-left: 200px;
}
</style>
