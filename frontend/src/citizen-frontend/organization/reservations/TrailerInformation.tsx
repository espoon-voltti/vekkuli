import { Column, Columns } from 'lib-components/dom'
import { NumberField, TextField } from 'lib-components/form'
import React from 'react'

import { Trailer } from 'citizen-frontend/shared/types'

export default React.memo(function TrailerInformation({
  trailer
}: {
  trailer: Trailer
}) {
  return (
    <>
      <Columns isVCentered>
        <Column isNarrow>
          <h4>Trailerin tiedot</h4>
        </Column>
      </Columns>
      <Columns>
        <Column isOneQuarter>
          <TextField
            label="Rekisterinumero"
            value={trailer.registrationNumber}
            readonly={true}
          />
        </Column>
        <Column isOneQuarter>
          <NumberField
            label="Leveys (m)"
            value={trailer.width}
            readonly={true}
            precision={2}
          />
        </Column>
        <Column isOneQuarter>
          <NumberField
            label="Pituus (m)"
            value={trailer.length}
            readonly={true}
            precision={2}
          />
        </Column>
      </Columns>
    </>
  )
})
