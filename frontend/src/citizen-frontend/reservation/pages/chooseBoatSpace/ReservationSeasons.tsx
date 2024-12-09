import React from 'react'

export default React.memo(function ReservationSeasons() {
  return (
    <div className="mb-xl">
      <div className="container is-highlight">
        <h2 className="has-text-weight-semibold">
          Venepaikkojen varaaminen 2025
        </h2>
        <h3 className="label">
          Uusien venepaikkojen varaaminen espoolaisille 3.3. alkaen ja muille
          1.4.–30.9.2025
        </h3>
        <h3 className="label">
          Suomenojan traileripaikkojen varaaminen espoolaisille 1.4. alkaen ja
          muille 1.5–31.12.2025
        </h3>
        <h3 className="label">
          Uusien talvipaikkojen varaaminen espoolaisille 1.9.–31.12.2025
        </h3>
        <h3 className="label">
          Ämmäsmäen säilytyspaikkojen varaaminen kaikille 1.9.–.31.7.2026{' '}
        </h3>
      </div>
    </div>
  )
})
