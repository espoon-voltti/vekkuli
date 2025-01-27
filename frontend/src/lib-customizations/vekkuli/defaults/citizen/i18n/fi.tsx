// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import type { Translations as ComponentTranslations } from 'lib-components/i18n'

import {
  BoatSpaceType,
  OwnershipStatus,
  ReservationValidity,
  ReserverType
} from 'citizen-frontend/shared/types'
import LocalDate from 'lib-common/date/local-date'
import components from 'lib-customizations/vekkuli/defaults/components/i18n/fi'

const componentTranslations: ComponentTranslations = {
  ...components
}

const yes = 'Kyllä'
const no = 'Ei'

export default {
  common: {
    title: 'Espoon venepaikkavaraus',
    cancel: 'Peruuta',
    continue: 'Jatka',
    return: 'Palaa',
    download: 'Lataa',
    print: 'Tulosta',
    ok: 'Ok',
    save: 'Tallenna',
    discard: 'Älä tallenna',
    saveConfirmation: 'Haluatko tallentaa muutokset?',
    saveSuccess: 'Tallennettu',
    confirm: 'Vahvista',
    delete: 'Poista',
    edit: 'Muokkaa',
    add: 'Lisää',
    show: 'Näytä',
    hide: 'Piilota',
    yes,
    no,
    select: 'Valitse',
    page: 'Sivu',
    unit: {
      languages: {
        fi: 'suomenkielinen',
        sv: 'ruotsinkielinen',
        en: 'englanninkielinen'
      },
      languagesShort: {
        fi: 'Suomi',
        sv: 'Svenska',
        en: 'English'
      },
      dimensions: {
        widthInMeters: 'Leveys (m)',
        lengthInMeters: 'Pituus (m)'
      }
    },
    openExpandingInfo: 'Avaa lisätietokenttä',
    errors: {
      genericGetError: 'Tietojen hakeminen ei onnistunut'
    },
    datetime: {
      dayShort: 'pv',
      weekdaysShort: ['Ma', 'Ti', 'Ke', 'To', 'Pe', 'La', 'Su'],
      week: 'Viikko',
      weekShort: 'Vk',
      weekdays: [
        'Maanantai',
        'Tiistai',
        'Keskiviikko',
        'Torstai',
        'Perjantai',
        'Lauantai',
        'Sunnuntai'
      ],
      months: [
        'Tammikuu',
        'Helmikuu',
        'Maaliskuu',
        'Huhtikuu',
        'Toukokuu',
        'Kesäkuu',
        'Heinäkuu',
        'Elokuu',
        'Syyskuu',
        'Lokakuu',
        'Marraskuu',
        'Joulukuu'
      ]
    },
    closeModal: 'Sulje ponnahdusikkuna',
    close: 'Sulje',
    tense: {
      past: 'Päättynyt',
      present: 'Voimassa',
      future: 'Tuleva'
    },
    showMore: 'Näytä lisää',
    showLess: 'Näytä vähemmän'
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
    login: 'Kirjaudu sisään',
    logout: 'Kirjaudu ulos',
    openMenu: 'Avaa valikko',
    closeMenu: 'Sulje valikko',
    goToHomepage: 'Siirry etusivulle',
    goToMainContent: 'Siirry pääsisältöön',
    selectLanguage: 'Valitse kieli'
  },
  components: componentTranslations,
  reservation: {
    steps: {
      chooseBoatSpace: 'Paikan valinta',
      fillInformation: 'Tietojen täyttäminen',
      payment: 'Maksaminen',
      confirmation: 'Vahvistus'
    },
    searchPage: {
      missingFieldsInfoBox:
        'Anna ensin paikan mitat niin näet veneellesi sopivat paikat.',
      filters: {
        title: 'Venepaikan haku',
        boatSpaceType: 'Haettava paikka',
        harbor: 'Satama',
        amenities: 'Varusteet',
        boatType: 'Venetyyppi',
        storageTypeAmenities: 'Säilytystapa',
        branchSpecific: {
          Slip: {
            width: 'Veneen leveys (m)',
            length: 'Veneen pituus (m)'
          },
          Trailer: {
            width: 'Trailerin leveys (m)',
            length: 'Trailerin pituus (m)'
          },
          Winter: {
            width: 'Säilytyspaikan leveys (m)',
            length: 'Säilytyspaikan pituus (m)'
          },
          Storage: {
            width: 'Säilytyspaikan leveys (m)',
            length: 'Säilytyspaikan pituus (m)'
          }
        }
      },
      modal: {
        reserveAnotherPlace: 'Varaan toisen paikan',
        reservingBoatSpace: 'Olet varaamassa venepaikkaa:',
        cannotReserveNewPlace:
          'Sinulla on jo kaksi venepaikkaa. Et voi varata uutta paikkaa, mutta voit vaihtaa nykyisen paikkasi.',
        currentPlaces: 'Paikkasi:',
        switchCurrentPlace: 'Vaihdan nykyisen paikan'
      }
    },
    formPage: {
      title: {
        Slip: (name: string) => `Venepaikan varaus: ${name}`,
        Trailer: (name: string) => `Traileripaikan varaus: ${name}`,
        Renew: (name: string) => `Paikan uusinta: ${name}`,
        Winter: (name: string) => `Talvipaikan varaus: ${name}`,
        Storage: (name: string) => `Säilytyspaikan varaus: ${name}`
      },
      info: {
        switch:
          'Olet vaihtamassa venepaikkaa. Venepaikkasi varausaika säilyy ennallaan. Samalla vanha paikkasi irtisanoutuu ja vapautuu muiden varattavaksi.'
      },
      submit: {
        continueToPayment: 'Jatka maksamaan',
        confirmReservation: 'Vahvista varaus'
      },
      storageTypeInfo: {
        title: 'Säilytyspaikan tiedot',
        buckWithTentInfo:
          'Huomioi, että veneen ja pukkin suojatelttoineen tulee mahtua varatun paikan sisään.'
      },
      trailerInfo: {
        title: 'Trailerin tiedot',
        registrationCode: 'Rekisteritunnus'
      }
    },
    paymentPage: {
      paymentCancelled:
        'Maksu epäonnistui, yritä uudelleen tai palaa takaisin peruuttaaksesi varauksen.'
    },

    noRegistererNumber: 'Ei rekisterinumeroa',
    certify: 'Vakuutan antamani tiedot oikeiksi',
    agreeToRules:
      'Olen lukenut venesatamasäännöt ja sitoudun noudattamaan niitä. Varaus korvaa venesatamasäännöissä mainitun vuokrasopimuksen.',
    prices: {
      totalPrice: (amount: string) => `Yhteensä: ${amount} €`,
      vatValue: (amount: string) => `Arvonlisävero: ${amount} €`,
      netPrice: (amount: string) => `Hinta ennen veroja: ${amount} €`
    },
    totalPrice: (totalPrice: string, vatValue: string) =>
      `${totalPrice} € (sis. alv ${vatValue} €)`,
    paymentInfo: {
      moreExpensive: (amount: string) =>
        `Huomaa, että uusi paikka on kalliimpi kuin nykyinen paikkasi. Hinnassa on huomioitu jo suorittamasi maksu ja sinun täytyy maksaa ainoastaan paikkojen hinnan välinen erotus ${amount} €.`,
      lessExpensive:
        'Huomaa, että uusi paikka on halvempi kuin nykyinen paikkasi. Hintaa ei palauteta.',
      equal:
        'Paikka maksaa saman verran kuin aikaisempi. Sinun ei tarvitse maksaa hintaa uudestaan.'
    },
    validity: (
      endDate: LocalDate,
      validity: ReservationValidity,
      boatSpaceType: BoatSpaceType
    ): string => {
      switch (validity) {
        case 'FixedTerm':
          return `${endDate.format()} asti`
        case 'Indefinite':
          switch (boatSpaceType) {
            case 'Slip':
              return 'Toistaiseksi, jatko vuosittain tammikuussa'
            case 'Winter':
            case 'Storage':
              return 'Toistaiseksi, jatko vuosittain elokuussa'
            case 'Trailer':
              return 'Toistaiseksi, jatko vuosittain huhtikuussa'
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
        type === 'Organization' ? `Yhteisölle ${reserverName}` : `Sinulle`
      return `${name} on myönnetty ${discountPercentage} % alennus. Alennuksen jälkeen paikan hinnaksi jää ${discountedPrice} €`
    },
    paymentState: (paymentDate?: LocalDate) => {
      return paymentDate ? `Maksettu ${paymentDate.format()}` : '-'
    },
    errors: {
      startReservation: {
        title: 'Varaaminen ei ole mahdollista',
        MAX_RESERVATIONS: 'Sinulla on jo maksimimäärä tämän tyypin paikkoja.',
        NOT_POSSIBLE: 'Varauskausi ei ole auki. Tarkista hakuajat etusivulta.',
        SERVER_ERROR:
          'Joko et ole oikeutettu varaamaan paikkaa, tai sattui muu virhe. Ota yhteyttä asiakaspalveluun. Asiakaspalvelun yhteystiedot löydät etusivulta.',
        MAX_PERSONAL_RESERVATIONS:
          'Sinulla on jo maksimimäärä tämän tyypin paikkoja. Jos asioit yhteisön puolesta, voit jatkaa varaamista. '
      },
      fillInformation: {
        title: 'Varaaminen ei onnistunut',
        SERVER_ERROR:
          'Joko et ole oikeutettu varaamaan paikkaa, tai sattui muu virhe. Ota yhteyttä asiakaspalveluun. Asiakaspalvelun yhteystiedot löydät etusivulta.',
        UNFINISHED_RESERVATION:
          'Sinulla on käynnissä oleva varaus. Ole hyvä ja täytä varaus loppuun tai peruuta se jatkaaksesi.'
      },
      cancelPayment: {
        title: 'Takaisin palaaminen ei onnistunut',
        SERVER_ERROR:
          'Palaaminen tietojen täyttöön ei onnistunut, tai sattui muu virhe. Ota yhteyttä asiakaspalveluun. Asiakaspalvelun yhteystiedot löydät etusivulta.'
      }
    },
    auth: {
      reservingBoatSpace: 'Olet varaamassa paikkaa:',
      reservingRequiresAuth:
        'Venepaikan varaaminen vaatii vahvan tunnistautumisen.',
      continue: 'Jatka tunnistautumiseen'
    },
    cancelConfirmation:
      'Olet poistumassa varauslomakkeelta. Huomioi, että paikkavarausta tai syötettyjä tietoja ei tallenneta.',
    cancelConfirmation2: 'Haluatko jatkaa?',
    cancelReservation: 'Peruuta varaus',
    cancelAndGoBack: 'Peruuta varaus ja palaa takaisin',
    goBack: 'Palaa takaisin',
    continueToPaymentButton: 'Jatka maksamaan'
  },
  boat: {
    delete: 'Poista vene',
    deleteFailed:
      'Veneen poistamisessa tapahtui virhe. Ota yhteyttä asiakaspalveluun.',
    deleteSuccess: 'Vene on poistettu',
    confirmDelete: (boatName: string) =>
      `Olet poistamassa veneen ${boatName} tietoja`
  },
  boatSpace: {
    renterType: {
      Citizen: 'Varaan yksityishenkilönä',
      Organization: 'Varaan yhteisön puolesta'
    },
    boatSpaceType: {
      Slip: {
        label: 'Laituripaikka',
        info: 'Vene vesillä laituripaikassa tai poijussa.'
      },
      Trailer: {
        label: 'Traileripaikka',
        info: 'Vene trailerilla, vesillelasku luiskalta.'
      },
      Winter: {
        label: 'Talvipaikka',
        info: 'Vene säilytettynä trailerilla tai pukilla.'
      },
      Storage: {
        label: 'Säilytyspaikka Ämmäsmäessä (ympärivuotinen)',
        info: 'Vene säilytettynä trailerilla tai pukilla.'
      }
    },
    boatType: {
      OutboardMotor: 'Perämoottorivene',
      Sailboat: 'Purjevene',
      InboardMotor: 'Sisämoottorivene',
      Rowboat: 'Soutuvene',
      JetSki: 'Vesiskootteri',
      Other: 'Muu'
    },
    ownershipStatus: {
      Owner: 'Omistan veneen',
      User: 'Olen veneen haltija',
      CoOwner: 'Omistan veneen yhdessä muiden kanssa',
      FutureOwner: 'Olen ostamassa veneen'
    },
    ownershipStatusInfo: (
      type: OwnershipStatus,
      spaceType: BoatSpaceType
    ): string | undefined => {
      if (type === 'CoOwner') {
        switch (spaceType) {
          case 'Slip':
            return 'Vähintään 50% veneenomistajista tulee olla Espoolaisia, jotta venepaikan voi uusia vuosittain. Muutoin paikka on määräaikainen.'

          case 'Winter':
            return 'Vähintään 50% veneenomistajista tulee olla Espoolaisia, jotta saatte varata talvipaikan. Ämmäsmäen säilytyspaikkoja saa varata kaikki kotikunnasta riippumatta.'
        }
      }
      return undefined
    },
    amenities: {
      Buoy: 'Poiju',
      RearBuoy: 'Peräpoiju',
      Beam: 'Aisa',
      WalkBeam: 'Kävelyaisa',
      Trailer: 'Trailerisäilytys',
      Buck: 'Pukkisäilytys'
    },
    winterStorageType: {
      Trailer: 'Trailerisäilytys',
      Buck: 'Pukkisäilytys',
      BuckWithTent: 'Pukkisäilytys suojateltalla'
    },
    reserve: 'Varaa'
  },
  citizen: {
    firstName: 'Etunimi',
    lastName: 'Sukunimi',
    email: 'Sähköposti',
    phoneNumber: 'Puhelinnumero',
    address: 'Osoite',
    nationalId: 'Henkilötunnus',
    postalCode: 'Postinumero',
    postOffice: 'Postitoimipaikka',
    municipality: 'Kotikunta'
  },
  citizenPage: {
    title: 'Omat tiedot',
    reservation: {
      title: 'Varaukset',
      noReservations: 'Ei varauksia',
      actions: {
        terminate: 'Irtisano paikka',
        renew: 'Maksa kausimaksu',
        change: 'Vaihda paikka'
      },
      modal: {
        goBackToReservation: 'Siirry varaukseen'
      }
    }
  },
  payment: {
    title: 'Valitse maksutapa'
  }
}
