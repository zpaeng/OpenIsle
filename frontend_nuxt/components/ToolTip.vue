<template>
  <div class="tooltip-wrapper" ref="wrapperRef">
    <!-- 触发器 -->
    <div
      class="tooltip-trigger"
      @mouseenter="handleMouseEnter"
      @mouseleave="handleMouseLeave"
      @click="handleClick"
      @focus="handleFocus"
      @blur="handleBlur"
      :tabindex="focusable ? 0 : -1"
    >
      <slot />
    </div>

    <!-- 提示内容 -->
    <Transition name="tooltip-fade">
      <div
        v-if="visible"
        ref="tooltipRef"
        class="tooltip-content"
        :class="[
          `tooltip-${placement}`,
          { 'tooltip-dark': dark },
          { 'tooltip-light': !dark }
        ]"
        :style="tooltipStyle"
        role="tooltip"
        :aria-describedby="ariaId"
      >
        <div class="tooltip-inner">
          <slot name="content">
            {{ content }}
          </slot>
        </div>
        <div class="tooltip-arrow" :class="`tooltip-arrow-${placement}`"></div>
      </div>
    </Transition>
  </div>
</template>

<script>
import { ref, computed, onMounted, onBeforeUnmount, nextTick, useId, watch } from 'vue'

export default {
  name: 'ToolTip',
  props: {
    // 提示内容
    content: {
      type: String,
      default: ''
    },
    // 触发方式：hover、click、focus
    trigger: {
      type: String,
      default: 'hover',
      validator: (value) => ['hover', 'click', 'focus', 'manual'].includes(value)
    },
    // 位置：top、bottom、left、right
    placement: {
      type: String,
      default: 'top',
      validator: (value) => ['top', 'bottom', 'left', 'right'].includes(value)
    },
    // 是否启用暗色主题
    dark: {
      type: Boolean,
      default: false
    },
    // 延迟显示时间（毫秒）
    delay: {
      type: Number,
      default: 100
    },
    // 是否禁用
    disabled: {
      type: Boolean,
      default: false
    },
    // 是否可通过Tab键聚焦
    focusable: {
      type: Boolean,
      default: true
    },
    // 偏移距离
    offset: {
      type: Number,
      default: 8
    },
    // 最大宽度
    maxWidth: {
      type: [String, Number],
      default: '200px'
    }
  },
  emits: ['show', 'hide'],
  setup(props, { emit }) {
    const wrapperRef = ref(null)
    const tooltipRef = ref(null)
    const visible = ref(false)
    const ariaId = ref(`tooltip-${useId()}`)
    
    let showTimer = null
    let hideTimer = null

    // 计算tooltip样式
    const tooltipStyle = computed(() => {
      const maxWidth = typeof props.maxWidth === 'number' 
        ? `${props.maxWidth}px` 
        : props.maxWidth
      
      return {
        maxWidth,
        zIndex: 2000
      }
    })

    // 显示tooltip
    const show = () => {
      if (props.disabled) return
      
      clearTimeout(hideTimer)
      showTimer = setTimeout(() => {
        visible.value = true
        emit('show')
        nextTick(() => {
          updatePosition()
        })
      }, props.delay)
    }

    // 隐藏tooltip
    const hide = () => {
      clearTimeout(showTimer)
      hideTimer = setTimeout(() => {
        visible.value = false
        emit('hide')
      }, 100)
    }

    // 立即显示（用于manual模式）
    const showImmediately = () => {
      if (props.disabled) return
      clearTimeout(hideTimer)
      clearTimeout(showTimer)
      visible.value = true
      emit('show')
      nextTick(() => {
        updatePosition()
      })
    }

    // 立即隐藏（用于manual模式）
    const hideImmediately = () => {
      clearTimeout(showTimer)
      clearTimeout(hideTimer)
      visible.value = false
      emit('hide')
    }

    // 更新位置
    const updatePosition = () => {
      if (!wrapperRef.value || !tooltipRef.value) return

      const trigger = wrapperRef.value.querySelector('.tooltip-trigger')
      const tooltip = tooltipRef.value
      
      if (!trigger) return

      const triggerRect = trigger.getBoundingClientRect()
      const tooltipRect = tooltip.getBoundingClientRect()
      
      let top = 0
      let left = 0

      switch (props.placement) {
        case 'top':
          top = triggerRect.top - tooltipRect.height - props.offset
          left = triggerRect.left + (triggerRect.width - tooltipRect.width) / 2
          break
        case 'bottom':
          top = triggerRect.bottom + props.offset
          left = triggerRect.left + (triggerRect.width - tooltipRect.width) / 2
          break
        case 'left':
          top = triggerRect.top + (triggerRect.height - tooltipRect.height) / 2
          left = triggerRect.left - tooltipRect.width - props.offset
          break
        case 'right':
          top = triggerRect.top + (triggerRect.height - tooltipRect.height) / 2
          left = triggerRect.right + props.offset
          break
      }

      // 边界检测
      const padding = 8
      const viewportWidth = window.innerWidth
      const viewportHeight = window.innerHeight

      if (left < padding) {
        left = padding
      } else if (left + tooltipRect.width > viewportWidth - padding) {
        left = viewportWidth - tooltipRect.width - padding
      }

      if (top < padding) {
        top = padding
      } else if (top + tooltipRect.height > viewportHeight - padding) {
        top = viewportHeight - tooltipRect.height - padding
      }

      tooltip.style.position = 'fixed'
      tooltip.style.top = `${top}px`
      tooltip.style.left = `${left}px`
    }

    // 事件处理
    const handleMouseEnter = () => {
      if (props.trigger === 'hover') {
        show()
      }
    }

    const handleMouseLeave = () => {
      if (props.trigger === 'hover') {
        hide()
      }
    }

    const handleClick = () => {
      if (props.trigger === 'click') {
        if (visible.value) {
          hide()
        } else {
          show()
        }
      }
    }

    const handleFocus = () => {
      if (props.trigger === 'focus') {
        show()
      }
    }

    const handleBlur = () => {
      if (props.trigger === 'focus') {
        hide()
      }
    }

    // 点击外部隐藏
    const handleClickOutside = (event) => {
      if (props.trigger === 'click' && wrapperRef.value && !wrapperRef.value.contains(event.target)) {
        hide()
      }
    }

    // 窗口大小改变时重新计算位置
    const handleResize = () => {
      if (visible.value) {
        updatePosition()
      }
    }

    // 监听禁用状态变化
    watch(() => props.disabled, (newVal) => {
      if (newVal && visible.value) {
        hideImmediately()
      }
    })

    onMounted(() => {
      document.addEventListener('click', handleClickOutside)
      window.addEventListener('resize', handleResize)
      window.addEventListener('scroll', handleResize)
    })

    onBeforeUnmount(() => {
      clearTimeout(showTimer)
      clearTimeout(hideTimer)
      document.removeEventListener('click', handleClickOutside)
      window.removeEventListener('resize', handleResize)
      window.removeEventListener('scroll', handleResize)
    })

    return {
      wrapperRef,
      tooltipRef,
      visible,
      ariaId,
      tooltipStyle,
      handleMouseEnter,
      handleMouseLeave,
      handleClick,
      handleFocus,
      handleBlur,
      // 暴露给父组件的方法
      show: showImmediately,
      hide: hideImmediately
    }
  }
}
</script>

<style scoped>
.tooltip-wrapper {
  position: relative;
  display: inline-block;
}

.tooltip-trigger {
  display: inline-block;
  outline: none;
}

.tooltip-content {
  position: fixed;
  pointer-events: none;
  z-index: 2000;
}

.tooltip-inner {
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 14px;
  line-height: 1.4;
  word-wrap: break-word;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

/* 亮色主题 */
.tooltip-light .tooltip-inner {
  background-color: var(--background-color);
  color: var(--text-color);
  border: 1px solid var(--normal-border-color);
}

/* 暗色主题 */
.tooltip-dark .tooltip-inner {
  background-color: rgba(0, 0, 0, 0.9);
  color: white;
}

/* 箭头基础样式 */
.tooltip-arrow {
  position: absolute;
  width: 0;
  height: 0;
  border-style: solid;
}

/* 顶部箭头 */
.tooltip-top .tooltip-arrow-top {
  bottom: -6px;
  left: 50%;
  transform: translateX(-50%);
  border-width: 6px 6px 0 6px;
}

.tooltip-light.tooltip-top .tooltip-arrow-top {
  border-color: var(--normal-border-color) transparent transparent transparent;
}

.tooltip-light.tooltip-top .tooltip-arrow-top::after {
  content: '';
  position: absolute;
  top: -7px;
  left: -6px;
  border-width: 6px 6px 0 6px;
  border-style: solid;
  border-color: var(--background-color) transparent transparent transparent;
}

.tooltip-dark.tooltip-top .tooltip-arrow-top {
  border-color: rgba(0, 0, 0, 0.9) transparent transparent transparent;
}

/* 底部箭头 */
.tooltip-bottom .tooltip-arrow-bottom {
  top: -6px;
  left: 50%;
  transform: translateX(-50%);
  border-width: 0 6px 6px 6px;
}

.tooltip-light.tooltip-bottom .tooltip-arrow-bottom {
  border-color: transparent transparent var(--normal-border-color) transparent;
}

.tooltip-light.tooltip-bottom .tooltip-arrow-bottom::after {
  content: '';
  position: absolute;
  top: 1px;
  left: -6px;
  border-width: 0 6px 6px 6px;
  border-style: solid;
  border-color: transparent transparent var(--background-color) transparent;
}

.tooltip-dark.tooltip-bottom .tooltip-arrow-bottom {
  border-color: transparent transparent rgba(0, 0, 0, 0.9) transparent;
}

/* 左侧箭头 */
.tooltip-left .tooltip-arrow-left {
  right: -6px;
  top: 50%;
  transform: translateY(-50%);
  border-width: 6px 0 6px 6px;
}

.tooltip-light.tooltip-left .tooltip-arrow-left {
  border-color: transparent transparent transparent var(--normal-border-color);
}

.tooltip-light.tooltip-left .tooltip-arrow-left::after {
  content: '';
  position: absolute;
  top: -6px;
  left: -7px;
  border-width: 6px 0 6px 6px;
  border-style: solid;
  border-color: transparent transparent transparent var(--background-color);
}

.tooltip-dark.tooltip-left .tooltip-arrow-left {
  border-color: transparent transparent transparent rgba(0, 0, 0, 0.9);
}

/* 右侧箭头 */
.tooltip-right .tooltip-arrow-right {
  left: -6px;
  top: 50%;
  transform: translateY(-50%);
  border-width: 6px 6px 6px 0;
}

.tooltip-light.tooltip-right .tooltip-arrow-right {
  border-color: transparent var(--normal-border-color) transparent transparent;
}

.tooltip-light.tooltip-right .tooltip-arrow-right::after {
  content: '';
  position: absolute;
  top: -6px;
  left: 1px;
  border-width: 6px 6px 6px 0;
  border-style: solid;
  border-color: transparent var(--background-color) transparent transparent;
}

.tooltip-dark.tooltip-right .tooltip-arrow-right {
  border-color: transparent rgba(0, 0, 0, 0.9) transparent transparent;
}

/* 过渡动画 */
.tooltip-fade-enter-active,
.tooltip-fade-leave-active {
  transition: all 0.2s ease;
}

.tooltip-fade-enter-from {
  opacity: 0;
  transform: scale(0.8);
}

.tooltip-fade-leave-to {
  opacity: 0;
  transform: scale(0.8);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .tooltip-inner {
    padding: 6px 10px;
    font-size: 13px;
    max-width: 250px;
  }
}

/* 键盘导航样式 */
.tooltip-trigger:focus-visible {
  outline: 2px solid var(--primary-color);
  outline-offset: 2px;
  border-radius: 4px;
}
</style>
