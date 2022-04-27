import { test } from '@js/lib/test'

import '@styles/main.scss'
import '@styles/faq.scss'



const App = () => {
  // Signal JS is active
  $('.page-container').removeClass('noJS');
  $('#header').removeClass('noJS');

  $('.faq-questions button').on('click', function() {
    var selectedButton = this.id;
    var selectedP = this.id + 'text';
    console.log(selectedButton)
    if($( "#" + selectedButton ).hasClass( "closed" )){
      console.log('class closed detected')
      // manage button
      // change aria-expanded for accessibility
      $('#'+selectedButton).attr("aria-expanded","true");
      $('#'+selectedButton).removeClass( "closed" );
      // manage p tag
      $('#'+selectedP).removeClass( "hide" );
    } else {
      // manage button
      // change aria-expanded for accessibility
      $('#'+selectedButton).attr("aria-expanded","false");
      $('#'+selectedButton).addClass( "closed" );
      // manage p tag
      $('#'+selectedP).addClass( "hide" );
    }
  });

  // open all acccordions
  $('#openAllFAQ').on('click', function() {
    $('.faq-questions button').attr("aria-expanded","true");
    $('.faq-questions p').removeClass( "hide" );
  });

  // close all accordions
  $('#closeAllFAQ').on('click', function() {
    $('.faq-questions button').attr("aria-expanded","false");
    $('.faq-questions p').addClass( "hide" );
  });
}

App()