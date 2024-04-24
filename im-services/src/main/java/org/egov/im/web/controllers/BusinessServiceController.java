package org.egov.im.web.controllers;


import java.util.List;

import javax.validation.Valid;

import org.egov.im.entity.BusinessService;
import org.egov.im.service.BusinessMasterService;
import org.egov.im.web.contract.factory.ResponseInfoFactory;
import org.egov.im.web.models.RequestInfoWrapper;
import org.egov.im.web.models.workflow.BusinessServiceRequest;
import org.egov.im.web.models.workflow.BusinessServiceResponse;
import org.egov.im.web.models.workflow.BusinessServiceSearchCriteria;
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
public class BusinessServiceController {

    private BusinessMasterService businessMasterService;

    private final ResponseInfoFactory responseInfoFactory;

    private ObjectMapper mapper;

    @Autowired
    public BusinessServiceController(BusinessMasterService businessMasterService, ResponseInfoFactory responseInfoFactory,
                                     ObjectMapper mapper) {
        this.businessMasterService = businessMasterService;
        this.responseInfoFactory = responseInfoFactory;
        this.mapper = mapper;
    }


    /**
     * Controller for creating BusinessService
     * @param businessServiceRequest The BusinessService request for create
     * @return The created object
     */
    @RequestMapping(value="/businessservice/_create", method = RequestMethod.POST)
    public ResponseEntity<BusinessServiceResponse> create(@Valid @RequestBody BusinessServiceRequest businessServiceRequest) {
        List<BusinessService> businessServices = businessMasterService.create(businessServiceRequest);
        BusinessServiceResponse response = BusinessServiceResponse.builder().businessServices(businessServices)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(businessServiceRequest.getRequestInfo(),true))
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }


    /**
     * Controller for searching BusinessService api
     * @param searchCriteria Object containing the search params
     * @param requestInfoWrapper The requestInfoWrapper object containing requestInfo
     * @return List of businessServices from db based on search params
     */
    @RequestMapping(value="/businessservice/_search", method = RequestMethod.POST)
    public ResponseEntity<BusinessServiceResponse> search(@Valid @ModelAttribute BusinessServiceSearchCriteria searchCriteria,
                                                          @Valid @RequestBody RequestInfoWrapper requestInfoWrapper) {

        List<BusinessService> businessServices = businessMasterService.search(searchCriteria);
        BusinessServiceResponse response = BusinessServiceResponse.builder().businessServices(businessServices)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(),true))
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }

    @RequestMapping(value="/businessservice/_update", method = RequestMethod.POST)
    public ResponseEntity<BusinessServiceResponse> update(@Valid @RequestBody BusinessServiceRequest businessServiceRequest) {
        List<BusinessService> businessServices = businessMasterService.update(businessServiceRequest);
        BusinessServiceResponse response = BusinessServiceResponse.builder().businessServices(businessServices)
                .responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(businessServiceRequest.getRequestInfo(),true))
                .build();
        return new ResponseEntity<>(response,HttpStatus.OK);
    }




}
