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
import com.complyt.domain.transaction.*;
import com.complyt.v1.config.BodyCheckConfig;
import com.complyt.v1.models.*;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.CustomerTypeDto;
import com.complyt.v1.models.customer.exemption.*;
import com.complyt.v1.models.nexus.*;
import com.complyt.v1.models.sales_tax.ComplytSalesTaxRatesDto;
import com.complyt.v1.models.sales_tax.SalesTaxRatesDto;
import com.complyt.v1.models.transaction.*;
import com.complyt.v1.validators.body_checkers.ItemsAlignmentChecker;
import com.complyt.v1.validators.body_checkers.TransactionDtoShippingAddressChecker;
import com.complyt.v1.validators.body_checkers.TransactionTotalAmountChecker;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UnitTestUtilities {

    static ResourceBundle validationMessages = ResourceBundle.getBundle("org.hibernate.validator.ValidationMessages", Locale.getDefault());
    public final String tenantId;
    LocalDateTime localDateTime;
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
        return new SalesTaxRates(new BigDecimal(0), new BigDecimal(0), new BigDecimal("0.005"), new BigDecimal("0.0125"), new BigDecimal("0.06"), null);
    }

    public static SalesTaxRatesDto createCaliforniaSalesTaxRatesDto() {
        return new SalesTaxRatesDto(new BigDecimal(0), new BigDecimal(0), new BigDecimal("0.005"), new BigDecimal("0.0125"), new BigDecimal("0.06"), null);
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

    public static boolean stringPassedRegex(String input, Pattern pattern) {
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    public static List<State> createStateList() {
        return new ArrayList<>() {{
            add(new State("California", "04", "CA"));
            add(new State("New York", "05", "NY"));
            add(new State("Arizona", "06", "AZ"));
        }};
    }

    public static List<StateDto> createStateListDto() {
        return new ArrayList<>() {{
            add(new StateDto("California", "04", "CA"));
            add(new StateDto("New York", "05", "NY"));
            add(new StateDto("Arizona", "06", "AZ"));
        }};
    }

    public static List<Exemption> createExemptionsListFromWrapper(ExemptionWrapper exemptionWrapper) {
        List<Exemption> exemptionList = new ArrayList<>();
        for (State state : exemptionWrapper.states()) {
            exemptionList.add(exemptionWrapper.exemption().withState(state));
        }

        return exemptionList;
    }

    public static List<ExemptionDto> createExemptionsDtoListFromWrapper(ExemptionWrapperDto exemptionWrapper) {
        List<ExemptionDto> exemptionList = new ArrayList<>();
        for (StateDto state : exemptionWrapper.states()) {
            exemptionList.add(exemptionWrapper.exemption().withState(state));
        }

        return exemptionList;
    }

    public static String extractStringFromJakartaProperties(String property) {
        return validationMessages.getString(property);
    }

    public TransactionNexusSummary createTransactionNexusSummary() {
        return new TransactionNexusSummary(BigDecimal.valueOf(1200), localDateTime, TransactionType.INVOICE);
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
                null,
                CustomerType.RETAIL,
                internalTimeStamps,
                externalTimestamps,
                "comment"
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
                null,
                CustomerTypeDto.RETAIL,
                internalTimeStamps,
                externalTimestamps,
                "comment"
        );
    }

    public Transaction createTransaction(String id) {
        String documentName = "INVUS1000";
        Address billingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip", false);
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip", false);
        List<Item> items = createItems(true, false);
        Timestamps timeStamps = new Timestamps(localDateTime, localDateTime);
        ShippingFee shippingFee = createShippingFee(true, false);

        return new Transaction(UUID.randomUUID(), id, id, source,
                documentName, items, billingAddress, shippingAddress,
                customerIdOtherDomains, createCustomer(customerIdOtherDomains.toString()),
                null, TransactionStatus.ACTIVE, tenantId, timeStamps, timeStamps,
                TransactionType.INVOICE, shippingFee, null, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, TransactionFilingStatus.NOT_FILED);
    }

    public TransactionDto createTransactionDto(String id) {
        String documentName = "INVUS1000";
        OptionalAddressDto billingAddress = new OptionalAddressDto("City", "Country", "County", "CA", "Street", "Zip", false);
        MandatoryAddressDto shippingAddress = new MandatoryAddressDto("City", "Country", "County", "CA", "Street", "Zip", false);
        List<ItemDto> items = createItemDtos(true, false);
        TimestampsDto timeStamps = new TimestampsDto(localDateTime.toString(), localDateTime.toString());
        ShippingFeeDto shippingFeeDto = createShippingFeeDto(true, false);
        return new TransactionDto(UUID.randomUUID(), id, source, documentName,
                items, billingAddress, shippingAddress, customerIdOtherDomains,
                createCustomerDto(customerIdOtherDomains.toString()), null,
                TransactionStatusDto.ACTIVE, timeStamps, timeStamps, TransactionTypeDto.INVOICE,
                shippingFeeDto, null, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, TransactionFilingStatusDto.NOT_FILED);
    }

    public List<Item> createItems(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        return new ArrayList<>() {{
            add(new Item(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), "description", "name", "C1S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRules() : null,
                    null, false, BigDecimal.ZERO, withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));
            add(new Item(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), "description", "name", "C3S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRules() : null,
                    null, false, BigDecimal.ZERO, withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));

        }};
    }

    public Item createItemWithNegativePrice(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        return new Item(new BigDecimal(-2000), new BigDecimal(4), new BigDecimal(-8000), "description", "name", "C1S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRules() : null,
                null, false, BigDecimal.ZERO, withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE);
    }

    public List<Item> createItemsWithSalesTaxRate(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        return new ArrayList<>() {{
            add(new Item(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), "description", "name", "C1S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRules() : null,
                    createSalesTaxRates(), false, BigDecimal.ZERO, withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));
            add(new Item(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), "description", "name", "C3S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRules() : null,
                    createSalesTaxRates(), false, BigDecimal.ZERO, withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));

        }};
    }

    public List<ItemDto> createItemDtosWithSalesTaxRate(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        return new ArrayList<>() {{
            add(new ItemDto(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), "description", "name", "C1S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRulesDto() : null,
                    createSalesTaxRatesDto(), false, BigDecimal.ZERO, withTangibleCategory ? TangibleCategoryDto.TANGIBLE : null, TaxableCategoryDto.TAXABLE));
            add(new ItemDto(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), "description", "name", "C3S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRulesDto() : null,
                    createSalesTaxRatesDto(), false, BigDecimal.ZERO, withTangibleCategory ? TangibleCategoryDto.TANGIBLE : null, TaxableCategoryDto.TAXABLE));
        }};
    }

    public List<ItemDto> createItemDtos(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        return new ArrayList<>() {{
            add(new ItemDto(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), "description", "name", "C1S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRulesDto() : null,
                    null, false, BigDecimal.ZERO, withTangibleCategory ? TangibleCategoryDto.TANGIBLE : null, TaxableCategoryDto.TAXABLE));
            add(new ItemDto(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), "description", "name", "C3S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRulesDto() : null,
                    null, false, BigDecimal.ZERO, withTangibleCategory ? TangibleCategoryDto.TANGIBLE : null, TaxableCategoryDto.TAXABLE));

        }};
    }

    public ItemDto createItemDtoWithNegativeAmount(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        return new ItemDto(new BigDecimal(-2000), new BigDecimal(4), new BigDecimal(-8000), "description", "name", "C1S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRulesDto() : null,
                null, false, BigDecimal.ZERO, withTangibleCategory ? TangibleCategoryDto.TANGIBLE : null, TaxableCategoryDto.TAXABLE);
    }

    public SalesTaxRates createSalesTaxRates() {
        return new SalesTaxRates(new BigDecimal("0.1"), new BigDecimal("0.1"), new BigDecimal("0.1"), new BigDecimal("0.4"), new BigDecimal("0.1"), null);
    }

    public SalesTaxRatesDto createSalesTaxRatesDto() {
        return new SalesTaxRatesDto(new BigDecimal("0.1"), new BigDecimal("0.1"), new BigDecimal("0.1"), new BigDecimal("0.4"), new BigDecimal("0.1"), null);
    }

    public ShippingFee createShippingFee(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, BigDecimal.ZERO, new BigDecimal(1000), withJurisdictionalRules ? rules : null, null, "C6S1", TaxableCategory.TAXABLE, withTangibleCategory ? TangibleCategory.INTANGIBLE : null);
    }

    public ShippingFee createShippingFeeWithSalesTaxRates(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, BigDecimal.ZERO, new BigDecimal(1000), withJurisdictionalRules ? rules : null, createSalesTaxRates(), "C6S1", TaxableCategory.TAXABLE, withTangibleCategory ? TangibleCategory.INTANGIBLE : null);
    }

    public ShippingFeeDto createShippingFeeDto(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        JurisdictionalSalesTaxRulesDto rules = createJurisdictionalSalesTaxRulesDto();
        return new ShippingFeeDto(false, BigDecimal.ZERO, new BigDecimal(1000), withJurisdictionalRules ? rules : null, null, "C6S1", TaxableCategoryDto.TAXABLE, withTangibleCategory ? TangibleCategoryDto.INTANGIBLE : null);
    }

    public JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", new BigDecimal("0.5"), null);
    }

    public JurisdictionalSalesTaxRulesDto createJurisdictionalSalesTaxRulesDto() {
        return new JurisdictionalSalesTaxRulesDto("California", "CA", true,
                false, CalculationType.FIXED, "description", new BigDecimal("0.5"), null);
    }

    public CitySalesTaxRules createCitySalesTaxRules() {
        return new CitySalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", new BigDecimal("0.5"));
    }

    public Exemption createExemption(String id) {
        State state = new State("CA", "02", "California");
        Classification classification = new Classification("code", "description");
        ValidationDates validationDates = new ValidationDates(localDateTime.minusYears(1), localDateTime.plusYears(1));
        Timestamps internalTimestamps = new Timestamps(localDateTime, localDateTime);
        Status status = new Status("code", "name");
        Certificate certificate = new Certificate(certificateId, "url", "name");

        return new Exemption(UUID.randomUUID(), id, tenantId, customerIdOtherDomains,
                state, classification, validationDates, internalTimestamps, status, certificate, ExemptionType.FULLY, ExemptionStatus.ACTIVE);
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
                state, classification, validationDates, internalTimestamps, status, certificate, ExemptionTypeDto.FULLY, ExemptionStatusDto.ACTIVE);
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

        NexusThreshold nexusThreshold = new NexusThreshold(new BigDecimal(1000), 2, Definition.AMOUNT_OR_COUNT);

        return new NexusStateRule(id, true, state, taxableCategories, tangibleCategories, customerTypes,
                TimeFrame.PREVIOUS_TWELVE_MONTHS, nexusThreshold, localDateTime);
    }

    public NexusStateRuleDto createNexusStateRuleDto() {
        StateDto state = new StateDto("CA", "02", "California");

        List<TaxableCategoryDto> taxableCategories = new ArrayList<>() {{
            add(TaxableCategoryDto.TAXABLE);
        }};

        List<TangibleCategoryDto> tangibleCategories = new ArrayList<>() {{
            add(TangibleCategoryDto.TANGIBLE);
        }};

        List<CustomerTypeDto> customerTypes = new ArrayList<>() {{
            add(CustomerTypeDto.RETAIL);
        }};

        NexusThresholdDto nexusThreshold = new NexusThresholdDto(new BigDecimal(1000), 2, DefinitionDto.AMOUNT_OR_COUNT);

        return new NexusStateRuleDto(true, state, taxableCategories, tangibleCategories, customerTypes, TimeFrameDto.PREVIOUS_TWELVE_MONTHS, nexusThreshold, localDateTime);
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
                new EconomicNexusTracker(false, localDateTime),
                createNexusStateRule(id + "nsr"),
                createClientTracking(tenantId),
                new HashMap<>(),
                new HashMap<>(),
                localDateTime,
                true, localDateTime,
                FilingFrequency.MONTHLY);
    }

    public ClientTracking createClientTracking(String tenantId) {
        Timestamps internalTimestamp = new Timestamps(localDateTime, localDateTime);
        return new ClientTracking(null, tenantId, new Nexus(localDateTime), "client dope", internalTimestamp);
    }

    public SalesTaxTrackingDto createSalesTaxTrackingDto() {
        StateDto state = new StateDto("CA", "02", "California");
        SalesTaxTrackingDto salesTaxTrackingDto = new SalesTaxTrackingDto(UUID.randomUUID(), state,
                "comment", true,
                new PhysicalNexusTrackerDto(false, localDateTime.toString()),
                new EconomicNexusTrackerDto(false, localDateTime.toString()),
                Map.of(),
                createNexusStateRuleDto(),
                createClientTrackingDto(),
                localDateTime.toString(),
                true, localDateTime.toString(),
                FilingFrequencyDto.MONTHLY);

        return salesTaxTrackingDto;
    }

    public ClientTrackingDtoTenant createClientTrackingDtoTenant(String tenantId) {
        TimestampsDto internalTimestamps = new TimestampsDto(localDateTime.toString(), localDateTime.toString());
        return new ClientTrackingDtoTenant(new NexusDto(localDateTime), "client dope", internalTimestamps, tenantId);
    }

    public ClientTrackingDto createClientTrackingDto() {
        TimestampsDto internalTimestamps =  new TimestampsDto(LocalDateTime.now().toString(), LocalDateTime.now().toString());
        return new ClientTrackingDto(new NexusDto(localDateTime), "client dope", internalTimestamps);
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

    public BodyCheckConfig createBodyCheckConfig() {
        return new BodyCheckConfig(
                List.of(
                        new TransactionDtoShippingAddressChecker(),
                        new TransactionTotalAmountChecker(),
                        new ItemsAlignmentChecker()
                ));
    }

    public Update buildSalesTaxTrackingUpdate(SalesTaxTracking salesTaxTracking) {
        Update update = buildSalesTaxTrackingUpdateOfPreviousTwelveMonths(salesTaxTracking);

        update.set("transactionNexusSummaries", salesTaxTracking.getTransactionNexusSummaries());
        Map<String, NexusCalculationSummary> stringKeysSummaries = salesTaxTracking.getNexusCalculationSummaries().entrySet().stream()
                .collect(Collectors.toMap(entry -> entry.getKey().format(DateTimeFormatter.ISO_LOCAL_DATE), Map.Entry::getValue));
        update.set("nexusCalculationSummaries", stringKeysSummaries);

        return update;
    }

    public Update buildSalesTaxTrackingUpdateOfPreviousTwelveMonths(SalesTaxTracking salesTaxTracking) {
        Update update = new Update();

        update.set("economicNexusTracker", salesTaxTracking.getEconomicNexusTracker());
        update.set("appliedDate", salesTaxTracking.getAppliedDate());

        return update;
    }

}