<template>
  <div class="activity-list-page">
    <h1>活动列表</h1>
    <ul>
      <li v-for="a in activities" :key="a.id">{{ a.title }}</li>
    </ul>
  </div>
</template>

<script>
import { API_BASE_URL } from '../main'

export default {
  name: 'ActivityListPageView',
  data() {
    return { activities: [] }
  },
  async mounted() {
    const res = await fetch(`${API_BASE_URL}/api/activities`)
    if (res.ok) {
      this.activities = await res.json()
    }
  }
}
</script>

<style scoped>
.activity-list-page {
  padding: 10px;
}
</style>
