import { CheckboxField } from 'lib-components/form/CheckboxField'
import React from 'react'

import { BoundForm, useFormFields } from 'lib-common/form/hooks'

import { UserAgreementForm } from '../formDefinitions'

export default React.memo(function UserAgreements({
  form
}: {
  form: BoundForm<UserAgreementForm>
}) {
  const { agreements } = useFormFields(form)
  return (
    <div className="form-section">
      <div id="agreements">
        <div className="columns">
          <div className="column">
            <CheckboxField
              id="user-agreements"
              name="userAgreements"
              bind={agreements}
              isFullWidth={true}
            />
          </div>
        </div>
      </div>
    </div>
  )
})
