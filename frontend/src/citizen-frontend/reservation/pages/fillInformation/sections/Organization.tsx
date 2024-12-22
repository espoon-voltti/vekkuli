import TextField from 'lib-components/form/TextField'
import React from 'react'

import { BoundForm, useFormFields, useFormUnion } from 'lib-common/form/hooks'

import { SelectField } from '../../../../../lib-components/form/SelectField'
import {
  existingOrganizationForm,
  newOrganizationForm,
  OrganisationUnionForm
} from '../formDefinitions/organization'

export default React.memo(function Organization({
  bind
}: {
  bind: BoundForm<OrganisationUnionForm>
}) {
  const { branch, form } = useFormUnion(bind)
  switch (branch) {
    case 'existing':
    case 'new':
      return <WithOrganization bind={form} />
  }
  return <NoOrganization />
})

const WithOrganization = React.memo(function Organization({
  bind
}: {
  bind: BoundForm<typeof newOrganizationForm | typeof existingOrganizationForm>
}) {
  const { details } = useFormFields(bind)
  const {
    name,
    businessId,
    phone,
    email,
    city,
    postalCode,
    address,
    municipality
  } = useFormFields(details)

  return (
    <div className="form-section">
      <div className="columns">
        <div className="column is-one-quarter">
          <TextField
            id="organization-name"
            label="Yhteisön nimi"
            required={true}
            bind={name}
          />
        </div>
        <div className="column is-one-quarter">
          <TextField
            id="organization-business-id"
            label="Y-tunnus"
            required={true}
            bind={businessId}
          />
        </div>
        <div className="column is-one-quarter">
          <SelectField
            id="organization-municipality-code"
            label="Kotikunta"
            required={true}
            bind={municipality}
          />
        </div>
      </div>
      <div className="columns">
        <div className="column is-one-quarter">
          <TextField
            id="organization-phone"
            label="Puhelinnumero"
            required={true}
            bind={phone}
          />
        </div>
        <div className="column is-one-quarter">
          <TextField
            id="organization-email"
            label="Sähköposti"
            required={true}
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

const NoOrganization = React.memo(function Organization() {
  return null
})
