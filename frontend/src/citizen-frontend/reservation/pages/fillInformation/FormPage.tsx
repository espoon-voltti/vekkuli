import React, { useEffect, useRef, useState } from 'react'
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
import { useReservationState } from '../../state'

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
  const unfinishedReservation = useReservationState()
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
    if (
      !hasSetDefaults.current.reservation &&
      unfinishedReservation !== undefined
    ) {
      const { email, phone } = unfinishedReservation.citizen
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
  }, [reserver, unfinishedReservation, citizenBoats])

  const { mutateAsync: cancelReservation } = useMutation(
    cancelReservationMutation
  )

  const onReservationCancel = () => {
    if (unfinishedReservation)
      cancelReservation(unfinishedReservation.id)
        .then(() => {
          return navigate('/kuntalainen/venepaikka')
        })
        .catch((error) => {
          console.error('Error cancelling reservation', error)
        })
  }

  const onSubmit = async () => {
    console.log(
      'onSubmit',
      form.isValid() ? 'isValid' : 'notValid',
      form.validationError()
    )
    if (unfinishedReservation && form.isValid()) {
      await submitForm({
        id: unfinishedReservation?.id,
        input: { ...form.value() }
      })
      return navigate('/kuntalainen/venepaikka/maksu')
    }
  }

  if (unfinishedReservation === undefined) {
    return (
      <section className="section">
        <div className="container">
          <h2 className="title pb-l">Error...</h2>
        </div>
      </section>
    )
  }

  return (
    <section className="section">
      <StepIndicator step="fillInformation" />
      <div className="container">
        <form id="form" className="column" onSubmit={(e) => e.preventDefault()}>
          <h1 className="title pb-l" id="boat-space-form-header">
            {i18n.reservation.formPage.title.Slip('Laajalahti 008')}
          </h1>
          <div id="form-inputs" className="block">
            <Reserver
              reserver={unfinishedReservation.citizen}
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
              boatSpace={unfinishedReservation.boatSpace}
              price={{
                totalPrice: unfinishedReservation.totalPrice,
                vatValue: unfinishedReservation.vatValue,
                netPrice: unfinishedReservation.netPrice
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
    </section>
  )
})
