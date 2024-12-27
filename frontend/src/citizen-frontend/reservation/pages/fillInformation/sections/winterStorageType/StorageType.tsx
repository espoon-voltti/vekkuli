import { RadioField } from 'lib-components/form/RadioField'
import React from 'react'

import { BoundForm } from 'lib-common/form/hooks'

import { StorageTypeForm } from '../../formDefinitions/winterStorage'

export default React.memo(function StorageType({
  bind
}: {
  bind: BoundForm<StorageTypeForm>
}) {
  return (
    <RadioField
      id="winter-storage-type"
      name="winterStorageType"
      bind={bind}
      noErrorContainer={true}
      horizontal={true}
    />
  )
})
