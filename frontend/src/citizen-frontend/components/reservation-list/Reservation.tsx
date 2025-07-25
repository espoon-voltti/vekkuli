import { Button, Buttons, Column, Columns } from 'lib-components/dom'
import { NumberField } from 'lib-components/form'
import TextField from 'lib-components/form/TextField'
import React, { useState } from 'react'
import { useNavigate } from 'react-router'

import { UpdateTrailerRequest } from 'citizen-frontend/api-clients/trailer'
import { ExistingBoatSpaceReservation } from 'citizen-frontend/api-types/reservation'
import TrailerInformation, {
  StorageTypeContainer
} from 'citizen-frontend/components/trailer/TrailerInformation'
import { useTranslation } from 'citizen-frontend/localization'
import { ErrorBox } from 'citizen-frontend/reservation/components/ErrorBox'
import { formatPlaceIdentifier } from 'citizen-frontend/shared/formatters'
import { useForm, useFormUnion } from 'lib-common/form/hooks'
import {
  MutationDescription,
  useMutation,
  useQueryResult
} from 'lib-common/query'

import ErrorModal, {
  ErrorCode
} from '../../reservation/pages/fillInformation/ErrorModal'
import { unfinishedReservationQuery } from '../../reservation/queries'
import {
  initialFormState,
  onStorageTypeInfoFormUpdate,
  storageTypeInfoUnionForm
} from '../trailer/formDefinitions'

import {
  startRenewReservationMutation,
  updateStorageTypeMutation
} from './queries'
import { updateStorageTypeDisabled } from './queries'

export default React.memo(function Reservation({
  reservation,
  onTerminate
}: {
  reservation: ExistingBoatSpaceReservation
  onTerminate?: () => void
  updateTrailerMutation?: MutationDescription<UpdateTrailerRequest, void>
}) {
  const canSwitch = reservation.allowedReservationOperations.includes('Switch')
  const canRenew = reservation.allowedReservationOperations.includes('Renew')
  const canTerminate =
    reservation.allowedReservationOperations.includes('Terminate')

  const i18n = useTranslation()
  const [editMode, setEditMode] = useState(false)
  const { boatSpace } = reservation
  const navigate = useNavigate()
  const [error, setError] = useState<ErrorCode | undefined>()

  const reservationStatus = useQueryResult(
    unfinishedReservationQuery()
  ).getOrElse(false)
  const { mutateAsync: renewReservation, isPending: renewIsPending } =
    useMutation(startRenewReservationMutation)

  const boatSpaceUnionForm = useForm(
    storageTypeInfoUnionForm,
    () => initialFormState(i18n, reservation.storageType, reservation.trailer),
    i18n.components.validationErrors,
    {
      onUpdate: (prev, next) =>
        onStorageTypeInfoFormUpdate({
          prev,
          next,
          trailer: reservation.trailer
        })
    }
  )

  const { mutateAsync: updateStorageType, isPending } = useMutation(
    updateStorageTypeMutation
  )

  const { form } = useFormUnion(boatSpaceUnionForm)
  const onRenew = () => {
    renewReservation(reservation.id)
      .then(() => {
        return navigate('/kuntalainen/venepaikka/jatka')
      })
      .catch((error) => {
        const errorCode = reservationStatus
          ? 'UNFINISHED_RESERVATION'
          : (error?.response?.data?.errorCode ?? 'SERVER_ERROR')
        console.error(errorCode)
        setError(errorCode)
      })
  }

  const formattedPlaceIdentifier = formatPlaceIdentifier(
    boatSpace.section,
    boatSpace.placeNumber,
    boatSpace.locationId && i18n.boatSpace.harbors[boatSpace.locationId]
  )

  const paymentStatus =
    reservation.status === 'Confirmed' || reservation.status === 'Cancelled'
      ? i18n.reservation.paymentState(reservation.paymentDate)
      : reservation.status === 'Invoiced'
        ? i18n.reservation.invoiceState(reservation.dueDate)
        : '-'

  const onStorageTypeSubmit = async () => {
    if (form.isValid()) {
      await updateStorageType({
        reservationId: reservation.id,
        input: form.value()
      })
      setEditMode(false)
    }
  }

  const resetForm = () => {
    boatSpaceUnionForm.set(
      initialFormState(i18n, reservation.storageType, reservation.trailer)
    )
  }

  return (
    <>
      <div className="reservation-card" data-testid="reservation-list-card">
        <Columns isVCentered>
          <Column isNarrow>
            <h4>{`${i18n.boatSpace.boatSpaceType[reservation.boatSpace.type].label}: ${formattedPlaceIdentifier}`}</h4>
          </Column>
        </Columns>
        <Columns>
          <Column>
            <TextField
              label={i18n.citizenPage.reservation.harbor}
              value={
                boatSpace.locationId
                  ? i18n.boatSpace.harbors[boatSpace.locationId]
                  : undefined
              }
              readonly={true}
            />
            <NumberField
              label={i18n.common.unit.dimensions.widthInMeters}
              value={boatSpace.width}
              readonly={true}
              precision={2}
            />
            <TextField
              label={i18n.citizenPage.reservation.reservationDate}
              value={reservation.startDate.format()}
              readonly={true}
            />
          </Column>
          <Column>
            <TextField
              label={i18n.citizenPage.reservation.place}
              value={formatPlaceIdentifier(
                reservation.boatSpace.section,
                reservation.boatSpace.placeNumber
              )}
              readonly={true}
            />
            <NumberField
              label={i18n.common.unit.dimensions.lengthInMeters}
              value={boatSpace.length}
              readonly={true}
              precision={2}
            />
            <div>
              <TextField
                label={i18n.citizenPage.reservation.reservationValidity}
                value={i18n.reservation.validity(
                  reservation.endDate,
                  reservation.validity,
                  reservation.status,
                  reservation.active
                )}
                readonly={true}
              />
              {reservation.status == 'Cancelled' &&
                reservation.terminationDate && (
                  <p>
                    {i18n.reservation.terminatedAt(reservation.terminationDate)}
                  </p>
                )}
            </div>
          </Column>
          <Column>
            <TextField
              label={i18n.citizenPage.reservation.placeType}
              value={i18n.boatSpace.boatSpaceType[boatSpace.type].label}
              readonly={true}
            />
            <TextField
              label={i18n.citizenPage.reservation.price}
              value={i18n.reservation.totalPrice(
                reservation.totalPrice,
                reservation.vatValue
              )}
              readonly={true}
            />
            <TextField
              label={i18n.citizenPage.reservation.boatPresent}
              value={reservation.boat.name}
              readonly={true}
            />
          </Column>
          <Column>
            {boatSpace.type === 'Slip' ? (
              <TextField
                label={i18n.citizenPage.reservation.equipment}
                value={i18n.boatSpace.amenities[reservation.boatSpace.amenity]}
                readonly={true}
              />
            ) : (
              <StorageTypeContainer
                editIsOn={editMode}
                setEditModeOn={() => setEditMode(true)}
                showEdit={boatSpace.type === 'Winter'}
                form={form}
              />
            )}
            <TextField
              dataTestId="payment-status"
              label={i18n.citizenPage.reservation.paymentStatus}
              value={paymentStatus}
              readonly={true}
            />
          </Column>
        </Columns>

        <TrailerInformation
          reservationId={reservation.id}
          editMode={editMode}
          setEditMode={setEditMode}
          unionForm={boatSpaceUnionForm}
          resetForm={resetForm}
          onSubmit={onStorageTypeSubmit}
          isPending={isPending}
          editDisabled={
            updateStorageTypeMutation === updateStorageTypeDisabled ||
            reservation.active === false
          }
        />
        {!editMode && canRenew && (
          <ErrorBox
            text={i18n.citizenPage.reservation.renewNotification(
              reservation.endDate
            )}
          />
        )}
        {!editMode && (
          <Buttons>
            {onTerminate && canTerminate && (
              <Button
                type="danger-outlined"
                action={onTerminate}
                ariaLabel={`${i18n.citizenPage.reservation.actions.terminate} : ${formattedPlaceIdentifier}`}
              >
                {i18n.citizenPage.reservation.actions.terminate}
              </Button>
            )}
            {canSwitch && (
              <Button
                type="primary"
                action={() =>
                  navigate(`/kuntalainen/venepaikka/vaihda/${reservation.id}`)
                }
                ariaLabel={`${i18n.citizenPage.reservation.actions.change} : ${formattedPlaceIdentifier}`}
              >
                {i18n.citizenPage.reservation.actions.change}
              </Button>
            )}

            {canRenew && (
              <Button
                type="primary"
                action={() => onRenew()}
                disabled={renewIsPending}
                ariaLabel={`${i18n.citizenPage.reservation.actions.renew} : ${formattedPlaceIdentifier}`}
              >
                {i18n.citizenPage.reservation.actions.renew}
              </Button>
            )}
          </Buttons>
        )}
      </div>
      {!!error && (
        <ErrorModal error={error} close={() => setError(undefined)} />
      )}
    </>
  )
})
