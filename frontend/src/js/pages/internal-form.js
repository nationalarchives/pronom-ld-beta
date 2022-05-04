import '@styles/main.scss'
import '@styles/user-form.scss'
import '@styles/internal-form.scss'

const formParts = ['#core', '#signatures', '#relationships', '#identifiers', '#additionalProperties', '#contributors', '#review']
const formMenuButtons = ['#coreBtn', '#signaturesBtn', '#relationshipsBtn', '#identifiersBtn', '#additionalPropertiesBtn', '#contributorsBtn', '#reviewBtn']
const App = () => {
    // Signal JS is active
    $('.modal-container').removeClass('noJS');
    // Initialise form
    let formStep = 0
    $('.form-part').addClass('hide');
  
    // active class to the button
    $(formMenuButtons[formStep]).addClass( "active" );
    // Show first step
    $(formParts[formStep]).addClass('show');

    $('.next').on('click', () => {
      $(formParts[formStep]).removeClass('show');
      $( '.main-nav li' ).removeClass( "active" );
      formStep++;
      $(formParts[formStep]).addClass('show');
      $(formMenuButtons[formStep]).addClass( "active" );
    });
    // Whenever .prev is clicked return a step ========= PREV
    $('.prev').on('click', function() {
      $(formParts[formStep]).removeClass('show');
      $( '.main-nav li' ).removeClass( "active" );
      formStep--;
      $(formParts[formStep]).addClass('show');
      $(formMenuButtons[formStep]).addClass( "active" );
    });
    // clicking on the side menu buttons
    $('.segment').on('click', function() {
      $( '.main-nav li' ).removeClass( "active" );
      $('.form-part').removeClass('show');
      var currentBtn = ('#' + $(this).closest('li').attr('id'));
      var currentFormPart = currentBtn.replace('Btn', '');
      formStep = formParts.indexOf(currentFormPart);
      $( currentBtn ).addClass('active');
      $(formParts[formStep]).addClass('show');
    });
      
}

App()