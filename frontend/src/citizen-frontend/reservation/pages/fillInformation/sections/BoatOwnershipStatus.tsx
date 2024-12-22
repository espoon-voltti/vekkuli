import { RadioField } from 'lib-components/form/RadioField'
import React from 'react'

import { BoundForm, useFormFields } from 'lib-common/form/hooks'

import { BoatOwnershipTypeForm } from '../formDefinitions/boat'

export default React.memo(function BoatOwnershipSatus({
  form
}: {
  form: BoundForm<BoatOwnershipTypeForm>
}) {
  const { status } = useFormFields(form)
  return (
    <div className="form-section">
      <div id="shipHolder">
        <div className="columns">
          <div className="column">
            <RadioField
              id="boat-ownership-status"
              name="boatOwnershipStatus"
              label="Veneen omistussuhde Traficomin mukaan"
              bind={status}
            />
          </div>
        </div>
      </div>
    </div>
  )
})
