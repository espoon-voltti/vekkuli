import { FillBoatSpaceReservationInput } from 'citizen-frontend/api-types/reservation'
import { Boat, BoatSpaceType, Citizen } from 'citizen-frontend/shared/types'
import { mapped, object } from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'
import { Translations } from 'lib-customizations/vekkuli/citizen'

import initialBoatFormState, { boatForm, onBoatFormUpdate } from './boat'
import initialReserverFormState, { reserverForm } from './reserver'
import {
  initialSpaceTypeInfoFormState,
  onSpaceTypeInfoUpdate,
  spaceTypeInfoUnionForm
} from './spaceTypeInfo'
import initialUserAgreementFormState, {
  userAgreementForm
} from './userAgreement'

export const reserveSpaceForm = mapped(
  object({
    reserver: reserverForm,
    boat: boatForm,
    spaceTypeInfo: spaceTypeInfoUnionForm,
    userAgreement: userAgreementForm
  }),
  ({
    reserver,
    boat,
    userAgreement,
    spaceTypeInfo
  }): FillBoatSpaceReservationInput => {
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
        registrationNumber: boat.boatInfo.registrationNumber.number,
        hasNoRegistrationNumber:
          boat.boatInfo.registrationNumber.noRegisterNumber == undefined ||
          boat.boatInfo.registrationNumber.noRegisterNumber.length > 0,
        otherIdentification: boat.boatInfo.otherIdentification,
        extraInformation: boat.boatInfo.extraInformation,
        ownership: boat.ownership
      },
      certifyInformation: !!userAgreement.certified?.includes(true),
      agreeToRules: !!userAgreement.terms?.includes(true),
      storageType: spaceTypeInfo.value?.storageType || null,
      trailer: spaceTypeInfo.value?.trailerInfo || null
    }
  }
)

export type ReserveSpaceForm = typeof reserveSpaceForm

export function initialFormState(
  i18n: Translations,
  boats: Boat[],
  reserver: Citizen,
  spaceType: BoatSpaceType
): StateOf<ReserveSpaceForm> {
  return {
    ...initialReserverFormState(reserver),
    boat: initialBoatFormState(i18n, boats, spaceType),
    //winterStorage: initialWinterStorageFormState(i18n),
    spaceTypeInfo: initialSpaceTypeInfoFormState(i18n, spaceType),
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
    spaceTypeInfo: onSpaceTypeInfoUpdate({
      prev: prev.spaceTypeInfo,
      next: next.spaceTypeInfo
    })
  }
}
/*
winterStorage: onWinterStorageFormUpdate({
  prev: prev.winterStorage,
  next: next.winterStorage
})
 */
