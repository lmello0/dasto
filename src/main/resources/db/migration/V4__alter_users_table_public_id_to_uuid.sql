create extension if not exists "uuid-ossp";

alter table users
alter column public_id type UUID
using public_id::uuid;