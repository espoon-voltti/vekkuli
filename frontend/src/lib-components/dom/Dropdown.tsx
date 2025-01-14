import classNames from 'classnames'
import React, { useCallback, useEffect, useRef, useState } from 'react'

interface DropdownProps {
  id: string
  ariaLabel?: string
  isHoverable?: boolean
  children: {
    label: React.ReactNode
    menuItems: React.ReactNode
  }
}

export default React.memo(function Dropdown({
  id,
  ariaLabel,
  isHoverable,
  children
}: DropdownProps) {
  const [isActive, setIsActive] = useState(false)
  const dropdownRef = useRef<HTMLDivElement>(null)

  const setDropdownOpen = useCallback((action: 'open' | 'close' | 'toggle') => {
    setIsActive((prev) => {
      switch (action) {
        case 'open':
          return true
        case 'close':
          return false
        case 'toggle':
          return !prev
        default:
          return prev
      }
    })
  }, [])

  const { label, menuItems } = children

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(event.target as Node)
      ) {
        setDropdownOpen('close')
      }
    }

    // Close dropdown when tabbing outside of it
    const handleKeyNavigation = (event: KeyboardEvent) => {
      if (event.key === 'Tab') {
        setTimeout(() => {
          const activeElement = document.activeElement
          if (
            dropdownRef.current &&
            !dropdownRef.current.contains(activeElement)
          ) {
            setDropdownOpen('close')
          }
        }, 0)
      }
    }

    document.addEventListener('click', handleClickOutside)
    document.addEventListener('keydown', handleKeyNavigation)
    return () => {
      document.removeEventListener('click', handleClickOutside)
      document.removeEventListener('keydown', handleKeyNavigation)
    }
  }, [setDropdownOpen])

  const handleKeyDown = (event: React.KeyboardEvent<HTMLDivElement>) => {
    const items =
      dropdownRef.current?.querySelectorAll<HTMLAnchorElement>('.dropdown-item')
    if (!items || items.length === 0) return

    if (!isActive && event.key === 'ArrowDown') {
      setDropdownOpen('open')
      return
    }

    const focusIndex = Array.from(items).findIndex(
      (item) => item === document.activeElement
    )

    switch (event.key) {
      case 'ArrowDown':
        event.preventDefault()
        items[(focusIndex + 1) % items.length]?.focus()
        break
      case 'ArrowUp':
        event.preventDefault()
        items[(focusIndex - 1 + items.length) % items.length]?.focus()
        break
      case 'Escape':
        setDropdownOpen('close')
        break
      case 'Enter':
        if (focusIndex >= 0) {
          setDropdownOpen('close')
        }
        break
      default:
        break
    }
  }

  return (
    <div
      id={id}
      className={classNames('dropdown', {
        'is-active': isActive,
        'is-hoverable': isHoverable
      })}
      ref={dropdownRef}
      onKeyDown={handleKeyDown}
    >
      <div className="dropdown-trigger">
        <button
          className="dropdown-title"
          aria-haspopup="true"
          aria-controls={`menu-${id}`}
          aria-expanded={isActive}
          aria-label={ariaLabel}
          onClick={() => setDropdownOpen('toggle')}
        >
          {label}
        </button>
      </div>
      <div className="dropdown-menu" id={`menu-${id}`}>
        <div className="dropdown-content">{menuItems}</div>
      </div>
    </div>
  )
})
