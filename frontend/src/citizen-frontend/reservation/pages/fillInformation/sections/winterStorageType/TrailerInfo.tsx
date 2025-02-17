import { Column, Columns } from 'lib-components/dom'
import { FormSection, NumberField, TextField } from 'lib-components/form'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { BoundForm, useFormFields } from 'lib-common/form/hooks'

import { TrailerInfoForm } from '../../formDefinitions/winterStorage'

export const TrailerInfo = React.memo(function TrailerInfo({
  bind
}: {
  bind: BoundForm<TrailerInfoForm>
}) {
  const i18n = useTranslation()
  const { registrationNumber, width, length } = useFormFields(bind)

  return (
    <FormSection>
      <Columns>
        <Column isOneQuarter>
          <TextField
            id="trailer-registration-number"
            label={i18n.reservation.formPage.trailerInfo.registrationNumber}
            required={true}
            bind={registrationNumber}
          />
        </Column>
        <Column isOneQuarter>
          <NumberField
            id="trailer-width"
            label={i18n.common.unit.dimensions.widthInMeters}
            bind={width}
            precision={2}
            required={true}
          />
        </Column>
        <Column isOneQuarter>
          <NumberField
            id="trailer-length"
            label={i18n.common.unit.dimensions.lengthInMeters}
            bind={length}
            precision={2}
            required={true}
          />
        </Column>
      </Columns>
    </FormSection>
  )
})
