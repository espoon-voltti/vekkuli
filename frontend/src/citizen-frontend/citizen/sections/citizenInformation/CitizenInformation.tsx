import { Column, Columns, Container } from 'lib-components/dom'
import TextField from 'lib-components/form/TextField'
import { EditLink } from 'lib-components/links'
import React from 'react'

import { User } from 'citizen-frontend/auth/state'

export default React.memo(function Header({ user }: { user: User }) {
  const [editMode, setEditMode] = React.useState(false)

  return (
    <Container isBlock>
      <Columns>
        <Column isNarrow>
          <h3 className="header">
            {user.firstName} {user.lastName}
          </h3>
        </Column>
        <Column>
          {editMode ? null : (
            <EditLink action={() => setEditMode(true)}>
              Muokkaa asiakastietoja
            </EditLink>
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
    </Container>
  )
})
