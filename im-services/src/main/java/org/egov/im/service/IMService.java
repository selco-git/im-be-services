package org.egov.im.service;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.egov.im.config.IMConfiguration;
import org.egov.im.producer.Producer;
import org.egov.im.repository.IMRepository;
import org.egov.im.validator.ServiceRequestValidator;
import org.egov.im.web.models.IncidentRequest;
import org.egov.im.web.models.IncidentWrapper;
import org.egov.im.web.models.RequestInfo;
import org.egov.im.web.models.RequestSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Service
public class IMService {



    private EnrichmentService enrichmentService;

    private UsersService userService;

    private WorkflowService workflowService;

    private ServiceRequestValidator serviceRequestValidator;

    private ServiceRequestValidator validator;

    private Producer producer;

    private IMConfiguration config;

    private IMRepository repository;

    @Autowired
    private IncidentService incidentService;
    
    @Autowired
    private NotificationService notificationService;


    public IMService(EnrichmentService enrichmentService, UsersService userService, WorkflowService workflowService,
                      ServiceRequestValidator serviceRequestValidator, ServiceRequestValidator validator, Producer producer,
                      IMConfiguration config, IMRepository repository,IncidentService incidentService,NotificationService notificationService) {
        this.enrichmentService = enrichmentService;
        this.userService = userService;
        this.workflowService = workflowService;
        this.serviceRequestValidator = serviceRequestValidator;
        this.validator = validator;
        this.producer = producer;
        this.config = config;
        this.repository = repository;
        this.incidentService=incidentService;
    }
    


    /**
     * Creates a complaint in the system
     * @param request The service request containg the complaint information
     * @return
     */
    public IncidentRequest create(IncidentRequest request){
        validator.validateCreate(request);
        enrichmentService.enrichCreateRequest(request);
        workflowService.updateWorkflowStatus(request);
        incidentService.save(request.getIncident());
        notificationService.process(request, null);
        return request;
    }


    /**
     * Searches the complaints in the system based on the given criteria
     * @param requestInfo The requestInfo of the search call
     * @param criteria The search criteria containg the params on which to search
     * @return
     */
    public List<IncidentWrapper> search(RequestInfo requestInfo, RequestSearchCriteria criteria){
        validator.validateSearch(requestInfo, criteria);

        enrichmentService.enrichSearchRequest(requestInfo, criteria);

        if(criteria.isEmpty())
            return new ArrayList<>();

        if(criteria.getMobileNumber()!=null && CollectionUtils.isEmpty(criteria.getUserIds()))
            return new ArrayList<>();

        criteria.setIsPlainSearch(false);

        List<IncidentWrapper> incidentWrappers = repository.getIncidentWrappers(criteria);

        if(CollectionUtils.isEmpty(incidentWrappers))
            return new ArrayList<>();;

         //to add later
        userService.enrichUsers(incidentWrappers);
        List<IncidentWrapper> enrichedServiceWrappers = workflowService.enrichWorkflow(requestInfo,incidentWrappers,criteria.getTenantId());
        Map<Long, List<IncidentWrapper>> sortedWrappers = new TreeMap<>(Collections.reverseOrder());
        for(IncidentWrapper svc : enrichedServiceWrappers){
            if(sortedWrappers.containsKey(svc.getIncident().getCreatedTime())){
                sortedWrappers.get(svc.getIncident().getCreatedTime()).add(svc);
            }else{
                List<IncidentWrapper> incidentWrapperList = new ArrayList<>();
                incidentWrapperList.add(svc);
                sortedWrappers.put(svc.getIncident().getCreatedTime(), incidentWrapperList);
            }
        }
        List<IncidentWrapper> sortedServiceWrappers = new ArrayList<>();
        for(Long createdTimeDesc : sortedWrappers.keySet()){
            sortedServiceWrappers.addAll(sortedWrappers.get(createdTimeDesc));
        }
        return sortedServiceWrappers;
    }


    /**
     * Updates the complaint (used to forward the complaint from one application status to another)
     * @param request The request containing the complaint to be updated
     * @return
     */
    public IncidentRequest update(IncidentRequest request){
       
        validator.validateUpdate(request);
        enrichmentService.enrichUpdateRequest(request);
        workflowService.updateWorkflowStatus(request);
        incidentService.save(request.getIncident());
        return request;
    }

    /**
     * Returns the total number of comaplaints matching the given criteria
     * @param requestInfo The requestInfo of the search call
     * @param criteria The search criteria containg the params for which count is required
     * @return
     */
    public Integer count(RequestInfo requestInfo, RequestSearchCriteria criteria){
        criteria.setIsPlainSearch(false);
        Integer count = repository.getCount(criteria);
        return count;
    }


    public List<IncidentWrapper> plainSearch(RequestInfo requestInfo, RequestSearchCriteria criteria) {
        validator.validatePlainSearch(criteria);

        criteria.setIsPlainSearch(true);

        if(criteria.getLimit()==null)
            criteria.setLimit(config.getDefaultLimit());

        if(criteria.getOffset()==null)
            criteria.setOffset(config.getDefaultOffset());

        if(criteria.getLimit()!=null && criteria.getLimit() > config.getMaxLimit())
            criteria.setLimit(config.getMaxLimit());

        List<IncidentWrapper> incidentWrappers = repository.getIncidentWrappers(criteria);

        if(CollectionUtils.isEmpty(incidentWrappers)){
            return new ArrayList<>();
        }

        userService.enrichUsers(incidentWrappers);
        List<IncidentWrapper> enrichedServiceWrappers = workflowService.enrichWorkflow(requestInfo, incidentWrappers,criteria.getTenantId());

        Map<Long, List<IncidentWrapper>> sortedWrappers = new TreeMap<>(Collections.reverseOrder());
//        for(IncidentWrapper svc : enrichedServiceWrappers){
//            if(sortedWrappers.containsKey(svc.getIncident().getAuditDetails().getCreatedTime())){
//                sortedWrappers.get(svc.getIncident().getAuditDetails().getCreatedTime()).add(svc);
//            }else{
//                List<IncidentWrapper> serviceWrapperList = new ArrayList<>();
//                serviceWrapperList.add(svc);
//                sortedWrappers.put(svc.getIncident().getAuditDetails().getCreatedTime(), serviceWrapperList);
//            }
//        }
        List<IncidentWrapper> sortedIncidentWrappers = new ArrayList<>();
        for(Long createdTimeDesc : sortedWrappers.keySet()){
        	sortedIncidentWrappers.addAll(sortedWrappers.get(createdTimeDesc));
        }
        return sortedIncidentWrappers;
    }


	public Map<String, Integer> getDynamicData(String tenantId) {
		
		Map<String,Integer> dynamicData = repository.fetchDynamicData(tenantId);

		return dynamicData;
	}


}
