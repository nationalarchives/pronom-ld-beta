import { test } from '@js/lib/test'
// import { common } from '@js/common'
import '@js/common.js'

// import '@styles/main.scss'
import '@styles/search.scss'

const App = () => {
  // Signal JS is active
  $('.page-container').removeClass('noJS')
  test()
}

App()