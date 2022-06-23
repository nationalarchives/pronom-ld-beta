import '@styles/main.scss'
import '@styles/user-form.scss'
import '@styles/internal-form.scss'
import { css } from 'jquery'

const formParts = ['#core', '#signatures', '#relationships', '#identifiers', '#additionalProperties', '#contributors', '#review']
const formMenuButtons = ['#coreBtn', '#signaturesBtn', '#relationshipsBtn', '#identifiersBtn', '#additionalPropertiesBtn', '#contributorsBtn', '#reviewBtn']
const App = () => {

  $(document).ready(function() {
    checkReviewIndicators()
  });

  // TODO write a function which will enable indicator in the form nav
  function checkReviewIndicators(){

    if($('.form-row').hasClass('before-review')) {
      $(".before-review").closest('section').each(function() {
        var id = this.id;
        $('#' + id + 'Btn').addClass('before');
      });   
    } 
    
    if ($('.form-row').hasClass('after-review')) {
      $(".after-review").closest('section').each(function() {
        var id = this.id;
        $('#' + id + 'Btn').addClass('after');
      });   
    }

  }
  // Signal JS is active
  $('.modal-container').removeClass('noJS');
  $('.modal-container').addClass('minWidth');
  // Initialise form
  let formStep = 0
  $('.form-part').addClass('hide');

  // active class to the button
  $(formMenuButtons[formStep]).addClass( "active" );
  // Show first step
  $(formParts[formStep]).addClass('show');
  $('.next').on('click', () => {
    $(formParts[formStep]).removeClass('show');
    $( '.main-nav li' ).removeClass( "active" );
    formStep++;
    $(formParts[formStep]).addClass('show');
    $(formMenuButtons[formStep]).addClass( "active" );
  });
  // Whenever .prev is clicked return a step ========= PREV
  $('.prev').on('click', function() {
    $(formParts[formStep]).removeClass('show');
    $( '.main-nav li' ).removeClass( "active" );
    formStep--;
    $(formParts[formStep]).addClass('show');
    $(formMenuButtons[formStep]).addClass( "active" );
  });
  // clicking on the side menu buttons
  $('.segment').on('click', function() {
    $( '.main-nav li' ).removeClass( "active" );
    $('.form-part').removeClass('show');
    var currentBtn = ('#' + $(this).closest('li').attr('id'));
    var currentFormPart = currentBtn.replace('Btn', '');
    formStep = formParts.indexOf(currentFormPart);
    $( currentBtn ).addClass('active');
    $(formParts[formStep]).addClass('show');
  });


  // cloning form fields
  
  // priority
  $('.add-priority-over').on('click', function(evt) {
    console.log('clicked')
    evt.preventDefault();
    $('.priority-group:last').clone(true).appendTo('.priority-list').find("input").val("").end();
  });
  $('.delete-priority-over').on('click', function(evt) {
    evt.preventDefault();
    $(this).closest(".priority-group").remove();
  });



  // reference
  $('#add-reference').on('click', function(evt) {
    evt.preventDefault();
    $('.reference-group:last').clone(true).appendTo('.references').find("input").val("").end();
  });
  $('.delete-reference').on('click', function(evt) {
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
    const lastIndexRg = /internalSignatures\[(\d+)\]/.exec(name);
    const newIndex = lastIndexRg ? parseInt(lastIndexRg[1]) + 1 : 0;
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
  $('.delete-signature').on('click', function(evt) {
    evt.preventDefault();
    $(this).closest(".signature").remove();
  });
  $('.delete-container-signature').on('click', function(evt) {
    evt.preventDefault();
    $(this).closest(".container-signature").remove();
  });
  // byte sequence
  $('.add-byte-sequence').on('click', function (evt) {
    evt.preventDefault();
    const $container = $(this).closest('.byte-sequence-list').find('.list');
    const lastSeq = $container.find('.byte-sequence:last');
    const name = $(lastSeq).find('input:first').attr('name');
    const lastIndexRg = /byteSequences\[(\d+)\]/.exec(name)
    const newIndex = lastIndexRg ? parseInt(lastIndexRg[1]) + 1 : 0;
    const clone = $('.holder .byte-sequence:last').clone(true)
    clone.find(':input').each(function () {
      const $this = $(this);
      // ignore buttons which get caught by :input as well
      if ($this.is('button')) return;
      $this.attr('name', $this.attr('name').replace(/byteSequences\[\d+\]/, `byteSequences[${newIndex}]`));
    });
    clone.appendTo($container);
  });
  $('.delete-byte-sequence').on('click', function(evt) {
    evt.preventDefault();
    $(this).closest(".byte-sequence").remove();
  });
  // path
  $('.add-path').on('click', function(evt) {
    evt.preventDefault();
    var $container =$(this).closest('.paths-list-container').find('.paths-list');
    $('.holder .path:last').clone(true).appendTo($container).find("input").val("").end();
  });
  $('.delete-path').on('click', function(evt) {
    evt.preventDefault();
    $(this).closest(".path").remove();
  });
  // collapse-all
  $('.collapse-all-signatures').on('click', function(evt) {
    evt.preventDefault();
    $( '.collapse-content-container' ).addClass( "collapse" );
  });
  // open-all
  $('.open-all-signatures').on('click', function(evt) {
    evt.preventDefault();
    $( '.collapse-content-container' ).removeClass( "collapse" );
  });
  // open selected
  $('.accordion').on('click', function(evt) {
    evt.preventDefault();
    // $( '.collapse-content-container' ).removeClass( "collapse" );
    if( $(this).closest(".collapse-content-container").hasClass("collapse")) {
      $(this).closest(".collapse-content-container").removeClass("collapse")
    }else{
      $(this).closest(".collapse-content-container").addClass("collapse")
    }
  });


  // identifier
  $('.add-identifier').on('click', function(evt) {
    evt.preventDefault();
    $('.identifier:last').clone(true).appendTo('#identifiers-list').find("input").val("").end();
  });
  $('.delete-identifier').on('click', function(evt) {
    evt.preventDefault();
    $(this).closest(".identifier").remove();
  });



  // aliases
  $('.add-alias').on('click', function(evt) {
    evt.preventDefault();
    $('.alias:last').clone(true).appendTo('#aliases-list').find("input").val("").end();
  });
  $('.delete-alias').on('click', function(evt) {
    evt.preventDefault();
    $(this).closest(".alias").remove();
  });



  // relationships
  $('.add-relationship').on('click', function(evt) {
    evt.preventDefault();
    $('.relationship:last').clone(true).appendTo('#relationships-list').find("input").val("").end();
  });
  $('.delete-relationship').on('click', function(evt) {
    evt.preventDefault();
    $(this).closest(".relationship").remove();
  });


  //contributor
  $('.add-record').on('click', function(evt) {
    evt.preventDefault();
    $('.action-container:last').clone(true).appendTo('.contributors-list');
  });
  $('.delete-record').on('click', function(evt) {
    evt.preventDefault();
    $(this).closest(".action-container").remove();
  });


      
}

App()