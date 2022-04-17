package com.complyt.v1.model;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@ApiModel("NexusRule")
public class NexusRuleDto {
    private String type;
    private int value;
}
