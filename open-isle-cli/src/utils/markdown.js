import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'
import { toast } from '../main'

const md = new MarkdownIt({
  html: false,
  linkify: true,
  breaks: true,
  highlight: (str, lang) => {
    let code = ''
    if (lang && hljs.getLanguage(lang)) {
      code = hljs.highlight(str, { language: lang, ignoreIllegals: true }).value
    } else {
      code = hljs.highlightAuto(str).value
    }
    return `<pre class="code-block"><button class="copy-code-btn">Copy</button><code class="hljs language-${lang || ''}">${code}</code></pre>`
  }
})

export function renderMarkdown(text) {
  return md.render(text || '')
}

export function handleMarkdownClick(e) {
  if (e.target.classList.contains('copy-code-btn')) {
    const pre = e.target.closest('pre')
    const codeEl = pre && pre.querySelector('code')
    if (codeEl) {
      navigator.clipboard.writeText(codeEl.innerText).then(() => {
        toast.success('已复制')
      })
    }
  }
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
