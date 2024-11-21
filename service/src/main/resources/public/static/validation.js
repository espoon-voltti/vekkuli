const validation = (function () {
  function init(config) {
    config.forms.forEach(function (formId) {
      const form = document.getElementById(formId);
      if (form) {
        setupFormValidation(form);
      } else {
        console.warn(`Form with id '${formId}' not found.`);
      }
    });
  }

  function setElementVisibility(element, isVisible) {
    element.style.visibility = isVisible ? "visible" : "hidden";
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
    if (errorMessageElement)
      setElementVisibility(errorMessageElement, false);
    if (patternErrorMessageElement)
      setElementVisibility(patternErrorMessageElement, false);
    if (serverErrorMessageElement)
      setElementVisibility(serverErrorMessageElement, false);

    if (errorMessageElement) {
      if (
        field.hasAttribute("data-required") &&
        !field.hasAttribute("disabled")
      ) {
        if (field.type === "checkbox") {
          if (!field.checked) {
            setElementVisibility(errorMessageElement, true);
            return false;
          } else {
            setElementVisibility(errorMessageElement, false);
          }
        }
        if (
          (field.tagName === "SELECT" && field.value === "") ||
          field.value.trim() === "" ||
          field.value === null
        ) {
          setElementVisibility(errorMessageElement, true);
          return false;
        } else {
          setElementVisibility(errorMessageElement, false);
        }
      }
    }

    if (patternErrorMessageElement) {
      if (isValid && field.hasAttribute("data-pattern")) {
        const pattern = new RegExp(field.getAttribute("data-pattern"));
        if (!pattern.test(field.value)) {
          setElementVisibility(patternErrorMessageElement, true);
          return false;
        } else {
          setElementVisibility(patternErrorMessageElement, false);
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
          setElementVisibility(errorMessageElement, false);
          errorMessageElement.innerHTML = data.message || "";
          return true;
        } else {
          setElementVisibility(errorMessageElement, true);
          errorMessageElement.innerHTML =
            data.message || "This value is invalid.";
          return false;
        }
      })
      .catch((error) => {
        console.error("Error validating field:", error);
        setElementVisibility(errorMessageElement, true);
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

    form.addEventListener("input", function (event) {
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
          event.preventDefault(); // Prevent the form submission
        }
      });
    }
  }

  return {
    init: init,
  };
})();
