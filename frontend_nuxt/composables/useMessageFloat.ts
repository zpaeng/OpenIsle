export const useMessageFloat = () =>
  useState<{ open: boolean; path: string }>('message-float', () => ({
    open: false,
    path: '/message-box',
  }))
export const openMessageFloat = (path: string) => {
  const state = useMessageFloat()
  state.value.open = true
  state.value.path = path
}

export const closeMessageFloat = () => {
  const state = useMessageFloat()
  state.value.open = false
}
