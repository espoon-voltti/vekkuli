import { CheckboxField } from 'lib-components/form/CheckboxField'
import { NumberField } from 'lib-components/form/NumberField'
import { RadioField } from 'lib-components/form/RadioField'
import { SelectField } from 'lib-components/form/SelectField'
import React from 'react'

import { BoundForm, useFormFields, useFormUnion } from 'lib-common/form/hooks'

import { useTranslation } from '../../../localization'

import { SearchForm } from './formDefinitions'

interface SearchFiltersProps {
  bind: BoundForm<SearchForm>
}

export default React.memo(function SearchFilters({ bind }: SearchFiltersProps) {
  const i18n = useTranslation()
  const { boatSpaceType, boatSpaceUnionForm } = useFormFields(bind)
  const { form, branch } = useFormUnion(boatSpaceUnionForm)
  const { boatType, width, length, amenities, harbor } = useFormFields(form)

  return (
    <form className="block">
      <h2 className="subtitle" id="search-page-header">
        Venepaikan hakuehdot
      </h2>
      <div className="block">
        <RadioField
          id="boat-space-type"
          name="boatSpaceType"
          label={i18n.reservation.searchPage.filters.boatSpaceType}
          bind={boatSpaceType}
        />
      </div>
      <div className="block">
        <SelectField
          id="boat-type"
          name="boatType"
          label={i18n.reservation.searchPage.filters.boatType}
          bind={boatType}
        />
      </div>
      <div className="columns">
        <div className="column">
          <NumberField
            id="boat-width"
            required={true}
            label={
              i18n.reservation.searchPage.filters.branchSpecific[branch].width
            }
            bind={width}
            name="width"
            step={0.01}
            min={1}
            max={9999999}
          />
        </div>
        <div className="column">
          <NumberField
            id="boat-length"
            label={
              i18n.reservation.searchPage.filters.branchSpecific[branch].length
            }
            required={true}
            bind={length}
            name="length"
            step={0.01}
            min={1}
            max={9999999}
          />
        </div>
      </div>
      {amenities.state.options.length === 0 ? null : (
        <div className="block">
          <CheckboxField
            id="amenities"
            name="amenities"
            bind={amenities}
            label="Varuste"
          />
        </div>
      )}
      {harbor.state.options.length === 0 ? null : (
        <div className="block">
          <CheckboxField
            id="harbor"
            name="harbor"
            bind={harbor}
            label="Satama"
          />
        </div>
      )}
    </form>
  )
})
