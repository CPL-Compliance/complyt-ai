package com.complyt.business.sales_tax.checker;

import com.complyt.business.tax.sales_tax.checker.TaxableItemExistChecker;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.transaction.Item;
import com.complyt.security.TenantResolver;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TaxableItemExistCheckerTest {

    private UnitTestUtilities testUtilities;

    private TaxableItemExistChecker taxableItemExistenceCheck;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

    @BeforeEach
    void setUp() {
        testUtilities =  new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        taxableItemExistenceCheck = new TaxableItemExistChecker();
    }

    private Item createTaxableItem() {
        return testUtilities.createItemsWithSalesTaxRate(true, false, true)
                .get(0).withTaxableCategory(TaxableCategory.TAXABLE);
    } //note gst is null

    private Item createNotTaxableItem() {
        return testUtilities.createItemsWithSalesTaxRate(true, false, true)
                .get(0).withTaxableCategory(TaxableCategory.NOT_TAXABLE);
    } //note gst is null

    @Test
    void hasTaxableItem_ListHasTaxableItem_ReturnsTrue() {
        // Given
        Item taxableItem = createTaxableItem();
        Item secondItem = taxableItem.withTaxCode("C2S1").withTaxableCategory(TaxableCategory.NOT_TAXABLE);
        List<Item> items = new ArrayList<>() {{
            add(taxableItem);
            add(secondItem);
        }};

        // When + Then
        boolean hasTaxable = taxableItemExistenceCheck.check(items);
        assertTrue(hasTaxable);
    }

    @Test
    void hasTaxableItem_ListDoesNotHaveTaxableItem_ReturnsFalse() {
        // Given
        Item notTaxableItem = createNotTaxableItem();
        List<Item> items = new ArrayList<>() {{
            add(notTaxableItem);
        }};

        // When + Then
        boolean hasTaxable = taxableItemExistenceCheck.check(items);
        assertFalse(hasTaxable);
    }

    @Test
    void hasTaxableItem_NullItemsListPassed_ThrowsException() {
        // Given
        List<Item> nullItems = null;

        // When
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            taxableItemExistenceCheck.check(nullItems);
        });

        // Then
        assertEquals(nullPointerException.getMessage(), "items is marked non-null but is null");
    }
}
