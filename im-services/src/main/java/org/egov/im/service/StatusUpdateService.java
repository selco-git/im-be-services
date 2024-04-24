package org.egov.im.service;

import java.util.LinkedList;
import java.util.List;

import org.egov.im.config.WorkflowConfig;
import org.egov.im.entity.Assignee;
import org.egov.im.entity.ProcessInstance;
import org.egov.im.entity.User;
import org.egov.im.producer.Producer;
import org.egov.im.repository.AssigneeRepository;
import org.egov.im.repository.ProcessInstanceRepository;
import org.egov.im.web.models.RequestInfo;
import org.egov.im.web.models.workflow.ProcessStateAndAction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class StatusUpdateService {

    private Producer producer;

    private WorkflowConfig config;
    
    private ProcessInstanceRepository  processInstanceRepository;
    
    private AssigneeRepository assigneeRepository;


    @Autowired
    public StatusUpdateService(WorkflowConfig config,ProcessInstanceRepository processInstanceRepository,AssigneeRepository assigneeRepository) {
        this.config = config;
        this.processInstanceRepository=processInstanceRepository;
        this.assigneeRepository=assigneeRepository;
    }


    /**
     * Updates the status and pushes the request on kafka to persist
      * @param requestInfo
     * @param processStateAndActions
     */
    public void updateStatus(RequestInfo requestInfo,List<ProcessStateAndAction> processStateAndActions){

        for(ProcessStateAndAction processStateAndAction : processStateAndActions){
            if(processStateAndAction.getProcessInstanceFromRequest().getState()!=null){
                String prevStatus = processStateAndAction.getProcessInstanceFromRequest().getState().getUuid();
                processStateAndAction.getProcessInstanceFromRequest().setPreviousStatus(prevStatus);
            }
            processStateAndAction.getProcessInstanceFromRequest().setState(processStateAndAction.getResultantState());
        }
        List<ProcessInstance> processInstances = new LinkedList<>();
        processStateAndActions.forEach(processStateAndAction -> {
            processInstances.add(processStateAndAction.getProcessInstanceFromRequest());
        });
        processInstanceRepository.save(processInstances.get(0));
        
        if(processInstances.get(0).getAssignes()!=null && processInstances.get(0).getAssignes().size()>0) {
        for(User user:processInstances.get(0).getAssignes())
        {
        	Assignee assignee=new Assignee();
        	assignee.setAssignee(user.getUuid());
        	assignee.setId(null);
        	assignee.setProcessinstanceid(processInstances.get(0).getId());
        	assignee.setCreatedBy(processInstances.get(0).getCreatedBy());        	
        	assignee.setCreatedTime(processInstances.get(0).getCreatedTime());        	
        	assignee.setLastModifiedBy(processInstances.get(0).getLastModifiedBy());        	
        	assignee.setLastModifiedTime(processInstances.get(0).getLastModifiedTime());        	
        	assigneeRepository.save(assignee);
        }
        }
        
        
    }







}
