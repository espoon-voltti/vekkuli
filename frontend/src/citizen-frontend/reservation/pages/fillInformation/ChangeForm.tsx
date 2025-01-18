import { Button, Buttons } from 'lib-components/dom'
import { FormSection } from 'lib-components/form'
import React from 'react'
import { useNavigate } from 'react-router'

import { Municipality } from 'citizen-frontend/api-types/reservation'
import { useTranslation } from 'citizen-frontend/localization'
import {
  formatCentsToEuros,
  formatPlaceIdentifier
} from 'citizen-frontend/shared/formatters'
import { Boat, Organization, StorageType } from 'citizen-frontend/shared/types'
import { useForm, useFormFields, useFormUnion } from 'lib-common/form/hooks'
import { useFormErrorContext } from 'lib-common/form/state'
import { StateOf } from 'lib-common/form/types'
import { useMutation } from 'lib-common/query'

import ReservationCancel from '../../components/ReservationCancel'
import ReservedSpace from '../../components/ReservedSpace'
import { Reservation } from '../../state'
import { InfoBox } from '../chooseBoatSpace/SearchResults/InfoBox'
import { switchSpaceMutation } from '../chooseBoatSpace/queries'

import { ValidationWarning } from './Form'
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
import ReserverSection from './sections/Reserver'
import UserAgreementsSection from './sections/UserAgreements'
import BoatSection from './sections/boat/Boat'
import OrganizationSection from './sections/organization/Organization'
import WinterStorageType from './sections/winterStorageType/WinterStorageType'

type ChangeFormProperties = {
  reservation: Reservation
  boats: Boat[]
  municipalities: Municipality[]
  organizations: Organization[]
}

export default React.memo(function ChangeForm({
  reservation,
  boats,
  municipalities,
  organizations
}: ChangeFormProperties) {
  const i18n = useTranslation()
  const navigate = useNavigate()
  const { showAllErrors, setShowAllErrors } = useFormErrorContext()

  const { mutateAsync: submitForm } = useMutation(switchSpaceMutation)

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
    () => initialFormState(i18n, boats, reservation),
    i18n.components.validationErrors,
    {
      onUpdate: (prev, next) => onReserveSpaceUpdate(prev, next, i18n, boats)
    }
  )

  const { reserver, boat, userAgreement, spaceTypeInfo } =
    useFormFields(formBind)

  const { branch, form: winterStorageFom } = useFormUnion(spaceTypeInfo)

  const onSubmit = async () => {
    const isValid = formBind.isValid() && organizationFormBind.isValid()
    if (!isValid) setShowAllErrors(true)
    else {
      await submitForm({
        id: reservation?.id,
        input: { ...formBind.value(), ...organizationFormBind.value() }
      })
      return (reservation.switchPriceDifference || 0) > 0
        ? navigate('/kuntalainen/venepaikka/maksa')
        : navigate(`/kuntalainen/venepaikka/vahvistus/${reservation.id}`)
    }
  }

  const updatedReservation = {
    ...reservation,
    storageType: winterStorageFom?.state?.storageType.domValue as
      | StorageType
      | undefined
  }

  return (
    <form className="column" onSubmit={(e) => e.preventDefault()}>
      <h1 className="title pb-l" id="boat-space-form-header">
        {i18n.reservation.formPage.title[updatedReservation.boatSpace.type](
          formatPlaceIdentifier(
            updatedReservation.boatSpace.section,
            updatedReservation.boatSpace.placeNumber,
            updatedReservation.boatSpace.locationName
          )
        )}
      </h1>
      <div className="block">
        <InfoBox text={i18n.reservation.formPage.info.switch} />
        <ReserverSection
          reserver={updatedReservation.citizen}
          bind={reserver}
        />
        <BoatSection bind={boat} />
        {branch === 'Winter' && <WinterStorageType bind={winterStorageFom} />}
        <FormSection>
          <ReservedSpace reservation={updatedReservation} />
          <SwitchPriceInfo
            priceDifference={reservation.switchPriceDifference}
          />
        </FormSection>
        <UserAgreementsSection bind={userAgreement} />
        {showAllErrors && <ValidationWarning />}
      </div>

      <Buttons>
        <ReservationCancel reservationId={reservation.id} type="button">
          {i18n.common.cancel}
        </ReservationCancel>
        <Button id="submit-button" type="primary" action={onSubmit}>
          {reservation.switchPriceDifference == undefined ||
          reservation.switchPriceDifference > 0
            ? i18n.reservation.formPage.submit.continueToPayment
            : i18n.reservation.formPage.submit.confirmReservation}
        </Button>
      </Buttons>
    </form>
  )
})

function SwitchPriceInfo({ priceDifference }: { priceDifference?: number }) {
  const i18n = useTranslation()

  if (priceDifference === undefined) {
    return null
  }

  const text = () => {
    if (priceDifference > 0)
      return i18n.reservation.paymentInfo.moreExpensive(
        formatCentsToEuros(priceDifference)
      )
    else if (priceDifference < 0)
      return i18n.reservation.paymentInfo.lessExpensive
    return i18n.reservation.paymentInfo.equal
  }

  return <InfoBox text={text()} />
}
