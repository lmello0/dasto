create extension if not exists "uuid-ossp";

create table users(
	id bigint generated always as identity,
	public_id uuid not null,

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