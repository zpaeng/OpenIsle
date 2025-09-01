<template>
  <div class="third-party-auth">
    <div v-for="p in providers" :key="p.name" :class="['auth-button', p.name]" @click="p.authorize">
      <img class="auth-button-icon" :src="p.icon" :alt="p.alt" />
      <div class="auth-button-text">{{ p.text }} {{ props.action }}</div>
    </div>
  </div>
</template>

<script setup>
import { googleAuthorize } from '~/utils/google'
import { githubAuthorize } from '~/utils/github'
import { discordAuthorize } from '~/utils/discord'
import { twitterAuthorize } from '~/utils/twitter'
import { telegramAuthorize } from '~/utils/telegram'

import googleIcon from '~/assets/icons/google.svg'
import githubIcon from '~/assets/icons/github.svg'
import discordIcon from '~/assets/icons/discord.svg'
import twitterIcon from '~/assets/icons/twitter.svg'
import telegramIcon from '~/assets/icons/telegram.svg'

const props = defineProps({
  action: {
    type: String,
    default: '登录',
  },
  inviteToken: {
    type: String,
    default: '',
  },
})

const providers = [
  {
    name: 'google',
    icon: googleIcon,
    alt: 'Google Logo',
    text: 'Google',
    authorize: () => googleAuthorize(props.inviteToken),
  },
  {
    name: 'github',
    icon: githubIcon,
    alt: 'GitHub Logo',
    text: 'GitHub',
    authorize: () => githubAuthorize(props.inviteToken),
  },
  {
    name: 'discord',
    icon: discordIcon,
    alt: 'Discord Logo',
    text: 'Discord',
    authorize: () => discordAuthorize(props.inviteToken),
  },
  {
    name: 'twitter',
    icon: twitterIcon,
    alt: 'Twitter Logo',
    text: 'Twitter',
    authorize: () => twitterAuthorize(props.inviteToken),
  },
  {
    name: 'telegram',
    icon: telegramIcon,
    alt: 'Telegram Logo',
    text: 'Telegram',
    authorize: () => telegramAuthorize(props.inviteToken),
  },
]
</script>

<style scoped>
.third-party-auth {
  margin-left: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 30%;
  gap: 20px;
}

.auth-button {
  display: flex;
  flex-direction: row;
  align-items: center;
  justify-content: center;
  padding: 10px 20px;
  border: 1px solid var(--normal-border-color);
  border-radius: 10px;
  cursor: pointer;
  min-width: 150px;
  gap: 10px;
  background-color: var(--auth-bg);
}

.auth-button:hover {
  background-color: var(--auth-bg-hover);
}

.auth-button-icon {
  width: 20px;
  height: 20px;
}

.auth-button-text {
  font-size: 16px;
}

.auth-button.google {
  --auth-bg: var(--google-login-background-color, var(--login-background-color));
  --auth-bg-hover: var(--google-login-background-color-hover, var(--login-background-color-hover));
}

.auth-button.github {
  --auth-bg: var(--github-login-background-color, var(--login-background-color));
  --auth-bg-hover: var(--github-login-background-color-hover, var(--login-background-color-hover));
}

.auth-button.discord {
  --auth-bg: var(--discord-login-background-color, var(--login-background-color));
  --auth-bg-hover: var(--discord-login-background-color-hover, var(--login-background-color-hover));
}

.auth-button.twitter {
  --auth-bg: var(--twitter-login-background-color, var(--login-background-color));
  --auth-bg-hover: var(--twitter-login-background-color-hover, var(--login-background-color-hover));
}

.auth-button.telegram {
  --auth-bg: var(--telegram-login-background-color, var(--login-background-color));
  --auth-bg-hover: var(
    --telegram-login-background-color-hover,
    var(--login-background-color-hover)
  );
}

@media (max-width: 768px) {
  .third-party-auth {
    margin-top: 20px;
    margin-left: 0;
    width: calc(100% - 40px);
    gap: 10px;
  }

  .auth-button {
    width: calc(100% - 40px);
  }
}
</style>
