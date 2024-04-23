package org.egov.im.web.security.oauth2.custom.authproviders;

import static java.util.Objects.isNull;
import static org.springframework.util.StringUtils.isEmpty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.egov.im.domain.exception.DuplicateUserNameException;
import org.egov.im.domain.exception.UserNotFoundException;
import org.egov.im.domain.model.SecureUser;
import org.egov.im.domain.model.enums.UserType;
import org.egov.im.domain.service.UserService;
import org.egov.im.entity.Role;
import org.egov.im.entity.User;
import org.egov.im.web.models.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class CustomPreAuthenticatedProvider implements AuthenticationProvider {

    @Autowired
    private UserService userService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        UsernamePasswordAuthenticationToken token = (UsernamePasswordAuthenticationToken) authentication.getPrincipal();

        SecureUser secureUser = (SecureUser) token.getPrincipal();
        String userName = secureUser.getUsername();

        final LinkedHashMap<String, String> details = (LinkedHashMap<String, String>) token.getDetails();

        String tenantId = details.get("tenantId");
        String userType = details.get("userType");

        if (isEmpty(tenantId)) {
            throw new OAuth2Exception("TenantId is mandatory");
        }
        if (isEmpty(userType) || isNull(UserType.fromValue(userType))) {
            throw new OAuth2Exception("User Type is mandatory and has to be a valid type");
        }

        User user;
        try {
            user = userService.getUniqueUser(userName, tenantId, UserType.fromValue(userType));
            /* decrypt here */
            List<Role> domain_roles = user.getRoles();
          
            User userInfo = User.builder().uuid(user.getUuid())
                    .type(user.getType() != null ? user.getType() : null).roles(domain_roles).build();
            RequestInfo requestInfo = RequestInfo.builder().userInfo(userInfo).build();
        } catch (UserNotFoundException e) {
            log.error("User not found", e);
            throw new OAuth2Exception("Invalid login credentials");
        } catch (DuplicateUserNameException e) {
            log.error("Fatal error, user conflict, more than one user found", e);
            throw new OAuth2Exception("Invalid login credentials");

        }

        if (user.getAccountLocked() == null || user.getAccountLocked()) {
            throw new OAuth2Exception("Account locked");
        }

        List<GrantedAuthority> grantedAuths = new ArrayList<>();
        grantedAuths.add(new SimpleGrantedAuthority("ROLE_" + user.getType()));
        final SecureUser finalUser = new SecureUser(user);
        return new PreAuthenticatedAuthenticationToken(finalUser,
                null, grantedAuths);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }


//    private org.egov.user.web.contract.auth.User getUser(User user) {
//        org.egov.user.web.contract.auth.User authUser =  org.egov.user.web.contract.auth.User.builder().id(user.getId()).userName(user.getUsername()).uuid(user.getUuid())
//                .name(user.getName()).mobileNumber(user.getMobileNumber()).emailId(user.getEmailId())
//                .locale(user.getLocale()).active(user.getActive()).type(user.getType().name())
//                .roles(toAuthRole(user.getRoles())).tenantId(user.getTenantId())
//                .build();
//
//        if(user.getPermanentAddress()!=null)
//            authUser.setPermanentCity(user.getPermanentAddress().getCity());
//
//        return authUser;
//    }

//    private Set<Role> toAuthRole(Set<org.egov.user.domain.model.Role> domainRoles) {
//        if (domainRoles == null)
//            return new HashSet<>();
//        return domainRoles.stream().map(org.egov.user.web.contract.auth.Role::new).collect(Collectors.toSet());
//    }
}
