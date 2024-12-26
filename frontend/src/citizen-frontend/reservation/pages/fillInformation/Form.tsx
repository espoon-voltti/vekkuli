import { Container } from 'lib-components/dom'
import React, { useState } from 'react'
import { useNavigate } from 'react-router'

import { useForm, useFormFields } from 'lib-common/form/hooks'
import { StateOf } from 'lib-common/form/types'
import { useMutation } from 'lib-common/query'

import { Municipality } from '../../../api-types/reservation'
import { useTranslation } from '../../../localization'
import { Boat, Organization } from '../../../shared/types'
import ReservedSpace from '../../components/ReservedSpace'
import { cancelReservationMutation } from '../../queries'
import { Reservation } from '../../state'

import { BoatForm } from './formDefinitions/boat'
import initialOrganizationFormState, {
  onOrganizationFormUpdate,
  OrganizationForm,
  organizationForm
} from './formDefinitions/organization'
import {
  initialFormState,
  ReserveSpaceForm,
  reserveSpaceForm
} from './formDefinitions/reserveSpace'
import { onBoatFormUpdate } from './helpers'
import { fillBoatSpaceReservationMutation } from './queries'
import BoatSection from './sections/Boat'
import BoatOwnershipStatus from './sections/BoatOwnershipStatus'
import ReserverSection from './sections/Reserver'
import UserAgreementsSection from './sections/UserAgreements'
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
  const [newBoatStateStore, setNewBoatStateStore] = useState<
    StateOf<BoatForm> | undefined
  >()

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
    () => initialFormState(i18n),
    i18n.components.validationErrors,
    {
      onUpdate: (prev, next): StateOf<ReserveSpaceForm> => {
        const prevBoatId = prev.boat.existingBoat.domValue
        const nextBoatId = next.boat.existingBoat.domValue

        if (prevBoatId !== nextBoatId) {
          return {
            ...next,
            ...{
              boat: onBoatFormUpdate({
                prevBoatState: prev.boat,
                nextBoatState: next.boat,
                i18n,
                citizenBoats: boats,
                newBoatStateStore,
                setNewBoatStateStore
              })
            }
          }
        }
        return next
      }
    }
  )
  const { reserver, boat, boatOwnership, userAgreement } =
    useFormFields(formBind)
  const { renterType, organization, organizationSelection } =
    useFormFields(organizationFormBind)

  const { mutateAsync: cancelReservation } = useMutation(
    cancelReservationMutation
  )

  const onReservationCancel = () => {
    cancelReservation(reservation.id)
      .then(() => {
        return navigate('/kuntalainen/venepaikka')
      })
      .catch((error) => {
        console.error('Error cancelling reservation', error)
      })
  }

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
    <Container>
      <form id="form" className="column" onSubmit={(e) => e.preventDefault()}>
        <h1 className="title pb-l" id="boat-space-form-header">
          {i18n.reservation.formPage.title.Slip('Laajalahti 008')}
        </h1>
        <div id="form-inputs" className="block">
          <ReserverSection reserver={reservation.citizen} bind={reserver} />
          <OrganizationSection
            organizationBind={organization}
            renterTypeBind={renterType}
            organizationSelectionBind={organizationSelection}
          />
          <BoatSection bind={boat} boats={boats} />
          <BoatOwnershipStatus bind={boatOwnership} />
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

        <div className="buttons">
          <button
            id="cancel"
            className="button is-secondary"
            onClick={onReservationCancel}
          >
            Peruuta varaus
          </button>
          <button
            id="submit-button"
            className="button is-primary"
            type="submit"
            onClick={onSubmit}
          >
            Jatka maksamaan
          </button>
        </div>
      </form>
    </Container>
  )
})
