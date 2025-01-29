// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import { Translations } from 'lib-components/i18n'

const components: Translations = {
  asyncButton: {
    inProgress: 'Loading',
    failure: 'Failed to load',
    success: 'Success'
  },
  common: {
    add: 'Add',
    cancel: 'Cancel',
    close: 'Close',
    confirm: 'Confirm',
    no: 'No',
    noResults: 'No results',
    open: 'Open',
    remove: 'Remove',
    saving: 'Saving',
    saved: 'Saved',
    search: 'Search',
    yes: 'Yes',
    openExpandingInfo: 'Open the details'
  },
  datePicker: {
    placeholder: 'dd.mm.yyyy',
    description:
      'Type the date in dd.mm.yyyy format. You can get to month picker with the tab key.',
    validationErrors: {
      validDate: 'Valid date format is dd.mm.yyyy',
      dateTooEarly: 'Pick a later date',
      dateTooLate: 'Pick an earlier date'
    }
  },
  loginErrorModal: {
    header: 'Login failed',
    message:
      'The identification process failed or was stopped. To log in, go back and try again.',
    returnMessage: 'Go back'
  },
  messages: {
    staffAnnotation: 'Staff',
    recipients: 'Recipients',
    message: 'Message',
    send: 'Send',
    sending: 'Sending',
    types: {
      MESSAGE: 'Message',
      BULLETIN: 'Bulletin'
    },
    thread: {
      type: 'Type',
      urgent: 'Urgent'
    }
  },
  messageReplyEditor: {
    // Currently only used for Finnish frontends
    discard: '',
    messagePlaceholder: undefined,
    messagePlaceholderSensitiveThread: undefined
  },
  sessionTimeout: {
    sessionExpiredTitle: 'Your session has timed out',
    sessionExpiredMessage: 'Please log in again.',
    goToLoginPage: 'Go to login page',
    cancel: 'Cancel'
  },
  notifications: {
    close: 'Close'
  },
  offlineNotification: 'No internet connection',
  reloadNotification: {
    title: 'New version of eVaka is available',
    buttonText: 'Reload page'
  },
  treeDropdown: {
    // Currently only used for Finnish frontends
    expand: () => '',
    collapse: () => '',
    expandDropdown: ''
  },
  stepIndicator: {
    title: 'Reservation steps'
  },
  validationErrors: {
    required: 'Value missing',
    requiredSelection: 'Please select one of the options',
    format: 'Give value in correct format',
    integerFormat: 'Give an integer value',
    ssn: 'Invalid person identification number',
    phone: 'Invalid telephone number',
    email: 'Invalid email',
    preferredStartDate: 'Invalid preferred start date',
    timeFormat: 'Check',
    timeRequired: 'Required',
    dateTooEarly: 'Pick a later date',
    dateTooLate: 'Pick an earlier date',
    unitNotSelected: 'Pick at least one choice',
    emailsDoNotMatch: 'The emails do not match',
    httpUrl: 'Valid url format is https://example.com',
    unselectableDate: 'Invalid date',
    openAttendance: 'Open attendance',
    generic: 'Check',
    selectedValuesMin: 'Select at least {min} values',
    selectedValuesMax: 'Select at most {max} values',
    positiveNumber: 'Enter a positive number',
    certify: 'Certify that the information you provided is correct',
    terms: 'Accept the harbor rules',
    validDate: 'Valid date format is dd.mm.yyyy'
  },
  links: {
    goBack: 'Back',
    delete: 'Delete',
    edit: 'Edit'
  }
}

export default components
