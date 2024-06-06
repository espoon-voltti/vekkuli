// SPDX-FileCopyrightText: 2023-2024 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

const validation = (function() {

  function init(config) {
    config.forms.forEach(function(formId) {
      const form = document.getElementById(formId);
      if (form) {
        setupFormValidation(form);
      } else {
        console.warn(`Form with id '${formId}' not found.`);
      }
    });
  }

  function validateField(field) {
    const errorMessageElement = document.getElementById(`${field.id}-error`);
    let isValid = true;

    if (errorMessageElement) {
      if (field.hasAttribute('data-required') && !field.hasAttribute('disabled')) {
        if (field.tagName === 'SELECT' && field.value === "" || field.value.trim() === "") {
          isValid = false;
          errorMessageElement.style.display = 'block';
        } else {
          errorMessageElement.style.display = 'none';
        }
      }

      if (isValid && field.hasAttribute('data-pattern')) {
        const pattern = new RegExp(field.getAttribute('data-pattern'));
        if (!pattern.test(field.value)) {
          isValid = false;
          errorMessageElement.style.display = 'block';
        } else {
          errorMessageElement.style.display = 'none';
        }
      }
    }

    return isValid;
  }

  function setupFormValidation(form) {
    form.addEventListener('submit', function(event) {
      let isValid = true;

      const fields = form.querySelectorAll('[data-required], [data-pattern]');

      fields.forEach(function(field) {
        if (!validateField(field)) {
          isValid = false;
        }
      });

      if (!isValid) {
        event.preventDefault();
      }
    });

    form.addEventListener('change', function(event) {
      const field = event.target;
      validateField(field);
    });
  }

  return {
    init: init
  };
})();
