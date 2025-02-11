import { Loader } from 'lib-components/Loader'
import React from 'react'

import { useQueryResult } from 'lib-common/query'

import { citizenOrganizationContactDetailsQuery } from '../queries'

import MemberList from './MemberList'

export default React.memo(function MemberListLoader({
  orgId
}: {
  orgId: string
}) {
  const membersList = useQueryResult(
    citizenOrganizationContactDetailsQuery(orgId)
  )

  return (
    <Loader results={[membersList]}>
      {(members) => <MemberList members={members} />}
    </Loader>
  )
})
