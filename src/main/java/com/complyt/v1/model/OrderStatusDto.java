package com.complyt.v1.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum OrderStatusDto {
    ACTIVE,
    CANCELLED
}
//    private String status;
//
//    private OrderStatusDto(String status) {
//        this.status=status;
//    }
//
//    @JsonCreator
//    public static OrderStatusDto decode(final String status) {
//        return Stream.of(OrderStatusDto.values()).filter(targetEnum -> targetEnum.status.equals(status)).findFirst().orElse(null);
//    }
//
//    @JsonValue
//    public String getStatus() {
//        return status;
//    }
//}
