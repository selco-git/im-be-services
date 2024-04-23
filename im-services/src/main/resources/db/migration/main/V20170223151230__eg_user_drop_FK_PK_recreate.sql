

alter table eg_user add column  id_bak bigint;
update eg_user set id_bak=id;
alter table eg_user drop column  id;
alter table eg_user add column  id bigint;
update  eg_user set id=id_bak;
alter table eg_user drop column  id_bak;
alter table eg_user alter column id  set not null;
alter table eg_user add constraint  eg_user_pkey primary key (id,tenantid);
