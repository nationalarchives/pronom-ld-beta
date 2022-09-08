import '@styles/main.scss'
import '@styles/user-form.scss'
import '@styles/internal-form.scss'
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
  setupAddActorModal,
  setupReviewFields,
  setupPuidValidation,
} from '../lib/jqueryUtils'

window.formStep = 0;

window.formParts = ['#core', '#signatures', '#relationships', '#identifiers', '#additionalProperties', '#contributors', '#review']
window.formMenuButtons = ['#coreBtn', '#signaturesBtn', '#relationshipsBtn', '#identifiersBtn', '#additionalPropertiesBtn', '#contributorsBtn', '#reviewBtn']

const App = () => {

  $(document).ready(function () {
    // Signal JS is active
    $('.noJS').removeClass('noJS');
    setupForm();
    $('html').addClass('watchWidth');
    $(window).on('resize', setupForm);
    optionWorkaround();
    setupIncoming();
    setupSignatureMultifield();
    setupPriorityMultifield();
    setupRelationshipMultifield();
    setupByteSeqMultifield();
    // setupFormNavigation();
    setupReferenceMultifield();
    // Internal specific
    setupAddActorModal()
    // checkReviewIndicators();
    // closeAccordions();
    if ($(window).width() < 1200) {
      $(".form-partial-content").addClass('hide');
    }
    setupReviewFields();
    setupPuidValidation();
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

  // contributors with action
  $('.add-record').on('click', function (evt) {
    evt.preventDefault();
    $('.action-container:last').clone(true).appendTo('.contributors-list').find("input").val("").end();
  });
  $('.delete-record').on('click', function (evt) {
    evt.preventDefault();
    $(this).closest(".action-container").remove();
  });


  // Autocomplete setup
  // Priority over
  autocomplete('ff', 'fieldset.priority-over .input-group input.label');
  // Has relationships
  autocomplete('ff', 'section#relationships .rel-ff .input-group input.label');
  // Reference author
  autocomplete('actor', 'section#core #reference-name .input-group input.label');
  // Development actors
  autocomplete('actor', 'section.form-part#additionalProperties .add-actor.development .input-group input.label');
  // Support actors
  autocomplete('actor', 'section.form-part#additionalProperties .add-actor.support .input-group input.label');


  // 
  window.$ = $;
  window.formStep = 0;
  // let formStep = 0
  $('.form-part').addClass('hide');

  // active class to the button
  $(formMenuButtons[formStep]).addClass( "active" );
  // Show first step
  $(formParts[formStep]).addClass('show');
  $('.next').on('click', (evt) => {
    evt.preventDefault();
    $(formParts[formStep]).removeClass('show');
    $( '.main-nav li' ).removeClass( "active" );
    formStep++;
    $(formParts[formStep]).addClass('show');
    $(formMenuButtons[formStep]).addClass( "active" );
  });
  // Whenever .prev is clicked return a step ========= PREV
  $('.prev').on('click', function(evt) {
    evt.preventDefault();
    $(formParts[formStep]).removeClass('show');
    $( '.main-nav li' ).removeClass( "active" );
    formStep--;
    $(formParts[formStep]).addClass('show');
    $(formMenuButtons[formStep]).addClass( "active" );
  });
  // clicking on the side menu buttons
  $('.segment').on('click', function(evt) {
    evt.preventDefault();evt.preventDefault();
    $( '.main-nav li' ).removeClass( "active" );
    $('.form-part').removeClass('show');
    var currentBtn = ('#' + $(this).closest('li').attr('id'));
    var currentFormPart = currentBtn.replace('Btn', '');
    formStep = formParts.indexOf(currentFormPart);
    $( currentBtn ).addClass('active');
    $(formParts[formStep]).addClass('show');
  });
  
}

App()