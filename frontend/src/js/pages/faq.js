import { test } from '@js/lib/test'

import '@styles/main.scss'
import '@styles/faq.scss'

const App = () => {
    // Signal JS is active
    $('.page-container').removeClass('noJS')
}

App()