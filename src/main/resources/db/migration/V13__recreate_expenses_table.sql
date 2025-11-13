drop table if exists expenses;

create table expenses (
    id bigint generated always as identity,

    title varchar(32) not null,
    amount decimal(10,2) not null,
    description text,
    expense_date date not null,

    category_id bigint not null,
    daily_control_id bigint not null,
    user_id bigint not null,

    primary key (id),
    constraint fk_expenses_user foreign key (user_id) references users (id),
    constraint fk_expenses_daily_control foreign key (daily_control_id) references daily_controls (id),
    constraint fk_expenses_category foreign key (category_id) references categories (id)
);