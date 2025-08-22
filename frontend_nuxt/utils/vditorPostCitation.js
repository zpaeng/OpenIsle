import { authState, getToken } from '~/utils/auth'

async function getPost(apiBaseUrl, id) {
  return await fetch(`${apiBaseUrl}/api/posts/${id}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${getToken()}`,
    },
  })
}

async function searchPost(apiBaseUrl, keyword) {
  return await fetch(`${apiBaseUrl}/api/search/global?keyword=${keyword}`, {
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
            body
              ?.filter((item) => item.type === 'comment' || item.type === 'post')
              .map((item) => ({
                value:
                  item.type === 'comment'
                    ? `[${item.text}](posts/${item.postId}#comment-${item.id})`
                    : `[${item.text}](posts/${item.id})`,
                html: `<div>${item.text}</div>`,
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
