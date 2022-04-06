import '@styles/main.scss'
import '@styles/user-form.scss'

const formParts = ['#core', '#signatures', '#priority', '#identifiers', '#relationships', '#additionalProperties', '#yourDetails', '#review']

const App = () => {
  // Signal JS is active
  $('.page-container').removeClass('noJS')
  // Initialise form
  let formStep = 0
  // Show first step
  $(formParts[formStep]).addClass('show');
  // Whenever .next is clicked add a step
  $('.next').on('click', () => {
    $(formParts[formStep]).removeClass('show');
    formStep++;
    $(formParts[formStep]).addClass('show');
  });
  // Whenever .prev is clicked return a step
  $('.prev').on('click', () => {
    $(formParts[formStep]).removeClass('show');
    formStep--;
    $(formParts[formStep]).addClass('show');
  });
}

App()