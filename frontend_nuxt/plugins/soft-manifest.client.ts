import { defineNuxtPlugin } from 'nuxt/app'

export default defineNuxtPlugin((nuxtApp) => {
  // 覆盖默认行为：收到 manifest 更新时，不立刻在路由切换里刷新
  nuxtApp.hooks.hook('app:manifest:update', () => {
    // todo 选择：弹个提示，让用户点击刷新；或延迟到页面隐藏时再刷新
    // 例如：document.addEventListener('visibilitychange', () => { if (document.hidden) location.reload() })
  })
})
