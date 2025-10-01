-- =========================
-- Tabela: portal
-- Tipos: QUICK_MAINTENANCE, SLOW_MAINTENANCE, POLICE_REPORT, RECOVERED_MOTORCYCLE
-- =========================
create table if not exists portal (
  id    bigserial primary key,
  type  varchar(40)  not null,
  name  varchar(255) not null,

  constraint ck_portal_type_enum
    check (type in ('QUICK_MAINTENANCE','SLOW_MAINTENANCE','POLICE_REPORT','RECOVERED_MOTORCYCLE'))
);

create index if not exists idx_portal_type on portal(type);


-- =========================
-- Tabela: motorcycle
-- Tipos: MOTTU_SPORT_110I, MOTTU_E, MOTTU_POP
-- =========================
create table if not exists motorcycle (
  id                     bigserial primary key,

  type                   varchar(40)  not null,
  constraint ck_motorcycle_type_enum
    check (type in ('MOTTU_SPORT_110I','MOTTU_E','MOTTU_POP')),

  license_plate          varchar(255) not null,
  constraint ck_motorcycle_license_plate_chars
    check (license_plate ~ '^[A-Za-z0-9 ]*$'),

  chassi                 varchar(255) not null,
  rfid                   varchar(255) not null,

  portal                 bigint       not null,
  constraint fk_motorcycle_portal
    foreign key (portal) references portal(id),

  problem_description    varchar(500),
  constraint ck_motorcycle_problem_desc_len
    check (problem_description is null or length(problem_description) between 10 and 500),

  entry_date             date         not null,
  constraint ck_motorcycle_entry_date_past_or_present
    check (entry_date <= current_date),

  availability_forecast  date         not null,
  constraint ck_motorcycle_availability_forecast_future_or_present
    check (availability_forecast >= current_date)
);

create index if not exists idx_motorcycle_portal on motorcycle(portal);
create index if not exists idx_motorcycle_type   on motorcycle(type);
create index if not exists idx_motorcycle_plate  on motorcycle(license_plate);

-- =========================
-- Tabela: usermottu (evita a palavra reservada "user" do Postgres)
-- =========================
create table if not exists usermottu (
  id    bigserial primary key,
  name  varchar(255),
  email varchar(320) unique
);

create index if not exists idx_usermottu_email on usermottu(email);
