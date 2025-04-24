# Frontend for citizen boat reservations

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
* `main.scss` - Styles used by the frontend

Other folders:
* `src/lib-common` - Common types and functions that could be potentially shared between different frontends (eg. citizen and employee)
* `src/lib-components` - Common components that could be shared between different frontends (eg. citizen and employee)
* `src/lib-customization` - A layer of customization (such icons and translations). Based on evaka, but mostly overkill for this project
* `src/lib-icons` - Icons used by the frontend, shared between different potential frontends

### Good to know
* The pages rely heavily on the Form component from eVaka (`src/lib-common/form`) 
  * The field/input components used with the form can be found in `src/lib-components/form`
  * Logic for the forms used in pages can be found in `formDefinitions` file or folders next to the page or component
