package org.egov.im.repository.rowmapper;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.im.entity.Address;
import org.egov.im.entity.Incident;
import org.egov.im.web.models.AuditDetails;
import org.egov.im.web.models.Boundary;
import org.egov.im.web.models.GeoLocation;
import org.egov.im.web.models.Priority;
import org.egov.tracer.model.CustomException;
import org.postgresql.util.PGobject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Repository
public class IMRowMapper implements ResultSetExtractor<List<Incident>> {


    @Autowired
    private ObjectMapper mapper;



    public List<Incident> extractData(ResultSet rs) throws SQLException, DataAccessException {

        Map<String, Incident> serviceMap = new LinkedHashMap<>();

        while (rs.next()) {

            String id = rs.getString("ser_id");
            Incident currentService = serviceMap.get(id);

            if(currentService == null){

                id = rs.getString("ser_id");
                String IncidentType = rs.getString("ser_inctype");
                String incidentid = rs.getString("ser_incidentid");
                String description = rs.getString("ser_desc");
                String applicationStatus = rs.getString("applicationstatus");
                String createdby = rs.getString("ser_createdby");
                Long createdtime = rs.getLong("ser_createdtime");
                String lastmodifiedby = rs.getString("ser_lastmodifiedby");
                Long lastmodifiedtime = rs.getLong("ser_lastmodifiedtime");
                String requestType = rs.getString("ser_requesttype");
                String environment = rs.getString("ser_env");
                String summary = rs.getString("ser_summary");
                String pendingreason = rs.getString("ser_pendingreason");
                String priority = rs.getString("ser_priority");
                String impact = rs.getString("ser_impact");
                String urgency = rs.getString("ser_urgency");
                String affectedServices = rs.getString("ser_affectedservices");
                Integer rating = rs.getInt("ser_rating");
                String accountid=rs.getString("ser_accountid");
                if(rs.wasNull()){rating = null;}

                AuditDetails auditDetails = AuditDetails.builder().createdBy(createdby).createdTime(createdtime)
                                                .lastModifiedBy(lastmodifiedby).lastModifiedTime(lastmodifiedtime).build();

                currentService = Incident.builder().id(id)
                        .incidentType(IncidentType)
                        .incidentId(incidentid)
                        .description(description)
                        .applicationStatus(applicationStatus)
                        .rating(rating)
                        .requestType(requestType)
                        .environment(environment)
                        .summary(summary)
                        .pendingreason(pendingreason)
                        .priority(Priority.fromValue(priority))
                        .impact(impact)
                        .urgency(urgency)
                        .accountId(accountid)
                        .affectedServices(affectedServices)
                        .createdBy(auditDetails.getCreatedBy())
                        .createdTime(auditDetails.getCreatedTime())
                        .lastModifiedBy(auditDetails.getLastModifiedBy())
                        .lastModifiedTime(auditDetails.getLastModifiedTime())
                        .build();

              
                serviceMap.put(currentService.getId(),currentService);

            }
           // addChildrenToProperty(rs, currentService);

        }

        return new ArrayList<>(serviceMap.values());


    }

   


    private JsonNode getAdditionalDetail(String columnName, ResultSet rs){

        JsonNode additionalDetail = null;
        try {
            PGobject pgObj = (PGobject) rs.getObject(columnName);
            if(pgObj!=null){
                 additionalDetail = mapper.readTree(pgObj.getValue());
            }
        }
        catch (IOException | SQLException e){
            throw new CustomException("PARSING_ERROR","Failed to parse additionalDetail object");
        }
        return additionalDetail;
    }


}
