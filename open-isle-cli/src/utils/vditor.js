import Vditor from 'vditor'
import 'vditor/dist/index.css'
import { API_BASE_URL } from '../main'
import { getToken } from './auth'

export function getEditorTheme() {
  return document.documentElement.dataset.theme === 'dark' ? 'dark' : 'classic'
}

export function getPreviewTheme() {
  return document.documentElement.dataset.theme === 'dark' ? 'dark' : 'light'
}

export function createVditor(editorId, options = {}) {
  const {
    placeholder = '',
    height,
    preview = {},
    input,
    after
  } = options

  return new Vditor(editorId, {
    placeholder,
    height,
    theme: getEditorTheme(),
    preview: Object.assign({ theme: { current: getPreviewTheme() } }, preview),
    cdn: 'https://openisle-1307107697.cos.ap-guangzhou.myqcloud.com/assert/vditor',
    toolbar: [
      'emoji',
      'bold',
      'italic',
      'strike',
      '|',
      'list',
      'line',
      'quote',
      'code',
      'inline-code',
      '|',
      'undo',
      'redo',
      '|',
      'link',
      'upload'
    ],
    upload: {
      fieldName: 'file',
      url: `${API_BASE_URL}/api/upload`,
      accept: 'image/*,video/*',
      multiple: false,
      headers: { Authorization: `Bearer ${getToken()}` },
      format(files, responseText) {
        const res = JSON.parse(responseText)
        if (res.code === 0) {
          return JSON.stringify({
            code: 0,
            msg: '',
            data: {
              errFiles: [],
              succMap: { [files[0].name]: res.data.url }
            }
          })
        } else {
          return JSON.stringify({
            code: 1,
            msg: '上传失败',
            data: { errFiles: files.map(f => f.name), succMap: {} }
          })
        }
      }
    },
    toolbarConfig: { pin: true },
    cache: { enable: false },
    input,
    after
  })
}
