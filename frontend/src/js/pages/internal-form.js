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
  setupFormNavigation,
  setupForm,
  setupReferenceMultifield,
  setupAddActorModal,
  reviewFields,
} from '../lib/jqueryUtils'

window.$ = $;
window.formStep = 0;

window.formParts = ['#core', '#signatures', '#identifiers', '#additionalProperties', '#contributors', '#review']
window.formMenuButtons = ['#coreBtn', '#signaturesBtn', '#relationshipsBtn', '#identifiersBtn', '#additionalPropertiesBtn', '#contributorsBtn', '#reviewBtn']

window.reviewFields = reviewFields;

const App = () => {

  $(document).ready(function () {
    // Signal JS is active
    $('.noJS').removeClass('noJS');
    setupForm();
    $(window).on('resize', setupForm);
    optionWorkaround();
    setupIncoming();
    setupSignatureMultifield();
    setupPriorityMultifield();
    setupRelationshipMultifield();
    setupByteSeqMultifield();
    setupFormNavigation();
    setupReferenceMultifield();
    // Internal specific
    setupAddActorModal()
    // checkReviewIndicators();
    // closeAccordions();
    if ($(window).width() < 1200) {
      $(".form-partial-content").addClass('hide');
    }
    reviewFields();
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
}

App()