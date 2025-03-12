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
    saveChanges: 'Tallenna muutokset',
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
      genericGetError: 'Tietojen hakeminen ei onnistunut',
      validationWarning: 'Pakollisia tietoja puuttuu',
      error400: 'Voi ei! Karahdit karille.',
      error403: 'Voi ei! Veneväylä on tukossa.',
      error404: 'Voi ei! Väylä hukassa.',
      error500:
        'Voi ei! Myrsky yllätti järjestelmän. Yritä myöhemmin uudestaan.'
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
    selectLanguage: 'Valitse kieli',
    mainNavigation: 'Päänavigaatio',
    instructionsLink: 'Ohje',
    openInANewWindow: 'Avaa uudessa välilehdessä',
    mapsLink: 'Satamien ja säilytyspaikkojen kartat voit avata uudelle välilehdelle tästä',
    harborsInfoLink: 'https://www.espoo.fi/fi/liikunta-ja-luonto/veneily/venesatamat',
  },
  footer: {
    accessibilityLink: 'Saavutettavuusseloste',
    privacyLink:
      '<a target="_blank" aria-label="Tietosuojaseloste (linkki aukeaa uuteen välilehteen)" href="https://www.espoo.fi/fi/kaupunki-ja-paatoksenteko/turvallisuus/tietosuoja/elinvoiman-tulosalueen-tietosuojaselosteet/tietosuojaseloste-henkilotietojen-kasittely-liikunnan-ja-urheilun-tulosyksikko">Tietosuojaseloste</a>',
    boatingLink:
      '<a target="_blank" aria-label="Espoon veneilyn etusivu (linkki aukeaa uuteen välilehteen)" href="https://www.espoo.fi/fi/liikunta-ja-luonto/veneily">Espoon veneilyn etusivu</a>'
  },
  components: componentTranslations,
  citizenFrontPage: {
    title: 'Venepaikat',
    info: {
      locations:
        'Varattavia laituripaikkoja löytyy seuraavista satamista: Haukilahti, Kivenlahti, Laajalahti, Otsolahti, Soukka, Suomenoja ja Svinö. Talvipaikkoja on varattavissa Laajalahdessa, Otsolahdessa ja Suomenojalla sekä ympärivuotisia säilytyspaikkoja Ämmäsmäellä.',
      authenticationRequired:
        'Paikan varaaminen vaatii vahvan tunnistautumisen ja venepaikka maksetaan varaamisen yhteydessä. Varausjärjestelmä toimii parhaiten Google Chrome ja Edge selaimilla.',
      boatRequired:
        'Vain veneen omistaja tai haltija voi varata laituri-, talvi-, tai säilytyspaikkoja. Pidä huoli, että tiedot ovat oikein Traficomin venerekisterissä.',
      contactInfo:
        'Jos et voi tunnistautua sähköisesti tai varaat ensimmäistä kertaa yhteisön (yritys tai seura) puolesta, ota yhteyttä sähköpostilla venepaikat@espoo.fi tai puhelimitse 09 81658984 ma ja ke klo 12.30-15 ja to 9-11.',
      preparations:
        'Tarvitset varausta varten seuraavat tiedot: sähköpostiosoite, puhelinnumero, veneen rekisteritunnus, leveys, pituus ja paino, veneen nimi ja merkki tai muu tunniste. Jos varaat ensimmäistä kertaa yhteisön puolesta tarvitset lisäksi yhteisön Y-tunnuksen ja laskutusosoitteen.',
      readMore:
        'Lisätietoja venesatamista, venepaikkamaksuista ja veneiden säilytyksestä löydät <a target="_blank" aria-label="Espoon veneilyn etusivu (linkki aukeaa uuteen välilehteen)" href="https://www.espoo.fi/fi/liikunta-ja-luonto/veneily">täältä</a>.'
    },
    periods: {
      Slip: {
        title: 'Laituripaikkojen varaaminen 2025',
        season: (season: string) => `Veneilykausi ${season}`,
        periods: [
          (period: string) =>
            `${period} vain espoolaiset* toistaiseksi voimassa olevan paikan vuokraajat voivat jatkaa laituripaikansa vuokrausta`,
          (period: string) =>
            `${period} vain espoolaiset* voivat varata laituripaikkoja`,
          (period: string) => `${period} kaikki voivat varata laituripaikkoja`
        ]
      },
      Trailer: {
        title: 'Suomenojan traileripaikkojen varaaminen 2025',
        season: (season: string) =>
          `Vuokrakausi ${season}. Vene trailerilla, vesillelasku luiskalta.`,
        periods: [
          (period: string) =>
            `${period} vain espoolaiset* toistaiseksi voimassa olevan paikan vuokraajat voivat jatkaa traileripaikan vuokrausta`,
          (period: string) => `${period} kaikki voivat varata traileripaikkoja`
        ]
      },
      Winter: {
        title: 'Talvipaikkojen varaaminen 2025',
        season: (season: string) => `Talvisäilytyskausi ${season}`,
        periods: [
          (period: string) =>
            `${period} vain espoolaiset* toistaiseksi voimassa olevan paikan vuokraajat voivat jatkaa talvipaikan vuokrausta`,
          (period: string) =>
            `${period} vain espoolaiset* voivat varata talvipaikkoja`
        ]
      },
      Storage: {
        title: 'Ämmäsmäen säilytyspaikan varaaminen 2025 **',
        season: (season: string) => `Säilytyskausi ${season}`,
        periods: [
          (period: string) =>
            `${period} säilytyspaikan vuokraajat voivat jatkaa säilytyspaikan vuokrausta`,
          (period: string) => `${period} kaikki voivat varata säilytyspaikkoja`
        ]
      },
      footNote:
        '* Jos vene on yhteisomistuksessa ja vähintään 50% veneen omistajista asuu Espoossa, voitte varata laituri-, talvi- tai säilytyspaikan espoolaisena. Jonkun Espoossa asuvista on tällöin tehtävä varaus.',
      footNote2:
        '** 14.9.2025 asti kaikki voivat varata säilytyspaikkoja kuluvalle kaudelle'
    },
    button: {
      browseBoatSpaces: 'Selaile vapaita venepaikkoja'
    },
    image: {
      harbors: {
        altText: 'Satamat kartalla'
      }
    }
  },
  reservation: {
    steps: {
      chooseBoatSpace: 'Paikan valinta',
      fillInformation: 'Tietojen täyttäminen',
      payment: 'Maksaminen',
      confirmation: 'Vahvistus',
      error: 'Virhe'
    },
    searchPage: {
      title: 'Espoon kaupungin venepaikkojen vuokraus',
      image: {
        harbors: {
          altText: 'Espoon venesatamat'
        }
      },
      missingFieldsInfoBox:
        'Anna ensin paikan mitat niin näet veneellesi sopivat paikat.',
      freeSpaceCount: 'Hakuehtoihin sopivat vapaat paikat',
      size: 'Paikan koko',
      amenityLabel: 'Varuste',
      price: 'Hinta/Kausi',
      place: 'Paikka',
      filters: {
        title: 'Venepaikan haku',
        boatSpaceType: 'Haettava paikka',
        harbor: 'Satama',
        amenities: 'Varusteet',
        harborHeader: 'Satama',
        amenityHeader: 'Varuste',
        boatType: 'Venetyyppi',
        storageTypeAmenities: 'Säilytystapa',
        branchSpecific: {
          Slip: {
            width: 'Veneen leveys (m)',
            length: 'Veneen pituus (m)',
            harborInfo: ''
          },
          Trailer: {
            width: 'Trailerin leveys (m)',
            length: 'Trailerin pituus (m)',
            harborInfo: 'Traileripaikan voit varata ainoastaan Suomenojalta.'
          },
          Winter: {
            width: 'Säilytyspaikan leveys (m)',
            length: 'Säilytyspaikan pituus (m)',
            harborInfo:
              'Valitse talvipaikka venepaikkasi satamasta tai Suomenojalta.'
          },
          Storage: {
            width: 'Säilytyspaikan leveys (m)',
            length: 'Säilytyspaikan pituus (m)',
            harborInfo:
              'Ympärivuotisen säilytyksen voit varata ainoastaan Ämmäsmäen säilytysalueelta.'
          }
        },
        storageInfo:
          'Veneen säilytykseen tarvittavan kaluston on mahduttava kokonaan varattavan paikan sisäpuolelle. Huomioi myös, että tarpeettoman suurille paikoille sijoitetut veneet voidaan siirtää pienemmille paikoille.'
      },
      switchInfoText:
        'Olet vaihtamassa venepaikkaa. Paikan voi vaihtaa ainoastaan saman paikkatyypin paikkoihin. ',
      infoText: {
        title: 'Venepaikkojen varaaminen 2025',
        periods: {
          newReservations:
            'Laituripaikkojen varaaminen espoolaisille 3.3. alkaen ja muille 1.4.–30.9.2025. Varaus on veneilykaudelle 10.6.–14.9.2025.',
          trailerReservations:
            'Suomenojan traileripaikkojen varaaminen 1.5.–31.12.2025. Varaus on kaudelle 1.5.2025–30.4.2026.',
          winter:
            'Talvipaikkojen varaaminen espoolaisille 15.9.–31.12.2025. Varaus on talvisäilytyskaudelle 15.9.2025–10.6.2026.',
          storage1:
            'Ämmäsmäen säilytyspaikkojen varaaminen kaudelle 15.9.2024–14.9.2025 on auki 14.9.2025 asti.',
          storage2:
            'Ämmäsmäen säilytyspaikkojen varaaminen kaudelle 15.9.2025–14.9.2026 aukeaa 15.9.2025.'
        }
      },
      modal: {
        reserveNewSpace: 'Varaan uuden paikan',
        reservingBoatSpace: 'Olet varaamassa paikkaa:',
        cannotReserveNewPlace:
          'Sinulla on jo kaksi paikkaa. Et voi varata uutta paikkaa, mutta voit vaihtaa nykyisen paikkasi.',
        currentPlaces: 'Omat paikkasi:',
        organizationCurrentPlaces: (organizationName: string) =>
          `Yhteisösi ${organizationName} paikat:`,
        switchCurrentPlace: 'Vaihdan tämän paikan'
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
          'Olet vaihtamassa paikkaa. Paikkasi varausaika säilyy ennallaan. Samalla vanha paikkasi irtisanoutuu ja vapautuu muiden varattavaksi.'
      },
      submit: {
        continueToPayment: 'Jatka maksamaan',
        confirmReservation: 'Vahvista varaus'
      },
      storageInfo: {
        title: 'Säilytyspaikan tiedot',
        buckWithTentInfo:
          'Huomioi, että veneen ja pukkin suojatelttoineen tulee mahtua varatun paikan sisään.'
      },
      trailerInfo: {
        title: 'Trailerin tiedot',
        registrationNumber: 'Rekisterinumero',
        editTrailerDetails: 'Muokkaa trailerin tietoja'
      },
      allYearStorage: {
        Trailer: {
          title: 'Trailerin tiedot'
        },
        Buck: {
          title: 'Pukin tiedot'
        }
      },
      reserver: 'Varaaja',
      tenant: 'Vuokralainen',
      boatInformation: 'Veneen tiedot',
      boatSpaceInformation: 'Varattava paikka',
      harbor: 'Satama',
      place: 'Paikka',
      boatSpaceType: 'Venepaikkatyyppi',
      boatSpaceDimensions: 'Paikan koko',
      boatSpaceAmenity: 'Varuste',
      reservationValidity: 'Varaus voimassa:',
      price: 'Hinta',
      storageType: 'Säilytystapa'
    },
    paymentPage: {
      paymentCancelled:
        'Maksu epäonnistui, yritä uudelleen tai palaa takaisin peruuttaaksesi varauksen.'
    },
    confirmationPage: {
      header: 'Paikan varaus onnistui',
      emailInfo:
        'Saat viestin vahvistuksesta myös ilmoittamaasi sähköpostiosoitteeseen.',
      indefiniteInfo:
        'Varauksesi on voimassa toistaiseksi ja voit jatkaa sitä seuraavalle kaudelle vuosittain jatkokauden aikana.',
      fixedInfo: 'Varauksesi on määräaikainen ja voimassa yhden kauden verran.'
    },

    noRegistererNumber: 'Ei rekisterinumeroa',
    certify: 'Vakuutan antamani tiedot oikeiksi',
    agreeToRules:
      'Olen lukenut <a target="_blank" href="https://www.espoo.fi/fi/venesatamamia-koskevat-ehdot-saannot-ja-ohjeet">venesatamien sopimusehdot ja säännöt</a> ja sitoudun noudattamaan niitä.',
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
      isActive: boolean
    ): string => {
      if (validity === 'Indefinite' && isActive) {
        return 'Toistaiseksi, jatko vuosittain'
      }
      return `${endDate.format()} asti`
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
    invoiceState: (dueDate?: LocalDate) => {
      return dueDate ? `Laskutettu, eräpäivä ${dueDate.format()}` : '-'
    },
    errors: {
      startReservation: {
        title: 'Varaaminen ei ole mahdollista',
        MAX_RESERVATIONS: 'Sinulla on jo maksimimäärä tämän tyypin paikkoja.',
        NOT_POSSIBLE: 'Varauskausi ei ole auki. Tarkista hakuajat etusivulta.',
        SERVER_ERROR:
          'Joko et ole oikeutettu varaamaan paikkaa, tai sattui muu virhe. Ota yhteyttä asiakaspalveluun. Asiakaspalvelun yhteystiedot löydät etusivulta.',
        MAX_PERSONAL_RESERVATIONS:
          'Sinulla on jo maksimimäärä tämän tyypin paikkoja. Jos asioit yhteisön puolesta, voit jatkaa varaamista. ',
        NOT_AVAILABLE:
          'Venepaikka jota yritit varata ei ole enää saatavilla. Valitse jokin muu paikka.'
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
      },
      failedReservation: {
        title: 'Paikan varaus epäonnistui',
        type: {
          boatSpaceNotAvailable: [
            'Valitettavasti et vahvistanut varausta ajoissa, ja paikka on jo varattu toiselle.',
            'Maksusi on saattanut lähteä, joten tarkistathan tilanteen ottamalla yhteyttä asiakaspalveluumme saadaksesi lisäohjeita ja mahdollisen hyvityksen.',
            'Asiakaspalvelun yhteystiedot löydät etusivulta.'
          ],
          unknown: [
            'Tuntematon virhe, paikkaa ei voitu varata.',
            'Maksusi on saattanut lähteä, joten tarkistathan tilanteen ottamalla yhteyttä asiakaspalveluumme saadaksesi lisäohjeita ja mahdollisen hyvityksen.',
            'Asiakaspalvelun yhteystiedot löydät etusivulta.'
          ]
        }
      }
    },
    auth: {
      reservingBoatSpace: 'Olet varaamassa paikkaa:',
      reservingRequiresAuth:
        'Venepaikan varaaminen vaatii vahvan tunnistautumisen.',
      continue: 'Jatka tunnistautumiseen'
    },
    cancelConfirmation:
      'Olet poistumassa varauslomakkeelta. Täytettyjä tietoja tai paikkavarausta ei tallenneta. Oletko varma, että haluat perua varauksen?',
    noAndGoBack: 'Ei, palaa takaisin',
    yesCancelReservation: 'Kyllä, peru varaus',
    cancelReservation: 'Peruuta varaus',
    cancelAndGoBack: 'Peruuta varaus ja palaa takaisin',
    goBack: 'Palaa takaisin',
    continueToPaymentButton: 'Jatka maksamaan',
    timer: {
      title:
        'Sinulla on ${minutes} ja ${seconds} aikaa vahvistaa venepaikkavaraus maksamalla.',
      announceTitle:
        'Sinulla on ${time} aikaa vahvistaa venepaikkavaraus maksamalla.',
      minute: 'minuutti',
      minutes: 'minuuttia',
      second: 'sekunti',
      seconds: 'sekuntia'
    }
  },
  boat: {
    newBoat: 'Uusi vene',
    delete: 'Poista vene',
    deleteFailed:
      'Veneen poistamisessa tapahtui virhe. Ota yhteyttä asiakaspalveluun.',
    deleteSuccess: 'Vene on poistettu',
    confirmDelete: (boatName: string) =>
      `Olet poistamassa veneen ${boatName} tietoja`,
    editBoatDetails: 'Muokkaa veneen tietoja',
    boatName: 'Veneen nimi',
    boatDepthInMeters: 'Syväys (m)',
    boatWeightInKg: 'Paino (kg)',
    registrationNumber: 'Rekisteritunnus',
    otherIdentifier: 'Merkki ja malli/muu tunniste',
    additionalInfo: 'Lisätiedot',
    title: 'Veneet',
    boatType: 'Veneen tyyppi',
    ownership: 'Omistussuhde',
    boatSizeWarning: 'Veneen koko ei ole sopiva valitulle venepaikalle.',
    boatSizeWarningExplanation:
      'Venepaikoilla on turvavälit veneiden ja laiturien vaurioiden estämiseksi. Liian ahtaaseen tai tarpeettoman suureen paikkaan laitettu vene voidaan siirtää kaupungin toimesta, ja venepaikan haltija vastaa kustannuksista.',
    boatWeightWarning:
      'Espoon kaupungin omistamiin venesatamiin kiinnitettävän veneen suurin sallittu paino on 15 000 kg.',
    boatWeightWarning2:
      'Liian painava vene voidaan siirtää kaupungin toimesta, ja venepaikan haltija vastaa kustannuksista.'
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
    ownershipTitle: 'Veneen omistussuhde Traficomin mukaan',
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
      Buoy: 'Poiju merellä',
      RearBuoy: 'Peräpoiju',
      Beam: 'Aisa',
      WalkBeam: 'Kävelyaisa',
      Trailer: 'Trailerisäilytys',
      Buck: 'Pukkisäilytys',
      None: '-'
    },
    winterStorageType: {
      Trailer: 'Trailerisäilytys',
      Buck: 'Pukkisäilytys',
      BuckWithTent: 'Pukkisäilytys suojateltalla'
    },
    reserve: 'Varaa',
    heightWarning: {
      bridge: (height: string) =>
        `Satamaan johtavan sillan alituskorkeus on ${height} m`,
      bridges: (height: string) =>
        `Satamaan johtavien siltojen alituskorkeus ${height} m`
    }
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
    municipality: 'Kotikunta',
    birthday: 'Syntymäaika',
    streetAddress: 'Katuosoite',
    homeAddress: 'Kotiosoite'
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
        goBackToReservation: 'Siirry varaukseen',
        termination: {
          title: 'Olet irtisanomassa venepaikkaa:',
          moveBoatImmediately:
            'Huomioi, että sinun on siirrettävä veneesi välittömästi pois venepaikalta kun olet irtisanonut paikan.',
          notEntitledToRefund:
            'Espoon kaupunki ei myönnä hyvitystä maksetusta venepaikasta.',
          confirm: 'Irtisano venepaikka',
          terminationFailed:
            'Venepaikan irtisanomisessa tapahtui virhe. Ota yhteyttä asiakaspalveluun.',
          success: 'Paikka irtisanottu onnistuneesti'
        }
      },
      showAllBoats: 'Näytä myös veneet joita ei ole liitetty venepaikkoihin',
      renewNotification: (date: LocalDate) =>
        `Sopimusaika päättymässä. Varmista sama paikka ensi kaudelle maksamalla kausimaksu ${date.format()} mennessä tai vaihda uuteen paikkaan`,
      harbor: 'Satama',
      reservationDate: 'Varauksen alkupäivä',
      place: 'Paikka',
      reservationValidity: 'Varaus voimassa',
      placeType: 'Paikan tyyppi',
      price: 'Hinta',
      boatPresent: 'Paikalla oleva vene',
      equipment: 'Varuste',
      paymentStatus: 'Maksun tila',
      storageType: 'Säilytystapa'
    },
    placeReservations: 'Paikkavaraukset',
    expired: 'Päättyneet',
    expiredReservations: 'Päättyneet varaukset'
  },
  organization: {
    information: {
      title: 'Yhteisön tiedot',
      phone: 'Yhteisön puhelinnumero',
      email: 'Yhteisön sähköposti',
      name: 'Yhteisön nimi'
    },
    title: 'Yhteisöt',
    name: 'Nimi',
    organizationId: 'Y-tunnus',
    municipality: 'Kotikunta',
    physicalAddress: 'Käyntiosoite',
    streetAddress: 'Katuosoite',
    postalCode: 'Postinumero',
    postOffice: 'Postitoimipaikka',
    contactDetails: {
      title: 'Yhteyshenkilöt',
      fields: {
        name: 'Nimi',
        phone: 'Puhelinnumero',
        email: 'Sähköposti'
      }
    }
  },
  payment: {
    title: 'Valitse maksutapa'
  }
}
