CREATE TABLE eg_user(

id                  character varying(256) NOT NULL,
username            character varying(256)  NOT NULL,
name		        character varying(256),
emptype             character varying(128),
mobilenumber        character varying(256)  NOT NULL,
emailId             character varying(256),
uuid                character varying(256),
tenantid            character varying(256),
active              boolean,

CONSTRAINT uk_eg_incident_v2 UNIQUE (id),
CONSTRAINT pk_eg_usserReq_v2 PRIMARY KEY (id)
);

CREATE TABLE eg_role(

name                  character varying(256) NOT NULL,
code            character varying(256)  NOT NULL,
tenantid		        character varying(256),

CONSTRAINT uk_eg_role_v2 UNIQUE (code),
CONSTRAINT pk_eg_role_v2 PRIMARY KEY (code)
);



CREATE TABLE eg_userrole_v1(

role_code                  character varying(256) NOT NULL,
user_id            character varying(256)  NOT NULL,
CONSTRAINT uk_eg_userrole_v2 UNIQUE (role_code,user_id),
CONSTRAINT pk_eg_userrole_v2 PRIMARY KEY (role_code,user_id)
);
