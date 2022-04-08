import { test } from '@js/lib/test'

import '@styles/main.scss'
import '@styles/contact.scss'

const App = () => {
  // Signal JS is active
  $('.page-container').removeClass('noJS')
  console.log("contact page JS")
  test()
}

App()