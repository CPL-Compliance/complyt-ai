package com.complyt.v1.config.patch;

import com.complyt.utils.object_mapper.ComplytObjectMapper;
import com.complyt.v1.models.EconomicNexusTrackerDto;
import com.complyt.v1.models.PhysicalNexusTrackerDto;
import com.complyt.v1.models.SalesTaxTrackingDto;
import com.complyt.v1.models.StateDto;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

public interface SalesTaxTrackingPatcher {

    BiFunction<SalesTaxTrackingDto, Object, SalesTaxTrackingDto> patchState = (salesTaxTrackingDto, state) -> {
        StateDto convertedState = (StateDto) ComplytObjectMapper.mapObject(state, StateDto.class);
        return salesTaxTrackingDto.withState(convertedState);
    };

    BiFunction<SalesTaxTrackingDto, Object, SalesTaxTrackingDto> patchEnforcesSalesTax = (salesTaxTrackingDto, enforcesSalesTax) -> {
        Boolean convertedEnforcesSalesTax = (Boolean) ComplytObjectMapper.mapObject(enforcesSalesTax, Boolean.class);
        return salesTaxTrackingDto.withEnforcesSalesTax(convertedEnforcesSalesTax);
    };

    BiFunction<SalesTaxTrackingDto, Object, SalesTaxTrackingDto> patchPhysicalNexusTracker = (salesTaxTrackingDto, physicalNexusTracker) -> {
        PhysicalNexusTrackerDto convertedPhysicalNexusTracker = (PhysicalNexusTrackerDto) ComplytObjectMapper.mapObject(physicalNexusTracker, PhysicalNexusTrackerDto.class);
        return salesTaxTrackingDto.withPhysicalNexusTracker(convertedPhysicalNexusTracker);
    };

    BiFunction<SalesTaxTrackingDto, Object, SalesTaxTrackingDto> patchEconomicNexusTracker = (salesTaxTrackingDto, economicNexusTracker) -> {
        EconomicNexusTrackerDto convertedEconomicNexusTracker = (EconomicNexusTrackerDto) ComplytObjectMapper.mapObject(economicNexusTracker, EconomicNexusTrackerDto.class);
        return salesTaxTrackingDto.withEconomicNexusTracker(convertedEconomicNexusTracker);
    };

    BiFunction<SalesTaxTrackingDto, Object, SalesTaxTrackingDto> patchAppliedDate = (salesTaxTrackingDto, appliedDate) -> {
        LocalDateTime convertedAppliedDate = (LocalDateTime) ComplytObjectMapper.mapObject(appliedDate, LocalDateTime.class);
        return salesTaxTrackingDto.withAppliedDate(convertedAppliedDate);
    };

    BiFunction<SalesTaxTrackingDto, Object, SalesTaxTrackingDto> patchApproved = (salesTaxTrackingDto, approved) -> {
        Boolean convertedApproved = (Boolean) ComplytObjectMapper.mapObject(approved, Boolean.class);
        return salesTaxTrackingDto.withApproved(convertedApproved);
    };

    BiFunction<SalesTaxTrackingDto, Object, SalesTaxTrackingDto> patchApprovalDate = (salesTaxTrackingDto, approvalDate) -> {
        LocalDateTime convertedApprovalDate = (LocalDateTime) ComplytObjectMapper.mapObject(approvalDate, LocalDateTime.class);
        return salesTaxTrackingDto.withApprovalDate(convertedApprovalDate);
    };

}
