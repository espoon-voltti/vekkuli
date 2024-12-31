import { Column, Columns } from 'lib-components/dom'
import { FormSection, RadioField } from 'lib-components/form'
import React from 'react'

import { BoundForm } from 'lib-common/form/hooks'

import { BoatOwnershipTypeForm } from '../../formDefinitions/boat'

export default React.memo(function BoatOwnershipSatus({
  bind
}: {
  bind: BoundForm<BoatOwnershipTypeForm>
}) {
  return (
    <FormSection>
      <Columns>
        <Column isHalf>
          <RadioField
            id="boat-ownership-status"
            name="boatOwnershipStatus"
            label="Veneen omistussuhde Traficomin mukaan"
            bind={bind}
          />
        </Column>
      </Columns>
    </FormSection>
  )
})
