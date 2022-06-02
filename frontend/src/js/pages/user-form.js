import '@styles/main.scss'
import '@styles/user-form.scss'
import '@js/common.js'

window.$ = $;

const formParts = ['#core', '#signatures', '#priority', '#identifiers', '#relationships', '#additionalProperties', '#yourDetails', '#review']
const formMenuButtons = ['#coreBtn', '#signaturesBtn', '#priorityBtn', '#identifiersBtn', '#relationshipsBtn', '#additionalPropertiesBtn', '#yourDetailsBtn', '#reviewBtn']
const formSubMenuButtons = ['#prioritySubBtn, #identifiersSubBtn, #relationshipSubBtn, #additionalSubBtn']

const App = () => {

  $(document).ready(function() {
    formSetup(),
    checkReviewIndicators();
    closeAccordions();
    console.log('lalala')
    if ($(window).width() < 1200) {
      $(".form-partial-content").addClass('hide');
    }
  });

  $(window).on('resize', formSetup);
  let formStep = 0;
  $('.modal-container').removeClass('noJS');

  $(window).on("load", function () {
    if ($(window).width() < 1200) {
      $(".form-partial-content").addClass('hide');
    }
  });

  function formSetup() {
    if ($(window).width() < 1200) {
      $('.form-section .form-part').addClass('show');
      // $(".form-partial-content").addClass('hide');
      $('.accordion').on('click', function (evt) {
        console.log(formStep);
        evt.preventDefault();
        if ($(this).closest('.form-section').hasClass("open")) {
          $('.form-section').removeClass('open');
          $(".form-partial-content").addClass('hide');
        } else {
          $('.form-section').removeClass('open');
          $(".form-partial-content").addClass('hide');
          $(this).closest('.form-section').addClass('open');
          $(".open .form-partial-content").removeClass('hide');
        }
      });
    } else {
      $('.form-part').addClass('hide');
      $(".form-section .form-part").removeClass('show');
      $(".form-section").removeClass('open');
      $(".form-partial-content").removeClass('hide');
      // Initialise form
      let formStep = 0
      // active class to the button
      $(formMenuButtons[formStep]).addClass("active");
      // Show first step
      $(formParts[formStep]).addClass('show');
    }
  }

  // Signal JS is active
  $('.page-container').removeClass('noJS');
  $('#header').removeClass('noJS');

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
  // Navigation bar
  $('#coreBtn button').on('click', () => {
    $('.side-menu li').removeClass("active");
    $(formParts[formStep]).removeClass('show');
    $('.main-nav li').removeClass("active");
    formStep = 0;
    $(formMenuButtons[0]).addClass("active");
    $(formParts[formStep]).addClass('show');
  });
  $('#signaturesBtn button').on('click', () => {
    $('.side-menu li').removeClass("active");
    $(formParts[formStep]).removeClass('show');
    $('.main-nav li').removeClass("active");
    formStep = 1;
    $(formMenuButtons[1]).addClass("active");
    $(formParts[formStep]).addClass('show');
  });
  $('#priorityBtn button').on('click', () => {
    $('.side-menu li').removeClass("active");
    $(formParts[formStep]).removeClass('show');
    $('.main-nav li').removeClass("active");
    formStep = 2;
    $(formMenuButtons[2]).addClass("active");
    $(formParts[formStep]).addClass('show');
    $('.side-menu li:nth-child(1)').addClass("active");
  });
  $('#prioritySubBtn').on('click', () => {
    $('.side-menu li').removeClass("active");
    $(formParts[formStep]).removeClass('show');
    $('.main-nav li').removeClass("active");
    formStep = 2;
    $(formMenuButtons[2]).addClass("active");
    $(formParts[formStep]).addClass('show');
    $('.main-nav li').removeClass("active");
    $('.side-menu li:nth-child(1)').addClass("active");
  });
  $('#identifiersSubBtn button').on('click', () => {
    $('.side-menu li').removeClass("active");
    $(formParts[formStep]).removeClass('show');
    $('.main-nav li').removeClass("active");
    formStep = 3;
    $(formMenuButtons[2]).addClass("active");
    $(formParts[formStep]).addClass('show');
    $('.side-menu li:nth-child(2)').addClass("active");
  });
  $('#relationshipSubBtn button').on('click', () => {
    $('.side-menu li').removeClass("active");
    $(formParts[formStep]).removeClass('show');
    $('.main-nav li').removeClass("active");
    formStep = 4;
    $(formMenuButtons[2]).addClass("active");
    $(formParts[formStep]).addClass('show');
    $('.side-menu li:nth-child(3)').addClass("active");
  });
  $('#additionalSubBtn button').on('click', () => {
    $('.side-menu li').removeClass("active");
    $(formParts[formStep]).removeClass('show');
    $('.main-nav li').removeClass("active");
    formStep = 5;
    $(formMenuButtons[2]).addClass("active");
    $(formParts[formStep]).addClass('show');
    $('.side-menu li:nth-child(4)').addClass("active");
  });
  $('#yourDetailsBtn button').on('click', () => {
    $('.side-menu li').removeClass("active");
    $(formParts[formStep]).removeClass('show');
    $('.main-nav li').removeClass("active");
    formStep = 6;
    $(formMenuButtons[6]).addClass("active");
    $(formParts[formStep]).addClass('show');
  });
  $('#reviewBtn button').on('click', () => {
    $('.side-menu li').removeClass("active");
    $(formParts[formStep]).removeClass('show');
    $('.main-nav li').removeClass("active");
    formStep = 7;
    $(formMenuButtons[7]).addClass("active");
    $(formParts[formStep]).addClass('show');
  });

  // Incoming buttons functionality
  // When clicked their value is copied to the current input
  $('button.incoming').on('click', function (evt) {
    evt.preventDefault();
    const $this = $(this);
    if ($this.hasClass('undo')) {
      revertChanges($this);
    } else {
      acceptIncoming($this);
    }
  });

  $('button.current').on('click', function (evt) {
    evt.preventDefault();
    const $this = $(this);
    acceptCurrent($this);

  });

  function acceptCurrent($this) {
    // hide the incoming group
    $this.parent().siblings('.input-group').hide();
    // mark as reviewed
    const wrapper = $this.parent().parent();
    wrapper.removeClass('before-review').addClass('after-review');
    // hide the current button
    $this.hide();
  }

  function acceptIncoming($this) {
    const input = $this.next(':input');
    // copy incoming value
    $this.parent().siblings('.input-group').find(':input').val(input.val());
    // hide current button
    $this.parent().siblings('.input-group').find('button.current').hide();
    // mark as reviewed
    const wrapper = $this.parent().parent();
    wrapper.removeClass('before-review').addClass('after-review');
    // hide incoming input
    input.hide();
    // change button text
    $this.text('Revert changes');
    $this.addClass('undo');
  }

  function revertChanges($this) {
    $this.removeClass('undo');
    const input = $this.next(':input');
    // find main input
    const currentInput = $this.parent().siblings('.input-group').find(':input');
    // copy undo value to main input
    currentInput.val(currentInput.attr("data-undo"));
    // show current button
    $this.parent().siblings('.input-group').find('button.current').show();
    // mark as un-reviewed
    const wrapper = $this.parent().parent();
    wrapper.removeClass('after-review').addClass('before-review');
    // hide incoming input
    input.show();
    // change button text
    $this.text('Accept incoming');
  }

  // cloning form fields


  // priority
  $('.add-priority-over').on('click', function (evt) {
    evt.preventDefault();
    $('.priority-group:last').clone().appendTo('.priority-list').find("input").val("").end();
  });
  $('.delete-priority-over').on('click', function (evt) {
    evt.preventDefault();
    $(this).closest(".priority-group").remove();
  });


  // reference
  $('#add-reference').on('click', function (evt) {
    evt.preventDefault();
    $('.reference-group:last').clone(true).appendTo('.references').find("input").val("").end();
  });
  $('.delete-reference').on('click', function (evt) {
    evt.preventDefault();
    $(this).closest(".reference-group").remove();
  });


  // signature
  $('#add-signature').on('click', function (evt) {
    evt.preventDefault();
    // Clone using array syntax like thymeleaf does: name="internalSignatures[0].name"
    // name="internalSignatures[0].byteSequences[0].signature"
    const lastSig = $('#signature-container .signature:last');
    const name = $(lastSig).find('input:first').attr('name');
    const lastIndex = /internalSignatures\[(\d+)\]/.exec(name)[1];
    if (lastIndex === undefined) return;
    const newIndex = parseInt(lastIndex) + 1;
    const type = $('#select-signature-type').val() == 'signature' ? 'signature' : 'container-signature';
    const clone = $(`.holder .${type}:last`).clone(true);
    clone.find(':input').each(function () {
      const $this = $(this);
      // ignore buttons which get caught by :input as well
      if ($this.is('button')) return;
      $this.attr('name', $this.attr('name').replace(/internalSignatures\[\d+\]/, `internalSignatures[${newIndex}]`));
    });
    clone.appendTo('#signature-container');
  });
  $('.delete-signature').on('click', function (evt) {
    evt.preventDefault();
    $(this).closest(".signature").remove();
  });
  $('.delete-container-signature').on('click', function (evt) {
    evt.preventDefault();
    $(this).closest(".container-signature").remove();
  });
  // byte sequence
  $('.add-byte-sequence').on('click', function (evt) {
    evt.preventDefault();
    const $container = $(this).closest('.byte-sequence-list').find('.list');
    const lastSeq = $container.find('.byte-sequence:last');
    const name = $(lastSeq).find('input:first').attr('name');
    const lastIndex = /byteSequences\[(\d+)\]/.exec(name)[1];
    if (lastIndex === undefined) return;
    const newIndex = parseInt(lastIndex) + 1;
    const clone = $('.holder .byte-sequence:last').clone(true)
    clone.find(':input').each(function () {
      const $this = $(this);
      // ignore buttons which get caught by :input as well
      if ($this.is('button')) return;
      $this.attr('name', $this.attr('name').replace(/byteSequences\[\d+\]/, `byteSequences[${newIndex}]`));
    });
    clone.appendTo($container);
  });
  $('.delete-byte-sequence').on('click', function (evt) {
    evt.preventDefault();
    $(this).closest(".byte-sequence").remove();
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
  $('.add-relationship').on('click', function (evt) {
    evt.preventDefault();
    $('.relationship:last').clone(true).appendTo('#relationships-list').find("input").val("").end();
  });
  $('.delete-relationship').on('click', function (evt) {
    evt.preventDefault();
    $(this).closest(".relationship").remove();
  });

  // display documentation fields
  $('.documentation input:checkbox').change(function(){
    if($(this).is(":checked")) {
      $(this).closest('.reference-group').addClass("open-additional");
    } else {
      $(this).closest('.reference-group').removeClass("open-additional");
    }
  });
  // TODO check on windows if issue 18 from Typos doc has been fixed
  // blocking tooltip to display when select option is visible 
  $('option').each(function () {
    if ($(this).css('display') != 'none') {
      $('.tooltip-toggle:hover:before').css('display', 'none');
    }else{
      $('.tooltip-toggle:hover:before').css('display', 'inherit');
    }
  });

}

App()