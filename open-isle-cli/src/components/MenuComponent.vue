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
  background-color: #f5f5f5;
  padding: 10px;
  position: fixed;
  left: 0;
  top: 0;
  bottom: 0;
  overflow-y: auto;
}
.slide-enter-active, .slide-leave-active {
  transition: transform 0.3s ease;
}
.slide-enter-from, .slide-leave-to {
  transform: translateX(-100%);
}
</style>
