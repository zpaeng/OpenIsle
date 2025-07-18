import MarkdownIt from 'markdown-it'

const md = new MarkdownIt({
  html: false,
  linkify: true,
  breaks: true
})

export function renderMarkdown(text) {
  return md.render(text || '')
}

export function stripMarkdown(text) {
  const html = md.render(text || '')
  const el = document.createElement('div')
  el.innerHTML = html
  return el.textContent || el.innerText || ''
}

export function stripMarkdownLength(text, length) {
  const plain = stripMarkdown(text)
  if (!length || plain.length <= length) {
    return plain
  }
  // 截断并加省略号
  return plain.slice(0, length) + '...'
}