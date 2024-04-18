package org.egov.im.util;

import java.util.Collections;
import java.util.List;

import org.egov.im.entity.BusinessService;
import org.egov.im.repository.BusinessServiceRepository;
import org.egov.im.web.models.workflow.BusinessServiceSearchCriteria;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
public class BusinessUtil {

    private BusinessServiceRepository businessServiceRepository;

    @Autowired
    public BusinessUtil(BusinessServiceRepository businessServiceRepository) {
        this.businessServiceRepository = businessServiceRepository;
    }

    
    /**
     * Searches for businessService for the given list of processStateAndActions
     * @param tenantId The tenantId of the BusinessService
     * @param businessService The businessService code of the businessService
     * @return BusinessService
     */
    public BusinessService getBusinessService(String tenantId,String businessService){
        BusinessServiceSearchCriteria criteria = new BusinessServiceSearchCriteria();
        criteria.setTenantId(tenantId);
        criteria.setBusinessServices(Collections.singletonList(businessService));
        List<BusinessService> businessServices = businessServiceRepository.getBusinessServices(criteria);
        if(CollectionUtils.isEmpty(businessServices))
            throw new CustomException("INVALID REQUEST","No BusinessService found for businessService: "+criteria.getBusinessServices());
        return businessServices.get(0);
    }

}
