package com.complyt.v1.config.patch;

import com.complyt.utils.object_mapper.ComplytObjectMapper;
import com.complyt.v1.models.EconomicNexusTrackerDto;
import com.complyt.v1.models.PhysicalNexusTrackerDto;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.StateDto;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.function.BiFunction;

public interface SalesTaxTrackingPatcherFunctions {

    BiFunction<SalesTaxTrackingDto, Object, SalesTaxTrackingDto> patchState = (salesTaxTrackingDto, state) -> {
        StateDto convertedStateDto = (StateDto) ComplytObjectMapper.mapObject(state, StateDto.class);
        return salesTaxTrackingDto.withState(convertedStateDto);
    };

    BiFunction<SalesTaxTrackingDto, Object, SalesTaxTrackingDto> patchEnforcesSalesTax = (salesTaxTrackingDto, enforcesSalesTax) ->{
        return salesTaxTrackingDto.withEnforcesSalesTax((Boolean) enforcesSalesTax);
    } ;

    BiFunction<SalesTaxTrackingDto, Object, SalesTaxTrackingDto> patchPhysicalNexusTracker = (salesTaxTrackingDto, physicalNexusTracker) -> {
        LinkedHashMap<String, Object> economicNexusTrackerDtoLinkedHashMap = (LinkedHashMap<String, Object>) physicalNexusTracker;
        boolean established = (boolean) economicNexusTrackerDtoLinkedHashMap.get("established");
        LocalDateTime establishedDate = LocalDateTime.parse(economicNexusTrackerDtoLinkedHashMap.get("establishedDate").toString());
        PhysicalNexusTrackerDto convertedPhysicalNexusTracker = new PhysicalNexusTrackerDto(established,establishedDate);

        return salesTaxTrackingDto.withPhysicalNexusTracker(convertedPhysicalNexusTracker);
    };

    BiFunction<SalesTaxTrackingDto, Object, SalesTaxTrackingDto> patchEconomicNexusTracker = (salesTaxTrackingDto, economicNexusTracker) -> {
        LinkedHashMap<String, Object> economicNexusTrackerDtoLinkedHashMap = (LinkedHashMap<String, Object>) economicNexusTracker;
        boolean established = (boolean) economicNexusTrackerDtoLinkedHashMap.get("established");
        LocalDateTime establishedDate = LocalDateTime.parse(economicNexusTrackerDtoLinkedHashMap.get("establishedDate").toString());
        EconomicNexusTrackerDto convertedEconomicNexusTracker = new EconomicNexusTrackerDto(established,establishedDate);

        return salesTaxTrackingDto.withEconomicNexusTracker(convertedEconomicNexusTracker);
    };

    BiFunction<SalesTaxTrackingDto, Object, SalesTaxTrackingDto> patchAppliedDate = (salesTaxTrackingDto, appliedDate) -> {
        LocalDateTime convertedAppliedDate = LocalDateTime.parse(appliedDate.toString());
        return salesTaxTrackingDto.withAppliedDate(convertedAppliedDate);
    };

    BiFunction<SalesTaxTrackingDto, Object, SalesTaxTrackingDto> patchApproved = (salesTaxTrackingDto, approved) -> {
        Boolean convertedApproved = (Boolean) ComplytObjectMapper.mapObject(approved, Boolean.class);
        return salesTaxTrackingDto.withApproved(convertedApproved);
    };

    BiFunction<SalesTaxTrackingDto, Object, SalesTaxTrackingDto> patchApprovalDate = (salesTaxTrackingDto, approvalDate) -> {
        LocalDateTime convertedApprovalDate = LocalDateTime.parse(approvalDate.toString());
        return salesTaxTrackingDto.withApprovalDate(convertedApprovalDate);
    };

}