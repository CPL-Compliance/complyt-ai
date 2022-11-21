package com.complyt.v1.mappers;

import com.complyt.domain.*;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.v1.model.*;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class TransactionMapperTest {

    private Transaction transaction;
    private Transaction transactionNoTenant;
    private TransactionDto transactionDto;
    private String externalId;
    private LocalDateTime localDateTime;
    private String tenantId;
    private ObjectId customerId;

    @BeforeEach
    void setup() {
        externalId = UUID.randomUUID().toString();
        localDateTime = LocalDateTime.now();
        tenantId = UUID.randomUUID().toString();
        customerId = new ObjectId();

        transaction = createTransaction(tenantId);
        transactionNoTenant = createTransaction(null);
        transactionDto = createTransactionDto();
    }

    @Test
    void transactionToTransactionDto_Transaction_gotTransactionDto() {

        // Given
        Transaction exampleTransaction = transaction;
        // When
        TransactionDto transactionDtoResult = TransactionMapper.INSTANCE.transactionToTransactionDto(exampleTransaction);
        // Then
        assertEquals(transactionDto, transactionDtoResult);
    }

    @Test
    void transactionDtoToTransaction_TransactionDto_gotTransaction() {

        // Given
        TransactionDto exampleTransactionDto = transactionDto;
        // When
        Transaction transactionResult = TransactionMapper.INSTANCE.transactionDtoToTransaction(exampleTransactionDto);
        // Then
        assertEquals(transactionNoTenant, transactionResult);
    }

    private Transaction createTransaction(String tenantId) {
        String id = null;
        Address billingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "State", "Street", "Zip");
        List<Item> items = new ArrayList<Item>() {
            {
                add(new Item(2000, 4, 8000, "description", "name", "taxCode",
                        null, new SalesTaxRate(0.5f, 0.5f, 0.5f, 0.5f, 0.5f, 0.5f), false, 0, TangibleCategory.INTANGIBLE, TaxableCategory.NOT_TAXABLE
                ));
            }
        };
        TimeStamps timeStamps = new TimeStamps(localDateTime, localDateTime);
        ShippingFee shippingFee = createShippingFee();
        return new Transaction(id, externalId, items, billingAddress, shippingAddress, customerId, null, null, TransactionStatus.ACTIVE, tenantId, timeStamps, timeStamps, TransactionType.INVOICE, shippingFee);
    }

    private TransactionDto createTransactionDto() {
        String id = null;
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
    private ShippingFee createShippingFee() {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, 0, 1000, rules, null, "C6S1", TaxableCategory.TAXABLE, TangibleCategory.INTANGIBLE);
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
