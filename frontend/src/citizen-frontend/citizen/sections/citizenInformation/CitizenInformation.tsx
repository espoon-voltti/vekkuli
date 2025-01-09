import { Button, Buttons, Column, Columns, Container } from 'lib-components/dom'
import { FormSection } from 'lib-components/form'
import TextField from 'lib-components/form/TextField'
import { EditLink } from 'lib-components/links'
import React from 'react'

import { User } from 'citizen-frontend/auth/state'
import { useTranslation } from 'citizen-frontend/localization'
import { Organization } from 'citizen-frontend/shared/types'
import { useForm, useFormFields } from 'lib-common/form/hooks'
import { useMutation } from 'lib-common/query'

import { updateCitizenInformationMutation } from '../../queries'

import CitizenOrganizations from './CitizenOrganizations'
import { citizenInformationForm, initialFormState } from './formDefinitions'

export default React.memo(function CitizenInformation({
  user,
  organizations
}: {
  user: User
  organizations: Organization[]
}) {
  const i18n = useTranslation()
  const [editMode, setEditMode] = React.useState(false)
  const { mutateAsync: updateInfo, isPending } = useMutation(
    updateCitizenInformationMutation
  )
  const form = useForm(
    citizenInformationForm,
    () => initialFormState(user),
    i18n.components.validationErrors
  )
  const cancel = () => {
    form.set(initialFormState(user))
    setEditMode(false)
  }

  const onSubmit = async () => {
    if (form.isValid()) {
      await updateInfo(form.value())
      setEditMode(false)
    }
  }

  const { phone, email } = useFormFields(form)
  return (
    <Container isBlock data-testid="citizen-information">
      <FormSection>
        <Columns>
          <Column>
            <h3 className="header">Omat tiedot</h3>
          </Column>
          <Column isNarrow toRight>
            {editMode ? null : (
              <EditLink action={() => setEditMode(true)}>Muokkaa</EditLink>
            )}
          </Column>
        </Columns>
        <Columns>
          <Column isOneQuarter>
            <TextField label="Etunimi" value={user.firstName} readonly={true} />
          </Column>
          <Column isOneQuarter>
            <TextField label="Sukunimi" value={user.lastName} readonly={true} />
          </Column>
          <Column isOneQuarter>
            <TextField
              label="Kotiosoite"
              value={user.streetAddress}
              readonly={true}
            />
          </Column>
          <Column isOneEight>
            <TextField
              label="Postinumero"
              value={user.postalCode}
              readonly={true}
            />
          </Column>
          <Column isOneEight>
            <TextField
              label="Postitoimipaikka"
              value={user.postOffice}
              readonly={true}
            />
          </Column>
        </Columns>
        <Columns>
          <Column isOneQuarter>
            <TextField
              label="Kotikunta"
              value={user.municipalityName}
              readonly={true}
            />
          </Column>
          <Column isOneQuarter>
            <TextField
              label="Puhelinnumero"
              bind={phone}
              readonly={!editMode}
            />
          </Column>
          <Column isOneQuarter>
            <TextField label="Sähköposti" bind={email} readonly={!editMode} />
          </Column>
          <Column isOneEight>
            <TextField
              label="Henkilötunnus"
              value="210281-9988"
              readonly={true}
            />
          </Column>
        </Columns>
        {editMode && (
          <Buttons>
            <Button action={cancel} loading={isPending}>
              Peruuta
            </Button>
            <Button action={onSubmit} type="primary" loading={isPending}>
              Tallenna muutokset
            </Button>
          </Buttons>
        )}
      </FormSection>
      {organizations.length > 0 && (
        <FormSection>
          <CitizenOrganizations organizations={organizations} />
        </FormSection>
      )}
    </Container>
  )
})
