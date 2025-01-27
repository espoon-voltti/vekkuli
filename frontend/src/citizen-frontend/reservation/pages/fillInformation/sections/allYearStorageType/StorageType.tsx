import { Column, Columns } from 'lib-components/dom'
import { FormSection } from 'lib-components/form'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { BoundForm, useFormFields, useFormUnion } from 'lib-common/form/hooks'

import { AllYearStorageForm } from '../../formDefinitions/allYearStorage'
import StorageType from '../winterStorageType/StorageType'
import { TrailerInfo } from '../winterStorageType/TrailerInfo'

export default React.memo(function AllYearStorageType({
  bind
}: {
  bind: BoundForm<AllYearStorageForm>
}) {
  const i18n = useTranslation()
  const { storageInfo } = useFormFields(bind)
  const { branch, form } = useFormUnion(storageInfo)

  return (
    <FormSection data-testid="winter-storage-type">
      {branch === 'Trailer' ? (
        <h3 className="header">
          {i18n.reservation.formPage.trailerInfo.title}
        </h3>
      ) : (
        <h3 className="header">
          {i18n.reservation.formPage.storageTypeInfo.title}
        </h3>
      )}
      <Columns>
        {branch === 'Buck' && <StorageType bind={form} />}
        <Column>{branch === 'Trailer' && <TrailerInfo bind={form} />}</Column>
      </Columns>
    </FormSection>
  )
})
