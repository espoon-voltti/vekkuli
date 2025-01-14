import { MainSection } from 'lib-components/dom'
import React from 'react'

import MapImage from 'lib-customizations/vekkuli/assets/map-of-locations.png'
import {
  Authorisation,
  Boat,
  ContactSupport,
  InfoCircle,
  MapMarker
} from 'lib-icons'

export default React.memo(function HomePage() {
  return (
    <MainSection>
      <div className="container">
        <h2>Venepaikat</h2>
        <div className="columns">
          <div className="column is-three-fifths">
            <InfoColumn
              icon={MapMarker}
              textKey="citizenFrontPage.info.locations"
            />
            <InfoColumn
              icon={Authorisation}
              textKey="citizenFrontPage.info.authenticationRequired"
            />
            <InfoColumn
              icon={Boat}
              textKey="citizenFrontPage.info.boatRequired"
            />
            <InfoColumn
              icon={ContactSupport}
              textKey="citizenFrontPage.info.contactInfo"
            />
            <InfoColumn
              icon={InfoCircle}
              textKey="citizenFrontPage.info.readMore"
            />
          </div>
          <div className="column is-align-content-center">
            <img src={MapImage} alt="Satamat" />
          </div>
        </div>
        <div className="block">
          <a className="button is-primary" href="/kuntalainen/venepaikka">
            Selaile vapaita venepaikkoja
          </a>
        </div>
      </div>
      <div className="container">
        <div className="container is-highlight">
          {periods.map((period, index) => (
            <Period key={`period-${index}`} {...period} />
          ))}
          <p>
            *Jos vene on yhteisomistuksessa ja yli 50% veneen omistajista asuu
            Espoossa, voitte varata vene-, talvi- tai säilytyspaikan
            espoolaisena. Jonkun Espoossa asuvista on tällöin tehtävä varaus.
          </p>
        </div>
      </div>
    </MainSection>
  )
})

interface InfoColumnProps {
  icon: () => React.JSX.Element
  textKey: string
}

const InfoColumn = React.memo(function InfoColumn({
  icon,
  textKey
}: InfoColumnProps) {
  return (
    <div className="columns">
      <div className="column is-narrow">
        <span className="icon is-medium">{icon()}</span>
      </div>
      <div className="column">
        <p>{getKeyText(textKey)}</p>
      </div>
    </div>
  )
})

interface PeriodProps {
  title: string
  season: string
  periods: string[]
}

const Period = React.memo(function Period({
  title,
  season,
  periods
}: PeriodProps) {
  return (
    <>
      <h2 className="has-text-weight-semibold">{title}</h2>
      <h3 className="label">{season}</h3>
      <div className="mb-m">
        {periods.map((period, index) => (
          <p className="block" key={`period-${index}`}>
            {period}
          </p>
        ))}
      </div>
    </>
  )
})

const getKeyText = (key: string) => {
  switch (key) {
    case 'citizenFrontPage.info.locations':
      return 'Varattavia laituripaikkoja löytyy seuraavista satamista: Haukilahti, Kivenlahti, Laajalahti, Otsolahti, Soukka, Suomenoja ja Svinö. Talvipaikkoja on varattavissa Laajalahdessa, Otsolahdessa ja Suomenojalla sekä ympärivuotisia säilytyspaikkoja Ämmäsmäellä.'
    case 'citizenFrontPage.info.authenticationRequired':
      return 'Paikan varaaminen vaatii vahvan tunnistautumisen ja venepaikka maksetaan varaamisen yhteydessä.'
    case 'citizenFrontPage.info.boatRequired':
      return 'Vain veneen omistaja tai haltija voi varata vene-, talvi-, tai säilytyspaikkoja. Pidä huoli, että tiedot ovat oikein Traficomin venerekisterissä.'
    case 'citizenFrontPage.info.contactInfo':
      return 'Jos et voi tunnistautua sähköisesti, ota yhteyttä sähköpostilla venepaikat@espoo.fi tai puhelimitse 09 81658984 ma ja ke klo 12.30-15 ja to 9-11. Kerääthän valmiiksi varausta varten seuraavat tiedot: varaajan henkilötunnus, nimet, osoite ja sähköpostiosoite, veneen leveys, pituus ja paino, veneen nimi tai muu tunniste.'
    case 'citizenFrontPage.info.readMore':
      return 'Lisätietoja venesatamista, venepaikkamaksuista ja veneiden säilytyksestä löydät täältä.'
    default:
      return ''
  }
}

const periods: PeriodProps[] = [
  {
    title: 'Venepaikkojen varaaminen 2026',
    season: 'Veneilykausi 10.6.–14.9.2026',
    periods: [
      '7.1.–31.1.2026 vain espoolaiset* toistaiseksi voimassa olevan paikan vuokraajat voivat jatkaa venepaikansa vuokrausta',
      '1.3.–30.9.2026 espoolaiset* voivat varata venepaikkoja',
      '1.4.–30.9.2026 kaikki voivat varata venepaikkoja'
    ]
  },
  {
    title: 'Suomenojan traileripaikkojen varaaminen 2026',
    season:
      'Vuokrakausi 1.5.2026–30.4.2027. Vene trailerilla, vesillelasku luiskalta.',
    periods: [
      '1.4.–30.4.2026 vain espoolaiset* toistaiseksi voimassa olevan paikan vuokraajat voivat jatkaa traileripaikan vuokrausta',
      '1.5.–31.12.2026 kaikki voivat varata traileripaikkoja'
    ]
  }
]
