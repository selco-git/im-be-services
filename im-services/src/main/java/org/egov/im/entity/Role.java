package org.egov.im.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name="eg_role")
public class Role {

    private static final String CITIZEN = "CITIZEN";

    @JsonProperty("name")
    @Column(name="name")
    private String name;
    
    @JsonProperty("code")
    @Column(name="code")
    private String code;
    
    @JsonProperty("id")
    @Id
    @Column(name="id")
    private Long id;
    
    @JsonProperty("tenantId")
    @Column(name="tenantid")
    private String tenantId;
    
    
    @JsonProperty("version")
    @Column(name="version")
    private String version;
    
    @Column(name="description")
    @JsonProperty("description")
    private String description;
    
    @Column(name="createdby")
    @JsonProperty("createdby")
    private Long createdBy;
    
    @Column(name="createddate")
    @JsonProperty("createddate")
    private Date createdDate;
    
    @Column(name="lastmodifiedby")
    @JsonProperty("lastmodifiedby")
    private Long lastModifiedBy;
    
    @Column(name="lastmodifieddate")
    @JsonProperty("lastmodifieddate")
    private Date lastModifiedDate;

    public static Role getCitizenRole() {
        return Role.builder().code(CITIZEN).build();
    }
}
