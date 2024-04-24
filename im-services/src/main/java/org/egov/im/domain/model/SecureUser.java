package org.egov.im.domain.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.egov.im.entity.Role;
import org.egov.im.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class SecureUser implements UserDetails {
    private static final long serialVersionUID = -8756608845278722035L;
    private final User user;
    private final List<SimpleGrantedAuthority> authorities = new ArrayList<>();

    public SecureUser(User user) {
        if (user == null) {
            throw new UsernameNotFoundException("UserRequest not found");
        } else {
            this.user = user;
            user.getRoles().forEach(role -> this.authorities.add(new SimpleGrantedAuthority(role.getCode())));
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getActive();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.user.getActive();
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return this.user.getUsername();
    }

    public User getUser() {
        return this.user;
    }

    @JsonIgnore
    public List<String> getRoleCodes() {
        return user.getRoles()
                .stream()
                .map(Role::getCode)
                .collect(Collectors.toList());
    }

    public String getTenantId() {
        return user.getTenantId();
    }
}