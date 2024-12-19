import { Button, Buttons, Column, Columns } from 'lib-components/dom'
import React from 'react'

import TextField from '../../../../lib-components/form/TextField'
import { BoatSpaceReservation } from '../../../api-types/reservation'

export default React.memo(function Reservation({
  reservation
}: {
  reservation: BoatSpaceReservation
}) {
  return (
    <div className="reservation-card">
      <Columns isVcentered>
        <Column isNarrow>
          <h4>Venepaikka: Haukilahti B 311</h4>
        </Column>
      </Columns>
      <Columns>
        <Column>
          <TextField label="Satama" value="Haukilahti" readonly={true} />
          <TextField label="Leveys (m)" value="3.00" readonly={true} />
          <TextField label="Varaus tehty" value="08.09.2024" readonly={true} />
        </Column>
        <Column>
          <TextField label="Paikka" value="B 311" readonly={true} />
          <TextField label="Pituus (m)" value="6.50" readonly={true} />
          <TextField
            label="Varaus voimassa"
            value="31.12.2024 asti"
            readonly={true}
          />
        </Column>
        <Column>
          <TextField
            label="Paikan tyyppi"
            value="Laituripaikka"
            readonly={true}
          />
          <TextField label="Hinta" value="345,13" readonly={true} />
          <TextField
            label="Paikalla oleva vene"
            value="Testivene"
            readonly={true}
          />
        </Column>
        <Column>
          <TextField label="Varuste" value="Aisa" readonly={true} />
          <TextField label="Maksettu" value="08.09.2024" readonly={true} />
        </Column>
      </Columns>

      <Buttons>
        <Button type="danger-outlined">Irtisano paikka</Button>
      </Buttons>
    </div>
  )
})
