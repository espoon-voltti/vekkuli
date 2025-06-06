import { Button, Buttons, Column, Columns } from 'lib-components/dom'
import { NumberField } from 'lib-components/form/NumberField'
import { SelectField } from 'lib-components/form/SelectField'
import TextField from 'lib-components/form/TextField'
import { DeleteLink, EditLink } from 'lib-components/links'
import React from 'react'

import { UpdateBoatRequest } from 'citizen-frontend/api-clients/boat'
import { useTranslation } from 'citizen-frontend/localization'
import { Boat } from 'citizen-frontend/shared/types'
import { useForm, useFormFields } from 'lib-common/form/hooks'
import { useFormErrorContext } from 'lib-common/form/state.js'
import { MutationDescription, useMutation } from 'lib-common/query'

import { boatForm, transformBoatToFormBoat } from './formDefinitions'
import { updateBoatDisabled } from './queries'

type BoatProps = {
  boat: Boat
  onDelete?: () => void
  updateMutation?: MutationDescription<UpdateBoatRequest, void>
}

export default React.memo(function Boat({
  boat,
  onDelete,
  updateMutation = updateBoatDisabled
}: BoatProps) {
  const editDisabled = updateMutation === updateBoatDisabled
  const i18n = useTranslation()
  const bind = useForm(
    boatForm,
    () => transformBoatToFormBoat(boat),
    i18n.components.validationErrors
  )
  const { setShowAllErrors } = useFormErrorContext()
  const { mutateAsync: updateBoat, isPending } = useMutation(updateMutation)
  const [editMode, setEditMode] = React.useState(false)

  const cancel = () => {
    bind.set(transformBoatToFormBoat(boat))
    setEditMode(false)
  }
  const onSubmit = async () => {
    if (bind.isValid()) {
      await updateBoat({ boatId: boat.id, input: bind.value() })
      setEditMode(false)
    } else {
      setShowAllErrors(true)
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
                <DeleteLink action={onDelete}>{i18n.boat.delete}</DeleteLink>
              </Column>
            )}
            <Column isNarrow toRight>
              {!editDisabled && (
                <EditLink action={() => setEditMode(true)}>
                  {i18n.boat.editBoatDetails}
                </EditLink>
              )}
            </Column>
          </Columns>
        </Column>
      </Columns>
      <Columns>
        <Column>
          <TextField
            label={i18n.boat.boatName}
            name="name"
            bind={name}
            required={editMode}
            readonly={!editMode}
          />
          <NumberField
            label={i18n.boat.boatWeightInKg}
            name="weight"
            bind={weight}
            required={editMode}
            readonly={!editMode}
          />
          <TextField
            label={i18n.boat.otherIdentifier}
            name="otherIdentification"
            bind={otherIdentification}
            required={editMode}
            readonly={!editMode}
          />
        </Column>
        <Column>
          <SelectField
            label={i18n.boat.boatType}
            bind={type}
            readonly={!editMode}
            required={editMode}
          />
          <NumberField
            label={i18n.boat.boatDepthInMeters}
            name="depth"
            bind={depth}
            required={editMode}
            readonly={!editMode}
            precision={2}
          />
          <TextField
            label={i18n.boat.additionalInfo}
            name="extraInformation"
            bind={extraInformation}
            readonly={!editMode}
          />
        </Column>
        <Column>
          <NumberField
            label={i18n.common.unit.dimensions.widthInMeters}
            name="width"
            bind={width}
            required={editMode}
            readonly={!editMode}
            precision={2}
          />
          <TextField
            label={i18n.boat.registrationNumber}
            name="registrationNumber"
            bind={registrationNumber}
            readonly={!editMode}
          />
        </Column>
        <Column>
          <NumberField
            label={i18n.common.unit.dimensions.lengthInMeters}
            name="length"
            bind={length}
            required={editMode}
            readonly={!editMode}
            precision={2}
          />
          <SelectField
            label={i18n.boat.ownership}
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
