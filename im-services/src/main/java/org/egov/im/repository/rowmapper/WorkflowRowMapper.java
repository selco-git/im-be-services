package org.egov.im.repository.rowmapper;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.egov.im.entity.Action;
import org.egov.im.entity.Document;
import org.egov.im.entity.ProcessInstance;
import org.egov.im.entity.State;
import org.egov.im.entity.User;
import org.egov.im.web.models.AuditDetails;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class WorkflowRowMapper implements ResultSetExtractor<List<ProcessInstance>> {


    /**
     * Converts resultset to List of processInstances
     * @param rs The resultSet from db query
     * @return List of ProcessInstances from the resultset
     * @throws SQLException
     * @throws DataAccessException
     */
    public List<ProcessInstance> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,ProcessInstance> processInstanceMap = new LinkedHashMap<>();

        while (rs.next()){
            String id = rs.getString("wf_id");
            ProcessInstance processInstance = processInstanceMap.get(id);

            if(processInstance==null) {
                Long lastModifiedTime = rs.getLong("wf_lastModifiedTime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }

                Long sla = rs.getLong("sla");
                if (rs.wasNull()) {
                    sla = null;
                }

                Long businessServiceSla = rs.getLong("businessservicesla");
                if (rs.wasNull()) {
                    businessServiceSla = null;
                }

                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("wf_createdBy"))
                        .createdTime(rs.getLong("wf_createdTime"))
                        .lastModifiedBy(rs.getString("wf_lastModifiedBy"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();


                // Building the assigner object
                String assignerUuid = rs.getString("assigner");
                User assigner;
                assigner = User.builder().uuid(assignerUuid).build();




                State state = State.builder()
                        .tenantId(rs.getString("st_tenantId"))
                        .uuid(rs.getString("st_uuid"))
                        .state(rs.getString("state"))
                        .sla(sla)
                        .applicationStatus(rs.getString("applicationStatus"))
                        .isStartState(rs.getBoolean("isStartState"))
                        .isTerminateState(rs.getBoolean("isTerminateState"))
                        .docUploadRequired(rs.getBoolean("docuploadrequired"))
                        //.businessService(rs.getString("businessserviceid"))
                        .build();


                processInstance = ProcessInstance.builder()
                        .id(rs.getString("id"))
                        .tenantId(rs.getString("tenantid"))
                        .businessService(rs.getString("businessService"))
                        .businessId(rs.getString("businessId"))
                        .action(rs.getString("action"))
                        .state(state)
                        .comment(rs.getString("comment"))
                        .assigner(assigner)
                        .stateSla(sla)
                        .businesssServiceSla(businessServiceSla)
                        .previousStatus(rs.getString("previousStatus"))
                        .moduleName(rs.getString("moduleName"))
                        .auditDetails(auditdetails)
                        .createdBy(auditdetails.getCreatedBy())
                        .createdTime(auditdetails.getCreatedTime())
                        .lastModifiedBy(auditdetails.getLastModifiedBy())
                        .lastModifiedTime(auditdetails.getLastModifiedTime())
                        .rating(rs.getInt("rating"))
                        .escalated(rs.getBoolean("escalated"))
                        .build();
            }
            addChildrenToProperty(rs,processInstance);
            processInstanceMap.put(id,processInstance);
        }
        return new ArrayList<>(processInstanceMap.values());
    }


    /**
     * Adds nested object to the parent
     * @param rs The resultSet from db query
     * @param processInstance The parent ProcessInstance Object
     * @throws SQLException
     */
    private void addChildrenToProperty(ResultSet rs, ProcessInstance processInstance) throws SQLException {

        // Building the assignes object
        String assigneeUuid = rs.getString("assigneeuuid");

        if(!StringUtils.isEmpty(assigneeUuid)){
            processInstance.addUsersItem(User.builder().uuid(assigneeUuid).build());
        }


        String documentId = rs.getString("doc_id");

        if(documentId!=null){

            Long lastModifiedTime = rs.getLong("doc_lastModifiedTime");
            if (rs.wasNull()) {
                lastModifiedTime = null;
            }

            AuditDetails auditdetails = AuditDetails.builder()
                    .createdBy(rs.getString("doc_createdBy"))
                    .createdTime(rs.getLong("doc_createdTime"))
                    .lastModifiedBy(rs.getString("doc_lastModifiedBy"))
                    .lastModifiedTime(lastModifiedTime)
                    .build();

            Document document = Document.builder()
                    .id(documentId)
                    .documentUid(rs.getString("documentUid"))
                    .documentType(rs.getString("documentType"))
                    .fileStoreId(rs.getString("fileStoreId"))
                    .auditDetails(auditdetails)
                    .createdBy(auditdetails.getCreatedBy())
                    .createdTime(auditdetails.getCreatedTime())
                    .lastModifiedBy(auditdetails.getLastModifiedBy())
                    .lastModifiedTime(auditdetails.getLastModifiedTime())
                    .build();
            processInstance.addDocumentsItem(document);
        }

        String actionUuid = rs.getString("ac_uuid");
        /*
         * null check added for action id to avoid adding empty action object in end state
         * 
         * also avoiding action related errors on end state
         */
        if(null != actionUuid) {
        String roles = rs.getString("roles");
        Action action = Action.builder()
                .tenantId(rs.getString("ac_tenantId"))
                .action(rs.getString("ac_action"))
                .nextState(rs.getString("nextState"))
                .uuid(actionUuid)
               // .currentStatee.setUuid(rs.getString("currentState"))
                .role(StringUtils.isEmpty(roles) ? Arrays.asList() : Arrays.asList(roles.split(",")))
                .roles(roles)
                .build();
        processInstance.getState().addActionsItem(action);
        }
    }



    }
