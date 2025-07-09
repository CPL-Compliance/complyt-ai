package testUtils.unit_test;

import io.complyt.domain.*;
import io.complyt.domain.audit.Action;
import io.complyt.domain.customer.Customer;
import io.complyt.domain.customer.CustomerStatus;
import io.complyt.domain.customer.CustomerType;
import io.complyt.domain.nexus.*;
import io.complyt.domain.nexus.enums.Definition;
import io.complyt.domain.nexus.enums.TangibleCategory;
import io.complyt.domain.nexus.enums.TaxableCategory;
import io.complyt.domain.nexus.enums.TimeFrame;
import io.complyt.domain.sales_tax.SalesTax;
import io.complyt.domain.sales_tax.SalesTaxRates;
import io.complyt.domain.sales_tax.product_classification.*;
import io.complyt.domain.timestamps.Timestamps;
import io.complyt.domain.transaction.*;
import io.complyt.domain.transaction.tax.GtRates;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;


public class UnitTestUtilities {

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

    public static List<State> createStateList() {
        return new ArrayList<>() {{
            add(new State("California", "04", "CA"));
            add(new State("New York", "05", "NY"));
            add(new State("Arizona", "06", "AZ"));
        }};
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
                TransactionType.INVOICE, shippingFee, null, BigDecimal.ZERO,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, TransactionFilingStatus.NOT_FILED, null, null, null, null, false, null);
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

    public List<Item> createItemsWithSalesTaxRate(boolean withJurisdictionalSalesTaxRules, boolean withJurisdictionalGtTaxRules, boolean withTangibleCategory) {
        List<Item> Items = createItems(withJurisdictionalSalesTaxRules, withJurisdictionalGtTaxRules, withTangibleCategory);

        return new ArrayList<>() {{
            add(Items.get(0).withSalesTaxRates(createSalesTaxRates()));
            add(Items.get(1).withSalesTaxRates(createSalesTaxRates()));
        }};
    }

    public SalesTax createSalesTaxWithAllFields() {
        return new SalesTax(null, BigDecimal.ZERO, BigDecimal.ZERO, createSalesTaxRates(), createGtRates(), null);
    }

    public SalesTaxRates createSalesTaxRates() {
        return new SalesTaxRates(new BigDecimal("0.1"), new BigDecimal("0.1"), new BigDecimal("0.1"), BigDecimal.ZERO, null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("0.4"));
    }

    public GtRates createGtRates() {
        return new GtRates(new BigDecimal("0.1"), new BigDecimal("0.1"), new BigDecimal("0.2"));
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

    public JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", new BigDecimal("0.5"), null);
    }

    public JurisdictionalTaxRules createJurisdictionalTaxRules() {
        return new JurisdictionalTaxRules("Armenia", "ARM", true,
                false, CalculationType.FIXED, "description", new BigDecimal("0.5"), null);
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

    public ClientTracking createClientTracking(String tenantId) {
        Timestamps internalTimestamp = new Timestamps(localDateTime, localDateTime);
        return new ClientTracking(null, tenantId, new Nexus(localDateTime), "client dope", internalTimestamp, null
                , null);
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