<template>
  <BasePopup :visible="state.visible" @close="onCancel">
    <div class="confirm-dialog">
      <h3 class="confirm-title">{{ state.title }}</h3>
      <p class="confirm-message">{{ state.message }}</p>
      <div class="confirm-actions">
        <button class="cancel-button" @click="onCancel">取消</button>
        <button class="confirm-button" @click="onConfirm">确认</button>
      </div>
    </div>
  </BasePopup>
</template>

<script setup>
import BasePopup from '~/components/BasePopup.vue'
import { useConfirm } from '~/composables/useConfirm'

const { state, onConfirm, onCancel } = useConfirm()
</script>

<style scoped>
.confirm-dialog {
  padding: 20px;
  text-align: center;
}
.confirm-title {
  margin-top: 0;
}
.confirm-message {
  margin: 20px 0;
}
.confirm-actions {
  display: flex;
  justify-content: center;
  gap: 10px;
}
.confirm-button {
  background-color: var(--primary-color);
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
}
.cancel-button {
  background-color: #ccc;
  color: black;
  border: none;
  padding: 10px 20px;
  border-radius: 5px;
  cursor: pointer;
}
</style>