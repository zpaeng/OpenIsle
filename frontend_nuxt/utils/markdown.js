import MarkdownIt from 'markdown-it'
import hljs from 'highlight.js'
import 'highlight.js/styles/github.css'

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

// todo: 简单用正则操作一下，后续体验不佳可以采用 striptags 
export function stripMarkdown(text) {
  const html = md.render(text)
  return html.replace(/<\/?[^>]+>/g, '') 
}

export function stripMarkdownLength(text, length) {
  const plain = stripMarkdown(text)
  if (!length || plain.length <= length) {
    return plain
  }
  return plain.slice(0, length) + '...'
}
