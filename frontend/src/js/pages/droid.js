import { test } from '@js/lib/test'

import '@styles/main.scss'
import '@styles/droid.scss'

const App = () => {
    // Signal JS is active
    $('.page-container').removeClass('noJS');
    $('#header').removeClass('noJS');
}

App()