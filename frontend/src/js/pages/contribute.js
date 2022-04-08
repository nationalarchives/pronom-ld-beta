import { test } from '@js/lib/test'

import '@styles/main.scss'
import '@styles/contribute.scss'

const App = () => {
  // Signal JS is active
  $('.page-container').removeClass('noJS')
  console.log("contribute page JS")
  test()
}

App()