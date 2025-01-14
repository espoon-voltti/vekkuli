import { Column, Columns } from 'lib-components/dom'
import { CheckboxField, FormSection } from 'lib-components/form'
import React from 'react'

import { BoundForm, useFormFields } from 'lib-common/form/hooks'

import { UserAgreementForm } from '../formDefinitions/userAgreement'

export default React.memo(function UserAgreements({
  bind
}: {
  bind: BoundForm<UserAgreementForm>
}) {
  const { certified, terms } = useFormFields(bind)
  return (
    <FormSection data-testid="user-agreement">
      <Columns isMultiline>
        <Column isFull>
          <CheckboxField
            id="user-agreements-certified"
            name="certified"
            bind={certified}
            isFullWidth={true}
          />
        </Column>
        <Column isFull>
          <CheckboxField
            id="user-agreements-terms"
            name="terms"
            bind={terms}
            isFullWidth={true}
          />
        </Column>
      </Columns>
    </FormSection>
  )
})
