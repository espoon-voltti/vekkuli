import { Column, Columns } from 'lib-components/dom'
import { CheckboxField } from 'lib-components/form/CheckboxField'
import { NumberField } from 'lib-components/form/NumberField'
import { RadioField } from 'lib-components/form/RadioField'
import { SelectField } from 'lib-components/form/SelectField'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { BoundForm, useFormFields, useFormUnion } from 'lib-common/form/hooks'

import { StorageInfo } from './StorageInfo'
import { SearchForm } from './formDefinitions'
import { Link } from "react-router";
import { OpenInNew } from "../../../../lib-icons";

interface SearchFiltersProps {
  bind: BoundForm<SearchForm>
}

export default React.memo(function SearchFilters({ bind }: SearchFiltersProps) {
  const i18n = useTranslation()
  const { boatSpaceType, boatSpaceUnionForm } = useFormFields(bind)
  const { form, branch } = useFormUnion(boatSpaceUnionForm)
  const {
    boatType,
    width,
    length,
    amenities,
    harbor,
    storageAmenity: storageAmenities
  } = useFormFields(form)

  return (
    <form className="block" data-testid="boat-space-filter">
      <h2 className="subtitle" id="search-page-header">
        {i18n.reservation.searchPage.filters.title}
      </h2>
      <div className="block">
        <RadioField
          id="boat-space-type"
          name="boatSpaceType"
          label={i18n.reservation.searchPage.filters.boatSpaceType}
          bind={boatSpaceType}
        />
      </div>
      <div className="filter-checkbox-container">
        <CheckboxField
          id="harbor"
          name="harbor"
          bind={harbor}
          label={i18n.reservation.searchPage.filters.harborHeader}
          infoText={
            i18n.reservation.searchPage.filters.branchSpecific[branch]
              .harborInfo
          }
        />

        <div className="is-primary-color ">
          <Link to={i18n.reservation.searchPage.filters.additionalHarborPlaceInfoLink} className="link open-in-new-tab" aria-label={i18n.header.openInANewWindow} target="_blank">
            <span>{i18n.reservation.searchPage.filters.additionalHarborPlaceInfo}</span>
            <OpenInNew />
          </Link>
        </div>
      </div>

      {boatType.state.options.length === 0 ? null : (
        <div className="block">
          <SelectField
            id="boat-type"
            name="boatType"
            label={i18n.reservation.searchPage.filters.boatType}
            bind={boatType}
          />
        </div>
      )}
      {storageAmenities.state.options.length !== 0 && (
        <RadioField
          id="storage-type-amenities"
          name="amenities"
          label={i18n.reservation.searchPage.filters.storageTypeAmenities}
          bind={storageAmenities}
          horizontal={true}
          required={true}
        />
      )}
      <Columns>
        <Column>
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
            precision={2}
          />
        </Column>
        <Column>
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
            precision={2}
          />
        </Column>
      </Columns>
      <StorageInfo boatSpaceType={branch} />
      {amenities.state.options.length === 0 ? null : (
        <div className="block">
          <CheckboxField
            id="amenities"
            name="amenities"
            bind={amenities}
            label={i18n.reservation.searchPage.filters.amenityHeader}
          />
        </div>
      )}
    </form>
  )
})
