package dev.cadebe.spring6restmvc.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityRoles {
    public static final String ADMIN = "ADMIN";
    public static final String ACTUATOR = "ACTUATOR";
    public static final String USER = "USER";
}
