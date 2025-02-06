import { CheckboxField } from 'lib-components/form/CheckboxField'
import { NumberField } from 'lib-components/form/NumberField'
import { SelectField } from 'lib-components/form/SelectField'
import TextField from 'lib-components/form/TextField'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { BoundForm, useFormFields } from 'lib-common/form/hooks'

import { BoatInfoForm } from '../../formDefinitions/boat'

export default React.memo(function Boat({
  bind
}: {
  bind: BoundForm<BoatInfoForm>
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
            min={0}
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
            min={0}
            max={9999999}
            required={true}
            precision={2}
          />
        </div>
      </div>
      <div className="columns is-vcentered">
        <div className="column is-one-quarter">
          <NumberField
            id="boat-depth"
            label={i18n.boat.boatDepthInMeters}
            bind={depth}
            name="depth"
            step={0.01}
            min={0}
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
