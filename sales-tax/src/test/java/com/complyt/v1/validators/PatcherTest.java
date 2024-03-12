package com.complyt.v1.validators;

import com.complyt.v1.config.patch.CustomerPatcherFunctions;
import com.complyt.v1.exceptions.types.InvalidPatchFieldException;
import com.complyt.v1.models.TimestampsDto;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.CustomerTypeDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PatcherTest {
    Map<String, BiFunction<CustomerDto, Object, CustomerDto>> fieldsToFunctions;
    UnitTestUtilities testUtilities;
    CustomerDto objectToPatch;
    Patcher<CustomerDto> patcher;

    @BeforeEach
    void setUp() {
        fieldsToFunctions = new HashMap<>() {{
            put("name", CustomerPatcherFunctions.patchName);
            put("customerType", CustomerPatcherFunctions.patchCustomerType);
            put("externalTimestamps", CustomerPatcherFunctions.patchExternalTimestamps);
        }};
        patcher = new Patcher<>(fieldsToFunctions);
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        objectToPatch = testUtilities.createCustomerDto(UUID.randomUUID().toString());
    }

    @Test
    void patch_PatchesOnePrimitiveValue_ReturnsPatchedObject() {
        // Given
        String nameToPatch = "nameToPatch";
        Map<String, Object> map = new HashMap<>() {{
            put("name", nameToPatch);
        }};
        CustomerDto expectedObject = objectToPatch.withName(nameToPatch);

        // When
        CustomerDto actualObject = patcher.patch(objectToPatch, map);

        // Then
        Assertions.assertEquals(expectedObject, actualObject);
    }

    @Test
    void patch_PatchesOneEnumValue_ReturnsPatchedObject() {
        // Given
        CustomerTypeDto customerTypePatch = CustomerTypeDto.RETAIL_EXEMPT;
        Map<String, Object> map = new HashMap<>() {{
            put("customerType", customerTypePatch);
        }};
        CustomerDto expectedObject = objectToPatch.withCustomerType(customerTypePatch);

        // When
        CustomerDto actualObject = patcher.patch(objectToPatch, map);

        // Then
        Assertions.assertEquals(expectedObject, actualObject);
    }

    @Test
    void patch_PatchesOneObjectValue_ReturnsPatchedObject() {
        // Given
        String now = LocalDateTime.now().toString();
        LinkedHashMap<String, Object> externalTimestampsToPatch = new LinkedHashMap<>() {{
            put("createdDate", now);
            put("updatedDate", now);
        }};
        TimestampsDto timestampsDto = new TimestampsDto(now, now);
        Map<String, Object> map = new HashMap<>() {{
            put("externalTimestamps", externalTimestampsToPatch);
        }};
        CustomerDto expectedObject = objectToPatch.withExternalTimestamps(timestampsDto);

        // When
        CustomerDto actualObject = patcher.patch(objectToPatch, map);

        // Then
        Assertions.assertEquals(expectedObject, actualObject);
    }

    @Test
    void patch_PatchesTwoFields_ReturnsPatchedObject() {
        // Given
        String now = LocalDateTime.now().toString();
        CustomerTypeDto customerTypePatch = CustomerTypeDto.RETAIL_EXEMPT;
        LinkedHashMap<String, Object> externalTimestampsToPatch = new LinkedHashMap<>() {{
            put("createdDate", now);
            put("updatedDate", now);
        }};
        TimestampsDto timestampsDto = new TimestampsDto(now, now);
        Map<String, Object> map = new HashMap<>() {{
            put("externalTimestamps", externalTimestampsToPatch);
            put("customerType", customerTypePatch);
        }};
        CustomerDto expectedObject = objectToPatch
                .withExternalTimestamps(timestampsDto)
                .withCustomerType(customerTypePatch);

        // When
        CustomerDto actualObject = patcher.patch(objectToPatch, map);

        // Then
        Assertions.assertEquals(expectedObject, actualObject);
    }

    @Test
    void patch_PatchesThreeFields_ReturnsPatchedObject() {
        // Given
        String now = LocalDateTime.now().toString();
        CustomerTypeDto customerTypePatch = CustomerTypeDto.RETAIL_EXEMPT;
        LinkedHashMap<String, Object> externalTimestampsToPatch = new LinkedHashMap<>() {{
            put("createdDate", now);
            put("updatedDate", now);
        }};
        TimestampsDto timestampsDto = new TimestampsDto(now, now);
        String nameToPatch = "nameToPatch";
        Map<String, Object> map = new HashMap<>() {{
            put("externalTimestamps", externalTimestampsToPatch);
            put("customerType", customerTypePatch);
            put("name", nameToPatch);
        }};
        CustomerDto expectedObject = objectToPatch
                .withExternalTimestamps(timestampsDto)
                .withCustomerType(customerTypePatch)
                .withName(nameToPatch);

        // When
        CustomerDto actualObject = patcher.patch(objectToPatch, map);

        // Then
        Assertions.assertEquals(expectedObject, actualObject);
    }

    @Test
    void patch_FieldDoesNotExistInMap_ReturnsInvalidPatchFieldException() {
        // Given
        String name = "Wrong key";
        Map<String, Object> map = new HashMap<>() {{
            put("namee", name);
        }};

        // When
        InvalidPatchFieldException exception = assertThrows(InvalidPatchFieldException.class, () -> patcher.patch(objectToPatch, map));

        // Then
        assertEquals(InvalidPatchFieldException.class, exception.getClass());
    }

    @Test
    void patch_FieldIsOfWrongType_ReturnsInvalidPatchFieldException() {
        // Given
        Map<String, Object> map = new HashMap<>() {{
            put("name", 5);
        }};

        // When
        InvalidPatchFieldException exception = assertThrows(InvalidPatchFieldException.class, () -> patcher.patch(objectToPatch, map));

        // Then
        assertEquals(InvalidPatchFieldException.class, exception.getClass());
    }

    @Test
    void patch_NullObject_ReturnsNullPointerException() {
        // Given
        Map<String, Object> map = new HashMap<>() {{
            put("externalTimestamps", null);
            put("customerType", null);
        }};

        // When
        CustomerDto nullObject = null;
        Exception nullPointerException = assertThrows(NullPointerException.class, () -> patcher.patch(nullObject, map));

        // Then
        assertEquals("object is marked non-null but is null", nullPointerException.getMessage());
    }

    @Test
    void patch_NullMap_ReturnsNullPointerException() {
        // Given
        Map<String, Object> nullMap = null;

        // When
        CustomerDto nullObject = null;
        Exception nullPointerException = assertThrows(NullPointerException.class, () -> patcher.patch(objectToPatch, nullMap));

        // Then
        assertEquals("map is marked non-null but is null", nullPointerException.getMessage());
    }

}