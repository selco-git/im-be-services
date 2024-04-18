package org.egov.im.entity;

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

    @JsonProperty("name")
    @Column(name="name")
    private String name;
    
    @JsonProperty("code")
    @Id
    @Column(name="code")
    private String code;
    
    @JsonProperty("tenantId")
    @Column(name="tenantId")
    private String tenantId;
}
