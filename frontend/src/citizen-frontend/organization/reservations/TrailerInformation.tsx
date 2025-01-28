import { Button, Buttons, Column, Columns } from 'lib-components/dom'
import { NumberField } from 'lib-components/form'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { Trailer } from 'citizen-frontend/shared/types'
import { useForm, useFormFields } from 'lib-common/form/hooks'
import { useMutation } from 'lib-common/query'

import TextField from '../../../lib-components/form/TextField'
import { EditLink } from '../../../lib-components/links'
import { updateTrailerInformationMutation } from '../queries'

import { initialFormState, trailerInformationForm } from './formDefinitions'

export default React.memo(function TrailerInformation({
  trailer,
  setEditIsOn
}: {
  trailer: Trailer
  setEditIsOn?: (value: boolean) => void
}) {
  const i18n = useTranslation()
  const [editMode, setEditMode] = React.useState(false)
  const { mutateAsync: updateTrailer, isPending } = useMutation(
    updateTrailerInformationMutation
  )
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
      await updateTrailer(form.value())
      setEditMode(false)
    }
  }

  const { registrationNumber, length, width } = useFormFields(form)
  return (
    <div data-testid="trailer-information">
      <Columns isVCentered>
        <Column isNarrow>
          <h4>Trailerin tiedot</h4>
        </Column>
        <Column isNarrow toRight>
          {editMode ? null : (
            <EditLink action={() => changeEditMode(true)}>
              Muokkaa trailerin tietoja
            </EditLink>
          )}
        </Column>
      </Columns>
      <Columns>
        <Column isOneQuarter>
          <TextField
            label="Rekisterinumero"
            bind={registrationNumber}
            readonly={!editMode}
          />
        </Column>
        <Column isOneQuarter>
          <NumberField
            label="Leveys (m)"
            bind={width}
            readonly={!editMode}
            precision={2}
          />
        </Column>
        <Column isOneQuarter>
          <NumberField
            label="Pituus (m)"
            bind={length}
            readonly={!editMode}
            precision={2}
          />
        </Column>
      </Columns>
      {editMode && (
        <Buttons>
          <Button action={cancel} loading={isPending}>
            Peruuta
          </Button>
          <Button action={onSubmit} type="primary" loading={isPending}>
            Tallenna muutokset
          </Button>
        </Buttons>
      )}
    </div>
  )
})
