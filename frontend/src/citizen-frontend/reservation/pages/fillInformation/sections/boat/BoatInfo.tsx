import { CheckboxField } from 'lib-components/form/CheckboxField'
import { NumberField } from 'lib-components/form/NumberField'
import { SelectField } from 'lib-components/form/SelectField'
import TextField from 'lib-components/form/TextField'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { BoundForm, useFormFields } from 'lib-common/form/hooks'

import { BoatSpace } from '../../../../../shared/types'
import ReservationCancel from '../../../../components/ReservationCancel'
import { BoatInfoForm } from '../../formDefinitions/boat'

export default React.memo(function Boat({
  bind,
  boatSpace,
  reservationId
}: {
  bind: BoundForm<BoatInfoForm>
  boatSpace: BoatSpace
  reservationId: number
}) {
  const i18n = useTranslation()
  const {
    name,
    type,
    width,
    length,
    weight,
    depth,
    registrationNumber,
    otherIdentification,
    extraInformation
  } = useFormFields(bind)

  const { noRegisterNumber, number: registrationNumberValue } =
    useFormFields(registrationNumber)

  const widthOrNaN = parseFloat(width.state)
  const lengthOrNaN = parseFloat(length.state)
  const weightOrNaN = parseFloat(weight.state)
  const minLength = boatSpace.minLength
  const maxLength = boatSpace.maxLength
  const minWidth = boatSpace.minWidth
  const maxWidth = boatSpace.maxWidth

  const showSizeWarning =
    (!isNaN(lengthOrNaN) &&
      ((minLength !== null && lengthOrNaN < minLength) ||
        (maxLength !== null && lengthOrNaN > maxLength))) ||
    (!isNaN(widthOrNaN) &&
      ((minWidth !== null && widthOrNaN < minWidth) ||
        (maxWidth !== null && widthOrNaN > maxWidth)))

  const maxWeight = 15000
  const showWeightWarning = !isNaN(weightOrNaN) && weightOrNaN > maxWeight

  return (
    <>
      <div className="columns">
        <div className="column is-one-quarter">
          <TextField
            id="boatName"
            label={i18n.boat.boatName}
            bind={name}
            required={true}
          />
        </div>
        <div className="column is-one-quarter">
          <SelectField
            id="boat-type"
            name="boatType"
            label={i18n.reservation.searchPage.filters.boatType}
            required={true}
            bind={type}
          />
        </div>
        <div className="column is-one-quarter">
          <NumberField
            id="boat-width"
            label={i18n.common.unit.dimensions.widthInMeters}
            bind={width}
            name="width"
            step={0.01}
            min={0.01}
            max={9999999}
            required={true}
            precision={2}
          />
        </div>
        <div className="column is-one-quarter">
          <NumberField
            id="boat-height"
            label={i18n.common.unit.dimensions.lengthInMeters}
            bind={length}
            name="length"
            step={0.01}
            min={0.01}
            max={9999999}
            required={true}
            precision={2}
          />
        </div>
      </div>
      {showSizeWarning && <BoatSizeWarning reservationId={reservationId} />}
      <div className="columns is-vcentered">
        <div className="column is-one-quarter">
          <NumberField
            id="boat-depth"
            label={i18n.boat.boatDepthInMeters}
            bind={depth}
            name="depth"
            step={0.01}
            min={0.01}
            max={9999999}
            required={true}
            precision={2}
          />
        </div>
        <div className="column is-one-quarter">
          <NumberField
            id="boat-weight"
            label={i18n.boat.boatWeightInKg}
            bind={weight}
            name="weight"
            step={0.01}
            min={1}
            max={9999999}
            required={true}
          />
        </div>
      </div>
      {showWeightWarning && <BoatWeightWarning reservationId={reservationId} />}
      <div className="columns is-vcentered">
        {noRegisterNumber.state.domValues.length === 0 && (
          <div className="column is-one-quarter">
            <TextField
              id="register-number"
              label={i18n.boat.registrationNumber}
              bind={registrationNumberValue}
              required={true}
            />
          </div>
        )}
        <div className="column is-one-quarter">
          <CheckboxField
            id="no-register-number"
            name="noRegisterNumber"
            bind={noRegisterNumber}
            isFullWidth={true}
          />
        </div>
      </div>
      <div className="columns is-vcentered">
        <div className="column is-one-quarter">
          <TextField
            id="other-identifier"
            label={i18n.boat.otherIdentifier}
            bind={otherIdentification}
            required={true}
          />
        </div>
        <div className="column is-one-quarter">
          <TextField
            id="additional-info"
            label={i18n.boat.additionalInfo}
            bind={extraInformation}
          />
        </div>
      </div>
    </>
  )
})

const BoatSizeWarning = ({ reservationId }: { reservationId: number }) => {
  const i18n = useTranslation()

  return (
    <div className="columns is-vcentered">
      <div className="warning" id="boatSize-warning">
        <p className="block">{i18n.boat.boatSizeWarning}</p>
        <p className="block">{i18n.boat.boatSizeWarningExplanation}</p>
        <ReservationCancel reservationId={reservationId} type="link">
          {i18n.reservation.goBack}
        </ReservationCancel>
      </div>
    </div>
  )
}

const BoatWeightWarning = ({ reservationId }: { reservationId: number }) => {
  const i18n = useTranslation()
  return (
    <div className="columns is-vcentered">
      <div className="warning" id="boatWeight-warning">
        <p className="block">{i18n.boat.boatWeightWarning}</p>
        <p className="block">{i18n.boat.boatWeightWarning2}</p>
        <ReservationCancel reservationId={reservationId} type="link">
          {i18n.reservation.goBack}
        </ReservationCancel>
      </div>
    </div>
  )
}
