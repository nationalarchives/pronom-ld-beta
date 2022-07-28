import '@styles/main.scss'
import '@styles/external.scss'

const App = () => {
  // Signal JS is active
  $('.page-container').removeClass('noJS');
  $('#header').removeClass('noJS');
}

App()