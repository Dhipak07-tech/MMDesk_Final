package com.connectit.core.util;

import org.springframework.jdbc.support.KeyHolder;
import java.util.Map;

public final class DbUtil {

    private DbUtil() {}

    public static long getGeneratedId(KeyHolder keyHolder) {
        if (keyHolder == null) {
            return 0L;
        }
        try {
            Number key = keyHolder.getKey();
            if (key != null) {
                return key.longValue();
            }
        } catch (org.springframework.dao.InvalidDataAccessApiUsageException e) {
            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null) {
                Object idVal = keys.get("id");
                if (idVal == null) idVal = keys.get("ID");
                if (idVal == null && !keys.isEmpty()) {
                    idVal = keys.values().iterator().next();
                }
                if (idVal instanceof Number) {
                    return ((Number) idVal).longValue();
                } else if (idVal != null) {
                    try {
                        return Long.parseLong(idVal.toString());
                    } catch (NumberFormatException nfe) {
                        // ignore
                    }
                }
            }
        }
        return 0L;
    }
}
