import React from 'react'

export const PriceFormat = React.memo(function PriceFormat({
  price
}: {
  price: number
}) {
  return (
    <>
      {new Intl.NumberFormat('fi-FI', {
        style: 'currency',
        currency: 'EUR'
      }).format(price / 100)}
    </>
  )
})

interface SpaceSizeProps {
  size: {
    width: number
    length: number
  }
}

export const SpaceSize = React.memo(function SpaceSize({
  size: { width, length }
}: SpaceSizeProps) {
  const f = (num: number) => {
    return new Intl.NumberFormat('fi-FI', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    }).format(num / 100)
  }
  return (
    <>
      {f(width)} x {f(length)} m
    </>
  )
})
