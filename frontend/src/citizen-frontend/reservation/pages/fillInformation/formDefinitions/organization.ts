import { Municipality } from 'citizen-frontend/api-types/reservation'
import {
  NewOrganization,
  Organization,
  ReserverType,
  reserverTypes
} from 'citizen-frontend/shared/types'
import {number, string, whitespaceTrimmedString} from 'lib-common/form/fields'
import {
  mapped,
  object,
  oneOf,
  required,
  union,
  value
} from 'lib-common/form/form'
import { StateOf } from 'lib-common/form/types'
import { Translations } from 'lib-customizations/vekkuli/citizen'

export const renterTypeForm = object({
  type: oneOf<ReserverType>()
})
export type RenterTypeForm = typeof renterTypeForm

const organizationInfoForm = object({
  id: string(),
  name: required(string()),
  businessId: required(string()),
  municipality: required(oneOf<Municipality>()),
  phone: required(whitespaceTrimmedString()),
  email: required(whitespaceTrimmedString()),
  streetAddress: string(),
  postalCode: string(),
  postOffice: string(),
  discountPercentage: number()
})
export type OrganizationInfoForm = typeof organizationInfoForm

const initialInfoFormState = (
  municipalities: Municipality[]
): StateOf<OrganizationInfoForm> => ({
  id: '',
  name: '',
  businessId: '',
  municipality: {
    domValue: '',
    options: municipalities.map((municipality) => ({
      domValue: municipality.code.toString(),
      label: municipality.name,
      value: municipality
    }))
  },
  phone: '',
  email: '',
  streetAddress: '',
  postalCode: '',
  postOffice: '',
  discountPercentage: 0
})

export type OrganizationUnionBranch = 'noOrganization' | 'existing' | 'new'

export const organizationSelectionForm = oneOf<Organization | null>()
export type OrganizationSelectionForm = typeof organizationSelectionForm

export const organisationUnionForm = union({
  noOrganization: value<null>(),
  existing: organizationInfoForm,
  new: organizationInfoForm
})
export type OrganisationUnionForm = typeof organisationUnionForm
const initialUnionFormState = (
  branch: OrganizationUnionBranch,
  municipalities: Municipality[],
  cache?: StateOf<OrganizationInfoForm> | null
): StateOf<OrganisationUnionForm> => {
  const organization = initialInfoFormState(municipalities)

  switch (branch) {
    case 'existing':
      return {
        branch: branch,
        state: organization
      }
    case 'new':
      return {
        branch: branch,
        state: cache || organization
      }
  }

  return {
    branch: branch,
    state: null
  }
}

export const organizationForm = mapped(
  object({
    renterType: renterTypeForm,
    organizationSelection: organizationSelectionForm,
    organization: organisationUnionForm,
    newOrganizationCache: value<StateOf<OrganizationInfoForm>>()
  }),
  ({
    organization
  }): { organization: Organization | NewOrganization | null } => {
    if (organization.branch === 'noOrganization')
      return {
        organization: null
      }

    return {
      organization: {
        ...organization.value,
        ...{
          municipalityCode: organization.value.municipality.code,
          municipalityName: organization.value.municipality.name,
          discountPercentage: organization.value.discountPercentage
        }
      }
    }
  }
)
export type OrganizationForm = typeof organizationForm

export default function initialFormState(
  municipalities: Municipality[],
  organizations: Organization[],
  canCitizenReserve: boolean
): StateOf<OrganizationForm> {
  const selectionOptions: StateOf<OrganizationSelectionForm> = {
    domValue: organizations[0]?.id || '',
    options: organizations.map((organization) => ({
      domValue: organization.id,
      label: organization.name,
      value: organization
    }))
  }

  const availableRenterTypes = reserverTypes.filter(
    (type) => type !== 'Citizen' || canCitizenReserve
  )

  const defaultChoice = availableRenterTypes[0]
  const defaultOrganization =
    defaultChoice === 'Citizen'
      ? {
          branch: 'noOrganization' as const,
          state: null
        }
      : {
          branch: 'existing' as const,
          state: transformOrganizationToFormOrganization(
            organizations[0],
            municipalities
          )
        }

  return {
    organization: defaultOrganization,
    organizationSelection: selectionOptions,
    newOrganizationCache: initialInfoFormState(municipalities),
    renterType: {
      type: {
        domValue: defaultChoice,
        options: availableRenterTypes.map((type) => ({
          domValue: type,
          label: (i18n: Translations) => i18n.boatSpace.renterType[type],
          value: type
        }))
      }
    }
  }
}

export const onOrganizationFormUpdate = (
  prev: StateOf<OrganizationForm>,
  next: StateOf<OrganizationForm>,
  organizations: Organization[],
  municipalities: Municipality[]
): StateOf<OrganizationForm> => {
  const nextBranch = determineOrganizationUnionBranch(next)
  const prevBranch = determineOrganizationUnionBranch(prev)
  const prevOrganizationId = prev.organizationSelection.domValue
  const nextOrganizationId = next.organizationSelection.domValue
  if (prevBranch !== nextBranch || prevOrganizationId !== nextOrganizationId) {
    const newOrganizationCache: StateOf<OrganizationInfoForm> =
      prevBranch === 'new'
        ? prev.organization.state!
        : prev.newOrganizationCache

    switch (nextBranch) {
      case 'new':
        return {
          ...next,
          ...{
            organization: {
              branch: nextBranch,
              state: next.newOrganizationCache
            }
          }
        }
      case 'existing': {
        const foundOrganization = organizations.find(
          (organization) => organization.id === nextOrganizationId
        )

        return {
          ...next,
          newOrganizationCache,
          ...{
            organization: {
              branch: nextBranch,
              state: transformOrganizationToFormOrganization(
                foundOrganization,
                municipalities
              )
            }
          }
        }
      }
      default:
        return {
          ...next,
          newOrganizationCache,
          ...{
            organization: {
              ...initialUnionFormState(nextBranch, municipalities, null)
            }
          }
        }
    }
  }
  return next
}

export const determineOrganizationUnionBranch = (
  state: StateOf<OrganizationForm>
): OrganizationUnionBranch => {
  const organizationId = state.organizationSelection.domValue
  const renterType = state.renterType.type.domValue

  if (renterType === 'Organization') {
    return organizationId !== '' ? 'existing' : 'new'
  }
  return 'noOrganization'
}

const transformOrganizationToFormOrganization = (
  organization: Organization | undefined,
  municipalities: Municipality[]
): StateOf<OrganizationInfoForm> => {
  if (organization === undefined) {
    return initialInfoFormState(municipalities)
  }
  return {
    ...organization,
    ...{
      streetAddress: organization.streetAddress || '',
      postalCode: organization.postalCode || '',
      postOffice: organization.postOffice || '',
      municipality: {
        domValue: organization.municipalityCode.toString(),
        options: municipalities.map((municipality) => ({
          domValue: municipality.code.toString(),
          label: municipality.name,
          value: municipality
        }))
      }
    }
  }
}
