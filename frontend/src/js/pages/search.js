// import { common } from '@js/common'
import '@js/common.js'
// import '@styles/main.scss'
import '@styles/search.scss'
import { autocomplete } from '../lib/jqueryUtils';

const App = () => {
  // Signal JS is active
  $('.page-container').removeClass('noJS');
  $('#header').removeClass('noJS');
  $('#filters input:checkbox').change(function(){
    if($(this).is(":checked")) {
        $(this).closest('.each-filter').addClass("selected");
    } else {
      $(this).closest('.each-filter').removeClass("selected");
    }
  });
  $('.sort-by select').change(function(){
    $('.search-input button').trigger('click');
  });

  // Autocomplete setup
  // Priority over
  autocomplete('ff', 'input#pronom_search');
}

App()