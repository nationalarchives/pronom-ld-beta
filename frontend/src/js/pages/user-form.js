import '@styles/main.scss'
import '@styles/user-form.scss'
import '@js/common.js'

const formParts = ['#core', '#signatures', '#priority', '#identifiers', '#relationships', '#additionalProperties', '#yourDetails', '#review']
const formMenuButtons = ['#coreBtn', '#signaturesBtn', '#priorityBtn', '#identifiersBtn', '#relationshipsBtn', '#additionalPropertiesBtn', '#yourDetailsBtn', '#reviewBtn']
const formSubMenuButtons = ['#prioritySubBtn, #identifiersSubBtn, #relationshipSubBtn, #additionalSubBtn']

const App = () => {

  $(document).ready(myfunction);
  $(window).on('resize',myfunction);
  let formStep = 0

  function myfunction() {
    if ($(window).width() < 1200) {
      $('.form-section .form-part').addClass('show');
      $(".form-partial-content").addClass('hide');
      $(".accordion").click(function(){ 
        if($(this).closest('.form-section').hasClass( "open" )){
          $('.form-section').removeClass('open');
          $(".form-partial-content").addClass('hide');
          console.log('hide')
        } else {
          $('.form-section').removeClass('open');
          $(".form-partial-content").addClass('hide');
          $(this).closest('.form-section').addClass('open');
          $(".open .form-partial-content").removeClass('hide');
          console.log('no hide')
        }
      });
    }
    else {
      $(".form-section .form-part").removeClass('show');
      $(".form-section").removeClass('open');
      $(".form-partial-content").removeClass('hide');
      // Initialise form
      let formStep = 0
      // active class to the button
      $(formMenuButtons[formStep]).addClass( "active" );
      // Show first step
      $(formParts[formStep]).addClass('show');
    }
  }

  // Signal JS is active
  $('.page-container').removeClass('noJS')
  
  // Whenever .next is clicked add a step ========= NEXT
  $('.next').on('click', () => {
    $(formParts[formStep]).removeClass('show');
    $( '.main-nav li' ).removeClass( "active" );
    formStep++;
    $(formParts[formStep]).addClass('show');
    console.log(formStep)
    if(1 < formStep && formStep < 6){
      $(formMenuButtons[2]).addClass( "active" );
    }else {
      $(formMenuButtons[formStep]).addClass( "active" );
    }
  });
  // Whenever .prev is clicked return a step ========= PREV
  $('.prev').on('click', () => {
    $(formParts[formStep]).removeClass('show');
    $( '.main-nav li' ).removeClass( "active" );
    formStep--;
    $(formParts[formStep]).addClass('show');
    if(1 < formStep && formStep < 6){
      $(formMenuButtons[2]).addClass( "active" );
    }else {
      $(formMenuButtons[formStep]).addClass( "active" );
    }
  });
  // Whenever skipping 3 steps forwards (only aplied to More intormation in external interface) ========= NEXT + 3
  $('.nextSkip').on('click', () => {
    $(formParts[formStep]).removeClass('show');
    $( '.main-nav li' ).removeClass( "active" );
    if(1 < formStep && formStep < 6){
      $(formMenuButtons[2]).addClass( "active" );
    } else {
      $( '.main-nav li' ).removeClass( "active" );
      formStep = 6
      $(formMenuButtons[formStep]).addClass( "active" );
    }
    $( '.main-nav li' ).removeClass( "active" );
    formStep = 6
    $(formMenuButtons[formStep]).addClass( "active" );
    $(formParts[formStep]).addClass('show');
    
  });
  // Whenever skipping 3 steps backwards (only aplied to More intormation in external interface) ========= PREV + 3
  $('.prevSkip').on('click', () => {
    $(formParts[formStep]).removeClass('show');
    $( '.main-nav li' ).removeClass( "active" );
    if(1 < formStep && formStep < 6){
      formStep = 1;
      $(formMenuButtons[formStep]).addClass( "active" );
    }else {
      $( '.main-nav li' ).removeClass( "active" );
      formStep--;
      $(formMenuButtons[formStep]).addClass( "active" );
    }
    $(formParts[formStep]).addClass('show');
  });
  // Navigation bar
  $('#coreBtn button').on('click', () => {
    $(formParts[formStep]).removeClass('show');
    $( '.main-nav li' ).removeClass( "active" );
    formStep = 0;
    $(formMenuButtons[0]).addClass( "active" );
    $(formParts[formStep]).addClass('show');
  });
  $('#signaturesBtn button').on('click', () => {
    $(formParts[formStep]).removeClass('show');
    $( '.main-nav li' ).removeClass( "active" );
    formStep = 1;
    $(formMenuButtons[1]).addClass( "active" );
    $(formParts[formStep]).addClass('show');
  });
  $('#priorityBtn button').on('click', () => {
    $(formParts[formStep]).removeClass('show');
    $( '.main-nav li' ).removeClass( "active" );
    formStep = 2;
    $(formMenuButtons[2]).addClass( "active" );
    $(formParts[formStep]).addClass('show');
  });
  $('#prioritySubBtn').on('click', () => {
    $(formParts[formStep]).removeClass('show');
    $( '.main-nav li' ).removeClass( "active" );
    formStep = 2;
    $(formMenuButtons[2]).addClass( "active" );
    $(formParts[formStep]).addClass('show');
  });
  $('#identifiersSubBtn button').on('click', () => {
    $(formParts[formStep]).removeClass('show');
    $( '.main-nav li' ).removeClass( "active" );
    formStep = 3;
    $(formMenuButtons[2]).addClass( "active" );
    $(formParts[formStep]).addClass('show');
  });
  $('#relationshipSubBtn button').on('click', () => {
    $(formParts[formStep]).removeClass('show');
    $( '.main-nav li' ).removeClass( "active" );
    formStep = 4;
    $(formMenuButtons[2]).addClass( "active" );
    $(formParts[formStep]).addClass('show');
  });
  $('#additionalSubBtn button').on('click', () => {
    $(formParts[formStep]).removeClass('show');
    $( '.main-nav li' ).removeClass( "active" );
    formStep = 5;
    $(formMenuButtons[2]).addClass( "active" );
    $(formParts[formStep]).addClass('show');
  });
  $('#yourDetailsBtn button').on('click', () => {
    $(formParts[formStep]).removeClass('show');
    $( '.main-nav li' ).removeClass( "active" );
    formStep = 6;
    $(formMenuButtons[6]).addClass( "active" );
    $(formParts[formStep]).addClass('show');
  });
  $('#reviewBtn button').on('click', () => {
    $(formParts[formStep]).removeClass('show');
    $( '.main-nav li' ).removeClass( "active" );
    formStep = 7;
    $(formMenuButtons[7]).addClass( "active" );
    $(formParts[formStep]).addClass('show');
  });
  
}

App()