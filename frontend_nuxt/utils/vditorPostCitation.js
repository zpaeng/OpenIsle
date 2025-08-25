import { authState, getToken } from '~/utils/auth'

async function searchPost(apiBaseUrl, keyword) {
  return await fetch(`${apiBaseUrl}/api/search/posts/title?keyword=${keyword}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${getToken()}`,
    },
  })
}

export default (apiBaseUrl) => {
  return {
    key: '#',
    hint: async (keyword) => {
      if (!keyword.trim()) return []
      try {
        const response = await searchPost(apiBaseUrl, keyword)
        if (response.ok) {
          const body = await response.json()
          let value = ''
          return (
            body.map((item) => ({
              value: `[${item.title}](/posts/${item.id})`,
              html: `<div>${item.title}</div>`,
            })) ?? []
          )
        } else {
          return []
        }
      } catch {
        return []
      }
    },
  }
}
