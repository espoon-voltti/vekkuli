import { Button, Buttons, Column, Columns } from 'lib-components/dom'
import { NumberField } from 'lib-components/form/NumberField'
import { SelectField } from 'lib-components/form/SelectField'
import TextField from 'lib-components/form/TextField'
import { DeleteLink, EditLink } from 'lib-components/links'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { Boat } from 'citizen-frontend/shared/types'
import { useForm, useFormFields } from 'lib-common/form/hooks'
import { useMutation } from 'lib-common/query'

import { updateBoatInformationMutation } from '../../queries'

import { boatForm, transformBoatToFormBoat } from './formDefinitions'

export default React.memo(function Boat({
  boat,
  onDelete
}: {
  boat: Boat
  onDelete?: () => void
}) {
  const i18n = useTranslation()
  const bind = useForm(
    boatForm,
    () => transformBoatToFormBoat(boat, i18n),
    i18n.components.validationErrors
  )
  const { mutateAsync: updateBoat, isPending } = useMutation(
    updateBoatInformationMutation
  )
  const [editMode, setEditMode] = React.useState(false)

  const cancel = () => {
    bind.set(transformBoatToFormBoat(boat, i18n))
    setEditMode(false)
  }
  const onSubmit = async () => {
    if (bind.isValid()) {
      await updateBoat(bind.value())
      setEditMode(false)
    }
  }
  const {
    name,
    weight,
    length,
    width,
    type,
    depth,
    registrationNumber,
    ownership,
    otherIdentification,
    extraInformation
  } = useFormFields(bind)

  return (
    <div className="reservation-card" data-testid="boat-row">
      <Columns isVCentered>
        <Column isNarrow>
          <h4>{boat.name}</h4>
        </Column>
        <Column>
          <Columns>
            {onDelete && (
              <Column isNarrow>
                <DeleteLink action={onDelete}>Poista vene</DeleteLink>
              </Column>
            )}
            <Column isNarrow toRight>
              <EditLink action={() => setEditMode(true)}>
                Muokkaa veneen tietoja
              </EditLink>
            </Column>
          </Columns>
        </Column>
      </Columns>
      <Columns>
        <Column>
          <TextField
            label="Veneen nimi"
            name="name"
            bind={name}
            required={editMode}
            readonly={!editMode}
          />
          <NumberField
            label="Paino (kg)"
            name="weight"
            bind={weight}
            required={editMode}
            readonly={!editMode}
          />
          <TextField
            label="Muu tunniste"
            name="otherIdentification"
            bind={otherIdentification}
            required={editMode}
            readonly={!editMode}
          />
        </Column>
        <Column>
          <SelectField
            label="Veneen tyyppi"
            bind={type}
            readonly={!editMode}
            required={editMode}
          />
          <NumberField
            label="Syväys (m)"
            name="depth"
            bind={depth}
            required={editMode}
            readonly={!editMode}
            precision={2}
          />
          <TextField
            label="Lisätiedot"
            name="extraInformation"
            bind={extraInformation}
            readonly={!editMode}
          />
        </Column>
        <Column>
          <NumberField
            label="Leveys (m)"
            name="width"
            bind={width}
            required={editMode}
            readonly={!editMode}
            precision={2}
          />
          <TextField
            label="Rekisteritunnus"
            name="registrationNumber"
            bind={registrationNumber}
            readonly={!editMode}
          />
        </Column>
        <Column>
          <NumberField
            label="Pituus (m)"
            name="length"
            bind={length}
            required={editMode}
            readonly={!editMode}
            precision={2}
          />
          <SelectField
            label="Omistussuhde"
            name="ownershipStatus"
            bind={ownership}
            required={editMode}
            readonly={!editMode}
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
