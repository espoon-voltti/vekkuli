import TextField from 'lib-components/form/TextField'
import React from 'react'

import { BoundForm, useFormFields } from 'lib-common/form/hooks'

import { NumberField } from '../../../../../lib-components/form/NumberField'
import { OrganizationForm } from '../formDefinitions'

export default React.memo(function Organization({
  form
}: {
  form: BoundForm<OrganizationForm>
}) {
  const { name, businessId, phone, email, city, postalCode, address } =
    useFormFields(form)
  return (
    <div className="form-section">
      <div className="columns">
        <div className="column is-one-quarter">
          <TextField
            id="organization-name"
            label="Yhteisön nimi *"
            bind={name}
          />
        </div>
        <div className="column is-one-quarter">
          <TextField
            id="organization-business-id"
            label="Y-tunnus *"
            bind={businessId}
          />
        </div>
        <div className="column is-one-quarter">
          <NumberField
            id="organization-municipality-code"
            label="Kotikunta *"
            value="n/a"
            readonly={true}
          />
        </div>
      </div>
      <div className="columns">
        <div className="column is-one-quarter">
          <TextField
            id="organization-phone"
            label="Puhelinnumero *"
            bind={phone}
          />
        </div>
        <div className="column is-one-quarter">
          <TextField
            id="organization-email"
            label="Sähköposti *"
            bind={email}
          />
        </div>
        <div className="column is-one-quarter">
          <TextField
            id="organization-address"
            label="Katuosoite"
            bind={address}
          />
        </div>
        <div className="column is-one-eight">
          <TextField
            id="organization-postal-code"
            label="Postinumero"
            bind={postalCode}
          />
        </div>
        <div className="column is-one-eight">
          <TextField
            id="organization-city"
            label="Postitoimipaikka"
            bind={city}
          />
        </div>
      </div>
    </div>
  )
})
