package com.viamatica.backend.util;

import java.util.Arrays;
import java.util.List;

public enum Role {

    USER(Arrays.asList(Permission.READ_ALL_USERS)),
    ADMINISTRATOR(Arrays.asList(Permission.SAVE_ONE_USER, Permission.MODIFY_ONE_USER, Permission.DELETE_ONE_USER, Permission.READ_ALL_USERS));

    private List<Permission> permissions;

    Role(List<Permission> permissions) {
        this.permissions = permissions;
    }

    public List<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(List<Permission> permissions) {
        this.permissions = permissions;
    }
}
