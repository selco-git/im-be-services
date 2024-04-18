CREATE TABLE eg_incident_v2(

id                  character varying(256) NOT NULL,
incidentType        character varying(256)  NOT NULL,
incidentId		    character varying(256),
description         character varying(4000) NOT NULL,
additionalDetails   JSONB,
applicationStatus   character varying(128),
rating              smallint,
createdby           character varying(256)  NOT NULL,
createdtime         bigint                  NOT NULL,
lastmodifiedby      character varying(256),
lastmodifiedtime    bigint,
CONSTRAINT uk_eg_incident_v2 UNIQUE (id),
CONSTRAINT pk_eg_incidentReq_v2 PRIMARY KEY (incidentId)
);
