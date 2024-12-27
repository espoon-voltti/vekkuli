import { Button, Buttons } from 'lib-components/dom'
import React from 'react'
import { useNavigate } from 'react-router'

import { useForm, useFormFields } from 'lib-common/form/hooks'
import { StateOf } from 'lib-common/form/types'
import { useMutation } from 'lib-common/query'

import { Municipality } from '../../../api-types/reservation'
import { useTranslation } from '../../../localization'
import { formatPlaceIdentifier } from '../../../shared/formatters'
import { Boat, Organization } from '../../../shared/types'
import ReservationCancel from '../../components/ReservationCancel'
import ReservedSpace from '../../components/ReservedSpace'
import { Reservation } from '../../state'

import initialOrganizationFormState, {
  onOrganizationFormUpdate,
  OrganizationForm,
  organizationForm
} from './formDefinitions/organization'
import {
  initialFormState,
  onReserveSpaceUpdate,
  reserveSpaceForm
} from './formDefinitions/reserveSpace'
import { fillBoatSpaceReservationMutation } from './queries'
import ReserverSection from './sections/Reserver'
import UserAgreementsSection from './sections/UserAgreements'
import BoatSection from './sections/boat/Boat'
import OrganizationSection from './sections/organization/Organization'

type FormProperties = {
  reservation: Reservation
  boats: Boat[]
  municipalities: Municipality[]
  organizations: Organization[]
}

export default React.memo(function Form({
  reservation,
  boats,
  municipalities,
  organizations
}: FormProperties) {
  const i18n = useTranslation()
  const navigate = useNavigate()
  const { mutateAsync: submitForm } = useMutation(
    fillBoatSpaceReservationMutation
  )

  const organizationFormBind = useForm(
    organizationForm,
    () => initialOrganizationFormState(i18n, municipalities, organizations),
    i18n.components.validationErrors,
    {
      onUpdate: (prev, next): StateOf<OrganizationForm> =>
        onOrganizationFormUpdate(prev, next, organizations, municipalities)
    }
  )
  const formBind = useForm(
    reserveSpaceForm,
    () => initialFormState(i18n, boats, reservation.citizen),
    i18n.components.validationErrors,
    {
      onUpdate: (prev, next) => onReserveSpaceUpdate(prev, next, i18n, boats)
    }
  )

  const { reserver, boat, userAgreement } = useFormFields(formBind)

  const onSubmit = async () => {
    if (formBind.isValid() && organizationFormBind.isValid()) {
      await submitForm({
        id: reservation?.id,
        input: { ...formBind.value(), ...organizationFormBind.value() }
      })
      return navigate('/kuntalainen/venepaikka/maksu')
    }
  }

  return (
    <form id="form" className="column" onSubmit={(e) => e.preventDefault()}>
      <h1 className="title pb-l" id="boat-space-form-header">
        {i18n.reservation.formPage.title[reservation.boatSpace.type](
          formatPlaceIdentifier(
            reservation.boatSpace.section,
            reservation.boatSpace.placeNumber,
            reservation.boatSpace.locationName
          )
        )}
      </h1>
      <div id="form-inputs" className="block">
        <ReserverSection reserver={reservation.citizen} bind={reserver} />
        <OrganizationSection bind={organizationFormBind} />
        <BoatSection bind={boat} />
        <ReservedSpace
          boatSpace={reservation.boatSpace}
          price={{
            totalPrice: reservation.totalPrice,
            vatValue: reservation.vatValue,
            netPrice: reservation.netPrice
          }}
        />
        <UserAgreementsSection bind={userAgreement} />
      </div>

      <Buttons>
        <ReservationCancel reservationId={reservation.id} type="button">
          Peruuta varaus
        </ReservationCancel>
        <Button id="submit-button" type="primary" action={onSubmit}>
          Jatka maksamaan
        </Button>
      </Buttons>
    </form>
  )
})
