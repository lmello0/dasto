create type expense_type as enum ('FIXED', 'VARIABLE', 'INVESTMENT', 'INSTALLMENT');

create table expenses (
    id bigint generated always as identity,

    expense_date date not null,
    title varchar(32) not null,
    amount decimal(10,2) not null,
    type expense_type not null,
    description text,
    installment_quantity integer,
    final_payment date,

    user_id bigint not null,
    category_id bigint not null,

    created_by varchar(255) not null,
    created_at timestamptz default now(),

    updated_by varchar(255),
    updated_at timestamptz,

    deleted_by varchar(255),
    deleted_at timestamptz,

    primary key (id),
    constraint fk_expenses_user foreign key (user_id) references users (id),
    constraint fk_expenses_category foreign key (category_id) references categories (id)
);