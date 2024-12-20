import { Loader } from 'lib-components/Loader'
import React, { useContext, useEffect, useRef, useState } from 'react'
import { useNavigate } from 'react-router'

import { useForm, useFormFields } from 'lib-common/form/hooks'
import { StateOf } from 'lib-common/form/types'
import { useMutation, useQueryResult } from 'lib-common/query'

import { useTranslation } from '../../../localization'
import { citizenBoatsQuery } from '../../../shared/queries'
import { ReserverType } from '../../../shared/types'
import StepIndicator from '../../StepIndicator'
import ReservedSpace from '../../components/ReservedSpace'
import { cancelReservationMutation } from '../../queries'
import { ReservationStateContext } from '../../state'

import {
  BoatForm,
  initialBoatValue,
  initialFormState,
  ReserveSpaceForm,
  reserveSpaceForm
} from './formDefinitions'
import { onBoatFormUpdate } from './helpers'
import { fillBoatSpaceReservationMutation } from './queries'
import BoatSection from './sections/Boat'
import BoatOwnershipStatus from './sections/BoatOwnershipStatus'
import Organization from './sections/Organization'
import RenterType from './sections/RenterType'
import Reserver from './sections/Reserver'
import UserAgreements from './sections/UserAgreements'

export default React.memo(function FormPage() {
  const i18n = useTranslation()
  const navigate = useNavigate()
  const { mutateAsync: submitForm } = useMutation(
    fillBoatSpaceReservationMutation
  )
  const hasSetDefaults = useRef({
    reservation: false,
    citizenBoats: false
  })
  const { reservation } = useContext(ReservationStateContext)
  const maybeReservation = reservation.getOrElse(undefined)
  const citizenBoats = useQueryResult(citizenBoatsQuery())

  const [newBoatStateStore, setNewBoatStateStore] = useState<
    StateOf<BoatForm> | undefined
  >()
  const form = useForm(
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
                citizenBoats: citizenBoats,
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
  const {
    reserver,
    renterType,
    boat,
    boatOwnership,
    userAgreement,
    organization
  } = useFormFields(form)

  useEffect(() => {
    if (!hasSetDefaults.current.reservation && maybeReservation !== undefined) {
      const { email, phone } = maybeReservation.citizen
      reserver.set({
        email: email,
        phone: phone
      })
      hasSetDefaults.current.reservation = true
    }

    if (!hasSetDefaults.current.citizenBoats && !citizenBoats.isLoading) {
      const options = citizenBoats.getOrElse([]).map((boat) => ({
        domValue: boat.id,
        label: boat.name,
        value: boat
      }))

      if (options.length > 0)
        options.unshift({
          domValue: '',
          label: 'Uusi vene',
          value: initialBoatValue()
        })

      boat.update((prev) => ({
        ...prev,
        existingBoat: {
          domValue: '',
          options: options
        }
      }))
      hasSetDefaults.current.citizenBoats = true
    }
  }, [reserver, maybeReservation, citizenBoats])

  const { mutateAsync: cancelReservation } = useMutation(
    cancelReservationMutation
  )

  const onReservationCancel = () => {
    if (maybeReservation)
      cancelReservation(maybeReservation.id)
        .then(() => {
          return navigate('/kuntalainen/venepaikka')
        })
        .catch((error) => {
          console.error('Error cancelling reservation', error)
        })
  }

  const onSubmit = async () => {
    if (maybeReservation && form.isValid()) {
      await submitForm({
        id: maybeReservation?.id,
        input: { ...form.value() }
      })
      return navigate('/kuntalainen/venepaikka/maksu')
    }
  }

  return (
    <section className="section">
      <Loader results={[reservation]}>
        {(loadedReservation) => (
          <>
            <StepIndicator step="fillInformation" />
            <div className="container">
              <form
                id="form"
                className="column"
                onSubmit={(e) => e.preventDefault()}
              >
                <h1 className="title pb-l" id="boat-space-form-header">
                  {i18n.reservation.formPage.title.Slip('Laajalahti 008')}
                </h1>
                <div id="form-inputs" className="block">
                  <Reserver
                    reserver={loadedReservation.citizen}
                    form={reserver}
                  />
                  <RenterType form={renterType} />
                  {renterType.state.type.domValue !==
                  ReserverType.Organization.toString() ? null : (
                    <Organization form={organization} />
                  )}
                  <BoatSection form={boat} />
                  <BoatOwnershipStatus form={boatOwnership} />
                  <ReservedSpace
                    boatSpace={loadedReservation.boatSpace}
                    price={{
                      totalPrice: loadedReservation.totalPrice,
                      vatValue: loadedReservation.vatValue,
                      netPrice: loadedReservation.netPrice
                    }}
                  />
                  <UserAgreements form={userAgreement} />
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
            </div>
          </>
        )}
      </Loader>
    </section>
  )
})
