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
      <div
        class="content"
        :class="{ 'menu-open': menuVisible && !hideMenu }"
      >
        <router-view />
      </div>
    </div>
    <ActivityPopup
      :visible="showMilkTeaPopup"
      :icon="milkTeaIcon"
      text="建站送奶茶活动火热进行中，快来参与吧！"
      @close="closeMilkTeaPopup"
    />
  </div>
</template>

<script>
import HeaderComponent from './components/HeaderComponent.vue'
import MenuComponent from './components/MenuComponent.vue'
import ActivityPopup from './components/ActivityPopup.vue'
import { API_BASE_URL } from './main'

export default {
  name: 'App',
  components: { HeaderComponent, MenuComponent, ActivityPopup },
  data() {
    return {
      menuVisible: window.innerWidth > 768,
      showMilkTeaPopup: false,
      milkTeaIcon: ''
    }
  },
  computed: {
    hideMenu() {
      return ['/login', '/signup', '/404', '/signup-reason', '/github-callback', '/twitter-callback', '/discord-callback', '/forgot-password'].includes(this.$route.path)
    }
  },
  async mounted() {
    await this.checkMilkTeaActivity()
  },
  methods: {
    async checkMilkTeaActivity() {
      if (localStorage.getItem('milkTeaActivityPopupShown')) return
      try {
        const res = await fetch(`${API_BASE_URL}/api/activities`)
        if (res.ok) {
          const list = await res.json()
          const a = list.find(i => i.type === 'MILK_TEA' && !i.ended)
          if (a) {
            this.milkTeaIcon = a.icon
            this.showMilkTeaPopup = true
          }
        }
      } catch (e) {
        // ignore network errors
      }
    },
    closeMilkTeaPopup() {
      localStorage.setItem('milkTeaActivityPopupShown', 'true')
      this.showMilkTeaPopup = false
    }
  }
}
</script>

<style>
.content {
  flex: 1;
  max-width: 100%;
  transition: max-width 0.3s ease;
}

.content.menu-open {
  max-width: calc(100% - var(--menu-width));
}

.main-container {
  display: flex;
  flex-direction: row;
  max-width: var(--page-max-width);
  margin: 0 auto;
}

@media (max-width: 768px) {
  .content,
  .content.menu-open {
    max-width: 100% !important;
  }
}
</style>
