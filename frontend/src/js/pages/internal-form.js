import '@styles/main.scss'
import '@styles/user-form.scss'
import '@styles/internal-form.scss'

const formParts = ['#core', '#signatures', '#relationships', '#identifiers', '#additionalProperties', '#contributors', '#review']
const formMenuButtons = ['#coreBtn', '#signaturesBtn', '#relationshipsBtn', '#identifiersBtn', '#additionalPropertiesBtn', '#contributorBtn', '#reviewBtn']
const App = () => {
    // Signal JS is active
    $('.page-container').removeClass('noJS');
    $('#header').removeClass('noJS');
    // Initialise form
    let formStep = 0
  
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
      $('.prev').on('click', () => {
        $(formParts[formStep]).removeClass('show');
        $( '.main-nav li' ).removeClass( "active" );
        formStep--;
        $(formParts[formStep]).addClass('show');
        $(formMenuButtons[formStep]).addClass( "active" );
      });
      
}

App()