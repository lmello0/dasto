create table categories(
    id bigint generated always as identity,

    name varchar(32) not null,

    user_id bigint not null,

    created_by varchar(255) not null,
    created_at timestamptz default now(),

    updated_by varchar(255),
    updated_at timestamptz,

    deleted_by varchar(255),
    deleted_at timestamptz,

    primary key (id),
    constraint fk_categories_user foreign key (user_id) references users (id)
);