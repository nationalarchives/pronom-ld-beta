import { test } from '@js/lib/test'

import '@styles/main.scss'
import '@styles/faq.scss'



const App = () => {
  // Signal JS is active
  $('.page-container').removeClass('noJS')
  // var category1List= [ 
  //   {title: 'some title', text: 'some text'},
  //   {title: 'some title', text: 'some text'},
  //   {title: 'some title', text: 'some text'},
  //   {title: 'some title', text: 'some text'},
  //   {title: 'some title', text: 'some text'},
  //   {title: 'some title', text: 'some text'},
  // ]
  $('.faq-questions button').on('click', function() {
    console.log( this.id)
    var selectedP = this.id + 'text';
    $('.faq-questions p').addClass( "hide" );
    $('#'+selectedP).removeClass( "hide" );
    console.log(selectedP)
  });
  $('#openAllFAQ').on('click', function() {
    $('.faq-questions p').removeClass( "hide" );
  });
  $('#closeAllFAQ').on('click', function() {
    $('.faq-questions p').addClass( "hide" );
  });
}

App()