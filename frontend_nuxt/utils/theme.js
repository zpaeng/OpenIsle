import { reactive } from 'vue'
import { toast } from '~/main'

export const ThemeMode = {
  SYSTEM: 'system',
  LIGHT: 'light',
  DARK: 'dark',
}

const THEME_KEY = 'theme-mode'

export const themeState = reactive({
  mode: ThemeMode.SYSTEM,
})

function apply(mode) {
  if (!import.meta.client) return
  const root = document.documentElement
  let newMode =
    mode === ThemeMode.SYSTEM
      ? window.matchMedia('(prefers-color-scheme: dark)').matches
        ? 'dark'
        : 'light'
      : mode
  if (root.dataset.theme === newMode) return
  root.dataset.theme = newMode
}

export function initTheme() {
  if (!import.meta.client) return
  const saved = localStorage.getItem(THEME_KEY)
  if (saved && Object.values(ThemeMode).includes(saved)) {
    themeState.mode = saved
  }
  apply(themeState.mode)
}

export function setTheme(mode) {
  if (!import.meta.client) return
  if (!Object.values(ThemeMode).includes(mode)) return
  themeState.mode = mode
  localStorage.setItem(THEME_KEY, mode)
  apply(mode)
}

function getCircle(event) {
  if (!import.meta.client) return undefined
  const x = event.clientX
  const y = event.clientY
  return {
    x,
    y,
    radius: Math.hypot(Math.max(x, window.innerWidth - x), Math.max(y, window.innerHeight - y)),
  }
}

function withViewTransition(event, applyFn, direction = true) {
  if (typeof document !== 'undefined' && document.startViewTransition) {
    const transition = document.startViewTransition(async () => {
      applyFn()
      await nextTick()
    })

    transition.ready
      .then(() => {
        const { x, y, radius } = getCircle(event)

        const clipPath = [`circle(0 at ${x}px ${y}px)`, `circle(${radius}px at ${x}px ${y}px)`]

        document.documentElement.animate(
          {
            clipPath: direction ? clipPath : [...clipPath].reverse(),
          },
          {
            duration: 400,
            easing: 'ease-in-out',
            pseudoElement: direction
              ? '::view-transition-new(root)'
              : '::view-transition-old(root)',
          },
        )
      })
      .catch(console.warn)
  } else {
    applyFn()
  }
}

function getSystemTheme() {
  return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
}

export function cycleTheme(event) {
  if (!import.meta.client) return
  const modes = [ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK]
  const index = modes.indexOf(themeState.mode)
  const next = modes[(index + 1) % modes.length]
  if (next === ThemeMode.SYSTEM) {
    toast.success('💻 已经切换到系统主题')
  } else if (next === ThemeMode.LIGHT) {
    toast.success('🌞 已经切换到明亮主题')
  } else {
    toast.success('🌙 已经切换到暗色主题')
  }
  // 获取当前真实主题
  const currentTheme = themeState.mode === ThemeMode.SYSTEM ? getSystemTheme() : themeState.mode

  // 获取新主题的真实表现
  const nextTheme = next === ThemeMode.SYSTEM ? getSystemTheme() : next

  // 如果新旧主题相同，不用过渡动画
  if (currentTheme === nextTheme) {
    setTheme(next)
    return
  }

  // 计算新主题是否是暗色
  const newThemeIsDark = nextTheme === 'dark'

  withViewTransition(
    event,
    () => {
      setTheme(next)
    },
    !newThemeIsDark,
  )
}

if (import.meta.client && window.matchMedia) {
  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', () => {
    if (themeState.mode === ThemeMode.SYSTEM) {
      apply(ThemeMode.SYSTEM)
    }
  })
}
