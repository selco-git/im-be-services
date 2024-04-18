package org.egov.im.service;


import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.im.entity.Action;
import org.egov.im.entity.BusinessService;
import org.egov.im.entity.ProcessInstance;
import org.egov.im.entity.State;
import org.egov.im.repository.BusinessServiceRepository;
import org.egov.im.repository.WorKflowRepository;
import org.egov.im.util.WorkflowUtil;
import org.egov.im.web.models.workflow.BusinessServiceSearchCriteria;
import org.egov.im.web.models.workflow.ProcessInstanceSearchCriteria;
import org.egov.im.web.models.workflow.ProcessStateAndAction;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TransitionService {


    private WorKflowRepository repository;

    private BusinessServiceRepository businessServiceRepository;

    private WorkflowUtil workflowUtil;



    @Autowired
    public TransitionService(WorKflowRepository repository,
                             BusinessServiceRepository businessServiceRepository,
                             WorkflowUtil workflowUtil) {
        this.repository = repository;
        this.businessServiceRepository = businessServiceRepository;
        this.workflowUtil = workflowUtil;
    }




    /**
     * Creates list of ProcessStateAndAction from the list of the processInstances
     * @return List of ProcessStateAndAction containing the State object for status before the action and after the action and
     * the Action object for the given action
     */
    public List<ProcessStateAndAction> getProcessStateAndActions(List<ProcessInstance> processInstances,Boolean isTransitionCall){
        List<ProcessStateAndAction> processStateAndActions = new LinkedList<>();

        BusinessService businessService = getBusinessService(processInstances);
        Map<String,ProcessInstance> idToProcessInstanceFromDbMap = prepareProcessStateAndAction(processInstances,businessService);
        List<String> allowedRoles = workflowUtil.rolesAllowedInService(businessService);
        for(ProcessInstance processInstance: processInstances){

            ProcessStateAndAction processStateAndAction = new ProcessStateAndAction();
            processStateAndAction.setProcessInstanceFromRequest(processInstance);
            if(isTransitionCall){
                processStateAndAction.getProcessInstanceFromRequest().setModuleName(businessService.getBusiness());
            }
            processStateAndAction.setProcessInstanceFromDb(idToProcessInstanceFromDbMap.get(processInstance.getBusinessId()));
            State currentState = null;
            if(processStateAndAction.getProcessInstanceFromDb()!=null && isTransitionCall)
            {
               // currentState = processStateAndAction.getProcessInstanceFromDb().getState();
                
                for(State state : businessService.getStates()){
                    if(state.getState()!=null && state.getState().equalsIgnoreCase(processStateAndAction.getProcessInstanceFromDb().getState().getUuid())){
                        
                    	currentState=state;
                        break;
                    }
                }
            }
            else if(!isTransitionCall)
                currentState = processStateAndAction.getProcessInstanceFromRequest().getState();


            //Assign businessSla when creating processInstance
            if(processStateAndAction.getProcessInstanceFromDb()==null && isTransitionCall)
                processInstance.setBusinesssServiceSla(businessService.getBusinessServiceSla());


            if(currentState==null){
                for(State state : businessService.getStates()){
                    if(StringUtils.isEmpty(state.getState())){
                        processStateAndAction.setCurrentState(state);
                        break;
                    }
                }
            }
        else processStateAndAction.setCurrentState(currentState);


            if(!CollectionUtils.isEmpty(processStateAndAction.getCurrentState().getActions())){
                for (Action action : processStateAndAction.getCurrentState().getActions()){
                    if(action.getAction().equalsIgnoreCase(processInstance.getAction())){
                        if(action.getRoles().contains("*"))
                            action.setRole(allowedRoles);
                        processStateAndAction.setAction(action);
                        break;
                    }
                }
            }


            if(isTransitionCall){
                if(processStateAndAction.getAction()==null)
                    throw new CustomException("INVALID ACTION","Action "+processStateAndAction.getProcessInstanceFromRequest().getAction()
                            + " not found in config for the businessId: "
                            +processStateAndAction.getProcessInstanceFromRequest().getBusinessId());

                for(State state : businessService.getStates()){
                    if(state.getUuid().equalsIgnoreCase(processStateAndAction.getAction().getNextState())){
                        processStateAndAction.setResultantState(state);
                        break;
                    }
                }
            }

            processStateAndActions.add(processStateAndAction);

        }


        return processStateAndActions;
    }




    /**
     * Current status of the incoming request is fetched from the DB and set
     *
     * If the request object is being created for the first time
     *
     * then state will remain null
     *
     * @param processInstances The list of ProcessInstance to be created
     */
    private Map<String,ProcessInstance> prepareProcessStateAndAction(List<ProcessInstance> processInstances,BusinessService businessService) {

        /*
         * preparing the criteria to search the process instances from DB
         */
        ProcessInstanceSearchCriteria criteria = new ProcessInstanceSearchCriteria();
        List<String> businessIds = processInstances.stream().map(ProcessInstance::getBusinessId)
                .collect(Collectors.toList());
        criteria.setTenantId(processInstances.get(0).getTenantId());
        criteria.setBusinessIds(businessIds);
        /*
         * fetching the result from repository
         *
         * converting the list of process instances to map of businessId and state
         * object
         */
        List<ProcessInstance> processInstancesFromDB = repository.getProcessInstances(criteria);

        Map<String, ProcessInstance> businessStateMap = new LinkedHashMap<>();
        for(ProcessInstance processInstance : processInstancesFromDB){
            businessStateMap.put(processInstance.getBusinessId(), processInstance);
        }

        return businessStateMap;
    }



    private BusinessService getBusinessService(List<ProcessInstance> processInstances){
        BusinessServiceSearchCriteria criteria = new BusinessServiceSearchCriteria();
        String tenantId = processInstances.get(0).getTenantId();
        String businessService = processInstances.get(0).getBusinessService();
        criteria.setTenantId(tenantId);
        criteria.setBusinessServices(Collections.singletonList(businessService));
        List<BusinessService> businessServices = businessServiceRepository.getBusinessServices(criteria);
        if(CollectionUtils.isEmpty(businessServices))
            throw new CustomException("BUSINESSSERVICE ERROR","No bussinessService object found for businessSerice: "+
                    businessService + " and tenantId: "+tenantId);
        if(businessServices.size()!=1)
            throw new CustomException("BUSINESSSERVICE ERROR","Multiple bussinessService object found for businessSerice: "+
                    businessService + " and tenantId: "+tenantId);
        return businessServices.get(0);
    }
















}
