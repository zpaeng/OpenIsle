import { defineNuxtConfig } from 'nuxt/config'

export default defineNuxtConfig({
  ssr: true,
  runtimeConfig: {
    public: {
      apiBaseUrl: process.env.NUXT_PUBLIC_API_BASE_URL || '',
      websiteBaseUrl: process.env.NUXT_PUBLIC_WEBSITE_BASE_URL || '',
      googleClientId: process.env.NUXT_PUBLIC_GOOGLE_CLIENT_ID || '',
      githubClientId: process.env.NUXT_PUBLIC_GITHUB_CLIENT_ID || '',
      discordClientId: process.env.NUXT_PUBLIC_DISCORD_CLIENT_ID || '',
      twitterClientId: process.env.NUXT_PUBLIC_TWITTER_CLIENT_ID || '',
    },
  },
  // Ensure Vditor styles load before our overrides in global.css
  css: ['vditor/dist/index.css', '~/assets/global.css'],
  app: {
    head: {
      script: [
        {
          tagPriority: 'high',
          innerHTML: `
            (function () {
              try {
                const mode = localStorage.getItem('theme-mode');
                const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
                const theme = mode === 'dark' || mode === 'light' ? mode : (prefersDark ? 'dark' : 'light');
                document.documentElement.dataset.theme = theme;
              } catch (e) {}
            })();
          `,
        },
      ],
      link: [
        {
          rel: 'icon',
          type: 'image/x-icon',
          href: '/favicon.ico',
        },
        {
          rel: 'apple-touch-icon',
          href: '/apple-touch-icon.png',
        },
        {
          rel: 'manifest',
          href: '/manifest.webmanifest',
        },
        {
          rel: 'stylesheet',
          href: 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css',
          referrerpolicy: 'no-referrer',
        },
      ],
    },
    baseURL: '/',
    buildAssetsDir: '/_nuxt/',
  },
  vue: {
    compilerOptions: {
      isCustomElement: (tag) => ['l-hatch', 'l-hatch-spinner'].includes(tag),
    },
  },
})
