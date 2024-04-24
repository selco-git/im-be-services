package org.egov.im.repository.querybuilder;

import org.springframework.stereotype.Component;

@Component
public class AddressQueryBuilder {

    public static final String GET_ADDRESSBY_IDAND_TENANT = "select * from eg_user_address where userid=:userId and tenantid=:tenantId";

    public static final String DELETE_ADDRESS = "delete from eg_user_address where id IN (:id) ";

}
