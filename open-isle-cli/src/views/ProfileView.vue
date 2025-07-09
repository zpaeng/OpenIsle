<template>
  <div class="profile-page">
    <div class="profile-page-header">
      <div class="profile-page-header-avatar">
        <img src="https://via.placeholder.com/150" alt="avatar" class="profile-page-header-avatar-img">
      </div>
      <div class="profile-page-header-user-info">
        <div class="profile-page-header-user-info-name">
          nagisa
        </div>
        <div class="profile-page-header-user-info-description">
          hello world
        </div>
      </div>
    </div>

    <div class="profile-info">
      <div class="profile-info-item">
        <div class="profile-info-item-label">加入时间: </div>
        <div class="profile-info-item-value">2024 年 1月 17 日</div>
      </div>

      <div class="profile-info-item">
        <div class="profile-info-item-label">最后发帖时间: </div>
        <div class="profile-info-item-value">2024 年 2月 17 日</div>
      </div>

      <div class="profile-info-item">
        <div class="profile-info-item-label">浏览量: </div>
        <div class="profile-info-item-value">11662</div>
      </div>
    </div>
    <div class="profile-tabs">
      <div class="profile-tabs-item selected">
        <i class="fas fa-chart-line"></i>
        <div class="profile-tabs-item-label">总结</div>
      </div>
      <div class="profile-tabs-item">
        <i class="fas fa-clock"></i>
        <div class="profile-tabs-item-label">时间线</div>
      </div>
    </div>

    <div class="profile-summary">
      <div class="total-summary">
        <div class="summary-title">统计信息</div>
        <div class="total-summary-content">
          <div class="total-summary-item">
            <div class="total-summary-item-label">访问天数</div>
            <div class="total-summary-item-value">0</div>
          </div>
          <div class="total-summary-item">
            <div class="total-summary-item-label">已读帖子</div>
            <div class="total-summary-item-value">165k</div>
          </div>
          <div class="total-summary-item">
            <div class="total-summary-item-label">已送出</div>
            <div class="total-summary-item-value">165k</div>
          </div>
          <div class="total-summary-item">
            <div class="total-summary-item-label">已收到</div>
            <div class="total-summary-item-value">165k</div>
          </div>
        </div>
      </div>

      <div class="summary-divider">
        <div class="hot-reply">
          <div class="summary-title">热门回复</div>
        </div>

        <div class="hot-topic">
          <div class="summary-title">热门话题</div>
        </div>
      </div>
    </div>
    <div class="profile-timeline"></div>
  </div>
</template>

<script>
export default {
  name: 'ProfileView'
}
</script>

<style scoped>
.profile-page {
  background-color: var(--background-color);
  height: calc(100vh - var(--header-height));
}

.profile-page-header {
  display: flex;
  align-items: center;
  padding: 20px;
}

.profile-page-header-avatar-img {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  background-color: lightblue;
}

.profile-page-header-user-info {
  margin-left: 20px;
}

.profile-page-header-user-info-name {
  font-size: 24px;
  font-weight: bold;
}

.profile-page-header-user-info-description {
  font-size: 20px;
  color: #666;
}

.profile-info {
  display: flex;
  flex-direction: row;
  padding: 0 20px;
  gap: 20px;
  border-top: 1px solid #e0e0e0;
  border-bottom: 1px solid #e0e0e0;
}

.profile-info-item {
  display: flex;
  flex-direction: row;
  gap: 5px;
  align-items: center;
  padding: 10px 0;
}

.profile-info-item-label {
  font-size: 14px;
  opacity: 0.7;
}

.profile-info-item-value {
  font-size: 14px;
}

.profile-tabs {
  display: flex;
  flex-direction: row;
  padding: 0 20px;
  border-bottom: 1px solid #e0e0e0;
}

.profile-tabs-item {
  display: flex;
  flex-direction: row;
  gap: 10px;
  align-items: center;
  justify-content: center;
  padding: 10px 0;
  width: 200px;
  cursor: pointer;
}

.profile-tabs-item.selected {
  color: var(--primary-color);
  border-bottom: 2px solid var(--primary-color);
}

.profile-summary {
  display: flex;
  flex-direction: column;
  padding: 20px;
  gap: 20px;
}

.total-summary {
  width: 100%;
}

.summary-title {
  font-size: 20px;
  font-weight: bold;
}

.total-summary-content {
  display: flex;
  flex-direction: row;
  gap: 20px;
}

.total-summary-item {
  display: flex;
  flex-direction: row;
  gap: 10px;
  align-items: center;
  padding: 10px 0;
}

.total-summary-item-label {
  font-size: 18px;
  opacity: 0.7;
}

.total-summary-item-value {
  font-size: 24px;
  font-weight: bold;
}

.summary-divider {
  margin-top: 20px;
  display: flex;
  flex-direction: row;
  gap: 20px;
  width: 100%;
}

.hot-reply,
.hot-topic {
  width: 50%;
}

</style>
