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
import com.complyt.domain.sales_tax.SalesTax;
import com.complyt.domain.sales_tax.SalesTaxRates;
import com.complyt.domain.sales_tax.product_classification.*;
import com.complyt.domain.sales_tax.zip_tax.Result;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.domain.transaction.*;
import com.complyt.domain.transaction.tax.ComplytGtRates;
import com.complyt.domain.transaction.tax.GtAddress;
import com.complyt.domain.transaction.tax.GtRates;
import com.complyt.v1.config.BodyCheckConfig;
import com.complyt.v1.mappers.ItemMapper;
import com.complyt.v1.models.*;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.CustomerTypeDto;
import com.complyt.v1.models.customer.exemption.*;
import com.complyt.v1.models.nexus.*;
import com.complyt.v1.models.sales_tax.ComplytSalesTaxRatesDto;
import com.complyt.v1.models.sales_tax.SalesTaxDto;
import com.complyt.v1.models.sales_tax.SalesTaxRatesDto;
import com.complyt.v1.models.sales_tax.gt.GtRatesDto;
import com.complyt.v1.models.transaction.*;
import com.complyt.v1.validators.body_checkers.transaction.*;
import org.springframework.data.mongodb.core.query.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.apache.el.lang.ELArithmetic.add;
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
        return new Address("Fresno", "US", "county", "CA", "7498 N Remington Ave", "93711-5508", "", false);
    }

    public static MandatoryAddressDto createAddressDtoInCalifornia() {
        return new MandatoryAddressDto("Fresno", "US", "county", "CA", "7498 N Remington Ave", "", "93711-5508", false);
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
                new Address("City", "Country", "County", "CA", "Street", "Zip", "", false),
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
                new OptionalAddressDto("City", "Country", "County", "CA", "Street", "", "Zip", false),
                null,
                CustomerTypeDto.RETAIL,
                internalTimeStamps,
                externalTimestamps,
                "comment"
        );
    }

    public Transaction createTransaction(String id) {
        String documentName = "INVUS1000";
        Address billingAddress = new Address("City", "USA", "County", "CA", "Street", "Zip", "", false);
        Address shippingAddress = new Address("City", "USA", "County", "CA", "Street", "Zip", "", false);
        List<Item> items = createItems(true, false, false);
        Timestamps timeStamps = new Timestamps(localDateTime, localDateTime);
        ShippingFee shippingFee = createShippingFee(true, false, false);
        String curreny = "USD";

        return new Transaction(UUID.randomUUID(), id, id, source,
                documentName, items, false, billingAddress, shippingAddress,
                customerIdOtherDomains, createCustomer(customerIdOtherDomains.toString()),
                null, TransactionStatus.ACTIVE, tenantId, timeStamps, timeStamps,
                TransactionType.INVOICE, shippingFee, null, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, TransactionFilingStatus.NOT_FILED, curreny, null);
    }

    public Transaction createGtTransaction(String id) {
        List<Item> itemsWithJurisdictionalGtTaxRules = createItems(false, true, false);
        String currency = "euro";
        return createTransaction(id)
                .withItems(itemsWithJurisdictionalGtTaxRules)
                .withCurrency(currency);
    }

    public TransactionDto createTransactionDto(String id) {
        String documentName = "INVUS1000";
        OptionalAddressDto billingAddress = new OptionalAddressDto("City", "USA", "County", "CA", "Street", "", "Zip", false);
        MandatoryAddressDto shippingAddress = new MandatoryAddressDto("City", "USA", "County", "CA", "Street", "", "Zip", false);
        List<ItemDto> items = createItemDtos(true, false, false);
        TimestampsDto timeStamps = new TimestampsDto(localDateTime.toString(), localDateTime.toString());
        ShippingFeeDto shippingFeeDto = createShippingFeeDto(true, false);
        return new TransactionDto(UUID.randomUUID(), id, source, documentName,
                items, false, billingAddress, shippingAddress, customerIdOtherDomains,
                createCustomerDto(customerIdOtherDomains.toString()), null,
                TransactionStatusDto.ACTIVE, timeStamps, timeStamps, TransactionTypeDto.INVOICE,
                shippingFeeDto, null, BigDecimal.ZERO, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, TransactionFilingStatusDto.NOT_FILED, "USD");
    }

    public List<Item> createItems(boolean withJurisdictionalSalesTaxRules, boolean withJurisdictionalGtTaxRules, boolean withTangibleCategory) {
        return new ArrayList<>() {{
            add(new Item(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), null,
                    "description", "name", "C1S1",
                    withJurisdictionalSalesTaxRules ? createJurisdictionalSalesTaxRules() : null,
                    withJurisdictionalGtTaxRules ? createJurisdictionalTaxRules() : null,
                    null, null, false, BigDecimal.ZERO, null,
                    withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));
            add(new Item(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), null,
                    "description", "name", "C3S1",
                    withJurisdictionalSalesTaxRules ? createJurisdictionalSalesTaxRules() : null,
                    withJurisdictionalGtTaxRules ? createJurisdictionalTaxRules() : null,
                    null, null, false, BigDecimal.ZERO, null,
                    withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));

        }};
    }

    public Item createItemWithNegativePrice(boolean withJurisdictionalSalesTaxRules, boolean withJurisdictionalGtTaxRules, boolean withTangibleCategory) {
        Item itemWithNegativePrice = createItems(withJurisdictionalSalesTaxRules, withJurisdictionalGtTaxRules, withTangibleCategory)
                .get(0)
                .withQuantity(BigDecimal.valueOf(4))
                .withUnitPrice(BigDecimal.valueOf(2000))
                .withTotalPrice(BigDecimal.valueOf(-8000));

        return itemWithNegativePrice;
    }

    public List<Item> createItemsWithSalesTaxRate(boolean withJurisdictionalSalesTaxRules, boolean withJurisdictionalGtTaxRules, boolean withTangibleCategory) {
        List<Item> Items = createItems(withJurisdictionalSalesTaxRules, withJurisdictionalGtTaxRules, withTangibleCategory);
        List<Item> itemsWithSalesTaxRate = new ArrayList<>() {{
            add(Items.get(0).withSalesTaxRates(createSalesTaxRates()));
            add(Items.get(1).withSalesTaxRates(createSalesTaxRates()));
        }};

        return itemsWithSalesTaxRate;
    }

    public List<Item> createItemsWithGstTaxRate(boolean withJurisdictionalSalesTaxRules, boolean withJurisdictionalGtTaxRules, boolean withTangibleCategory) {
//        return new ArrayList<>() {{
//            add(new Item(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), "description", "name", "C1S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRules() : null,
//                    createSalesTaxRates(), false, BigDecimal.ZERO, withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));
//            add(new Item(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), "description", "name", "C3S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRules() : null,
//                    createSalesTaxRates(), false, BigDecimal.ZERO, withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));
//
//        }};

        List<Item> Items = createItems(withJurisdictionalSalesTaxRules, withJurisdictionalGtTaxRules, withTangibleCategory);
        List<Item> itemsWithGtTaxRate = new ArrayList<>() {{
            add(Items.get(0).withGtRates(createGtRates()));
            add(Items.get(1).withGtRates(createGtRates()));
        }};

        return itemsWithGtTaxRate;
    }

    public SalesTax createSalesTaxWithAllFields() {
        return new SalesTax(BigDecimal.ZERO, BigDecimal.ZERO, createSalesTaxRates(), createGtRates());
    }

    public SalesTaxDto createSalesTaxDtoWithAllFields() {
        return new SalesTaxDto(BigDecimal.ZERO, BigDecimal.ZERO, createSalesTaxRatesDto(), createGtRatesDto());
    }


    public List<Item> setCalculatedTotalOnItemList(List<Item> items) {
        return items.stream()
                .map(item ->
                        item.getDiscount() != null ?
                                item.withCalculatedTotal(item.getTotalPrice()
                                        .subtract(item.getDiscount())) :
                                item.withCalculatedTotal(item.getTotalPrice())
                ).collect(Collectors.toList());
    }

    public ShippingFee setCalculatedTotalOnShippingFee(ShippingFee shippingFee) {
        return shippingFee.withCalculatedTotal(shippingFee.getTotalPrice());
    }

    public List<ItemDto> createItemDtosWithSalesTaxRate(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        return new ArrayList<>() {{
            add(new ItemDto(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), new BigDecimal(8000), "description", "name", "C1S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRulesDto() : null,
                    createSalesTaxRatesDto(), null, false, BigDecimal.ZERO, null, withTangibleCategory ? TangibleCategoryDto.TANGIBLE : null, TaxableCategoryDto.TAXABLE));
            add(new ItemDto(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), new BigDecimal(8000), "description", "name", "C3S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRulesDto() : null,
                    createSalesTaxRatesDto(), null, false, BigDecimal.ZERO, null, withTangibleCategory ? TangibleCategoryDto.TANGIBLE : null, TaxableCategoryDto.TAXABLE));
        }};
    }

    public List<ItemDto> createItemDtos(boolean withJurisdictionalSalesTaxRules, boolean withJurisdictionalGtTaxRules, boolean withTangibleCategory) {

        JurisdictionalSalesTaxRulesDto jurisdictionalSalesTaxRules = null;
        if (withJurisdictionalSalesTaxRules) {
            jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRulesDto();
        }
        if (withJurisdictionalGtTaxRules) {
            jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRulesDtoWithGt();
        }

        return new ArrayList<>() {{
            add(new ItemDto(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), BigDecimal.ZERO,
                    "description", "name", "C1S1",
                    withJurisdictionalSalesTaxRules || withJurisdictionalGtTaxRules ? createJurisdictionalSalesTaxRulesDto() : null, null, null,
                    false, BigDecimal.ZERO, null,
                    withTangibleCategory ? TangibleCategoryDto.TANGIBLE : null, TaxableCategoryDto.TAXABLE));
            add(new ItemDto(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), BigDecimal.ZERO,
                    "description", "name", "C3S1",
                    withJurisdictionalSalesTaxRules || withJurisdictionalGtTaxRules ? createJurisdictionalSalesTaxRulesDto() : null,
                    null, null, false, BigDecimal.ZERO, null,
                    withTangibleCategory ? TangibleCategoryDto.TANGIBLE : null, TaxableCategoryDto.TAXABLE));
        }};
    }

    public ItemDto createItemDtoWithNegativeAmount(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        return new ItemDto(new BigDecimal(-2000), new BigDecimal(4), new BigDecimal(-8000), null, "description", "name", "C1S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRulesDto() : null,
                null, null, false, BigDecimal.ZERO, null, withTangibleCategory ? TangibleCategoryDto.TANGIBLE : null, TaxableCategoryDto.TAXABLE);
    }

    public SalesTaxRates createSalesTaxRates() {
        return new SalesTaxRates(new BigDecimal("0.1"), new BigDecimal("0.1"), new BigDecimal("0.1"), new BigDecimal("0.4"), new BigDecimal("0.1"), null);
    }

    public GtRates createGtRates() {
        return new GtRates(new BigDecimal("0.1"), new BigDecimal("0.1"), new BigDecimal("0.2"));
    }

    public GtAddress createCanadaGtAddress() {
        return new GtAddress("Canada", "Quebec");
    }

    public GtAddress createArmeniaGtAddress() {
        return new GtAddress("Armenia", "Armenia");
    }

    public ComplytGtRates createComplytGtRates() {
        return new ComplytGtRates(createCanadaGtAddress(), createGtRates());
    }

    public SalesTaxRatesDto createSalesTaxRatesDto() {
        return new SalesTaxRatesDto(new BigDecimal("0.1"), new BigDecimal("0.1"), new BigDecimal("0.1"), new BigDecimal("0.4"), new BigDecimal("0.1"), null);
    }

    public GtRatesDto createGtRatesDto() {
        return new GtRatesDto(new BigDecimal("0.1"), new BigDecimal("0.1"), new BigDecimal("0.2"));
    }

    public ShippingFee createShippingFee(boolean withJurisdictionalSalesTaxRules, boolean withJurisdictionalGtTaxRules, boolean withTangibleCategory) {
        JurisdictionalSalesTaxRules salesTaxRules = createJurisdictionalSalesTaxRules();
        JurisdictionalTaxRules gtTaxRules = createJurisdictionalTaxRules();
        return new ShippingFee(false, BigDecimal.ZERO, new BigDecimal(1000),
                withJurisdictionalSalesTaxRules ? salesTaxRules : null,
                withJurisdictionalGtTaxRules ? gtTaxRules : null, null,
                null, "C6S1", TaxableCategory.TAXABLE,
                withTangibleCategory ? TangibleCategory.INTANGIBLE : null, null);
    }

    public ShippingFee createShippingFeeWithSalesTaxRates(boolean withJurisdictionalSalesTaxRules, boolean withTangibleCategory) {
        JurisdictionalSalesTaxRules salesTaxRules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, BigDecimal.ZERO, new BigDecimal(1000),
                withJurisdictionalSalesTaxRules ? salesTaxRules : null,
                null, createSalesTaxRates(),
                null, "C6S1", TaxableCategory.TAXABLE,
                withTangibleCategory ? TangibleCategory.INTANGIBLE : null, null);
    }

    public ShippingFee createShippingFeeWithGtTaxRates(boolean withJurisdictionalGtTaxRules, boolean withTangibleCategory) {
        JurisdictionalTaxRules gtRules = createJurisdictionalTaxRules();

        return createShippingFeeWithSalesTaxRates(false, withTangibleCategory)
                .withJurisdictionalTaxRules(withJurisdictionalGtTaxRules ? gtRules : null)
                .withSalesTaxRates(null)
                .withGtRates(createGtRates());
    }

    public ShippingFeeDto createShippingFeeDto(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        JurisdictionalSalesTaxRulesDto rules = createJurisdictionalSalesTaxRulesDto();
        return new ShippingFeeDto(false, BigDecimal.ZERO, new BigDecimal(1000), BigDecimal.ZERO, withJurisdictionalRules ? rules : null, null, null, "C6S1", TaxableCategoryDto.TAXABLE, withTangibleCategory ? TangibleCategoryDto.INTANGIBLE : null);
    }

    public ShippingFeeDto createShippingFeeGtRateDto(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        JurisdictionalSalesTaxRulesDto rules = createJurisdictionalSalesTaxRulesDto();
        return new ShippingFeeDto(false, BigDecimal.ZERO, new BigDecimal(1000), BigDecimal.ZERO, withJurisdictionalRules ? rules : null, null, null, "C6S1", TaxableCategoryDto.TAXABLE, withTangibleCategory ? TangibleCategoryDto.INTANGIBLE : null);
    }

    public JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", new BigDecimal("0.5"), null);
    }

    public JurisdictionalTaxRules createJurisdictionalTaxRules() {
        return new JurisdictionalTaxRules("Armenia", "ARM", true,
                false, CalculationType.FIXED, "description", new BigDecimal("0.5"), null);
    }

    public JurisdictionalSalesTaxRulesDto createJurisdictionalSalesTaxRulesDto() {
        return new JurisdictionalSalesTaxRulesDto("California", "CA", true,
                false, CalculationType.FIXED, "description", new BigDecimal("0.5"), null, null);
    }

    public JurisdictionalSalesTaxRulesDto createJurisdictionalSalesTaxRulesDtoWithCities() {
        return createJurisdictionalSalesTaxRulesDto()
                .withCities(new HashMap<String, SubJurisdictionalTaxRules>() {{
                    add("CA-city1", new SubJurisdictionalTaxRules("CA-city1", "CA-1",
                            true, false, CalculationType.FIXED, "CA-1", new BigDecimal("0.6")));
                }});
    }

    public JurisdictionalSalesTaxRulesDto createJurisdictionalSalesTaxRulesDtoWithGtAndRegion() {
        return createJurisdictionalSalesTaxRulesDtoWithGt()
                .withRegions(new HashMap<String, SubJurisdictionalTaxRules>() {{
                    add("Armenia-region1", new SubJurisdictionalTaxRules("Armenia-region1", "arm1",
                            true, false, CalculationType.FIXED, "armenia #1", new BigDecimal("0.6")));
                }});
    }

    public JurisdictionalSalesTaxRulesDto createJurisdictionalSalesTaxRulesDtoWithGt() {
        JurisdictionalTaxRules jurisdictionalTaxRules = new JurisdictionalTaxRules("Armenia", "ARM", true,
                false, CalculationType.FIXED, "armenia #1", new BigDecimal("0.6"), null);

        return ItemMapper.INSTANCE.combineJurisdictionalRules(null, jurisdictionalTaxRules);
    }

    public SubJurisdictionalTaxRules createCitySalesTaxRules() {
        return new SubJurisdictionalTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", new BigDecimal("0.5"));
    }

    public SubJurisdictionalTaxRules createRegionSalesTaxRules() {
        return new SubJurisdictionalTaxRules("Quebec", "QU", true,
                false, CalculationType.FIXED, "description", new BigDecimal("0.5"));
    }

    public Exemption createExemption(String id) {
        String country = "USA";
        State state = new State("CA", "02", "California");
        Classification classification = new Classification("code", "description");
        ValidationDates validationDates = new ValidationDates(localDateTime.minusYears(1), localDateTime.plusYears(1));
        Timestamps internalTimestamps = new Timestamps(localDateTime, localDateTime);
        Status status = new Status("code", "name");
        Certificate certificate = new Certificate(certificateId, "url", "name");
        return new Exemption(UUID.randomUUID(), id, tenantId, customerIdOtherDomains,
                country, state, classification, validationDates, internalTimestamps,
                status, certificate, ExemptionType.FULLY, ExemptionStatus.ACTIVE);
    }

    public ExemptionDto createExemptionDto() {
        String country = "USA";
        StateDto state = new StateDto("CA", "02", "California");
        ClassificationDto classification = new ClassificationDto("code", "description");
        ValidationDatesDto validationDates = new ValidationDatesDto(
                localDateTime.minusYears(1).toString(),
                localDateTime.plusYears(1).toString());
        TimestampsDto internalTimestamps = new TimestampsDto(localDateTime.toString(), localDateTime.toString());
        StatusDto status = new StatusDto("code", "name");
        CertificateDto certificate = new CertificateDto(certificateId, "url", "name");

        return new ExemptionDto(UUID.randomUUID(), customerIdOtherDomains,
                country, state, classification, validationDates, internalTimestamps, status, certificate, ExemptionTypeDto.FULLY, ExemptionStatusDto.ACTIVE);
    }

    public StateDto createNonUsStateDto() {
        return new StateDto("CN", "02", "Canada");
    }

    public NexusStateRule createNexusStateRule(String id) {
        String country = "USA";
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

        return new NexusStateRule(id, true, country, state, taxableCategories, tangibleCategories, customerTypes,
                TimeFrame.PREVIOUS_TWELVE_MONTHS, nexusThreshold, localDateTime);
    }

    public NexusStateRuleDto createNexusStateRuleDto() {
        String country = "USA";
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

        return new NexusStateRuleDto(true, country, state, taxableCategories, tangibleCategories, customerTypes, TimeFrameDto.PREVIOUS_TWELVE_MONTHS, nexusThreshold, localDateTime);
    }

    public List<Taxable> createTaxables(Transaction transaction) {
        List<Taxable> taxables = new ArrayList<>(transaction.getItems());
        taxables.add(transaction.getShippingFee());
        return taxables;
    }

    public SalesTaxTracking createSalesTaxTracking(String id) {
        State state = new State("CA", "02", "California");
        return new SalesTaxTracking(UUID.randomUUID(), id, "USA", state,
                tenantId, "comment", true,
                new PhysicalNexusTracker(false, localDateTime),
                new EconomicNexusTracker(false, localDateTime),
                createNexusStateRule(id + "nsr"),
                createClientTracking(tenantId),
                new HashMap<>(),
                new HashMap<>(),
                localDateTime,
                true, localDateTime,
                FilingFrequency.MONTHLY,
                null, null, null);
    }

    public SalesTaxTracking createSalesTaxTrackingGT(String id) {
        String country = "Armenia";
        State state = new State("ARM", "02", "Armenia");
        return new SalesTaxTracking(UUID.randomUUID(), id, country, state,
                tenantId, "comment", true,
                new PhysicalNexusTracker(false, localDateTime),
                new EconomicNexusTracker(false, localDateTime),
                createNexusStateRule(id + "nsr"),
                createClientTracking(tenantId),
                new HashMap<>(),
                new HashMap<>(),
                localDateTime,
                true, localDateTime,
                FilingFrequency.MONTHLY,
                null, null, null);
    }

    public ClientTracking createClientTracking(String tenantId) {
        Timestamps internalTimestamp = new Timestamps(localDateTime, localDateTime);
        return new ClientTracking(null, tenantId, new Nexus(localDateTime), "client dope", internalTimestamp, null);
    }

    public SalesTaxTrackingDto createSalesTaxTrackingDto() {
        String country = "USA";
        StateDto state = new StateDto("CA", "02", "California");
        SalesTaxTrackingDto salesTaxTrackingDto = new SalesTaxTrackingDto(UUID.randomUUID(), country, state,
                "comment", true,
                new PhysicalNexusTrackerDto(false, localDateTime),
                new EconomicNexusTrackerDto(false, localDateTime),
                Map.of(),
                createNexusStateRuleDto(),
                createClientTrackingDto(),
                localDateTime,
                true, localDateTime,
                FilingFrequencyDto.MONTHLY,
                null, null, new SubsidiaryDto("0", "subsidiary"));

        return salesTaxTrackingDto;
    }

    public SalesTaxTrackingDto createSalesTaxTrackingDtoGt() {
        String country = "Armenia";
        StateDto state = new StateDto("ARM", "02", "Armenia");
        SalesTaxTrackingDto salesTaxTrackingDto = new SalesTaxTrackingDto(UUID.randomUUID(), country, state,
                "comment", true,
                new PhysicalNexusTrackerDto(false, localDateTime),
                new EconomicNexusTrackerDto(false, localDateTime),
                Map.of(),
                createNexusStateRuleDto(),
                createClientTrackingDto(),
                localDateTime,
                true, localDateTime,
                FilingFrequencyDto.MONTHLY,
                null, null, null);

        return salesTaxTrackingDto;
    }


    public ClientTrackingDtoTenant createClientTrackingDtoTenant(String tenantId) {
        TimestampsDto internalTimestamps = new TimestampsDto(localDateTime.toString(), localDateTime.toString());
        return new ClientTrackingDtoTenant(new NexusDto(localDateTime), "client dope", internalTimestamps, tenantId);
    }

    public ClientTrackingDto createClientTrackingDto() {
        TimestampsDto internalTimestamps = new TimestampsDto(localDateTime.toString(), localDateTime.toString());
        return new ClientTrackingDto(new NexusDto(localDateTime), "client dope", internalTimestamps, null);
    }

    public Result createResult() {
        return new Result("", "", "injectedCounty", "", 0f, 0f, "", "",
                0f, 0f, 0f, 0f, "", 0f, 0, "",
                0f, 0f, "", 0, 0, "", 0,
                0, "", 0, 0, "", 0, 0, "",
                0, 0, "");
    }

    public Address createAddress() {
        return new Address("City", "Country", "County", "CA", "Street", "Zip", "region", false);
    }

    public Address createUsaAddress() {
        return new Address("Fresno", "USA", "County", "CA", "7498 Ave", "55591", "region", false);
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
                        new ItemsAlignmentChecker(),
                        new ItemHaveEitherTotalOrUnitPriceAndQuantityChecker(),
                        new NegativeItemsNotHavingDiscountChecker()
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

    public Map<String, ProductClassification> createUsaClassificationsMap(JurisdictionalSalesTaxRules firstRule, JurisdictionalSalesTaxRules secondRule) {
        Map<String, JurisdictionalSalesTaxRules> firstRulesMap = new HashMap<>() {{
            put(firstRule.getAbbreviation(), firstRule);
        }};
        Map<String, JurisdictionalSalesTaxRules> secondRulesMap = new HashMap<>() {{
            put(secondRule.getAbbreviation(), secondRule);
        }};
        ProductClassification productClassification1 = new ProductClassification("id", "C1S1", "description", "title", firstRulesMap, null, TangibleCategory.TANGIBLE);
        ProductClassification productClassification2 = new ProductClassification("id", "C3S1", "description", "title", secondRulesMap, null, TangibleCategory.TANGIBLE);

        return new HashMap<>() {{
            put(productClassification1.getTaxCode(), productClassification1);
            put(productClassification2.getTaxCode(), productClassification2);
        }};
    }

    public Map<String, ProductClassification> createNonUsaClassificationsMap(JurisdictionalTaxRules firstRule, JurisdictionalTaxRules secondRule) {
        Map<String, JurisdictionalTaxRules> firstRulesMap = new HashMap<>() {{
            put(firstRule.getAbbreviation(), firstRule);
        }};
        Map<String, JurisdictionalTaxRules> secondRulesMap = new HashMap<>() {{
            put(secondRule.getAbbreviation(), secondRule);
        }};

        ProductClassification productClassification1 = new ProductClassification("id", "C1S1", "description", "title", null, firstRulesMap, TangibleCategory.TANGIBLE);
        ProductClassification productClassification2 = new ProductClassification("id", "C3S1", "description", "title", null, secondRulesMap, TangibleCategory.TANGIBLE);

        return new HashMap<>() {{
            put(productClassification1.getTaxCode(), productClassification1);
            put(productClassification2.getTaxCode(), productClassification2);
        }};
    }

    public Map<String, ProductClassification> createNonUsaShippingFeeClassificationsMap(JurisdictionalTaxRules jurisdictionalTaxRules) {
        Map<String, JurisdictionalTaxRules> ruleMap = new HashMap<>() {{
            put(jurisdictionalTaxRules.getAbbreviation(), jurisdictionalTaxRules);
        }};

        ProductClassification productClassification = new ProductClassification("id", "C6S1", "description", "title", null, ruleMap, TangibleCategory.TANGIBLE);

        return new HashMap<>() {{
            put(productClassification.getTaxCode(), productClassification);
        }};
    }

    public Map<String, ProductClassification> createUsaShippingFeeClassificationsMap(JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules) {
        Map<String, JurisdictionalSalesTaxRules> ruleMap = new HashMap<>() {{
            put(jurisdictionalSalesTaxRules.getAbbreviation(), jurisdictionalSalesTaxRules);
        }};

        ProductClassification productClassification = new ProductClassification("id", "C6S1", "description", "title", ruleMap, null, TangibleCategory.TANGIBLE);

        return new HashMap<>() {{
            put(productClassification.getTaxCode(), productClassification);
        }};
    }

    public Address createNonUsaAddress() {
        return new Address(null, "ARM", null, null, null, null, null, false);
    }

    public Map<String, ProductClassification> createMapTaxCodesToClassifications() {
        JurisdictionalSalesTaxRules jurisdictionalSalesTaxRules = createJurisdictionalSalesTaxRules();
        JurisdictionalTaxRules jurisdictionalTaxRules = createJurisdictionalTaxRules();
        Map<String, JurisdictionalSalesTaxRules> item1JurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};

        Map<String, JurisdictionalTaxRules> item1JurisdictionalTaxRulesMap = new HashMap<>() {{
            put("ARM", jurisdictionalTaxRules);
        }};

        ProductClassification item1ProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C1S1", "item", "title", item1JurisdictionalSalesTaxRulesMap, item1JurisdictionalTaxRulesMap, TangibleCategory.TANGIBLE);
        Map<String, JurisdictionalSalesTaxRules> item2JurisdictionalSalesTaxRulesMap = new HashMap<>() {{
            put("CA", jurisdictionalSalesTaxRules);
        }};

        Map<String, JurisdictionalTaxRules> item2JurisdictionalTaxRulesMap = new HashMap<>() {{
            put("ARM", jurisdictionalTaxRules);
        }};

        ProductClassification item2ProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C2S1", "item", "title", item2JurisdictionalSalesTaxRulesMap, item2JurisdictionalTaxRulesMap, TangibleCategory.TANGIBLE);
        ProductClassification item3ProductClassification = new ProductClassification(UUID.randomUUID().toString()
                , "C3S1", "item", "title", item1JurisdictionalSalesTaxRulesMap, item1JurisdictionalTaxRulesMap, TangibleCategory.TANGIBLE);

        return new HashMap<>() {{
            put("C1S1", item1ProductClassification);
            put("C6S1", item2ProductClassification);
            put("C3S1", item3ProductClassification);
        }};
    }
}