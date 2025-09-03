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
    saveChanges: 'Spara ändringar',
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
      },
      dimensions: {
        widthInMeters: 'Bredd (m)',
        lengthInMeters: 'Längd (m)'
      }
    },
    openExpandingInfo: 'Öppna detaljer',
    errors: {
      genericGetError: 'Hämtning av information misslyckades',
      validationWarning: 'Vänligen fyll i alla obligatoriska fält',
      error400: 'Oj nej! Du har gått på grund.',
      error403: 'Oj nej! Båtleder är blockerad.',
      error404: 'Oj nej! Leden är borta.',
      error500: 'Oj nej! En storm överraskade systemet. Försök igen senare.'
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
    selectLanguage: 'Välj språk',
    mainNavigation: 'Huvudnavigering',
    instructionsLink: 'Instruktioner',
    openInANewWindow: 'Öppna i en ny flik',
    mapsLink:
      'Du kan öppna kartorna över hamnar och förvaringsplatser i en ny flik härifrån.',
    harborsInfoLink:
      'https://www.espoo.fi/sv/idrott-motion-och-natur/batliv/bathamnar'
  },
  footer: {
    accessibilityLink: 'Tillgänglighetsutlåtande',
    privacyLink:
      '<a target="_blank" aria-label="Integritetspolicy (länken öppnas i en ny flik)" href="https://www.espoo.fi/fi/kaupunki-ja-paatoksenteko/turvallisuus/tietosuoja/elinvoiman-tulosalueen-tietosuojaselosteet/tietosuojaseloste-henkilotietojen-kasittely-liikunnan-ja-urheilun-tulosyksikko">Integritetspolicy</a>',
    boatingLink:
      '<a target="_blank" aria-label="Esbo båtliv hemsida (länken öppnas i en ny flik)" href="https://www.espoo.fi/sv/idrott-motion-och-natur/batliv">Esbo båtliv hemsida</a>'
  },
  components: componentTranslations,
  citizenFrontPage: {
    title: 'Båtplatser',
    info: {
      locations:
        'Lediga båtplatser finns i följande hamnar: Gäddvik, Stensvik, Bredvik, Björnviken, Sökö, Finno och Svinö. Vinterförvaringsplats finns i Bredvik, Björnviken och Finno samt året-runt-förvaringsplatser i Käringbacken.',
      authenticationRequired:
        'För att boka en plats krävs stark autentisering, och båtplatsen betalas vid bokningstillfället. Bokningssystemet fungerar bäst med Google Chrome och Edge-webbläsare.',
      boatRequired:
        'Endast ägaren eller innehavaren av en båt kan boka båt-, vinter- eller förvaringsplats. Se till att uppgifterna är korrekta i Traficoms båtregister.',
      contactInfo:
        'Om du inte kan identifiera dig elektroniskt eller bokar för första gången för en gemenskap (företag eller förening), kontakta oss via e-post på venepaikat@espoo.fi eller per telefon på 09 81658984 på måndagar och onsdagar kl. 12.30–15.00 och på torsdagar kl. 9.00–11.00.',
      preparations:
        'Det är möjligt att boka från kl. 09.00 på den första dagen av bokningsperioden. För att göra en bokning behöver du följande uppgifter: e-postadress, telefonnummer, båtens registernummer, bredd, längd och vikt, båtens namn och märke eller annan identifiering. Om du bokar för första gången för en organisation behöver du dessutom organisationens FO-nummer och faktureringsadress.',
      readMore:
        'Du hittar mer information om småbåtshamnar, båtplatsavgifter och båtförvaring <a target="_blank" aria-label="Esbo båtliv hemsida (länken öppnas i en ny flik)" href="https://www.espoo.fi/sv/idrott-motion-och-natur/batliv">här</a>.'
    },
    periods: {
      Slip: {
        title: 'Boka båtplats 2025',
        season: (season: string) => `Båtsäsong ${season}`,
        periods: [
          (period: string) =>
            `${period} endast Esbobor* med ett tillsvidare gällande hyresavtal kan fortsätta hyra sin båtplats.`,
          (period: string) => `${period} endast Esbobor* kan boka båtplats.`,
          (period: string) => `${period} alla kan boka båtplats`
        ]
      },
      Trailer: {
        title: 'Boka trailerplats i Finno 2025',
        season: (season: string) =>
          `Hyressäsong ${season}. Båt på trailer, sjösättning från ramp.`,
        periods: [
          (period: string) =>
            `${period} endast Esbobor* med ett tillsvidare gällande hyresavtal kan fortsätta hyra sin trailerplats.`,
          (period: string) => `${period} alla kan boka trailerplats`
        ]
      },
      Winter: {
        title: 'Boka vinterförvaringsplats 2025',
        season: (season: string) => `Vinterförvaringssäsong ${season}`,
        periods: [
          (period: string) =>
            `${period} endast Esbobor* med ett tillsvidare gällande hyresavtal kan fortsätta hyra sin vinterförvaringsplats`,
          (period: string) =>
            `${period} endast Esbobor* kan boka vinterförvaringsplats`
        ]
      },
      Storage: {
        title: 'Boka förvaringsplats i Käringbacken 2025 **',
        season: (season: string) => `Förvaringssäsong ${season}`,
        periods: [
          (period: string) =>
            `${period} nuvarande hyrestagare som är esbobor kan förnya sin förvaringsplats`,
          (period: string) => `${period} alla kan boka förvaringsplats`
        ]
      },
      footNote:
        '* Om en båt är delägd och över 50% av ägarna bor i Esbo, kan ni boka båtplats, vinter- eller förvaringsplats som Esbobo. I detta fall måste en Esbo-bo göra bokningen.',
      footNote2:
        '** Fram till 14.9.2025 kan alla boka förvaringsplats för den pågående säsongen.'
    },
    button: {
      browseBoatSpaces: 'Bläddra bland båtplatser'
    },
    image: {
      harbors: {
        altText: 'Hamnar på kartan'
      }
    }
  },
  reservation: {
    searchPage: {
      title: 'Esbo stads båtplatsuthyrning',
      image: {
        harbors: {
          altText: 'Esbos småbåtshamnar'
        }
      },
      missingFieldsInfoBox:
        'Ange först båttyp och mått för att se vilka platser som passar din båt.',
      freeSpaceCount: 'Antal platser tillgängliga enligt sökkriterier',
      size: 'Storlek',
      amenityLabel: 'Förtöjningssätt',
      price: 'Pris/Period',
      place: 'Plats',
      filters: {
        title: 'Sök båtplats',
        boatSpaceType: 'Sök plats',
        harbor: 'Hamnen',
        amenities: 'Faciliteter',
        harborHeader: 'Hamnar',
        amenityHeader: 'Förtöjningssätt',
        boatType: 'Båttyp',
        storageTypeAmenities: 'Förråd typ',
        additionalHarborPlaceInfo:
          'Mer information om platserna och deras priser här',
        additionalHarborPlaceInfoLink:
          'https://www.espoo.fi/sv/idrott-motion-och-natur/batliv/avgifter-batplatser',
        branchSpecific: {
          Slip: {
            width: 'Båtens bredd (m)',
            length: 'Båtens längd (m)',
            harborInfo: 'Du kan endast reservera en trailerplats från Finno.'
          },
          Trailer: {
            width: 'Trailerns bredd (m)',
            length: 'Trailerns längd (m)',
            harborInfo: 'Du kan endast reservera en trailerplats från Finno.'
          },
          Winter: {
            width: 'Vinterplatsens bredd (m)',
            length: 'Vinterplatsens längd (m)',
            harborInfo: 'Välj en vinterplats i din båthamn eller i Finno.'
          },
          Storage: {
            width: 'Förvaringsplatsens bredd (m)',
            length: 'Förvaringsplatsens längd (m)',
            harborInfo:
              'Du kan endast reservera en året-runt-förvaringsplats i Käringbacken.'
          }
        },
        storageInfo:
          'All utrustning som behövs för båtens förvaring måste rymmas helt inom den reserverade platsen. Observera också att båtar placerade på onödigt stora platser kan flyttas till mindre platser.'
      },
      switchInfoText:
        'Du håller på att byta båtplats. Platsen kan endast bytas till en plats av samma typ.',
      infoText: {
        title: 'Bokning av båtplatser 2025',
        periods: {
          newReservations:
            'Bokning av båtplatser för Esbobor från och med 3.3. och för andra från 1.4.–30.9.2025. Bokningen är för båtsäsongen 10.6–14.9.2025.',
          trailerReservations:
            'Bokning av trailerplatser i Finno för alla från 1.5.–31.12.2025. Bokningen är för båtsäsongen 1.5.2025–30.4.2026.',
          winter:
            'Bokning av vinterplatser för Esbobor från 15.9.–31.12.2025. Bokningen är för vintersäsongen 15.9.2025–10.6.2026.',
          storage1:
            'Bokningen av förvaringsplatser i Käringbacken för säsongen 15.9.2024–14.9.2025 är öppen till 14.9.2025.',
          storage2:
            'Bokningen av förvaringsplatser i Käringbacken för säsongen 15.9.2025–14.9.2026 öppnar den 15.9.2025.'
        }
      },
      modal: {
        reserveNewSpace: 'Boka ny plats',
        reservingBoatSpace: 'Du håller på att boka en båtplats:',
        cannotReserveNewPlace:
          'Du har redan två båtplatser. Du kan inte boka en ny plats, men du kan byta din nuvarande plats.',
        currentPlaces: 'Din nuvarande plats:',
        switchCurrentPlace: 'Byt denna plats',
        organizationCurrentPlaces: (organizationName: string) =>
          `Din organisation ${organizationName} plats:`
      }
    },
    formPage: {
      title: {
        Slip: (name: string) => `Båtplatsreservation: ${name}`,
        Trailer: (name: string) => `Släpvagnsplatsreservation: ${name}`,
        Renew: (name: string) => `Förnyelse av plats: ${name}`,
        Winter: (name: string) => `Vinterplatsreservation: ${name}`,
        Storage: (name: string) => `Förvaringsplatsreservation: ${name}`
      },
      info: {
        switch:
          'Du håller på att byta båtplats. Bokningstiden för din båtplats förblir oförändrad. Samtidigt sägs din gamla plats upp och görs tillgänglig för andra att boka.'
      },
      submit: {
        continueToPayment: 'Fortsätt till betalning',
        confirmReservation: 'Bekräfta bokning'
      },
      storageInfo: {
        title: 'Information om förvaringsplatsen',
        buckWithTentInfo:
          'Observera att båten och stöttan, inklusive deras skyddstält, måste rymmas inom det reserverade utrymmet.'
      },
      trailerInfo: {
        title: 'Information om släpvagnen',
        registrationNumber: 'Registernummer',
        editTrailerDetails: 'Redigera släpvagnsdetaljer'
      },
      allYearStorage: {
        Trailer: {
          title: 'Trailer information'
        },
        Buck: {
          title: 'Bock information'
        }
      },
      reserver: 'Bokare',
      tenant: 'Hyresgäst',
      boatInformation: 'Båtinformation',
      boatSpaceInformation: 'Båtplats att reservera',
      harbor: 'Hamn',
      place: 'Plats',
      boatSpaceType: 'Båtplatstyp',
      boatSpaceDimensions: 'Båtplatsens dimensioner',
      boatSpaceAmenity: 'Förtöjningssätt',
      reservationValidity: 'Reservationens giltighet:',
      price: 'Pris',
      storageType: 'Förvaringstyp'
    },
    paymentPage: {
      paymentCancelled:
        'Betalningen misslyckades, försök igen eller gå tillbaka för att avbryta bokningen.'
    },
    confirmationPage: {
      header: 'Bokningen lyckades',
      emailInfo:
        'Du kommer också att få en bekräftelse via e-post till den adress du angav.',
      indefiniteInfo:
        'Din bokning gäller tills vidare, och du kan förlänga den till nästa säsong årligen under förlängningsperioden.',
      fixedInfo: 'Din bokning är tidsbegränsad och gäller för en säsong.'
    },
    steps: {
      chooseBoatSpace: 'Välj plats',
      fillInformation: 'Fyll i information',
      payment: 'Betalning',
      confirmation: 'Bekräftelse',
      error: 'Fel'
    },
    noRegistererNumber: 'Inget registreringsnummer',
    certify: 'Jag intygar att de uppgifter jag har lämnat är korrekta.',
    agreeToRules:
      'Jag har läst <a target="_blank" href="https://www.espoo.fi/sv/bestammelser-bathamnar">hamnens avtalsvillkor och regler</a> och förbinder mig att följa dem.',
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
      status: ReservationStatus,
      isActive: boolean
    ) => {
      if (status !== 'Cancelled' && validity === 'Indefinite' && isActive) {
        return 'Tills vidare, förnyas årligen'
      }
      return `Till ${endDate.format()}`
    },
    terminatedAt: (terminationDate: LocalDate): string => {
      return `Avslutad ${terminationDate.format()}`
    },
    reserverDiscountInfo: (
      type: ReserverType,
      reserverName: string,
      discountPercentage: number,
      discountedPrice: string
    ) => {
      const name = type === 'Organization' ? `${reserverName}` : `dig `
      return `En rabatt på ${discountPercentage} % har definierats för ${name}. Efter rabatten förblir priset på platsen ${discountedPrice} €`
    },
    paymentState: (paymentDate?: LocalDate) => {
      return paymentDate ? `Betald ${paymentDate.format()}` : '-'
    },
    invoiceState: (dueDate?: LocalDate) => {
      return dueDate ? `Faktureras, förfallodag ${dueDate.format()}` : '-'
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
          'Du har redan det maximala antalet platser av denna typ. Om du agerar på uppdrag av en organisation kan du fortsätta att boka.',
        NOT_AVAILABLE:
          'Båtplatsen du försökte reservera är inte längre tillgänglig. Vänligen välj en annan ledig plats.'
      },
      fillInformation: {
        title: 'Reservering misslyckades',
        SERVER_ERROR:
          'Antingen är du inte berättigad att boka platsen, eller så inträffade ett annat fel. Kontakta kundtjänst. Kundtjänstens kontaktuppgifter finns på startsidan.',
        UNFINISHED_RESERVATION:
          'Du har en pågående bokning. Vänligen slutför bokningen eller avbryt den för att fortsätta.'
      },
      cancelPayment: {
        title: 'Återgång misslyckades',
        SERVER_ERROR:
          'Återgång för att fylla i informationen misslyckades, eller så inträffade ett annat fel. Vänligen kontakta kundtjänst. Du hittar kontaktuppgifter till kundtjänsten på startsidan.'
      },
      failedReservation: {
        title: 'Reservering misslyckades',
        type: {
          boatSpaceNotAvailable: [
            'Tyvärr bekräftade du inte bokningen i tid, och platsen har redan bokats av någon annan.',
            'Din betalning kan ha genomförts, så vänligen kontrollera situationen genom att kontakta vår kundtjänst för vidare instruktioner och eventuell återbetalning.',
            'Du hittar kundtjänstens kontaktuppgifter på startsidan.'
          ],
          unknown: [
            'Okänt fel, platsen kunde inte bokas.',
            'Din betalning kan ha genomförts, så vänligen kontrollera situationen genom att kontakta vår kundtjänst för vidare instruktioner och eventuell återbetalning.',
            'Du hittar kundtjänstens kontaktuppgifter på startsidan.'
          ]
        }
      }
    },
    paymentInfo: {
      moreExpensive: (amount: string) =>
        `Observera att den nya platsen är dyrare än din nuvarande plats. Priset tar redan hänsyn till den betalning du har gjort, och du behöver bara betala mellanskillnaden på ${amount} €.`,
      lessExpensive:
        'Observera att den nya platsen är billigare än din nuvarande plats. Ingen återbetalning ges.',
      equal:
        'Platsen kostar lika mycket som den tidigare. Du behöver inte betala igen.'
    },
    auth: {
      reservingBoatSpace: 'Bokar båtplats:',
      reservingRequiresAuth:
        'För att boka en båtplats krävs stark autentisering.',
      continue: 'Fortsätt'
    },
    cancelConfirmation:
      'Du håller på att lämna bokningsformuläret. Den ifyllda informationen eller bokningen sparas inte. Är du säker på att du vill avbryta bokningen?',
    noAndGoBack: 'Nej, gå tillbaka',
    yesCancelReservation: 'Ja, avbryt bokningen',
    cancelReservation: 'Avbryt reservation',
    cancelAndGoBack: 'Avbryt och gå tillbaka',
    goBack: 'Gå tillbaka',
    continueToPaymentButton: 'Fortsätt till betalning',
    timer: {
      title:
        'Du har ${minutes} och ${seconds} på dig att bekräfta din båtplatsreservation genom att betala.',
      announceTitle:
        'Du har ${time} på dig att bekräfta din båtplatsreservation genom att betala.',
      minute: 'minut',
      minutes: 'minuter',
      second: 'sekund',
      seconds: 'sekunder'
    }
  },
  boat: {
    newBoat: 'Ny båt',
    delete: 'Ta bort båt',
    deleteFailed:
      'Ett fel uppstod vid borttagning av båten. Vänligen kontakta kundtjänst.',
    deleteSuccess: 'Båten har tagits bort',
    confirmDelete: (boatName: string) =>
      `Du håller på att ta bort informationen för båten ${boatName}`,
    editBoatDetails: 'Redigera båtuppgifter',
    boatName: 'Båtnamn',
    boatDepthInMeters: 'Djupgående (m)',
    boatWeightInKg: 'Vikt (kg)',
    registrationNumber: 'Registreringsnummer',
    otherIdentifier: 'Märke och modell/annan identifierare',
    additionalInfo: 'Ytterligare information',
    title: 'Båtar',
    boatType: 'Båttyp',
    ownership: 'Ägande',
    boatSizeWarning: 'Båten passar inte på den valda båtplatsen.',
    boatSizeWarningExplanation:
      'Båtplatser har säkerhetsutrymmen för att förhindra skador på båtar och bryggor. En båt placerad på en plats som är för trång kan flyttas av kommunen, och ägaren av båtplatsen ansvarar för kostnaderna.',
    boatWeightWarning:
      'Den maximala tillåtna vikten för en båt förtöjd i Esbo stads hamnar är 15 000 kg.',
    boatWeightWarning2:
      'En båt som är för tung kan flyttas av staden, och båtplatsinnehavaren ansvarar för kostnaderna.'
  },
  boatSpace: {
    boatSpaceType: {
      Slip: {
        label: 'Båtplats',
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
        label: 'Förvaringsplats i Käringbacken (året runt)',
        info: 'Båt förvarad på trailer eller bock.'
      }
    },

    boatType: {
      OutboardMotor: 'Utbordsmotorbåt',
      Sailboat: 'Segelbåt',
      InboardMotor: 'Inombordsmotorbåt',
      Rowboat: 'Roddbåt',
      JetSki: 'Vattenskoter',
      Other: 'Annan'
    },
    renterType: {
      Citizen: 'Jag bokar som privatperson',
      Organization: 'Jag bokar för en organisation'
    },
    ownershipTitle: 'Ägande enligt Traficom',
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
            return 'Minst 50% av båtägarna måste vara bosatta i Esbo för att ni ska kunna boka en vinterplats. Förvaringsplatser i Käringbacken kan bokas av alla oavsett hemkommun.'
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
      Buck: 'Förvaring på bock',
      None: '-'
    },
    harbors: {
      1: 'Gäddvik',
      2: 'Stensvik',
      3: 'Bredvik',
      4: 'Björnviken',
      5: 'Sökö',
      6: 'Finno',
      7: 'Svinö',
      8: 'Käringbacken'
    },
    winterStorageType: {
      Trailer: 'Trailerförvaring',
      Buck: 'Förvaring på bock',
      BuckWithTent: 'Förvaring på bock med skyddstält'
    },
    reserve: 'Boka',
    heightWarning: {
      bridge: (height: string) =>
        `Bron som leder till hamnen har en seglingshöjd på ${height} m`,
      bridges: (height: string) =>
        `Broarna som leder till hamnen har en seglingshöjd på ${height} m`
    }
  },
  citizen: {
    firstName: 'Förnamn',
    lastName: 'Efternamn',
    email: 'E-post',
    phoneNumber: 'Telefonnummer',
    address: 'Adress',
    nationalId: 'Personnummer',
    postalCode: 'Postnummer',
    postOffice: 'Postort',
    municipality: 'Kommun',
    birthday: 'Födelsedatum',
    streetAddress: 'Adress',
    homeAddress: 'Hemadress'
  },
  citizenPage: {
    title: 'Mina uppgifter',
    reservation: {
      title: 'Bokningar',
      noReservations: 'Inga bokningar',
      actions: {
        terminate: 'Avboka plats',
        renew: 'Förnya plats',
        change: 'Byt plats'
      },
      modal: {
        goBackToReservation: 'Gå till bokningen',
        termination: {
          title: 'Du säger upp båtplatsreservationen',
          spaceInformation:
            'Observera att du omedelbart måste flytta din båt från brygg- eller trailerplatsen när du har sagt upp platsen. Vinter- eller Käringbackens förvaringsplats kan användas till slutet av den pågående säsongen.',
          notEntitledToRefund:
            'Esbo stad beviljar ingen återbetalning för en betald båtplats.',
          confirm: 'Säg upp båtplatsen',
          terminationFailed:
            'Ett fel inträffade vid uppsägning av båtplatsen. Vänligen kontakta kundtjänst.',
          success: 'Bokningen har avslutats framgångsrikt'
        }
      },
      showAllBoats: 'Visa även båtar som inte är kopplade till en reservation',
      renewNotification: (date: LocalDate) =>
        `Avtalsperioden är på väg att ta slut. Säkerställ samma plats för nästa säsong genom att betala säsongsavgiften senast ${date.format()} eller byt till en ny plats.`,
      harbor: 'Hamn',
      reservationDate: 'Reservationens startdatum',
      place: 'Plats',
      reservationValidity: 'Reservationens giltighet',
      placeType: 'Platstyp',
      price: 'Pris',
      boatPresent: 'Båt på platsen',
      equipment: 'Utrustning',
      paymentStatus: 'Betalningsstatus',
      storageType: 'Förvaringstyp'
    },
    placeReservations: 'Platsreservationer',
    expired: 'Avslutade',
    expiredReservations: 'Avslutade bokningar'
  },
  organization: {
    information: {
      title: 'Organisationens information',
      phone: 'Organisationens telefonnummer',
      email: 'Organisationens e-post',
      name: 'Organisationens namn'
    },
    title: 'Organisationer',
    name: 'Namn',
    organizationId: 'Organisationsnummer',
    municipality: 'Hemkommun',
    physicalAddress: 'Besökadress',
    streetAddress: 'Adress',
    postalCode: 'Postnummer',
    postOffice: 'Postanstalt',
    contactDetails: {
      title: 'Kontaktpersoner',
      fields: {
        name: 'Namn',
        phone: 'Telefonnummer',
        email: 'E-post'
      }
    }
  },
  payment: {
    title: 'Välj betalningsmetod'
  }
}

export default sv
