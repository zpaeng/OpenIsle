<template>
  <transition name="slide">
    <nav v-if="visible" class="menu">
      <ul>
        <li><router-link to="/">Home</router-link></li>
      </ul>
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
  width: 0;
}
.slide-enter-to, .slide-leave-from {
  transform: translateX(0);
  opacity: 1;
  width: 200px;
}
</style>
