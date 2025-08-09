<template>
  <div class="achievements-list">
    <div
      v-for="medal in medals"
      :key="medal.type"
      class="achievements-list-item"
    >
      <img
        :src="medal.icon"
        :alt="medal.title"
        :class="['achievements-list-item-icon', { not_completed: !medal.completed }]"
      />
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
import { ref, watch } from 'vue'
import { API_BASE_URL } from '../main'

const props = defineProps({
  userId: {
    type: Number,
    required: true
  }
})

const medals = ref([])

const fetchMedals = async () => {
  const res = await fetch(`${API_BASE_URL}/api/medals?userId=${props.userId}`)
  if (res.ok) {
    medals.value = await res.json()
  }
}

watch(
  () => props.userId,
  id => {
    if (id) {
      fetchMedals()
    }
  },
  { immediate: true }
)
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
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.1);
  padding: 10px;
  border-radius: 10px;
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
</style>

