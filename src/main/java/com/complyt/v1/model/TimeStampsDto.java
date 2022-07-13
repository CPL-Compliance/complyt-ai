package com.complyt.v1.model;

import lombok.*;

import java.util.Date;


@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
public class TimeStampsDto {
    private Date createdDate;
    private Date updatedDate;
}
