import React, { createContext, useContext, useState } from 'react'

interface FormErrorContextType {
  showAllErrors: boolean | undefined
  setShowAllErrors: React.Dispatch<React.SetStateAction<boolean>>
}

const FormErrorContext = createContext<FormErrorContextType | undefined>(
  undefined
)

export const useFormErrorContext = () => {
  const defaultContext: FormErrorContextType = {
    showAllErrors: undefined,
    setShowAllErrors: () => null
  }

  return useContext(FormErrorContext) ?? defaultContext
}

export const FormErrorProvider: React.FC<{ children: React.ReactNode }> = ({
  children
}) => {
  const [showAllErrors, setShowAllErrors] = useState(false)

  return (
    <FormErrorContext.Provider value={{ showAllErrors, setShowAllErrors }}>
      {children}
    </FormErrorContext.Provider>
  )
}
