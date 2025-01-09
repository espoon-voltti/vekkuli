import { Button, Buttons, Column, Columns } from 'lib-components/dom'
import { NumberField } from 'lib-components/form'
import TextField from 'lib-components/form/TextField'
import React, { useState } from 'react'

import { BoatSpaceReservation } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import {
  formatNumber,
  formatPlaceIdentifier
} from 'citizen-frontend/shared/formatters'
import { Result } from 'lib-common/api'

import TerminateModal from './TerminateModal'
import TerminateModalFailure from './TerminateModalFailure'
import TerminateModalSuccess from './TerminateModalSuccess'
import TrailerInformation from './TrailerInformation'

type TerminateModalState = 'hidden' | 'visible' | 'success' | 'failure'

export default React.memo(function Reservation({
  reservation,
  canTerminate
}: {
  reservation: BoatSpaceReservation
  canTerminate?: boolean
}) {
  const i18n = useTranslation()
  const [terminateModalVisible, setTerminateModalVisible] =
    useState<TerminateModalState>('hidden')
  const [buttonsVisible, setButtonsVisible] = useState(true)
  const { boatSpace } = reservation
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
  return (
    <>
      <div className="reservation-card">
        <Columns isVCentered>
          <Column isNarrow>
            <h4>
              {i18n.boatSpace.boatSpaceType[reservation.boatSpace.type].label}:{' '}
              {formatPlaceIdentifier(
                boatSpace.section,
                boatSpace.placeNumber,
                boatSpace.locationName
              )}
            </h4>
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
              value={formatNumber(boatSpace.width)}
              readonly={true}
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
              value={formatNumber(boatSpace.length)}
              readonly={true}
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
          />
        )}
        {canTerminate && buttonsVisible && (
          <Buttons>
            <Button
              type="danger-outlined"
              action={() => setTerminateModalVisible('visible')}
            >
              Irtisano paikka
            </Button>
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
    </>
  )
})
