export const medalTitles = {
  COMMENT: '评论达人',
  POST: '发帖达人',
  SEED: '种子用户'
}

export function getMedalTitle(type) {
  return medalTitles[type] || ''
}
