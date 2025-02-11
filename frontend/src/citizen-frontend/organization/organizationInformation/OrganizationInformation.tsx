import { Column, Columns, Container } from 'lib-components/dom'
import { FormSection } from 'lib-components/form'
import TextField from 'lib-components/form/TextField'
import React from 'react'

import { Organization } from 'citizen-frontend/shared/types'

import { useTranslation } from '../../localization'
import { MemberListLoader } from '../organizationMembers'

export default React.memo(function OrganizationInformation({
  organization
}: {
  organization: Organization
}) {
  const i18n = useTranslation()
  return (
    <Container isBlock>
      <FormSection>
        <Columns>
          <Column>
            <h3 className="header">{i18n.organization.information.title}</h3>
          </Column>
        </Columns>
        <Columns>
          <Column isOneQuarter>
            <TextField
              label="Etunimi"
              value={organization.name}
              readonly={true}
            />
          </Column>

          <Column isOneQuarter>
            <TextField
              label="Kotiosoite"
              value={organization.streetAddress || undefined}
              readonly={true}
            />
          </Column>
          <Column isOneEight>
            <TextField
              label="Postinumero"
              value={organization.postalCode || undefined}
              readonly={true}
            />
          </Column>
          <Column isOneEight>
            <TextField
              label="Postitoimipaikka"
              value={organization.postOffice || undefined}
              readonly={true}
            />
          </Column>
        </Columns>
        <Columns>
          <Column isOneQuarter>
            <TextField
              label="Kotikunta"
              value={organization.municipalityName || undefined}
              readonly={true}
            />
          </Column>
          <Column isOneQuarter>
            <TextField
              label="Puhelinnumero"
              value={organization.phone}
              readonly={true}
            />
          </Column>
          <Column isOneQuarter>
            <TextField
              label="Sähköposti"
              value={organization.email}
              readonly={true}
            />
          </Column>
          <Column isOneEight>
            <TextField
              label="Y-Tunnus"
              value={organization.businessId}
              readonly={true}
            />
          </Column>
        </Columns>
      </FormSection>
      <MemberListLoader orgId={organization.id} />
    </Container>
  )
})
