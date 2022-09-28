package com.example.YerevanCinema.enums;

public enum UserPermission {
    CREATE("session:create"),
    READ("session:read"),
    UPDATE("session:update"),
    DELETE("session:delete");

    private final String permission;

    UserPermission(String permission) {
        this.permission = permission;
    }

    public String getPermission(){
        return permission;
    }
}
