import { test } from '@js/lib/test'
// import { common } from '@js/common'
import '@js/common.js'

// import '@styles/main.scss'
import '@styles/search.scss'

const App = () => {
  // Signal JS is active
  $('.page-container').removeClass('noJS');
  $('#header').removeClass('noJS');
  test()

  $('#filters input:checkbox').change(function(){
    if($(this).is(":checked")) {
        $(this).closest('.each-filter').addClass("selected");
    } else {
      $(this).closest('.each-filter').removeClass("selected");
    }
  });
}

App()