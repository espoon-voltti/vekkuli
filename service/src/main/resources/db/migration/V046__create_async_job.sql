CREATE TABLE async_job (
   id uuid DEFAULT uuid_generate_v1mc() NOT NULL,
   type text NOT NULL,
   submitted_at timestamp with time zone DEFAULT now() NOT NULL,
   run_at timestamp with time zone DEFAULT now() NOT NULL,
   claimed_at timestamp with time zone,
   claimed_by bigint,
   retry_count integer NOT NULL,
   retry_interval interval NOT NULL,
   started_at timestamp with time zone,
   completed_at timestamp with time zone,
   payload jsonb NOT NULL
);

CREATE INDEX "idx$async_job_run_at" ON public.async_job USING btree (run_at) WHERE (completed_at IS NULL);

CREATE TABLE async_job_work_permit (
   pool_id text PRIMARY KEY,
   available_at timestamp with time zone NOT NULL
);
