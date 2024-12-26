import { RadioField } from 'lib-components/form/RadioField'
import React from 'react'

import { BoundForm, useFormFields } from 'lib-common/form/hooks'

import { RenterTypeForm } from '../../formDefinitions/organization'

export default React.memo(function ReserverType({
  bind
}: {
  bind: BoundForm<RenterTypeForm>
}) {
  const { type } = useFormFields(bind)
  return (
    <RadioField
      id="reseverType"
      name="renterType"
      bind={type}
      noErrorContainer={true}
    />
  )
})
