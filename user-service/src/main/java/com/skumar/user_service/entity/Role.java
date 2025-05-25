package com.skumar.user_service.entity;

import java.util.Set;

public enum Role {
    USER(Set.of(
        Authority.USER_READ,
        Authority.USER_UPDATE
    )),
    ADMIN(Set.of(
        Authority.USER_READ,
        Authority.USER_CREATE,
        Authority.USER_UPDATE,
        Authority.USER_DELETE,
        Authority.ADMIN_READ,
        Authority.ADMIN_CREATE,
        Authority.ADMIN_UPDATE,
        Authority.ADMIN_DELETE
    ));

    private final Set<Authority> authorities;

    Role(Set<Authority> authorities) {
        this.authorities = authorities;
    }

    public Set<Authority> getAuthorities() {
        return authorities;
    }
}