import { mapped, object } from 'lib-common/form/form'
import { Translations } from 'lib-customizations/vekkuli/citizen'

import { FillBoatSpaceReservationInput } from '../../../../api-types/reservation'
import { formatMToCm } from '../../../../shared/formatters'

import initialBoatFormState, { boatForm, boatOwnershipTypeForm } from './boat'
import initialReserverFormState, { reserverForm } from './reserver'
import initialUserAgreementFormState, {
  userAgreementForm
} from './userAgreement'

export const reserveSpaceForm = mapped(
  object({
    reserver: reserverForm,
    boat: boatForm,
    boatOwnership: boatOwnershipTypeForm,
    userAgreement: userAgreementForm
  }),
  ({
    reserver,
    boat,
    boatOwnership,
    userAgreement
  }): FillBoatSpaceReservationInput => {
    return {
      citizen: { ...reserver },
      organization: null,
      boat: {
        id: boat.id || undefined,
        name: boat.name,
        type: boat.type,
        width: formatMToCm(boat.width),
        length: formatMToCm(boat.length),
        depth: formatMToCm(boat.depth),
        weight: boat.weight,
        registrationNumber: boat.registrationNumber,
        hasNoRegistrationNumber:
          boat.noRegisterNumber == undefined ||
          boat.noRegisterNumber.length > 0,
        otherIdentification: boat.otherIdentification,
        extraInformation: boat.extraInformation,
        ownership: boatOwnership.status
      },
      certifyInformation: !!userAgreement.agreements?.includes(true),
      agreeToRules: !!userAgreement.agreements?.includes(true)
    }
  }
)

export type ReserveSpaceForm = typeof reserveSpaceForm

export function initialFormState(i18n: Translations) {
  return {
    ...initialReserverFormState(),
    ...initialBoatFormState(i18n),
    ...initialUserAgreementFormState(i18n)
  }
}
