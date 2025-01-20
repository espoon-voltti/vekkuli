// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import { Translations } from 'lib-components/i18n'

const components: Translations = {
  asyncButton: {
    inProgress: 'Ladataan',
    failure: 'Lataus epäonnistui',
    success: 'Valmis'
  },
  links: {
    goBack: 'Takaisin',
    delete: 'Poista',
    edit: 'Muokkaa'
  },
  common: {
    add: 'Lisää',
    cancel: 'Peruuta',
    close: 'Sulje',
    confirm: 'Vahvista',
    no: 'Ei',
    noResults: 'Ei hakutuloksia',
    open: 'Avaa',
    remove: 'Poista',
    saving: 'Tallennetaan',
    saved: 'Tallennettu',
    search: 'Hae',
    yes: 'Kyllä',
    openExpandingInfo: 'Avaa lisätietokenttä'
  },
  datePicker: {
    placeholder: 'pp.kk.vvvv',
    description:
      'Kirjoita päivämäärä kenttään muodossa pp.kk.vvvv. Tab-näppäimellä pääset kuukausivalitsimeen.',
    validationErrors: {
      validDate: 'Anna muodossa pp.kk.vvvv',
      dateTooEarly: 'Valitse myöhäisempi päivä',
      dateTooLate: 'Valitse aikaisempi päivä'
    }
  },
  loginErrorModal: {
    header: 'Kirjautuminen epäonnistui',
    message:
      'Palveluun tunnistautuminen epäonnistui tai se keskeytettiin. Kirjautuaksesi sisään palaa takaisin ja yritä uudelleen.',
    returnMessage: 'Palaa takaisin'
  },
  messages: {
    staffAnnotation: 'Henkilökunta',
    message: 'Viesti',
    recipients: 'Vastaanottajat',
    send: 'Lähetä',
    sending: 'Lähetetään',
    types: {
      MESSAGE: 'Viesti',
      BULLETIN: 'Tiedote'
    },
    thread: {
      type: 'Tyyppi',
      urgent: 'Kiireellinen'
    }
  },
  messageReplyEditor: {
    messagePlaceholder: undefined,
    discard: 'Hylkää',
    messagePlaceholderSensitiveThread: undefined
  },
  sessionTimeout: {
    sessionExpiredTitle: 'Istuntosi on aikakatkaistu',
    sessionExpiredMessage: 'Ole hyvä ja kirjaudu sisään uudelleen.',
    goToLoginPage: 'Siirry kirjautumissivulle',
    cancel: 'Peruuta'
  },
  notifications: {
    close: 'Sulje'
  },
  offlineNotification: 'Ei verkkoyhteyttä',
  reloadNotification: {
    title: 'Uusi versio eVakasta saatavilla',
    buttonText: 'Lataa sivu uudelleen'
  },
  treeDropdown: {
    expand: (opt: string) => `Avaa vaihtoehdon ${opt} alaiset vaihtoehdot`,
    collapse: (opt: string) => `Sulje vaihtoehdon ${opt} alaiset vaihtoehdot`,
    expandDropdown: 'Avaa'
  },
  validationErrors: {
    required: 'Tämä kenttä on pakollinen',
    requiredSelection: 'Valinta puuttuu',
    format: 'Anna oikeassa muodossa',
    integerFormat: 'Anna kokonaisluku',
    ssn: 'Virheellinen henkilötunnus',
    phone: 'Virheellinen numero',
    email: 'Virheellinen sähköpostiosoite',
    preferredStartDate: 'Aloituspäivä ei ole sallittu',
    timeFormat: 'Tarkista',
    timeRequired: 'Pakollinen',
    dateTooEarly: 'Valitse myöhäisempi päivä',
    dateTooLate: 'Valitse aikaisempi päivä',
    unitNotSelected: 'Valitse vähintään yksi hakutoive',
    emailsDoNotMatch: 'Sähköpostiosoitteet eivät täsmää',
    httpUrl: 'Anna muodossa https://example.com',
    unselectableDate: 'Päivä ei ole sallittu',
    openAttendance: 'Avoin kirjaus',
    generic: 'Tarkista',
    selectedValuesMin: 'Valitse vähintään {min} arvoa',
    selectedValuesMax: 'Valitse enintään {max} arvoa',
    positiveNumber: 'Anna positiivinen luku',
    certify: 'Vakuuta antamasi tiedot oikeiksi',
    terms: 'Hyväksy venesatamasäännöt',
    validDate: 'Anna muodossa pp.kk.vvvv'
  }
}

export default components
