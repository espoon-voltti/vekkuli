CREATE EXTENSION IF NOT EXISTS "uuid-ossp" WITH SCHEMA public;

CREATE COLLATION fi_fi_icu (provider = icu, locale = 'fi-FI', deterministic = true);

CREATE DOMAIN text_fi AS TEXT COLLATE fi_fi_icu;

create sequence invoice_number_seq
    start with 100000;

create type boatspacetype as enum ('Storage', 'Slip', 'Trailer', 'Winter');

create type boatamenity as enum ('None', 'Buoy', 'RearBuoy', 'Beam', 'WalkBeam', 'Trailer', 'Buck');

create type boattype as enum ('Rowboat', 'OutboardMotor', 'InboardMotor', 'Sailboat', 'JetSki', 'Other');

create type paymentstatus as enum ('Created', 'Success', 'Failed', 'Refunded');

create type messagetype as enum ('Email', 'SMS');

create type messagestatus as enum ('Queued', 'Sent', 'Failed', 'Processing');

create type reserver_type as enum ('Citizen', 'Organization');

create type reservationoperation as enum ('New', 'Renew', 'Change', 'SecondNew');

create type validity as enum ('FixedTerm', 'Indefinite');

create type reservationtype as enum ('Marine', 'Spaces', 'GroupExercise');

create type reservationterminationreason as enum ('UserRequest', 'InvalidOwner', 'RuleViolation', 'PaymentViolation', 'Other');

create type storagetype as enum ('None', 'Trailer', 'Buck', 'BuckWithTent');

create type ownershipstatus as enum ('Owner', 'User', 'CoOwner', 'FutureOwner');

create type reservation_creation_type as enum ('Switch', 'Renewal', 'New');

create type reservationstatus as enum ('Info', 'Payment', 'Confirmed', 'Cancelled', 'Invoiced');

create type payment_type as enum ('OnlinePayment', 'Invoice', 'Other');

create table location
(
    id      serial
        primary key,
    name    text_fi not null,
    address text_fi not null
);

create table price
(
    id              serial
        primary key,
    name            text              not null,
    price_cents     integer           not null,
    vat_cents       integer default 0 not null,
    net_price_cents integer default 0 not null
);

create table app_user
(
    id          uuid                     default uuid_generate_v1mc() not null
        primary key,
    created     timestamp with time zone default now()                not null,
    updated     timestamp with time zone,
    external_id text                                                  not null,
    first_name  text_fi                                                  not null,
    last_name   text_fi                                                  not null,
    email       text,
    is_system_user boolean                  default false
);

create unique index uniq$users$external_id
    on app_user (external_id);

create table boat_space
(
    id           serial
        primary key,
    type         boatspacetype not null,
    location_id  integer       not null
        constraint fk_location_id
            references location,
    price_id     integer       not null
        constraint fk_price_id
            references price,
    section      text_fi          not null,
    place_number integer       not null,
    amenity      boatamenity   not null,
    width_cm     integer       not null,
    length_cm    integer       not null,
    description  text_fi          not null
);

create table harbor_restriction
(
    location_id        integer  not null
        constraint fk_location_id
            references location,
    excluded_boat_type boattype not null
);

create table email_template
(
    id      varchar(128) not null
        primary key,
    subject text_fi         not null,
    body    text_fi         not null
);

create table municipality
(
    code integer not null
        primary key,
    name text_fi    not null
);

create table reserver
(
    id                  uuid                       not null
        primary key,
    type                reserver_type              not null,
    created_at          timestamp default now()    not null,
    updated_at          timestamp,
    name                text_fi      default ''::text not null,
    email               text      default ''::text not null,
    phone               text      default ''::text not null,
    municipality_code   integer   default 1        not null,
    street_address      text_fi      default ''::text not null,
    street_address_sv   text_fi      default ''::text not null,
    postal_code         text      default ''::text not null,
    post_office         text_fi      default ''::text not null,
    post_office_sv      text_fi      default ''::text not null,
    espoo_rules_applied boolean   default false    not null,
    discount_percentage integer   default 0        not null
);

create table citizen
(
    id                 uuid    default uuid_generate_v1mc() not null
        primary key
        constraint fk_citizen_reserver_id
            references reserver,
    national_id        text    default ''::text             not null,
    first_name         text_fi    default ''::text             not null,
    last_name          text_fi    default ''::text             not null,
    full_name_tsvector tsvector,
    data_protection    boolean default false                not null
);

create unique index uniq$citizen$national_id
    on citizen (national_id);

create index idx_citizen_full_name_tsvector
    on citizen using gin (full_name_tsvector);

create table boat
(
    id                   serial
        primary key,
    registration_code    text_fi,
    reserver_id          uuid            not null
        references reserver,
    name                 text_fi,
    width_cm             integer         not null,
    length_cm            integer         not null,
    depth_cm             integer         not null,
    weight_kg            integer         not null,
    type                 boattype        not null,
    other_identification text_fi,
    extra_information    text_fi,
    ownership            ownershipstatus not null,
    deleted_at           timestamp
);

create table sent_message
(
    id                uuid          default uuid_generate_v1mc()    not null
        primary key,
    provider_id       text,
    created           timestamp     default now()                   not null,
    sent_at           timestamp,
    type              messagetype                                   not null,
    status            messagestatus default 'Queued'::messagestatus not null,
    sender_id         uuid
        references app_user,
    recipient_id      uuid
        references reserver,
    recipient_address text                                          not null,
    subject           text_fi                                          not null,
    body              text_fi                                          not null,
    sender_address    text,
    retry_count       integer       default 0                       not null
);

create table citizen_memo
(
    id          serial
        primary key,
    created_at  timestamp       default now()                     not null,
    created_by  uuid
        references app_user,
    updated_at  timestamp,
    updated_by  uuid
        references app_user,
    reserver_id uuid                                              not null
        references reserver,
    content     text_fi                                              not null,
    category    reservationtype default 'Marine'::reservationtype not null
);

create table organization
(
    id                     uuid                  not null
        primary key
        references reserver
        constraint fk_organization_reserver_id
            references reserver,
    business_id            text default ''::text not null,
    billing_name           text_fi default ''::text not null,
    billing_street_address text_fi default ''::text not null,
    billing_postal_code    text default ''::text not null,
    billing_post_office    text_fi default ''::text not null
);

create table organization_member
(
    organization_id uuid not null
        references reserver
        constraint fk_organization_member_organization_id
            references reserver,
    member_id       uuid not null
        references reserver
        constraint fk_organization_member_member_id
            references reserver,
    primary key (organization_id, member_id)
);

create table reservation_period
(
    start_month      smallint             default 1                           not null,
    start_day        smallint             default 1                           not null,
    end_month        smallint             default 12                          not null,
    end_day          smallint             default 31                          not null,
    is_espoo_citizen boolean              default false                       not null,
    operation        reservationoperation default 'New'::reservationoperation not null,
    boat_space_type  boatspacetype        default 'Slip'::boatspacetype       not null
);

create table variable
(
    id    varchar(128) not null
        primary key,
    value text         not null
);

create table processed_message
(
    reservation_type reservationtype not null,
    reservation_id   integer         not null,
    message_type     varchar(255)    not null,
    recipient_email  varchar(255)    not null,
    primary key (reservation_type, reservation_id, message_type, recipient_email)
);

create table async_job
(
    id             uuid                     default uuid_generate_v1mc() not null,
    type           text                                                  not null,
    submitted_at   timestamp with time zone default now()                not null,
    run_at         timestamp with time zone default now()                not null,
    claimed_at     timestamp with time zone,
    claimed_by     bigint,
    retry_count    integer                                               not null,
    retry_interval interval                                              not null,
    started_at     timestamp with time zone,
    completed_at   timestamp with time zone,
    payload        jsonb                                                 not null
);

create index idx$async_job_run_at
    on async_job (run_at)
    where (completed_at IS NULL);

create table async_job_work_permit
(
    pool_id      text                     not null
        primary key,
    available_at timestamp with time zone not null
);

create table trailer
(
    id                serial
        primary key,
    reserver_id       uuid    not null
        references reserver,
    registration_code text_fi,
    width_cm          integer not null,
    length_cm         integer not null
);

create table boat_space_reservation
(
    id                      serial
        primary key,
    reserver_id             uuid
        references reserver,
    boat_space_id           serial
        references boat_space,
    start_date              date                                                        not null,
    end_date                date                                                        not null,
    created                 timestamp                 default now()                     not null,
    updated                 timestamp                 default now()                     not null,
    status                  reservationstatus         default 'Info'::reservationstatus not null,
    boat_id                 integer
        constraint fk_boat_id
            references boat,
    employee_id             uuid,
    acting_citizen_id       uuid
        references citizen,
    validity                validity                  default 'FixedTerm'::validity     not null,
    original_reservation_id integer
        constraint boat_space_reservation_renewed_from_id_fkey
            references boat_space_reservation,
    termination_reason      reservationterminationreason,
    termination_comment     text_fi,
    termination_timestamp   timestamp,
    trailer_id              integer
        constraint fk_trailer_id
            references trailer,
    storage_type            storagetype               default 'None'::storagetype,
    creation_type           reservation_creation_type default 'New'::reservation_creation_type
);

create index idx_boat_space_reservation_citizen_id
    on boat_space_reservation (reserver_id);

create index idx_boat_space_reservation_boat_space_id
    on boat_space_reservation (boat_space_id);

create index idx_boat_space_reservation_start_date
    on boat_space_reservation (start_date);

create index idx_boat_space_reservation_end_date
    on boat_space_reservation (end_date);

create index idx_boat_space_reservation_status
    on boat_space_reservation (status);

create table payment
(
    id             uuid          default uuid_generate_v1mc()     not null
        primary key,
    created        timestamp     default now()                    not null,
    updated        timestamp     default now()                    not null,
    status         paymentstatus default 'Created'::paymentstatus not null,
    reference      text                                           not null,
    total_cents    integer                                        not null,
    vat_percentage numeric(4, 1)                                  not null,
    product_code   text                                           not null,
    reservation_id integer
        references boat_space_reservation,
    paid           timestamp,
    reserver_id    uuid                                           not null
        references reserver,
    payment_type   payment_type                                   not null,
    price_info     text
);

create index idx_payment_reference
    on payment (reference);

create index idx_payment_created
    on payment (created);

create index idx_payment_reserver_id
    on payment (reserver_id);

create table reservation_warning
(
    reservation_id integer                                not null
        references boat_space_reservation
            on delete cascade,
    key            text                                   not null,
    created        timestamp with time zone default now() not null,
    boat_id        integer
        constraint fk_boat_id
            references boat,
    trailer_id     integer
        references trailer
            on delete cascade
);

create table invoice
(
    id             uuid    default uuid_generate_v1mc()                    not null
        primary key,
    due_date       date                                                    not null,
    reference      text                                                    not null,
    reservation_id integer                                                 not null
        references boat_space_reservation,
    payment_id     uuid
        references payment,
    invoice_number integer default nextval('invoice_number_seq'::regclass) not null
        unique,
    reserver_id    uuid                                                    not null
        references reserver
);

create index idx_invoice_reserver_id
    on invoice (reserver_id);


create function update_full_name_tsvector() returns trigger
    language plpgsql
as
$$
BEGIN
    NEW.full_name_tsvector := to_tsvector('simple', CONCAT_WS(' ', NEW.first_name, NEW.last_name));
    RETURN NEW;
END;
$$;

create trigger tsvectorupdate
    before insert or update
    on citizen
    for each row
execute procedure update_full_name_tsvector();

create function check_reserver_type() returns trigger
    language plpgsql
as
$$
DECLARE
    reserver_id_value UUID;
BEGIN
    EXECUTE 'SELECT ($1).' || TG_ARGV[1] INTO reserver_id_value USING NEW;
    IF NOT EXISTS (
        SELECT 1 FROM reserver
        WHERE id = reserver_id_value
          AND type = CAST(TG_ARGV[0] AS reserver_type)
    ) THEN
        RAISE EXCEPTION '% must refer to a reserver with type = %', TG_ARGV[1], TG_ARGV[0];
    END IF;

    RETURN NEW;
END;
$$;

create trigger trigger_check_citizen_type
    before insert or update
    on citizen
    for each row
execute procedure check_reserver_type('Citizen', 'id');

create trigger trigger_check_organization_type
    before insert or update
    on organization
    for each row
execute procedure check_reserver_type('Organization', 'id');

create trigger trigger_check_organization_member_citizen_type
    before insert or update
    on organization_member
    for each row
execute procedure check_reserver_type('Citizen', 'member_id');

create trigger trigger_check_organization_member_organization_type
    before insert or update
    on organization_member
    for each row
execute procedure check_reserver_type('Organization', 'organization_id');

create function update_reserver_name() returns trigger
    language plpgsql
as
$$
BEGIN
    UPDATE reserver
    SET name = NEW.last_name || ' ' || NEW.first_name
    WHERE id = NEW.id;
    RETURN NEW;
END;
$$;

create trigger update_reserver_from_citizen
    after insert or update
        of first_name, last_name
    on citizen
    for each row
execute procedure update_reserver_name();


