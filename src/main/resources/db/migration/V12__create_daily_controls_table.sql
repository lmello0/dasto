create table daily_controls (
    id bigint generated always as identity,

    "date" date not null,
    daily_limit decimal(10,2) not null,
    carried_over decimal(10,2) not null,
    adjusted_limit decimal(10,2) not null,
    total_spent decimal(10,2) not null default 0,
    remaining decimal(10,2) not null default 0,
    carryover_next decimal(10,2) not null,

    user_id bigint not null,
    budget_id bigint not null,

    primary key (id),
    constraint daily_controls_user foreign key (user_id) references users (id),
    constraint daily_controls_budget foreign key (budget_id) references budgets (id)
);