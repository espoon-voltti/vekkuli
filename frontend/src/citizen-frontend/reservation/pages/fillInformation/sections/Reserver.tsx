import DateField from 'lib-components/form/DateField'
import TextField from 'lib-components/form/TextField'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
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
  const { email, phone } = useFormFields(bind)
  const i18n = useTranslation()

  if (!reserver) return null

  return (
    <div className="form-section" data-testid="citizen">
      <h3 className="header">{i18n.reservation.formPage.reserver}</h3>
      <div className="columns">
        <div className="column is-one-quarter">
          <TextField
            id="firstName"
            label={i18n.citizen.firstName}
            value={reserver.firstName}
            readonly={true}
          />
        </div>
        <div className="column is-one-quarter">
          <TextField
            id="lastName"
            label={i18n.citizen.lastName}
            value={reserver.lastName}
            readonly={true}
          />
        </div>
        <div className="column is-one-quarter">
          <DateField
            id="dateOfBirth"
            label={i18n.citizen.birthday}
            value={reserver.birthDate}
            readonly={true}
          />
        </div>
        <div className="column is-one-quarter">
          <TextField
            id="municipality"
            label={i18n.citizen.municipality}
            value={reserver.municipalityName}
            readonly={true}
          />
        </div>
      </div>
      <div className="columns">
        <div className="column is-one-quarter">
          <TextField
            id="email"
            label={i18n.citizen.email}
            bind={email}
            required
          />
        </div>
        <div className="column is-one-quarter">
          <TextField
            id="phone"
            label={i18n.citizen.phoneNumber}
            bind={phone}
            required
          />
        </div>
        <div className="column is-half">
          <TextField
            id="address"
            label={i18n.citizen.streetAddress}
            value={`${reserver.address}, ${reserver.postalCode}, ${reserver.municipalityName}`}
            readonly={true}
          />
        </div>
      </div>
    </div>
  )
})
