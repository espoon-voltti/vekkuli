import { Button, Buttons } from 'lib-components/dom'
import React from 'react'
import { useNavigate } from 'react-router'

import { Municipality } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import { formatPlaceIdentifier } from 'citizen-frontend/shared/formatters'
import { Boat, Organization, StorageType } from 'citizen-frontend/shared/types'
import { useForm, useFormFields, useFormUnion } from 'lib-common/form/hooks'
import { useFormErrorContext } from 'lib-common/form/state'
import { useMutation } from 'lib-common/query'
import { WarningExclamation } from 'lib-icons'

import ReservationCancel from '../../components/ReservationCancel'
import ReservedSpace from '../../components/ReservedSpace'
import { Reservation } from '../../state'
import useStoredSearchState from '../useStoredSearchState'

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
import WinterStorageType from './sections/winterStorageType/WinterStorageType'

type FormProperties = {
  reservation: Reservation
  boats: Boat[]
  municipalities: Municipality[]
  organizations: Organization[]
  organizationBoats: Record<string, Boat[]>
}

export default React.memo(function Form({
  reservation,
  boats,
  municipalities,
  organizations,
  organizationBoats
}: FormProperties) {
  const i18n = useTranslation()
  const navigate = useNavigate()
  const { showAllErrors, setShowAllErrors } = useFormErrorContext()

  const { mutateAsync: submitForm } = useMutation(
    fillBoatSpaceReservationMutation
  )

  const [searchState, setSearchState] = useStoredSearchState()
  const formBind = useForm(
    reserveSpaceForm,
    () =>
      initialFormState(
        i18n,
        boats,
        reservation.citizen,
        reservation.boatSpace.type,
        municipalities,
        organizations,
        searchState
      ),
    i18n.components.validationErrors,
    {
      onUpdate: (prev, next) =>
        onReserveSpaceUpdate(
          prev,
          next,
          i18n,
          boats,
          organizationBoats,
          municipalities,
          organizations
        )
    }
  )

  const { reserver, boat, userAgreement, spaceTypeInfo, organization } =
    useFormFields(formBind)

  const { branch, form: winterStorageFom } = useFormUnion(spaceTypeInfo)

  const onSubmit = async () => {
    if (!formBind.isValid()) setShowAllErrors(true)
    else {
      setSearchState({})
      await submitForm({
        id: reservation?.id,
        input: formBind.value()
      })
      return navigate('/kuntalainen/venepaikka/maksa')
    }
  }

  const updatedReservation = {
    ...reservation,
    storageType: winterStorageFom?.state?.storageType.domValue as
      | StorageType
      | undefined
  }

  return (
    <form id="form" className="column" onSubmit={(e) => e.preventDefault()}>
      <h1 className="title pb-l" id="boat-space-form-header">
        {i18n.reservation.formPage.title[updatedReservation.boatSpace.type](
          formatPlaceIdentifier(
            updatedReservation.boatSpace.section,
            updatedReservation.boatSpace.placeNumber,
            updatedReservation.boatSpace.locationName
          )
        )}
      </h1>
      <div id="form-inputs" className="block">
        <ReserverSection
          reserver={updatedReservation.citizen}
          bind={reserver}
        />
        {organizations.length > 0 && (
          <OrganizationSection bind={organization} />
        )}
        <BoatSection bind={boat} />
        {branch === 'Winter' && <WinterStorageType bind={winterStorageFom} />}
        <ReservedSpace reservation={updatedReservation} />
        <UserAgreementsSection bind={userAgreement} />
        {showAllErrors && <ValidationWarning />}
      </div>

      <Buttons>
        <ReservationCancel reservationId={reservation.id} type="button">
          {i18n.reservation.cancelReservation}
        </ReservationCancel>
        <Button id="submit-button" type="primary" action={onSubmit}>
          {i18n.reservation.continueToPaymentButton}
        </Button>
      </Buttons>
    </form>
  )
})

const ValidationWarning = React.memo(function ValidationWarning() {
  return (
    <div className="warning block form-validation-message">
      <span className="icon">
        <WarningExclamation isError={false} />
      </span>
      <span className="p-l">Pakollisia tietoja puuttuu</span>
    </div>
  )
})
