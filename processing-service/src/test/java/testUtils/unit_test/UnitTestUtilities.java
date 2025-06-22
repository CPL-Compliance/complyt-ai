package testUtils.unit_test;

import io.complyt.domain.*;
import io.complyt.domain.audit.Action;
import io.complyt.domain.currency.CurrencyExchangeRateObject;
import io.complyt.domain.currency.CurrencySource;
import io.complyt.domain.customer.Customer;
import io.complyt.domain.customer.CustomerStatus;
import io.complyt.domain.customer.CustomerType;
import io.complyt.domain.customer.exemption.*;
import io.complyt.domain.enums.FieldMatchType;
import io.complyt.domain.enums.FieldsMatchScore;
import io.complyt.domain.enums.MatchLevelType;
import io.complyt.domain.nexus.*;
import io.complyt.domain.nexus.enums.Definition;
import io.complyt.domain.nexus.enums.TangibleCategory;
import io.complyt.domain.nexus.enums.TaxableCategory;
import io.complyt.domain.nexus.enums.TimeFrame;
import io.complyt.domain.sales_tax.ComplytSalesTaxRates;
import io.complyt.domain.sales_tax.FilingMetaData;
import io.complyt.domain.sales_tax.SalesTax;
import io.complyt.domain.sales_tax.SalesTaxRates;
import io.complyt.domain.sales_tax.product_classification.*;
import io.complyt.domain.sales_tax.zip_tax.Result;
import io.complyt.domain.timestamps.Timestamps;
import io.complyt.domain.transaction.*;
import io.complyt.domain.transaction.tax.ComplytGtRates;
import io.complyt.domain.transaction.tax.GtAddress;
import io.complyt.domain.transaction.tax.GtRates;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


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

    public static ShippingAddress createAddressInNewYork() {
        return new ShippingAddress("New York City", "US", "county", "NY", "160 Broadway", "", "10038", false, null);
    }


    public static ShippingAddress createShippingAddressInCalifornia1() {
        return new ShippingAddress("Fresno", "US", "county", "CA", "7498 N Remington Ave", "", "93711-5508", false, null);
    }

    public static MandatoryAddress createShippingAddressInCalifornia() {
        return new MandatoryAddress("Fresno", "US", "county", "CA", "7498 N Remington Ave", "", "93711-5508", false);
    }

    public static MatchedAddressData createMatchedAddressData() {
        MandatoryAddress mandatoryAddress = new MandatoryAddress("Fresno", "US", "county", "CA", "7498 N Remington Ave", "", "93711-5508", false);
        return new MatchedAddressData(mandatoryAddress, createScoring());
    }

    public static Scoring createScoring() {
        return new Scoring(MatchLevelType.EXCELLENT, 1, new FieldsMatchScore(FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT));
    }

    public static SalesTaxRates createCaliforniaSalesTaxRates() {
        return new SalesTaxRates(
                new BigDecimal("0.06"), // stateRate
                new BigDecimal("0.0125"), // countyRate
                new BigDecimal("0.005"), // cityRate
                new BigDecimal("0"), // combinedDistrictRate
                null, // ratesMetaData
                null, // mtaRate
                null, // spdRate
                null, // otherRate
                new BigDecimal("0.0775") // taxRate
        );
    }

    public static SalesTaxRates createInternalCaliforniaSalesTaxRates() {
        return new SalesTaxRates(
                new BigDecimal("0.06"), // stateRate
                new BigDecimal("0.0125"), // countyRate
                new BigDecimal("0.005"), // cityRate
                null, // combinedDistrictRate
                null, // ratesMetaData
                new BigDecimal(0), // mtaRate
                new BigDecimal(0), // spdRate
                new BigDecimal(0), // otherRate
                new BigDecimal("0.0775") // taxRate
        );
    }

    public static ComplytSalesTaxRates createCaliforniaComplytSalesTaxRates() {
        MatchedAddressData address = createMatchedAddressData();
        SalesTaxRates salesTaxRates = createCaliforniaSalesTaxRates();
        FilingMetaData filingMetaData = createFilingMetaData();
        return new ComplytSalesTaxRates(null, address, salesTaxRates, filingMetaData);
    }

    public static FilingMetaData createFilingMetaData() {
        return new FilingMetaData(
                null,                        // cityName
                "Fresno",                    // countyName
                null,                        // other1Rate
                null,                        // other2Rate
                null,                        // other3Rate
                null,                        // other4Rate
                "06",                        // countyRptCode
                "037",                       // cityRptCode
                "mtaName",                   // mtaName
                "0603744000",                // mtaNumber
                "spdName",                   // spdName
                "100000",                    // spdNumber
                "other1Name",               // other1Name
                "2%",                        // other1Number
                "other2Name",               // other2Name
                "50000",                     // other2Number
                "other3Name",               // other3Name
                "1%",                        // other3Number
                "other4Name",               // other4Name
                "Yes",                       // other4Number
                "037"                        // fipsCounty
        );
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

    public static List<Exemption> createExemptionsListFromWrapper(ExemptionWrapper exemptionWrapper) {
        List<Exemption> exemptionList = new ArrayList<>();
        for (State state : exemptionWrapper.states()) {
            exemptionList.add(exemptionWrapper.exemption().withState(state));
        }

        return exemptionList;
    }

    public static List<Exemption> createNonUsaExemptionsListFromWrapper(ExemptionWrapper exemptionWrapper) {
        return new ArrayList<>() {{
            add(exemptionWrapper.exemption().withState(null));
        }};
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
        CustomerStatus customerStatus = CustomerStatus.ACTIVE;

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
                "comment",
                customerStatus
        );
    }

    public Customer createCustomerProjection(String id) {
        /**
         * keeping in customer:
         * complytId
         * name
         * externalId
         * source
         * customerType
         * externalTimeStamps
         *
         */
        return createCustomer(id)
                .withAddress(null)
                .withInternalTimestamps(null);
    }

    public Transaction createTransaction(String id) {
        String documentName = "INVUS1000";
        Address billingAddress = new Address("City", "USA", "County", "CA", "Street", "10000", "", false);
        ShippingAddress shippingAddress = new ShippingAddress("City", "USA", "County", "CA", "Street", "", "10000", false, null);
        List<Item> items = createItems(true, false, false);
        Timestamps timeStamps = new Timestamps(localDateTime, localDateTime);
        ShippingFee shippingFee = createShippingFee(true, false, false);

        return new Transaction(UUID.randomUUID(), id, id, source,
                documentName, items, false, billingAddress, shippingAddress,
                customerIdOtherDomains, createCustomer(customerIdOtherDomains.toString()),
                null, TransactionStatus.ACTIVE, tenantId, timeStamps, timeStamps,
                TransactionType.INVOICE/*shippingFee*/, null, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, TransactionFilingStatus.NOT_FILED, null, null, null, null, false, null);
    }

    public Transaction createTransactionProjectionAfterProjection(String id) {
        String documentName = "INVUS1000";
        Address billingAddress = new Address("City", "USA", "County", "CA", "Street", "Zip", "", false);
        ShippingAddress shippingAddress = new ShippingAddress("City", "USA", "County", "CA", "Street", "", "Zip", false, null);
        List<Item> items = createItems(false, false, true);
        Timestamps timeStamps = new Timestamps(localDateTime, localDateTime);
        ShippingFee shippingFee = createShippingFee(false, false, false);
        String currency = "USD";

        return new Transaction(UUID.randomUUID(), id, id, source,
                documentName, items, false, billingAddress, shippingAddress,
                customerIdOtherDomains, createCustomerProjection(customerIdOtherDomains.toString()),
                null, TransactionStatus.ACTIVE, tenantId, timeStamps, timeStamps,
                TransactionType.INVOICE/*shippingFee*/, null, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, TransactionFilingStatus.NOT_FILED,
                null, null, null, null, false, BigDecimal.ZERO);
    }

    public Transaction createTransactionWithCalculatedTotalItem(String id) {
        String documentName = "INVUS1000";
        Address billingAddress = new Address("City", "USA", "County", "CA", "Street", "Zip", "", false);
        ShippingAddress shippingAddress = new ShippingAddress("City", "USA", "County", "CA", "Street", "", "Zip", false, null);
        List<Item> items = createItemsWithCalculatedTotal(true, false, false);
        Timestamps timeStamps = new Timestamps(localDateTime, localDateTime);
        ShippingFee shippingFee = createShippingFee(true, false, false);
        String currency = "USD";

        return new Transaction(UUID.randomUUID(), id, id, source,
                documentName, items, false, billingAddress, shippingAddress,
                customerIdOtherDomains, createCustomer(customerIdOtherDomains.toString()),
                null, TransactionStatus.ACTIVE, tenantId, timeStamps, timeStamps,
                TransactionType.INVOICE/*shippingFee*/, null, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, TransactionFilingStatus.NOT_FILED, currency, null, null, null, false, null);
    }

    public Transaction createTransactionWithThreeItemsAndCalculatedTotal(String id) {
        String documentName = "INVUS1000";
        Address billingAddress = new Address("City", "USA", "County", "CA", "Street", "Zip", "", false);
        ShippingAddress shippingAddress = new ShippingAddress("City", "USA", "County", "CA", "Street", "", "Zip", false, null);
        List<Item> items = createThreeItemsWithCalculatedTotal(true, false, false);
        Timestamps timeStamps = new Timestamps(localDateTime, localDateTime);
        ShippingFee shippingFee = createShippingFee(true, false, false);
        String currency = "USD";

        return new Transaction(UUID.randomUUID(), id, id, source,
                documentName, items, false, billingAddress, shippingAddress,
                customerIdOtherDomains, createCustomer(customerIdOtherDomains.toString()),
                null, TransactionStatus.ACTIVE, tenantId, timeStamps, timeStamps,
                TransactionType.INVOICE/*shippingFee*/, null, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, TransactionFilingStatus.NOT_FILED, currency, null, null, null, false, null);
    }

    public Transaction createGtTransaction(String id) {
        List<Item> itemsWithJurisdictionalGtTaxRules = createItems(false, true, false);
        String currency = "euro";
        return createTransaction(id)
                .withItems(itemsWithJurisdictionalGtTaxRules)
                .withCurrency(currency);
    }

    public List<Item> createItems(boolean withJurisdictionalSalesTaxRules, boolean withJurisdictionalGtTaxRules, boolean withTangibleCategory) {
        return new ArrayList<>() {{
            add(new Item(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), null,
                    "description", "name", "C1S1",
                    withJurisdictionalSalesTaxRules ? createJurisdictionalSalesTaxRules() : null,
                    withJurisdictionalGtTaxRules ? createJurisdictionalTaxRules() : null,
                    null, null, false, BigDecimal.ZERO, null, null,
                    withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));
            add(new Item(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), null,
                    "description", "name", "C3S1",
                    withJurisdictionalSalesTaxRules ? createJurisdictionalSalesTaxRules() : null,
                    withJurisdictionalGtTaxRules ? createJurisdictionalTaxRules() : null,
                    null, null, false, BigDecimal.ZERO, null, null,
                    withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));

        }};
    }

    public List<Item> createItemsWithCalculatedTotal(boolean withJurisdictionalSalesTaxRules, boolean withJurisdictionalGtTaxRules, boolean withTangibleCategory) {
        return new ArrayList<>() {{
            add(new Item(new BigDecimal(500), new BigDecimal(3), new BigDecimal(1500), new BigDecimal(1500),
                    "description", "name", "C3S1",
                    withJurisdictionalSalesTaxRules ? createJurisdictionalSalesTaxRules() : null,
                    withJurisdictionalGtTaxRules ? createJurisdictionalTaxRules() : null,
                    null, null, false, BigDecimal.ZERO, null, null,
                    withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));
            add(new Item(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), new BigDecimal(8000),
                    "description", "name", "C1S1",
                    withJurisdictionalSalesTaxRules ? createJurisdictionalSalesTaxRules() : null,
                    withJurisdictionalGtTaxRules ? createJurisdictionalTaxRules() : null,
                    null, null, false, BigDecimal.ZERO, null, null,
                    withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));
        }};
    }

    public List<Item> createThreeItemsWithCalculatedTotal(boolean withJurisdictionalSalesTaxRules, boolean withJurisdictionalGtTaxRules, boolean withTangibleCategory) {
        return new ArrayList<>() {{
            add(new Item(new BigDecimal(500), new BigDecimal(3), new BigDecimal(1500), new BigDecimal(1500),
                    "description", "name", "C3S1",
                    withJurisdictionalSalesTaxRules ? createJurisdictionalSalesTaxRules() : null,
                    withJurisdictionalGtTaxRules ? createJurisdictionalTaxRules() : null,
                    null, null, false, BigDecimal.ZERO, null, null,
                    withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));
            add(new Item(new BigDecimal(2000), new BigDecimal(4), new BigDecimal(8000), new BigDecimal(8000),
                    "description", "name", "C1S1",
                    withJurisdictionalSalesTaxRules ? createJurisdictionalSalesTaxRules() : null,
                    withJurisdictionalGtTaxRules ? createJurisdictionalTaxRules() : null,
                    null, null, false, BigDecimal.ZERO, null, null,
                    withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));
            add(new Item(new BigDecimal(1000), new BigDecimal(8), new BigDecimal(8000), new BigDecimal(8000),
                    "description", "name", "C1S1",
                    withJurisdictionalSalesTaxRules ? createJurisdictionalSalesTaxRules() : null,
                    withJurisdictionalGtTaxRules ? createJurisdictionalTaxRules() : null,
                    null, null, false, BigDecimal.ZERO, null, null,
                    withTangibleCategory ? TangibleCategory.TANGIBLE : null, TaxableCategory.TAXABLE));
        }};
    }

    public Item createItemWithNegativePrice(boolean withJurisdictionalSalesTaxRules, boolean withJurisdictionalGtTaxRules, boolean withTangibleCategory) {

        return createItems(withJurisdictionalSalesTaxRules, withJurisdictionalGtTaxRules, withTangibleCategory)
                .get(0)
                .withQuantity(BigDecimal.valueOf(4))
                .withUnitPrice(BigDecimal.valueOf(2000))
                .withTotalPrice(BigDecimal.valueOf(-8000));
    }

    public List<Item> createItemsWithSalesTaxRate(boolean withJurisdictionalSalesTaxRules, boolean withJurisdictionalGtTaxRules, boolean withTangibleCategory) {
        List<Item> Items = createItems(withJurisdictionalSalesTaxRules, withJurisdictionalGtTaxRules, withTangibleCategory);

        return new ArrayList<>() {{
            add(Items.get(0).withSalesTaxRates(createSalesTaxRates()));
            add(Items.get(1).withSalesTaxRates(createSalesTaxRates()));
        }};
    }

    public List<Taxable> createTaxablesWithSalesTaxRate(boolean withJurisdictionalSalesTaxRules, boolean withJurisdictionalGtTaxRules, boolean withTangibleCategory) {
        List<Item> Items = createItems(withJurisdictionalSalesTaxRules, withJurisdictionalGtTaxRules, withTangibleCategory);

        return new ArrayList<>() {{
            add(Items.get(0).withCalculatedTotal(BigDecimal.valueOf(1000)).withSalesTaxRates(createSalesTaxRates()));
            add(Items.get(1).withCalculatedTotal(BigDecimal.valueOf(1000)).withSalesTaxRates(createSalesTaxRates()));
        }};
    }

    public List<Item> createItemsWithGstTaxRate(boolean withJurisdictionalSalesTaxRules, boolean withJurisdictionalGtTaxRules, boolean withTangibleCategory) {

        List<Item> Items = createItems(withJurisdictionalSalesTaxRules, withJurisdictionalGtTaxRules, withTangibleCategory);

        return new ArrayList<>() {{
            add(Items.get(0).withGtRates(createGtRates()));
            add(Items.get(1).withGtRates(createGtRates()));
        }};
    }

    public SalesTax createSalesTaxWithAllFields() {
        return new SalesTax(null, BigDecimal.ZERO, BigDecimal.ZERO, createSalesTaxRates(), createGtRates(), null);
    }

    public SalesTax createSalesTaxWithAmount(BigDecimal amount) {
        return new SalesTax(null, amount, BigDecimal.ZERO, createSalesTaxRates(), createGtRates(), null);
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

    public List<Item> setCalculatedTotalAndRelativeDiscountOnItemsList(List<Item> items, BigDecimal relativeDiscountPercentageForNewAmountCalculation) {
        return items.stream()
                .map(item ->
                        item.withRelativeTransactionDiscount(removeTrailingZeros(item.getCalculatedTotal().multiply(relativeDiscountPercentageForNewAmountCalculation)))
                                .withCalculatedTotal(removeTrailingZeros(item.getCalculatedTotal().subtract(item.getCalculatedTotal().multiply(relativeDiscountPercentageForNewAmountCalculation))))
                ).collect(Collectors.toList());
    }

    private BigDecimal removeTrailingZeros(BigDecimal integer) {
        return new BigDecimal(integer.stripTrailingZeros().toPlainString());
    }

    public ShippingFee setCalculatedTotalOnShippingFee(ShippingFee shippingFee) {
        return shippingFee.withCalculatedTotal(shippingFee.getTotalPrice());
    }

    public SalesTaxRates createSalesTaxRates() {
        return new SalesTaxRates(new BigDecimal("0.1"), new BigDecimal("0.1"), new BigDecimal("0.1"), BigDecimal.ZERO, null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("0.4"));
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

    public JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", new BigDecimal("0.5"), null);
    }

    public JurisdictionalTaxRules createJurisdictionalTaxRules() {
        return new JurisdictionalTaxRules("Armenia", "ARM", true,
                false, CalculationType.FIXED, "description", new BigDecimal("0.5"), null);
    }

    public SubJurisdictionalTaxRules createCitySalesTaxRules() {
        return new SubJurisdictionalTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", new BigDecimal("0.5"));
    }

    public SubJurisdictionalTaxRules createRegionSalesTaxRules() {
        return new SubJurisdictionalTaxRules("Quebec", "QU", true,
                false, CalculationType.FIXED, "description", new BigDecimal("0.5"));
    }

    public ExemptionWrapper createExemptionWrapper(String id) {
        return new ExemptionWrapper(createExemption(id), List.of(new State(
                "CO", "04", "Colorado"
        )));
    }

    public Exemption createNonUsaExemption(String id) {
        Exemption exemption = createExemption(id);

        return exemption.withCountry("CANADA").withState(null);
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
                status, certificate, ExemptionType.FULLY, ExemptionStatus.ACTIVE, null);
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

    public List<Taxable> createTaxables(Transaction transaction) {
        List<Taxable> taxables = new ArrayList<>(transaction.getItems());
//        taxables.add(transaction.getShippingFee());
        return taxables;
    }

    public SalesTaxTracking createSalesTaxTracking(String id) {
        State state = new State("CA", "02", "California");
        return new SalesTaxTracking(UUID.randomUUID(), id, "USA", state,
                tenantId, "comment", true,
                new PhysicalNexusTracker(false, localDateTime),
                EconomicNexusTracker.build(),
                createNexusStateRule(id + "nsr"),
                createClientTracking(tenantId),
                new HashMap<>(),
                new HashMap<>(),
                localDateTime,
                true, localDateTime,
                FilingFrequency.MONTHLY,
                null, null, null, null);
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
                null, null, null, null);
    }

    public ClientTracking createClientTracking(String tenantId) {
        Timestamps internalTimestamp = new Timestamps(localDateTime, localDateTime);
        return new ClientTracking(null, tenantId, new Nexus(localDateTime), "client dope", internalTimestamp, null
                , null);
    }

    public Result createResult() {
        return new Result("", "", "injectedCounty", "", 0f, 0f, "", "",
                0f, 0f, 0f, 0f, "", 0f, 0, "",
                0f, 0f, "", 0, 0, "", 0,
                0, "", 0, 0, "", 0, 0, "",
                0, 0, "");
    }

    public String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1).toLowerCase();
    }

    public Address createAddress() {
        return new Address("City", "Country", "County", "CA", "Street", "Zip", "region", false);
    }

    public ShippingAddress createShippingAddress() {
        return new ShippingAddress("City", "Country", "County", "CA", "Street", "region", "Zip", false, null);
    }

    public Address createUsaAddress() {
        return new Address("Fresno", "USA", "County", "CA", "7498 Ave", "55591", "region", false);
    }

    public ShippingAddress createUsaShippingAddress() {
        return new ShippingAddress("Fresno", "USA", "County", "CA", "7498 Ave", "region", "55591", false, null);
    }

    public Timestamps createTimestamps() {
        return new Timestamps(localDateTime.minusYears(1), localDateTime);
    }

    public ValidationDates createValidationDates() {
        return new ValidationDates(localDateTime.minusYears(1), localDateTime);
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
        return new Address(null, "ARM", null, null, null, "12345", null, false);
    }

    public ShippingAddress createNonUsaShippingAddress() {
        return new ShippingAddress(null, "ARM", null, null, null, null, "12345", false, null);
    }

    public ShippingAddress createNonUsaShippingAddressWithMatchedAddress() {
        return new ShippingAddress(null, "ARM", null, null, null, null, "12345", false, createNonUsaMatchedAddress());
    }

    public ShippingAddress createUsaShippingAddressWithMatchedAddress() {
        return new ShippingAddress(null, "USA", null, "California", null, null, "12345", false, createUsaMatchedAddress());
    }

    public MatchedAddressData createNonUsaMatchedAddress() {
        return new MatchedAddressData(new MandatoryAddress(null, "ARM", null, null, null, null, "12345", false), null);
    }

    public MatchedAddressData createUsaMatchedAddress() {
        return new MatchedAddressData(new MandatoryAddress(null, "USA", null, "California", null, null, "12345", false), null);
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

    public CurrencyExchangeRateObject createCurrencyExchangeRateObject(String currency, LocalDateTime date, BigDecimal rate) {
        return new CurrencyExchangeRateObject(currency, date, rate);
    }

    public ExchangeRateInfo createExchangeRateInfo(BigDecimal totalItemsAmountInUSD, BigDecimal transactionSalesTaxInUsd, BigDecimal finalTransactionAmountInUsd, String fromCurrency, String toCurrency, BigDecimal fxRate, CurrencySource source, Boolean isExchangeRateEstimated, LocalDateTime exchangeRateDate) {
        return new ExchangeRateInfo(totalItemsAmountInUSD, transactionSalesTaxInUsd, finalTransactionAmountInUsd, fromCurrency, toCurrency, fxRate, source, isExchangeRateEstimated, exchangeRateDate);
    }

    public ExchangeRateInfo createNotTaxableEuroExchangeRateInfo(Transaction transaction) {
        BigDecimal itemsAmount = BigDecimal.valueOf(1107.31);
        BigDecimal tax = BigDecimal.ZERO;
        BigDecimal finalTransactionAmount = itemsAmount.add(tax);

        return new ExchangeRateInfo(itemsAmount, tax, finalTransactionAmount, "EUR", "USD", BigDecimal.valueOf(1.10731), CurrencySource.COMPLYT, false, transaction.getExternalTimestamps().getCreatedDate());
    }


    public CurrencyExchangeRateObject createEuroCurrencyExchangeRateObject() {
        return new CurrencyExchangeRateObject("EUR", LocalDateTime.now(), BigDecimal.valueOf(1.10731));
    }


    public ValidatedVat createValidatedVat() {
        //todo: put a const time

        return new ValidatedVat("BE", "Belgium", "0835221567",
                true, "BV BE³-PROJECTS", "Kasteeldreef 9\\n2940 Stabroek", new Timestamps(LocalDateTime.now(), LocalDateTime.now()));
    }

    public ValidatedVat createValidatedVat(LocalDateTime created, LocalDateTime updated) {
        return createValidatedVat().withInternalTimestamps(new Timestamps(created, updated));
    }

    public VatDetailsToValidate createVatDetailsToValidate() {

        return new VatDetailsToValidate("BE", "0835221567");
    }

    public WebhookEntityWrapper<Transaction> createWebhookEntityWrapper() {
        return new WebhookEntityWrapper<>(
                UUID.randomUUID(),
                LocalDateTime.now(),
                Action.CREATE,
                Transaction.class.getSimpleName(),
                createTransaction(UUID.randomUUID().toString()),
                "host",
                "path"
        );
    }

    public WebhookDetails createWebhookDetails() {
        return new WebhookDetails(false, null, null);
    }
}