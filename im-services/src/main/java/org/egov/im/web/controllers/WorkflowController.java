package org.egov.im.web.controllers;


import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.egov.im.entity.ProcessInstance;
import org.egov.im.service.WorkflowService;
import org.egov.im.web.contract.factory.ResponseInfoFactory;
import org.egov.im.web.models.RequestInfoWrapper;
import org.egov.im.web.models.workflow.ProcessInstanceRequest;
import org.egov.im.web.models.workflow.ProcessInstanceResponse;
import org.egov.im.web.models.workflow.ProcessInstanceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;


@RestController
@RequestMapping("/egov-wf")
public class WorkflowController {


    private final ObjectMapper objectMapper;

    private final HttpServletRequest request;

    private final WorkflowService workflowService;

    private final ResponseInfoFactory responseInfoFactory;


    @Autowired
    public WorkflowController(ObjectMapper objectMapper, HttpServletRequest request,
                              WorkflowService workflowService, ResponseInfoFactory responseInfoFactory) {
        this.objectMapper = objectMapper;
        this.request = request;
        this.workflowService = workflowService;
        this.responseInfoFactory = responseInfoFactory;
    }



        @RequestMapping(value="/process/_transition", method = RequestMethod.POST)
        public ResponseEntity<ProcessInstanceResponse> processTransition(@Valid @RequestBody ProcessInstanceRequest processInstanceRequest) {
                List<ProcessInstance> processInstances =  workflowService.transition(processInstanceRequest);
                ProcessInstanceResponse response = ProcessInstanceResponse.builder().processInstances(processInstances)
                        .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(processInstanceRequest.getRequestInfo(), true))
                        .build();
                return new ResponseEntity<>(response,HttpStatus.OK);
        }




        @RequestMapping(value="/process/_search", method = RequestMethod.POST)
        public ResponseEntity<ProcessInstanceResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
                                                              @Valid @ModelAttribute ProcessInstanceSearchCriteria criteria) {
        List<ProcessInstance> processInstances = workflowService.search(requestInfoWrapper.getRequestInfo(),criteria);
        Integer count = workflowService.getUserBasedProcessInstancesCount(requestInfoWrapper.getRequestInfo(),criteria);
            ProcessInstanceResponse response  = ProcessInstanceResponse.builder().processInstances(processInstances).totalCount(count).build();
                return new ResponseEntity<>(response,HttpStatus.OK);
        }



}
