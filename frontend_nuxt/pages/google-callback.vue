<template>
  <CallbackPage />
</template>

<script setup>
import CallbackPage from '~/components/CallbackPage.vue'
import { googleAuthWithToken } from '~/utils/google'

onMounted(async () => {
  const hash = new URLSearchParams(window.location.hash.substring(1))
  const idToken = hash.get('id_token')
  if (idToken) {
    await googleAuthWithToken(
      idToken,
      () => {
        navigateTo('/', { replace: true })
      },
      (token) => {
        navigateTo(`/signup-reason?token=${token}`, { replace: true })
      },
    )
  } else {
    navigateTo('/login', { replace: true })
  }
})
</script>
