const validation = (function () {

    function init(config) {
      config.forms.forEach(function (formId) {
        const form = document.getElementById(formId);
        if (form) {
          setupFormValidation(form);
          setupSubmitButtonBehavior(form)
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
          if (field.type === 'checkbox') {
            console.log("validating checkbox")
            if (!field.checked) {
              console.log("invalid")
              errorMessageElement.style.visibility = 'visible';
              return false
            } else {
              console.log("valid")
              errorMessageElement.style.visibility = 'hidden';
            }
          }
          if (field.tagName === 'SELECT' && field.value === "" || field.value.trim() === "") {
            errorMessageElement.style.visibility = 'visible';
            return false
          } else {
            errorMessageElement.style.visibility = 'hidden';
          }
        }
        if (isValid && field.hasAttribute('data-pattern')) {
          const pattern = new RegExp(field.getAttribute('data-pattern'));
          if (!pattern.test(field.value)) {
            errorMessageElement.style.visibility = 'visible';
            return false
          } else {
            errorMessageElement.style.visibility = 'hidden';
          }
        }
      }

      return isValid;
    }

    function setupFormValidation(form) {
      form.addEventListener('submit', function (event) {
        let isValid = true;

        const fields = form.querySelectorAll('[data-required], [data-pattern]');

        fields.forEach(function (field) {
          if (!validateField(field)) {
            isValid = false;
          }
        });

        if (!isValid) {
          event.preventDefault();
        }
      });

      form.addEventListener('change', function (event) {
        const field = event.target;
        validateField(field);
      });
    }

    function setupSubmitButtonBehavior(form) {
      const submitButton = form.querySelector('button[type="submit"], input[type="submit"]');

      if (submitButton) {
        submitButton.addEventListener('click', function (event) {
          let isValid = true;

          const fields = form.querySelectorAll('[data-required], [data-pattern]');

          fields.forEach(function (field) {
            if (!validateField(field)) {
              isValid = false;
            }
          });

          if (!isValid) {
            event.preventDefault(); // Prevent the form submission
          }
        });
      }
    }

    return {
      init: init
    };
  }

)
();
