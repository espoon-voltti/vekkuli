import { MainSection } from 'lib-components/dom'
import React from 'react'

import { Lang, useLang } from 'citizen-frontend/localization'

export default React.memo(function AccessibilityStatementPage() {
  const [lang] = useLang()
  return (
    <MainSection>
      <div className="container">
        <div className="content">{translations[lang]}</div>
      </div>
    </MainSection>
  )
})

type Translation = Record<Lang, React.ReactNode>

const translations: Translation = {
  fi: (
    <>
      <h2>Saavutettavuusseloste</h2>
      <p>
        Tämä saavutettavuusseloste koskee Espoon kaupungin
        venepaikkavarausjärjestelmää osoitteessa{' '}
        <a href="https://varaukset.espoo.fi/">varaukset.espoo.fi</a>. Espoon
        kaupunki pyrkii takaamaan verkkopalvelun saavutettavuuden, parantamaan
        käyttäjäkokemusta jatkuvasti ja soveltamaan asianmukaisia
        saavutettavuusstandardeja.
      </p>
      <p>
        Palvelun saavutettavuuden on arvioinut palvelun kehitystiimi, ja seloste
        on laadittu 12.2.2025.
      </p>
      <h3>Palvelun vaatimustenmukaisuus</h3>
      <p>
        Verkkopalvelu täyttää lain asettamat kriittiset
        saavutettavuusvaatimukset WCAG v2.1 -tason AA mukaisesti. Palvelu ei ole
        vielä kaikilta osin vaatimusten mukainen.
      </p>
      <h3>Toimet saavutettavuuden tukemiseksi</h3>
      <p>
        Verkkopalvelun saavutettavuus varmistetaan muun muassa seuraavilla
        toimenpiteillä:
      </p>
      <ul>
        <li>
          Saavutettavuus huomioidaan alusta lähtien suunnitteluvaiheessa, mm.
          valitsemalla palvelun värit ja kirjaisinten koot saavutettavasti.
        </li>
        <li>
          Palvelun elementit on määritelty semantiikaltaan johdonmukaisesti.
        </li>
        <li>Palvelua testataan jatkuvasti ruudunlukijalla.</li>
        <li>
          Erilaiset käyttäjät testaavat palvelua ja antavat saavutettavuudesta
          palautetta.
        </li>
        <li>
          Sivuston saavutettavuudesta huolehditaan jatkuvalla valvonnalla
          tekniikan tai sisällön muuttuessa.
        </li>
      </ul>
      <p>
        Tätä selostetta päivitetään sivuston muutosten ja saavutettavuuden
        tarkistusten yhteydessä.
      </p>
      <h3>Tunnetut saavutettavuusongelmat</h3>
      <p>
        Käyttäjät saattavat edelleen kohdata sivustolla joitakin ongelmia. Jos
        huomaat sivustolla ongelman, otathan meihin yhteyttä.
      </p>
      <h3>Kolmannet osapuolet</h3>
      <p>
        Verkkopalvelussa käytetään seuraavia kolmannen osapuolen palveluita,
        joiden saavutettavuudesta emme voi vastata.
      </p>
      <ul>
        <li>Suomi.fi-tunnistautuminen</li>
      </ul>
      <h3>Vaihtoehtoiset asiointitavat</h3>
      <p>
        <a href="https://www.espoo.fi/fi/espoon-kaupunki/asiakaspalvelu/asiointipisteet-ja-espoo-info/asiointipisteet">
          Espoon kaupungin asiointipisteistä
        </a>{' '}
        saa apua sähköiseen asiointiin. Asiointipisteiden palveluneuvojat
        auttavat käyttäjiä, joille digipalvelut eivät ole saavutettavissa.
      </p>
      <h3>Anna palautetta</h3>
      <p>
        Jos huomaat saavutettavuuspuutteen verkkopalvelussamme, kerro siitä
        meille. Voit antaa palautetta{' '}
        <a href="https://easiointi.espoo.fi/eFeedback/fi/Feedback/20-S%C3%A4hk%C3%B6iset%20asiointipalvelut">
          verkkolomakkeella
        </a>
        .
      </p>
      <h3>Valvontaviranomainen</h3>
      <p>
        Jos huomaat sivustolla saavutettavuusongelmia, anna ensin palautetta
        meille sivuston ylläpitäjille. Vastauksessa voi mennä 14 päivää. Jos et
        ole tyytyväinen saamaasi vastaukseen, tai et saa vastausta lainkaan
        viikon aikana, voit antaa palautteen Etelä-Suomen aluehallintovirastoon.
        Etelä-Suomen aluehallintoviraston sivulla kerrotaan tarkasti, miten
        valituksen voi tehdä, ja miten asia käsitellään.
      </p>

      <p>
        <strong>Valvontaviranomaisen yhteystiedot </strong>
        <br />
        Etelä-Suomen aluehallintovirasto <br />
        Saavutettavuuden valvonnan yksikkö
        <br />
        <a href="https://www.saavutettavuusvaatimukset.fi">
          www.saavutettavuusvaatimukset.fi
        </a>
        <br />
        <a href="mailto:saavutettavuus@avi.fi">saavutettavuus@avi.fi</a>
        <br />
        puhelinnumero vaihde 0295 016 000
        <br />
        Avoinna: ma-pe klo 8.00–16.15
      </p>
    </>
  ),
  sv: (
    <>
      <h2>Tillgänglighetsutlåtande</h2>
      <p>
        Detta tillgänglighetsutlåtande gäller Esbo stads båtplatsbokningssystem
        på adressen <a href="https://varaukset.espoo.fi/">varaukset.espoo.fi</a>
        . Esbo stad strävar efter att säkerställa webbtjänstens tillgänglighet,
        kontinuerligt förbättra användarupplevelsen och att tillämpa lämpliga
        tillgänglighetsstandarder.
      </p>
      <p>
        Tjänstens tillgänglighet har bedömts av tjänsteutvecklingsteamet, och
        utlåtandet har utarbetats den 12 februari 2025.
      </p>
      <h3>Tjänstens överensstämmelse med krav</h3>
      <p>
        Webbtjänsten uppfyller de lagstadgade kritiska tillgänglighetskraven
        enligt nivå AA i WCAG 2.1. Tjänsten uppfyller ännu inte alla krav.
      </p>
      <h3>Åtgärder för att stödja tillgängligheten</h3>
      <p>
        Webbtjänstens tillgänglighet säkerställs bland annat genom följande
        åtgärder:
      </p>
      <ul>
        <li>
          Tillgängligheten beaktas redan från början i planeringsfasen till
          exempel genom att välja färgerna och fontstorleken i tjänsten med
          tillgängligheten i åtanke.
        </li>
        <li>Elementen i tjänsten har definierats semantiskt konsekvent.</li>
        <li>Tjänsten testas ständigt med en skärmläsare.</li>
        <li>
          Olika användare testar tjänsten och ger respons på tillgängligheten.
        </li>
        <li>
          Webbplatsens tillgänglighet säkerställs genom kontinuerliga kontroller
          vid tekniska eller innehållsmässiga förändringar.
        </li>
      </ul>
      <p>
        Detta utlåtande uppdateras när webbplatsen ändras eller tillgängligheten
        justeras.
      </p>
      <h3>Kända tillgänglighetsproblem</h3>
      <p>
        Användare kan fortfarande stöta på vissa problem på webbplatsen. Om du
        upptäcker ett problem, vänligen kontakta oss.
      </p>
      <h3>Tredje parter</h3>
      <p>
        Webbtjänsten använder följande tredjepartstjänster, vars tillgänglighet
        vi inte är ansvariga för.
      </p>
      <ul>
        <li>Tjänsten suomi.fi</li>
      </ul>
      <h3>Alternativa sätt att sköta ärenden</h3>
      <p>
        <a href="https://www.espoo.fi/sv/esbo-stad/kundservice/servicepunkterna-och-esbo-info/servicepunkterna">
          Esbo stads servicepunkter
        </a>{' '}
        hjälper till med användningen av e-tjänsterna. Rådgivarna vid
        servicepunkterna hjälper de användare, för vilka de digitala tjänsterna
        inte är tillgängliga.
      </p>
      <h3>Ge respons</h3>
      <p>
        er en tillgänglighetsbrist i vår webbtjänst, vänligen meddela oss. Du
        kan ge respons med{' '}
        <a href="https://easiointi.espoo.fi/eFeedback/sv/Feedback/20-S%C3%A4hk%C3%B6iset%20asiointipalvelut">
          webformuläret
        </a>
        .
      </p>
      <h3>Tillsynsmyndighet</h3>
      <p>
        Om du upptäcker tillgänglighetsproblem på webbplatsen, ge först respons
        till oss, webbplatsens administratörer. Det kan ta upp till 14 dagar
        tills du får ett svar från oss. Om du inte är nöjd med det svar du har
        fått eller om du inte alls har fått något svar inom två veckor, kan du
        ge respons till Regionförvaltningsverket i Södra Finland. På
        regionförvaltningsverkets webbplats finns information om hur du kan
        lämna in ett klagomål samt om hur ärendet handläggs.
      </p>
      <p>
        <strong>Kontaktuppgifter till tillsynsmyndigheten</strong>
        <br />
        Regionförvaltningsverket i Södra Finland
        <br />
        Enheten för tillgänglighetstillsyn
        <br />
        <a href="https://www.tillganglighetskrav.fi">
          www.tillganglighetskrav.fi
        </a>
        <br />
        <a href="mailto:saavutettavuus@avi.fi">saavutettavuus@avi.fi</a>
        <br />
        telefonnummer till växeln 0295 016 000
        <br />
        Öppet mån.– fre. kl. 8.00–16.15
      </p>
    </>
  ),
  en: (
    <>
      <h2>Accessibility statement</h2>
      <p>
        This accessibility statement applies to the City of Espoo’s boat berth
        reservation system at{' '}
        <a href="https://varaukset.espoo.fi/">varaukset.espoo.fi</a>. The City
        of Espoo endeavours to ensure the accessibility of the online service,
        continuously improve the user experience and apply appropriate
        accessibility standards.
      </p>
      <p>
        The accessibility of the service was assessed by the development team of
        the service, and this statement was drawn up on 12 February 2025.
      </p>
      <h3>Compliance of the service</h3>
      <p>
        The online service complies with the statutory critical accessibility
        requirements in accordance with Level AA of the Accessibility Guidelines
        for the WCAG v2.1. The service is not yet fully compliant with the
        requirements.
      </p>
      <h3>Measures to support accessibility</h3>
      <p>
        The accessibility of the online service is ensured, among other things,
        by the following measures:
      </p>
      <ul>
        <li>
          Accessibility has been taken into account from the beginning of the
          design phase, for example, when choosing the colours and font sizes of
          the service.
        </li>
        <li>
          The service elements have been defined in consistently in terms of
          semantics.
        </li>
        <li>The service is continuously tested with a screen reader.</li>
        <li>
          Various users test the service and give feedback on its accessibility.
        </li>
        <li>
          When website technology or content changes, its accessibility is
          ensured through constant monitoring.
        </li>
      </ul>
      <p>
        This statement will be updated in conjunction with website changes and
        accessibility evaluations.
      </p>
      <h3>Known accessibility issues</h3>
      <p>
        Users may still encounter some issues on the website. If you notice an
        issue on the site, please contact us.
      </p>
      <h3>Third parties</h3>
      <p>
        The online service uses the following third party services, the
        accessibility of which we cannot be responsible for.
      </p>
      <ul>
        <li>Suomi.fi identification</li>
      </ul>
      <h3>Alternative ways of accessing the service</h3>
      <p>
        <a href="https://www.espoo.fi/en/city-espoo/customer-service/service-points-and-espoo-info/service-points">
          The City of Espoo’s Service Points
        </a>{' '}
        provide assistance with using electronic services. Service advisors at
        the Service Points help users who cannot access digital services.
      </p>
      <h3>Give feedback</h3>
      <p>
        If you notice an accessibility gap in our online service, please let us
        know! You can give us feedback using the{' '}
        <a href="https://easiointi.espoo.fi/eFeedback/en/Feedback/20-S%C3%A4hk%C3%B6iset%20asiointipalvelut">
          online form
        </a>
        .
      </p>
      <h3>Supervisory authority</h3>
      <p>
        If you notice any accessibility issues on the website, please send us,
        the site administrator, feedback first. It may take us up to 14 days to
        reply. If you are not satisfied with the reply or you do not receive a
        reply within two weeks, you can give feedback to the Regional State
        Administrative Agency for Southern Finland. The website of the Regional
        State Administrative Agency for Southern Finland explains in detail how
        a complaint can be submitted, and how the matter will be processed.
      </p>

      <p>
        <strong>Contact information of the supervisory authority</strong>
        <br />
        Regional State Administrative Agency of Southern Finland
        <br />
        Accessibility Supervision Unit
        <br />
        <a href="https://www.saavutettavuusvaatimukset.fi">
          www.saavutettavuusvaatimukset.fi
        </a>
        <br />
        <a href="mailto:saavutettavuus@avi.fi">saavutettavuus@avi.fi</a>
        <br />
        tel. (exchange) 0295 016 000
        <br />
        Open: Mon-Fri 8.00–16.15
      </p>
    </>
  )
}
