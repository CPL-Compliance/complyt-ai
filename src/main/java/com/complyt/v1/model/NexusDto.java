package com.complyt.v1.model;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@ApiModel("Nexus")
public class NexusDto {
    private String type;
    private List<NexusRuleDto> rules;
}
