<template>
  <CallbackPage />
</template>

<script setup>
import CallbackPage from '~/components/CallbackPage.vue'
import { githubExchange } from '~/utils/github'

onMounted(async () => {
  const url = new URL(window.location.href)
  const code = url.searchParams.get('code')
  const state = url.searchParams.get('state')
  const result = await githubExchange(code, state, '')

  if (result.needReason) {
    navigateTo(`/signup-reason?token=${result.token}`, { replace: true })
  } else {
    navigateTo('/', { replace: true })
  }
})
</script>
