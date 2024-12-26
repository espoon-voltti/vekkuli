import { RadioField } from 'lib-components/form/RadioField'
import React from 'react'

import { BoundForm } from 'lib-common/form/hooks'

import { BoatOwnershipTypeForm } from '../../formDefinitions/boat'

export default React.memo(function BoatOwnershipSatus({
  bind
}: {
  bind: BoundForm<BoatOwnershipTypeForm>
}) {
  return (
    <div className="form-section">
      <div id="shipHolder">
        <div className="columns">
          <div className="column">
            <RadioField
              id="boat-ownership-status"
              name="boatOwnershipStatus"
              label="Veneen omistussuhde Traficomin mukaan"
              bind={bind}
            />
          </div>
        </div>
      </div>
    </div>
  )
})
