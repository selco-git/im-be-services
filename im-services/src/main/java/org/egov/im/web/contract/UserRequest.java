package org.egov.im.web.contract;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.Email;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.egov.im.config.UserServiceConstants;
import org.egov.im.domain.model.enums.AddressType;
import org.egov.im.domain.model.enums.BloodGroup;
import org.egov.im.domain.model.enums.Gender;
import org.egov.im.domain.model.enums.GuardianRelation;
import org.egov.im.entity.Address;
import org.egov.im.entity.Role;
import org.egov.im.entity.User;
import org.hibernate.validator.constraints.SafeHtml;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {

    private Long id;

    @SafeHtml
    @Size(max = 64)
    private String userName;

    @SafeHtml
    @Size(max = 5)
    private String salutation;

    @Pattern(regexp = UserServiceConstants.PATTERN_NAME)
    @Size(max = 50)
    private String name;

    @Pattern(regexp = UserServiceConstants.PATTERN_GENDER)
    @Size(max = 15)
    private String gender;

    @Pattern(regexp = UserServiceConstants.PATTERN_MOBILE)
    private String mobileNumber;
    
    @Pattern(regexp = UserServiceConstants.PATTERN_MOBILE)
    private String alternatemobilenumber;

    @Email
    @Size(max = 128)
    private String emailId;

    @SafeHtml
    @Size(max = 50)
    private String altContactNumber;

    @SafeHtml
    @Size(max = 10)
    private String pan;

    @SafeHtml
    @Size(max = 20)
    private String aadhaarNumber;

    @SafeHtml
    @Size(max = 300)
    private String permanentAddress;

    @SafeHtml
    @Pattern(regexp = UserServiceConstants.PATTERN_CITY)
    @Size(max = 50)
    private String permanentCity;

    @SafeHtml
    @Pattern(regexp = UserServiceConstants.PATTERN_PINCODE)
    @Size(max = 10)
    private String permanentPinCode;

    @SafeHtml
    @Size(max = 300)
    private String correspondenceAddress;

    @Pattern(regexp = UserServiceConstants.PATTERN_CITY)
    @Size(max = 50)
    private String correspondenceCity;

    @Pattern(regexp = UserServiceConstants.PATTERN_PINCODE)
    @Size(max = 10)
    private String correspondencePinCode;
    private Boolean active;

    @SafeHtml
    @Size(max = 16)
    private String locale;

    private String type;
    private Boolean accountLocked;
    private Long accountLockedDate;

    @Pattern(regexp = UserServiceConstants.PATTERN_NAME)
    @Size(max = 50)
    private String fatherOrHusbandName;
    private GuardianRelation relationship;

    @SafeHtml
    @Size(max = 36)
    private String signature;

    @SafeHtml
    @Size(max = 32)
    private String bloodGroup;

    @SafeHtml
    @Size(max = 36)
    private String photo;

    @SafeHtml
    @Size(max = 300)
    private String identificationMark;
    private Long createdBy;

    @Size(max = 64)
    private String password;

    @SafeHtml
    private String otpReference;
    private Long lastModifiedBy;

    @Pattern(regexp = UserServiceConstants.PATTERN_TENANT)
    @Size(max = 50)
    private String tenantId;

    private List<RoleRequest> roles;

    @SafeHtml
    @Size(max = 36)
    private String uuid;


    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date createdDate;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date lastModifiedDate;
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date dob;
    @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    private Date pwdExpiryDate;

    public UserRequest(User user) {

        this.id = user.getId();
        this.userName = user.getUsername();
        this.salutation = user.getSalutation();
        this.name = user.getName();
        this.gender = user.getGender() != null ? user.getGender().toString() : null;
        this.mobileNumber = user.getMobileNumber();
        this.emailId = user.getEmailId();
        this.altContactNumber = user.getAltContactNumber();
        this.pan = user.getPan();
        this.aadhaarNumber = user.getAadhaarNumber();
        this.active = user.getActive();
        this.dob = user.getDob();
        this.pwdExpiryDate = user.getPasswordExpiryDate();
        this.locale = user.getLocale();
        this.type = user.getType();
        this.accountLocked = user.getAccountLocked();
        this.accountLockedDate = user.getAccountLockedDate();
        this.signature = user.getSignature();
        this.bloodGroup = user.getBloodGroup() != null ? user.getBloodGroup() : null;
        this.photo = user.getPhoto();
        this.identificationMark = user.getIdentificationMark();
        this.createdBy = user.getCreatedBy();
        this.createdDate = user.getCreatedDate();
        this.lastModifiedBy = user.getLastModifiedBy();
        this.lastModifiedDate = user.getLastModifiedDate();
        this.tenantId = user.getTenantId();
        this.roles = convertDomainRoleToContract(user.getRoles());
        this.fatherOrHusbandName = user.getGuardian();
        this.relationship = user.getGuardianRelation();
        this.uuid = user.getUuid();
        this.alternatemobilenumber=user.getAlternateMobileNumber();
        mapPermanentAddress(user);
        mapCorrespondenceAddress(user);
    }

    private void mapCorrespondenceAddress(User user) {
        if (user.getCorrespondenceAddress() != null) {
            this.correspondenceAddress = user.getCorrespondenceAddress().getAddress();
            this.correspondenceCity = user.getCorrespondenceAddress().getCity();
            this.correspondencePinCode = user.getCorrespondenceAddress().getPinCode();
        }
    }

    private void mapPermanentAddress(User user) {
        if (user.getPermanentAddress() != null) {
            this.permanentAddress = user.getPermanentAddress().getAddress();
            this.permanentCity = user.getPermanentAddress().getCity();
            this.permanentPinCode = user.getPermanentAddress().getPinCode();
        }
    }

    private List<RoleRequest> convertDomainRoleToContract(List<Role> roleEntities) {
        if (roleEntities == null) return new ArrayList<>();
        return roleEntities.stream().map(RoleRequest::new).collect(Collectors.toList());
    }

    @JsonIgnore
    public User toDomain(Long loggedInUserId, String loggedInUserUuid, boolean isCreate) {
        BloodGroup bloodGroup = null;
        try {
            if (this.bloodGroup != null)
                bloodGroup = BloodGroup.valueOf(this.bloodGroup.toUpperCase());
        } catch (Exception e) {
            bloodGroup = BloodGroup.fromValue(this.bloodGroup);
        }
        return User.builder()
                .uuid(this.uuid)
                .id(this.id)
                .name(this.name)
                .username(this.userName)
                .salutation(this.salutation)
                .mobileNumber(this.mobileNumber)
                .emailId(this.emailId)
                .altContactNumber(this.altContactNumber)
                .pan(this.pan)
                .aadhaarNumber(this.aadhaarNumber)
                .active(isActive(isCreate))
                .dob(this.dob)
                .passwordExpiryDate(this.pwdExpiryDate)
                .locale(this.locale)
                .type(this.type)
                .accountLocked(isAccountLocked(isCreate))
                .accountLockedDate(this.accountLockedDate)
                .signature(this.signature)
                .photo(this.photo)
                .identificationMark(this.identificationMark)
                .gender(this.gender != null ? Gender.valueOf(this.gender.toUpperCase()) : null)
                .bloodGroup(bloodGroup.toString())
                .lastModifiedDate(new Date())
                .createdDate(new Date())
                .otpReference(this.otpReference)
                .tenantId(this.tenantId)
                .password(this.password)
                .roles(toDomainRoles())
                .loggedInUserId(loggedInUserId)
                .loggedInUserUuid(loggedInUserUuid)
                .permanentAddress(toDomainPermanentAddress())
                .correspondenceAddress(toDomainCorrespondenceAddress())
                .guardian(fatherOrHusbandName)
                .guardianRelation(relationship).alternateMobileNumber(this.alternatemobilenumber)
                .build();
    }

    private Boolean isActive(boolean isCreate) {
        if (this.active == null && isCreate) {
            return false;
        }
        return this.active;
    }

    private Boolean isAccountLocked(boolean isCreate) {
        if (this.accountLocked == null && isCreate) {
            return false;
        }
        return this.accountLocked;
    }

    private Address toDomainPermanentAddress() {
        return Address.builder()
                .type(AddressType.PERMANENT)
                .city(permanentCity)
                .pinCode(permanentPinCode)
                .address(permanentAddress)
                .build();
    }

    private Address toDomainCorrespondenceAddress() {
        return Address.builder()
                .type(AddressType.CORRESPONDENCE)
                .city(correspondenceCity)
                .pinCode(correspondencePinCode)
                .address(correspondenceAddress)
                .build();
    }

    private List<Role> toDomainRoles() {
        return this.roles != null
                ? this.roles.stream()
                .map(RoleRequest::toDomain)
                .distinct()
                .collect(Collectors.toList())
                : null;
    }
}
