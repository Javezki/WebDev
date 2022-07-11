package com.lftech.sqlapi.pojo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.lftech.sqlapi.Utils.JsonUtils;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@RequiredArgsConstructor
@Slf4j

public class User implements UserDetails, CredentialsContainer {

    private static final long serialVersionUID = 1540301443318450196L;
    @Id
    private Long id;
    private transient String username;
    private transient String password;
    private transient String roles;
    private transient boolean enabled;

    @Transient
    private transient Long sessionId;

    @Override
    public void eraseCredentials() {
        password = null;
    }

    public List<String> getRoles() {
        try {
            return Arrays.asList(JsonUtils.objectMapper.readValue(roles, String[].class));
        } catch (JsonProcessingException e) {
            log.error("getRoles", e);
        }
        return null;
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return getRoles().stream()
                .map(s -> new SimpleGrantedAuthority(s))
                .collect(Collectors.toList());
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}

