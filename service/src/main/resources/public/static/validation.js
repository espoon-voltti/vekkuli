const validation = (function () {
  function init(config) {
    config.forms.forEach(formId => {
      const form = document.getElementById(formId);
      if (form) {
        setupFormValidation(form);
      } else {
        console.warn(`Form with id '${formId}' not found.`);
      }
    });
  }

  function updateGlobalErrorMessage(form) {
    const globalErrorMessage = form.querySelector('.form-validation-message');
    const errorElements = form.querySelectorAll('[id$="-error"]');

    const hasVisibleErrors = Array.from(errorElements).some(el =>
      el.getAttribute('aria-hidden') === 'false'
    );

    if (globalErrorMessage) {
      globalErrorMessage.style.display = hasVisibleErrors ? 'unset' : 'none';
    }
  }

  function showError(element, show) {
    if (!element) return;
    element.setAttribute('aria-hidden', show ? 'false' : 'true');
    element.style.display = show ? 'unset' : 'none';
  }

  async function validateField(field) {
    const errorElements = {
      required: document.getElementById(`${field.id}-error`),
      pattern: document.getElementById(`${field.id}-pattern-error`),
      server: document.getElementById(`${field.id}-server-error`)
    };

    // Reset all error states
    Object.values(errorElements).forEach(el => showError(el, false));

    // Required validation
    if (errorElements.required) {
      const isRequired = field.hasAttribute('data-required') && !field.hasAttribute('disabled');
      const isEmpty = field.type === 'checkbox' ? !field.checked :
        field.value.trim() === '' || field.value === null;

      if (isRequired && isEmpty) {
        showError(errorElements.required, true);
        updateGlobalErrorMessage(field.closest('form'));
        return false;
      }
    }

    // Pattern validation
    if (errorElements.pattern && field.hasAttribute('data-pattern')) {
      const pattern = new RegExp(field.getAttribute('data-pattern'));
      if (!pattern.test(field.value)) {
        showError(errorElements.pattern, true);
        updateGlobalErrorMessage(field.closest('form'));
        return false;
      }
    }

    // Server-side validation
    if (field.hasAttribute('data-validate-url')) {
      try {
        const isServerValid = await validateFieldWithServer(field, errorElements.server);
        updateGlobalErrorMessage(field.closest('form'));
        return isServerValid;
      } catch (error) {
        console.error('Server validation error:', error);
        return false;
      }
    }

    updateGlobalErrorMessage(field.closest('form'));
    return true;
  }

  async function validateFieldWithServer(field, errorElement) {
    const validationUrl = field.getAttribute('data-validate-url');

    try {
      const response = await fetch(validationUrl, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ value: field.value })
      });

      const data = await response.json();

      if (data.isValid) {
        showError(errorElement, false);
        return true;
      } else {
        if (errorElement) {
          errorElement.textContent = data.message || 'Invalid value';
          showError(errorElement, true);
        }
        return false;
      }
    } catch (error) {
      if (errorElement) {
        errorElement.textContent = 'Validation failed';
        showError(errorElement, true);
      }
      throw error;
    }
  }

  async function validateForm(form) {
    const fields = form.querySelectorAll(
      '[data-required], [data-pattern], [data-validate-url]'
    );

    const validationPromises = Array.from(fields).map(validateField);
    const validationResults = await Promise.all(validationPromises);

    const isValid = validationResults.every(result => result);

    if (!isValid) {
      // Find and focus on the first invalid field
      const firstInvalidField = fields[validationResults.indexOf(false)];
      firstInvalidField.scrollIntoView({
        behavior: 'smooth',
        block: 'center'
      });
      firstInvalidField.focus();
    }

    return isValid;
  }

  function setupFormValidation(form) {
    // Handle submit button clicks
    form.addEventListener('click', async function (event) {
      if (event.target.matches('button[type="submit"], input[type="submit"]')) {
        const isValid = await validateForm(form);

        if (!isValid) {
          event.preventDefault();
        }
      }
    }, true);

    form.addEventListener('input', function (event) {
      const field = event.target;
      if (field.matches('[data-required], [data-pattern], [data-validate-url]')) {
        validateField(field);
      }
    });

    form.addEventListener('submit', function (event) {
      const submitButton = form.querySelector('button[type="submit"], input[type="submit"]');
      if(submitButton) {
        submitButton.setAttribute('disabled', 'disabled');
      }
    });
  }

  return { init };
})();
