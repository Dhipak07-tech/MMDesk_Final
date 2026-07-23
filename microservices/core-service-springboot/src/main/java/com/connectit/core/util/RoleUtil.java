package com.connectit.core.util;

import java.util.ArrayList;
import java.util.List;

public class RoleUtil {
    public static final List<String> ROLE_HIERARCHY = List.of(
        "ultra_super_admin",
        "super_admin",
        "admin",
        "sub_admin",
        "agent",
        "user"
    );

    public static List<String> getViewableRoles(String currentUserRole) {
        if (currentUserRole == null) {
            return List.of();
        }
        String normalizedRole = currentUserRole.trim().toLowerCase();
        int index = ROLE_HIERARCHY.indexOf(normalizedRole);
        if (index == -1) {
            return List.of();
        }
        // Return everything strictly below the current role (index + 1 to end)
        return ROLE_HIERARCHY.subList(index + 1, ROLE_HIERARCHY.size());
    }
}
