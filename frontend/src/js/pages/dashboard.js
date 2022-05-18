import { test } from '@js/lib/test'

import '@styles/main.scss'
import '@styles/dashboard.scss'

const App = () => {
    // Signal JS is active
    $('.page-container').removeClass('noJS');
    $('#header').removeClass('noJS');

    $('.collapse-button').on('click', function() {
      if($(this).closest('.collapse-container').hasClass('collapsed')){
        $(this).closest('.collapse-container').removeClass('collapsed');
        $('.collapse-container .collapse-content').css('display','block');
        $('.collapsed .collapse-content').css('display','none');
      }else{
        $(this).closest('.collapse-container').addClass('collapsed');
        $('.collapsed .collapse-content').css('display','none');
        console.log('clicked')
      }
    });
    
}

App()