import { Column, Columns, Container } from 'lib-components/dom'
import { FormSection } from 'lib-components/form'
import TextField from 'lib-components/form/TextField'
import React from 'react'

import { Organization } from 'citizen-frontend/shared/types'

import { useTranslation } from '../../localization'
import { MemberListLoader } from '../organizationMembers'

const buildAddress = (
  address: string,
  postalCode: string,
  postOffice: string
) => {
  const addressParts = [address, postalCode, postOffice]
  return addressParts.filter((part) => part).join(', ')
}

export default React.memo(function OrganizationInformation({
  organization
}: {
  organization: Organization
}) {
  const i18n = useTranslation()
  const physicalAddress = buildAddress(
    organization.streetAddress || '',
    organization.postalCode || '',
    organization.postOffice || ''
  )
  return (
    <Container isBlock data-testid='organization-information'>
      <FormSection>
        <Columns>
          <Column>
            <h3 className="header">{i18n.organization.information.title}</h3>
          </Column>
        </Columns>
        <Columns>
          <Column isOneQuarter>
            <TextField
              label={i18n.organization.name}
              value={organization.name}
              readonly={true}
            />
          </Column>
          <Column isOneQuarter>
            <TextField
              label={i18n.organization.organizationId}
              value={organization.businessId}
              readonly={true}
            />
          </Column>
          <Column isOneQuarter>
            <TextField
              label={i18n.organization.municipality}
              value={organization.municipalityName || undefined}
              readonly={true}
            />
          </Column>
        </Columns>
        <Columns>
          <Column isOneQuarter>
            <TextField
              label={i18n.organization.contactDetails.fields.phone}
              value={organization.phone}
              readonly={true}
            />
          </Column>
          <Column isOneQuarter>
            <TextField
              label={i18n.organization.contactDetails.fields.email}
              value={organization.email}
              readonly={true}
            />
          </Column>
          <Column isOneQuarter>
            <TextField
              label={i18n.organization.physicalAddress}
              value={physicalAddress || undefined}
              readonly={true}
            />
          </Column>
        </Columns>
      </FormSection>
      <MemberListLoader orgId={organization.id} />
    </Container>
  )
})
