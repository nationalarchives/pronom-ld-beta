import { test } from '@js/lib/test'

import '@styles/main.scss'
import '@styles/external.scss'

const App = () => {
  console.log("external page JS")
  test()
}

App()