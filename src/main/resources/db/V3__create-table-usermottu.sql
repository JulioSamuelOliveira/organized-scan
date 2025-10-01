-- Evita a palavra reservada "user" do Postgres
create table if not exists usermottu (
  id    bigserial primary key,
  name  varchar(255),
  email varchar(320) unique
);
create index if not exists idx_usermottu_email on usermottu(email);
