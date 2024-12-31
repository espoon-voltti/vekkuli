import { Column, Columns, Container } from 'lib-components/dom'
import TextField from 'lib-components/form/TextField'
import { EditLink } from 'lib-components/links'
import React from 'react'

import { User } from 'citizen-frontend/auth/state'

import { FormSection } from '../../../../lib-components/form'
import { Organization } from '../../../shared/types'

import CitizenOrganizations from './CitizenOrganizations'

export default React.memo(function CitizenInformation({
  user,
  organizations
}: {
  user: User
  organizations: Organization[]
}) {
  const [editMode, setEditMode] = React.useState(false)

  return (
    <Container isBlock>
      <FormSection>
        <Columns>
          <Column>
            <h3 className="header">Omat tiedot</h3>
          </Column>
          <Column isNarrow toRight>
            {editMode ? null : (
              <EditLink action={() => setEditMode(true)}>Muokkaa</EditLink>
            )}
          </Column>
        </Columns>
        <Columns>
          <Column isOneQuarter>
            <TextField label="Etunimi" value={user.firstName} readonly={true} />
          </Column>
          <Column isOneQuarter>
            <TextField label="Sukunimi" value={user.lastName} readonly={true} />
          </Column>
          <Column isOneQuarter>
            <TextField
              label="Kotiosoite"
              value={user.streetAddress}
              readonly={true}
            />
          </Column>
          <Column isOneEight>
            <TextField
              label="Postinumero"
              value={user.postalCode}
              readonly={true}
            />
          </Column>
          <Column isOneEight>
            <TextField
              label="Postitoimipaikka"
              value={user.postOffice}
              readonly={true}
            />
          </Column>
        </Columns>
        <Columns>
          <Column isOneQuarter>
            <TextField
              label="Kotikunta"
              value={user.municipalityName}
              readonly={true}
            />
          </Column>
          <Column isOneQuarter>
            <TextField
              label="Puhelinnumero"
              value={user.phone}
              readonly={!editMode}
            />
          </Column>
          <Column isOneQuarter>
            <TextField
              label="Sähköposti"
              value={user.email}
              readonly={!editMode}
            />
          </Column>
          <Column isOneEight>
            <TextField
              label="Henkilötunnus"
              value="210281-9988"
              readonly={true}
            />
          </Column>
        </Columns>
      </FormSection>
      {organizations.length > 0 && (
        <FormSection>
          <CitizenOrganizations organizations={organizations} />
        </FormSection>
      )}
    </Container>
  )
})
