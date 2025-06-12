import { Button, Buttons, Column, Columns } from 'lib-components/dom'
import { NumberField, SelectField, TextField } from 'lib-components/form'
import { EditLink } from 'lib-components/links'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { ReservationId, StorageType } from 'citizen-frontend/shared/types'
import { BoundForm, useFormFields, useFormUnion } from 'lib-common/form/hooks'

import {
  StorageTypeInfoForm,
  StorageTypeInfoUnionForm,
  TrailerStorageForm
} from './formDefinitions'

interface StorageTypeProps {
  setEditModeOn?: () => void
  editIsOn: boolean
  form: BoundForm<StorageTypeInfoForm>
  showEdit?: boolean
}

export function StorageTypeContainer({
  editIsOn,
  setEditModeOn,
  form,
  showEdit
}: StorageTypeProps) {
  const i18n = useTranslation()

  const { storageType: storageTypeBind } = useFormFields(form)
  const storageType = storageTypeBind.state.domValue as StorageType | undefined

  return (
    <>
      {showEdit && editIsOn ? (
        <SelectField
          label={i18n.reservation.formPage.storageType}
          bind={storageTypeBind}
          readonly={!editIsOn}
          required={editIsOn}
        />
      ) : (
        <TextField
          label={i18n.citizenPage.reservation.storageType}
          value={
            storageType ? i18n.boatSpace.winterStorageType[storageType] : '-'
          }
          readonly={true}
          editAction={showEdit ? setEditModeOn : undefined}
        />
      )}
    </>
  )
}

export default React.memo(function TrailerInformation({
  setEditMode,
  editMode,
  unionForm,
  resetForm,
  onSubmit,
  isPending,
  editDisabled = false
}: {
  reservationId: ReservationId
  setEditMode: (value: boolean) => void
  editMode: boolean
  unionForm: BoundForm<StorageTypeInfoUnionForm>
  resetForm: () => void
  onSubmit?: () => Promise<void>
  isPending: boolean
  editDisabled?: boolean
}) {
  const i18n = useTranslation()

  const cancel = () => {
    resetForm()
    setEditMode(false)
  }

  const { form, branch } = useFormUnion(unionForm)

  return (
    <div className="trailer-information" data-testid="trailer-information">
      {branch === 'Trailer' && (
        <TrailerForm
          setEditMode={setEditMode}
          editMode={editMode}
          form={form}
          editDisabled={editDisabled}
        />
      )}
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

function TrailerForm({
  setEditMode,
  editMode,
  form,
  editDisabled
}: {
  setEditMode: (value: boolean) => void
  editMode: boolean
  form: BoundForm<TrailerStorageForm>
  editDisabled?: boolean
}) {
  const i18n = useTranslation()

  const { trailerInfo } = useFormFields(form)
  const { registrationNumber, width, length } = useFormFields(trailerInfo)
  return (
    <>
      <Columns isVCentered>
        <Column isNarrow>
          <h4>{i18n.reservation.formPage.trailerInfo.title}</h4>
        </Column>
        <Column isNarrow toRight>
          {editMode || editDisabled ? null : (
            <EditLink action={() => setEditMode(true)}>
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
    </>
  )
}
