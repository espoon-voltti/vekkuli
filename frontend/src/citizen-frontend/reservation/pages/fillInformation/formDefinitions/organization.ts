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

import { Municipality } from '../../../../api-types/reservation'
import {
  NewOrganization,
  Organization,
  ReserverType
} from '../../../../shared/types'

export const renterTypeForm = object({
  type: oneOf<ReserverType>()
})
export type RenterTypeForm = typeof renterTypeForm

const organizationInfoForm = object({
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

export type OrganizationUnionBranch = 'noOrganization' | 'existing' | 'new'

export const existingOrganizationForm = object({
  details: organizationInfoForm,
  newCache: value<StateOf<OrganizationInfoForm>>()
})

export const newOrganizationForm = object({
  details: organizationInfoForm
})

export const organisationUnionForm = union({
  noOrganization: value<null>(),
  existing: existingOrganizationForm,
  new: newOrganizationForm
})
export type OrganisationUnionForm = typeof organisationUnionForm

export const organizationForm = mapped(
  object({
    renterType: renterTypeForm,
    organization: organisationUnionForm
  }),
  ({
    organization
  }): { organization: Organization | NewOrganization | null } => {
    console.log('mapping organization', organization)
    if (organization.branch === 'noOrganization')
      return {
        organization: null
      }

    return {
      organization: {
        ...organization.value.details,
        ...{
          municipalityCode: parseInt(
            organization.value.details.municipality.code
          )
        }
      }
    }
  }
)
export type OrganizationForm = typeof organizationForm

export default function initialFormState(
  i18n: Translations
): StateOf<OrganizationForm> {
  return {
    organization: {
      branch: 'noOrganization',
      state: null
    },
    renterType: {
      type: {
        domValue: ReserverType.Citizen,
        options: Object.values(ReserverType).map((type) => ({
          domValue: type,
          label: i18n.boatSpace.renterType[type],
          value: type
        }))
      }
    }
  }
}

export const initialUnionFormState = (
  branch: OrganizationUnionBranch,
  municipalities: Municipality[]
): StateOf<OrganisationUnionForm> => {
  const organization = {
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
  }

  switch (branch) {
    case 'existing':
      return {
        branch: branch,
        state: {
          details: organization,
          newCache: organization
        }
      }
    case 'new':
      return {
        branch: branch,
        state: {
          details: organization
        }
      }
  }

  return {
    branch: branch,
    state: null
  }
}
