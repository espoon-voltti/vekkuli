import { Button, Buttons, Column, Columns } from 'lib-components/dom'
import { NumberField, TextField } from 'lib-components/form'
import { EditLink } from 'lib-components/links'
import React from 'react'

import { UpdateTrailerRequest } from 'citizen-frontend/api-clients/trailer'
import { useTranslation } from 'citizen-frontend/localization'
import { Trailer } from 'citizen-frontend/shared/types'
import { useForm, useFormFields } from 'lib-common/form/hooks'
import { MutationDescription, useMutation } from 'lib-common/query'

import { initialFormState, trailerInformationForm } from './formDefinitions'
import { updateTrailerDisabled } from './queries'

export default React.memo(function TrailerInformation({
  trailer,
  setEditIsOn,
  updateMutation = updateTrailerDisabled
}: {
  trailer: Trailer
  setEditIsOn?: (value: boolean) => void
  updateMutation?: MutationDescription<UpdateTrailerRequest, void>
}) {
  const editDisabled = updateMutation === updateTrailerDisabled
  const i18n = useTranslation()
  const [editMode, setEditMode] = React.useState(false)
  const { mutateAsync: updateTrailer, isPending } = useMutation(updateMutation)
  const form = useForm(
    trailerInformationForm,
    () => initialFormState(trailer),
    i18n.components.validationErrors
  )
  const cancel = () => {
    form.set(initialFormState(trailer))
    changeEditMode(false)
  }
  const changeEditMode = (mode: boolean) => {
    setEditMode(mode)
    setEditIsOn?.(mode)
  }
  const onSubmit = async () => {
    if (form.isValid()) {
      await updateTrailer({ trailerId: trailer.id, input: form.value() })
      setEditMode(false)
      setEditIsOn?.(false)
    }
  }

  const { registrationNumber, length, width } = useFormFields(form)
  return (
    <div className="trailer-information" data-testid="trailer-information">
      <Columns isVCentered>
        <Column isNarrow>
          <h4>{i18n.reservation.formPage.trailerInfo.title}</h4>
        </Column>
        <Column isNarrow toRight>
          {editMode || editDisabled ? null : (
            <EditLink action={() => changeEditMode(true)}>
              {i18n.reservation.formPage.trailerInfo.editTrailerDetails}
            </EditLink>
          )}
        </Column>
      </Columns>
      <Columns>
        <Column isOneQuarter>
          <TextField
            label={i18n.reservation.formPage.trailerInfo.registrationNumber}
            bind={registrationNumber}
            readonly={!editMode}
          />
        </Column>
        <Column isOneQuarter>
          <NumberField
            label={i18n.common.unit.dimensions.widthInMeters}
            bind={width}
            readonly={!editMode}
            precision={2}
          />
        </Column>
        <Column isOneQuarter>
          <NumberField
            label={i18n.common.unit.dimensions.lengthInMeters}
            bind={length}
            readonly={!editMode}
            precision={2}
          />
        </Column>
      </Columns>
      {editMode && (
        <Buttons>
          <Button action={cancel} loading={isPending}>
            {i18n.common.cancel}
          </Button>
          <Button action={onSubmit} type="primary" loading={isPending}>
            {i18n.common.saveChanges}
          </Button>
        </Buttons>
      )}
    </div>
  )
})
