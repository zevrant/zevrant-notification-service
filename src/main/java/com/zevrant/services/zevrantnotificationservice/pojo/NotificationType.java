package com.zevrant.services.zevrantnotificationservice.pojo;

import java.util.*;
import java.util.stream.Collectors;

public enum NotificationType {
    SNS,
    EMAIL;

    NotificationType() {
    }

    private static List<String> listOf() {
        return Arrays.stream(NotificationType.values())
                .map(Enum::toString)
                .collect(Collectors.toList());
    }

    public static boolean isValidType(String type) {
        List<String> types = listOf();
        return !Objects.isNull(type)
            && listOf().contains(type.toUpperCase(Locale.ROOT));
    }
}
