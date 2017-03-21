alter table kwery_user add column super_user boolean;

update kwery_user set super_user = true where id = (select id from kwery_user order by created asc fetch first row only);
