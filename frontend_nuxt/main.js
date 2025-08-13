import { useRuntimeConfig } from '#app'

const config = useRuntimeConfig()

export const API_BASE_URL = config.public.apiBaseUrl
export const GOOGLE_CLIENT_ID = config.public.googleClientId
export const GITHUB_CLIENT_ID = config.public.githubClientId
export const DISCORD_CLIENT_ID = config.public.discordClientId
export const TWITTER_CLIENT_ID = config.public.twitterClientId

// 重新导出 toast 功能，使用 composable 方式
export { toast } from './composables/useToast'
