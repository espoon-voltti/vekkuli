import { Column, Columns } from 'lib-components/dom'
import { NumberField } from 'lib-components/form/NumberField'
import { SelectField } from 'lib-components/form/SelectField'
import TextField from 'lib-components/form/TextField'

import React from 'react'

import { Boat } from 'citizen-frontend/shared/types'
import { useTranslation } from '../../localization'

export default React.memo(function Boat({ boat }: { boat: Boat }) {
  const i18n = useTranslation()
  return (
    <div className="reservation-card">
      <Columns isVCentered>
        <Column isNarrow>
          <h4>{boat.name}</h4>
        </Column>
      </Columns>
      <Columns>
        <Column>
          <TextField
            label="Veneen nimi"
            name="name"
            value={boat.name}
            readonly={true}
          />
          <NumberField
            label="Paino (kg)"
            name="weight"
            value={boat.weight}
            readonly={true}
          />
          <TextField
            label="Muu tunniste"
            name="otherIdentification"
            value={boat.otherIdentification}
            readonly={true}
          />
        </Column>
        <Column>
          <SelectField
            label="Veneen tyyppi"
            value={i18n.boatSpace.boatType[boat.type]}
            readonly={true}
          />
          <NumberField
            label="Syväys (m)"
            name="depth"
            value={boat.depth}
            readonly={true}
          />
          <TextField
            label="Lisätiedot"
            name="extraInformation"
            value={boat.extraInformation || undefined}
            readonly={true}
          />
        </Column>
        <Column>
          <NumberField
            label="Leveys (m)"
            name="width"
            value={boat.width}
            readonly={true}
          />
          <TextField
            label="Rekisteritunnus"
            name="registrationNumber"
            value={boat.registrationNumber}
            readonly={true}
          />
        </Column>
        <Column>
          <TextField
            label="Pituus (m)"
            name="length"
            value={String(boat.length)}
            readonly={true}
          />
          <TextField
            label="Omistussuhde"
            name="ownershipStatus"
            value={i18n.boatSpace.ownershipStatus[boat.ownership]}
            readonly={true}
          />
        </Column>
      </Columns>
    </div>
  )
})
