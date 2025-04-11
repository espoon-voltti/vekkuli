CREATE TABLE paytrail_payment_cache (
    transaction_id TEXT PRIMARY KEY,
    response JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    expires_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_paytrail_payment_cache_expires_at ON paytrail_payment_cache (expires_at);
