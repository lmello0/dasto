alter table categories
add constraint unique_name_per_user
unique (name, user_id);

alter table categories
add constraint unique_id_per_user
unique (id, user_id);