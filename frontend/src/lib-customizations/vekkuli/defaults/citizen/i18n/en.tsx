// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import { Translations as ComponentTranslations } from 'lib-components/i18n'

import {
  BoatSpaceType,
  OwnershipStatus,
  ReservationStatus,
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
    saveChanges: 'Save changes',
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
      genericGetError: 'Error in fetching the requested information',
      validationWarning: 'Missing required information',
      error400: 'Oh no! You have run aground.',
      error403: 'Oh no! The boat route is blocked.',
      error404: 'Oh no! The route is lost.',
      error500:
        'Oh no! A storm caught the system off guard. Please try again later.'
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
    selectLanguage: 'Select language',
    mainNavigation: 'Main navigation',
    instructionsLink: 'Guide',
    openInANewWindow: 'Open in a new tab',
    mapsLink:
      'You can open the maps of harbors and storage locations in a new tab from here.',
    harborsInfoLink: 'https://www.espoo.fi/en/sports-and-nature/boating/marinas'
  },
  footer: {
    accessibilityLink: 'Accessibility statement',
    privacyLink:
      '<a target="_blank" aria-label="Privacy policy (link opens in a new tab)" href="https://www.espoo.fi/fi/kaupunki-ja-paatoksenteko/turvallisuus/tietosuoja/elinvoiman-tulosalueen-tietosuojaselosteet/tietosuojaseloste-henkilotietojen-kasittely-liikunnan-ja-urheilun-tulosyksikko">Privacy policy</a>',
    boatingLink:
      '<a target="_blank" aria-label="Espoo boating homepage (link opens in a new tab)" href="https://www.espoo.fi/en/sports-and-nature/boating">Espoo boating homepage</a>'
  },
  components: componentTranslations,
  citizenFrontPage: {
    title: 'Boat spaces',
    info: {
      locations:
        'Available berth spaces can be found at the following marinas: Haukilahti, Kivenlahti, Laajalahti, Otsolahti, Soukka, Suomenoja, and Svinö. Winter storage spaces are available at Laajalahti, Otsolahti, and Suomenoja, as well as year-round storage spaces at Ämmäsmäki.',
      authenticationRequired:
        'Reserving a space requires strong authentication, and the berth is paid for at the time of reservation. The reservation system works best with Google Chrome and Edge browsers.',
      boatRequired:
        'Only the owner or holder of a boat can reserve berth, winter, or storage spaces. Ensure that the information is correct in Traficom’s boat register.',
      contactInfo:
        'If you cannot authenticate electronically or are making a reservation on behalf of a community (company or club), please contact us by email at venepaikat@espoo.fi or info@espoo.fi or by calling Espoo Info at 09 81621, open on weekdays from 8:30 a.m. to 4:00 p.m.',
      preparations: `Booking is possible starting at 09:00 from the first day of the reservation period. For the booking, you need the following information: email address, phone number, boat's registration number, width, length, and weight, boat's name and brand or another identifier. If you are booking for the first time on behalf of a community, you also need the community's business ID and billing address.`,
      readMore:
        'You can find more information about marinas, berth fees, and boat storage <a target="_blank" aria-label="Espoo boating homepage (link opens in a new tab)" href="https://www.espoo.fi/en/sports-and-nature/boating">here</a>.'
    },
    periods: {
      Slip: {
        title: 'Berth Place 2027',
        season: (season: string) =>
          `The boating season is the period ${season} of each calendar year. The rental agreement is valid for one season at a time.`,
        periods: [
          (period: string) =>
            `${period} Espoo residents* with an existing permanent rental place can continue renting their berth place`,
          (period: string) =>
            `${period} Espoo residents* can reserve berth places`,
          (period: string) => `${period} everyone can reserve berth places`,
          (period: string) =>
            `${period} berth place changes (possible only after the place has been paid for). A berth can only be exchanged for another berth place`
        ]
      },
      Trailer: {
        title: 'Trailer Place 2027',
        season: (season: string) =>
          `The trailer season is ${season}. The rental agreement is valid for one season at a time.`,
        periods: [
          (period: string) =>
            `${period} Espoo residents* with an existing permanent rental place can continue renting their trailer place`,
          (period: string) => `${period} everyone can reserve trailer places`,
          (period: string) =>
            `${period} trailer place changes (possible only after the place has been paid for). A trailer place can only be exchanged for another trailer place`
        ]
      },
      Winter: {
        title: 'Winter Place 2027',
        season: (season: string) =>
          `The winter storage season is ${season} of each calendar year. The rental agreement is valid for one season at a time.`,
        periods: [
          (period: string) =>
            `${period} Espoo residents* with an existing permanent rental place can continue renting their winter place`,
          (period: string) =>
            `${period} only Espoo residents* can reserve winter places`,
          (period: string) =>
            `${period} winter place changes (possible only after the place has been paid for). A winter storage space may only be exchanged for another winter storage place`
        ]
      },
      Storage: {
        title: 'Ämmäsmäki 2027',
        season: (season: string) =>
          `The storage seasons are ${season}. The rental agreement is valid for one storage season at a time. 15.9.2026–31.7.2027 Everyone can reserve storage place 15.9.2026–14.9.2027.`,
        periods: [
          (period: string) =>
            `${period} storage place renters can continue renting their storage place for the next season 15.9.2027–14.9.2028`,
          (period: string) =>
            `${period} everyone can reserve a place for the season 15.9.2027–14.9.2028**`,
          (period: string) =>
            `${period} storage place changes (available only after payment of the storage space). A storage place may only be exchanged for another storage space.`
        ]
      },
      footNote:
        '*You may reserve a place for a watercraft at the resident rate if at least half of the watercraft’s owners and/or operators reside in Espoo. The reservation must be made in the name of an owner or operator who resides in Espoo.',
      footNote2:
        '**The Ämmäsmäki storage area will be exceptionally closed from 1 May to 1 July 2028. All property must be removed from the area by 30 April 2028 at the latest. During the closure, no property may be stored in the area and entry to the area is not permitted.'
    },
    button: {
      browseBoatSpaces: 'Browse boat spaces'
    },
    image: {
      harbors: {
        altText: 'Marinas on the map'
      }
    }
  },
  reservation: {
    steps: {
      chooseBoatSpace: 'Select a space',
      fillInformation: 'Fill in information',
      payment: 'Make payment',
      confirmation: 'Confirmation',
      error: 'Error'
    },
    searchPage: {
      title: 'Espoo City boat place rental',
      image: {
        harbors: {
          altText: "Espoo's marinas"
        }
      },
      missingFieldsInfoBox:
        'First provide the boat type and dimensions to see suitable spaces for your boat.',
      freeSpaceCount: 'Number of spaces available according to search criteria',
      size: 'Size',
      amenityLabel: 'Amenity',
      price: 'Price/Season',
      space: 'space',
      filters: {
        title: 'Boat space reservation',
        boatSpaceType: 'Space type',
        harbor: 'Harbor',
        amenities: 'Amenities',
        harborHeader: 'Harbor',
        amenityHeader: 'Amenity',
        boatType: 'Boat type',
        storageTypeAmenities: 'Storage type',
        additionalHarborSpaceInfo:
          'More information about the spaces and their prices here',
        additionalHarborSpaceInfoLink:
          'https://www.espoo.fi/fi/liikunta-ja-luonto/veneily/venepaikkojen-koot-ja-hinnasto',
        branchSpecific: {
          Slip: {
            width: 'Boat width (m)',
            length: 'Boat length (m)',
            harborInfo: ''
          },
          Trailer: {
            width: 'Trailer width (m)',
            length: 'Trailer length (m)',
            harborInfo: 'You can only reserve a trailer space from Suomenoja.'
          },
          Winter: {
            width: 'Winter space width (m)',
            length: 'Winter space length (m)',
            harborInfo:
              'Select a winter storage space from your boat harbor or from Suomenoja.'
          },
          Storage: {
            width: 'Storage space width (m)',
            length: 'Storage space length (m)',
            harborInfo:
              'You can only reserve year-round storage from the Ämmäsmäki storage area.'
          }
        },
        storageInfo:
          'All equipment required for boat storage must fit entirely within the reserved space. Please also note that boats placed in unnecessarily large spaces may be moved to smaller spaces.'
      },
      switchInfoText:
        'You are changing your boat space. The space can only be changed to another space of the same type.',
      infoText: {
        title: 'Reserve Boat Places 2027',
        periods: {
          newReservations:
            'Berth places: Espoo residents 1.3.–31.10.2027 and for others 1.4.–31.10.2027. The resevation is for season 10.6.–14.9.2027.',
          trailerReservations:
            'Trailer places: 1.5.–15.12. The reservation is for season 1.5.2027–30.4.2028.',
          winter:
            'Winter places:  Espoo residents 15.9.–15.12.2027. The reservation is for season 15.9.2027–10.6.2028.',
          storage1:
            'Ämmäsmäki storage places: reservation for season 15.9.2026–14.9.2027 is open until 31.7.2027.',
          storage2:
            'Ämmäsmäki storage places: reservation for season 15.9.2027–14.9.2028* opens on 15.9.2027 and is open until 31.7.2028.',
          storage3:
            '*The Ämmäsmäki storage area will be exceptionally closed from 1 May to 1 July 2028. All property must be removed from the area by 30 April 2028 at the latest. During the closure, no property may be stored in the area and entry to the area is not permitted.'
        }
      },
      modal: {
        reserveNewSpace: 'Reserve a new space',
        reservingBoatSpace: 'You are reserving a boat space:',
        reservingBoatSpaceWarning:
          'Note! If the renewal period is ongoing, please pay for the old place before making the change.',
        cannotReserveNewPlace:
          'You already have two boat spaces. You cannot reserve a new space, but you can switch your current space.',
        currentPlaces: 'Your current spaces:',
        switchCurrentPlace: 'Switch this space',
        organizationCurrentPlaces: (organizationName: string) =>
          `Your organization's ${organizationName} spaces:`
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
          'You are switching your boat space. The reservation period for your boat space remains unchanged. At the same time, your old space will be canceled and made available for others to reserve.'
      },
      submit: {
        continueToPayment: 'Continue to payment',
        confirmReservation: 'Confirm reservation'
      },
      storageInfo: {
        title: 'Storage information',
        buckWithTentInfo:
          'Please note that the boat and the cradle, including their protective tents, must fit within the reserved space.'
      },
      trailerInfo: {
        title: 'Trailer information',
        registrationNumber: 'Registration number',
        editTrailerDetails: 'Edit trailer details'
      },
      allYearStorage: {
        Trailer: {
          title: 'Trailer information'
        },
        Buck: {
          title: 'Buck information'
        }
      },
      reserver: 'Reserver',
      tenant: 'Renter',
      boatInformation: 'Boat information',
      boatSpaceInformation: 'Boat space to be reserved',
      harbor: 'Harbor',
      space: 'Space',
      boatSpaceType: 'Boat space type',
      boatSpaceDimensions: 'Boat space dimensions',
      boatSpaceAmenity: 'Amenity',
      reservationValidity: 'Reservation Validity:',
      price: 'Price',
      storageType: 'Storage method'
    },
    paymentPage: {
      paymentCancelled:
        'Payment failed, please try again or go back to cancel the reservation.'
    },
    confirmationPage: {
      header: 'Reservation successful',
      emailInfo:
        'You will also receive a confirmation message at the email address you provided.',
      indefiniteInfo:
        'Your reservation is valid indefinitely, and you can extend it to the next season annually during the renewal period.',
      fixedInfo:
        'Your reservation is for a fixed term and valid for one season.'
    },
    noRegistererNumber: 'No registration number',
    certify: 'I certify that the information I have provided is correct',
    agreeToRules:
      'I have read the <a target="_blank" href="https://www.espoo.fi/en/marina-rules">marina terms and rules</a> and agree to comply with them.',
    prices: {
      totalPrice: (amount: string) => `Total: ${amount} €`,
      vatValue: (amount: string) => `VAT: ${amount} €`,
      netPrice: (amount: string) => `Net price: ${amount} €`
    },
    totalPrice: (totalPrice: string, vatValue: string) =>
      `${totalPrice} € (incl. vat ${vatValue} €)`,
    paymentInfo: {
      moreExpensive: (amount: string) =>
        `Note that the new space is more expensive than your current space. The price already accounts for the payment you have made, and you only need to pay the difference of ${amount} €.`,
      lessExpensive:
        'Note that the new space is cheaper than your current space. No refund will be issued.',
      equal:
        'The space costs the same as your previous one. You do not need to pay again.'
    },
    validity: (
      endDate: LocalDate,
      validity: ReservationValidity,
      status: ReservationStatus,
      isActive: boolean
    ) => {
      if (status !== 'Cancelled' && validity === 'Indefinite' && isActive) {
        return 'For now, resume annually'
      }
      return `until ${endDate.format()}`
    },
    terminatedAt: (terminationDate: LocalDate): string => {
      return `Terminated ${terminationDate.format()}`
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
    invoiceState: (dueDate?: LocalDate) => {
      return dueDate ? `Invoiced, due date ${dueDate.format()}` : '-'
    },
    errors: {
      startReservation: {
        title: 'Reservation is not possible',
        MAX_RESERVATIONS:
          'You already have the maximum number of spaces of this type.',
        NOT_POSSIBLE:
          'The reservation period is not open, or you do not have permission to reserve the space. Check the search times on the homepage.',
        SERVER_ERROR:
          'Either you are not eligible to reserve a space, or another error occurred. Contact customer service. You can find customer service contact information on the homepage.',
        MAX_PERSONAL_RESERVATIONS:
          'You already have the maximum number of spaces of this type. If you are acting on behalf of a community, you can continue reserving.',
        NOT_AVAILABLE:
          'The boat space you tried to reserve is no longer available. Please select another available space.'
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
      },
      failedReservation: {
        title: 'Reserving failed',
        type: {
          boatSpaceNotAvailable: [
            'Unfortunately, you did not confirm the reservation in time, and the space has already been reserved by someone else.',
            'Your payment may have been processed, so please check the situation by contacting our customer service for further instructions and a possible refund.',
            'You can find the customer service contact details on the homepage.'
          ],
          unknown: [
            'Unknown error, the space could not be reserved.',
            'Your payment may have been processed, so please check the situation by contacting our customer service for further instructions and a possible refund.',
            'You can find the customer service contact details on the homepage.'
          ]
        }
      }
    },
    auth: {
      reservingBoatSpace: 'You are reserving a space:',
      reservingRequiresAuth:
        'Reserving a boat space requires strong authentication.',
      continue: 'Continue to authentication'
    },
    cancelConfirmation:
      'You are leaving the reservation form. The filled-in information or reservation will not be saved. Are you sure you want to cancel the reservation?',
    noAndGoBack: 'No, go back',
    yesCancelReservation: 'Yes, cancel the reservation',
    cancelReservation: 'Cancel reservation',
    cancelAndGoBack: 'Cancel reservation and go back',
    goBack: 'Go back',
    continueToPaymentButton: 'Continue to payment',
    timer: {
      title:
        'You have ${minutes} and ${seconds} to confirm your boat space reservation by paying.',
      announceTitle:
        'You have ${time} to confirm your boat space reservation by paying.',
      minute: 'minute',
      minutes: 'minutes',
      second: 'second',
      seconds: 'seconds'
    }
  },
  boat: {
    newBoat: 'New boat',
    delete: 'Delete boat',
    deleteFailed:
      'An error occurred while deleting the boat. Please contact customer support.',
    deleteSuccess: 'The boat has been deleted',
    confirmDelete: (boatName: string) =>
      `You are about to delete the information for the boat ${boatName}`,
    editBoatDetails: 'Edit Boat Details',
    boatName: 'Boat name',
    boatDepthInMeters: 'Draft (m)',
    boatWeightInKg: 'Weight (kg)',
    registrationNumber: 'Registration Number',
    otherIdentifier: 'Brand and model/Other identifier',
    additionalInfo: 'Additional Information',
    title: 'Boats',
    boatType: 'Boat Type',
    ownership: 'Ownership',
    boatSizeWarning: `Boat doesn't fit in the selected boat space.`,
    boatSizeWarningExplanation:
      'Boat spaces have safety spaces to prevent damage to boats and docks. A boat placed in a space that is too cramped can be moved by the city, and the owner of the boat space is responsible for the costs.',
    boatWeightWarning: `The maximum allowed weight for a boat moored in the City of Espoo's harbors is 15,000 kg.`,
    boatWeightWarning2:
      'A boat that is too heavy may be relocated by the city, and the berth holder is responsible for the costs.'
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
    ownershipTitle: 'Ownership of the boat according to Traficom',
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
      Buck: 'Stand storage',
      None: '-'
    },
    harbors: {
      1: 'Haukilahti',
      2: 'Kivenlahti',
      3: 'Laajalahti',
      4: 'Otsolahti',
      5: 'Soukka',
      6: 'Suomenoja',
      7: 'Svinö',
      8: 'Ämmäsmäki'
    },
    ownershipStatusInfo: (type: OwnershipStatus, spaceType: BoatSpaceType) => {
      if (type === 'CoOwner') {
        switch (spaceType) {
          case 'Slip':
            return 'At least 50% of the boat owners and/or operators of the boat must be residents of Espoo to renew the boat space annually. Otherwise, the space is fixed-term.'
          case 'Winter':
            return 'At least 50% of the boat owners and/or operators must be residents of Espoo to reserve a winter space. Ämmäsmäki storage spaces can be reserved by anyone regardless of their home municipality.'
        }
      }
      return undefined
    },
    winterStorageType: {
      Trailer: 'Trailer storage',
      Buck: 'Stand storage',
      BuckWithTent: 'Stand storage with protective tent'
    },
    reserve: 'Reserve',
    heightWarning: {
      bridge: (height: string) =>
        `The clearance height of the bridge leading to the harbor is ${height} m`,
      bridges: (height: string) =>
        `The clearance height of the bridges leading to the harbor is ${height} m`
    }
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
    municipality: 'Municipality',
    birthday: 'Birthday',
    streetAddress: 'Address',
    homeAddress: 'Home Address'
  },
  citizenPage: {
    title: 'My information',
    reservation: {
      title: 'Reservations',
      noReservations: 'No reservations',
      actions: {
        terminate: 'Cancel space',
        renew: 'Renew space',
        change: 'Change space'
      },
      modal: {
        goBackToReservation: 'Go to the reservation',
        termination: {
          title: 'You are terminating the reservation:',
          spaceInformation:
            'Please note that the place must be vacated immediately after you have terminated the place.',
          notEntitledToRefund:
            'The City of Espoo does not grant a refund for a paid berth.',
          confirm: 'Terminate',
          terminationFailed:
            'An error occurred while terminating the boat space reservation. Please contact customer service.',
          success: 'Reservation terminated successfully'
        }
      },
      showAllBoats: 'Also show boats that are not linked to a reservation',
      renewNotification: (date: LocalDate) =>
        `The contract period is ending. Secure the same space for next season by paying the season fee by ${date.format()}.`,
      harbor: 'Harbor',
      reservationDate: 'Reservation start date',
      space: 'Space',
      reservationValidity: 'Reservation validity',
      spaceType: 'Type of space',
      price: 'Price',
      boatPresent: 'Boat Present',
      equipment: 'Equipment',
      paymentStatus: 'Payment status',
      storageType: 'Storage type'
    },
    spaceReservations: 'Space reservations',
    expired: 'Expired',
    expiredReservations: 'Expired reservations'
  },
  organization: {
    information: {
      title: 'Organization information',
      phone: 'Organization phone number',
      email: 'Organization email',
      name: 'Organization name'
    },
    title: 'Organizations',
    name: 'Name',
    organizationId: 'Business ID',
    municipality: 'Municipality',
    physicalAddress: 'Physical address',
    streetAddress: 'Address',
    postalCode: 'Postal code',
    postOffice: 'City',
    contactDetails: {
      title: 'Contact information',
      fields: {
        name: 'Name',
        phone: 'Phone',
        email: 'Email'
      }
    }
  },
  payment: {
    title: 'Choose payment method'
  }
}

export default en
