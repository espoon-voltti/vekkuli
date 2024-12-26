import { CheckboxField } from 'lib-components/form/CheckboxField'
import { NumberField } from 'lib-components/form/NumberField'
import { RadioField } from 'lib-components/form/RadioField'
import { SelectField } from 'lib-components/form/SelectField'
import TextField from 'lib-components/form/TextField'
import React, { useEffect, useRef } from 'react'

import { BoundForm, useFormFields } from 'lib-common/form/hooks'

import { Boat } from '../../../../shared/types'
import { BoatForm, initialBoatValue } from '../formDefinitions/boat'

export default React.memo(function Boat({
  bind,
  boats
}: {
  bind: BoundForm<BoatForm>
  boats: Boat[]
}) {
  const initialized = useRef(false)

  useEffect(() => {
    if (!initialized.current) {
      const options = boats.map((boat) => ({
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

      bind.update((prev) => ({
        ...prev,
        existingBoat: {
          domValue: '',
          options: options
        }
      }))
      initialized.current = true
    }
  }, [boats, bind])

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
    extraInformation,
    existingBoat
  } = useFormFields(bind)

  return (
    <div className="form-section">
      <h3 className="header">Veneen tiedot</h3>
      {existingBoat && existingBoat.state.options.length > 0 && (
        <RadioField id="boat-select" name="boatSelect" bind={existingBoat} />
      )}
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
    </div>
  )
})
