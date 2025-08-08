import { defineNuxtConfig } from 'nuxt/config'

export default defineNuxtConfig({
  ssr: true,
  css: ['~/assets/global.css'],
  app: {
    head: {
      script: [
        {
          tagPriority: 'high',
          children: `
            (function () {
              try {
                const mode = localStorage.getItem('theme-mode');
                const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
                const theme = mode === 'dark' || mode === 'light' ? mode : (prefersDark ? 'dark' : 'light');
                document.documentElement.dataset.theme = theme;
              } catch (e) {}
            })();
          `
        }
      ],
      link: [
        {
          rel: 'stylesheet',
          href: 'https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.7.2/css/all.min.css',
          referrerpolicy: 'no-referrer'
        }
      ]
    }
  }
})
