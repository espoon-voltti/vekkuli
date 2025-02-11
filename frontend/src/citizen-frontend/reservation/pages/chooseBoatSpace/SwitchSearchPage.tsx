import { Loader } from 'lib-components/Loader'
import React from 'react'

import { useQueryResult } from 'lib-common/query'
import useRouteParams from 'lib-common/useRouteParams'

import SearchPage from './SearchPage'
import { reservationBeingSwitchedQuery } from './queries'

export default React.memo(function SwitchSearchPage() {
  const { switchReservationId } = useRouteParams(['switchReservationId'])
  const switchInfoResult = useQueryResult(
    reservationBeingSwitchedQuery(parseInt(switchReservationId))
  )

  return (
    <Loader results={[switchInfoResult]}>
      {(switchInfo) => <SearchPage switchInfo={switchInfo} />}
    </Loader>
  )
})
