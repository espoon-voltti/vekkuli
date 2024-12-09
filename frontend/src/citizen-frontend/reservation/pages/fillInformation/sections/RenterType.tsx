import { RadioField } from 'lib-components/form/RadioField'
import React from 'react'

import { BoundForm, useFormFields } from 'lib-common/form/hooks'

import { RenterTypeForm } from '../formDefinitions'

export default React.memo(function ReserverType({
  form
}: {
  form: BoundForm<RenterTypeForm>
}) {
  const { type } = useFormFields(form)
  return (
    <div className="form-section">
      <div id="shipHolder">
        <h3 className="header">Vuokralainen</h3>
        <div className="columns">
          <div className="column is-one-quarter">
            <RadioField id="reseverType" name="renterType" bind={type} />
          </div>
        </div>
      </div>
    </div>
  )
})
