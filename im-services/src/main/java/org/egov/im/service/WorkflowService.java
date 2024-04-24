package org.egov.im.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.im.config.IMConfiguration;
import org.egov.im.config.WorkflowConfig;
import org.egov.im.entity.User;
import org.egov.im.entity.BusinessService;
import org.egov.im.entity.Incident;
import org.egov.im.entity.ProcessInstance;
import org.egov.im.entity.State;
import org.egov.im.entity.Users;
import org.egov.im.repository.BusinessServiceRepository;
import org.egov.im.repository.ServiceRequestRepository;
import org.egov.im.repository.WorKflowRepository;
import org.egov.im.util.IMConstants;
import org.egov.im.util.WorkflowUtil;
import org.egov.im.validator.WorkflowValidator;
import org.egov.im.web.models.IncidentRequest;
import org.egov.im.web.models.IncidentWrapper;
import org.egov.im.web.models.RequestInfo;
import org.egov.im.web.models.RequestInfoWrapper;
import org.egov.im.web.models.Workflow;
import org.egov.im.web.models.workflow.BusinessServiceSearchCriteria;
import org.egov.im.web.models.workflow.ProcessInstanceRequest;
import org.egov.im.web.models.workflow.ProcessInstanceResponse;
import org.egov.im.web.models.workflow.ProcessInstanceSearchCriteria;
import org.egov.im.web.models.workflow.ProcessStateAndAction;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;


@Service
public class WorkflowService {

    private WorkflowConfig config;

    private TransitionService transitionService;

    private EnrichmentService enrichmentService;
  

    private WorkflowValidator workflowValidator;

    private StatusUpdateService statusUpdateService;

    private WorKflowRepository workflowRepository;
   

    private WorkflowUtil util;

    private BusinessServiceRepository businessServiceRepository;
    
    private IMConfiguration imConfiguration;

    private ServiceRequestRepository repository;

    private ObjectMapper mapper;
    
    private IMConstants imConstants;


    @Autowired
    private BusinessMasterService businessMasterService;


    @Autowired
    public WorkflowService(WorkflowConfig config, TransitionService transitionService,
                           EnrichmentService enrichmentService, WorkflowValidator workflowValidator,
                           StatusUpdateService statusUpdateService, WorKflowRepository workflowRepository,
                           WorkflowUtil util,BusinessServiceRepository businessServiceRepository) {
        this.config = config;
        this.transitionService = transitionService;
        this.enrichmentService = enrichmentService;
        this.workflowValidator = workflowValidator;
        this.statusUpdateService = statusUpdateService;
        this.workflowRepository = workflowRepository;
        this.util = util;
        this.businessServiceRepository = businessServiceRepository;
    }


    /**
     * Creates or updates the processInstanceFromRequest
     * @param request The incoming request for workflow transition
     * @return The list of processInstanceFromRequest objects after taking action
     */
    public List<ProcessInstance> transition(ProcessInstanceRequest request){
        RequestInfo requestInfo = request.getRequestInfo();

        List<ProcessStateAndAction> processStateAndActions = transitionService.getProcessStateAndActions(request.getProcessInstances(),true);
        enrichmentService.enrichProcessRequest(requestInfo,processStateAndActions);
        workflowValidator.validateRequest(requestInfo,processStateAndActions);
        statusUpdateService.updateStatus(requestInfo,processStateAndActions);
        return request.getProcessInstances();
    }


    /**
     * Fetches ProcessInstances from db based on processSearchCriteria
     * @param requestInfo The RequestInfo of the search request
     * @param criteria The object containing Search params
     * @return List of processInstances based on search criteria
     */
    public List<ProcessInstance> search(RequestInfo requestInfo,ProcessInstanceSearchCriteria criteria){
        List<ProcessInstance> processInstances;
        if(criteria.isNull())
            processInstances = getUserBasedProcessInstances(requestInfo, criteria);
        else processInstances = workflowRepository.getProcessInstances(criteria);
        if(CollectionUtils.isEmpty(processInstances))
            return processInstances;

        enrichmentService.enrichUsersFromSearch(requestInfo,processInstances);
        List<ProcessStateAndAction> processStateAndActions = enrichmentService.enrichNextActionForSearch(requestInfo,processInstances);
    //    workflowValidator.validateSearch(requestInfo,processStateAndActions);
        enrichmentService.enrichAndUpdateSlaForSearch(processInstances);
        return processInstances;
    }


    



    /**
     * Searches the processInstances based on user and its roles
     * @param requestInfo The RequestInfo of the search request
     * @param criteria The object containing Search params
     * @return List of processInstances based on search criteria
     */
    private List<ProcessInstance> getUserBasedProcessInstances(RequestInfo requestInfo,
                                       ProcessInstanceSearchCriteria criteria){

        enrichSearchCriteriaFromUser(requestInfo, criteria);
        List<ProcessInstance> processInstances = workflowRepository.getProcessInstancesForUserInbox(criteria);

        processInstances = filterDuplicates(processInstances);

        return processInstances;

    }
    public Integer getUserBasedProcessInstancesCount(RequestInfo requestInfo,ProcessInstanceSearchCriteria criteria){
        Integer count;
        count = workflowRepository.getProcessInstancesForUserInboxCount(criteria);
        return count;
    }

    /**
     * Removes duplicate businessId which got created due to simultaneous request
     * @param processInstances
     * @return
     */
    private List<ProcessInstance> filterDuplicates(List<ProcessInstance> processInstances){

        if(CollectionUtils.isEmpty(processInstances))
            return processInstances;

        Map<String,ProcessInstance> businessIdToProcessInstanceMap = new LinkedHashMap<>();

        for(ProcessInstance processInstance : processInstances){
            businessIdToProcessInstanceMap.put(processInstance.getBusinessId(), processInstance);
        }

        return new LinkedList<>(businessIdToProcessInstanceMap.values());
    }
    
   
    /**
     * Enriches processInstance search criteria based on requestInfo
     * @param requestInfo
     * @param criteria
     */
    private void enrichSearchCriteriaFromUser(RequestInfo requestInfo,ProcessInstanceSearchCriteria criteria){

        /*BusinessServiceSearchCriteria businessServiceSearchCriteria = new BusinessServiceSearchCriteria();

        *//*
         * If tenantId is sent in query param processInstances only for that tenantId is returned
         * else all tenantIds for which the user has roles are returned
         * *//*
        if(criteria.getTenantId()!=null)
            businessServiceSearchCriteria.setTenantIds(Collections.singletonList(criteria.getTenantId()));
        else
            businessServiceSearchCriteria.setTenantIds(util.getTenantIds(requestInfo.getUserInfo()));

        Map<String, Boolean> stateLevelMapping = stat

        List<BusinessService> businessServices = businessServiceRepository.getAllBusinessService();
        List<String> actionableStatuses = util.getActionableStatusesForRole(requestInfo,businessServices,criteria);
        criteria.setAssignee(requestInfo.getUserInfo().getUuid());
        criteria.setStatus(actionableStatuses);*/

        util.enrichStatusesInSearchCriteria(requestInfo, criteria);
        criteria.setAssignee(requestInfo.getUserInfo().getUuid());


    }


     
   public BusinessService getBusinessService(IncidentRequest incidentRequest) {
       String tenantId = incidentRequest.getIncident().getTenantId();
       BusinessServiceSearchCriteria searchCriteria=new BusinessServiceSearchCriteria();
       List<String> list=new ArrayList<String>();
       list.add(imConstants.IM_BUSINESSSERVICE);
       searchCriteria.setBusinessServices(list);
       searchCriteria.setTenantId(tenantId);     
       List<BusinessService> response=businessMasterService.search(searchCriteria);
       if(response.isEmpty())
           throw new CustomException("NOT FOUND ERROR", "No Business service exists");
       return response.get(0);
   }


   /*
    * Call the workflow service with the given action and update the status
    * return the updated status of the application
    *
    * */
   public String updateWorkflowStatus(IncidentRequest incidentRequest) {
       ProcessInstance processInstance = getProcessInstanceForIM(incidentRequest);
       ProcessInstanceRequest workflowRequest = new ProcessInstanceRequest(incidentRequest.getRequestInfo(), Collections.singletonList(processInstance));
       State state = callWorkFlow(workflowRequest);
       incidentRequest.getIncident().setApplicationStatus(state.getApplicationStatus());
       return state.getApplicationStatus();
   }


   public void validateAssignee(IncidentRequest incidentRequest) {
       /*
        * Call HRMS service and validate of the assignee belongs to same department
        * as the employee assigning it
        *
        * */

   }




   public void enrichmentForSendBackToCititzen() {
       /*
        * If send bac to citizen action is taken assignes should be set to accountId
        *
        * */
   }


   public List<IncidentWrapper> enrichWorkflow(RequestInfo requestInfo, List<IncidentWrapper> incidentWrappers, String tenantId) {

       Map<String, List<IncidentWrapper>> tenantIdToServiceWrapperMap = getTenantIdToServiceWrapperMap(incidentWrappers,tenantId);
       ProcessInstanceSearchCriteria criteria=new ProcessInstanceSearchCriteria();
       List<IncidentWrapper> enrichedServiceWrappers = new ArrayList<>();

       for(String tenantid : tenantIdToServiceWrapperMap.keySet()) {

           List<String> incidentIds = new ArrayList<>();

           List<IncidentWrapper> tenantSpecificWrappers = tenantIdToServiceWrapperMap.get(tenantid);

           tenantSpecificWrappers.forEach(imEntity -> {
               incidentIds.add(imEntity.getIncident().getIncidentId());
           });

           criteria.setTenantId(tenantId);
           criteria.setBusinessIds(incidentIds);         
           RequestInfoWrapper requestInfoWrapper = RequestInfoWrapper.builder().requestInfo(requestInfo).build();
           List<ProcessInstance> processInstances = search(requestInfoWrapper.getRequestInfo(),criteria);

           Map<String, Workflow> businessIdToWorkflow = getWorkflow(processInstances);
           tenantSpecificWrappers.forEach(pgrEntity -> {
               pgrEntity.setWorkflow(businessIdToWorkflow.get(pgrEntity.getIncident().getIncidentId()));
           });

           enrichedServiceWrappers.addAll(tenantSpecificWrappers);
       }

       return enrichedServiceWrappers;

   }

   private Map<String, List<IncidentWrapper>> getTenantIdToServiceWrapperMap(List<IncidentWrapper> incidentWrappers,String tenantId) {
       Map<String, List<IncidentWrapper>> resultMap = new HashMap<>();
       for(IncidentWrapper incidentWrapper : incidentWrappers){
           if(resultMap.containsKey(incidentWrapper.getIncident().getTenantId())){
               resultMap.get(incidentWrapper.getIncident().getTenantId()).add(incidentWrapper);
           }else{
               List<IncidentWrapper> incidentWrapperList = new ArrayList<>();
               incidentWrapperList.add(incidentWrapper);
               resultMap.put(incidentWrapper.getIncident().getTenantId()==null?tenantId:incidentWrapper.getIncident().getTenantId(), incidentWrapperList);
           }
       }
       return resultMap;
   }

   /**
    * Enriches ProcessInstance Object for workflow
    *
    * @param request
    */
   @SuppressWarnings("static-access")
private ProcessInstance getProcessInstanceForIM(IncidentRequest request) {

       Incident incident = request.getIncident();
       Workflow workflow = request.getWorkflow();

       ProcessInstance processInstance = new ProcessInstance();
       processInstance.setBusinessId(incident.getIncidentId());
       processInstance.setAction(request.getWorkflow().getAction());
       processInstance.setModuleName(imConstants.IM_BUSINESSSERVICE);
       processInstance.setTenantId(incident.getTenantId());
       processInstance.setBusinessService(getBusinessService(request).getBusinessService());
       processInstance.setDocuments(request.getWorkflow().getVerificationDocuments());
       processInstance.setComment(workflow.getComments());

       if(!CollectionUtils.isEmpty(workflow.getAssignes())){
           List<User> users = new ArrayList<>();

           workflow.getAssignes().forEach(uuid -> {
               User user = new User();
               user.setUuid(uuid);
               users.add(user);
           });

           processInstance.setAssignes(users);
       }

       return processInstance;
   }

   /**
    *
    * @param processInstances
    */
   public Map<String, Workflow> getWorkflow(List<ProcessInstance> processInstances) {

       Map<String, Workflow> businessIdToWorkflow = new HashMap<>();

       processInstances.forEach(processInstance -> {
           List<String> userIds = null;

           if(!CollectionUtils.isEmpty(processInstance.getAssignes())){
               userIds = processInstance.getAssignes().stream().map(User::getUuid).collect(Collectors.toList());
           }

           Workflow workflow = Workflow.builder()
                   .action(processInstance.getAction())
                   .assignes(userIds)
                   .comments(processInstance.getComment())
                   .verificationDocuments(processInstance.getDocuments())
                   .build();

           businessIdToWorkflow.put(processInstance.getBusinessId(), workflow);
       });

       return businessIdToWorkflow;
   }

   /**
    * Method to integrate with workflow
    * <p>
    * take the ProcessInstanceRequest as paramerter to call wf-service
    * <p>
    * and return wf-response to sets the resultant status
    */
   private State callWorkFlow(ProcessInstanceRequest workflowReq) {

       ProcessInstanceResponse response = null;
       List<ProcessInstance> processInstances =  transition(workflowReq);
       return processInstances.get(0).getState();
   }


   
}
