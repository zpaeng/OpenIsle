<template>
  <div>
    <ActivityPopup
      :visible="showMilkTeaPopup"
      :icon="milkTeaIcon"
      text="建站送奶茶活动火热进行中，快来参与吧！"
      @close="closeMilkTeaPopup"
    />
  </div>
</template>

<script>
import ActivityPopup from '~/components/ActivityPopup.vue'
import { API_BASE_URL } from '~/main'

export default {
  name: 'GlobalPopups',
  components: { ActivityPopup },
  data () {
    return {
      showMilkTeaPopup: false,
      milkTeaIcon: ''
    }
  },
  async mounted () {
    await this.checkMilkTeaActivity()
  },
  methods: {
    async checkMilkTeaActivity () {
      if (!process.client) return
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
    closeMilkTeaPopup () {
      if (!process.client) return
      localStorage.setItem('milkTeaActivityPopupShown', 'true')
      this.showMilkTeaPopup = false
    }
  }
}
</script>
