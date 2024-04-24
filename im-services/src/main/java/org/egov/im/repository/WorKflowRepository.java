package org.egov.im.repository;


import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.egov.im.entity.ProcessInstance;
import org.egov.im.repository.querybuilder.WorkflowQueryBuilder;
import org.egov.im.repository.rowmapper.WorkflowRowMapper;
import org.egov.im.web.models.RequestInfo;
import org.egov.im.web.models.workflow.ProcessInstanceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class WorKflowRepository {

    private WorkflowQueryBuilder queryBuilder;

    private JdbcTemplate jdbcTemplate;

    private WorkflowRowMapper rowMapper;


    @Autowired
    public WorKflowRepository(WorkflowQueryBuilder queryBuilder, JdbcTemplate jdbcTemplate, WorkflowRowMapper rowMapper) {
        this.queryBuilder = queryBuilder;
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }


    /**
     * Executes the search criteria on the db
     * @param criteria The object containing the params to search on
     * @return The parsed response from the search query
     */
    public List<ProcessInstance> getProcessInstances(ProcessInstanceSearchCriteria criteria){
        List<Object> preparedStmtList = new ArrayList<>();

        List<String> ids = getProcessInstanceIds(criteria);

        if(CollectionUtils.isEmpty(ids))
            return new LinkedList<>();

        String query = queryBuilder.getProcessInstanceSearchQueryById(ids, preparedStmtList);
        log.debug("query for status search: "+query+" params: "+preparedStmtList);

        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }


    public Integer getProcessInstancesCount(ProcessInstanceSearchCriteria criteria){
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getProcessInstanceCount(criteria, preparedStmtList,Boolean.FALSE);
        return jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), Integer.class);
    }

    

    private List<String> getProcessInstanceIds(ProcessInstanceSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        String query = queryBuilder.getProcessInstanceIds(criteria,preparedStmtList);
        log.info(query);
        log.info(preparedStmtList.toString());
        return jdbcTemplate.query(query, preparedStmtList.toArray(), new SingleColumnRowMapper<>(String.class));
    }


    public List<String> fetchEscalatedApplicationsBusinessIdsFromDb(RequestInfo requestInfo,ProcessInstanceSearchCriteria criteria) {
        ArrayList<Object> preparedStmtList = new ArrayList<>();

        // 1st step is to fetch businessIds based on the assignee and the module.
        /*

        String query = queryBuilder.getInboxApplicationsBusinessIdsQuery(criteria, preparedStmtList);
        List<String> inboxApplicationsBusinessIds = jdbcTemplate.query(query, preparedStmtList.toArray(), new SingleColumnRowMapper<>(String.class));
        log.info(inboxApplicationsBusinessIds.toString());
        preparedStmtList.clear();

        // (DONE) 2nd step is to fetch businessIds of inbox applications which have been autoEscalated at least once in their wf
        // (DONE) For this step, fetch AUTO_ESCALATION_EMPLOYEES uuids based on role codes by doing a call to user service
        // (PENDING) Also, add the call to mdms service for filtering out states which need to be excluded

        criteria.setBusinessIds(inboxApplicationsBusinessIds);
         */
        String query = queryBuilder.getAutoEscalatedApplicationsFinalQuery(requestInfo,criteria, preparedStmtList);
        log.info(query);
        List<String> escalatedApplicationsBusinessIds = jdbcTemplate.query(query, preparedStmtList.toArray(), new SingleColumnRowMapper<>(String.class));
        preparedStmtList.clear();
        log.info(escalatedApplicationsBusinessIds.toString());
        // 3rd step is to do a simple search on these business ids(DONE IN WORKFLOW SERVICE)

        return escalatedApplicationsBusinessIds;
    }

    public List<ProcessInstance> getProcessInstancesForUserInbox(ProcessInstanceSearchCriteria criteria){
        List<Object> preparedStmtList = new ArrayList<>();

        if(CollectionUtils.isEmpty(criteria.getStatus()) && CollectionUtils.isEmpty(criteria.getTenantSpecifiStatus()))
            return new LinkedList<>();

        List<String> ids = getInboxSearchIds(criteria);

        if(CollectionUtils.isEmpty(ids))
            return new LinkedList<>();

        String query = queryBuilder.getProcessInstanceSearchQueryById(ids, preparedStmtList);
        log.debug("query for status search: "+query+" params: "+preparedStmtList);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), rowMapper);
    }

    public Integer getProcessInstancesForUserInboxCount(ProcessInstanceSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        criteria.setIsAssignedToMeCount(true);
        String query = queryBuilder.getInboxIdCount(criteria, (ArrayList<Object>) preparedStmtList);
        Integer count =  jdbcTemplate.queryForObject(query, preparedStmtList.toArray(), Integer.class);
        return count;
    }
    private List<String> getInboxSearchIds(ProcessInstanceSearchCriteria criteria) {
        List<Object> preparedStmtList = new ArrayList<>();
        criteria.setIsAssignedToMeCount(false);
        String query = queryBuilder.getInboxIdQuery(criteria,preparedStmtList,true);
        return jdbcTemplate.query(query, preparedStmtList.toArray(), new SingleColumnRowMapper<>(String.class));
    }

}