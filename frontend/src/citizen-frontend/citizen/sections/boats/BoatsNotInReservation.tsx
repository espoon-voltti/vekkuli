import { CheckboxField } from 'lib-components/form/CheckboxField'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { Boat } from 'citizen-frontend/shared/types'
import { useForm, useFormFields } from 'lib-common/form/hooks'

import BoatComponent from './Boat'
import { initShowBoatsForm, showBoatsForm } from './formDefinitions'

export default React.memo(function BoatsNotInreservations({
  boats
}: {
  boats: Boat[]
}) {
  const i18n = useTranslation()

  const bind = useForm(
    showBoatsForm,
    () => initShowBoatsForm(i18n),
    i18n.components.validationErrors
  )
  const { show } = useFormFields(bind)

  if (!boats || !boats.length) {
    return null
  }

  return (
    <div>
      <div className="pb-l">
        <CheckboxField id="show-boats" name="showBoats" bind={show} />
      </div>
      {!!show.value()?.length && (
        <div className="reservation-list form-section no-bottom-border">
          {boats.map((boat) => (
            <BoatComponent key={boat.id} boat={boat} />
          ))}
        </div>
      )}
    </div>
  )
})
