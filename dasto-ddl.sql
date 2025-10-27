create extension if not exists "uuid-ossp";

create table users(
	id bigint generated always as identity,
	public_id varchar(36) not null,
	
	first_name varchar(255) not null,
	last_name varchar(255),
	email varchar(255) not null unique,
	password_hash varchar(255) not null,
	
	created_by varchar(255) not null,
	created_at timestamptz default now(),
	
	updated_by varchar(255),
	updated_at timestamptz,
	
	deleted_by varchar(255),
	deleted_at timestamptz,
	
	primary key (id)
);

create table budget_configs (
	id bigint generated always as identity,
	
	total_money decimal(10,2) not null check(total_money > 0),
	fixed_expenses decimal(10,2) not null check(fixed_expenses >= 0),
	investment_amount decimal(10,2) check(investment_amount >= 0),
	investment_percentage decimal(5,2) check(investment_percentage >= 0 and investment_percentage <= 100),
	effective_date date,
	termination_date date,
	
	created_by varchar(255) not null,
	created_at timestamptz default now(),
	
	updated_by varchar(255),
	updated_at timestamptz,
	
	deleted_by varchar(255),
	deleted_at timestamptz,
	
	user_id bigint,
	
	primary key (id),
	constraint fk_budget_configs_users foreign key (user_id) references users (id)
);

create table monthly_budgets (
	id bigint generated always as identity,
	
	month date,
	daily_allowance decimal(10,2),
	total_available decimal(10,2),
	
	user_id bigint,
	budget_config_id bigint,
	
	created_by varchar(255) not null,
	created_at timestamptz default now(),
	
	updated_by varchar(255),
	updated_at timestamptz,
	
	deleted_by varchar(255),
	deleted_at timestamptz,
	
	primary key (id),
	constraint fk_monthly_budgets_users foreign key (user_id) references users (id),
	constraint fk_monthly_budgets_budget_configs foreign key (budget_config_id) references budget_configs (id)
);

create table daily_tracking (
	id bigint generated always as identity,
	
	when_spent date,
	daily_allowance decimal(10,2),
	spent_today decimal(10,2) default 0 check(spent_today >= 0),
	carried_over decimal(10,2) default 0,
	balance_end_of_day decimal(10,2),
	
	budget_id bigint,
	
	created_by varchar(255) not null,
	created_at timestamptz default now(),
	
	updated_by varchar(255),
	updated_at timestamptz,
	
	deleted_by varchar(255),
	deleted_at timestamptz,
	
	primary key (id),
	constraint fk_daily_tracking_monthly_budgets foreign key (budget_id) references monthly_budgets (id)
);

create table expenses (
	id bigint generated always as identity,
	
	amount decimal(10,2),
	category varchar(25),
	description text,
	expense_date date,
	
	budget_id bigint,
	daily_tracking_id bigint,
	
	created_by varchar(255) not null,
	created_at timestamptz default now(),
	
	updated_by varchar(255),
	updated_at timestamptz,
	
	deleted_by varchar(255),
	deleted_at timestamptz,
	
	primary key (id),
	
	constraint fk_expenses_monthly_budgets foreign key (budget_id) references monthly_budgets (id),
	constraint fk_expenses_daily_tracking foreign key (daily_tracking_id) references daily_tracking (id)
);
