<template>
  <div id="app">
    <Popup
      v-if="showPopup"
      :icon="popupInfo.icon"
      :message="popupInfo.message"
      :link="popupInfo.link"
      @close="showPopup = false"
    />
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
  </div>
</template>

<script>
import HeaderComponent from './components/HeaderComponent.vue'
import MenuComponent from './components/MenuComponent.vue'
import Popup from './components/Popup.vue'
import { API_BASE_URL } from './main'

export default {
  name: 'App',
  components: { HeaderComponent, MenuComponent, Popup },
  data() {
    return {
      menuVisible: window.innerWidth > 768,
      showPopup: false,
      popupInfo: {}
    }
  },
  computed: {
    hideMenu() {
      return ['/login', '/signup', '/404', '/signup-reason', '/github-callback', '/twitter-callback', '/discord-callback', '/forgot-password'].includes(this.$route.path)
    }
  },
  async mounted() {
    try {
      const res = await fetch(`${API_BASE_URL}/api/activities`)
      if (!res.ok) return
      const activities = await res.json()
      const act = activities.find(a => !a.ended && a.type === 'MILK_TEA')
      if (act) {
        const key = `activity-popup-${act.id}`
        if (!localStorage.getItem(key)) {
          this.popupInfo = {
            icon: act.icon || 'fas fa-gift',
            message: `${act.title} 正在进行中，点击前往活动页面`,
            link: '/activities'
          }
          this.showPopup = true
          localStorage.setItem(key, 'shown')
        }
      }
    } catch (e) {
      // ignore errors
    }
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
}

</style>
