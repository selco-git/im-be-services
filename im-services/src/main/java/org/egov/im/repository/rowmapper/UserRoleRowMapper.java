package org.egov.im.repository.rowmapper;

import org.egov.im.entity.Role;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class UserRoleRowMapper implements RowMapper<Role> {

    @Override
    public Role mapRow(final ResultSet rs, final int rowNum) throws SQLException {
        return Role.builder().tenantId(rs.getString("roleidtenantid")).build();
    }
}