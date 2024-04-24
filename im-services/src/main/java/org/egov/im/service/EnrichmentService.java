package org.egov.im.service;

import static org.egov.im.util.IMConstants.USERTYPE_CITIZEN;
import static org.egov.im.util.WorkflowConstants.UUID_REGEX;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.egov.im.config.IMConfiguration;
import org.egov.im.entity.Role;
import org.egov.im.entity.User;
import org.egov.im.entity.Action;
import org.egov.im.entity.BusinessService;
import org.egov.im.entity.Incident;
import org.egov.im.entity.ProcessInstance;
import org.egov.im.entity.State;
import org.egov.im.entity.Users;
import org.egov.im.repository.IdGenRepository;
import org.egov.im.util.IMUtils;
import org.egov.im.util.WorkflowUtil;
import org.egov.im.web.models.AuditDetails;
import org.egov.im.web.models.IncidentRequest;
import org.egov.im.web.models.RequestInfo;
import org.egov.im.web.models.RequestSearchCriteria;
import org.egov.im.web.models.Workflow;
import org.egov.im.web.models.Idgen.IdResponse;
import org.egov.im.web.models.workflow.BusinessServiceRequest;
import org.egov.im.web.models.workflow.ProcessInstanceRequest;
import org.egov.im.web.models.workflow.ProcessStateAndAction;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@org.springframework.stereotype.Service
@Slf4j
public class EnrichmentService {


    private IMUtils utils;

    private IdGenRepository idGenRepository;

    private IMConfiguration config;

    private UsersService userService;

    private WorkflowUtil util;

    private TransitionService transitionService;

    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    public EnrichmentService(IMUtils utils, IdGenRepository idGenRepository, IMConfiguration config, 
    		UsersService userService,WorkflowUtil util,TransitionService transitionService) {
        this.utils = utils;
        this.idGenRepository = idGenRepository;
        this.config = config;
        this.userService = userService;
        this.util=util;
        this.transitionService=transitionService;
        }


    /**
     * Enriches the create request with auditDetails. uuids and custom ids from idGen service
     * @param serviceRequest The create request
     */
    public void enrichCreateRequest(IncidentRequest incidentRequest){

        RequestInfo requestInfo = incidentRequest.getRequestInfo();
        Incident incident = incidentRequest.getIncident();
        Workflow workflow = incidentRequest.getWorkflow();
        if(requestInfo.getUserInfo().getType().equalsIgnoreCase(USERTYPE_CITIZEN))
        incidentRequest.getIncident().setAccountId(requestInfo.getUserInfo().getUuid());

        userService.callUserService(incidentRequest);


        AuditDetails auditDetails = utils.getAuditDetails(requestInfo.getUserInfo().getUuid(), incident,true);

        incident.setCreatedBy(auditDetails.getCreatedBy());
        incident.setCreatedTime(auditDetails.getCreatedTime());
        incident.setId(UUID.randomUUID().toString());
        //incident.setActive(true);

        if(workflow.getVerificationDocuments()!=null){
            workflow.getVerificationDocuments().forEach(document -> {
                document.setId(UUID.randomUUID().toString());
            });
        }

        if(StringUtils.isEmpty(incident.getAccountId()))
        	incident.setAccountId(incident.getReporterr().getUuid());

        List<String> customIds = getIdList(requestInfo,null,config.getServiceRequestIdGenName(),config.getServiceRequestIdGenFormat(),1);

        incident.setIncidentId(customIds.get(0));

    }


    /**
     * Enriches the update request (updates the lastModifiedTime in auditDetails0
     * @param serviceRequest The update request
     */
    public void enrichUpdateRequest(IncidentRequest incidentRequest){

        RequestInfo requestInfo = incidentRequest.getRequestInfo();
        Incident incident = incidentRequest.getIncident();
        AuditDetails auditDetails = utils.getAuditDetails(requestInfo.getUserInfo().getUuid(), incident,false);

        incident.setCreatedBy(auditDetails.getCreatedBy());
        incident.setCreatedTime(auditDetails.getCreatedTime());
        incident.setLastModifiedBy(auditDetails.getLastModifiedBy());
        incident.setLastModifiedTime(auditDetails.getLastModifiedTime());
        userService.callUserService(incidentRequest);
    }

    /**
     * Enriches the search criteria in case of default search and enriches the userIds from mobileNumber in case of seach based on mobileNumber.
     * Also sets the default limit and offset if none is provided
     * @param requestInfo
     * @param criteria
     */
    public void enrichSearchRequest(RequestInfo requestInfo, RequestSearchCriteria criteria){

        if(criteria.isEmpty() && requestInfo.getUserInfo().getType().equalsIgnoreCase(USERTYPE_CITIZEN)){
            String citizenMobileNumber = requestInfo.getUserInfo().getUsername();
            criteria.setMobileNumber(citizenMobileNumber);
        }

        criteria.setAccountId(requestInfo.getUserInfo().getUuid());

        String tenantId = (criteria.getTenantId()!=null) ? criteria.getTenantId() : requestInfo.getUserInfo().getTenantId();

        if(criteria.getMobileNumber()!=null){
            userService.enrichUserIds(tenantId, criteria);
        }

        if(criteria.getLimit()==null)
            criteria.setLimit(config.getDefaultLimit());

        if(criteria.getOffset()==null)
            criteria.setOffset(config.getDefaultOffset());

        if(criteria.getLimit()!=null && criteria.getLimit() > config.getMaxLimit())
            criteria.setLimit(config.getMaxLimit());

    }


    /**
     * Returns a list of numbers generated from idgen
     *
     * @param requestInfo RequestInfo from the request
     * @param tenantId    tenantId of the city
     * @param idKey       code of the field defined in application properties for which ids are generated for
     * @param idformat    format in which ids are to be generated
     * @param count       Number of ids to be generated
     * @return List of ids generated using idGen service
     */
    private List<String> getIdList(RequestInfo requestInfo, String tenantId, String idKey,
                                   String idformat, int count) {
        List<IdResponse> idResponses = null;
		try {
			idResponses = idGenRepository.getId(requestInfo, tenantId, idKey, idformat, count).getIdResponses();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        if (CollectionUtils.isEmpty(idResponses))
            throw new CustomException("IDGEN ERROR", "No ids returned from idgen Service");

        return idResponses.stream()
                .map(IdResponse::getId).collect(Collectors.toList());
    }

    
    /**
     * Enriches the incoming list of businessServices
     * @param request The BusinessService request to be enriched
     */
    public void enrichCreateBusinessService(BusinessServiceRequest request){
        RequestInfo requestInfo = request.getRequestInfo();
        List<BusinessService> businessServices = request.getBusinessServices();
        AuditDetails auditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(),true);
        businessServices.forEach(businessService -> {
        	
        	String tenantId = businessService.getTenantId();
            businessService.setUuid(UUID.randomUUID().toString());
            businessService.setCreatedBy(auditDetails.getCreatedBy());
            businessService.setCreatedTime(auditDetails.getCreatedTime());
            businessService.setLastModifiedBy(auditDetails.getLastModifiedBy());
            businessService.setLastModifiedTime(auditDetails.getLastModifiedTime());
            businessService.getStates().forEach(state -> {
            	state.setBusinessService(businessService);
            	state.setCreatedBy(auditDetails.getCreatedBy());
                state.setCreatedTime(auditDetails.getCreatedTime());
                state.setLastModifiedBy(auditDetails.getLastModifiedBy());
                state.setLastModifiedTime(auditDetails.getLastModifiedTime());               
                state.setUuid(UUID.randomUUID().toString());
                state.setTenantId(tenantId);
                if(!CollectionUtils.isEmpty(state.getActions()))
                    state.getActions().forEach(action -> {
                    	action.setCreatedBy(auditDetails.getCreatedBy());
                        action.setCreatedTime(auditDetails.getCreatedTime());
                        action.setLastModifiedBy(auditDetails.getLastModifiedBy());
                        action.setLastModifiedTime(auditDetails.getLastModifiedTime());  
                        action.setUuid(UUID.randomUUID().toString());
                        action.setCurrentStatee(state);
                        action.setCurrentState(state.getUuid());
                        action.setTenantId(tenantId);
                        action.setActive(true);
                        List<String> r=action.getRole();
                        String ro=String.join(",",r);
                        action.setRoles(ro);
               
                        });
            });
            enrichNextState(businessService);
        });
    }

    /**
     * Enriches update request
     * @param request The update request
     */
    public void enrichUpdateBusinessService(BusinessServiceRequest request){
        RequestInfo requestInfo = request.getRequestInfo();
        List<BusinessService> businessServices = request.getBusinessServices();
        AuditDetails audit = util.getAuditDetails(requestInfo.getUserInfo().getUuid(),true);
        /*
        * Loop over all states and if any new state is encountered enrich it
        * */

        businessServices.forEach(businessService -> {
            businessService.setCreatedBy(audit.getCreatedBy());
            businessService.setCreatedTime(audit.getCreatedTime());
            businessService.setLastModifiedBy(audit.getLastModifiedBy());
            businessService.setLastModifiedTime(audit.getLastModifiedTime());
            businessService.getStates().forEach(state -> {
                if (state.getUuid() == null) {
                    state.setAuditDetails(audit);
                    state.setUuid(UUID.randomUUID().toString());
                    state.setTenantId(businessService.getTenantId());
                }
                else state.setAuditDetails(audit);
                });
            });

       /*
       * Extra loop is used as top loop needs to be completed first so that all new
       * states are assigned uuid which are required in the nextState map to assign
       * state uuid in the field nextState
       * */
        businessServices.forEach(businessService -> {
            businessService.getStates().forEach(state -> {
                if(!CollectionUtils.isEmpty(state.getActions()))
                    state.getActions().forEach(action -> {
                        if(action.getUuid()==null){
                            action.setAuditDetails(audit);
                            action.setUuid(UUID.randomUUID().toString());
                            action.setCurrentStatee(state);
                            action.setCurrentState(state.getUuid());
                            action.setTenantId(state.getTenantId());
                        }
                        else action.setAuditDetails(audit);
                    });
            });
            enrichNextState(businessService);
        });
    }

    /**
     * Enriches the nextState varibale in BusinessService
     * @param businessService The businessService whose action objects are to be enriched
     */
    private void enrichNextState(BusinessService businessService){
        Map<String,String> statusToUuidMap = new HashMap<>();
        businessService.getStates().forEach(state -> {
            statusToUuidMap.put(state.getState(),state.getUuid());
        });
        HashMap<String,String> errorMap = new HashMap<>();
        businessService.getStates().forEach(state -> {
            if(!CollectionUtils.isEmpty(state.getActions())){
                state.getActions().forEach(action -> {
                    if (!action.getNextState().matches(UUID_REGEX) && statusToUuidMap.containsKey(action.getNextState()))
                        action.setNextState(statusToUuidMap.get(action.getNextState()));
                    else if (!action.getNextState().matches(UUID_REGEX) && !statusToUuidMap.containsKey(action.getNextState()))
                        errorMap.put("INVALID NEXTSTATE","The state with name: "+action.getNextState()+ " does not exist in config");
                });
            }
        });
        if(!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }

    /**
     * Enriches incoming request
     * @param requestInfo The RequestInfo of the request
     * @param processStateAndActions List of ProcessStateAndAction containing ProcessInstance to be created
     */
    public void enrichProcessRequest(RequestInfo requestInfo,List<ProcessStateAndAction> processStateAndActions){
        AuditDetails auditDetails = util.getAuditDetails(requestInfo.getUserInfo().getUuid(),true);
        processStateAndActions.forEach(processStateAndAction -> {
            String tenantId = processStateAndAction.getProcessInstanceFromRequest().getTenantId();
            processStateAndAction.getProcessInstanceFromRequest().setId(UUID.randomUUID().toString());
            if(processStateAndAction.getAction().getNextState().equalsIgnoreCase(processStateAndAction.getAction().getCurrentState())){
                auditDetails.setCreatedBy(processStateAndAction.getProcessInstanceFromDb().getAuditDetails().getCreatedBy());
                auditDetails.setCreatedTime(processStateAndAction.getProcessInstanceFromDb().getAuditDetails().getCreatedTime());
            }
            processStateAndAction.getProcessInstanceFromRequest().setAuditDetails(auditDetails);
            processStateAndAction.getProcessInstanceFromRequest().setCreatedBy(auditDetails.getCreatedBy());
            processStateAndAction.getProcessInstanceFromRequest().setCreatedTime(auditDetails.getCreatedTime());
            processStateAndAction.getProcessInstanceFromRequest().setLastModifiedBy(auditDetails.getLastModifiedBy());
            processStateAndAction.getProcessInstanceFromRequest().setLastModifiedTime(auditDetails.getLastModifiedTime());
            processStateAndAction.getProcessInstanceFromRequest().setAssigner(requestInfo.getUserInfo().getUuid());
            if(!CollectionUtils.isEmpty(processStateAndAction.getProcessInstanceFromRequest().getDocuments())){
                processStateAndAction.getProcessInstanceFromRequest().getDocuments().forEach(document -> {
                    document.setAuditDetails(auditDetails);
                    document.setCreatedBy(auditDetails.getCreatedBy());
                    document.setCreatedTime(auditDetails.getCreatedTime());
                    document.setLastModifiedBy(auditDetails.getLastModifiedBy());
                    document.setLastModifiedTime(auditDetails.getLastModifiedTime());
                    document.setId(UUID.randomUUID().toString());
                });
            }
            Action action = processStateAndAction.getAction();
            Boolean isStateChanging = (action.getCurrentStatee().getUuid().equalsIgnoreCase( action.getNextState())) ? false : true;
            if(isStateChanging)
                processStateAndAction.getProcessInstanceFromRequest().setStateSla(processStateAndAction.getResultantState().getSla());
            enrichAndUpdateSlaForTransition(processStateAndAction,isStateChanging);
            setNextActions(requestInfo,processStateAndActions,true);
        });
        enrichUsers(requestInfo,processStateAndActions);
    }

    private void enrichAndUpdateSlaForTransition(ProcessStateAndAction processStateAndAction,Boolean isStateChanging){
        if(processStateAndAction.getProcessInstanceFromDb()!=null){
            Long businesssServiceSlaRemaining = processStateAndAction.getProcessInstanceFromDb().getBusinesssServiceSla();
            Long stateSlaRemaining = processStateAndAction.getProcessInstanceFromDb().getStateSla();
            Long timeSpent = processStateAndAction.getProcessInstanceFromRequest().getAuditDetails().getLastModifiedTime()
                           - processStateAndAction.getProcessInstanceFromDb().getAuditDetails().getLastModifiedTime();
            processStateAndAction.getProcessInstanceFromRequest().setBusinesssServiceSla(businesssServiceSlaRemaining-timeSpent);
            if(!isStateChanging && stateSlaRemaining!=null)
                processStateAndAction.getProcessInstanceFromRequest().setStateSla(stateSlaRemaining-timeSpent);
        }
    }





    /**
     * Enriches the processInstanceFromRequest with next possible action depending on current currentState
     * @param requestInfo The RequestInfo of the request
     * @param processStateAndActions
     */
    private void setNextActions(RequestInfo requestInfo,List<ProcessStateAndAction> processStateAndActions,Boolean isTransition){
        List<Role> roles = requestInfo.getUserInfo().getRoles();

        processStateAndActions.forEach(processStateAndAction -> {
            State state;
            String tenantId = processStateAndAction.getProcessInstanceFromRequest().getTenantId();
            if(isTransition)
             state = processStateAndAction.getResultantState();
            else state = processStateAndAction.getCurrentState();
            List<Action> nextAction = new ArrayList<>();
            if(!CollectionUtils.isEmpty( state.getActions())){
                state.getActions().forEach(action -> {
                    if(util.isRoleAvailable(tenantId,roles,action.getRole()) && !nextAction.contains(action))
                        nextAction.add(action);
                });
            }
            if(!CollectionUtils.isEmpty(nextAction))
                nextAction.sort(Comparator.comparing(Action::getAction));
        });
    }

    /**
     * Enriches the assignee and assigner user object from user search response
     * @param requestInfo The RequestInfo of the request
     * @param processStateAndActions The List of ProcessStateAndAction containing processInstanceFromRequest to be enriched
     */
    public void enrichUsers(RequestInfo requestInfo,List<ProcessStateAndAction> processStateAndActions){
        List<String> uuids = new LinkedList<>();

        processStateAndActions.forEach(processStateAndAction -> {

            if(!CollectionUtils.isEmpty(processStateAndAction.getProcessInstanceFromRequest().getAssignes()))
                uuids.addAll(processStateAndAction.getProcessInstanceFromRequest().getAssignes().stream().map(User::getUuid).collect(Collectors.toSet()));
            uuids.add(processStateAndAction.getProcessInstanceFromRequest().getAssigner());

            if(processStateAndAction.getProcessInstanceFromDb() != null){
                if(!CollectionUtils.isEmpty(processStateAndAction.getProcessInstanceFromDb().getAssignes())){
                    uuids.addAll(processStateAndAction.getProcessInstanceFromDb().getAssignes().stream().map(User::getUuid).collect(Collectors.toSet()));
                }
            }

        });


        Map<String,User> idToUserMap =userService.enrichUserss(uuids);

        Map<String,String> errorMap = new HashMap<>();
        processStateAndActions.forEach(processStateAndAction -> {

            // Setting Assignes
            if(!CollectionUtils.isEmpty(processStateAndAction.getProcessInstanceFromRequest().getAssignes())){
                enrichAssignes(processStateAndAction.getProcessInstanceFromRequest(), idToUserMap, errorMap);
            }

            // Setting Assigner
            if(processStateAndAction.getProcessInstanceFromRequest().getAssigner()!=null)
                enrichAssigner(processStateAndAction.getProcessInstanceFromRequest(), idToUserMap, errorMap);

            // Setting Assignes for previous processInstance
            if(processStateAndAction.getProcessInstanceFromDb()!=null && !CollectionUtils.isEmpty(processStateAndAction.getProcessInstanceFromDb().getAssignes())){
                enrichAssignes(processStateAndAction.getProcessInstanceFromDb(), idToUserMap, errorMap);
            }

        });
        if(!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }

    /**
     * Enriches the processInstance's assignes from the search response map of uuid to User
     * @param processInstance The processInstance to be enriched
     * @param idToUserMap Search response as a map of UUID to user
     */
    private void enrichAssignes(ProcessInstance processInstance, Map<String,User> idToUserMap, Map<String , String> errorMap){
        List<User> assignes = new LinkedList<>();
        processInstance.getAssignes().forEach(assigne -> {
            if(idToUserMap.containsKey(assigne.getUuid()))
                assignes.add(idToUserMap.get(assigne.getUuid()));
            else
                errorMap.put("INVALID UUID","User not found for uuid: "+assigne.getUuid());
        });
        processInstance.setAssignes(assignes);
    }

    /**
     * Enriches the processInstance's assigner from the search response map of uuid to User
     * @param processInstance The processInstance to be enriched
     * @param idToUserMap Search response as a map of UUID to user
     */
    private void enrichAssigner(ProcessInstance processInstance, Map<String,User> idToUserMap, Map<String , String> errorMap){
        User assigner = idToUserMap.get(processInstance.getAssigner());
        if(assigner==null)
            errorMap.put("INVALID UUID","User not found for uuid: "+processInstance.getAssigner());
        processInstance.setAssigner(assigner.getUuid());
        }
    
    /**
     * Enriches processInstanceFromRequest from the search response
     * @param processInstances The list of processInstances from search
     */
    public void enrichUsersFromSearch(RequestInfo requestInfo,List<ProcessInstance> processInstances){
        List<String> uuids = new LinkedList<>();
        processInstances.forEach(processInstance -> {

            if(!CollectionUtils.isEmpty(processInstance.getAssignes()))
                uuids.addAll(processInstance.getAssignes().stream().map(User::getUuid).collect(Collectors.toList()));

            uuids.add(processInstance.getAssigner());
        });
        Map<String,User> idToUserMap = userService.searchUser(requestInfo,uuids);
        Map<String,String> errorMap = new HashMap<>();
        processInstances.forEach(processInstance -> {

            // Enriching assignes if present
            if(!CollectionUtils.isEmpty(processInstance.getAssignes()))
                enrichAssignes(processInstance, idToUserMap, errorMap);

            // Enriching assigner if present
            if(processInstance.getAssigner()!=null)
                enrichAssigner(processInstance, idToUserMap, errorMap);

        });
        if(!errorMap.isEmpty())
            throw new CustomException(errorMap);
    }
    /**
     * Sets the businessServiceSla for search output
     * @param processInstances The list of processInstance
     */
    public void enrichAndUpdateSlaForSearch(List<ProcessInstance> processInstances){
        processInstances.forEach(processInstance -> {
            Long businessServiceSlaInDb = processInstance.getBusinesssServiceSla();
            Long stateSlaInDB = processInstance.getStateSla();
            Long timeSinceLastAction = System.currentTimeMillis() - processInstance.getAuditDetails().getLastModifiedTime();
            processInstance.setBusinesssServiceSla(businessServiceSlaInDb-timeSinceLastAction);
            if(stateSlaInDB!=null)
                processInstance.setStateSla(stateSlaInDB-timeSinceLastAction);
        });
    }

    public List<ProcessStateAndAction> enrichNextActionForSearch(RequestInfo requestInfo,List<ProcessInstance> processInstances){
        List<ProcessStateAndAction> processStateAndActions = new LinkedList<>();
        Map<String, List<ProcessInstance>> businessServiceToProcessInstance = getRequestByBusinessService(new ProcessInstanceRequest(requestInfo,processInstances));

        for(Map.Entry<String, List<ProcessInstance>> entry : businessServiceToProcessInstance.entrySet()){
            try{
             processStateAndActions.addAll(transitionService.getProcessStateAndActions(entry.getValue(),false));}
            catch (Exception e){
                log.error("Error while creating processStateAndActions",e);
            }
        }

        setNextActions(requestInfo,processStateAndActions,false);
        return processStateAndActions;
    }

    private Map<String,List<ProcessInstance>> getRequestByBusinessService(ProcessInstanceRequest request){
        List<ProcessInstance> processInstances = request.getProcessInstances();

        Map<String,List<ProcessInstance>> businessServiceToProcessInstance = new HashMap<>();
        if(!CollectionUtils.isEmpty(processInstances)){
            processInstances.forEach(processInstance -> {
                if(businessServiceToProcessInstance.containsKey(processInstance.getBusinessService()))
                    businessServiceToProcessInstance.get(processInstance.getBusinessService()).add(processInstance);
                else{
                    List<ProcessInstance> list = new ArrayList<>();
                    list.add(processInstance);
                    businessServiceToProcessInstance.put(processInstance.getBusinessService(),list);
                }
            });
        }

        return businessServiceToProcessInstance;
    }

}
