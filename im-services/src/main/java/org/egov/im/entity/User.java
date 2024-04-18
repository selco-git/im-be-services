package org.egov.im.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="eg_user")
public class User {

	@Id
	@Column(name="id")
    private Long id;
	
	@Column(name="username")
    private String userName;
	
	@Column(name="name")
    private String name;
	
	@Column(name="emptype")
    private String emptype;
	
	@Column(name="mobilenumber")
    private String mobileNumber;
	
	@Column(name="emailid")
    private String emailId;
	
	@Transient
    private List<Role> roles;
    
	@Column(name="uuid")
    private String uuid;
	
	@Column(name="active")
    private Boolean active;
	
	@Column(name="tenantid")
    private String tenantId;



}
