<template>
  <div class="achievements-list">
    <div class="achievements-list-item">
      <img src="https://openisle-1307107697.cos.ap-guangzhou.myqcloud.com/assert/icons/achi_comment.png" alt="comment"
        class="achievements-list-item-icon">
      <div class="achievements-list-item-title">评论达人</div>
      <div class="achievements-list-item-description">评论达到300条 718/300</div>
    </div>

    <div class="achievements-list-item">
      <img src="https://openisle-1307107697.cos.ap-guangzhou.myqcloud.com/assert/icons/achi_comment.png" alt="comment"
        class="achievements-list-item-icon not_completed">
      <div class="achievements-list-item-title">评论达人</div>
      <div class="achievements-list-item-description">评论达到300条 718/300</div>
    </div>
  </div>
</template>

<script>

export default {
  data() {
    return {
      value: 7,
      max: 10,
    }
  },
}
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