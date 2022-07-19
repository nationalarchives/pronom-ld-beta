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
  setupFormNavigation,
  setupForm,
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
    setupFormNavigation();
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
  $('.documentation input:checkbox').change(function (evt) {
    evt.preventDefault();
    if ($(this).is(":checked")) {
      $(this).closest('.reference-group').addClass("open-additional");
    } else {
      $(this).closest('.reference-group').removeClass("open-additional");
    }
  });


  // Autocomplete setup
  // Priority over
  autocomplete('ff', 'section#priority .input-group input.label');
  // Has relationships
  autocomplete('ff', 'section#relationships .rel-ff .input-group input.label');

}

App()