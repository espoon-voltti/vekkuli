import { RadioField } from 'lib-components/form/RadioField'
import React from 'react'

import { BoundForm, useFormFields } from 'lib-common/form/hooks'

import { StorageTypeForm } from '../../formDefinitions/winterStorage'
import { InfoBox } from 'citizen-frontend/reservation/components/InfoBox'
import { useTranslation } from 'citizen-frontend/localization'
import { Column, Columns } from 'lib-components/dom'

export default React.memo(function StorageType({
  bind
}: {
  bind: BoundForm<StorageTypeForm>
}) {
  const i18n = useTranslation()
  const storageType = bind.state.domValue
  return (
    <Column isFull>
      <RadioField
        id="winter-storage-type"
        name="winterStorageType"
        bind={bind}
        noErrorContainer={true}
        horizontal={true}
      />
      {storageType === 'BuckWithTent' && (
        <InfoBox
          text={i18n.reservation.formPage.storageTypeInfo.buckWithTentInfo}
        />
      )}
    </Column>
  )
})
