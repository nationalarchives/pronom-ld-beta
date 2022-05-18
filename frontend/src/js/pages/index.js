import { test } from '@js/lib/test'

import '@styles/main.scss'
import '@styles/index.scss'
import '@js/common.js'

const App = () => {
  // Signal JS is active
  $('.page-container').removeClass('noJS');
  $('#header').removeClass('noJS');
  console.log("index page JS");
  test()
}

App()

