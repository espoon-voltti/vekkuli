import React from 'react'

import { PaymentInformation } from '../../../api-types/reservation'

type PaymentButtonProps = {
  paymentInformation: PaymentInformation
}

export default React.memo(function PaymentButton({
  paymentInformation: { method, url, id, parameters, svg, name }
}: PaymentButtonProps) {
  return (
    <div className="column is-one-quarter">
      <form method={method} action={url} id={id} className="payment-form">
        {parameters.map(({ name, value }) => (
          <input key={name} type="hidden" name={name} value={value} />
        ))}
        <button className="button is-flex-direction-column">
          <img
            className="row"
            src={svg}
            alt={name}
            aria-labelledby={`${id}-label`}
          />
          <span id={`${id}-label`} className="row">
            {name}
          </span>
        </button>
      </form>
    </div>
  )
})
