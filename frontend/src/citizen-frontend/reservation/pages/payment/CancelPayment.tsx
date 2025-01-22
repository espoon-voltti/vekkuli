import { GoBackLink } from 'lib-components/links'
import React from 'react'

import { useTranslation } from 'citizen-frontend/localization'
import { useMutation } from 'lib-common/query'

import ErrorModal from './CancelPaymentErrorModal'
import { cancelPaymentMutation } from './queries'

export default React.memo(function CancelPayment({
  reservationId
}: {
  reservationId: number
}) {
  const [error, setError] = React.useState(false)
  const i18n = useTranslation()
  const ariaLabel = i18n.reservation.goBack
  //const navigate = useNavigate()
  const { mutateAsync: cancelPayment, isPending } = useMutation(
    cancelPaymentMutation
  )

  const onCancelPayment = () => {
    cancelPayment(reservationId)
      .then(() => {
        //return navigate('/kuntalainen/venepaikka/varaa')
      })
      .catch((error) => {
        setError(true)
        console.error('Error cancelling payment', error)
      })
  }

  return (
    <>
      {!isPending ? (
        <GoBackLink action={onCancelPayment} ariaLabel={ariaLabel}>
          {i18n.components.links.goBack}
        </GoBackLink>
      ) : (
        <div className="is-primary-color button is-loading is-transparent" />
      )}
      {error && <ErrorModal close={() => setError(false)} />}
    </>
  )
})
