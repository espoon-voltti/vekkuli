import { Column, Columns, Label } from 'lib-components/dom'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { ContactDetails } from 'citizen-frontend/shared/types'

export default React.memo(function MemberList({
  members
}: {
  members: ContactDetails[]
}) {
  const i18n = useTranslation()

  return (
    <div data-testid="organization-contact-list">
      <h3 className="header">{i18n.organization.contactDetails.title}</h3>
      <Columns hideOnMobile>
        <Column isOneQuarter>
          <Label>{i18n.organization.contactDetails.fields.name}</Label>
        </Column>
        <Column isOneQuarter>
          <Label>{i18n.organization.contactDetails.fields.phone}</Label>
        </Column>
        <Column isOneQuarter>
          <Label>{i18n.organization.contactDetails.fields.email}</Label>
        </Column>
      </Columns>
      {members.map((member, index) => (
        <Columns
          key={index}
          bottomMarginOnMobile
          data-testid="organization-contact-list-item"
        >
          <Column isOneQuarter>{member.name}</Column>
          <Column isOneQuarter>{member.phone}</Column>
          <Column isOneQuarter>{member.email}</Column>
        </Columns>
      ))}
    </div>
  )
})
