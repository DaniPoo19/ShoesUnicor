package com.mycompany.shoesunicor.model;
/**
 * Roles de usuario en el sistema
 * @author Victor Negrete
 */
public enum UserRole {
    USER("Usuario"),
    ADMIN("Administrador");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
