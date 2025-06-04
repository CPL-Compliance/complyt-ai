package com.complyt.repositories.pagination;

import org.junit.jupiter.api.Test;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CriteriaBuilderTest {

    @Test
    void whenBuildingCriteria_andFiltersAreEmpty_shouldReturnNull() {
        Map<String, String> filterMap = new HashMap<>();
        Map<String, Boolean> filterKeys = new HashMap<>();

        Criteria result = CriteriaBuilder.build(filterMap, filterKeys);
        assertNull(result);
    }

    @Test
    void whenBuildingCriteria_andRegexValueFalse_shouldReturnCriteria() {
        Map<String, String> filterMap = Map.of("status", "ACTIVE");
        Map<String, Boolean> filterKeys = Map.of("status", false);

        Criteria result = CriteriaBuilder.build(filterMap, filterKeys);
        String criteria = result.getCriteriaObject().get("$and").toString();
        assertEquals("[Document{{status=ACTIVE}}]", criteria);
    }

    @Test
    void whenBuildingCriteria_andRegexValueTrue_shouldReturnCriteria() {
        Map<String, String> filterMap = Map.of("name", "john");
        Map<String, Boolean> filterKeys = Map.of("name", true);

        Criteria result = CriteriaBuilder.build(filterMap, filterKeys);
        assertNotNull(result);
        String criteria = result.getCriteriaObject().get("$and").toString();
        assertEquals("[Document{{name=john}}]", criteria);
    }

    @Test
    void whenBuildingCriteria_andIfFilterPropertyPartOfUUIDFiltersList_shouldReturnQueryWithUUID() {
        String uuidStr = UUID.randomUUID().toString();
        Map<String, String> filterMap = Map.of("customerId", uuidStr);
        Map<String, Boolean> filterKeys = Map.of("customerId", false);
        Criteria result = CriteriaBuilder.build(filterMap, filterKeys);
        assertNotNull(result);
        String uuidCriteria = result.getCriteriaObject().get("$and").toString();
        assertEquals("[Document{{customerId=" + uuidStr + "}}]", uuidCriteria);
    }

    @Test
    void whenBuildingCriteria_andIfFilterPropertyPartOfUUIDFiltersList_shouldReturnQueryWithUUID2() {
        String uuidStr = "randomStringThatIsNotAUUID";
        Map<String, String> filterMap = Map.of("customerId", uuidStr);
        Map<String, Boolean> filterKeys = Map.of("customerId", false);
        assertThrows(IllegalArgumentException.class, () -> CriteriaBuilder.build(filterMap, filterKeys));
    }

    @Test
    void whenBuildingCriteria_shouldIgnoreEmptyValues() {
        Map<String, String> filterMap = Map.of("status", "");
        Map<String, Boolean> filterKeys = Map.of("status", false);

        Criteria result = CriteriaBuilder.build(filterMap, filterKeys);
        assertNull(result);
    }
}
