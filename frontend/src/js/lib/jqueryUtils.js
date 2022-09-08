import "jquery-ui/ui/widgets/autocomplete"

export function setupReviewFields() {
  $('.next, .prev, .nextSkip, .segment').on('click', function (evt) {
    if (formParts[formStep] === '#review') {
      reviewFieldsInner();
    }
  });
}

function reviewFieldsInner() {
  const $revContainer = $('section#review #review-container')
  const singleFields = [
    'file-format-name',
    'version-container',
    'file-format-description',
  ];
  singleFields.forEach(field => {
    const val = $(`.form-row.${field} .input-group:first input,textarea`).val() || '---';
    $revContainer.find(`.${field}`).text(val);
  });

  const selectFields = [
    'format-type-container'
  ];

  selectFields.forEach(field => {
    const val = $(`.form-row.${field} .input-group:first option:selected`).text() || '---';
    $revContainer.find(`.${field}`).text(val);
  });

  // Priority over
  const $priorityContainer = $revContainer.find('#priority-over');
  $('fieldset.priority-over').each((i, p) => {
    if (i === 0) {
      // cleanup prev instances
      $priorityContainer.find('.element').remove();
    }
    const $priority = $(p).find('.input-group:first input.label');
    const val = $priority.val() || '---';
    const element = $('<div>', { class: 'element' }).append(
      $('<p>', { class: 'label' }).text("File format"),
      $('<p>', { class: 'value' }).text(val)
    )
    $priorityContainer.append(element)
  });

  // references
  const $refContainer = $revContainer.find('.part.references');
  // cleanup prev instances
  $('fieldset.references').each((i, r) => {
    if (i === 0) {
      $refContainer.find('.count').remove();
    }
    const $ref = $(r).find('.reference-group');
    const documentationPart =
      $('<div>', { class: 'ifDocumentation' }).append(
        $('<div>', { class: 'element' }).append(
          $('<p>', { class: 'label' }).text("Author"),
          $('<p>', { class: 'value' }).text($ref.find('.reference-author .input-group:first input.label').val() || '---')
        ),
        $('<div>', { class: 'element' }).append(
          $('<p>', { class: 'label' }).text("Publication Date"),
          $('<p>', { class: 'value' }).text($ref.find('.publication-date .input-group:first input.date').val() || '---')
        ),
        $('<div>', { class: 'element' }).append(
          $('<p>', { class: 'label' }).text("Publication Type"),
          $('<p>', { class: 'value' }).text($ref.find('.publication-type .input-group:first input').val() || '---')
        ),
        $('<div>', { class: 'element' }).append(
          $('<p>', { class: 'label' }).text("Publication Note"),
          $('<p>', { class: 'value' }).text($ref.find('.publication-note .input-group:first textarea').val() || '---')
        ),
      )

    const isDocumentation = $ref.find('.reference-documentation .input-group:first input').is(':checked');
    const element = $('<div>', { class: 'count' }).append(
      $('<p>').text(i + 1),
      $('<div>', { class: 'review-part three' }).append(
        $('<div>', { class: 'element' }).append(
          $('<p>', { class: 'label' }).text("Name"),
          $('<p>', { class: 'value' }).text($ref.find('.reference-name .input-group:first input').val() || '---')
        ),
        $('<div>').append(
          $('<div>', { class: 'element' }).append(
            $('<p>', { class: 'label' }).text("Link"),
            $('<p>', { class: 'value' }).text($ref.find('.reference-link .input-group:first input').val() || '---')
          )
        ),
        ...(isDocumentation ? [documentationPart] : []),
        $('<div>', { class: 'element' }).append(
          $('<p>', { class: 'label' }).text("Documentation"),
          $('<p>', { class: 'value' }).text(isDocumentation ? 'Yes' : 'No')
        )
      )
    );
    $refContainer.append(element)
  });
  // signatures
  const $sigContainer = $revContainer.find('.part.signatures');
  $('fieldset.signature:not(.template)').each((i, s) => {
    if (i === 0) {
      $sigContainer.find('.count').remove();
    }
    const $ref = $(s);
    const sequences = $ref.find('.byte-sequence-list .byte-sequence').map((j, seq) => {
      const $seq = $(seq);
      return $('<div>', { class: 'bordered-box' }).append(
        $('<div>', { class: 'element' }).append(
          $('<p>', { class: 'label' }).text("Binary value"),
          $('<p>', { class: 'value' }).text($seq.find('.binary-value .input-group:first input').val() || '---')
        ),
        $('<div>', { class: 'element' }).append(
          $('<p>', { class: 'label' }).text("Position type"),
          $('<p>', { class: 'value' }).text($seq.find('.position-type .input-group:first option:selected').text() || '---')
        ),
        $('<div>', { class: 'element' }).append(
          $('<p>', { class: 'label' }).text("Min offset"),
          $('<p>', { class: 'value' }).text($seq.find('.min-offset .input-group:first input').val() || '---')
        ),
        $('<div>', { class: 'element' }).append(
          $('<p>', { class: 'label' }).text("Max offset"),
          $('<p>', { class: 'value' }).text($seq.find('.max-offset .input-group:first input').val() || '---')
        ),
      );
    })
    const element = $('<div>', { class: 'count' }).append(
      $('<p>').text(i + 1),
      $('<div>', { class: 'review-part' }).append(
        $('<div>', { class: 'element' }).append(
          $('<p>', { class: 'label' }).text("Name"),
          $('<p>', { class: 'value' }).text($ref.find('.signature-name .input-group:first input').val() || '---')
        ),
        $('<h4>').text('Byte Sequence'),
        $('<div>').append(
          $('<div>', { class: 'element' }).append(
            $('<p>', { class: 'label' }).text("Endianness"),
            $('<p>', { class: 'value' }).text($ref.find('.byte-order .input-group:first input').val() || '---')
          )
        ),
        $('<div>', { class: 'element' }).append(
          $('<p>', { class: 'label' }).text("Description"),
          $('<p>', { class: 'value' }).text($ref.find('.signature-description .input-group:first textarea').val() || '---')
        ),
        ...sequences
      )
    );
    $sigContainer.append(element);
  });
  // relationships
  const $relContainer = $revContainer.find('#review-relationships:visible');
  $('fieldset.relationships').each((i, r) => {
    if (i === 0) {
      // cleanup prev instances
      $relContainer.find('.two').remove();
    }
    const $rel = $(r);
    const type = $rel.find('.rel-type .input-group:first option:selected').text() || '---';
    const ff = $rel.find('.rel-ff .input-group:first input.label').val() || '---';

    const element = $('<div>', { class: 'two' }).append(
      $('<div>', { class: 'element' }).append(
        $('<p>', { class: 'label' }).text("Relationship type"),
        $('<p>', { class: 'value' }).text(type)
      ),
      $('<div>', { class: 'element' }).append(
        $('<p>', { class: 'label' }).text("Related file format"),
        $('<p>', { class: 'value' }).text(ff)
      ),
    );
    $relContainer.append(element);
  });
  // format families
  const $ffContainer = $revContainer.find('#rel-fam:visible');
  $('fieldset.format-families').each((i, ff) => {
    if (i === 0) {
      // cleanup prev instances
      $ffContainer.find('> .element').remove();
    }

    const $fam = $(ff);
    const fam = $fam.find('.input-group:first option:selected').text() || '---';

    const element = $('<div>', { class: 'element' }).append(
      $('<p>', { class: 'label' }).text("Format family"),
      $('<p>', { class: 'value' }).text(fam)
    );
    $ffContainer.append(element);
  });


}

export function autocomplete(field, selector) {
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

export function FAQWordFilter(evt) {
  evt.preventDefault();
  console.log("searching");
  const term = $(this).val().toLowerCase().trim();
  if (term == "" || term.length < 3) {
    $('.search-hide').removeClass('search-hide');
  }
  $('.category').each(function (i, c) {
    const $category = $(c);
    $category.addClass('search-hide');
    $category.find('li').addClass('search-hide');
    const catTitleMatches = $category.find('h2').text().toLowerCase().trim().includes(term);
    $category.find('li').each((i, li) => {
      const $li = $(li);
      const title = $li.find('button span').text();
      const content = $li.find('.content *').map((i, elem) => $(elem).text());
      const texts = [title, ...content].map(text => text.toLowerCase().trim());
      if (catTitleMatches || texts.some(text => text.includes(term))) {
        $li.removeClass('search-hide');
        $category.removeClass('search-hide');
      }
    });
  })
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

function refreshIndices($elem, field) {
  $elem.each(function (idx) {
    $(this).find(':input').each(function () {
      const $this = $(this);
      // ignore buttons which get caught by :input as well
      if ($this.is('button')) return;
      const newName = $this.attr('name').replace(new RegExp(`${field}\\[\\d+\\]`), `${field}[${idx}]`);
      $this.attr('name', newName);
    });
  });
}

function replaceAllNames($elem, oldName, newName) {
  $elem.find(':input').each(function () {
    const $this = $(this);
    // ignore buttons which get caught by :input as well
    if ($this.is('button')) return;
    $this.attr('name', $this.attr('name').replace(oldName, newName));
  });
}

function enableFields($elem) {
  $elem.find(':input').each(function () {
    const $this = $(this);
    // ignore buttons which get caught by :input as well
    if ($this.is('button')) return;
    $this.prop('disabled', false);
  });
}

export function setupSignatureMultifield() {
  $('#add-signature').on('click', function (evt) {
    evt.preventDefault();
    // Clone using array syntax like thymeleaf does: name="internalSignatures[0].name"
    // name="internalSignatures[0].byteSequences[0].signature"
    const type = $('#select-signature-type').val() == 'signature' ? 'signature' : 'container-signature';
    const clone = $(`.holder .${type}:last`).clone(true);
    clone.removeClass('template');
    enableFields(clone);
    clone.appendTo('#signature-container');
    // Refresh indices
    const field = type == 'signature' ? 'internalSignatures' : 'containerSignatures'
    refreshIndices($(`#signature-container .${type}:not(.template)`), field);
  });
  $('.delete-signature').on('click', function (evt) {
    evt.preventDefault();
    $(this).closest(".signature").remove();
    refreshIndices($(`#signature-container .signature:not(.template)`), 'internalSignatures');
  });
  $('.delete-container-signature').on('click', function (evt) {
    evt.preventDefault();
    $(this).closest(".container-signature").remove();
    refreshIndices($(`#signature-container .container-signature:not(.template)`), 'containerSignatures');
  });
}

export function setupContainerAddPath() {
  $('.add-path').on('click', function (evt) {
    evt.preventDefault();
    const $container = $(this).closest('.paths-list-container').find('.paths-list');
    const clone = $('.holder .path:last').clone(true);
    clone.removeClass('template');
    enableFields(clone);
    const parentName = $container.parentsUntil('fieldset').find('textarea').attr('name').split('.').find(x => !!x);
    replaceAllNames(clone, 'containerSignatures[0]', parentName);
    clone.appendTo($container);
    refreshIndices($container.find('.path:not(.template)'), 'files');
  });

  $('.delete-path').on('click', function (evt) {
    evt.preventDefault();
    const $container = $(this).closest('.paths-list-container').find('.paths-list');
    $(this).closest(".path").remove();
    refreshIndices($container.find('.path:not(.template)'), 'files');
  });
}

export function setupByteSeqMultifield() {
  // TODO: make this work with container signatures and make the Path work as well
  $('.add-byte-sequence').on('click', function (evt) {
    evt.preventDefault();
    const $signature = $(this).parentsUntil('#signature-container').last();
    const $container = $(this).parentsUntil('.byte-sequence-list').last().siblings('.list');
    const type = $signature.hasClass('container-signature') ? 'files' : 'internalSignatures';

    const clone = $('.holder .byte-sequence:last').clone(true)
    clone.removeClass('template');
    enableFields(clone);
    let parentName;
    if (type == 'internalSignatures') {
      parentName = $container.parentsUntil('fieldset').find('textarea').attr('name').split('.').find(x => !!x);
    } else {
      const split = $container.parentsUntil('.path').last().siblings('.form-row').find('input').attr('name').split('.')
      split.pop();
      parentName = split.join('.');
    }
    replaceAllNames(clone, 'internalSignatures[0]', parentName);
    clone.appendTo($container);
    refreshIndices($container.find('.byte-sequence'), 'byteSequences');
  });
  $('.delete-byte-sequence').on('click', function (evt) {
    evt.preventDefault();
    const $container = $(this).parentsUntil(".byte-sequence-list").last()
    $(this).closest(".byte-sequence").remove();
    const $byteSeqs = $container.find('.byte-sequence:not(.template)')
    refreshIndices($byteSeqs, 'byteSequences');
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
      // ignore buttons and checkboxes which get caught by :input as well
      if ($this.is('button') || $this.is(':checkbox')) return;
      $this.attr('name', $this.attr('name').replace(/reference\[\d+\]/, `reference[${newIndex}]`));
      $this.prop('disabled', false);
    });
    clone.appendTo('.references');
  });
  $('.delete-reference').on('click', function (evt) {
    evt.preventDefault();
    $(this).closest(".reference-group").remove();
  });

  $('.documentation input:checkbox').change(function (evt) {
    evt.preventDefault();
    if ($(this).is(":checked")) {
      $(this).closest('.reference-group').addClass("open-additional");
    } else {
      $(this).closest('.reference-group').removeClass("open-additional");
    }
  });
  $('.documentation input:checkbox').trigger('change');
}

// export function setupFormNavigation() {
//   // Whenever .next is clicked add a step ========= NEXT
//   $('.next').on('click', (evt) => {
//     evt.preventDefault();
//     $(formParts[formStep]).removeClass('show');
//     $('.main-nav li').removeClass("active");
//     formStep++;
//     $(formParts[formStep]).addClass('show');
//     if (1 < formStep && formStep < 6) {
//       $(formMenuButtons[2]).addClass("active");
//     } else {
//       $(formMenuButtons[formStep]).addClass("active");
//     }
//   });
// Whenever .prev is clicked return a step ========= PREV
// $('.prev').on('click', (evt) => {
//   evt.preventDefault();
//   $(formParts[formStep]).removeClass('show');
//   $('.main-nav li').removeClass("active");
//   formStep--;
//   $(formParts[formStep]).addClass('show');
//   if (1 < formStep && formStep < 6) {
//     $(formMenuButtons[2]).addClass("active");
//   } else {
//     $(formMenuButtons[formStep]).addClass("active");
//   }
// });
// Whenever skipping 3 steps forwards (only aplied to More intormation in external interface) ========= NEXT + 3
// $('.nextSkip').on('click', (evt) => {
//   evt.preventDefault();
//   $(formParts[formStep]).removeClass('show');
//   $('.main-nav li').removeClass("active");
//   if (1 < formStep && formStep < 6) {
//     $(formMenuButtons[2]).addClass("active");
//   } else {
//     $('.main-nav li').removeClass("active");
//     formStep = 6
//     $(formMenuButtons[formStep]).addClass("active");
//   }
//   $('.main-nav li').removeClass("active");
//   formStep = 6
//   $(formMenuButtons[formStep]).addClass("active");
//   $(formParts[formStep]).addClass('show');

// });

// Navigation bar

// Main menu buttons
// $('.segment').on('click', function () {
//   $('.main-nav li').removeClass("active");
//   $('.side-menu li').removeClass("active");
//   $('.form-part').removeClass('show');
//   var currentBtn = ('#' + $(this).closest('li').attr('id'));
//   var currentFormPart = currentBtn.replace('Btn', '');
//   formStep = formParts.indexOf(currentFormPart);
//   $(currentBtn).addClass('active');
//   $(formParts[formStep]).addClass('show');
//   if (formStep === 2) {
//     $('#prioritySubBtn').closest('li').addClass('active');
//   }
// });

// Side menu buttons (More information)
// $('.segment-sub').on('click', function () {
//   $('.form-part').removeClass('show');
//   $('.side-menu li').removeClass("active");
//   var currentBtn = ('#' + $(this).closest('li').attr('id'));
//   var currentFormPart = currentBtn.replace('SubBtn', '');
//   formStep = formParts.indexOf(currentFormPart);
//   $(formParts[formStep]).addClass('show');
//   $(currentBtn).closest('li').addClass('active');
// });



// Whenever skipping 3 steps backwards (only aplied to More intormation in external interface) ========= PREV + 3
// $('.prevSkip').on('click', (evt) => {
//   evt.preventDefault();
//   $(formParts[formStep]).removeClass('show');
//   $('.main-nav li').removeClass("active");
//   if (1 < formStep && formStep < 6) {
//     formStep = 1;
//     $(formMenuButtons[formStep]).addClass("active");
//   } else {
//     $('.main-nav li').removeClass("active");
//     formStep--;
//     $(formMenuButtons[formStep]).addClass("active");
//   }
//   $(formParts[formStep]).addClass('show');
// });
// }



export function setupAddActorModal() {
  $('.add-actor-button').on('click', function (evt) {
    evt.preventDefault();
    $('.add-actor-modal').show();
    $('.add-actor-modal .close').show();
  });

  $('.add-contributor').on('click', function (evt) {
    evt.preventDefault();
    $('.add-actor-modal').show();
    $('.add-actor-modal .close').show();
    $("#ifContributor").attr("checked", true);
  });

  // $('.add-actor-modal .overlay').on('click', function (evt) {
  //   evt.preventDefault();
  //   $('.add-actor-modal').hide();
  // });

  $('.add-actor-modal .close').on('click', function (evt) {
    evt.preventDefault();
    $('.add-actor-modal').hide();
  });

  $(".add-actor-modal form").on("submit", function (evt) {
    evt.preventDefault();
    const $form = $(this)
    const dataString = $form.serialize();

    $.ajax({
      type: "POST",
      url: $form.prop('action'),
      data: dataString,
      success: function () {
        $('.add-actor-modal').hide();
        $form.trigger('reset');
      }
    });

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

export function setupPuidValidation(){
  $('.puid-type-container #puidType').on('change', function(){
    $(".puid-holder .next-puid").text(" ");
    const $this = $(this);
    const selected = $this.find("option:selected").text();
    $.ajax({ url: `/next-puid/${selected}`, success: function(result){
      $(".puid-holder .next-puid").text(`Next available PUID: ${result}`);
    }});
  });
  $('.puid-type-container #puidType').trigger('change');

  $('.file-format-puid #FileFormatPUID').on('input', function(){
    const $this = $(this);
    if($this.val() == "") return;
    const type = $(".puid-type-container #puidType option:selected").text();
    const puid = `${type}/${$this.val()}`;
    $.ajax({ url: `/puid-exists/${puid}`, success: function(result){
      if(result == "true"){
        $('.puid-valid').hide();
        $('.puid-exists').show();
      }else{
        $('.puid-valid').show();
        $('.puid-exists').hide();
      }
    }});
  });
  $('.file-format-puid #FileFormatPUID').trigger('input');
}

export default { autocomplete }