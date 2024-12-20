import { Button, Buttons, Column, Columns } from 'lib-components/dom'
import TextField from 'lib-components/form/TextField'
import React from 'react'

import { Result } from '../../../../lib-common/api'
import DateField from '../../../../lib-components/form/DateField'
import { BoatSpaceReservation } from '../../../api-types/reservation'
import { useTranslation } from '../../../localization'
import {
  formatDimension,
  formatPlaceIdentifier
} from '../../../shared/formatters'

import TerminateModal from './TerminateModal'
import TerminateModalFailure from './TerminateModalFailure'
import TerminateModalSuccess from './TerminateModalSuccess'

type TerminateModalState = 'hidden' | 'visible' | 'success' | 'failure'

export default React.memo(function Reservation({
  reservation
}: {
  reservation: BoatSpaceReservation
}) {
  const i18n = useTranslation()
  const [terminateModalVisible, setTerminateModalVisible] =
    React.useState<TerminateModalState>('hidden')
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
            <TextField
              label="Leveys (m)"
              value={formatDimension(boatSpace.width)}
              readonly={true}
            />
            <TextField label="Varaus tehty" value="<MISSING>" readonly={true} />
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
            <TextField
              label="Pituus (m)"
              value={formatDimension(boatSpace.length)}
              readonly={true}
            />
            <DateField
              label="Varaus voimassa"
              value={reservation.endDate}
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
              value={reservation.totalPrice}
              readonly={true}
            />
            <TextField
              label="Paikalla oleva vene"
              value={reservation.boat.name}
              readonly={true}
            />
          </Column>
          <Column>
            <TextField
              label="Varuste"
              value={i18n.boatSpace.amenities[reservation.boatSpace.amenity]}
              readonly={true}
            />
            <TextField label="Maksettu" value="<MISSING>" readonly={true} />
          </Column>
        </Columns>

        <Buttons>
          <Button
            type="danger-outlined"
            action={() => setTerminateModalVisible('visible')}
          >
            Irtisano paikka
          </Button>
        </Buttons>
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
