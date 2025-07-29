<template>
  <div id="app">
    <HeaderComponent
      @toggle-menu="menuVisible = !menuVisible"
      :show-menu-btn="!hideMenu"
    />

    <div class="main-container">
      <MenuComponent
        :visible="!hideMenu && menuVisible"
        @item-click="menuVisible = false"
      />
      <div class="content">
        <router-view />
      </div>
    </div>
    <GlobalPopups />
  </div>
</template>

<script>
import HeaderComponent from './components/HeaderComponent.vue'
import MenuComponent from './components/MenuComponent.vue'
import GlobalPopups from './components/GlobalPopups.vue'

export default {
  name: 'App',
  components: { HeaderComponent, MenuComponent, GlobalPopups },
  data() {
    return {
      menuVisible: window.innerWidth > 768
    }
  },
  computed: {
    hideMenu() {
      return ['/login', '/signup', '/404', '/signup-reason', '/github-callback', '/twitter-callback', '/discord-callback', '/forgot-password'].includes(this.$route.path)
    }
  },
  async mounted() {
    // placeholder for future global initializations
  },
  methods: {}
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
}

</style>
