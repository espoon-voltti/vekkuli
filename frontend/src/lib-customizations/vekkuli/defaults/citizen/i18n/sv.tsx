// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import { Translations as ComponentTranslations } from 'lib-components/i18n'

import {
  BoatSpaceType,
  OwnershipStatus,
  ReservationValidity
} from 'citizen-frontend/shared/types'
import LocalDate from 'lib-common/date/local-date'
import { Translations } from 'lib-customizations/vekkuli/citizen'
import components from 'lib-customizations/vekkuli/defaults/components/i18n/sv'

const componentTranslations: ComponentTranslations = {
  ...components
}

const yes = 'Ja'
const no = 'Nej'

const sv: Translations = {
  common: {
    title: 'Bokning av båtplats i Esbo',
    cancel: 'Gå tillbaka',
    continue: 'Fortsätt',
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
        en: 'English'
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
    },
    showMore: 'Visa mer',
    showLess: 'Visa mindre'
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
    goToHomepage: 'Gå till hemsidan',
    goToMainContent: 'Hoppa till innehållet',
    selectLanguage: 'Välj språk'
  },
  components: componentTranslations,
  reservation: {
    searchPage: {
      missingFieldsInfoBox:
        'Ange först båttyp och mått för att se vilka platser som passar din båt.',
      filters: {
        boatSpaceType: 'Sök plats',
        harbor: 'Hamnen',
        amenities: 'Faciliteter',
        boatType: 'Båttyp',
        branchSpecific: {
          Slip: {
            width: 'Båtens bredd (m)',
            length: 'Båtens längd (m)'
          },
          Trailer: {
            width: 'Släpvagnens bredd (m)',
            length: 'Släpvagnens längd (m)'
          },
          Winter: {
            width: 'Förvaringsplatsens bredd (m)',
            length: 'Förvaringsplatsens längd (m)'
          },
          Storage: {
            width: 'Förvaringsplatsens bredd (m)',
            length: 'Förvaringsplatsens längd (m)'
          }
        }
      }
    },
    formPage: {
      title: {
        Slip: (name: string) => `Båtplatsreservation: ${name}`,
        Trailer: (name: string) => `Släpvagnsplatsreservation: ${name}`,
        Renew: (name: string) => `Förnyelse av plats: ${name}`,
        Winter: (name: string) => `Vinterplatsreservation: ${name}`,
        Storage: (name: string) => `Förvaringsplatsreservation: ${name}`
      }
    },
    steps: {
      chooseBoatSpace: 'Välj plats',
      fillInformation: 'Fyll i information',
      payment: 'Betalning',
      confirmation: 'Bekräftelse'
    },
    noRegistererNumber: 'Inget registreringsnummer',
    certify: 'Jag intygar att de uppgifter jag har lämnat är korrekta.',
    agreeToRules: '',
    prices: {
      totalPrice: (amount: string) => `Totalt: ${amount} €`,
      vatValue: (amount: string) => `Moms: ${amount} €`,
      netPrice: (amount: string) => `Pris före skatt: ${amount} €`
    },
    totalPrice: (totalPrice: string, vatValue: string) =>
      `${totalPrice} € (inkl. moms ${vatValue} €)`,
    validity: (
      endDate: LocalDate,
      validity: ReservationValidity,
      boatSpaceType: BoatSpaceType
    ) => {
      switch (validity) {
        case 'FixedTerm':
          return `Till ${endDate.format()}`
        case 'Indefinite':
          switch (boatSpaceType) {
            case 'Slip':
              return 'Tills vidare, förnyas årligen i januari'
            case 'Winter':
            case 'Storage':
              return 'Tills vidare, förnyas årligen i augusti'
            case 'Trailer':
              return 'Tills vidare, förnyas årligen i april'
          }
      }
    },
    paymentState: (paymentDate?: LocalDate) => {
      return paymentDate ? `Betald ${paymentDate.format()}` : '-'
    },
    errors: {
      startReservation: {
        title: 'Det går inte att göra en reservation',
        MAX_RESERVATIONS:
          'Du har redan det maximala antalet platser av denna typ.',
        NOT_POSSIBLE:
          'Bokningsperioden är inte öppen. Kontrollera bokningstiderna på startsidan.',
        SERVER_ERROR:
          'Antingen är du inte berättigad att boka platsen, eller så inträffade ett annat fel. Kontakta kundtjänst. Kundtjänstens kontaktuppgifter finns på startsidan.',
        MAX_PERSONAL_RESERVATIONS:
          'Du har redan det maximala antalet platser av denna typ. Om du agerar på uppdrag av en organisation kan du fortsätta att boka.'
      }
    },
    auth: {
      reservingBoatSpace: 'Bokar båtplats:',
      reservingRequiresAuth:
        'För att boka en båtplats krävs stark autentisering.',
      continue: 'Fortsätt'
    },
    cancelConfirmation:
      'Du är på väg att lämna bokningsformuläret. Observera att platsbokningen eller inmatad information inte kommer att sparas.',
    cancelConfirmation2: 'Vill du fortsätta?',
    cancelReservation: 'Avbryt reservation',
    cancelAndGoBack: 'Avbryt och gå tillbaka',
    continueToPaymentButton: 'Fortsätt till betalning'
  },
  boat: {
    delete: 'Ta bort båt',
    deleteFailed:
      'Ett fel uppstod vid borttagning av båten. Vänligen kontakta kundtjänst.',
    deleteSuccess: 'Båten har tagits bort',
    confirmDelete: (boatName: string) =>
      `Du håller på att ta bort informationen för båten ${boatName}`
  },
  boatSpace: {
    boatSpaceType: {
      Slip: {
        label: 'Bryggplats',
        info: 'Båt i vattnet vid en bryggplats eller boj.'
      },
      Trailer: {
        label: 'Trailerplats',
        info: 'Båt på en trailer, sjösättning från ramp.'
      },
      Winter: {
        label: 'Vinterplats',
        info: 'Båt förvarad på trailer eller bock.'
      },
      Storage: {
        label: 'Förvaringsplats i Ämmäsmäki (året runt)',
        info: 'Båt förvarad på trailer eller bock.'
      }
    },

    boatType: {
      OutboardMotor: 'Utbordsmotorbåt',
      Sailboat: 'Segelbåt',
      InboardMotor: 'Inombordsmotorbåt',
      Rowboat: 'Rodd båt',
      JetSki: 'Vattenskoter',
      Other: 'Annan'
    },
    renterType: {
      Citizen: 'Jag bokar som privatperson',
      Organization: 'Jag bokar för en organisation'
    },

    ownershipStatus: {
      Owner: 'Jag äger båten',
      User: 'Jag är användare av båten',
      CoOwner: 'Jag äger båten tillsammans med andra',
      FutureOwner: 'Jag ska köpa båten'
    },

    ownershipStatusInfo: (type: OwnershipStatus, spaceType: BoatSpaceType) => {
      if (type === 'CoOwner') {
        switch (spaceType) {
          case 'Slip':
            return 'Minst 50% av båtägarna måste vara bosatta i Esbo för att platsen ska kunna förnyas årligen. Annars är platsen tidsbegränsad.'

          case 'Winter':
            return 'Minst 50% av båtägarna måste vara bosatta i Esbo för att ni ska kunna boka en vinterplats. Förvaringsplatser i Ämmäsmäki kan bokas av alla oavsett hemkommun.'
        }
      }
      return undefined
    },

    amenities: {
      Buoy: 'Boj',
      RearBuoy: 'Akterboj',
      Beam: 'Bom',
      WalkBeam: 'Gångbom',
      Trailer: 'Trailerförvaring',
      Buck: 'Förvaring på bock'
    },
    winterStorageType: {
      Trailer: 'Trailerförvaring',
      Buck: 'Förvaring på bock',
      BuckWithTent: 'Förvaring på bock med skyddstält'
    }
  }
}

export default sv
