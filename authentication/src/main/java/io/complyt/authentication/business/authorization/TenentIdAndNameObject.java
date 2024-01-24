package io.complyt.authentication.business.authorization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.aspectj.weaver.Iterators;

@AllArgsConstructor
@Getter
public class TenentIdAndNameObject {
    private String tenantId;
    private String name;
}