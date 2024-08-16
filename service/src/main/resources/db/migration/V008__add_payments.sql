/*
 * SPDX-FileCopyrightText: 2023-2024 City of Espoo
 *
 * SPDX-License-Identifier: LGPL-2.1-or-later
 */

CREATE TYPE PaymentStatus AS ENUM ('Created', 'Success', 'Failed');

CREATE TABLE payment (
    -- internal id (used also for stamp parameter in PayTrail)
    id uuid PRIMARY KEY DEFAULT uuid_generate_v1mc(),

    -- The id of the citizen that made the payment
    citizen_id uuid NOT NULL,

    -- When was the payment initiated
    created TIMESTAMP NOT NULL DEFAULT now(),

    -- When was this row last updated (e.g. status changed)
    updated TIMESTAMP NOT NULL DEFAULT now(),

    -- The status of the payment (payment row starts with status 'Created')
    status PaymentStatus NOT NULL DEFAULT 'Created',

    -- The reference number of the payment (shown to the customer)
    reference TEXT NOT NULL,

    -- The total amount of the payment in cents (including VAT)
    total_cents INT NOT NULL,

    -- The VAT percentage of the payment
    vat_percentage NUMERIC(4, 1) NOT NULL,
    
    -- The product code that identifies what was purchased
    product_code TEXT NOT NULL,

    FOREIGN KEY (citizen_id) REFERENCES citizen(id)
);
CREATE INDEX idx_payment_citizen_id ON payment(citizen_id);
CREATE INDEX idx_payment_reference ON payment(reference);
CREATE INDEX idx_payment_created ON payment(created);


