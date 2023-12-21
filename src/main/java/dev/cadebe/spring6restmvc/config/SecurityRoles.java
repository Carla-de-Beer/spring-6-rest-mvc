package dev.cadebe.spring6restmvc.config;

public enum SecurityRoles {
    ADMIN("ADMIN"),
    ACTUATOR("ACTUATOR"),
    USER("USER");

    private final String roleName;

    SecurityRoles(String roleName) {
        this.roleName = roleName;
    }
}
