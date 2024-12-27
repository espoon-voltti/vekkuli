import React from 'react'

import {
  BoundForm,
  useFormFields
} from '../../../../../../lib-common/form/hooks'
import { Column, Columns } from '../../../../../../lib-components/dom'
import { FormSection, TextField } from '../../../../../../lib-components/form'
import { TrailerInfoForm } from '../../formDefinitions/winterStorage'

export const TrailerInfo = React.memo(function TrailerInfo({
  bind
}: {
  bind: BoundForm<TrailerInfoForm>
}) {
  const { registrationNumber, width, length } = useFormFields(bind)

  return (
    <FormSection>
      <Columns>
        <Column isOneQuarter>
          <TextField
            id="trailer-registration-number"
            label="Rekisteritunnus"
            required={true}
            bind={registrationNumber}
          />
        </Column>
        <Column isOneQuarter>
          <TextField
            id="trailer-width"
            label="Leveys (m)"
            required={true}
            bind={width}
          />
        </Column>
        <Column isOneQuarter>
          <TextField
            id="trailer-length"
            label="Pituus (m)"
            required={true}
            bind={length}
          />
        </Column>
      </Columns>
    </FormSection>
  )
})
