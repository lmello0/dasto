alter table expenses
add column created_by varchar(255) not null,
add column created_at timestamptz default now(),
add column updated_by varchar(255),
add column updated_at timestamptz,
add column deleted_by varchar(255),
add column deleted_at timestamptz;