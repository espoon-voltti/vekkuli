import { CheckboxField } from 'lib-components/form/CheckboxField'
import React, { useState } from 'react'

import { UpdateBoatRequest } from 'citizen-frontend/api-clients/boat'
import { useTranslation } from 'citizen-frontend/localization'
import { Boat, BoatId } from 'citizen-frontend/shared/types'
import { useForm, useFormFields } from 'lib-common/form/hooks'
import { MutationDescription, useMutationResult } from 'lib-common/query'

import BoatComponent from './Boat'
import ConfirmDeleteBoatModal from './ConfirmDeleteBoatModal'
import DeleteBoatFailedModal from './DeleteBoatFailedModal'
import DeleteBoatSuccessModal from './DeleteBoatSuccessModal'
import { initShowBoatsForm, showBoatsForm } from './formDefinitions'
import { deleteBoatDisabled } from './queries'

type BoatsNotInreservationsProps = {
  boats: Boat[]
  deleteMutation?: MutationDescription<BoatId, void>
  updateMutation?: MutationDescription<UpdateBoatRequest, void>
}

export default React.memo(function BoatsNotInreservations({
  boats,
  updateMutation,
  deleteMutation = deleteBoatDisabled
}: BoatsNotInreservationsProps) {
  const deleteDisabled = deleteMutation === deleteBoatDisabled
  const i18n = useTranslation()

  const [boatPendingDeletion, setBoatPendingDeletion] = useState<Boat | null>(
    null
  )
  const [boatDeleteSuccess, setBoatDeleteSuccess] = useState(false)
  const [boatDeleteFailed, setBoatDeleteFailed] = useState(false)

  const bind = useForm(
    showBoatsForm,
    () => initShowBoatsForm(i18n),
    i18n.components.validationErrors
  )
  const { show } = useFormFields(bind)

  const { mutateAsync: deleteBoat, isPending } =
    useMutationResult(deleteMutation)

  return (
    <>
      {boats && boats.length > 0 && (
        <>
          <div className="pb-l">
            <CheckboxField id="show-boats" name="showBoats" bind={show} />
          </div>
          {!!show.value()?.length && (
            <div className="reservation-list form-section no-bottom-border">
              {boats.map((boat) => (
                <BoatComponent
                  key={boat.id}
                  boat={boat}
                  onDelete={
                    deleteDisabled
                      ? undefined
                      : () => setBoatPendingDeletion(boat)
                  }
                  updateMutation={updateMutation}
                />
              ))}
            </div>
          )}
        </>
      )}
      {boatPendingDeletion && (
        <ConfirmDeleteBoatModal
          boat={boatPendingDeletion}
          onCancel={() => {
            setBoatPendingDeletion(null)
          }}
          onConfirm={() => {
            deleteBoat(boatPendingDeletion.id)
              .then((result) => {
                if (result.isSuccess) {
                  setBoatDeleteSuccess(true)
                } else if (result.isFailure) {
                  setBoatDeleteFailed(true)
                }
              })
              .catch(() => setBoatDeleteFailed(true))
              .finally(() => setBoatPendingDeletion(null))
          }}
          isPending={isPending}
        />
      )}
      {boatDeleteSuccess && (
        <DeleteBoatSuccessModal onClose={() => setBoatDeleteSuccess(false)} />
      )}
      {boatDeleteFailed && (
        <DeleteBoatFailedModal onClose={() => setBoatDeleteFailed(false)} />
      )}
    </>
  )
})
