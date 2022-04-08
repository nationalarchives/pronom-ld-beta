import { test } from '@js/lib/test'

import '@styles/main.scss'
import '@styles/dashboard.scss'

const App = () => {
    // Signal JS is active
    $('.page-container').removeClass('noJS')
}

App()