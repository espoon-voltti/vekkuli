import React, { useLayoutEffect } from 'react'
import { useLocation } from 'react-router'

import { scrollToPos } from 'lib-common/utils/scrolling'

export default React.memo(function ScrollToTop({
  children
}: {
  children?: React.ReactNode
}) {
  const location = useLocation()

  useLayoutEffect(() => {
    scrollToPos({ top: 0, left: 0, behavior: 'auto' })
  }, [location.pathname])

  return <>{children}</>
})
