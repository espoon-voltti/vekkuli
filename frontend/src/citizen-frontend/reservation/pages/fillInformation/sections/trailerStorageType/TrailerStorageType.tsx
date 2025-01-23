import { Column, Columns } from 'lib-components/dom'
import { FormSection } from 'lib-components/form'
import React from 'react'

import { BoundForm, useFormFields } from 'lib-common/form/hooks'

import { TrailerInfo } from '../winterStorageType/TrailerInfo'
import { TrailerStorageForm } from '../../formDefinitions/trailerStorage'
import { useTranslation } from '../../../../../localization'

export default React.memo(function TrailerSpaceType({
  bind
}: {
  bind: BoundForm<TrailerStorageForm>
}) {
  const i18n = useTranslation()
  const { trailerInfo } = useFormFields(bind)
  return (
    <FormSection data-testid="winter-storage-type">
      <h3 className="header">{i18n.reservation.formPage.trailerInfo.title}</h3>
      <Columns>
        <Column>
          <TrailerInfo bind={trailerInfo} />
        </Column>
      </Columns>
    </FormSection>
  )
})
