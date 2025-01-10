import ScrollToTop from 'lib-components/ScrollToTop'
import React from 'react'
import { createBrowserRouter, Outlet } from 'react-router'

import RequireAuth from './auth/RequireAuth'
import { AuthContextProvider } from './auth/state'
import CitizenPage from './citizen/CitizenPage'
import ErrorPage from './errors/ErrorPage'
import HomePage from './home/HomePage'
import Navigation from './layout/Navigation'
import { Localization } from './localization'
import { queryClient, QueryClientProvider } from './query'
import ReservationStateRedirect from './reservation/ReservationStateRedirect'
import SearchPage from './reservation/pages/chooseBoatSpace/SearchPage'
import ConfirmationPage from './reservation/pages/confirmation/ConfirmationPage'
import FormPage from './reservation/pages/fillInformation/FormPage'
import PaymentPage from './reservation/pages/payment/PaymentPage'
import { ReservationStateContextProvider } from './reservation/state'
import OrganizationPage from './organization/OrganizationPage'

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <Localization>
        <AuthContextProvider>
          <Content />
          <div id="modal-container" />
        </AuthContextProvider>
      </Localization>
    </QueryClientProvider>
  )
}

const Content = React.memo(function Content() {
  return (
    <div>
      <Navigation />
      <Outlet />
    </div>
  )
})

export const appRouter = createBrowserRouter([
  {
    path: '/',
    element: <App />,
    errorElement: <ErrorPage />,
    children: [
      {
        index: true,
        element: (
          <ScrollToTop>
            <HomePage />
          </ScrollToTop>
        )
      },
      {
        path: 'kuntalainen/venepaikka',
        element: (
          <ReservationStateContextProvider>
            <ReservationStateRedirect>
              <Outlet />
            </ReservationStateRedirect>
          </ReservationStateContextProvider>
        ),
        children: [
          {
            index: true,
            element: (
              <ScrollToTop>
                <SearchPage />
              </ScrollToTop>
            )
          },
          {
            path: 'varaa',
            element: (
              <RequireAuth>
                <ScrollToTop>
                  <FormPage />
                </ScrollToTop>
              </RequireAuth>
            )
          },
          {
            path: 'maksa',
            element: (
              <RequireAuth>
                <ScrollToTop>
                  <PaymentPage />
                </ScrollToTop>
              </RequireAuth>
            )
          }
        ]
      },
      {
        path: 'kuntalainen/venepaikka/vahvistus/:reservationId',
        element: (
          <RequireAuth>
            <ScrollToTop>
              <ConfirmationPage />
            </ScrollToTop>
          </RequireAuth>
        )
      },
      {
        path: 'kuntalainen/omat-tiedot',
        element: (
          <RequireAuth>
            <ScrollToTop>
              <CitizenPage />
            </ScrollToTop>
          </RequireAuth>
        )
      },
      {
        path: 'kuntalainen/yhteiso/:organizationId',
        element: (
          <RequireAuth>
            <ScrollToTop>
              <OrganizationPage />
            </ScrollToTop>
          </RequireAuth>
        )
      }
    ]
  }
])
