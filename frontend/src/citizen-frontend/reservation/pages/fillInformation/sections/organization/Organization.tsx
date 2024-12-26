import { Column, Columns } from 'lib-components/dom'
import { FormSection } from 'lib-components/form'
import React from 'react'

import { BoundForm, useFormUnion } from 'lib-common/form/hooks'

import {
  OrganisationUnionForm,
  OrganizationSelectionForm,
  RenterTypeForm
} from '../../formDefinitions/organization'

import OrganizationSelection from './OrganizationSelection'
import RenterType from './RenterType'
import { WithOrganization } from './WithOrganization'

export default React.memo(function Organization({
  renterTypeBind,
  organizationSelectionBind,
  organizationBind
}: {
  organizationBind: BoundForm<OrganisationUnionForm>
  renterTypeBind: BoundForm<RenterTypeForm>
  organizationSelectionBind: BoundForm<OrganizationSelectionForm>
}) {
  const { branch, form } = useFormUnion(organizationBind)
  return (
    <FormSection>
      <h3 className="header">Vuokralainen</h3>
      <Columns>
        <Column>
          <RenterType bind={renterTypeBind} />
          {branch !== 'noOrganization' && (
            <>
              <OrganizationSelection bind={organizationSelectionBind} />
              <WithOrganization bind={form} />
            </>
          )}
        </Column>
      </Columns>
    </FormSection>
  )
})
