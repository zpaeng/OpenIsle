import { reactive } from 'vue'

const stores = reactive({})

export function registerDropdownStore(id, store) {
  stores[id] = store
}

export function getDropdownStore(id) {
  return stores[id]
}

export function removeDropdownStore(id) {
  delete stores[id]
}
