import { Column, Columns } from 'lib-components/dom'
import { FormSection } from 'lib-components/form'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { BoundForm, useFormFields } from 'lib-common/form/hooks'

import { TrailerStorageForm } from '../../formDefinitions/trailerStorage'
import { TrailerInfo } from '../winterStorageType/TrailerInfo'

export default React.memo(function TrailerSpaceType({
  bind
}: {
  bind: BoundForm<TrailerStorageForm>
}) {
  const i18n = useTranslation()
  const { trailerInfo } = useFormFields(bind)
  return (
    <FormSection data-testid="trailer-storage-type">
      <h3 className="header">{i18n.reservation.formPage.trailerInfo.title}</h3>
      <Columns>
        <Column>
          <TrailerInfo bind={trailerInfo} />
        </Column>
      </Columns>
    </FormSection>
  )
})
