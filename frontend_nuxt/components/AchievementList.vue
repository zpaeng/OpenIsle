<template>
  <div class="achievements-list">
    <div
      v-for="medal in sortedMedals"
      :key="medal.type"
      class="achievements-list-item select"
    >
      <img
        :src="medal.icon"
        :alt="medal.title"
        :class="['achievements-list-item-icon', { not_completed: !medal.completed }]"
      />
      <div class="achievements-list-item-top-right-label">展示</div>
      <div class="achievements-list-item-title">{{ medal.title }}</div>
      <div class="achievements-list-item-description">
        {{ medal.description }}
        <template v-if="medal.type === 'COMMENT'">
          {{ medal.currentCommentCount }}/{{ medal.targetCommentCount }}
        </template>
        <template v-else-if="medal.type === 'POST'">
          {{ medal.currentPostCount }}/{{ medal.targetPostCount }}
        </template>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  medals: {
    type: Array,
    required: true,
    default: () => []
  }
})

const sortedMedals = computed(() => {
  return [...props.medals].sort((a, b) => {
    if (a.completed === b.completed) return 0
    return a.completed ? -1 : 1
  })
})

</script>

<style scoped>
.achievements-list {
  padding: 20px;
  display: flex;
  flex-direction: flex-start;
  flex-wrap: wrap;
  gap: 10px;
}

.achievements-list-item {
  position: relative;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.1);
  padding: 10px;
  border-radius: 10px;
}

.achievements-list-item.select {
  border: 2px solid var(--primary-color);
}

.achievements-list-item-icon {
  width: 200px;
  height: 200px;
}

.achievements-list-item-title {
  font-size: 16px;
  font-weight: bold;
}

.achievements-list-item-description {
  font-size: 14px;
  color: #666;
}

.not_completed {
  filter: grayscale(100%);
}

.achievements-list-item-top-right-label {
  font-size: 10px;
  color: white;
  background-color: var(--primary-color);
  padding: 2px 4px;
  border-radius: 2px;
  position: absolute;
  top: 10px;
  right: 10px;
}

@media (max-width: 768px) {
  .achievements-list-item-icon {
    width: 100px;
    height: 100px;
  }

  .achievements-list-item-title {
    font-size: 14px;
  }

  .achievements-list-item-description {
    font-size: 12px;
  }

  .achievements-list-item {
    min-width: calc(50% - 30px);
  }
}

</style>

