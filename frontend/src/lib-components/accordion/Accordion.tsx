import classNames from 'classnames'
import React, { useCallback } from 'react'

import { OutlinedChevronDown } from 'lib-icons'

type AccordionProperties = {
  children: React.ReactNode
  title: string
}

export default React.memo(function Accordion({
  children,
  title
}: AccordionProperties) {
  const [isOpen, setIsOpen] = React.useState(false)
  const handleKeyDown = useCallback(
    (event: React.KeyboardEvent<HTMLDivElement>) => {
      if (event.key === 'Enter' || event.key === ' ') {
        event.preventDefault()
        setIsOpen((prev) => !prev)
      }
    },
    []
  )
  return (
    <div className="accordion">
      <h4
        role="button"
        tabIndex={0}
        className="accordion-title is-flex is-align-items-center"
        onClick={() => setIsOpen(!isOpen)}
        onKeyDown={handleKeyDown}
      >
        {title}
        <span
          className={classNames('ml-xl icon icon-transform', {
            'icon-rotate-90': !isOpen
          })}
        >
          <OutlinedChevronDown />
        </span>
      </h4>
      {isOpen && <div className="accordion-content">{children}</div>}
    </div>
  )
})
