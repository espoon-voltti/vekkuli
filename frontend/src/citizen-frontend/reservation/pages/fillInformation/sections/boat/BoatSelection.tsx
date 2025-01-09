import { RadioField } from 'lib-components/form/RadioField'
import React from 'react'

import { BoundForm } from 'lib-common/form/hooks'

import { BoatSelectionForm } from '../../formDefinitions/boat'

export default React.memo(function ExistingBoatSelection({
  bind
}: {
  bind: BoundForm<BoatSelectionForm>
}) {
  if (!bind.state.options.length) return null
  return <RadioField id="boat-select" name="boatSelect" bind={bind} />
})
