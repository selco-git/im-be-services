CREATE TABLE eg_userrole_v1 (

role_code character varying(18),
role_tenantid character varying(128),
user_tenantid character varying(128),
user_id bigint,
lastmodifieddate   timestamp  
);



ALTER TABLE eg_userrole_v1 ADD CONSTRAINT fk_role_v1 FOREIGN KEY (role_code, role_tenantid) REFERENCES eg_role(code, tenantid);
ALTER TABLE eg_userrole_v1 ADD CONSTRAINT fk_user_role_v1 FOREIGN KEY (user_id, user_tenantid) REFERENCES eg_user(id, tenantid);