package org.egov.im.service;

import java.util.Collections;
import java.util.List;

import org.egov.im.config.WorkflowConfig;
import org.egov.im.entity.BusinessService;
import org.egov.im.entity.State;
import org.egov.im.producer.Producer;
import org.egov.im.repository.BSRepository;
import org.egov.im.repository.BusinessServiceRepository;
import org.egov.im.web.models.workflow.BusinessServiceRequest;
import org.egov.im.web.models.workflow.BusinessServiceSearchCriteria;
import org.egov.im.web.models.workflow.ProcessInstanceSearchCriteria;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class BusinessMasterService {

    private Producer producer;

    private WorkflowConfig config;

    private EnrichmentService enrichmentService;

    private BusinessServiceRepository repository;
    
    private BSRepository bSrepository;


    private CacheManager cacheManager;

  
    
    @Autowired
    public BusinessMasterService(Producer producer, WorkflowConfig config, EnrichmentService enrichmentService,
                                 BusinessServiceRepository repository, CacheManager cacheManager,BSRepository bsRepository) {
        this.producer = producer;
        this.config = config;
        this.enrichmentService = enrichmentService;
        this.repository = repository;
        this.cacheManager = cacheManager;
        this.bSrepository=bsRepository;
    }

    public BusinessService save(BusinessService businessService) {
  		return bSrepository.save(businessService);
  	}


    /**
     * Enriches and sends the request on kafka to persist
     * @param request The BusinessServiceRequest to be persisted
     * @return The enriched object which is persisted
     */
    public List<BusinessService> create(BusinessServiceRequest request){
        enrichmentService.enrichCreateBusinessService(request);
        for(BusinessService bs:request.getBusinessServices())
        {
        	save(bs);        		
        		
        }	
        return request.getBusinessServices();
    }

    /**
     * Fetches business service object from db
     * @param criteria The search criteria
     * @return Data fetched from db
     */
    @Cacheable(value = "businessService")
    public List<BusinessService> search(BusinessServiceSearchCriteria criteria){
        String tenantId = criteria.getTenantId();
        List<BusinessService> businessServices = repository.getBusinessServices(criteria);
        //enrichmentService.enrichTenantIdForStateLevel(tenantId,businessServices);

        return businessServices;
    }



    public List<BusinessService> update(BusinessServiceRequest request){
        evictAllCacheValues("businessService");
        evictAllCacheValues("roleTenantAndStatusesMapping");
        enrichmentService.enrichUpdateBusinessService(request);
        producer.push(config.getUpdateBusinessServiceTopic(),request);
        return request.getBusinessServices();
    }


    private void evictAllCacheValues(String cacheName) {
        cacheManager.getCache(cacheName).clear();
    }
    
    public Long getMaxBusinessServiceSla(ProcessInstanceSearchCriteria criteria) {
        BusinessServiceSearchCriteria searchCriteria = new BusinessServiceSearchCriteria();
        searchCriteria.setBusinessServices(Collections.singletonList(criteria.getBusinessService()));
        List<BusinessService> businessServices = repository.getBusinessServices(searchCriteria);
        //enrichmentService.enrichTenantIdForStateLevel(tenantId,businessServices);

        Long maxSla = businessServices.get(0).getBusinessServiceSla();
        return maxSla;
    }



}
