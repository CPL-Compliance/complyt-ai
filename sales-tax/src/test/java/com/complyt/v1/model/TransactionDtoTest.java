package com.complyt.v1.model;

import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TransactionDtoTest {

    private TransactionDto transactionDto;
    private String externalId;
    private LocalDateTime localDateTime;
    private ObjectId customerId;

    @BeforeEach
    void setup() {
        externalId = UUID.randomUUID().toString();
        localDateTime = LocalDateTime.now();
        customerId = new ObjectId();
        transactionDto = createTransactionDto("1111");
    }

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "TransactionDto(id=1111, externalId=" + externalId +
                ", items=[ItemDto(unitPrice=2000.0, quantity=4, totalPrice=8000.0, description=description, name=name, taxCode=taxCode, jurisdictionalSalesTaxRules=null, salesTaxRate=SalesTaxRateDto(cityDistrictRate=0.5, cityRate=0.5, countyDistrictRate=0.5, countyRate=0.5, stateRate=0.5, taxRate=0.5), manualSalesTax=false, manualSalesTaxRate=0.0, tangibleCategory=INTANGIBLE, taxableCategory=NOT_TAXABLE)], billingAddress=AddressDto(city=City, country=Country, county=County, state=State, street=Street, zip=Zip), shippingAddress=AddressDto(city=City, country=Country, county=County, state=State, street=Street, zip=Zip), customerId=" + customerId +
                ", customer=null, salesTax=null, transactionStatus=ACTIVE, internalTimeStamps=TimeStampsDto(createdDate=" + localDateTime +
                ", updatedDate=" + localDateTime +
                "), externalTimeStamps=TimeStampsDto(createdDate=" + localDateTime +
                ", updatedDate=" + localDateTime +
                "), transactionType=INVOICE, shippingFee=ShippingFeeDto(manualSalesTax=false, manualSalesTaxRate=0.0, totalPrice=1000.0, jurisdictionalSalesTaxRules=JurisdictionalSalesTaxRules(name=California, abbreviation=CA, taxable=true, specialTreatment=false, calculationType=FIXED, description=description, calculationValue=0.0, cities=null), salesTaxRate=null, taxCode=C6S1, taxableCategory=TAXABLE, tangibleCategory=INTANGIBLE))";

        // When
        String actualString = transactionDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void withId_DifferentId_ReturnTransactionDto() {
        // Given
        TransactionDto expectedTransactionDto = createTransactionDto("2222");

        // When
        TransactionDto actualTransactionDto = transactionDto.withId("2222");

        // Then
        assertEquals(expectedTransactionDto,actualTransactionDto);
    }

    private TransactionDto createTransactionDto(String id) {
        AddressDto billingAddress = new AddressDto("City", "Country", "County", "State", "Street", "Zip");
        AddressDto shippingAddress = new AddressDto("City", "Country", "County", "State", "Street", "Zip");
        List<ItemDto> items = new ArrayList<ItemDto>() {
            {
                add(new ItemDto(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRateDto(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategoryDto.INTANGIBLE, TaxableCategoryDto.NOT_TAXABLE
                ));
            }
        };
        TimeStampsDto timeStamps = new TimeStampsDto(localDateTime, localDateTime);
        ShippingFeeDto shippingFeeDto = createShippingFeeDto();
        return new TransactionDto(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatusDto.ACTIVE, timeStamps, timeStamps, TransactionTypeDto.INVOICE, shippingFeeDto);
    }

    private ShippingFeeDto createShippingFeeDto() {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();
        return new ShippingFeeDto(false, 0, 1000, rules, null, "C6S1", TaxableCategoryDto.TAXABLE, TangibleCategoryDto.INTANGIBLE);
    }

    private JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", 0, null);
    }

}