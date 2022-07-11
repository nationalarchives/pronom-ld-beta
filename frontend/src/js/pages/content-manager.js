import '@styles/main.scss'
import '@styles/contentManager.scss'

const App = () => {
  // Signal JS is active
  $('.page-container').removeClass('noJS');
  $('#header').removeClass('noJS');

  $('.tab-buttons button').on('click', function() {
    $('.tab-buttons button' ).removeClass( "active" );
    const $this = $(this);
    $this.addClass( "active");
    const tabName = $this.attr('class').replace("active","").trim();
    $('.tab-content .tab' ).removeClass( "show" );
    const $tab = $('.tab-content .tab.' + tabName);
    $tab.addClass('show');
  });
  // activate first tab
  $('.tab-buttons button').first().trigger('click');
}
App()
