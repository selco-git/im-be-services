package org.egov.im.repository;


import java.util.ArrayList;
import java.util.List;

import org.egov.im.config.IMConfiguration;
import org.egov.im.service.IdGenerationService;
import org.egov.im.web.models.RequestInfo;
import org.egov.im.web.models.Idgen.IdGenerationRequest;
import org.egov.im.web.models.Idgen.IdGenerationResponse;
import org.egov.im.web.models.Idgen.IdRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class IdGenRepository {



    private RestTemplate restTemplate;

    private IMConfiguration config;
    
    @Autowired
	private IdGenerationService idGenerationService;

    @Autowired
    public IdGenRepository(RestTemplate restTemplate, IMConfiguration config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }


    /**
     * Call iDgen to generateIds
     * @param requestInfo The rquestInfo of the request
     * @param tenantId The tenantiD of the service request
     * @param name Name of the foramt
     * @param format Format of the ids
     * @param count Total Number of idGen ids required
     * @return
     * @throws Exception 
     */
    public IdGenerationResponse getId(RequestInfo requestInfo, String tenantId, String name, String format, int count) throws Exception {

        List<IdRequest> reqList = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            reqList.add(IdRequest.builder().idName(name).format(format).build());
        }
        IdGenerationRequest req = IdGenerationRequest.builder().idRequests(reqList).requestInfo(requestInfo).build();
        IdGenerationResponse response = idGenerationService.generateIdResponse(req);
        return response;
    }



}
