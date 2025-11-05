alter table budgets
alter column effective_date type timestamptz;

alter table budgets
alter column termination_date type timestamptz;
