package org.egov.im.web.contract.auth;

import java.util.List;

import org.egov.im.domain.model.SecureUser;
import org.egov.im.domain.model.UserDetail;
import org.egov.im.entity.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CustomUserDetails {
    private Long id;
    private String userName;
    private String name;
    private String mobileNumber;
    private String emailId;
    private String locale;
    private String type;
    private List<Role> roles;
    private boolean active;
    private List<Action> actions;
    private String tenantId;
    private String uuid;

    public CustomUserDetails(UserDetail userDetail) {
        final SecureUser secureUser = userDetail.getSecureUser();
        this.id = secureUser.getUser().getId();
        this.userName = secureUser.getUser().getUsername();
        this.name = secureUser.getUser().getName();
        this.mobileNumber = secureUser.getUser().getMobileNumber();
        this.emailId = secureUser.getUser().getEmailId();
        this.locale = secureUser.getUser().getLocale();
        this.type = secureUser.getUser().getType();
        this.roles = secureUser.getUser().getRoles();
        this.active = secureUser.getUser().getActive();
        this.tenantId = secureUser.getUser().getTenantId();
        this.uuid = secureUser.getUser().getUuid();
//		this.actions = userDetail.getActions().stream().map(Action::new).collect(Collectors.toList());
    }
}

