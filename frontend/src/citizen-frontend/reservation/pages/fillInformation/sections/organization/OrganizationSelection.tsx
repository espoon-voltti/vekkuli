import { RadioField } from 'lib-components/form/RadioField'
import React from 'react'

import { BoundForm } from 'lib-common/form/hooks'

import { OrganizationSelectionForm } from '../../formDefinitions/organization'

export default React.memo(function OrganizationSelection({
  bind
}: {
  bind: BoundForm<OrganizationSelectionForm>
}) {
  return (
    <div className="ml-xl">
      <RadioField
        id="existing-organization"
        name="existingOrganization"
        bind={bind}
      />
    </div>
  )
})
