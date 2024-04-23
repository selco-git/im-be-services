package org.egov.im.persistence.repository;

import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.egov.im.repository.querybuilder.RoleQueryBuilder;
import org.egov.im.repository.rowmapper.RoleRowMapper;
import org.egov.im.repository.rowmapper.UserRoleRowMapper;

import org.egov.tracer.model.CustomException;
import org.egov.im.entity.Role;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
@Setter
public class RoleRepository {

    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;



    public RoleRepository(NamedParameterJdbcTemplate namedParameterJdbcTemplate, RestTemplate restTemplate,
                          ObjectMapper objectMapper) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    /**
     * Get UserRoles By UserId And TenantId
     *
     * @param userId
     * @param tenantId
     * @return
     */
    public List<Role> getUserRoles(final long userId, final String tenantId) {

        final Map<String, Object> parametersMap = new HashMap<String, Object>();
        parametersMap.put("userId", userId);
        parametersMap.put("tenantId", tenantId);
        List<Role> roleList = namedParameterJdbcTemplate.query(RoleQueryBuilder.GET_ROLES_BY_ID_TENANTID, parametersMap,
                new UserRoleRowMapper());
        List<Long> roleIdList = new ArrayList<Long>();
        String tenantid = null;
        if (!roleList.isEmpty()) {
            for (Role role : roleList) {
                tenantid = role.getTenantId();
            }
        }
        List<Role> roles = new ArrayList<Role>();
        if (!roleIdList.isEmpty()) {

            final Map<String, Object> Map = new HashMap<String, Object>();
            Map.put("id", roleIdList);
            Map.put("tenantId", tenantid);

            roles = namedParameterJdbcTemplate.query(RoleQueryBuilder.GET_ROLES_BY_ROLEIDS, Map, new RoleRowMapper());
        }

        return roles;
    }

    /**
     * Get Role By role code and tenantId
     *
     * @param tenantId
     * @param code
     * @return
     */
    public Role findByTenantIdAndCode(String tenantId, String code) {

        final Map<String, Object> parametersMap = new HashMap<String, Object>();
        parametersMap.put("code", code);
        parametersMap.put("tenantId", tenantId);
        Role role = null;
        List<Role> roleList = namedParameterJdbcTemplate
                .query(RoleQueryBuilder.GET_ROLE_BYTENANT_ANDCODE, parametersMap, new RoleRowMapper());

        if (!roleList.isEmpty()) {
            role = roleList.get(0);
        }
        return role;
    }

    List<Role> findRolesByCode(Set<String> roles, String tenantId) {
    	 List<Role> roless = new ArrayList<Role>();
    	 
         if (!roles.isEmpty()) {

             final Map<String, Object> Map = new HashMap<String, Object>();
             Map.put("code", roles);
             Map.put("tenantId", tenantId);

            roless = namedParameterJdbcTemplate.query(RoleQueryBuilder.GET_ROLES_BY_CODE, Map, new RoleRowMapper());
        }

        return roless;

    }

   
}
