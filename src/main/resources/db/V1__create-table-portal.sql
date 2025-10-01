create table if not exists portal (
  id    bigserial primary key,
  type  varchar(40)  not null,
  name  varchar(255) not null,
  constraint ck_portal_type_enum
    check (type in ('QUICK_MAINTENANCE','SLOW_MAINTENANCE','POLICE_REPORT','RECOVERED_MOTORCYCLE'))
);
create index if not exists idx_portal_type on portal(type);
