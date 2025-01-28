import { Column } from 'lib-components/dom'
import { RadioField } from 'lib-components/form/RadioField'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { InfoBox } from 'citizen-frontend/reservation/components/InfoBox'
import { BoundForm } from 'lib-common/form/hooks'

import { StorageTypeForm } from '../../formDefinitions/winterStorage'

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
          text={i18n.reservation.formPage.storageInfo.buckWithTentInfo}
        />
      )}
    </Column>
  )
})
