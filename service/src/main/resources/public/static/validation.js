const validation = (function () {
  function init(config) {
    config.forms.forEach(function (formId) {
      const form = document.getElementById(formId);
      if (form) {
        setupFormValidation(form);
        setupSubmitButtonBehavior(form);
      } else {
        console.warn(`Form with id '${formId}' not found.`);
      }
    });
  }

  function validateField(field) {
    const errorMessageElement = document.getElementById(`${field.id}-error`);
    const patternErrorMessageElement = document.getElementById(
      `${field.id}-pattern-error`,
    );
    const serverErrorMessageElement = document.getElementById(
      `${field.id}-server-error`,
    );
    let isValid = true;

    // Hide all error messages initially
    if (errorMessageElement) errorMessageElement.style.visibility = "hidden";
    if (patternErrorMessageElement)
      patternErrorMessageElement.style.visibility = "hidden";
    if (serverErrorMessageElement)
      serverErrorMessageElement.style.visibility = "hidden";

    if (errorMessageElement) {
      if (
        field.hasAttribute("data-required") &&
        !field.hasAttribute("disabled")
      ) {
        if (field.type === "checkbox") {
          if (!field.checked) {
            errorMessageElement.style.visibility = "visible";
            return false;
          } else {
            errorMessageElement.style.visibility = "hidden";
          }
        }
        if (
          (field.tagName === "SELECT" && field.value === "") ||
          field.value.trim() === "" ||
          field.value === null
        ) {
          errorMessageElement.style.visibility = "visible";
          return false;
        } else {
          errorMessageElement.style.visibility = "hidden";
        }
      }
    }

    if (patternErrorMessageElement) {
      if (isValid && field.hasAttribute("data-pattern")) {
        const pattern = new RegExp(field.getAttribute("data-pattern"));
        if (!pattern.test(field.value)) {
          patternErrorMessageElement.style.visibility = "visible";
          return false;
        } else {
          patternErrorMessageElement.style.visibility = "hidden";
        }
      }
    }

    if (field.hasAttribute("data-validate-url")) {
      return validateFieldWithServer(field);
    }

    return isValid;
  }

  function validateFieldWithServer(field) {
    const validationUrl = field.getAttribute("data-validate-url");
    const errorMessageElement = document.getElementById(
      `${field.id}-server-error`,
    );

    return fetch(validationUrl, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify({ value: field.value }), // Send the field value to the server
    })
      .then((response) => response.json())
      .then((data) => {
        if (data.isValid) {
          errorMessageElement.style.visibility = "hidden";
          errorMessageElement.innerHTML = data.message || "";
          return true;
        } else {
          errorMessageElement.style.visibility = "visible";
          errorMessageElement.innerHTML =
            data.message || "This value is invalid.";
          return false;
        }
      })
      .catch((error) => {
        console.error("Error validating field:", error);
        errorMessageElement.style.visibility = "visible";
        errorMessageElement.innerText =
          "Validation failed due to server error.";
        return false;
      });
  }

  function setupFormValidation(form) {
    form.addEventListener("submit", function (event) {
      let isValid = true;

      const fields = form.querySelectorAll(
        "[data-required], [data-pattern], [data-validate-url]",
      );

      fields.forEach(function (field) {
        if (!validateField(field)) {
          isValid = false;
        }
      });

      if (!isValid) {
        event.preventDefault();
      }
    });

    form.addEventListener("change", function (event) {
      const field = event.target;
      validateField(field);
    });
  }

  function setupSubmitButtonBehavior(form) {
    const submitButton = form.querySelector(
      'button[type="submit"], input[type="submit"]',
    );

    if (submitButton) {
      submitButton.addEventListener("click", function (event) {
        const updatedForm = document.getElementById(form.id);
        setupFormValidation(updatedForm);
        let isValid = true;

        const fields = updatedForm.querySelectorAll(
          "[data-required], [data-pattern], [data-validate-url]",
        );

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
    init: init,
  };
})();
