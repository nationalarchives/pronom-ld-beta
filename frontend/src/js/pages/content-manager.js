import '@styles/main.scss'
import '@styles/contentManager.scss'

const tabParts = ['#home-content', '#search-content', '#about-content', '#faq-content', '#external-content', '#submit-content', '#contact-content', '#droid-content', '#formChoice-content']
const tabButtons = ['#home-edit', '#search-edit', '#about-edit', '#faq-edit', '#external-edit', '#submit-edit', '#content-edit', '#droid-edit', '#form-edit']

const App = () => {
  // Signal JS is active
  $('.page-container').removeClass('noJS');
  // Initialise tabs
  let tabStep = 0
  // active class to the button
  $(tabButtons[tabStep]).addClass( "active" );
  // Show first tab
  $(tabParts[tabStep]).addClass('show');
  $('.tab-buttons button').on('click', function() {
    var index = tabButtons.indexOf('#' + this.id);
    $( '.tab-buttons button' ).removeClass( "active" );
    $( '.tab-content div' ).removeClass( "show" );
    tabStep = index;
    $(tabButtons[tabStep]).addClass( "active" );
    $(tabParts[tabStep]).addClass('show');
  });
}
App()
