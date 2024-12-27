import { mapped, object } from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'
import { Translations } from 'lib-customizations/vekkuli/citizen'

import { FillBoatSpaceReservationInput } from '../../../../api-types/reservation'
import { Boat, Citizen } from '../../../../shared/types'

import initialBoatFormState, { boatForm, onBoatFormUpdate } from './boat'
import initialReserverFormState, { reserverForm } from './reserver'
import initialUserAgreementFormState, {
  userAgreementForm
} from './userAgreement'
import initialWinterStorageFormState, {
  onWinterStorageFormUpdate,
  winterStorageForm
} from './winterStorage'

export const reserveSpaceForm = mapped(
  object({
    reserver: reserverForm,
    boat: boatForm,
    winterStorage: winterStorageForm,
    userAgreement: userAgreementForm
  }),
  ({ reserver, boat, userAgreement }): FillBoatSpaceReservationInput => {
    return {
      citizen: { ...reserver },
      organization: null,
      boat: {
        id: boat.boatInfo.id || undefined,
        name: boat.boatInfo.name,
        type: boat.boatInfo.type,
        width: boat.boatInfo.width,
        length: boat.boatInfo.length,
        depth: boat.boatInfo.depth,
        weight: boat.boatInfo.weight,
        registrationNumber: boat.boatInfo.registrationNumber,
        hasNoRegistrationNumber:
          boat.boatInfo.noRegisterNumber == undefined ||
          boat.boatInfo.noRegisterNumber.length > 0,
        otherIdentification: boat.boatInfo.otherIdentification,
        extraInformation: boat.boatInfo.extraInformation,
        ownership: boat.ownership
      },
      certifyInformation: !!userAgreement.agreements?.includes(true),
      agreeToRules: !!userAgreement.agreements?.includes(true)
    }
  }
)

export type ReserveSpaceForm = typeof reserveSpaceForm

export function initialFormState(
  i18n: Translations,
  boats: Boat[],
  reserver: Citizen
): StateOf<ReserveSpaceForm> {
  return {
    ...initialReserverFormState(reserver),
    boat: initialBoatFormState(i18n, boats),
    winterStorage: initialWinterStorageFormState(i18n),
    ...initialUserAgreementFormState(i18n)
  }
}

export const onReserveSpaceUpdate = (
  prev: StateOf<ReserveSpaceForm>,
  next: StateOf<ReserveSpaceForm>,
  i18n: Translations,
  boats: Boat[]
): StateOf<ReserveSpaceForm> => {
  return {
    ...next,
    boat: onBoatFormUpdate({
      prev: prev.boat,
      next: next.boat,
      i18n,
      citizenBoats: boats
    }),
    winterStorage: onWinterStorageFormUpdate({
      prev: prev.winterStorage,
      next: next.winterStorage
    })
  }
}
