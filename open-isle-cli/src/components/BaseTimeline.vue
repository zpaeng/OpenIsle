<template>
  <div class="timeline">
    <div class="timeline-item" v-for="(item, idx) in items" :key="idx">
      <div class="timeline-icon">
        <img v-if="item.src" :src="item.src" class="timeline-img" />
        <i v-else-if="item.icon" :class="item.icon"></i>
      </div>
      <div class="timeline-content">
        <slot name="item" :item="item">{{ item.content }}</slot>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BaseTimeline',
  props: {
    items: { type: Array, default: () => [] }
  }
}
</script>

<style scoped>
.timeline {
  display: flex;
  flex-direction: column;
  height: 100%;
}

.timeline-item {
  display: flex;
  flex-direction: row;
  align-items: flex-start;
  position: relative;
  margin-bottom: 20px;
}

.timeline-icon {
  width: 24px;
  height: 24px;
  border-radius: 50%;
  background-color: var(--primary-color, #3498db);
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  margin-right: 10px;
  flex-shrink: 0;
}

.timeline-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 50%;
}

.timeline-item::before {
  content: '';
  position: absolute;
  top: 24px;
  left: 12px;
  width: 2px;
  bottom: -20px;
  background: #eee;
}

.timeline-item:last-child::before {
  display: none;
}

.timeline-content {
  flex: 1;
}
</style>
