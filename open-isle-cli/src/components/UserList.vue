<template>
  <div class="user-list">
    <div v-if="users.length === 0" class="no-users">
      <i class="fas fa-inbox no-users-icon"></i>
      <div class="no-users-text">
        暂无用户
      </div>
    </div>
    <div v-for="u in users" :key="u.id" class="user-item" @click="handleUserClick(u)">
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
  },
  methods: {
    handleUserClick(user) {
      this.$router.push(`/users/${user.id}`).then(() => {
        window.location.reload()
      })
    }
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
  cursor: pointer;
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

.no-users {
  display: flex;
  flex-direction: row;
  gap: 10px;
  justify-content: center;
  align-items: center;
  height: 300px;
  opacity: 0.5;
}

.no-users-text {
  font-size: 16px;
  color: var(--text-color);
}
</style>
