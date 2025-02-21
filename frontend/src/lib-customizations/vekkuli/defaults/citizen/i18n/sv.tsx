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
      validationWarning: 'Vänligen fyll i alla obligatoriska fält'
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
    mainNavigation: 'Huvudnavigering'
  },
  components: componentTranslations,
  citizenFrontPage: {
    title: 'Båtplatser',
    info: {
      locations:
        'Lediga båtplatser finns i följande hamnar: Haukilahti, Kivenlahti, Laajalahti, Otsolahti, Soukka, Suomenoja och Svinö. Vinterförvaringsplatser finns i Laajalahti, Otsolahti och Suomenoja samt året-runt-förvaringsplatser i Ämmäsmäki.',
      authenticationRequired:
        'För att boka en plats krävs stark autentisering, och båtplatsen betalas vid bokningstillfället.',
      boatRequired:
        'Endast ägaren eller innehavaren av en båt kan boka båt-, vinter- eller förvaringsplatser. Se till att uppgifterna är korrekta i Traficoms båtregister.',
      contactInfo:
        'Om du inte kan autentisera dig elektroniskt, kontakta oss via e-post på venepaikat@espoo.fi eller per telefon på 09 81658984 mån och ons kl. 12:30-15:00 och tors kl. 9:00-11:00. Ha följande information redo för bokningen: bokarens personnummer, namn, adress och e-postadress; båtens bredd, längd och vikt; samt båtens namn eller annan identifierare.',
      readMore:
        'Du hittar mer information om småbåtshamnar, båtplatsavgifter och båtförvaring här.'
    },
    periods: {
      Slip: {
        title: 'Boka båtplatser',
        season: (season: string) => `Båtsäsong ${season}`,
        periods: [
          (period: string) => `${period} Esbo-bor* kan boka båtplatser`,
          (period: string) => `${period} alla kan boka båtplatser`
        ]
      },
      Trailer: {
        title: 'Boka trailerplatser vid Suomenoja',
        season: (season: string) =>
          `Hyressäsong ${season} Båt på trailer, sjösättning från ramp.`,
        periods: [
          (period: string) =>
            `${period} endast Esbo-bor* med ett nuvarande hyresavtal för trailerplats kan förnya sin hyra`,
          (period: string) => `${period} alla kan boka trailerplatser`
        ]
      },
      Winter: {
        title: 'Boka vinterförvaringsplatser',
        season: (season: string) => `Vinterförvaringssäsong ${season}`,
        periods: [
          (period: string) =>
            `${period} endast Esbo-bor* med ett nuvarande hyresavtal för vinterplats kan förnya sin hyra`,
          (period: string) =>
            `${period} endast Esbo-bor* kan boka vinterförvaringsplatser`
        ]
      },
      Storage: {
        title: 'Boka förvaringsplatser i Ämmässuo',
        season: (season: string) => `Förvaringssäsong ${season}`,
        periods: [
          (period: string) =>
            `${period} nuvarande hyrestagare kan förnya sin förvaringsplats`,
          (period: string) => `${period} alla kan boka förvaringsplatser`
        ]
      },
      footNote:
        '*Om en båt är delägd och över 50% av ägarna bor i Esbo, kan ni boka båtplats, vinter- eller förvaringsplats som Esbo-bo. I detta fall måste en Esbo-bo göra bokningen.'
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
        branchSpecific: {
          Slip: {
            width: 'Båtens bredd (m)',
            length: 'Båtens längd (m)',
            harborInfo: 'Du kan endast reservera en trailerplats från Finno.'
          },
          Trailer: {
            width: 'Släpvagnens bredd (m)',
            length: 'Släpvagnens längd (m)',
            harborInfo: 'Du kan endast reservera en trailerplats från Finno.'
          },
          Winter: {
            width: 'Förvaringsplatsens bredd (m)',
            length: 'Förvaringsplatsens längd (m)',
            harborInfo: 'Välj en vinterplats i din båthamn eller i Finno.'
          },
          Storage: {
            width: 'Förvaringsplatsens bredd (m)',
            length: 'Förvaringsplatsens längd (m)',
            harborInfo:
              'Du kan endast reservera en året-runt-förvaringsplats i Ämmäsmäki.'
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
            'Bokning av nya båtplatser för Esbo-invånare från och med 3.3. och för andra från 1.4.–30.9.2025',
          trailerReservations:
            'Bokning av trailerplatser i Finno för alla från 1.5.–31.12.2025',
          winter:
            'Bokning av nya vinterplatser för Esbo-invånare från 15.9.–31.12.2025',
          storage:
            'Bokning av förvaringsplatser i Ämmäsmäki för alla från 15.9.2025–31.7.2026'
        }
      },
      modal: {
        reserveNewSpace: 'Boka ny plats',
        reservingBoatSpace: 'Du håller på att boka en båtplats:',
        cannotReserveNewPlace:
          'Du har redan två båtplatser. Du kan inte boka en ny plats, men du kan byta din nuvarande plats.',
        currentPlaces: 'Din nuvarande plats',
        switchCurrentPlace: 'Byt min nuvarande plats',
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
      'Jag har läst och godkänner att följa <a target="_blank" href="https://www.espoo.fi/sv/bestammelser-bathamnar">hamnreglerna</a>. Bokningen ersätter hyresavtalet som nämns i hamnreglerna.',
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
      isActive: boolean
    ) => {
      if (validity === 'Indefinite' && isActive) {
        return 'Tills vidare, förnyas årligen'
      }
      return `Till ${endDate.format()}`
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
      return dueDate ? `Faktureras ${dueDate.format()}` : '-'
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
      'Du är på väg att lämna bokningsformuläret. Observera att platsbokningen eller inmatad information inte kommer att sparas.',
    cancelConfirmation2: 'Vill du fortsätta?',
    cancelReservation: 'Avbryt reservation',
    cancelAndGoBack: 'Avbryt och gå tillbaka',
    goBack: 'Gå tillbaka',
    continueToPaymentButton: 'Fortsätt till betalning',
    timer: {
      title:
        'Du har ${minutes} och ${seconds} på dig att bekräfta din båtplatsreservation genom att betala.',
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
    editBoatDetails: 'Redigera båtdetaljer',
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
      'Båtplatser har säkerhetsutrymmen för att förhindra skador på båtar och bryggor. En båt placerad på en plats som är för trång kan flyttas av kommunen, och ägaren av båtplatsen ansvarar för kostnaderna.'
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
      Buck: 'Förvaring på bock',
      None: '-'
    },
    winterStorageType: {
      Trailer: 'Trailerförvaring',
      Buck: 'Förvaring på bock',
      BuckWithTent: 'Förvaring på bock med skyddstält'
    },
    reserve: 'Boka'
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
          moveBoatImmediately:
            'Observera att du måste flytta din båt omedelbart från båtplatsen när du har sagt upp platsen.',
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
      reservationDate: 'Reservation gjord',
      place: 'Plats',
      reservationValidity: 'Reservationens giltighet',
      placeType: 'Platstyp',
      price: 'Pris',
      boatPresent: 'Båt närvarande',
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
