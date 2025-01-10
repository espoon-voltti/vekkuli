import { Municipality } from 'citizen-frontend/api-types/reservation'
import {
  NewOrganization,
  Organization,
  ReserverType,
  reserverTypes
} from 'citizen-frontend/shared/types'
import { string } from 'lib-common/form/fields'
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
  phone: required(string()),
  email: required(string()),
  address: string(),
  postalCode: string(),
  city: string()
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
  address: '',
  postalCode: '',
  city: ''
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
          municipalityCode: parseInt(organization.value.municipality.code),
          municipalityName: organization.value.municipality.name
        }
      }
    }
  }
)
export type OrganizationForm = typeof organizationForm

export default function initialFormState(
  i18n: Translations,
  municipalities: Municipality[],
  organizations: Organization[]
): StateOf<OrganizationForm> {
  const selectionOptions: StateOf<OrganizationSelectionForm> = {
    domValue: '',
    options: organizations.map((organization) => ({
      domValue: organization.id,
      label: organization.name,
      value: organization
    }))
  }
  if (selectionOptions.options.length > 0)
    selectionOptions.options.push({
      domValue: '',
      label: 'Uusi yhteisö',
      value: null
    })

  return {
    organization: {
      branch: 'noOrganization',
      state: null
    },
    organizationSelection: selectionOptions,
    newOrganizationCache: initialInfoFormState(municipalities),
    renterType: {
      type: {
        domValue: 'Citizen',
        options: reserverTypes.map((type) => ({
          domValue: type,
          label: i18n.boatSpace.renterType[type],
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
      address: organization.address || '',
      postalCode: organization.postalCode || '',
      city: organization.city || '',
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
