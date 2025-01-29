import classNames from 'classnames'
import React from 'react'

import { Translations, useTranslation } from '../localization'

type Step = 'chooseBoatSpace' | 'fillInformation' | 'payment' | 'confirmation'

type StepIndicatorProps = {
  step: Step
}

export default React.memo(function StepIndicator({ step }: StepIndicatorProps) {
  const i18n = useTranslation()
  const steps: Step[] = [
    'chooseBoatSpace',
    'fillInformation',
    'payment',
    'confirmation'
  ]
  const currentStepSequence = steps.indexOf(step)
  return (
    <div className="container" id="step-indicator">
      <div
        className="columns is-mobile"
        id="step-indicator-step"
        role="list"
        aria-label={i18n.components.stepIndicator.title}
      >
        {steps.map((s, index) => (
          <Step
            key={s}
            step={s}
            visited={index <= currentStepSequence}
            i18n={i18n}
            currentStep={index === currentStepSequence}
          />
        ))}
      </div>
    </div>
  )
})

type StepProps = {
  step: Step
  visited: boolean
  i18n: Translations
  currentStep: boolean
}

const Step = React.memo(function Step({
  step,
  visited,
  i18n,
  currentStep
}: StepProps) {
  return (
    <div
      className="column"
      aria-current={currentStep ? 'page' : undefined}
      role="listitem"
    >
      <div
        className={classNames('step mb-m', { visited: visited })}
        aria-hidden={true}
      />
      <p className="is-uppercase has-text-centered title is-7">
        {i18n.reservation.steps[step]}
      </p>
    </div>
  )
})
