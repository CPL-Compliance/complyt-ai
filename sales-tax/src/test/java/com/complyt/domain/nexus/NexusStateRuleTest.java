package com.complyt.domain.nexus;

import com.complyt.domain.State;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class NexusStateRuleTest {

    private NexusStateRule nexusStateRule;

    private String id;

    @BeforeEach
    void setup() {
        id = UUID.randomUUID().toString();
        nexusStateRule = createNexusStateRule();
    }

    private NexusStateRule createNexusStateRule() {
        State state = new State("CA", "02", "California");
        List<TaxableCategory> taxableCategories = new ArrayList<>() {{
            add(TaxableCategory.TAXABLE);
        }};

        List<TangibleCategory> tangibleCategories = new ArrayList<>() {{
            add(TangibleCategory.TANGIBLE);
        }};

        List<CustomerType> customerTypes = new ArrayList<>() {{
            add(CustomerType.RETAIL);
        }};

        NexusThreshold nexusThreshold = new NexusThreshold(new BigDecimal(1000), 2, Definition.AMOUNT_OR_COUNT);

        return new NexusStateRule(id, true, state, taxableCategories, tangibleCategories, customerTypes,
                TimeFrame.CURRENT_CALENDER_YEAR, nexusThreshold, null);
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "NexusStateRule[id=" + nexusStateRule.id() +
                ", enforcesSalesTax=" + nexusStateRule.enforcesSalesTax() +
                ", state=" + nexusStateRule.state() +
                ", taxableCategories=" + nexusStateRule.taxableCategories() +
                ", tangibleCategories=" + nexusStateRule.tangibleCategories() +
                ", customerTypes=" + nexusStateRule.customerTypes() +
                ", timeFrame=" + nexusStateRule.timeFrame() +
                ", nexusThreshold=" + nexusStateRule.nexusThreshold() +
                ", appliedDate=" + nexusStateRule.appliedDate() + "]";

        // When
        String actualString = nexusStateRule.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void Equals_SameNexusStateRule_ReturnTrue() {
        // Given
        NexusStateRule givenNexusStateRule = createNexusStateRule();

        // When
        boolean isEquals = nexusStateRule.equals(givenNexusStateRule);

        // Then
        assertTrue(isEquals);
    }

}