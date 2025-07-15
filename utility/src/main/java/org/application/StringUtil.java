package org.application;

import java.util.List;
import java.util.stream.Collectors;

public class StringUtil {

    public static String join(List<?> ids) {

        if (ids == null || ids.size() == 0) {
            return "";
        }

        return ids
                .stream()
                .filter((id) -> {
                    return id != null;
                })
                .map(id -> id.toString())
                .collect(Collectors.joining(","));

    }

    public static Integer getInt(String value) {
        int digit = 0;
        try {
            digit = Integer.valueOf(value);
        } catch (Exception e) {

        } finally {
            return digit;
        }
    }

}
