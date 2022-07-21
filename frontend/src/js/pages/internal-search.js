import '@styles/main.scss'
import '@styles/internalSearch.scss'

const App = () => {
    // Signal JS is active
    $('.page-container').removeClass('noJS');
    $('#header').removeClass('noJS');
    $('#pronom-content').addClass('editorial');
    $('html').addClass('watchWidth');
}

App()