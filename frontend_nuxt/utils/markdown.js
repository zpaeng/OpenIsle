export function stripMarkdown(text) {
  return text ? text.replace(/[#_*`>\-\[\]\(\)!]/g, '') : ''
}

export function stripMarkdownLength(text, length) {
  const plain = stripMarkdown(text)
  if (!length || plain.length <= length) {
    return plain
  }
  return plain.slice(0, length) + '...'
}
