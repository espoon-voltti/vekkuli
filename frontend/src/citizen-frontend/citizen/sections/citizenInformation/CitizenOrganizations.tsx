import { Column, Columns } from 'lib-components/dom'
import TextField from 'lib-components/form/TextField'
import React from 'react'
import { Link } from 'react-router'

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
            <Link
              className="link"
              to={`/kuntalainen/yhteiso/${organization.id}`}
            >
              <TextField
                label="Nimi"
                value={organization.name}
                readonly={true}
              />
            </Link>
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
