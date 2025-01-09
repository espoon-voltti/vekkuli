// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import { Translations as ComponentTranslations } from 'lib-components/i18n'

import { Translations } from 'lib-customizations/vekkuli/citizen'

import components from '../../components/i18n/sv'

const componentTranslations: ComponentTranslations = {
  ...components
}

const yes = 'Ja'
const no = 'Nej'

const sv: Translations = {
  common: {
    title: 'Bokning av båtplats i Esbo',
    cancel: 'Gå tillbaka',
    return: 'Tillbaka',
    download: 'Ladda ner',
    print: 'Skriva ut',
    ok: 'Ok',
    save: 'Spara',
    discard: 'Spar inte',
    saveConfirmation: 'Vill du spara ändringar?',
    saveSuccess: 'Sparat',
    confirm: 'Bekräfta',
    delete: 'Ta bort',
    edit: 'Redigera',
    add: 'Lägg till',
    show: 'Visa',
    hide: 'Gömma',
    yes,
    no,
    select: 'Utvalda',
    page: 'Sida',
    unit: {
      languages: {
        fi: 'finskspråkig',
        sv: 'svenskspråkig',
        en: 'engelskspråkig'
      },
      languagesShort: {
        fi: 'Suomi',
        sv: 'Svenska',
        en: 'Engelska'
      }
    },
    openExpandingInfo: 'Öppna detaljer',
    errors: {
      genericGetError: 'Hämtning av information misslyckades'
    },
    datetime: {
      dayShort: 'pv',
      weekdaysShort: ['Mån', 'Tis', 'Ons', 'Tor', 'Fre', 'Lör', 'Sön'],
      week: 'Vecka',
      weekShort: 'V',
      weekdays: [
        'Måndag',
        'Tisdag',
        'Onsdag',
        'Torsdag',
        'Fredag',
        'Lördag',
        'Söndag'
      ],
      months: [
        'Januari',
        'Februari',
        'Mars',
        'April',
        'Maj',
        'Juni',
        'Juli',
        'Augusti',
        'September',
        'Oktober',
        'November',
        'December'
      ]
    },
    closeModal: 'Stäng popup',
    close: 'Stäng',
    tense: {
      past: 'Päättynyt (sv)',
      present: 'Voimassa (sv)',
      future: 'Tuleva (sv)'
    }
  },
  header: {
    lang: {
      fi: 'Suomeksi',
      sv: 'På svenska',
      en: 'In English'
    },
    langMobile: {
      fi: 'Suomeksi',
      sv: 'Svenska',
      en: 'English'
    },
    login: 'Logga in',
    logout: 'Logga ut',
    openMenu: 'Öppna menyn',
    closeMenu: 'Stäng menyn',
    goToHomepage: 'Gå till hemsidan'
  },
  components: componentTranslations,
  reservation: {
    searchPage: {
      missingFieldsInfoBox:
        'Ange först båttyp och mått för att se vilka platser som passar din båt.'
    },
    formPage: {
      title: {
        Slip: (name: string) => `Båtplatsreservation: ${name}`,
        Trailer: (name: string) => `Släpvagnsplatsreservation: ${name}`,
        Renew: (name: string) => `Förnyelse av plats: ${name}`,
        Winter: (name: string) => `Vinterplatsreservation: ${name}`
      }
    }
  },
  boatSpace: {
    boatSpaceType: {
      Slip: 'Bryggplats',
      Trailer: 'Trailerplats',
      Winter: 'Vinterförvaring'
    },
    boatType: {
      OutboardMotor: 'Utbordsmotorbåt',
      Sailboat: 'Segelbåt',
      InboardMotor: 'Inombordsmotorbåt',
      Rowboat: 'Rodd båt',
      JetSki: 'Vattenskoter',
      Other: 'Annan'
    }
  }
}

export default sv
