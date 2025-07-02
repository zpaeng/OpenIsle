<template>
  <transition name="slide">
    <nav v-if="visible" class="menu">
      <div class="menu-item-container">
        <div class="menu-item selected">
          <i class="menu-item-icon fas fa-hashtag"></i>
          <router-link class="menu-item-text" to="/">话题</router-link>
        </div>
        <div class="menu-item">
          <i class="menu-item-icon fas fa-envelope"></i>
          <router-link class="menu-item-text" to="/">我的消息</router-link>
        </div>
        <div class="menu-item">
          <i class="menu-item-icon fas fa-info-circle"></i>
          <router-link class="menu-item-text" to="/">关于</router-link>
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
</style>
