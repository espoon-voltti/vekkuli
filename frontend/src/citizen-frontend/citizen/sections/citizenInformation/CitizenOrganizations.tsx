import { Column, Columns } from 'lib-components/dom'
import TextField from 'lib-components/form/TextField'
import React from 'react'

import { Organization } from 'citizen-frontend/shared/types'

export default React.memo(function CitizenInformation({
  organizations
}: {
  organizations: Organization[]
}) {
  return (
    <>
      <h4 className="mb-1 has-text-left">Yhteisöt</h4>
      {organizations.map((organization) => (
        <Columns key={`organization-${organization.id}`}>
          <Column isOneQuarter>
            <TextField label="Nimi" value={organization.name} readonly={true} />
          </Column>
          <Column isOneQuarter>
            <TextField
              label="Y-tunnus"
              value={organization.businessId}
              readonly={true}
            />
          </Column>
        </Columns>
      ))}
    </>
  )
})
