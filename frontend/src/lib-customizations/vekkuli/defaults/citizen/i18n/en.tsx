// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import { Translations as ComponentTranslations } from 'lib-components/i18n'

import {
  BoatSpaceType,
  OwnershipStatus,
  ReservationValidity,
  ReserverType
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
      },
      dimensions: {
        widthInMeters: 'Width (m)',
        lengthInMeters: 'Length (m)'
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
      },
      modal: {
        reserveNewSpace: 'Reserve a new space',
        reservingBoatSpace: 'You are reserving a boat space:',
        cannotReserveNewPlace:
          'You already have two boat spaces. You cannot reserve a new space, but you can switch your current space.',
        currentPlaces: 'Your current space',
        switchCurrentPlace: 'Switch my current space'
      }
    },
    formPage: {
      title: {
        Slip: (name: string) => `Space reservation: ${name}`,
        Trailer: (name: string) => `Trailer space reservation: ${name}`,
        Renew: (name: string) => `Renew reservation: ${name}`,
        Winter: (name: string) => `Winter space reservation: ${name}`,
        Storage: (name: string) => `Storage space reservation: ${name}`
      },
      info: {
        switch:
          'You are switching your boat place. The reservation period for your boat place remains unchanged. At the same time, your old place will be canceled and made available for others to reserve.'
      },
      submit: {
        continueToPayment: 'Continue to payment',
        confirmReservation: 'Confirm reservation'
      },
      trailerInfo: {
        title: 'Trailer information',
        registrationCode: 'Registration number'
      }
    },
    paymentPage: {
      paymentCancelled:
        'Payment failed, please try again or go back to cancel the reservation.'
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
    paymentInfo: {
      moreExpensive: (amount: string) =>
        `Note that the new place is more expensive than your current place. The price already accounts for the payment you have made, and you only need to pay the difference of ${amount} €.`,
      lessExpensive:
        'Note that the new place is cheaper than your current place. No refund will be issued.',
      equal:
        'The place costs the same as your previous one. You do not need to pay again.'
    },
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
    reserverDiscountInfo: (
      type: ReserverType,
      reserverName: string,
      discountPercentage: number,
      discountedPrice: string
    ) => {
      const name =
        type === 'Organization'
          ? `Organization ${reserverName} has`
          : `You have `
      return `${name} a discount of ${discountPercentage} %. After the discount, price of the boat space is ${discountedPrice} €`
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
      },
      fillInformation: {
        title: 'Reserving failed',
        SERVER_ERROR:
          'Either you are not eligible to reserve a space, or another error occurred. Contact customer service. You can find customer service contact information on the homepage.',
        UNFINISHED_RESERVATION:
          'You have an ongoing reservation. Please complete the reservation or cancel it to continue.'
      },
      cancelPayment: {
        title: 'Returning was unsuccessful',
        SERVER_ERROR:
          'Returning to fill in the information was unsuccessful, or another error occurred. Please contact customer service. You can find customer service contact details on the front page.'
      }
    },
    auth: {
      reservingBoatSpace: 'You are reserving a spot:',
      reservingRequiresAuth:
        'Reserving a boat space requires strong authentication.',
      continue: 'Continue to authentication'
    },
    cancelConfirmation:
      'You are leaving the reservation form. Note that the reservation or entered information will not be saved.',
    cancelConfirmation2: 'Do you want to continue?',
    cancelReservation: 'Cancel reservation',
    cancelAndGoBack: 'Cancel reservation and go back',
    goBack: 'Go back',
    continueToPaymentButton: 'Continue to payment'
  },
  boat: {
    delete: 'Delete boat',
    deleteFailed:
      'An error occurred while deleting the boat. Please contact customer support.',
    deleteSuccess: 'The boat has been deleted',
    confirmDelete: (boatName: string) =>
      `You are about to delete the information for the boat ${boatName}`
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
    },
    reserve: 'Reserve'
  },
  citizen: {
    firstName: 'First Name',
    lastName: 'Last Name',
    email: 'Email',
    phoneNumber: 'Phone Number',
    address: 'Address',
    nationalId: 'National ID',
    postalCode: 'Postal Code',
    postOffice: 'Post Office',
    municipality: 'Municipality'
  },
  citizenPage: {
    title: 'My Information',
    reservation: {
      title: 'Reservations',
      noReservations: 'No Reservations',
      actions: {
        terminate: 'Cancel Place',
        renew: 'Renew Place',
        change: 'Change Place'
      },
      modal: {
        goBackToReservation: 'Go to the reservation'
      }
    }
  },
  payment: {
    title: 'Choose payment method'
  }
}

export default en
