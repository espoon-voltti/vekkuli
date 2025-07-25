import { FormSection } from 'lib-components/form'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { BoundForm, useFormFields, useFormUnion } from 'lib-common/form/hooks'

import { WinterStorageForm } from '../../formDefinitions/winterStorage'

import StorageType from './StorageType'
import { TrailerInfo } from './TrailerInfo'

export default React.memo(function WinterStorageType({
  bind
}: {
  bind: BoundForm<WinterStorageForm>
}) {
  const i18n = useTranslation()
  const { storageType, trailerInfo } = useFormFields(bind)
  const { branch, form } = useFormUnion(trailerInfo)
  return (
    <FormSection data-testid="winter-storage-type">
      <h3 className="header">{i18n.reservation.formPage.storageType}</h3>
      <StorageType bind={storageType} />
      {branch === 'Trailer' && <TrailerInfo bind={form} />}
    </FormSection>
  )
})
