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
