import { CheckboxField } from 'lib-components/form/CheckboxField'
import { NumberField } from 'lib-components/form/NumberField'
import { SelectField } from 'lib-components/form/SelectField'
import TextField from 'lib-components/form/TextField'
import React from 'react'

import { BoundForm, useFormFields } from 'lib-common/form/hooks'

import { BoatInfoForm } from '../../formDefinitions/boat'

export default React.memo(function Boat({
  bind
}: {
  bind: BoundForm<BoatInfoForm>
}) {
  const {
    name,
    type,
    width,
    length,
    weight,
    depth,
    registrationNumber,
    noRegisterNumber,
    otherIdentification,
    extraInformation
  } = useFormFields(bind)

  return (
    <>
      <div className="columns">
        <div className="column is-one-quarter">
          <TextField id="boatName" label="Veneen nimi" bind={name} />
        </div>
        <div className="column is-one-quarter">
          <SelectField
            id="boat-type"
            name="boatType"
            label="Venetyyppi"
            bind={type}
          />
        </div>
        <div className="column is-one-quarter">
          <NumberField
            id="boat-width"
            label="Leveys (m)"
            bind={width}
            name="width"
            step={0.01}
            min={0}
            max={9999999}
          />
        </div>
        <div className="column is-one-quarter">
          <NumberField
            id="boat-height"
            label="Pituus (m)"
            bind={length}
            name="length"
            step={0.01}
            min={0}
            max={9999999}
          />
        </div>
      </div>
      <div className="columns is-vcentered">
        <div className="column is-one-quarter">
          <NumberField
            id="boat-depth"
            label="Syväys (m)"
            bind={depth}
            name="depth"
            step={0.01}
            min={0}
            max={9999999}
          />
        </div>
        <div className="column is-one-quarter">
          <NumberField
            id="boat-weight"
            label="Paino (kg)"
            bind={weight}
            name="weight"
            step={0.01}
            min={1}
            max={9999999}
          />
        </div>
        {bind.state.noRegisterNumber.domValues.length === 0 && (
          <div className="column is-one-quarter">
            <TextField
              id="register-number"
              label="Rekisteritunnus"
              bind={registrationNumber}
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
            label="Muu tunniste"
            bind={otherIdentification}
          />
        </div>
        <div className="column is-one-quarter">
          <TextField
            id="additional-info"
            label="Lisätiedot"
            bind={extraInformation}
          />
        </div>
      </div>
    </>
  )
})
