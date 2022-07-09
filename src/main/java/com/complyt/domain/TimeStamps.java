package com.complyt.domain;

import lombok.*;

import java.util.Date;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@With
public class TimeStamps {
    private Date createdDate;
    private Date updatedDate;
}
