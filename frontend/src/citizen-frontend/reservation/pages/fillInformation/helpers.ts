import { Result } from 'lib-common/api'
import { StateOf } from 'lib-common/form/types'
import { Translations } from 'lib-customizations/vekkuli/citizen'

import { Boat } from '../../../shared/types'

import { BoatForm, transformBoatToFormBoat } from './formDefinitions'

type BoatFormUpdateProps = {
  prevBoatState: StateOf<BoatForm>
  nextBoatState: StateOf<BoatForm>
  newBoatStateStore: StateOf<BoatForm> | undefined
  setNewBoatStateStore: (state: StateOf<BoatForm> | undefined) => void
  citizenBoats: Result<Boat[]>
  i18n: Translations
}

export function onBoatFormUpdate({
  prevBoatState,
  nextBoatState,
  newBoatStateStore,
  setNewBoatStateStore,
  citizenBoats,
  i18n
}: BoatFormUpdateProps): StateOf<BoatForm> {
  const prevBoatId = prevBoatState.existingBoat.domValue
  const nextBoatId = nextBoatState.existingBoat.domValue

  if (prevBoatId !== nextBoatId) {
    const boatStateStoreValue = newBoatStateStore ?? prevBoatState
    const selectedBoat = citizenBoats
      .getOrElse([])
      .find((boat) => boat.id === nextBoatId)
    if (selectedBoat) {
      //Update state store if previous boat id is empty
      if (!prevBoatId) {
        setNewBoatStateStore({
          ...prevBoatState,
          ...{ existingBoat: boatStateStoreValue.existingBoat }
        })
      }
      return {
        ...nextBoatState,
        ...{
          ...transformBoatToFormBoat(selectedBoat, i18n),
          ...{ existingBoat: nextBoatState.existingBoat }
        }
      }
    }
    return {
      ...nextBoatState,
      ...boatStateStoreValue
    }
  }
  return nextBoatState
}
