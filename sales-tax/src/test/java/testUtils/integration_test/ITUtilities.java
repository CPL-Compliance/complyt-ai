package testUtils.integration_test;

import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.fast_tax.InformationComponent;
import com.complyt.domain.sales_tax.fast_tax.TaxInfoItem;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.domain.transaction.Address;
import com.complyt.v1.models.*;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.CustomerTypeDto;
import com.complyt.v1.models.customer.exemption.ClassificationDto;
import com.complyt.v1.models.nexus.*;
import com.complyt.v1.models.sales_tax.SalesTaxRatesDto;
import com.complyt.v1.models.transaction.*;
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.ObjectId;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public interface ITUtilities {

    String NON_EXISTING_COMPLYT_ID = "d18068f0-6d98-4b0d-ba19-4536f0b4173a";

    // if no items provided, puts a default stub
    static TransactionDto stubTransactionDto(String externalId, UUID customerId, ItemDto... items) {
        return new TransactionDto(null, externalId, "1", "INVUS1000",
                List.of(items.length < 1 ? new ItemDto[]{stubItemDto()} : items),
                false, null, new MandatoryAddressDto("Acampo", "US", null, "CA", "1525 R Jahant Rd", "", "95220", false), customerId,
                null, null, TransactionStatusDto.ACTIVE, null, new TimestampsDto(LocalDateTime.now().toString(), LocalDateTime.now().toString()),
                TransactionTypeDto.INVOICE, null, null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, TransactionFilingStatusDto.NOT_FILED, "USD", null);
    } // note isTaxInclusive is false, finalTransactionAmount is zero


    static TransactionDto stubTransactionDtoNonUsaCountry(String externalId, UUID customerId, ItemDto... items) {
        MandatoryAddressDto shippingAddress = new MandatoryAddressDto(null, "Canada", null, null, "", "Quebec", null, false);
        return stubTransactionDto(externalId, customerId, items)
                .withShippingAddress(shippingAddress);
    }

    static CustomerDto stubCustomerDto(String externalId) {
        return new CustomerDto(null, externalId, "1",
                "stub customer", null, "captaindope@gg.com", CustomerTypeDto.RETAIL,
                null, new TimestampsDto(LocalDateTime.now().toString(), LocalDateTime.now().toString()), "comment");
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
                null, null);
    }

    static ShippingFeeDto stubShippingFeeDto() {
        return new ShippingFeeDto(false, BigDecimal.ZERO, BigDecimal.valueOf(500), null,
                null, null, null, "C6S1", null, null);
    }

    static FastTaxData stubFastTaxFlorida() {
        return new FastTaxData("Address", List.of(new TaxInfoItem(
                "Miami", "0", "0",
                "Miami-Dade", "0", "0.010",
                List.of(new InformationComponent("CountyFIPS", "086")),
                "", "", "0",
                "FL", "Florida", "0.06",
                "0.070", "SERVICES", "33142")));
    }

    static FastTaxData stubFastTaxNewYork() {
        return new FastTaxData("Address", List.of(new TaxInfoItem(
                "New York", "0", "0.045",
                "New York", "0.00375", "0",
                List.of(new InformationComponent("CountyFIPS", "061")),
                "", "", "0",
                "NY", "New York", "0.04",
                "0.08875", "SERVICES", "10001")));
    }

    static FastTaxData stubFastTaxMinnesota() {
        return new FastTaxData("Address", List.of(new TaxInfoItem(
                "Minneapolis", "0", "0.0500",
                "Hennepin", "0", "0.00150",
                List.of(new InformationComponent("CountyFIPS", "053")),
                "", "", "0.00500",
                "MN", "Minnesota", "0.06875",
                "0.08025", "SERVICES", "55410")));
    }

    static FastTaxData stubFastTaxConnecticut() {
        return new FastTaxData("Address", List.of(new TaxInfoItem(
                "West Haven", "0", "0",
                "New Haven", "0", "0",
                List.of(new InformationComponent("CountyFIPS", "009")),
                "", "", "0",
                "CT", "Connecticut", "0.06350",
                "0.06350", "SERVICES", "06516")));
    }

    static FastTaxData stubFastTaxGeorgia() {
        return new FastTaxData("Address", List.of(new TaxInfoItem(
                "Atlanta", "0", "0.01900",
                "Fulton", "0", "0.03000",
                List.of(new InformationComponent("CountyFIPS", "121")),
                "", "", "0",
                "GA", "Georgia", "0.0400",
                "0.08900", "LABOR/SERVICES", "30303-3192")));
    }

    static FastTaxData stubFastTaxIndiana() {
        return new FastTaxData("Address", List.of(new TaxInfoItem(
                "Indianapolis", "0", "0",
                "Marion", "0", "0",
                List.of(new InformationComponent("CountyFIPS", "097")),
                "", "", "0",
                "IN", "Indiana", "0.07000",
                "0.07000", "LABOR/SERVICES", "46202-5109")));
    }

    static FastTaxData stubFastTaxMaine() {
        return new FastTaxData("Address", List.of(new TaxInfoItem(
                "Cape Elizabeth", "0", "0",
                "Cumberland", "0", "0",
                List.of(new InformationComponent("CountyFIPS", "005")),
                "", "", "0",
                "IN", "Indiana", "0.05500",
                "0.05500", "LABOR/FREIGHT/SERVICES", "04107-1929")));
    }

    static FastTaxData stubFastTaxKentucky() {
        return new FastTaxData("Address", List.of(new TaxInfoItem(
                "Louisville", "0", "0",
                "Jefferson", "0", "0",
                List.of(new InformationComponent("CountyFIPS", "111")),
                "", "", "0",
                "KY", "Kentucky", "0.06000",
                "0.06000", "LABOR/SERVICES", "40127-2430")));
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

    static Timestamps createTimestamps() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return new Timestamps(localDateTime.minusYears(1), localDateTime);
    }

    static TimestampsDto createTimestampsDto() {
        LocalDateTime localDateTime = LocalDateTime.now();
        return new TimestampsDto(localDateTime.minusYears(1).toString(), localDateTime.toString());
    }

    static Address createAddressInCalifornia() {
        return new Address("Fresno", "US", null, "CA", "7498 N Remington Ave", "93711-5508", "", false);
    }

    static MandatoryAddressDto createAddressDtoInCalifornia() {
        return new MandatoryAddressDto("Fresno", "US", null, "CA", "7498 N Remington Ave", "", "93711-5508", false);
    }

    static OptionalAddressDto createOptionalAddressDtoInCalifornia() {
        return new OptionalAddressDto("Fresno", "US", null, "CA", "7498 N Remington Ave", null, "93711-5508", false);
    }

    static SalesTaxRates createCaliforniaSalesTaxRates() {
        return new SalesTaxRates(new BigDecimal("0.00375"), new BigDecimal("0.0"), new BigDecimal("0.00725"), new BigDecimal("0.0125"), new BigDecimal("0.06"), null);
    }

    static SalesTaxRatesDto createCaliforniaSalesTaxRatesDto() {
        return new SalesTaxRatesDto(new BigDecimal("0.00375"), new BigDecimal("0.0"), new BigDecimal("0.00725"), new BigDecimal("0.0125"), new BigDecimal("0.06"), null);
    }

    static ComplytSalesTaxRates createCaliforniaComplytSalesTaxRates() {
        Address address = createAddressInCalifornia();
        SalesTaxRates salesTaxRates = createCaliforniaSalesTaxRates();
        LocalDateTime now = LocalDateTime.now();
        return new ComplytSalesTaxRates(address, salesTaxRates);
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
        return new Document("_id", new ObjectId())
                .append("nexus", new Document("taxableDate", LocalDateTime.now()))
                .append("tenantId", "other_it_tenant")
                .append("name", "Bestclient TM")
                .append("internalTimestamps", new Document("createdDate", LocalDateTime.now())
                        .append("updatedDate", LocalDateTime.now()));
    }

    static Document customerDocument() {
        return new Document("_id", new ObjectId("62c5672583479c0adfa2c4a4"))
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
                .append("complytId", new Binary((byte) 0x04, Base64.getDecoder().decode("TPu/C9PlSVSKkMnC6DLl9Q==")))
                .append("source", "1")
                .append("email", "captain@dope.com");
    }

    static Document exemptionDocument() {
        return new Document("_id", new ObjectId("6372009a6887300e852749e4"))
                .append("customerId", new Binary((byte) 0x04, Base64.getDecoder().decode("TPu/C9PlSVSKkMnC6DLl9Q==")))
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
                .append("complytId", new Binary((byte) 0x04, Base64.getDecoder().decode("KqWAnzAdRPOQgbTzJhNGPA==")))
                .append("country", "USA")
                .append("exemptionStatus", "ACTIVE")
                .append("internalTimestamps", new Document("createdDate", LocalDateTime.now())
                        .append("updatedDate", LocalDateTime.now()));
    }

    static Document salesTaxTrackingDocument() {
        return new Document("_id", new ObjectId("642415ce6632165b050b01ab"))
                .append("complytId", new Binary((byte) 0x04, Base64.getDecoder().decode("bqoTPN+cT4i7qW3ThFyAOg==")))
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
                .append("registered", null)
                .append("registrationDate", null)
                .append("clientTracking", new Document("_id", new ObjectId("649d6ecf86de4c7ec0ca6128"))
                        .append("tenantId", "it_tenant")
                        .append("nexus", new Document("taxableDate", LocalDateTime.now()))
                        .append("name", "it_tenant")
                        .append("internalTimestamps", new Document("createdDate", LocalDateTime.now())
                                .append("updatedDate", LocalDateTime.now())));
    }

    static Document transactionDocument() {
        return new Document("_id", new ObjectId("65b6a7f8f930555db9c7c246"))
                .append("complytId", new Binary((byte) 0x04, Base64.getDecoder().decode("sxrpBJCNThmlyXaNzR0BqQ==")))
                .append("externalId", "2c9d525b-e809-4916-831e-c50d08a22d92")
                .append("source", "6")
                .append("documentName", "INV-0151")
                .append("items", List.of(
                        new Document("unitPrice", "12500")
                                .append("quantity", "1")
                                .append("totalPrice", "12500")
                                .append("description", "Quarterly Activation Fees")
                                .append("name", "e7ec72ea-0b5b-45b8-b2d8-c8f6ab3e4603")
                                .append("taxCode", "")
                                .append("jurisdictionalSalesTaxRules", new Document("name", "Texas")
                                        .append("abbreviation", "TX")
                                        .append("taxable", true)
                                        .append("specialTreatment", false)
                                        .append("calculationType", "PERCENTAGE")
                                        .append("description", "description3")
                                        .append("calculationValue", "0.8"))
                                .append("manualSalesTax", true)
                                .append("manualSalesTaxRate", "0")
                                .append("tangibleCategory", "TANGIBLE")
                                .append("taxableCategory", "TAXABLE")
                                .append("calculatedTotal", "12500")))
                .append("shippingAddress", new Document("city", "San Antonio")
                        .append("country", "USA")
                        .append("county", "Bexar")
                        .append("state", "TX")
                        .append("street", "100 Montana Suite 3D01")
                        .append("zip", "78203")
                        .append("isPartial", false))
                .append("customerId", new Binary((byte) 0x04, Base64.getDecoder().decode("xNUzPYEmSSmYGe7Dqj6hmA==")))
                .append("transactionStatus", "ACTIVE")
                .append("tenantId", "org_AHOc5X21mIdGLgrd")
                .append("internalTimestamps", new Document("createdDate", LocalDateTime.now())
                        .append("updatedDate", LocalDateTime.now()))
                .append("externalTimestamps", new Document("createdDate", LocalDateTime.now())
                        .append("updatedDate", LocalDateTime.now()))
                .append("transactionType", "INVOICE")
                .append("taxableItemsAmount", "43750")
                .append("tangibleItemsAmount", "43750")
                .append("totalItemsAmount", "43750")
                .append("_class", "com.complyt.domain.transaction.Transaction")
                .append("totalDiscount", "0")
                .append("billingAddress", new Document("street", "123 Main St")
                        .append("zip", "12345")
                        .append("country", "USA")
                        .append("county", "SomeCounty")
                        .append("state", "CA")
                        .append("city", "SomeCity"));
    }

    public static Document nexusStateRuleDocument() {
        return new Document("_id", new ObjectId("62de9f8c2a864b00112ec127"))
                .append("state", new Document("abbreviation", "GA")
                        .append("name", "Georgia")
                        .append("code", "13"))
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
}
