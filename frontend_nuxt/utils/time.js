// time.js
export default class TimeManager {
  // 统一入口：最近 N 天用相对时间，其余走原来的绝对时间
  static format(input, opts = {}) {
    const {
      relativeUntilMs = 48 * 60 * 60 * 1000, // 小于 48h 显示相对时间
      now = new Date(),
      locale = 'zh-CN',
    } = opts

    const date = new Date(input)
    if (Number.isNaN(date.getTime())) return ''

    const diffMs = now - date
    if (diffMs < 0) {
      // 未来时间：也用相对格式（“X 分钟后”）
      return this.#formatRelative(date, { now, locale })
    }
    if (diffMs <= relativeUntilMs) {
      return this.#formatRelative(date, { now, locale })
    }
    return this.#formatAbsolute(date, now)
  }

  // 仅相对时间（供需要时直接调用）
  static formatRelative(input, { now = new Date(), locale = 'zh-CN' } = {}) {
    const date = new Date(input)
    if (Number.isNaN(date.getTime())) return ''
    return this.#formatRelative(date, { now, locale })
  }

  // 仅绝对时间（保留你的原逻辑）
  static formatAbsolute(input, now = new Date()) {
    const date = new Date(input)
    if (Number.isNaN(date.getTime())) return ''
    return this.#formatAbsolute(date, now)
  }

  // —— 私有实现 —— //
  static #formatRelative(date, { now, locale }) {
    const rtf = new Intl.RelativeTimeFormat(locale, { numeric: 'always' })
    const diffSec = Math.round((date - now) / 1000) // 过去是负数，未来是正数

    const absSec = Math.abs(diffSec)

    // 你想要的“几秒钟前”
    if (absSec < 10) return diffSec <= 0 ? '几秒钟前' : '几秒钟后'
    if (absSec < 60) return rtf.format(Math.trunc(diffSec), 'second')

    const diffMin = Math.trunc(diffSec / 60)
    const absMin = Math.abs(diffMin)
    if (absMin < 60) return rtf.format(diffMin, 'minute')

    const diffHour = Math.trunc(diffSec / 3600)
    const absHour = Math.abs(diffHour)
    if (absHour < 24) return rtf.format(diffHour, 'hour')

    const diffDay = Math.trunc(diffSec / 86400)
    if (Math.abs(diffDay) < 30) return rtf.format(diffDay, 'day')

    const diffMonth = Math.trunc(diffDay / 30)
    if (Math.abs(diffMonth) < 12) return rtf.format(diffMonth, 'month')

    const diffYear = Math.trunc(diffDay / 365)
    return rtf.format(diffYear, 'year')
  }

  static #formatAbsolute(date, now) {
    const startOfToday = new Date(now.getFullYear(), now.getMonth(), now.getDate())
    const startOfDate = new Date(date.getFullYear(), date.getMonth(), date.getDate())
    const diffDays = Math.floor((startOfToday - startOfDate) / 86400000)

    const hh = date.getHours().toString().padStart(2, '0')
    const mm = date.getMinutes().toString().padStart(2, '0')
    const timePart = `${hh}:${mm}`

    if (diffDays === 0) return `今天 ${timePart}`
    if (diffDays === 1) return `昨天 ${timePart}`
    if (diffDays === 2) return `前天 ${timePart}`

    const month = date.getMonth() + 1
    const day = date.getDate()

    if (date.getFullYear() === now.getFullYear()) {
      return `${month}.${day} ${timePart}`
    }
    if (date.getFullYear() === now.getFullYear() - 1) {
      return `去年 ${month}.${day} ${timePart}`
    }
    return `${date.getFullYear()}.${month}.${day} ${timePart}`
  }
}
