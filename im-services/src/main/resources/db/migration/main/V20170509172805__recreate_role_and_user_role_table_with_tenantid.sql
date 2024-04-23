ALTER TABLE eg_role ADD roleid bigint NOT NULL DEFAULT 0;
UPDATE eg_role SET roleid = id;
ALTER TABLE eg_role ALTER COLUMN roleid DROP DEFAULT;
ALTER TABLE eg_role DROP COLUMN id;
ALTER TABLE eg_role RENAME COLUMN roleid TO id;
ALTER TABLE eg_role ADD CONSTRAINT eg_role_pk PRIMARY KEY (id, tenantid);
