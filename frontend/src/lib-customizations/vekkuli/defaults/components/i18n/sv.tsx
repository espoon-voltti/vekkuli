// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import { Translations } from 'lib-components/i18n'

const components: Translations = {
  asyncButton: {
    inProgress: 'Laddar',
    failure: 'Något gick fel',
    success: 'Klar'
  },
  common: {
    add: 'Lägg till',
    cancel: 'Gå tillbaka',
    close: 'Stäng',
    confirm: 'Bekräfta',
    no: 'Nej',
    noResults: 'Inga sökresultat',
    open: 'Öppna',
    remove: 'Ta bort',
    saving: 'Sparar',
    saved: 'Sparad',
    search: 'Sök',
    yes: 'Ja',
    openExpandingInfo: 'Öppna detaljer'
  },
  datePicker: {
    placeholder: 'dd.mm.åååå',
    description:
      'Skriv in datumet i formatet dd.mm.åååå. Du kan komma till månadsväljaren med tabbtangenten.',
    validationErrors: {
      validDate: 'Ange i format dd.mm.åååå',
      dateTooEarly: 'Välj ett senare datum',
      dateTooLate: 'Välj ett tidigare datum'
    }
  },
  loginErrorModal: {
    header: 'Inloggningen misslyckades',
    message:
      'Autentiseringen för tjänsten misslyckades eller avbröts. För att logga in gå tillbaka och försök på nytt.',
    returnMessage: 'Gå tillbaka till inloggningen'
  },
  messages: {
    staffAnnotation: 'Personal',
    message: 'Meddelande',
    recipients: 'Mottagare',
    send: 'Skicka',
    sending: 'Skickar',
    types: {
      MESSAGE: 'Meddelande',
      BULLETIN: 'Anslag'
    },
    thread: {
      type: 'Meddelandetyp',
      urgent: 'Akut'
    }
  },
  messageReplyEditor: {
    // Currently only used for Finnish frontends
    discard: '',
    messagePlaceholder: undefined,
    messagePlaceholderSensitiveThread: undefined
  },
  sessionTimeout: {
    sessionExpiredTitle: 'Din session har gått ut',
    sessionExpiredMessage: 'Vänligen logga in igen.',
    goToLoginPage: 'Gå till inloggningssidan',
    cancel: 'Avbryt'
  },
  notifications: {
    close: 'Stäng'
  },
  offlineNotification: 'Ingen internetanslutning',
  reloadNotification: {
    title: 'En ny version av eVaka är tillgänglig',
    buttonText: 'Ladda om sidan'
  },
  treeDropdown: {
    // Currently only used for Finnish frontends
    expand: () => '',
    collapse: () => '',
    expandDropdown: ''
  },
  validationErrors: {
    required: 'Värde saknas',
    requiredSelection: 'Val saknas',
    format: 'Ange rätt format',
    integerFormat: 'Ange ett heltal',
    ssn: 'Ogiltigt personbeteckning',
    phone: 'Ogiltigt telefonnummer',
    email: 'Ogiltig e-postadress',
    preferredStartDate: 'Ogiltigt datum',
    timeFormat: 'Kolla',
    timeRequired: 'Nödvändig',
    dateTooEarly: 'Välj ett senare datum',
    dateTooLate: 'Välj ett tidigare datum',
    unitNotSelected: 'Välj minst en enhet',
    emailsDoNotMatch: 'E-postadresserna är olika',
    httpUrl: 'Ange i formen https://example.com',
    unselectableDate: 'Ogiltigt datum',
    openAttendance: 'Öppen närvaro',
    generic: 'Kolla',
    selectedValuesMin: 'Välj minst {min} värden',
    selectedValuesMax: 'Välj högst {max} värden',
    positiveNumber: 'Ange ett positivt tal',
    certify: 'Intyga att den information du har angett är korrekt',
    terms: 'Godkänn hamnreglerna'
  },
  links: {
    goBack: 'Tillbaka',
    edit: 'Redigera'
  }
}

export default components
