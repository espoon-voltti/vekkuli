import { Column, Columns } from 'lib-components/dom'
import { FormSection } from 'lib-components/form'
import React from 'react'

import { BoundForm, useFormFields, useFormUnion } from 'lib-common/form/hooks'

import { WinterStorageForm } from '../../formDefinitions/winterStorage'

import StorageType from './StorageType'
import { TrailerInfo } from './TrailerInfo'

export default React.memo(function Organization({
  bind
}: {
  bind: BoundForm<WinterStorageForm>
}) {
  const { storageType, trailerInfo } = useFormFields(bind)
  const { branch, form } = useFormUnion(trailerInfo)
  return (
    <FormSection>
      <h3 className="header">Säilytystapa</h3>
      <Columns>
        <Column>
          <StorageType bind={storageType} />
          {branch === 'Trailer' && <TrailerInfo bind={form} />}
        </Column>
      </Columns>
    </FormSection>
  )
})
