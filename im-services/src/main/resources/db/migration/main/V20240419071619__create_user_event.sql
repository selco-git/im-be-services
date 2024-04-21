create table eg_usrevents_events(
id character varying(64),
tenantid character varying(128),
source character varying(128),
eventtype character varying(128), 
category character varying(128), 
name character varying(128), 
postedby character varying(128), 
referenceid character varying(128), 
description character varying(512), 
status character varying(128), 
recepient character varying(512),    
createdBy character varying(64),
lastModifiedBy character varying(64),
createdTime bigint,
lastModifiedTime bigint,CONSTRAINT uk_eg_wf_userevent UNIQUE (id)
);