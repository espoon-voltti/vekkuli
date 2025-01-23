import {
  FillBoatSpaceReservationInput,
  Municipality
} from 'citizen-frontend/api-types/reservation'
import { Reservation } from 'citizen-frontend/reservation/state'
import {
  Boat,
  BoatSpaceType,
  Citizen,
  Organization
} from 'citizen-frontend/shared/types'
import { mapped, object } from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'
import { Translations } from 'lib-customizations/vekkuli/citizen'

import { StoredSearchState } from '../../useStoredSearchState'

import initialBoatFormState, { boatForm, onBoatFormUpdate } from './boat'
import initialOrganizationFormState, {
  onOrganizationFormUpdate,
  organizationForm
} from './organization'
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
    userAgreement: userAgreementForm,
    organization: organizationForm
  }),
  ({
    reserver,
    boat,
    userAgreement,
    spaceTypeInfo,
    organization
  }): FillBoatSpaceReservationInput => {
    return {
      citizen: { ...reserver },
      organization: organization.organization,
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
  reserver: Citizen | undefined,
  spaceType: BoatSpaceType,
  municipalities: Municipality[],
  organizations: Organization[],
  storedState?: StoredSearchState,
  reservation?: Reservation
): StateOf<ReserveSpaceForm> {
  return {
    ...initialReserverFormState(reserver),
    organization: initialOrganizationFormState(
      i18n,
      municipalities,
      organizations
    ),
    boat: initialBoatFormState(
      i18n,
      boats,
      spaceType,
      storedState,
      reservation?.reservation.boat
    ),
    //winterStorage: initialWinterStorageFormState(i18n),
    spaceTypeInfo: initialSpaceTypeInfoFormState(i18n, spaceType, storedState),
    ...initialUserAgreementFormState(i18n)
  }
}

export const onReserveSpaceUpdate = (
  prev: StateOf<ReserveSpaceForm>,
  next: StateOf<ReserveSpaceForm>,
  i18n: Translations,
  boats: Boat[],
  organizationBoats: Record<string, Boat[]>,
  municipalities: Municipality[],
  organizations: Organization[]
): StateOf<ReserveSpaceForm> => {
  return {
    ...next,
    boat: onBoatFormUpdate({
      prev: prev.boat,
      next: next.boat,
      i18n,
      boats: getBoatsSelection(next, organizationBoats, boats)
    }),
    spaceTypeInfo: onSpaceTypeInfoUpdate({
      prev: prev.spaceTypeInfo,
      next: next.spaceTypeInfo
    }),
    organization: onOrganizationFormUpdate(
      prev.organization,
      next.organization,
      organizations,
      municipalities
    )
  }
}
const getBoatsSelection = (
  next: StateOf<ReserveSpaceForm>,
  organizationBoats: Record<string, Boat[]>,
  citizenBoats: Boat[]
) => {
  return next.organization.renterType.type.domValue === 'Organization'
    ? organizationBoats[next.organization.organizationSelection.domValue] || []
    : citizenBoats
}

/*
winterStorage: onWinterStorageFormUpdate({
  prev: prev.winterStorage,
  next: next.winterStorage
})
 */
