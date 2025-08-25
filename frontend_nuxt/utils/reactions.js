const toCdnUrl = (emoji) => {
  const codepoints = Array.from(emoji)
    .map((c) => c.codePointAt(0).toString(16))
    .join('_')
  return `https://fonts.gstatic.com/s/e/notoemoji/latest/${codepoints}/emoji.svg`
}

export const reactionEmojiMap = {
  LIKE: toCdnUrl('❤️'),
  DISLIKE: toCdnUrl('👎'),
  RECOMMEND: toCdnUrl('👏'),
  ANGRY: toCdnUrl('😡'),
  FLUSHED: toCdnUrl('😳'),
  STAR_STRUCK: toCdnUrl('🤩'),
  ROFL: toCdnUrl('🤣'),
  HOLDING_BACK_TEARS: toCdnUrl('🥹'),
  MIND_BLOWN: toCdnUrl('🤯'),
  POOP: toCdnUrl('💩'),
  CLOWN: toCdnUrl('🤡'),
  SKULL: toCdnUrl('☠️'),
  FIRE: toCdnUrl('🔥'),
  EYES: toCdnUrl('👀'),
  FROWN: toCdnUrl('☹️'),
  HOT: toCdnUrl('🥵'),
  EAGLE: toCdnUrl('🦅'),
  SPIDER: toCdnUrl('🕷️'),
  BAT: toCdnUrl('🦇'),
  CHINA: toCdnUrl('🇨🇳'),
  USA: toCdnUrl('🇺🇸'),
  JAPAN: toCdnUrl('🇯🇵'),
  KOREA: toCdnUrl('🇰🇷'),
}
