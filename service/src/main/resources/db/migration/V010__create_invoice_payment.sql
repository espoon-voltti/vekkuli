CREATE TABLE invoice_payment (
     id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
     transaction_number TEXT NOT NULL UNIQUE,
     amount_paid_cents INTEGER NOT NULL,
     payment_date DATE NOT NULL,
     invoice_number INTEGER NOT NULL,
     created TIMESTAMP DEFAULT NOW(),

     CONSTRAINT fk_invoice FOREIGN KEY (invoice_number)
         REFERENCES invoice (invoice_number)
         ON DELETE CASCADE
);
