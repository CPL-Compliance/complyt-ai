package testUtils.integration_test;

import com.complyt.domain.currency.CurrencySource;
import com.complyt.v1.models.*;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.CustomerTypeDto;
import com.complyt.v1.models.customer.exemption.*;
import com.complyt.v1.models.matched_address.ScoringDto;
import com.complyt.v1.models.matched_address.enums.FieldMatchType;
import com.complyt.v1.models.matched_address.enums.FieldsMatchScore;
import com.complyt.v1.models.matched_address.enums.MatchLevelType;
import com.complyt.v1.models.nexus.*;
import com.complyt.v1.models.tax.sales_tax.RatesMetaDataDto;
import com.complyt.v1.models.tax.sales_tax.SalesTaxRatesDto;
import com.complyt.v1.models.transaction.*;
import com.complyt.v1.models.vat_validation.ValidatedVatDto;
import com.complyt.v1.models.vat_validation.VatDetailsToValidateDto;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface ITUtilities {

    static void checkErrorMessages(LinkedHashMap map, Set<String> expectedErrors) {
        String message = (String) map.get("message");
        String[] errors = message.split(", ");
        assertEquals(expectedErrors.size(), errors.length);
        for (String err : errors) {
            assertTrue(expectedErrors.contains(err));
        }
    }

    String NON_EXISTING_COMPLYT_ID = "d18068f0-6d98-4b0d-ba19-4536f0b4173a";

    String localDateTime = "2024-01-01T00:00:00.00";

    // if no items provided, puts a default stub
    static TransactionDto stubTransactionDto(String externalId, UUID customerId, ItemDto... items) {
        return new TransactionDto(null, externalId, "1", "INVUS1000",
                List.of(items.length < 1 ? new ItemDto[]{stubItemDto()} : items),
                false, null, new ShippingAddressDto("Acampo", "US", null, "CA", "1525 R Jahant Rd", "", "95220", false, null),
                customerId, null, null, TransactionStatusDto.ACTIVE, null, new TimestampsDto(localDateTime, localDateTime),
                TransactionTypeDto.INVOICE, null, null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, TransactionFilingStatusDto.NOT_FILED,
                "USD", null, null, null, false, null, null, null);
    } // note isTaxInclusive is false, finalTransactionAmount is zero

    static TransactionDto stubTransactionDtoWithThreeItems(String externalId, UUID customerId, ItemDto... items) {
        return new TransactionDto(null, externalId, "1", "INVUS1000",
                List.of(items.length < 1 ? new ItemDto[]{stubItemDtoWithDiscount(BigDecimal.valueOf(500), BigDecimal.ZERO), stubItemDtoWithDiscount(BigDecimal.valueOf(1000), BigDecimal.ZERO), stubItemDtoWithDiscount(BigDecimal.valueOf(10000), BigDecimal.ZERO)} : items),
                false, null, new ShippingAddressDto("Acampo", "US", null, "CA", "1525 R Jahant Rd", "", "95220", false, null),
                customerId, null, null, TransactionStatusDto.ACTIVE, null, new TimestampsDto(LocalDateTime.now().toString(), LocalDateTime.now().toString()),
                TransactionTypeDto.INVOICE, null, null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, TransactionFilingStatusDto.NOT_FILED,
                "USD", null, null, null, false, null, null, null);
    } // note isTaxInclusive is false, finalTransactionAmount is zero

    static TransactionDto stubTransactionDtoNonUsaCountry(String externalId, UUID customerId, ItemDto... items) {
        ShippingAddressDto shippingAddress = new ShippingAddressDto(null, "Canada", null, null, "", "Quebec", "12345", false, null);
        return stubTransactionDto(externalId, customerId, items)
                .withShippingAddress(shippingAddress);
    }

    static CustomerDto stubCustomerDto(String externalId) {
        return new CustomerDto(null, externalId, "1",
                "stub customer", null, "captaindope@gg.com", CustomerTypeDto.RETAIL,
                null, new TimestampsDto(localDateTime, localDateTime), "comment", null);
    }

    static SalesTaxTrackingDto stubSalesTaxTrackingDto(String country, StateDto state) {
        return new SalesTaxTrackingDto(null, country, state, "comment", true,
                new PhysicalNexusTrackerDto(false, LocalDateTime.now()),
                new EconomicNexusTrackerDto(false, LocalDateTime.now()),
                null, null, stubClientTrackingDto(),
                LocalDateTime.now(), false, LocalDateTime.now(), FilingFrequencyDto.MONTHLY, null, null, null, null);
    }

    static SalesTaxTrackingDto stubSalesTaxTrackingNonUsaDto(String country) {
        return stubSalesTaxTrackingDto(country, null);
    }

    static ItemDto stubItemDto() {
        return new ItemDto(new BigDecimal(10000), new BigDecimal(1), new BigDecimal(10000),
                null, "some description", "Hardware", "C1S1",
                null, null, null, false, BigDecimal.ZERO, null,
                null, null, null);
    }

    static ItemDto stubItemDtoWithDiscount(BigDecimal price, BigDecimal discount) {
        return new ItemDto(price, new BigDecimal(1), price,
                price, "some description", "Hardware", "C1S1",
                null, null, null, false, BigDecimal.ZERO, discount,
                null, null, null);
    }

    static ShippingFeeDto stubShippingFeeDto() {
        return new ShippingFeeDto(false, BigDecimal.ZERO, BigDecimal.valueOf(500), null,
                null, null, null, "C6S1", null, null);
    }

    static Jwt.Builder stubJwt() {
        return Jwt.withTokenValue("token")
                .header("typ", "JWT")
                .issuer("https://localhost")
                .claim("tenant_id", "it_tenant")
                .claim("scope", "create:customer delete:customer read:customer " +
                        "update:customer create:transaction read:transaction " +
                        "update:transaction delete:transaction read:state " +
                        "create:exemption update:exemption delete:exemption " +
                        "read:exemption create:nexus read:nexus delete:nexus update:nexus read:link");
    }

    static OptionalAddressDto createOptionalAddressDtoInCalifornia() {
        return new OptionalAddressDto("Fresno", "US", null, "CA", "7498 N Remington Ave", null, "93711-5508", false);
    }

    static SalesTaxRatesDto createSalesTaxRatesDto() {
        return new SalesTaxRatesDto(
                new BigDecimal("0.06"), // state
                new BigDecimal("0.0125"), // county
                new BigDecimal("0"), // city
                new BigDecimal("0.005"), // combinedDistrictRate
                new RatesMetaDataDto(
                        new BigDecimal("0"), // cityDistrictRate
                        new BigDecimal("0.005"), // countyDistrictRate
                        BigDecimal.ZERO
                ),
                null, // mtaRate
                null, // spdRate
                null, // otherRate
                new BigDecimal("0.0775") // taxRate
        );
    }

    static SalesTaxRatesDto createSalesTaxRatesDto(BigDecimal taxRate) {
        return new SalesTaxRatesDto(new BigDecimal("0.0"), new BigDecimal("0.0"), new BigDecimal("0.0"),null, null, null, null, taxRate, new BigDecimal("0.0"));
    }

    static ScoringDto createScoringDto() {
        return new ScoringDto(MatchLevelType.EXCELLENT, 1, new FieldsMatchScore(FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT));
    }
    static ClientTrackingDto stubClientTrackingDto() {
        String localDate = "2024-01-01T00:00";
        TimestampsDto internalTimestamps = new TimestampsDto(localDate, localDate);
        return new ClientTrackingDto(new NexusDto(LocalDateTime.parse("2015-06-01T00:00")), "it_tenant", internalTimestamps, null);
    }

    static ClientTrackingDtoTenant stubClientTrackingDtoTenant(String tenantId, String name) {
        TimestampsDto internalTimestamps = new TimestampsDto(LocalDateTime.now().toString(), LocalDateTime.now().toString());
        return new ClientTrackingDtoTenant(new NexusDto(LocalDateTime.parse("2015-06-01T00:00")), name, internalTimestamps, tenantId, null);
    }

    static NexusStateRuleDto stubAlabamaNexusStateRuleDto() {
        return new NexusStateRuleDto(true, "USA",
                new StateDto("AL", "01", "Alabama"),
                List.of(TaxableCategoryDto.TAXABLE, TaxableCategoryDto.NOT_TAXABLE),
                List.of(TangibleCategoryDto.INTANGIBLE, TangibleCategoryDto.TANGIBLE),
                List.of(CustomerTypeDto.RETAIL),
                TimeFrameDto.PREVIOUS_TWELVE_MONTHS,
                new NexusThresholdDto(BigDecimal.valueOf(250000), 0, DefinitionDto.AMOUNT),
                LocalDateTime.of(1970, 1, 1, 0, 0, 0));
    }

    static NexusStateRuleDto stubMichiganNexusStateRuleDto() {
        return new NexusStateRuleDto(true, "USA",
                new StateDto("MI", "26", "Michigan"),
                List.of(TaxableCategoryDto.TAXABLE, TaxableCategoryDto.NOT_TAXABLE),
                List.of(TangibleCategoryDto.INTANGIBLE, TangibleCategoryDto.TANGIBLE),
                List.of(CustomerTypeDto.RETAIL, CustomerTypeDto.MARKETPLACE, CustomerTypeDto.RESELLER),
                TimeFrameDto.CURRENT_CALENDER_YEAR,
                new NexusThresholdDto(BigDecimal.valueOf(100000), 200, DefinitionDto.AMOUNT_OR_COUNT),
                LocalDateTime.of(1970, 1, 1, 0, 0, 0));
    }

    static NexusStateRuleDto stubBrazilNexusStateRuleDto() {
        return stubAlabamaNexusStateRuleDto()
                .withState(null)
                .withCountry("Brazil")
                .withAppliedDate(LocalDateTime.of(2022, 1, 1, 0, 0, 0));
    }

    static ClassificationDto createClassificationDto() {
        return new ClassificationDto("new code", "new description");
    }

    static Document clientTrackingDocument() {
        return new Document("_id", new ObjectId("62c5672583479c0adfa2c4a5"))
                .append("nexus", new Document("taxableDate", LocalDateTime.now()))
                .append("tenantId", "other_it_tenant")
                .append("name", "Bestclient TM")
                .append("internalTimestamps", new Document("createdDate", LocalDateTime.now())
                        .append("updatedDate", LocalDateTime.now()))
                .append("subsidiaries", List.of("1"));
    }

    static Document customerDocument() {
        return new Document("_id", new ObjectId("f2c5672583479c0adfa2c4a4"))
                .append("externalId", "1586")
                .append("name", "Bestcompany Com")
                .append("address", new Document("city", "Phoenix")
                        .append("country", "US")
                        .append("state", "AZ")
                        .append("street", "3400 E Sky Harbor Blvd")
                        .append("zip", "85034")
                        .append("isPartial", false))
                .append("_class", "com.complyt.domain.Customer")
                .append("customerType", "RETAIL")
                .append("tenantId", "it_tenant")
                .append("externalTimestamps", new Document("createdDate", LocalDateTime.now())
                        .append("updatedDate", LocalDateTime.now()))
                .append("internalTimestamps", new Document("createdDate", LocalDateTime.now())
                        .append("updatedDate", LocalDateTime.now()))
                .append("complytId", UUID.randomUUID())
                .append("source", "1")
                .append("email", "captain@dope.com")
                .append("customerStatus", "ACTIVE");
    }

    static Document exemptionDocument() {
        return new Document("_id", new ObjectId("6372009a6887300e852749e4"))
                .append("customerId", UUID.randomUUID())
                .append("state", new Document("abbreviation", "AZ")
                        .append("code", "04")
                        .append("name", "Arizona"))
                .append("classification", new Document("code", "code")
                        .append("description", "description"))
                .append("validationDates", new Document("fromDate", LocalDateTime.now())
                        .append("toDate", LocalDateTime.now()))
                .append("status", new Document("code", "code")
                        .append("name", "name"))
                .append("certificate", new Document("certificateId", "id")
                        .append("url", "url")
                        .append("name", "name"))
                .append("exemptionType", "FULLY")
                .append("tenantId", "it_tenant")
                .append("complytId", UUID.randomUUID())
                .append("country", "USA")
                .append("exemptionStatus", "ACTIVE")
                .append("internalTimestamps", new Document("createdDate", LocalDateTime.now())
                        .append("updatedDate", LocalDateTime.now()));
    }

    static Document salesTaxTrackingDocument() {
        return new Document("_id", new ObjectId("642415ce6632165b050b01ac"))
                .append("complytId", UUID.randomUUID())
                .append("country", "")
                .append("state", new Document("abbreviation", "NY")
                        .append("code", "36")
                        .append("name", "New York"))
                .append("tenantId", "it_tenant")
                .append("enforcesSalesTax", true)
                .append("physicalNexusTracker", new Document("established", false)
                        .append("establishedDate", LocalDateTime.now()))
                .append("economicNexusTracker", new Document("established", false)
                        .append("establishedDate", LocalDateTime.now()))
                .append("appliedDate", LocalDateTime.now())
                .append("approved", false)
                .append("approvalDate", LocalDateTime.now())
                .append("_class", "com.complyt.domain.nexus.SalesTaxTracking")
                .append("transactionNexusSummaries", new Document())
                .append("nexusCalculationSummaries", new Document())
                .append("clientTracking", new Document("_id", new ObjectId("649d6ecf86de4c7ec0ca6128"))
                        .append("tenantId", "it_tenant")
                        .append("nexus", new Document("taxableDate", LocalDateTime.now()))
                        .append("name", "it_tenant")
                        .append("internalTimestamps", new Document("createdDate", LocalDateTime.now())
                                .append("updatedDate", LocalDateTime.now())))
                .append("nexusStateRule", new Document("enforcesSalesTax", true)
                        .append("country", "USA")
                        .append("taxableCategories", List.of("TAXABLE"))
                        .append("tangibleCategories", List.of("TANGIBLE"))
                        .append("customerTypes", List.of("RETAIL"))
                        .append("timeFrame", "CURRENT_CALENDER_YEAR")
                        .append("appliedDate", LocalDateTime.now())
                        .append("state", new Document("abbreviation", "GA")
                                .append("code", "13")
                                .append("name", "Georgia"))
                        .append("nexusThreshold", new Document("amount", "")
                                .append("count", 0)
                                .append("definition", "AMOUNT_AND_COUNT")));
    }

    static Document transactionDocument() {
        return new Document("_class", "com.complyt.domain.transaction.Transaction")
                .append("_id", new ObjectId("65b6a7f8f930555db9c7c246"))
                .append("subsidiary", "Some subsidiary")
                .append("tenantId", "org_AHOc5X21mIdGLgrd")
                .append("internalTimestamps", new Document("createdDate", LocalDateTime.now())
                        .append("updatedDate", LocalDateTime.now()))
                .append("taxableItemsAmount", "43750")
                .append("transactionType", "INVOICE")
                .append("source", "6")
                .append("complytId", UUID.randomUUID())
                .append("billingAddress", new Document("street", "123 Main St")
                        .append("zip", "12345")
                        .append("country", "USA")
                        .append("county", "SomeCounty")
                        .append("state", "CA")
                        .append("city", "SomeCity"))
                .append("externalId", "2c9d525b-e809-4916-831e-c50d08a22d92")
                .append("customerId", UUID.randomUUID())
                .append("documentName", "INV-0151")
                .append("totalItemsAmount", "43750")
                .append("tangibleItemsAmount", "43750")
                .append("totalDiscount", "0")
                .append("finalTransactionAmount", "")
                .append("transactionFilingStatus", "NOT_FILED")
                .append("currency", "ILS")
                .append("transactionStatus", "ACTIVE")
                .append("externalTimestamps", new Document("createdDate", LocalDateTime.now())
                        .append("updatedDate", LocalDateTime.now()))
                .append("items", List.of(transactionItemDocument()))
                .append("shippingAddress", new Document("country", "MOLDOVA")
                        .append("city", "")
                        .append("county", "")
                        .append("state", "")
                        .append("street", "")
                        .append("zip", "")
                        .append("isPartial", false)
                        .append("matchedAddressData", new Document("address", new Document("city", "New York")
                                .append("country", "USA")
                                .append("countryCode", "USA")
                                .append("county", "New York County")
                                .append("state", "New York")
                                .append("street", "5th Ave")
                                .append("zip", "10001")
                                .append("isPartial", false))
                                .append("scoring", new Document("matchLevel", "GOOD")
                                        .append("score", 0.8)
                                        .append("fieldScore", new Document("countryMatch", "EXACT")
                                                .append("stateMatch", "EXACT")
                                                .append("cityMatch", "EXACT")
                                                .append("zipMatch", "EXACT")
                                                .append("streetMatch", "PARTIAL")
                                        )
                                )

                        ))
                .append("salesTax", new Document("amount", "0")
                        .append("complytId", UUID.randomUUID())
                        .append("salesTaxRates", new Document("taxRate", "0.0825")
                                .append("cityRate", "0.01")
                                .append("combinedDistrictRate", "0.015")
                                .append("countyRate", "0.005")
                                .append("stateRate", "0.0525")
                                .append("mtaRate", "0")
                                .append("spdRate", "0")
                                .append("otherRate", "0")
                                .append("ratesMetaData", new Document("cityDistrictRate", "0.005")
                                        .append("countyDistrictRate", "0.01")
                                        .append("specialDistrictRate", "0.01")
                                ))
                        .append("gtRates", new Document("taxRate", "0.05")
                                .append("countryRate", "0.02")
                                .append("regionRate", "0.03"))
                        .append("rate", "0.0825"))
                .append("shippingFee", shippingFeeDocument()
                        .append("taxCode", "SHIPPING")
                        .append("taxableCategory", "TAXABLE")
                        .append("tangibleCategory", "TANGIBLE")
                        .append("calculatedTotal", "100"));
    }

    private static Document shippingFeeDocument() {
        return new Document("manualSalesTax", true)
                .append("manualSalesTaxRate", "0")
                .append("totalPrice", "100")
                .append("jurisdictionalSalesTaxRules", new Document("name", "Texas")
                        .append("abbreviation", "TX")
                        .append("taxable", true)
                        .append("specialTreatment", false)
                        .append("calculationType", "PERCENTAGE")
                        .append("description", "Shipping fee tax rules")
                        .append("calculationValue", "0.8")
                        .append("cities", new Document("city1", new Document("name", "city")
                                .append("abbreviation", "R1")
                                .append("taxable", true)
                                .append("specialTreatment", false)
                                .append("calculationType", "FIXED")
                                .append("description", "region description")
                                .append("calculationValue", "0.5"))))
                .append("jurisdictionalTaxRules", new Document("name", "Texas")
                        .append("abbreviation", "TX")
                        .append("taxable", true)
                        .append("specialTreatment", false)
                        .append("calculationType", "PERCENTAGE")
                        .append("description", "Shipping fee jurisdictional tax rules")
                        .append("calculationValue", "0.8")
                        .append("regions", new Document("region1", new Document("name", "Region1")
                                .append("abbreviation", "R1")
                                .append("taxable", true)
                                .append("specialTreatment", false)
                                .append("calculationType", "FIXED")
                                .append("description", "Region description")
                                .append("calculationValue", "0.5"))))
                .append("salesTaxRates", new Document("taxRate", "0.0825")
                        .append("cityRate", "0.01")
                        .append("combinedDistrictRate", "0.015")
                        .append("countyRate", "0.005")
                        .append("stateRate", "0.0525")
                        .append("ratesMetaData", new Document("cityDistrictRate", "0.005")
                                .append("countyDistrictRate", "0.01")))
                .append("gtRates", new Document("taxRate", "0.05")
                        .append("countryRate", "0.02")
                        .append("regionRate", "0.03"));
    }

    static Document transactionItemDocument() {
        return new Document("name", "e7ec72ea-0b5b-45b8-b2d8-c8f6ab3e4603")
                .append("taxCode", "")
                .append("tangibleCategory", "TANGIBLE")
                .append("taxableCategory", "TAXABLE")
                .append("manualSalesTax", true)
                .append("manualSalesTaxRate", "0")
                .append("unitPrice", "12500")
                .append("quantity", "1")
                .append("totalPrice", "12500")
                .append("description", "Quarterly Activation Fees")
                .append("calculatedTotal", "12500")
                .append("jurisdictionalSalesTaxRules", new Document("name", "Texas")
                        .append("abbreviation", "NN")
                        .append("taxable", true)
                        .append("specialTreatment", false)
                        .append("calculationType", "PERCENTAGE")
                        .append("description", "description3")
                        .append("calculationValue", "0.8")
                        .append("cities", new Document("city1", new Document("name", "city")
                                .append("abbreviation", "R1")
                                .append("taxable", true)
                                .append("specialTreatment", false)
                                .append("calculationType", "FIXED")
                                .append("description", "region description")
                                .append("calculationValue", "0.5"))))
                .append("salesTaxRates", new Document("taxRate", "0.0825")
                        .append("cityRate", "0.01")
                        .append("combinedDistrictRate", "0.015")
                        .append("countyRate", "0.005")
                        .append("stateRate", "0.0525")
                        .append("ratesMetaData", new Document("cityDistrictRate", "0.005")
                                .append("countyDistrictRate", "0.01")))
                .append("gtRates", new Document("taxRate", "0.05")
                        .append("countryRate", "0.02")
                        .append("regionRate", "0.03"))
                .append("jurisdictionalTaxRules", new Document("name", "Texas")
                        .append("abbreviation", "TX")
                        .append("taxable", true)
                        .append("specialTreatment", false)
                        .append("calculationType", "PERCENTAGE")
                        .append("description", "description")
                        .append("calculationValue", "0.8")
                        .append("regions", new Document("region1", new Document("name", "Region1")
                                .append("abbreviation", "R1")
                                .append("taxable", true)
                                .append("specialTreatment", false)
                                .append("calculationType", "FIXED")
                                .append("description", "region description")
                                .append("calculationValue", "0.5"))));
    }

    static Document nexusStateRuleDocument() {
        return new Document("_id", new ObjectId("62de9f8c2a864b00112ec128"))
                .append("state", new Document("abbreviation", "state")
                        .append("name", "name")
                        .append("code", "11"))
                .append("taxableCategories", List.of("TAXABLE", "NOT_TAXABLE"))
                .append("customerTypes", List.of("RETAIL", "MARKETPLACE", "RESELLER"))
                .append("timeFrame", "CURRENT_CALENDER_YEAR")
                .append("nexusThreshold", new Document("amount", "100000")
                        .append("count", 200)
                        .append("definition", "AMOUNT_AND_COUNT"))
                .append("tangibleCategories", List.of("TANGIBLE"))
                .append("enforcesSalesTax", true)
                .append("appliedDate", LocalDateTime.now())
                .append("country", "USA");
    }

    static Document productClassificationDocument() {
        return new Document("_id", new ObjectId("66474705627c74a48bd6ba0f"))
                .append("taxCode", "R1S1")
                .append("description", "Royalties")
                .append("title", "Royalties")
                .append("jurisdictionalSalesTaxRules", new Document("CA", new Document()
                        .append("name", "California")
                        .append("abbreviation", "CA")
                        .append("taxable", false)
                        .append("specialTreatment", false)
                        .append("calculationType", "FIXED")
                        .append("description", "description3")
                        .append("calculationValue", "0")))
                .append("tangibleCategory", "INTANGIBLE")
                .append("jurisdictionalTaxRules", new Document());
    }

    static ExchangeRateInfoDto createExchangeRateInfoDto(BigDecimal totalItemsAmountInUsd, BigDecimal transactionSalesTaxInUsd, BigDecimal finalTransactionAmountInUsd, String fromCurrency, String toCurrency, BigDecimal fxRate, CurrencySource source, Boolean isExchangeRateEstimated, LocalDateTime exchangeRateDate) {
        return new ExchangeRateInfoDto(totalItemsAmountInUsd, transactionSalesTaxInUsd, finalTransactionAmountInUsd, fromCurrency, toCurrency, fxRate, source, isExchangeRateEstimated, exchangeRateDate);
    }

    static ShippingAddressDto createAddressDtoInKensas() {
        return new ShippingAddressDto("Kensas City", "USA", null, "KS", "101st Main st", null, "66106", false,null);
    }

    static ExemptionDto createExemptionDto() {
        String country = "USA";
        StateDto state = new StateDto("CA", "02", "California");
        ClassificationDto classification = new ClassificationDto("code", "description");
        ValidationDatesDto validationDates = new ValidationDatesDto(
                LocalDateTime.parse(localDateTime).minusYears(1).toString(),
                LocalDateTime.parse(localDateTime).plusYears(1).toString());
        TimestampsDto internalTimestamps = new TimestampsDto(localDateTime, localDateTime);
        StatusDto status = new StatusDto("code", "name");
        CertificateDto certificate = new CertificateDto(UUID.randomUUID().toString(), "url", "name");

        return new ExemptionDto(UUID.randomUUID(), UUID.fromString("e10cd4a2-6a4e-4621-bdad-4860bfa91ecb"),
                country, state, classification, validationDates, internalTimestamps, status, certificate, ExemptionTypeDto.FULLY, ExemptionStatusDto.ACTIVE, null);
    }

    static ValidatedVatDto createValidatedVatDto() {
        ValidatedVatDto validatedVatDto = new ValidatedVatDto("BE", "Belgium", "0835221567",
                true, "BV BE³-PROJECTS", "Kasteeldreef 9\\n2940 Stabroek", new TimestampsDto(LocalDateTime.now().toString(), LocalDateTime.now().toString())); //todo: put a const time

        return validatedVatDto;
    }

    static ValidatedVatDto createValidatedVatDto(LocalDateTime created, LocalDateTime updated) {
        return createValidatedVatDto().withInternalTimestamps(new TimestampsDto(created.toString(), updated.toString()));
    }

    static VatDetailsToValidateDto createVatDetailsToValidateDto() {
        VatDetailsToValidateDto vatDetailsToValidateDto = new VatDetailsToValidateDto("BE", "0835221567");

        return vatDetailsToValidateDto;
    }
}
