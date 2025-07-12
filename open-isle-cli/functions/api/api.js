export async function onRequest({ request }) {
  const url = new URL(request.url)
  url.hostname = '129.204.254.110'
  url.port     = '8080'
  url.protocol = 'http:'

  // 直接透传：不区分 user/category
  return fetch(url, request)
}