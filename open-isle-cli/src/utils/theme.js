import { reactive } from 'vue'

export const ThemeMode = {
  SYSTEM: 'system',
  LIGHT: 'light',
  DARK: 'dark'
}

const THEME_KEY = 'theme-mode'

export const themeState = reactive({
  mode: ThemeMode.SYSTEM
})

function apply(mode) {
  const root = document.documentElement
  if (mode === ThemeMode.SYSTEM) {
    root.dataset.theme = window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
  } else {
    root.dataset.theme = mode
  }
}

export function initTheme() {
  const saved = localStorage.getItem(THEME_KEY)
  if (saved && Object.values(ThemeMode).includes(saved)) {
    themeState.mode = saved
  }
  apply(themeState.mode)
}

export function setTheme(mode) {
  if (!Object.values(ThemeMode).includes(mode)) return
  themeState.mode = mode
  localStorage.setItem(THEME_KEY, mode)
  apply(mode)
}

export function cycleTheme() {
  const modes = [ThemeMode.SYSTEM, ThemeMode.LIGHT, ThemeMode.DARK]
  const index = modes.indexOf(themeState.mode)
  const next = modes[(index + 1) % modes.length]
  setTheme(next)
}

if (window.matchMedia) {
  window.matchMedia('(prefers-color-scheme: dark)').addEventListener('change', () => {
    if (themeState.mode === ThemeMode.SYSTEM) {
      apply(ThemeMode.SYSTEM)
    }
  })
}
