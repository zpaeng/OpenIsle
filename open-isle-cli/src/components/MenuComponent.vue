<template>
  <transition name="slide">
    <nav v-if="visible" class="menu">
      <div class="menu-item-container">
        <router-link class="menu-item" exact-active-class="selected" to="/">
          <i class="menu-item-icon fas fa-hashtag"></i>
          <span class="menu-item-text">话题</span>
        </router-link>
        <router-link class="menu-item" exact-active-class="selected" to="/message">
          <i class="menu-item-icon fas fa-envelope"></i>
          <span class="menu-item-text">我的消息</span>
        </router-link>
        <router-link class="menu-item" exact-active-class="selected" to="/about">
          <i class="menu-item-icon fas fa-info-circle"></i>
          <span class="menu-item-text">关于</span>
        </router-link>
        <router-link class="menu-item" exact-active-class="selected" to="/new-post">
          <i class="menu-item-icon fas fa-edit"></i>
          <span class="menu-item-text">发帖</span>
        </router-link>
      </div>

      <div class="menu-footer">
        <div class="menu-footer-btn" @click="$emit('toggle-dark-mode')">
          <i class="fas fa-moon"></i>
        </div>
      </div>
    </nav>
  </transition>
</template>

<script>
export default {
  name: 'MenuComponent',
  props: {
    visible: {
      type: Boolean,
      default: true
    }
  }
}
</script>

<style scoped>
.menu {
  width: 200px;
  background-color: var(--menu-background-color);
  height: calc(100vh - var(--header-height));
  border-right: 1px solid var(--menu-border-color);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.menu-item-container {
  padding: 10px;
}

.menu-item {
  display: block;
  padding: 4px 10px;
  text-decoration: none;
  color: var(--menu-text-color);
  border-radius: 10px;
}

.menu-item.selected {
  font-weight: bold;
  background-color: var(--menu-selected-background-color);
}

.menu-item-text {
  font-size: 16px;
  text-decoration: none;
  color: var(--menu-text-color);
}

.menu-item-icon {
  margin-right: 10px;
  opacity: 0.5;
}

.menu-footer {
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  margin-bottom: 10px;
}

.menu-footer-btn {
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
}

/*
.slide-enter-active, .slide-leave-active {
  transition: 
    transform 0.3s ease,
    opacity 0.3s ease,
    width 0.3s ease;
}
.slide-enter-from, .slide-leave-to {
  transform: translateX(-100%);
  opacity: 0;
}
.slide-enter-to, .slide-leave-from {
  transform: translateX(0);
  opacity: 1;
}
  */
</style>
