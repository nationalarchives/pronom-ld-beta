import { test } from '@js/lib/test'

import '@styles/main.scss'
import '@styles/about.scss'

const App = () => {
  // Signal JS is active
  $('.page-container').removeClass('noJS')
  $('#header').removeClass('noJS')
  console.log("about page JS")
  test()
}

App()