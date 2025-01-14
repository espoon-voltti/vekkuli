import DateField from 'lib-components/form/DateField'
import TextField from 'lib-components/form/TextField'
import React from 'react'

import { Citizen } from 'citizen-frontend/shared/types'
import { BoundForm, useFormFields } from 'lib-common/form/hooks'

import { ReserverForm } from '../formDefinitions/reserver'

export default React.memo(function Reserver({
  reserver,
  bind
}: {
  reserver: Citizen | undefined
  bind: BoundForm<ReserverForm>
}) {
  if (!reserver) return null

  const { email, phone } = useFormFields(bind)
  return (
    <div className="form-section" data-testid="citizen">
      <h3 className="header">Varaaja</h3>
      <div className="columns">
        <div className="column is-one-quarter">
          <TextField
            id="firstName"
            label="Etunimi"
            value={reserver.firstName}
            readonly={true}
          />
        </div>
        <div className="column is-one-quarter">
          <TextField
            id="lastName"
            label="Sukunimi"
            value={reserver.lastName}
            readonly={true}
          />
        </div>
        <div className="column is-one-quarter">
          <DateField
            id="dateOfBirth"
            label="Syntymäaika"
            value={reserver.birthDate}
            readonly={true}
          />
        </div>
        <div className="column is-one-quarter">
          <TextField
            id="municipality"
            label="Kotikunta"
            value="Turku"
            readonly={true}
          />
        </div>
      </div>
      <div className="columns">
        <div className="column is-one-quarter">
          <TextField id="email" label="Sähköposti *" bind={email} />
        </div>
        <div className="column is-one-quarter">
          <TextField id="phone" label="Puhelinnumero *" bind={phone} />
        </div>
        <div className="column is-half">
          <TextField
            id="address"
            label="Katuosoite"
            value={`${reserver.address}, ${reserver.postalCode}, ${reserver.city}`}
            readonly={true}
          />
        </div>
      </div>
    </div>
  )
})
