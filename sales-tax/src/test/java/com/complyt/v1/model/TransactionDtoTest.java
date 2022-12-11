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
    private String transactionId;

    @BeforeEach
    void setup() {
        externalId = UUID.randomUUID().toString();
        localDateTime = LocalDateTime.now();
        customerId = new ObjectId();
        transactionId = UUID.randomUUID().toString();
        transactionDto = createTransactionDto(transactionId);
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

    @Test
    void toString_ReturnString() {
        // Given
        String expectedString = "TransactionDto(id=" + transactionDto.getId() +
                ", externalId=" + transactionDto.getExternalId() +
                ", items=" + transactionDto.getItems() +
                ", billingAddress=" + transactionDto.getBillingAddress() +
                ", shippingAddress=" + transactionDto.getShippingAddress() +
                ", customerId=" + transactionDto.getCustomerId() +
                ", customer=null, salesTax=null, transactionStatus=" + transactionDto.getTransactionStatus() +
                ", internalTimeStamps=" + transactionDto.getInternalTimeStamps() +
                ", externalTimeStamps=" + transactionDto.getExternalTimeStamps() +
                ", transactionType=INVOICE, shippingFee=" + transactionDto.getShippingFee() + ")";

        // When
        String actualString = transactionDto.toString();

        // Then
        assertEquals(expectedString, actualString);
    }

    @Test
    void withId_DifferentId_ReturnTransactionDto() {
        // Given
        String differentId = UUID.randomUUID().toString();
        TransactionDto expectedTransactionDto = createTransactionDto(differentId);

        // When
        TransactionDto actualTransactionDto = transactionDto.withId(differentId);

        // Then
        assertEquals(expectedTransactionDto, actualTransactionDto);
    }

}