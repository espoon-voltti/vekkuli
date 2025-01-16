// SPDX-FileCopyrightText: 2017-2022 City of Espoo
//
// SPDX-License-Identifier: LGPL-2.1-or-later

import type { Translations as ComponentTranslations } from 'lib-components/i18n'

import {
  BoatSpaceType,
  OwnershipStatus,
  ReservationValidity
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
        boatSpaceType: 'Haettava paikka',
        harbor: 'Satama',
        amenities: 'Varusteet',
        boatType: 'Venetyyppi',
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
      }
    },
    formPage: {
      title: {
        Slip: (name: string) => `Venepaikan varaus: ${name}`,
        Trailer: (name: string) => `Traileripaikan varaus: ${name}`,
        Renew: (name: string) => `Paikan uusinta: ${name}`,
        Winter: (name: string) => `Talvipaikan varaus: ${name}`,
        Storage: (name: string) => `Säilytyspaikan varaus: ${name}`
      }
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
    cancelConfirmation2: 'Haluatko jatkaa?'
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
    }
  }
}
