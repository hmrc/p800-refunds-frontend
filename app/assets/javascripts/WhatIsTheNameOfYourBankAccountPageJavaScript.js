const inputId = "selectedBankId"

window.addEventListener("load", function(event) {
  var originalSelect = document.querySelector(`#${inputId}-select`);
  var form = document.querySelector("form");
  var combo = form.querySelector('[role="combobox"]');

  // Set error styles on input added by accessible autocomplete
  var errorSummary = $('h2[class="govuk-error-summary__title"]');
  if (errorSummary.length) {
    document.getElementById(inputId).className += " govuk-input--error input-error";
    document.getElementById(inputId).setAttribute("aria-describedby", `${inputId}-hint ${inputId}-error`)
  }

  // Ensures selection is a valid option from the list
  form.addEventListener('submit', function(e) {
    e.stopPropagation();
    e.preventDefault();
    var $this = $(this);

    if (originalSelect.querySelectorAll('[selected]').length > 0 || originalSelect.value > "") {
      var resetSelect = false;

      if (originalSelect.value) {
          if (combo.value != originalSelect.querySelector(`option[value="${originalSelect.value}"]`).text) {
          resetSelect = true;
        }
      }

      if (resetSelect) {
        originalSelect.value = "";
        if (originalSelect.querySelectorAll('[selected]').length > 0) {
          originalSelect.querySelectorAll('[selected]')[0].removeAttribute('selected');
        }
      }
    }

    $this.submit();
  });
});

// Init for Accessible Autocomplete
accessibleAutocomplete.enhanceSelectElement({
  confirmOnBlur: true,
  defaultValue: '',
  selectElement: document.querySelector(`#${inputId}`),
  showAllValues: true,
  displayMenu: 'overlay',
  autoselect: true
})
