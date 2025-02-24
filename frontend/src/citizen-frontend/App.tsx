import ScrollToTop from 'lib-components/ScrollToTop'
import { SkipToContentLink } from 'lib-components/links'
import React from 'react'
import { createBrowserRouter, Outlet } from 'react-router'

import RequireAuth from './auth/RequireAuth'
import { AuthContextProvider } from './auth/state'
import CitizenPage from './citizen/CitizenPage'
import ErrorPage from './errors/ErrorPage'
import HomePage from './home/HomePage'
import { Footer } from './layout/Footer'
import Navigation from './layout/Navigation'
import { Localization, useTranslation } from './localization'
import OrganizationPage from './organization/OrganizationPage'
import { queryClient, QueryClientProvider } from './query'
import ReservationStateRedirect from './reservation/ReservationStateRedirect'
import SearchPage from './reservation/pages/chooseBoatSpace/SearchPage'
import SwitchSearchPage from './reservation/pages/chooseBoatSpace/SwitchSearchPage'
import ConfirmationPage from './reservation/pages/confirmation/ConfirmationPage'
import ReservationErrorPage from './reservation/pages/error/ErrorPage'
import FormPage from './reservation/pages/fillInformation/FormPage'
import PaymentPage from './reservation/pages/payment/PaymentPage'
import { ReservationStateContextProvider } from './reservation/state'

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
  const i18n = useTranslation()
  return (
    <div>
      <SkipToContentLink target="main">
        {i18n.header.goToMainContent}
      </SkipToContentLink>
      <Navigation />
      <Outlet />
      <Footer />
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
            <Outlet />
          </ReservationStateContextProvider>
        ),
        children: [
          {
            index: true,
            element: (
              <ScrollToTop>
                <ReservationStateRedirect />
                <SearchPage />
              </ScrollToTop>
            )
          },
          {
            path: 'varaa',
            element: (
              <RequireAuth>
                <ScrollToTop>
                  <ReservationStateRedirect />
                  <FormPage />
                </ScrollToTop>
              </RequireAuth>
            )
          },
          {
            path: 'vaihda/:switchReservationId',
            element: (
              <RequireAuth>
                <ScrollToTop>
                  <ReservationStateRedirect />
                  <SwitchSearchPage />
                </ScrollToTop>
              </RequireAuth>
            )
          },
          {
            path: 'vaihda',
            element: (
              <RequireAuth>
                <ScrollToTop>
                  <ReservationStateRedirect />
                  <FormPage />
                </ScrollToTop>
              </RequireAuth>
            )
          },
          {
            path: 'jatka',
            element: (
              <RequireAuth>
                <ScrollToTop>
                  <ReservationStateRedirect />
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
                  <ReservationStateRedirect />
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
        path: 'kuntalainen/venepaikka/varausvirhe/:reservationId/:error',
        element: (
          <RequireAuth>
            <ScrollToTop>
              <ReservationErrorPage />
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
