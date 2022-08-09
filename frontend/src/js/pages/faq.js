import '@styles/main.scss'
import '@styles/faq.scss'
import { FAQWordFilter } from '../lib/jqueryUtils';



const App = () => {
  // Signal JS is active
  $('.noJS').removeClass('noJS');

  $('.faq-questions button').on('click', function () {
    var selectedButton = this.id;
    var selectedP = this.id + 'text';
    if ($("#" + selectedButton).hasClass("closed")) {
      // manage button
      // change aria-expanded for accessibility
      $('#' + selectedButton).attr("aria-expanded", "true");
      $('#' + selectedButton).removeClass("closed");
      // manage p tag
      $('#' + selectedP).removeClass("hide");
    } else {
      // manage button
      // change aria-expanded for accessibility
      $('#' + selectedButton).attr("aria-expanded", "false");
      $('#' + selectedButton).addClass("closed");
      // manage p tag
      $('#' + selectedP).addClass("hide");
    }
  });

  // open all acccordions
  $('#openAllFAQ').on('click', function () {
    $('.faq-questions button').attr("aria-expanded", "true");
    $('.faq-questions p').removeClass("hide");
  });

  // close all accordions
  $('#closeAllFAQ').on('click', function () {
    $('.faq-questions button').attr("aria-expanded", "false");
    $('.faq-questions p').addClass("hide");
  });

  //faq term filter
  $('#faq-search').on('keyup', FAQWordFilter);
}

App()