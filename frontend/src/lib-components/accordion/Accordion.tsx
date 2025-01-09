import classNames from 'classnames'
import React from 'react'

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
  return (
    <div className="accordion">
      <h4
        className="accordion-title is-flex is-align-items-center"
        onClick={() => setIsOpen(!isOpen)}
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
