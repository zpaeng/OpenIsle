<template>
  <div class="settings-page">
    <div class="settings-title">设置</div>
    <div class="profile-section">
      <div class="avatar-row">
        <!-- label 充当点击区域，内部隐藏 input -->
        <label class="avatar-container">
          <img :src="avatar" class="avatar-preview" />
          <!-- 半透明蒙层：hover 时出现 -->
          <div class="avatar-overlay">更换头像</div>
          <input
            type="file"
            class="avatar-input"
            accept="image/*"
            @change="onAvatarChange"
          />
        </label>
      </div>
      <div class="form-row">
        <label>用户名</label>
        <BaseInput v-model="username" />
      </div>
      <div class="form-row">
        <label>自我介绍</label>
        <BaseInput v-model="introduction" textarea rows="3" />
      </div>
    </div>
    <div v-if="role === 'ADMIN'" class="admin-section">
      <h3>管理员设置</h3>
      <div class="form-row">
        <label>发布规则</label>
        <select v-model="publishMode">
          <option value="DIRECT">直接发布</option>
          <option value="REVIEW">审核后发布</option>
        </select>
      </div>
      <div class="form-row">
        <label>密码强度</label>
        <select v-model="passwordStrength">
          <option value="LOW">低</option>
          <option value="MEDIUM">中</option>
          <option value="HIGH">高</option>
        </select>
      </div>
    </div>
    <div class="buttons">
      <button @click="save">保存</button>
    </div>
  </div>
</template>

<script>
import { API_BASE_URL, toast } from '../main'
import { getToken, fetchCurrentUser } from '../utils/auth'
import BaseInput from '../components/BaseInput.vue'
export default {
  name: 'SettingsPageView',
  components: { BaseInput },
  data() {
    return {
      username: '',
      introduction: '',
      avatar: '',
      avatarFile: null,
      role: '',
      publishMode: 'DIRECT',
      passwordStrength: 'LOW'
    }
  },
  async mounted() {
    const user = await fetchCurrentUser()
    if (user) {
      this.username = user.username
      this.introduction = user.introduction || ''
      this.avatar = user.avatar
      this.role = user.role
      if (this.role === 'ADMIN') {
        this.loadAdminConfig()
      }
    }
  },
  methods: {
    onAvatarChange(e) {
      this.avatarFile = e.target.files[0]
    },
    async loadAdminConfig() {
      try {
        const token = getToken()
        const res = await fetch(`${API_BASE_URL}/api/admin/config`, {
          headers: { Authorization: `Bearer ${token}` }
        })
        if (res.ok) {
          const data = await res.json()
          this.publishMode = data.publishMode
          this.passwordStrength = data.passwordStrength
        }
      } catch (e) {
        // ignore
      }
    },
    async save() {
      const token = getToken()
      if (this.avatarFile) {
        const form = new FormData()
        form.append('file', this.avatarFile)
        const res = await fetch(`${API_BASE_URL}/api/users/me/avatar`, {
          method: 'POST',
          headers: { Authorization: `Bearer ${token}` },
          body: form
        })
        const data = await res.json()
        if (res.ok) {
          this.avatar = data.url
        } else {
          toast.error(data.error || '上传失败')
          return
        }
      }
      const res = await fetch(`${API_BASE_URL}/api/users/me`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
        body: JSON.stringify({ username: this.username, introduction: this.introduction })
      })
      if (!res.ok) {
        const data = await res.json()
        toast.error(data.error || '保存失败')
        return
      }
      if (this.role === 'ADMIN') {
        await fetch(`${API_BASE_URL}/api/admin/config`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json', Authorization: `Bearer ${token}` },
          body: JSON.stringify({ publishMode: this.publishMode, passwordStrength: this.passwordStrength })
        })
      }
      toast.success('保存成功')
    },
  }
}
</script>

<style scoped>
.settings-page {
  padding: 20px;
}
.settings-title {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 20px;
}
.avatar-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 15px;
}
.avatar-preview {
  width: 80px;
  height: 80px;
  border-radius: 40px;
  object-fit: cover;
}
.form-row {
  margin-bottom: 15px;
  display: flex;
  flex-direction: column;
}
.admin-section {
  margin-top: 30px;
  padding-top: 10px;
  border-top: 1px solid #ccc;
}
.buttons {
  margin-top: 20px;
  display: flex;
  gap: 10px;
}

/* 容器：定位 + 光标 */
.avatar-container {
  position: relative;
  display: inline-block;
  cursor: pointer;
}

/* 隐藏默认文件选择按钮 */
.avatar-input {
  display: none;
}

/* 蒙层：初始透明，hover 时渐显 */
.avatar-overlay {
  position: absolute;
  inset: 0;
  border-radius: 40px;
  background: rgba(0, 0, 0, 0.4);
  color: #fff;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: opacity 0.2s ease-in-out;
}

/* hover 触发 */
.avatar-container:hover .avatar-overlay {
  opacity: 1;
}
</style>
