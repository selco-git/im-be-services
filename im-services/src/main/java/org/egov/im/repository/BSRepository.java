package org.egov.im.repository;

import org.egov.im.entity.BusinessService;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BSRepository extends CrudRepository<BusinessService, String>{


}
