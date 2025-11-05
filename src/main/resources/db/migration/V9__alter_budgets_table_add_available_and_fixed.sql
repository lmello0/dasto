alter table budgets
add available_amount decimal(10,2) not null default 0 check(available_amount >= 0),
add fixed_expenses decimal(10, 2) not null default 0 check(fixed_expenses >= 0);