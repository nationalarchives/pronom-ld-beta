import '@styles/main.scss'
import '@styles/user-form.scss'
import '@styles/internal-form.scss'

const formParts = ['#core', '#signatures', '#relationships', '#identifiers', '#additionalProperties', '#contributors', '#review']
const formMenuButtons = ['#coreBtn', '#signaturesBtn', '#relationshipsBtn', '#identifiersBtn', '#additionalPropertiesBtn', '#contributorBtn', '#reviewBtn']
const App = () => {
    // Signal JS is active
    $('.page-container').removeClass('noJS')
    // Initialise form
    let formStep = 6
  
    // active class to the button
    $(formMenuButtons[formStep]).addClass( "active" );
    // Show first step
    $(formParts[formStep]).addClass('show');
}

App()