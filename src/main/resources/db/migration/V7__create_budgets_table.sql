create table budgets (
    id bigint generated always as identity,

    total_amount decimal(10,2) not null,
    investment_percentage int not null check(investment_percentage >= 0 and investment_percentage <= 100),

    effective_date date not null default now(),
    termination_date date,

    created_by varchar(255) not null,
    created_at timestamptz default now(),

    updated_by varchar(255),
    updated_at timestamptz,

    deleted_by varchar(255),
    deleted_at timestamptz,

    user_id bigint not null,

    primary key (id),
    constraint fk_budgets_user foreign key (user_id) references users (id)
);
