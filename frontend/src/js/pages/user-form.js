import '@styles/main.scss'
import '@styles/user-form.scss'
import '@js/common.js'
import {
  autocomplete,
  optionWorkaround,
  setupByteSeqMultifield,
  setupIncoming,
  setupPriorityMultifield,
  setupSignatureMultifield,
  setupRelationshipMultifield,
  // setupFormNavigation,
  setupForm,
  setupReferenceMultifield,
  setupReviewFields,
} from '../lib/jqueryUtils'

window.$ = $;
window.formStep = 0;

window.formParts = ['#core', '#signatures', '#priority', '#identifiers', '#relationships', '#additionalProperties', '#yourDetails', '#review']
window.formMenuButtons = ['#coreBtn', '#signaturesBtn', '#priorityBtn', '#identifiersBtn', '#relationshipsBtn', '#additionalPropertiesBtn', '#yourDetailsBtn', '#reviewBtn']
window.formSubMenuButtons = ['#prioritySubBtn, #identifiersSubBtn, #relationshipsSubBtn, #additionalPropertiesSubBtn']

const App = () => {

  $(document).ready(function () {
    // Signal JS is active
    $('.page-container').removeClass('noJS');
    $('#header').removeClass('noJS');
    $('.modal-container').removeClass('noJS');
    setupForm();
    $(window).on('resize', setupForm);
    optionWorkaround();
    setupIncoming();
    setupSignatureMultifield();
    setupPriorityMultifield();
    setupRelationshipMultifield();
    setupByteSeqMultifield();
    // setupFormNavigation();
    setupReferenceMultifield();
    setupReviewFields();
    // checkReviewIndicators();
    // closeAccordions();
    if ($(window).width() < 1200) {
      $(".form-partial-content").addClass('hide');
    }
  });


  $(window).on("load", function () {
    if ($(window).width() < 1200) {
      $(".form-partial-content").addClass('hide');
    }
  });

  // path
  $('.add-path').on('click', function (evt) {
    evt.preventDefault();
    var $container = $(this).closest('.paths-list-container').find('.paths-list');
    $('.holder .path:last').clone(true).appendTo($container).find("input").val("").end();
  });
  $('.delete-path').on('click', function (evt) {
    evt.preventDefault();
    $(this).closest(".path").remove();
  });

  // collapse-all
  $('.collapse-all-signatures').on('click', function (evt) {
    evt.preventDefault();
    $('.collapse-content-container').addClass("collapse");
  });
  // open-all
  $('.open-all-signatures').on('click', function (evt) {
    evt.preventDefault();
    $('.collapse-content-container').removeClass("collapse");
  });
  // open selected
  $('.accordion').on('click', function (evt) {
    evt.preventDefault();
    // $( '.collapse-content-container' ).removeClass( "collapse" );
    if ($(this).closest(".collapse-content-container").hasClass("collapse")) {
      $(this).closest(".collapse-content-container").removeClass("collapse")
    } else {
      $(this).closest(".collapse-content-container").addClass("collapse")
    }
  });


  // identifier
  $('.add-identifier').on('click', function (evt) {
    evt.preventDefault();
    $('.identifier:last').clone(true).appendTo('#identifiers-list').find("input").val("").end();
  });
  $('.delete-identifier').on('click', function (evt) {
    evt.preventDefault();
    $(this).closest(".identifier").remove();
  });


  // aliases
  $('.add-alias').on('click', function (evt) {
    evt.preventDefault();
    $('.alias:last').clone(true).appendTo('#aliases-list').find("input").val("").end();
  });
  $('.delete-alias').on('click', function (evt) {
    evt.preventDefault();
    $(this).closest(".alias").remove();
  });


  // relationships

  // display documentation fields
  


  // Autocomplete setup
  // Priority over
  autocomplete('ff', 'section#priority .input-group input.label');
  // Has relationships
  autocomplete('ff', 'section#relationships .rel-ff .input-group input.label');
  // Reference author
  autocomplete('actor', 'section#core #reference-name .input-group input.label');
  // Development actors
  autocomplete('actor', 'section.form-part#additionalProperties .add-actor.development .input-group input.label');
  // Support actors
  autocomplete('actor', 'section.form-part#additionalProperties .add-actor.support .input-group input.label');



  // 

   // Whenever .next is clicked add a step ========= NEXT
   $('.next').on('click', (evt) => {
    evt.preventDefault();
    $(formParts[formStep]).removeClass('show');
    $('.main-nav li').removeClass("active");
    formStep++;
    $(formParts[formStep]).addClass('show');
    if (1 < formStep && formStep < 6) {
      $(formMenuButtons[2]).addClass("active");
    } else {
      $(formMenuButtons[formStep]).addClass("active");
    }
  });
  // Whenever .prev is clicked return a step ========= PREV
  $('.prev').on('click', (evt) => {
    evt.preventDefault();
    $(formParts[formStep]).removeClass('show');
    $('.main-nav li').removeClass("active");
    formStep--;
    $(formParts[formStep]).addClass('show');
    if (1 < formStep && formStep < 6) {
      $(formMenuButtons[2]).addClass("active");
    } else {
      $(formMenuButtons[formStep]).addClass("active");
    }
  });
  // Whenever skipping 3 steps forwards (only aplied to More intormation in external interface) ========= NEXT + 3
  $('.nextSkip').on('click', (evt) => {
    evt.preventDefault();
    $(formParts[formStep]).removeClass('show');
    $('.main-nav li').removeClass("active");
    if (1 < formStep && formStep < 6) {
      $(formMenuButtons[2]).addClass("active");
    } else {
      $('.main-nav li').removeClass("active");
      formStep = 6
      $(formMenuButtons[formStep]).addClass("active");
    }
    $('.main-nav li').removeClass("active");
    formStep = 6
    $(formMenuButtons[formStep]).addClass("active");
    $(formParts[formStep]).addClass('show');

  });

  // Navigation bar

  // Main menu buttons
  $('.segment').on('click', function() {
    $( '.main-nav li' ).removeClass( "active" );
    $( '.side-menu li' ).removeClass( "active" );
    $('.form-part').removeClass('show');
    var currentBtn = ('#' + $(this).closest('li').attr('id'));
    var currentFormPart = currentBtn.replace('Btn', '');
    formStep = formParts.indexOf(currentFormPart);
    $( currentBtn ).addClass('active');
    $(formParts[formStep]).addClass('show');
    console.log(formStep);
    if(formStep === 2){
      $('#prioritySubBtn').closest('li').addClass('active');
    }
  });

  // Side menu buttons (More information)
  $('.segment-sub').on('click', function() {
    $('.form-part').removeClass('show');
    $( '.side-menu li' ).removeClass( "active" );
    var currentBtn = ('#' + $(this).closest('li').attr('id'));
    var currentFormPart = currentBtn.replace('SubBtn', '');
    formStep = formParts.indexOf(currentFormPart);
    console.log(currentBtn);
    $(formParts[formStep]).addClass('show');
    $(currentBtn).closest('li').addClass('active');
  });


  
  // Whenever skipping 3 steps backwards (only aplied to More intormation in external interface) ========= PREV + 3
  $('.prevSkip').on('click', (evt) => {
    evt.preventDefault();
    $(formParts[formStep]).removeClass('show');
    $('.main-nav li').removeClass("active");
    if (1 < formStep && formStep < 6) {
      formStep = 1;
      $(formMenuButtons[formStep]).addClass("active");
    } else {
      $('.main-nav li').removeClass("active");
      formStep--;
      $(formMenuButtons[formStep]).addClass("active");
    }
    $(formParts[formStep]).addClass('show');
  });

}

App()