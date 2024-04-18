package org.egov.im.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.im.entity.Action;
import org.egov.im.entity.BusinessService;
import org.egov.im.entity.State;
import org.egov.im.web.models.AuditDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class BusinessServiceRowMapper implements ResultSetExtractor<List<BusinessService>> {

    public List<BusinessService> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,BusinessService> businessServiceMap = new HashMap<>();

        while (rs.next()){
            String uuid = rs.getString("bs_uuid");
            BusinessService businessService = businessServiceMap.get(uuid);
            if(businessService==null){
                Long lastModifiedTime = rs.getLong("bs_lastModifiedTime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }
                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("bs_createdBy"))
                        .createdTime(rs.getLong("bs_createdTime"))
                        .lastModifiedBy(rs.getString("bs_lastModifiedBy"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();
                businessService = BusinessService.builder()
                        .tenantId(rs.getString("bs_tenantId"))
                        .getUri(rs.getString("geturi"))
                        .postUri(rs.getString("posturi"))
                        .businessService(rs.getString("businessService"))
                        .business(rs.getString("business"))
                        .uuid(uuid)
                        .businessServiceSla(rs.getLong("businessservicesla"))
                        .createdBy(rs.getString("createdby"))
                        .createdTime(rs.getLong("createdtime"))
                        .lastModifiedBy(rs.getString("lastmodifiedby"))
                        .lastModifiedTime(rs.getLong("lastModifiedTime"))
                        .build();
                businessServiceMap.put(uuid,businessService);
            }
            addChildrenToBusinessService(rs,businessService);
        }
        return new LinkedList<>(businessServiceMap.values());
    }




    /**
     *  Adds child object's (States and Actions) to parent Object(BusinessService)
     * @param rs The result set from sql query
     * @param businessService The parent object
     * @throws SQLException
     */
    private void addChildrenToBusinessService(ResultSet rs,BusinessService businessService) throws SQLException{

        String stateUuid = rs.getString("st_uuid");
        String actionUuid = rs.getString("ac_uuid");

        Long lastModifiedTime = rs.getLong("st_lastModifiedTime");
        if (rs.wasNull()) {
            lastModifiedTime = null;
        }

        State state;
        if(businessService.getStateFromUuid(stateUuid)==null){
            AuditDetails auditdetails = AuditDetails.builder()
                    .createdBy(rs.getString("st_createdBy"))
                    .createdTime(rs.getLong("st_createdTime"))
                    .lastModifiedBy(rs.getString("st_lastModifiedBy"))
                    .lastModifiedTime(lastModifiedTime)
                    .build();

            Long sla = rs.getLong("sla");
            if (rs.wasNull()) {
                sla = null;
            }

            state = State.builder()
                .tenantId(rs.getString("st_tenantId"))
                .uuid(stateUuid)
                .state(rs.getString("state"))
                .sla(sla)
                .applicationStatus(rs.getString("applicationStatus"))
                .isStartState(rs.getBoolean("isStartState"))
                .isTerminateState(rs.getBoolean("isTerminateState"))
                .docUploadRequired(rs.getBoolean("docuploadrequired"))
                .isStateUpdatable(rs.getBoolean("isStateUpdatable"))
                .businessService(businessService)
                .auditDetails(auditdetails)
                .createdBy(auditdetails.getCreatedBy())
                .createdTime(auditdetails.getCreatedTime())
                .lastModifiedBy(auditdetails.getLastModifiedBy())
                .lastModifiedTime(auditdetails.getLastModifiedTime())
                .build();

            businessService.addStatesItem(state);
        }
        else {
            state = businessService.getStateFromUuid(stateUuid);
        }

        if(actionUuid!=null){
            Long actionLastModifiedTime = rs.getLong("ac_lastModifiedTime");
            if (rs.wasNull()) {
                actionLastModifiedTime = null;
            }

            AuditDetails auditDetails = AuditDetails.builder()
                    .createdBy(rs.getString("ac_createdBy"))
                    .createdTime(rs.getLong("ac_createdTime"))
                    .lastModifiedBy(rs.getString("ac_lastModifiedBy"))
                    .lastModifiedTime(actionLastModifiedTime)
                    .build();

            Action action = Action.builder()
                    .tenantId(rs.getString("ac_tenantId"))
                    .action(rs.getString("action"))
                    .nextState(rs.getString("nextState"))
                    .uuid(actionUuid)
                    .currentState(rs.getString("currentState"))
                    .currentStatee(state)
                    .role(Arrays.asList(rs.getString("roles").split(",")))
                    .roles(rs.getString("roles"))
                    .active(rs.getBoolean("ac_active"))
                    .createdBy(auditDetails.getCreatedBy())
                    .createdTime(auditDetails.getCreatedTime())
                    .lastModifiedBy(auditDetails.getLastModifiedBy())
                    .lastModifiedTime(auditDetails.getLastModifiedTime())
                    .auditDetails(auditDetails)
                    .build();
            state.addActionsItem(action);
            
            action.setCurrentStatee(state);
            action.setCurrentState(state.getUuid());
        }
    }






    }
