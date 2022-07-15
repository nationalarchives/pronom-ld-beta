import "jquery-ui/ui/widgets/autocomplete"

export function autocomplete(field, selector) {
  console.log('autocomplete: ' + selector);
  $(document).on('keydown.autocomplete', selector, function () {
    const options = {
      source: `/autocomplete/${field}`,
      select: function (event, ui) {
        $(event.target).val(ui.item.label);
        $(event.target).parent().find('input.value').val(ui.item.value);
        return false;
      }
    }
    $(this).autocomplete(options);
  });
}

export function optionWorkaround() {
  // TODO check on windows if issue 18 from Typos doc has been fixed
  // blocking tooltip to display when select option is visible 
  $('option').each(function () {
    if ($(this).css('display') != 'none') {
      $('.tooltip-toggle:hover:before').css('display', 'none');
    } else {
      $('.tooltip-toggle:hover:before').css('display', 'inherit');
    }
  });
}

export function setupSignatureMultifield() {
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
      $this.prop('disabled', false);
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
}

export function setupPriorityMultifield() {
  $('.add-priority-over').on('click', function (evt) {
    evt.preventDefault();
    const last = $('.priority-group:last')
    const name = $(last).find('input:first').attr('name');
    const lastIndexRg = /hasPriorityOver\[(\d+)\]/.exec(name);
    const newIndex = lastIndexRg ? parseInt(lastIndexRg[1]) + 1 : 0;
    const clone = last.clone(true)
    clone.find(':input').each(function () {
      const $this = $(this);
      // ignore buttons which get caught by :input as well
      if ($this.is('button')) return;
      $this.val('');
      $this.attr('name', $this.attr('name').replace(/hasPriorityOver\[\d+\]/, `hasPriorityOver[${newIndex}]`));
      $this.prop('disabled', false);
    });
    clone.appendTo('.priority-list');
  });
  $('.delete-priority-over').on('click', function (evt) {
    evt.preventDefault();
    $(this).closest(".priority-group").remove();
  });
}

export function setupRelationshipMultifield() {
  $('.add-relationship').on('click', function (evt) {
    evt.preventDefault();
    const last = $('.relationship:last')
    const name = $(last).find('input:first').attr('name');
    const lastIndexRg = /hasRelationships\[(\d+)\]/.exec(name);
    const newIndex = lastIndexRg ? parseInt(lastIndexRg[1]) + 1 : 0;
    const clone = last.clone(true)
    clone.find(':input').each(function () {
      const $this = $(this);
      // ignore buttons which get caught by :input as well
      if ($this.is('button')) return;
      $this.val('');
      $this.attr('name', $this.attr('name').replace(/hasRelationships\[\d+\]/, `hasRelationships[${newIndex}]`));
      $this.prop('disabled', false);
    });
    clone.appendTo('#relationships-list');
  });
  $('.delete-relationship').on('click', function (evt) {
    evt.preventDefault();
    $(this).closest(".relationship").remove();
  });
}

export function setupByteSeqMultifield(){
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
      $this.prop('disabled', false);
    });
    clone.appendTo($container);
  });
  $('.delete-byte-sequence').on('click', function (evt) {
    evt.preventDefault();
    $(this).closest(".byte-sequence").remove();
  });
}

export function setupReferenceMultifield() {
  $('#add-reference').on('click', function (evt) {
    evt.preventDefault();
    const last = $('.reference-group:last')
    const name = $(last).find('input:first').attr('name');
    const lastIndexRg = /reference\[(\d+)\]/.exec(name);
    const newIndex = lastIndexRg ? parseInt(lastIndexRg[1]) + 1 : 0;
    const clone = last.clone(true)
    clone.find(':input').each(function () {
      const $this = $(this);
      // ignore buttons which get caught by :input as well
      if ($this.is('button')) return;
      $this.val('');
      $this.attr('name', $this.attr('name').replace(/reference\[\d+\]/, `reference[${newIndex}]`));
      $this.prop('disabled', false);
    });
    clone.appendTo('.references');
  });
  $('.delete-reference').on('click', function (evt) {
    evt.preventDefault();
    $(this).closest(".reference-group").remove();
  });
}

export function setupFormNavigation(){
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
  $('.segment').on('click', function () {
    $('.main-nav li').removeClass("active");
    $('.side-menu li').removeClass("active");
    $('.form-part').removeClass('show');
    var currentBtn = ('#' + $(this).closest('li').attr('id'));
    var currentFormPart = currentBtn.replace('Btn', '');
    formStep = formParts.indexOf(currentFormPart);
    $(currentBtn).addClass('active');
    $(formParts[formStep]).addClass('show');
    console.log(formStep);
    if (formStep === 2) {
      $('#prioritySubBtn').closest('li').addClass('active');
    }
  });

  // Side menu buttons (More information)
  $('.segment-sub').on('click', function () {
    $('.form-part').removeClass('show');
    $('.side-menu li').removeClass("active");
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

export function setupForm() {
  if ($(window).width() < 1200) {
    $('.form-section .form-part').addClass('show');
    // $(".form-partial-content").addClass('hide');
    $('.accordion').on('click', function (evt) {
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
    // active class to the button
    $(formMenuButtons[formStep]).addClass("active");
    // Show first step
    $(formParts[formStep]).addClass('show');
  }
}

// Incoming buttons functionality
// When clicked their value is copied to the current input
export function setupIncoming() {
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
}

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
export default { autocomplete }