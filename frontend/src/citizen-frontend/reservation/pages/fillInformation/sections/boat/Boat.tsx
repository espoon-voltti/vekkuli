import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { BoundForm, useFormFields } from 'lib-common/form/hooks'

import { BoatForm } from '../../formDefinitions/boat'

import BoatInfo from './BoatInfo'
import BoatOwnershipStatus from './BoatOwnershipStatus'
import ExistingBoat from './BoatSelection'

export default React.memo(function Boat({
  bind
}: {
  bind: BoundForm<BoatForm>
}) {
  const i18n = useTranslation()
  const { boatInfo, boatSelection, ownership } = useFormFields(bind)

  return (
    <div data-testid="boat">
      <div className="form-section">
        <h3 className="header">{i18n.reservation.formPage.boatInformation}</h3>
        <ExistingBoat bind={boatSelection} />
        <BoatInfo bind={boatInfo} />
      </div>
      <BoatOwnershipStatus bind={ownership} />
    </div>
  )
})
