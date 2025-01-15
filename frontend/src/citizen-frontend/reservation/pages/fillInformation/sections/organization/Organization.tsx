import { Column, Columns } from 'lib-components/dom'
import { FormSection } from 'lib-components/form'
import React from 'react'

import { BoundForm, useFormFields, useFormUnion } from 'lib-common/form/hooks'

import { OrganizationForm } from '../../formDefinitions/organization'

import OrganizationSelection from './OrganizationSelection'
import RenterType from './RenterType'
import { WithOrganization } from './WithOrganization'

export default React.memo(function Organization({
  bind
}: {
  bind: BoundForm<OrganizationForm>
}) {
  const { renterType, organizationSelection, organization } =
    useFormFields(bind)
  const { branch, form } = useFormUnion(organization)

  return (
    <FormSection data-testid="organization">
      <h3 className="header">Vuokralainen</h3>
      <Columns>
        <Column>
          <RenterType bind={renterType} />
          {branch !== 'noOrganization' && (
            <>
              <OrganizationSelection bind={organizationSelection} />
              <WithOrganization bind={form} />
            </>
          )}
        </Column>
      </Columns>
    </FormSection>
  )
})
