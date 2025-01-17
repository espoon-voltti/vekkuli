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
import components from 'lib-customizations/vekkuli/defaults/components/i18n/en'

const yes = 'Yes'
const no = 'No'

const componentTranslations: ComponentTranslations = {
  ...components
}

const en: Translations = {
  common: {
    title: "Espoo's boat space reservation",
    cancel: 'Cancel',
    continue: 'Continue',
    return: 'Return',
    download: 'Download',
    print: 'Print',
    ok: 'Ok',
    save: 'Save',
    discard: 'Discard',
    saveConfirmation: 'Do you want to save changes?',
    saveSuccess: 'Saved',
    confirm: 'Confirm',
    delete: 'Remove',
    edit: 'Edit',
    add: 'Add',
    show: 'Show',
    hide: 'Hide',
    yes,
    no,
    select: 'Select',
    page: 'Page',
    unit: {
      languages: {
        fi: 'Finnish',
        sv: 'Swedish',
        en: 'English'
      },
      languagesShort: {
        fi: 'Suomi',
        sv: 'Svenska',
        en: 'English'
      }
    },
    openExpandingInfo: 'Open the details',
    errors: {
      genericGetError: 'Error in fetching the requested information'
    },
    datetime: {
      dayShort: 'd',
      weekdaysShort: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
      week: 'Week',
      weekShort: 'Wk',
      weekdays: [
        'Monday',
        'Tuesday',
        'Wednesday',
        'Thursday',
        'Friday',
        'Saturday',
        'Sunday'
      ],
      months: [
        'January',
        'February',
        'March',
        'April',
        'May',
        'June',
        'July',
        'August',
        'September',
        'October',
        'November',
        'December'
      ]
    },
    closeModal: 'Close popup',
    close: 'Close',
    tense: {
      past: 'Past',
      present: 'Active',
      future: 'Future'
    },
    showMore: 'Show more',
    showLess: 'Show less'
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
    login: 'Log in',
    logout: 'Log out',
    openMenu: 'Open menu',
    closeMenu: 'Close menu',
    goToHomepage: 'Go to homepage',
    goToMainContent: 'Skip to main content',
    selectLanguage: 'Select language'
  },
  components: componentTranslations,
  reservation: {
    steps: {
      chooseBoatSpace: 'Select a space',
      fillInformation: 'Fill in information',
      payment: 'Make payment',
      confirmation: 'Confirmation'
    },
    searchPage: {
      missingFieldsInfoBox:
        'First provide the boat type and dimensions to see suitable spaces for your boat.',
      filters: {
        boatSpaceType: 'Space type',
        harbor: 'Harbor',
        amenities: 'Amenities',
        boatType: 'Boat type',
        branchSpecific: {
          Slip: {
            width: 'Boat width (m)',
            length: 'Boat length (m)'
          },
          Trailer: {
            width: 'Trailer width (m)',
            length: 'Trailer length (m)'
          },
          Winter: {
            width: 'Storage space width (m)',
            length: 'Storage space length (m)'
          },
          Storage: {
            width: 'Storage space width (m)',
            length: 'Storage space length (m)'
          }
        }
      }
    },
    formPage: {
      title: {
        Slip: (name: string) => `Space reservation: ${name}`,
        Trailer: (name: string) => `Trailer space reservation: ${name}`,
        Renew: (name: string) => `Renew reservation: ${name}`,
        Winter: (name: string) => `Winter space reservation: ${name}`,
        Storage: (name: string) => `Storage space reservation: ${name}`
      }
    },
    noRegistererNumber: 'No registration number',
    certify: 'I certify that the information I have provided is correct',
    agreeToRules:
      'I have read and agree to follow the harbor rules. The reservation replaces the rental agreement mentioned in the harbor rules.',
    prices: {
      totalPrice: (amount: string) => `Total: ${amount} €`,
      vatValue: (amount: string) => `VAT: ${amount} €`,
      netPrice: (amount: string) => `Net price: ${amount} €`
    },
    totalPrice: (totalPrice: string, vatValue: string) =>
      `${totalPrice} € (incl. vat ${vatValue} €)`,
    validity: (
      endDate: LocalDate,
      validity: ReservationValidity,
      boatSpaceType: BoatSpaceType
    ) => {
      switch (validity) {
        case 'FixedTerm':
          return `until ${endDate.format()}`
        case 'Indefinite':
          switch (boatSpaceType) {
            case 'Slip':
              return 'For now, resume annually in January'
            case 'Winter':
            case 'Storage':
              return 'For now, resume annually in August'
            case 'Trailer':
              return 'For now, resume annually in April'
          }
      }
    },
    paymentState: (paymentDate?: LocalDate) => {
      return paymentDate ? `Paid ${paymentDate.format()}` : '-'
    },
    errors: {
      startReservation: {
        title: 'Reservation is not possible',
        MAX_RESERVATIONS:
          'You already have the maximum number of spaces of this type.',
        NOT_POSSIBLE:
          'The reservation period is not open. Check the search times on the homepage.',
        SERVER_ERROR:
          'Either you are not eligible to reserve a space, or another error occurred. Contact customer service. You can find customer service contact information on the homepage.',
        MAX_PERSONAL_RESERVATIONS:
          'You already have the maximum number of spaces of this type. If you are acting on behalf of a community, you can continue reserving.'
      }
    },
    auth: {
      reservingBoatSpace: 'Reserving boat space:',
      reservingRequiresAuth:
        'Reserving a boat space requires strong authentication.',
      continue: 'Continue'
    },
    cancelConfirmation:
      'You are about to leave the reservation form. Please note that the space reservation or entered information will not be saved.',
    cancelConfirmation2: 'Do you want to continue?',
    cancelReservation: 'Cancel reservation',
    cancelAndGoBack: 'Cancel and go back',
    continueToPaymentButton: 'Continue to payment'
  },
  boatSpace: {
    renterType: {
      Citizen: 'I am reserving as an individual',
      Organization: 'I am reserving on behalf of an organization'
    },
    boatSpaceType: {
      Slip: {
        label: 'Dock space',
        info: 'Boat on water in a dock space or with a buoy.'
      },
      Trailer: {
        label: 'Trailer space',
        info: 'Boat on a trailer, launched from a ramp.'
      },
      Winter: {
        label: 'Winter space',
        info: 'Boat stored on a trailer or stand.'
      },
      Storage: {
        label: 'Storage space in Ämmäsmäki (year-round)',
        info: 'Boat stored on a trailer or stand.'
      }
    },
    boatType: {
      OutboardMotor: 'Outboard motorboat',
      Sailboat: 'Sailboat',
      InboardMotor: 'Inboard motorboat',
      Rowboat: 'Rowboat',
      JetSki: 'Jet ski',
      Other: 'Other'
    },
    ownershipStatus: {
      Owner: 'I own the boat',
      User: 'I am the user of the boat',
      CoOwner: 'I co-own the boat',
      FutureOwner: 'I am buying the boat'
    },
    amenities: {
      Buoy: 'Buoy',
      RearBuoy: 'Rear buoy',
      Beam: 'Beam',
      WalkBeam: 'Walk beam',
      Trailer: 'Trailer storage',
      Buck: 'Stand storage'
    },
    ownershipStatusInfo: (type: OwnershipStatus, spaceType: BoatSpaceType) => {
      if (type === 'CoOwner') {
        switch (spaceType) {
          case 'Slip':
            return 'At least 50% of the boat owners must be residents of Espoo to renew the boat space annually. Otherwise, the space is fixed-term.'
          case 'Winter':
            return 'At least 50% of the boat owners must be residents of Espoo to reserve a winter space. Ämmäsmäki storage spaces can be reserved by anyone regardless of their home municipality.'
        }
      }
      return undefined
    },
    winterStorageType: {
      Trailer: 'Trailer storage',
      Buck: 'Stand storage',
      BuckWithTent: 'Stand storage with protective tent'
    }
  }
}

export default en
