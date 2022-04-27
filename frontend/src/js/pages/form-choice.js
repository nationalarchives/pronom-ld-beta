import '@styles/main.scss'
import '@styles/form-choice.scss'

const App = () => {
    // Signal JS is active
    $('.page-container').removeClass('noJS');
    $('#header').removeClass('noJS');
}

App()