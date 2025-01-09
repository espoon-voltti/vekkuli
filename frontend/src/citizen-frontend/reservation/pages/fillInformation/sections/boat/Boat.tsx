import React from 'react'

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
  const { boatInfo, boatSelection, ownership } = useFormFields(bind)

  return (
    <>
      <div className="form-section">
        <h3 className="header">Veneen tiedot</h3>
        <ExistingBoat bind={boatSelection} />
        <BoatInfo bind={boatInfo} />
      </div>
      <BoatOwnershipStatus bind={ownership} />
    </>
  )
})
