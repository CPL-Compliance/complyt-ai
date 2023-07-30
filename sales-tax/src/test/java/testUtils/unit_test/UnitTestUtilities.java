package testUtils.unit_test;

import com.complyt.domain.*;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.customer.exemption.*;
import com.complyt.domain.nexus.*;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.sales_tax.ComplytSalesTaxRates;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.CitySalesTaxRules;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.zip_tax.Result;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.v1.models.*;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.CustomerTypeDto;
import com.complyt.v1.models.customer.exemption.*;
import com.complyt.v1.models.sales_tax.SalesTaxRatesDto;
import com.complyt.v1.models.timestamps.TimestampsDto;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnitTestUtilities {

    LocalDateTime localDateTime;
    String tenantId;

    UUID customerIdOtherDomains;

    String certificateId;

    String source;

    public UnitTestUtilities(LocalDateTime localDateTime, String tenantId) {
        this.localDateTime = localDateTime;
        this.tenantId = tenantId;
        customerIdOtherDomains = UUID.randomUUID();
        certificateId = UUID.randomUUID().toString();
        source = "1";
    }

    public static Address createAddressInCalifornia() {
        return new Address("Fresno", "US", "county", "CA", "7498 N Remington Ave", "93711-5508", false);
    }

    public static MandatoryAddressDto createAddressDtoInCalifornia() {
        return new MandatoryAddressDto("Fresno", "US", "county", "CA", "7498 N Remington Ave", "93711-5508", false);
    }

    public static SalesTaxRates createCaliforniaSalesTaxRates() {
        return new SalesTaxRates(0f, 0.0f, 0.005f, 0.0125f, 0.06f, null);
    }

    public static SalesTaxRatesDto createCaliforniaSalesTaxRatesDto() {
        return new SalesTaxRatesDto(0f, 0.0f, 0.005f, 0.0125f, 0.06f, null);
    }

    public static ComplytSalesTaxRates createCaliforniaComplytSalesTaxRates() {
        Address address = createAddressInCalifornia();
        SalesTaxRates salesTaxRates = createCaliforniaSalesTaxRates();
        return new ComplytSalesTaxRates(address, salesTaxRates);
    }

    public static ComplytSalesTaxRatesDto createCaliforniaComplytSalesTaxRatesDto() {
        MandatoryAddressDto address = createAddressDtoInCalifornia();
        SalesTaxRatesDto salesTaxRates = createCaliforniaSalesTaxRatesDto();
        return new ComplytSalesTaxRatesDto(address, salesTaxRates);
    }

    public void checkErrorMessages(LinkedHashMap map, Set<String> expectedErrors) {
        String message = (String) map.get("message");
        String[] errors = message.substring(1, message.length() - 1).split(", ");
        assertEquals(expectedErrors.size(), errors.length);
        for (String err : errors) {
            assertTrue(expectedErrors.contains(err));
        }
    }

    public String stringWithLength(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (; 0 < length; length--) stringBuilder.append('a');
        return stringBuilder.toString();
    }

    public String getUnifiedSource() {
        return source;
    }

    public Customer createCustomer(String id) {
        Timestamps internalTimeStamps = new Timestamps(localDateTime, localDateTime);
        LocalDateTime localDateTimeMinusOneMinute = localDateTime.minusMinutes(1);
        Timestamps externalTimestamps = new Timestamps(localDateTimeMinusOneMinute, localDateTime);
        return new Customer(
                UUID.randomUUID(),
                id,
                id,
                source,
                "name",
                new Address("City", "Country", "County", "CA", "Street", "Zip", false),
                tenantId,
                CustomerType.RETAIL,
                internalTimeStamps,
                externalTimestamps
        );
    }

    public CustomerDto createCustomerDto(String id) {
        TimestampsDto internalTimeStamps = new TimestampsDto(localDateTime.toString(), localDateTime.toString());
        LocalDateTime localDateTimeMinusOneMinute = localDateTime.minusMinutes(1);
        TimestampsDto externalTimestamps = new TimestampsDto(localDateTimeMinusOneMinute.toString(), localDateTime.toString());
        return new CustomerDto(
                UUID.randomUUID(),
                id,
                source,
                "name",
                new OptionalAddressDto("City", "Country", "County", "CA", "Street", "Zip", false),
                CustomerTypeDto.RETAIL,
                internalTimeStamps,
                externalTimestamps
        );
    }

    public Transaction createTransaction(String id) {
        String documentName = "INVUS1000";
        Address billingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip", false);
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip", false);
        List<Item> items = createItems(true, false);
        Timestamps timeStamps = new Timestamps(localDateTime, localDateTime);
        ShippingFee shippingFee = createShippingFee(true, false);
        return new Transaction(UUID.randomUUID(), id, id, source, documentName, items, billingAddress, shippingAddress, customerIdOtherDomains, createCustomer(customerIdOtherDomains.toString()), null, TransactionStatus.ACTIVE, tenantId, timeStamps, timeStamps, TransactionType.INVOICE, shippingFee, null, 0, 0, 0);
    }

    public TransactionDto createTransactionDto(String id) {
        String documentName = "INVUS1000";
        OptionalAddressDto billingAddress = new OptionalAddressDto("City", "Country", "County", "CA", "Street", "Zip", false);
        MandatoryAddressDto shippingAddress = new MandatoryAddressDto("City", "Country", "County", "CA", "Street", "Zip", false);
        List<ItemDto> items = createItemDtos(true, false);
        TimestampsDto timeStamps = new TimestampsDto(localDateTime.toString(), localDateTime.toString());
        ShippingFeeDto shippingFeeDto = createShippingFeeDto(true, false);

        return new TransactionDto(UUID.randomUUID(), id, source, documentName, items, billingAddress, shippingAddress, customerIdOtherDomains, createCustomerDto(customerIdOtherDomains.toString()), null, TransactionStatusDto.ACTIVE, timeStamps, timeStamps, TransactionTypeDto.INVOICE, shippingFeeDto, null, 0, 0, 0);
    }

    public List<Item> createItems(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        return new ArrayList<>() {{
            add(new Item(2000, 4, 8000, "description", "name", "C1S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRules() : null,
                    null, false, 0, withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));
            add(new Item(2000, 4, 8000, "description", "name", "C3S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRules() : null,
                    null, false, 0, withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));

        }};
    }

    public List<Item> createItemsWithSalesTaxRate(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        return new ArrayList<>() {{
            add(new Item(2000, 4, 8000, "description", "name", "C1S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRules() : null,
                    createSalesTaxRates(), false, 0, withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));
            add(new Item(2000, 4, 8000, "description", "name", "C3S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRules() : null,
                    createSalesTaxRates(), false, 0, withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));

        }};
    }

    public List<ItemDto> createItemDtosWithSalesTaxRate(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        return new ArrayList<>() {{
            add(new ItemDto(2000, 4, 8000, "description", "name", "C1S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRulesDto() : null,
                    createSalesTaxRatesDto(), false, 0, withTangibleCategory ? TangibleCategoryDto.TANGIBLE : null, TaxableCategoryDto.TAXABLE));
            add(new ItemDto(2000, 4, 8000, "description", "name", "C3S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRulesDto() : null,
                    createSalesTaxRatesDto(), false, 0, withTangibleCategory ? TangibleCategoryDto.TANGIBLE : null, TaxableCategoryDto.TAXABLE));
        }};
    }

    public List<ItemDto> createItemDtos(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        return new ArrayList<>() {{
            add(new ItemDto(2000, 4, 8000, "description", "name", "C1S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRulesDto() : null,
                    null, false, 0, withTangibleCategory ? TangibleCategoryDto.TANGIBLE : null, TaxableCategoryDto.TAXABLE));
            add(new ItemDto(2000, 4, 8000, "description", "name", "C3S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRulesDto() : null,
                    null, false, 0, withTangibleCategory ? TangibleCategoryDto.TANGIBLE : null, TaxableCategoryDto.TAXABLE));

        }};
    }

    public SalesTaxRates createSalesTaxRates() {
        return new SalesTaxRates(0.1f, 0.1f, 0.1f, 0.4f, 0.1f, null);
    }

    public SalesTaxRatesDto createSalesTaxRatesDto() {
        return new SalesTaxRatesDto(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, null);
    }

    public ShippingFee createShippingFee(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, 0, 1000, withJurisdictionalRules ? rules : null, null, "C6S1", TaxableCategory.TAXABLE, withTangibleCategory ? TangibleCategory.INTANGIBLE : null);
    }

    public ShippingFee createShippingFeeWithSalesTaxRates(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, 0, 1000, withJurisdictionalRules ? rules : null, createSalesTaxRates(), "C6S1", TaxableCategory.TAXABLE, withTangibleCategory ? TangibleCategory.INTANGIBLE : null);
    }

    public ShippingFeeDto createShippingFeeDto(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        JurisdictionalSalesTaxRulesDto rules = createJurisdictionalSalesTaxRulesDto();
        return new ShippingFeeDto(false, 0, 1000, withJurisdictionalRules ? rules : null, null, "C6S1", TaxableCategoryDto.TAXABLE, withTangibleCategory ? TangibleCategoryDto.INTANGIBLE : null);
    }

    public JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", 0.5f, null);
    }

    public JurisdictionalSalesTaxRulesDto createJurisdictionalSalesTaxRulesDto() {
        return new JurisdictionalSalesTaxRulesDto("California", "CA", true,
                false, CalculationType.FIXED, "description", 0.5f, null);
    }

    public CitySalesTaxRules createCitySalesTaxRules() {
        return new CitySalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", 0.05f);
    }

    public Exemption createExemption(String id) {
        State state = new State("CA", "02", "California");
        Classification classification = new Classification("code", "description");
        ValidationDates validationDates = new ValidationDates(localDateTime.minusYears(1), localDateTime.plusYears(1));
        Timestamps internalTimestamps = new Timestamps(localDateTime, localDateTime);
        Status status = new Status("code", "name");
        Certificate certificate = new Certificate(certificateId, "url", "name");

        return new Exemption(UUID.randomUUID(), id, tenantId, customerIdOtherDomains,
                state, classification, validationDates, internalTimestamps, status, certificate, ExemptionType.FULLY);
    }

    public ExemptionDto createExemptionDto() {
        StateDto state = new StateDto("CA", "02", "California");
        ClassificationDto classification = new ClassificationDto("code", "description");
        ValidationDatesDto validationDates = new ValidationDatesDto(
                localDateTime.minusYears(1).toString(),
                localDateTime.plusYears(1).toString());
        TimestampsDto internalTimestamps = new TimestampsDto(localDateTime.toString(), localDateTime.toString());
        StatusDto status = new StatusDto("code", "name");
        CertificateDto certificate = new CertificateDto(certificateId, "url", "name");

        return new ExemptionDto(UUID.randomUUID(), customerIdOtherDomains,
                state, classification, validationDates, internalTimestamps, status, certificate, ExemptionTypeDto.FULLY);
    }

    public NexusStateRule createNexusStateRule(String id) {
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

        NexusThreshold nexusThreshold = new NexusThreshold(1000, 2, Definition.AMOUNT_OR_COUNT);

        return new NexusStateRule(id, true, state, taxableCategories, tangibleCategories, customerTypes,
                TimeFrame.PREVIOUS_TWELVE_MONTHS, nexusThreshold);
    }

    public List<Taxable> createTaxables(Transaction transaction) {
        List<Taxable> taxables = new ArrayList<>(transaction.getItems());
        taxables.add(transaction.getShippingFee());
        return taxables;
    }

    public SalesTaxTracking createSalesTaxTracking(String id) {
        State state = new State("CA", "02", "California");
        return new SalesTaxTracking(UUID.randomUUID(), id, state,
                tenantId, "comment", true,
                new PhysicalNexusTracker(false, localDateTime),
                new EconomicNexusTracker(false, localDateTime), localDateTime,
                true, localDateTime);
    }

    public SalesTaxTrackingDto createSalesTaxTrackingDto() {
        StateDto state = new StateDto("CA", "02", "California");
        SalesTaxTrackingDto salesTaxTrackingDto = new SalesTaxTrackingDto(UUID.randomUUID(), state,
                "comment", true,
                new PhysicalNexusTrackerDto(false, localDateTime),
                new EconomicNexusTrackerDto(false, localDateTime), localDateTime,
                true, localDateTime);

        return salesTaxTrackingDto;
    }

    public Result createResult() {
        return new Result("", "", "injectedCounty", "", 0f, 0f, "", "",
                0f, 0f, 0f, 0f, "", 0f, 0, "",
                0f, 0f, "", 0, 0, "", 0,
                0, "", 0, 0, "", 0, 0, "",
                0, 0, "");
    }

    public Address createAddress() {
        return new Address("City", "Country", "County", "CA", "Street", "Zip", false);
    }

    public Timestamps createTimestamps() {
        return new Timestamps(localDateTime.minusYears(1), localDateTime);
    }

    public TimestampsDto createTimestampsDto() {
        return new TimestampsDto(localDateTime.minusYears(1).toString(), localDateTime.toString());
    }

    public ValidationDates createValidationDates() {
        return new ValidationDates(localDateTime.minusYears(1), localDateTime);
    }

    public ValidationDatesDto createValidationDatesDto() {
        return new ValidationDatesDto(localDateTime.minusYears(1).toString(), localDateTime.toString());
    }

}
