import { MainSection } from 'lib-components/dom'
import React from 'react'
import { Link } from 'react-router'

import { useTranslation } from 'citizen-frontend/localization'
import { BoatSpaceType } from 'citizen-frontend/shared/types'
import MapImage from 'lib-customizations/vekkuli/assets/map-of-locations.png'
import {
  Authorisation,
  Boat,
  ContactSupport,
  InfoCircle,
  MapMarker, Preparations
} from 'lib-icons'

export default React.memo(function HomePage() {
  const i18n = useTranslation()
  return (
    <MainSection>
      <div className="container">
        <h2>{i18n.citizenFrontPage.title}</h2>
        <div className="columns">
          <div className="column is-three-fifths">
            <InfoColumn
              icon={MapMarker}
              text={i18n.citizenFrontPage.info.locations}
            />
            <InfoColumn
              icon={Authorisation}
              text={i18n.citizenFrontPage.info.authenticationRequired}
            />
            <InfoColumn
              icon={Boat}
              text={i18n.citizenFrontPage.info.boatRequired}
            />
            <InfoColumn
              icon={ContactSupport}
              text={i18n.citizenFrontPage.info.contactInfo}
            />
            <InfoColumn
                icon={Preparations}
                text={i18n.citizenFrontPage.info.preparations}
            />
            <InfoColumn
              icon={InfoCircle}
              text={i18n.citizenFrontPage.info.readMore}
            />
          </div>
          <div className="column is-align-content-center">
            <img
              src={MapImage}
              alt={i18n.citizenFrontPage.image.harbors.altText}
            />
          </div>
        </div>
        <div className="block">
          <Link className="button is-primary" to="/kuntalainen/venepaikka">
            {i18n.citizenFrontPage.button.browseBoatSpaces}
          </Link>
        </div>
      </div>
      <div className="container">
        <div className="container is-highlight">
          {boatSpaceSeasons.map((period, index) => (
            <Period key={`period-${index}`} {...period} />
          ))}
          <p>{i18n.citizenFrontPage.periods.footNote}</p>
          <p>{i18n.citizenFrontPage.periods.footNote2}</p>
        </div>
      </div>
    </MainSection>
  )
})

interface InfoColumnProps {
  icon: () => React.JSX.Element
  text: string
}

const InfoColumn = React.memo(function InfoColumn({
  icon,
  text
}: InfoColumnProps) {
  return (
    <div className="columns">
      <div className="column is-narrow" aria-hidden="true">
        <span className="icon is-medium">{icon()}</span>
      </div>
      <div className="column">
        <p>{text}</p>
      </div>
    </div>
  )
})

const Period = React.memo(function Period({
  boatSpaceType,
  season,
  periods
}: BoatSpaceSeason) {
  const i18n = useTranslation()
  return (
    <>
      <h2 className="has-text-weight-semibold">
        {i18n.citizenFrontPage.periods[boatSpaceType].title}
      </h2>
      <h3 className="label">
        {i18n.citizenFrontPage.periods[boatSpaceType].season(season)}
      </h3>
      <div className="mb-m">
        {periods.map((period, index) => (
          <p className="block" key={`period-${index}`}>
            {i18n.citizenFrontPage.periods[boatSpaceType].periods[index](
              period
            )}
          </p>
        ))}
      </div>
    </>
  )
})

interface BoatSpaceSeason {
  boatSpaceType: BoatSpaceType
  season: string
  periods: string[] // period items map into translations in i18n files
}
const boatSpaceSeasons: BoatSpaceSeason[] = [
  {
    boatSpaceType: 'Slip',
    season: '10.6.–14.9.2025',
    periods: ['7.1.–31.1.2025', '3.3.–31.3.2025', '1.4-30.9.2025']
  },
  {
    boatSpaceType: 'Trailer',
    season: '1.5.–30.4.2025',
    periods: ['1.4.–30.4.2025', '1.5.–31.12.2025']
  },
  {
    boatSpaceType: 'Winter',
    season: '15.9.–10.6.2025',
    periods: ['15.8.–14.9.2025', '15.9.–31.12.2025']
  },
  {
    boatSpaceType: 'Storage',
    season: '15.9.–14.9.2025',
    periods: ['15.8.–14.9.2025', '15.9.–31.12.2025']
  }
]
