import { Button, Buttons, Column, Columns } from 'lib-components/dom'
import { NumberField } from 'lib-components/form'
import TextField from 'lib-components/form/TextField'
import React, { useState } from 'react'
import { useNavigate } from 'react-router'

import { BoatSpaceReservation } from 'citizen-frontend/api-types/reservation'
import { updateCitizenTrailerMutation } from 'citizen-frontend/citizen/queries'
import TrailerInformation from 'citizen-frontend/components/trailer/TrailerInformation'
import { useTranslation } from 'citizen-frontend/localization'
import { formatPlaceIdentifier } from 'citizen-frontend/shared/formatters'
import { Result } from 'lib-common/api'
import { useMutation, useQueryResult } from 'lib-common/query'

import ErrorModal, {
  ErrorCode
} from '../../../reservation/pages/fillInformation/ErrorModal'
import { unfinishedReservationQuery } from '../../../reservation/queries'

import TerminateModal from './TerminateModal'
import TerminateModalFailure from './TerminateModalFailure'
import TerminateModalSuccess from './TerminateModalSuccess'
import { startRenewReservationMutation } from './queries'

type TerminateModalState = 'hidden' | 'visible' | 'success' | 'failure'

export default React.memo(function Reservation({
  reservation,
  canTerminate,
  canSwitch,
  canRenew
}: {
  reservation: BoatSpaceReservation
  canTerminate?: boolean
  canSwitch?: boolean
  canRenew?: boolean
}) {
  const i18n = useTranslation()
  const [terminateModalVisible, setTerminateModalVisible] =
    useState<TerminateModalState>('hidden')
  const [buttonsVisible, setButtonsVisible] = useState(true)
  const { boatSpace } = reservation
  const navigate = useNavigate()
  const [error, setError] = useState<ErrorCode | undefined>()
  const reservationStatus = useQueryResult(
    unfinishedReservationQuery()
  ).getOrElse(false)
  const onTermination = (mutation: Promise<Result<void>>) => {
    mutation
      .then((result) => {
        if (result.isSuccess) setTerminateModalVisible('success')
        else setTerminateModalVisible('failure')
      })
      .catch(() => {
        setTerminateModalVisible('failure')
      })
  }
  const { mutateAsync: renewReservation, isPending: renewIsPending } =
    useMutation(startRenewReservationMutation)

  const onRenew = () => {
    renewReservation(reservation.id)
      .then((response) => {
        console.info('switch place response', response)
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
    boatSpace.locationName
  )

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
              label="Satama"
              value={boatSpace.locationName || undefined}
              readonly={true}
            />
            <NumberField
              label="Leveys (m)"
              value={boatSpace.width}
              readonly={true}
              precision={2}
            />
            <TextField
              label="Varaus tehty"
              value={reservation.created.format()}
              readonly={true}
            />
          </Column>
          <Column>
            <TextField
              label="Paikka"
              value={formatPlaceIdentifier(
                reservation.boatSpace.section,
                reservation.boatSpace.placeNumber
              )}
              readonly={true}
            />
            <NumberField
              label="Pituus (m)"
              value={boatSpace.length}
              readonly={true}
              precision={2}
            />
            <TextField
              label="Varaus voimassa"
              value={i18n.reservation.validity(
                reservation.endDate,
                reservation.validity,
                boatSpace.type
              )}
              readonly={true}
            />
          </Column>
          <Column>
            <TextField
              label="Paikan tyyppi"
              value={i18n.boatSpace.boatSpaceType[boatSpace.type].label}
              readonly={true}
            />
            <TextField
              label="Hinta"
              value={i18n.reservation.totalPrice(
                reservation.totalPrice,
                reservation.vatValue
              )}
              readonly={true}
            />
            <TextField
              label="Paikalla oleva vene"
              value={reservation.boat.name}
              readonly={true}
            />
          </Column>
          <Column>
            {boatSpace.type !== 'Winter' && (
              <TextField
                label="Varuste"
                value={i18n.boatSpace.amenities[reservation.boatSpace.amenity]}
                readonly={true}
              />
            )}
            {boatSpace.type === 'Winter' && (
              <TextField
                label="Säilytystapa"
                value={
                  reservation.storageType
                    ? i18n.boatSpace.winterStorageType[reservation.storageType]
                    : '-'
                }
                readonly={true}
              />
            )}
            <TextField
              label="Maksun tila"
              value={i18n.reservation.paymentState(reservation.paymentDate)}
              readonly={true}
            />
          </Column>
        </Columns>
        {reservation.trailer && (
          <TrailerInformation
            trailer={reservation.trailer}
            setEditIsOn={(mode) => setButtonsVisible(!mode)}
            updateMutation={updateCitizenTrailerMutation}
          />
        )}
        {buttonsVisible && (
          <Buttons>
            {canTerminate && (
              <Button
                type="danger-outlined"
                action={() => setTerminateModalVisible('visible')}
                ariaLabel={`${i18n.citizenPage.reservation.actions.terminate} : ${formattedPlaceIdentifier}`}
              >
                {i18n.citizenPage.reservation.actions.terminate}
              </Button>
            )}
            {canSwitch && (
              <Button
                type="primary"
                action={() => navigate('/kuntalainen/venepaikka')}
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
      {terminateModalVisible === 'visible' && (
        <TerminateModal
          close={() => setTerminateModalVisible('hidden')}
          reservation={reservation}
          onTermination={onTermination}
        />
      )}
      {terminateModalVisible === 'success' && (
        <TerminateModalSuccess
          close={() => setTerminateModalVisible('hidden')}
        />
      )}
      {terminateModalVisible === 'failure' && (
        <TerminateModalFailure
          close={() => setTerminateModalVisible('hidden')}
        />
      )}
      {!!error && (
        <ErrorModal error={error} close={() => setError(undefined)} />
      )}
    </>
  )
})
