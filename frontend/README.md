# Frontend for citizen boat reservations

## Features missing which are implemented in HTMX version
* General
  * Most of the translations are missing
  * Type definitions are hastily hacked together -> need a redo
  * E2E tests will break as the id's unrelated to functionality are mostly removed
* Home page
  * All texts are static
* Boat search page
  * search result improvements
    * Notices
    * proper pagination
  * User not logged in
    * Login form redirects back to search page and doesn't remember search parameters
* Reservation form
  * Boat size doesn't transfer currently from the search form
  * Renewal
    * Not yet tested
  * Boat dimension and weight checks from backend
    * including warnings from those
* Payment page
  * Back button
* Confirmation page
  * Page included in history, so eg. after going to citizen details page the back button leads back to the confirmation page
* Citizen details page
  * Renew button

## Project structure & Info

### App, pages, api clients, etc
The React app lives in `src/citizen-frontend` folder. In there the structure is as follows:
* `api-clients` - API clients, eg. all the requests
* `api-types` - Types for the API clients
* `auth` - Context & queries for authentication
* `citizen` - Citizen details page
* `errors` - Contains generic error page used by Router
* `home` - Static home page
* `layout` - Layout components, eg. header, menu, etc.
* `localization` - Localization context and state management
* `reservation` - The whole reservation flow including "search", "form", "payment", "confirmation" pages
* `shared` - Shared types, formatters and queries
* `App.tsx` - Main app component, routes are defined here
* `config.ts` - Configuration for the app. Not really used and could be deprecated/moved
* `query.ts` - Main query client for the app
* `api-client.ts` - Axios api client used by different clients in `api-clients` folder
* `main.scss` - Styles used by the frontend - copy from htmx version

Other folders:
* `src/lib-common` - Common types and functions that could be potentially shared between different frontends (eg. citizen and employee)
* `src/lib-components` - Common components that could be shared between different frontends (eg. citizen and employee)
* `src/lib-customization` - A layer of customization (such icons and translations). Based on evaka, but mostly overkill for this project
* `src/lib-icons` - Icons used by the frontend, shared between different potential frontends

### Good to know
* The pages rely heavily on the Form component from eVaka (`src/lib-common/form`) 
  * The field/input components used with the form can be found in `src/lib-components/form`
  * Logic for the forms used in pages can be found in `formDefinitions` file or folders next to the page or component

### Known issues
The frontend was set up in a rapid burst from a combination of "eVaka" and "Oppivelvollisuus" projects so there are some known issues.
* The build process is not optimized or checked through. It's made in way that it seems to work. That would require a gothrough
* Types are probably not logically in the right place
    * Eg. some types live in api-types, others in shared, etc. and probably that logic would need a glance
* Typedefinitions might be missing or be broken
* A lot of functionality was brought from eVaka to lib-common and lib-customization. They might contain a lot of redundant code which is errorprone as the Form that is used heavily on this project was dependant of many libraries in lib-common
* The frontend is not fully translated
* The frontend is not fully tested - E2E tests *will* break
* The frontend is not fully accessible - Work is been done to improve this
* The frontend is not fully optimized

