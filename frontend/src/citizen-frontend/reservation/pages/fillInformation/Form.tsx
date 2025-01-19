import { Block, Button, Buttons } from 'lib-components/dom'
import { FormSection } from 'lib-components/form'
import React from 'react'
import { useNavigate } from 'react-router'

import { Municipality } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import {
  formatPlaceIdentifier,
  parsePrice
} from 'citizen-frontend/shared/formatters'
import { Boat, Organization, StorageType } from 'citizen-frontend/shared/types'
import { useForm, useFormFields, useFormUnion } from 'lib-common/form/hooks'
import { useFormErrorContext } from 'lib-common/form/state'
import { useMutation } from 'lib-common/query'
import { WarningExclamation } from 'lib-icons'

import { InfoBox } from '../../components/InfoBox'
import ReservationCancel from '../../components/ReservationCancel'
import ReservedSpace from '../../components/ReservedSpace'
import { Reservation } from '../../state'
import useStoredSearchState from '../useStoredSearchState'

import ErrorModal from './ErrorModal'
import SwitchPriceInfoBox from './SwitchPriceInfoBox'
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
  const [submitError, setSubmitError] = React.useState<'SERVER_ERROR' | null>(
    null
  )

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
        searchState,
        reservation
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
      try {
        const updatedReservation = await submitForm({
          id: reservation?.id,
          input: formBind.value()
        })

        setSearchState({})
        if (updatedReservation.status === 'Confirmed')
          return navigate(
            `/kuntalainen/venepaikka/vahvistus/${updatedReservation.id}`
          )

        return navigate('/kuntalainen/venepaikka')
      } catch (e) {
        console.error(e)
        setSubmitError('SERVER_ERROR')
      }
    }
  }

  const updatedReservation = {
    ...reservation,
    storageType: winterStorageFom?.state?.storageType.domValue as
      | StorageType
      | undefined
  }

  return (
    <>
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
        <Block>
          {reservation.creationType === 'Switch' && (
            <InfoBox text={i18n.reservation.formPage.info.switch} />
          )}
          <ReserverSection
            reserver={updatedReservation.citizen}
            bind={reserver}
          />
          {organizations.length > 0 && (
            <OrganizationSection bind={organization} />
          )}
          <BoatSection bind={boat} />
          {branch === 'Winter' && <WinterStorageType bind={winterStorageFom} />}
          <FormSection>
            <ReservedSpace reservation={updatedReservation} />
            {reservation.creationType === 'Switch' && (
              <SwitchPriceInfoBox priceDifference={reservation.revisedPrice} />
            )}
          </FormSection>
          <UserAgreementsSection bind={userAgreement} />
          {showAllErrors && <ValidationWarning />}
        </Block>

        <Buttons>
          <ReservationCancel reservationId={reservation.id} type="button">
            {i18n.reservation.cancelReservation}
          </ReservationCancel>
          <Button type="primary" action={onSubmit}>
            {parsePrice(reservation.revisedPrice) > 0
              ? i18n.reservation.formPage.submit.continueToPayment
              : i18n.reservation.formPage.submit.confirmReservation}
          </Button>
        </Buttons>
      </form>
      {submitError && (
        <ErrorModal error={submitError} close={() => setSubmitError(null)} />
      )}
    </>
  )
})

export const ValidationWarning = React.memo(function ValidationWarning() {
  return (
    <div className="warning block form-validation-message">
      <span className="icon">
        <WarningExclamation isError={false} />
      </span>
      <span className="p-l">Pakollisia tietoja puuttuu</span>
    </div>
  )
})
