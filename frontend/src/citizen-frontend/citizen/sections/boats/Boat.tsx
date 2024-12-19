import { Column, Columns } from 'lib-components/dom'
import { NumberField } from 'lib-components/form/NumberField'
import { SelectField } from 'lib-components/form/SelectField'
import TextField from 'lib-components/form/TextField'
import { EditLink } from 'lib-components/links'
import React from 'react'

import { useForm, useFormFields } from 'lib-common/form/hooks'

import { useTranslation } from '../../../localization'
import { Boat } from '../../../shared/types'

import { boatForm, transformBoatToFormBoat } from './formDefinitions'

export default React.memo(function Boat({ boat }: { boat: Boat }) {
  const i18n = useTranslation()
  const bind = useForm(
    boatForm,
    () => transformBoatToFormBoat(boat, i18n),
    i18n.components.validationErrors
  )
  const {
    name,
    weight,
    length,
    width,
    type,
    depth,
    registrationNumber,
    ownershipStatus,
    otherIdentification,
    extraInformation
  } = useFormFields(bind)
  return (
    <div className="reservation-card">
      <Columns isVcentered>
        <Column isNarrow>
          <h4>{boat.name}</h4>
        </Column>
        <Column>
          <Columns>
            <Column isNarrow toRight>
              <EditLink>Muokkaa veneen tietoja</EditLink>
            </Column>
          </Columns>
        </Column>
      </Columns>
      <Columns>
        <Column>
          <TextField
            label="Veneen nimi"
            name="name"
            bind={name}
            readonly={true}
          />
          <NumberField
            label="Paino (kg)"
            name="weight"
            bind={weight}
            readonly={true}
          />
        </Column>
        <Column>
          <SelectField label="Veneen tyyppi" bind={type} readonly={true} />
          <NumberField
            label="Syväys (m)"
            name="depth"
            bind={depth}
            readonly={true}
          />
        </Column>
        <Column>
          <NumberField
            label="Leveys (m)"
            name="width"
            bind={width}
            readonly={true}
          />
          <TextField
            label="Rekisteritunnus"
            name="registrationNumber"
            bind={registrationNumber}
            readonly={true}
          />
        </Column>
        <Column>
          <TextField
            label="Pituus (m)"
            name="length"
            bind={length}
            readonly={true}
          />
          <SelectField
            label="Omistussuhde"
            name="ownershipStatus"
            bind={ownershipStatus}
            readonly={true}
          />
        </Column>
        <Column>
          <TextField
            label="Muu tunniste"
            name="otherIdentification"
            bind={otherIdentification}
            readonly={true}
          />
          <TextField
            label="Lisätiedot"
            name="extraInformation"
            bind={extraInformation}
            readonly={true}
          />
        </Column>
      </Columns>
    </div>
  )
})
