import { Block, Button, Buttons } from 'lib-components/dom'
import { FormSection } from 'lib-components/form'
import React from 'react'
import { useNavigate } from 'react-router'

import { ReservationInfo } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import {
  formatPlaceIdentifier,
  parsePrice
} from 'citizen-frontend/shared/formatters'
import { StorageType } from 'citizen-frontend/shared/types'
import { useForm, useFormFields, useFormUnion } from 'lib-common/form/hooks'
import { useFormErrorContext } from 'lib-common/form/state'
import { useMutation } from 'lib-common/query'
import { WarningExclamation } from 'lib-icons'

import { getReservationInfoForReservation } from '../../ReservationInfoForReservation'
import { InfoBox } from '../../components/InfoBox'
import ReservationCancel from '../../components/ReservationCancel'
import ReservedSpace from '../../components/ReservedSpace'
import { Reservation } from '../../state'
import useStoredSearchState from '../useStoredSearchState'

import ErrorModal from './ErrorModal'
import {
  initialFormState,
  onReserveSpaceUpdate,
  reserveSpaceForm
} from './formDefinitions/reserveSpace'
import { fillBoatSpaceReservationMutation } from './queries'
import ReserverSection from './sections/Reserver'
import UserAgreementsSection from './sections/UserAgreements'
import AllYearStorageType from './sections/allYearStorageType/StorageType'
import BoatSection from './sections/boat/Boat'
import OrganizationSection from './sections/organization/Organization'
import TrailerStorageType from './sections/trailerStorageType/TrailerStorageType'
import WinterStorageType from './sections/winterStorageType/WinterStorageType'

type FormProperties = {
  reservation: Reservation
}

export default React.memo(function Form({ reservation }: FormProperties) {
  const i18n = useTranslation()
  const navigate = useNavigate()
  const { showAllErrors, setShowAllErrors } = useFormErrorContext()
  const [submitError, setSubmitError] = React.useState<'SERVER_ERROR' | null>(
    null
  )
  const {
    boats,
    municipalities,
    organizations,
    organizationsBoats,
    organizationReservationInfos
  } = reservation

  const { mutateAsync: submitForm, isPending } = useMutation(
    fillBoatSpaceReservationMutation
  )
  const [searchState, setSearchState] = useStoredSearchState()
  const formBind = useForm(
    reserveSpaceForm,
    () =>
      initialFormState(
        i18n,
        boats,
        organizationsBoats,
        reservation.reservation.citizen,
        reservation.reservation.boatSpace,
        municipalities,
        organizations,
        reservation,
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
          organizationsBoats,
          municipalities,
          organizations,
          reservation.reservation.boatSpace.excludedBoatTypes || []
        )
    }
  )

  const { reserver, boat, userAgreement, spaceTypeInfo, organization } =
    useFormFields(formBind)

  const { branch, form: spaceTypeInfoForm } = useFormUnion(spaceTypeInfo)

  const onSubmit = async () => {
    if (!formBind.isValid()) setShowAllErrors(true)
    else {
      try {
        const updatedReservation = await submitForm({
          id: reservation?.reservation.id,
          input: formBind.value()
        })

        setSearchState({})
        if (updatedReservation.status === 'Confirmed')
          return navigate(
            `/kuntalainen/venepaikka/vahvistus/${updatedReservation.id}`
          )

        return navigate('/kuntalainen/venepaikka/maksa')
      } catch (e) {
        console.error(e)
        setSubmitError('SERVER_ERROR')
      }
    }
  }

  // Update storage type based on the form values
  const updatedReservation = {
    ...reservation.reservation,
    storageType: (branch === 'Winter'
      ? spaceTypeInfoForm.state.storageType.domValue
      : branch === 'Storage' &&
          spaceTypeInfoForm.state.storageInfo.branch === 'Buck'
        ? spaceTypeInfoForm.state.storageInfo.state.domValue
        : 'Trailer') as StorageType | undefined
  }

  const getSelectedOrganizationId = () => {
    const org = organization.isValid() && organization.value().organization
    return org && 'id' in org ? org?.id : null
  }

  const getRevisedPrice = (): ReservationInfo => {
    const selectedOrganizationId = getSelectedOrganizationId()
    const organizationRevisedPrice = organizationReservationInfos.find(
      (r) => r.id === selectedOrganizationId
    )
    return organizationRevisedPrice ?? updatedReservation.reservationInfo
  }

  const reservationInfoForReservation = getReservationInfoForReservation(
    updatedReservation,
    getRevisedPrice()
  )

  return (
    <>
      <form id="form" className="column" onSubmit={(e) => e.preventDefault()}>
        <h2 className="title pb-l" id="boat-space-form-header">
          {i18n.reservation.formPage.title[updatedReservation.boatSpace.type](
            formatPlaceIdentifier(
              updatedReservation.boatSpace.section,
              updatedReservation.boatSpace.placeNumber,
              updatedReservation.boatSpace.locationName
            )
          )}
        </h2>
        <Block>
          {reservation.reservation.creationType === 'Switch' && (
            <InfoBox text={i18n.reservation.formPage.info.switch} />
          )}
          <ReserverSection
            reserver={updatedReservation.citizen}
            bind={reserver}
          />
          {organizations.length > 0 && (
            <OrganizationSection bind={organization} />
          )}
          <BoatSection
            bind={boat}
            boatSpace={reservation.reservation.boatSpace}
            reservationId={reservation.reservation.id}
          />
          {branch === 'Winter' && (
            <WinterStorageType bind={spaceTypeInfoForm} />
          )}
          {branch === 'Trailer' && (
            <TrailerStorageType bind={spaceTypeInfoForm} />
          )}
          {branch === 'Storage' && (
            <AllYearStorageType bind={spaceTypeInfoForm} />
          )}
          <FormSection>
            <ReservedSpace
              reservation={updatedReservation}
              reservationInfoForReservation={reservationInfoForReservation}
            />
          </FormSection>
          <UserAgreementsSection bind={userAgreement} />
          {showAllErrors && <ValidationWarning />}
        </Block>
        <Buttons>
          <ReservationCancel
            reservationId={reservation.reservation.id}
            type="button"
          >
            {i18n.reservation.cancelReservation}
          </ReservationCancel>
          <Button type="primary" action={onSubmit} loading={isPending}>
            {parsePrice(
              reservationInfoForReservation.revisedPriceWithDiscountInEuro
            ) > 0
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
  const i18n = useTranslation()
  return (
    <div className="warning block form-validation-message" role="alert">
      <span className="icon">
        <WarningExclamation isError={false} />
      </span>
      <span className="p-l">{i18n.common.errors.validationWarning}</span>
    </div>
  )
})
