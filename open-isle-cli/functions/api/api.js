// open-isle-cli/functions/api/[...path].js
export async function onRequest({ request }) {
  const url = new URL(request.url)

  // 把路径原封不动转发到广州裸 IP
  url.hostname = "129.204.254.110"
  url.port     = "8080"
  url.protocol = "http:"

  const resp = await fetch(url, request)

  // 附加 CORS 头
  return new Response(resp.body, {
    status:  resp.status,
    headers: {
      ...resp.headers,
      "Access-Control-Allow-Origin": "https://9ac7b637-openisle.cjt807916.workers.dev",
      "Access-Control-Allow-Credentials": "true"
    }
  })
}