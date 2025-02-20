import { Column, Columns } from 'lib-components/dom'
import { FormSection, SelectField, TextField } from 'lib-components/form'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { BoundForm, useFormFields } from 'lib-common/form/hooks'

import { OrganizationInfoForm } from '../../formDefinitions/organization'

export const WithOrganization = React.memo(function Organization({
  bind
}: {
  bind: BoundForm<OrganizationInfoForm>
}) {
  const {
    name,
    businessId,
    phone,
    email,
    postOffice,
    postalCode,
    streetAddress,
    municipality
  } = useFormFields(bind)

  const i18n = useTranslation()

  return (
    <FormSection>
      <Columns>
        <Column isOneQuarter>
          <TextField
            id="organization-name"
            label={i18n.organization.information.name}
            readonly={true}
            bind={name}
          />
        </Column>
        <Column isOneQuarter>
          <TextField
            id="organization-business-id"
            label={i18n.organization.organizationId}
            readonly={true}
            bind={businessId}
          />
        </Column>
        <Column isOneQuarter>
          <SelectField
            id="organization-municipality-code"
            label={i18n.organization.municipality}
            readonly={true}
            bind={municipality}
          />
        </Column>
      </Columns>
      <Columns>
        <Column isOneQuarter>
          <TextField
            id="organization-phone"
            label={i18n.organization.contactDetails.fields.phone}
            required={true}
            bind={phone}
            ariaLabel={i18n.organization.information.phone}
          />
        </Column>
        <Column isOneQuarter>
          <TextField
            id="organization-email"
            label={i18n.organization.contactDetails.fields.email}
            required={true}
            bind={email}
            ariaLabel={i18n.organization.information.email}
          />
        </Column>
        <Column isOneQuarter>
          <TextField
            id="organization-address"
            label={i18n.organization.streetAddress}
            bind={streetAddress}
          />
        </Column>
        <Column isOneEight>
          <TextField
            id="organization-postal-code"
            label={i18n.organization.postalCode}
            bind={postalCode}
          />
        </Column>
        <Column isOneEight>
          <TextField
            id="organization-city"
            label={i18n.organization.postOffice}
            bind={postOffice}
          />
        </Column>
      </Columns>
    </FormSection>
  )
})
