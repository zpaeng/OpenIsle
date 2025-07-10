<template>
  <div class="user-list">
    <div v-for="u in users" :key="u.id" class="user-item">
      <img :src="u.avatar" alt="avatar" class="user-avatar" />
      <div class="user-info">
        <div class="user-name">{{ u.username }}</div>
        <div v-if="u.introduction" class="user-intro">{{ u.introduction }}</div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserList',
  props: {
    users: { type: Array, default: () => [] }
  }
}
</script>

<style scoped>
.user-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.user-item {
  display: flex;
  flex-direction: row;
  align-items: center;
  gap: 10px;
}
.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  flex-shrink: 0;
  object-fit: cover;
}
.user-info {
  display: flex;
  flex-direction: column;
}
.user-name {
  font-weight: bold;
}
.user-intro {
  font-size: 14px;
  opacity: 0.7;
}
</style>
