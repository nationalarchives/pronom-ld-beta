import '@styles/main.scss'
import '@styles/contribute.scss'

const App = () => {
  // Signal JS is active
  $('.page-container').removeClass('noJS');
  $('#header').removeClass('noJS');
}

App()