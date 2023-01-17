package testUtils;

import com.complyt.domain.*;
import com.complyt.domain.customer.Customer;
import com.complyt.domain.customer.CustomerType;
import com.complyt.domain.customer.exemption.*;
import com.complyt.domain.nexus.*;
import com.complyt.domain.nexus.enums.Definition;
import com.complyt.domain.nexus.enums.TangibleCategory;
import com.complyt.domain.nexus.enums.TaxableCategory;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.domain.sales_tax.SalesTaxRate;
import com.complyt.domain.sales_tax.product_classification.CalculationType;
import com.complyt.domain.sales_tax.product_classification.JurisdictionalSalesTaxRules;
import com.complyt.domain.sales_tax.zip_tax.Result;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.v1.model.*;
import com.complyt.v1.model.customer.CustomerDto;
import com.complyt.v1.model.customer.CustomerTypeDto;
import com.complyt.v1.model.customer.exemption.*;
import com.complyt.v1.model.timestamps.ComplytTimestampDto;
import com.complyt.v1.model.timestamps.TimestampsDto;
import org.bson.types.ObjectId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DomainObjectStub {

    ComplytTimestamp complytTimestamp;
    ComplytTimestampDto complytTimestampDto;
    String tenantId;

    UUID customerIdOtherDomains;

    String certificateId;

    String source;

    public DomainObjectStub(ComplytTimestamp complytTimestamp, String tenantId) {
        this.complytTimestamp = complytTimestamp;
        this.complytTimestampDto = new ComplytTimestampDto(complytTimestamp.getTimestamp().toString());
        this.tenantId = tenantId;
        customerIdOtherDomains = UUID.randomUUID();
        certificateId = UUID.randomUUID().toString();
        source = String.valueOf(Math.round(Math.random()*10));
    }

    public String getUnifiedSource() {
        return source;
    };

    public Customer createCustomer(String id) {
        Timestamps internalTimeStamps = new Timestamps(complytTimestamp, complytTimestamp);
        ComplytTimestamp complytTimestampMinusOneMinute = new ComplytTimestamp(complytTimestamp.getTimestamp().minusMinutes(1));
        Timestamps externalTimestamps = new Timestamps(complytTimestampMinusOneMinute, complytTimestamp);
        return new Customer(
                UUID.randomUUID(),
                id,
                id,
                source,
                "name",
                new Address("City", "Country", "County", "CA", "Street", "Zip"),
                tenantId,
                CustomerType.RETAIL,
                internalTimeStamps,
                externalTimestamps
        );
    }

    public CustomerDto createCustomerDto(String id) {
        TimestampsDto internalTimeStamps = new TimestampsDto(complytTimestampDto, complytTimestampDto);
        ComplytTimestampDto complytTimestampMinusOneMinute = new ComplytTimestampDto(complytTimestampDto.getTimestamp().minusMinutes(1).toString());
        TimestampsDto externalTimestamps = new TimestampsDto(complytTimestampMinusOneMinute, complytTimestampDto);
        return new CustomerDto(
                UUID.randomUUID(),
                id,
                source,
                "name",
                new AddressDto("City", "Country", "County", "CA", "Street", "Zip"),
                CustomerTypeDto.RETAIL,
                internalTimeStamps,
                externalTimestamps
        );
    }


    public Transaction createTransaction(String id) {
        Address billingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        Address shippingAddress = new Address("City", "Country", "County", "CA", "Street", "Zip");
        List<Item> items = createItems(false, false);
        Timestamps timeStamps = new Timestamps(complytTimestamp, complytTimestamp);
        ShippingFee shippingFee = createShippingFee(false,false);
        return new Transaction(UUID.randomUUID(), id, id, source, items, billingAddress, shippingAddress, customerIdOtherDomains, createCustomer(customerIdOtherDomains.toString()), null, TransactionStatus.ACTIVE, tenantId, timeStamps, timeStamps, TransactionType.INVOICE, shippingFee, null);
    }

    public TransactionDto createTransactionDto(String id) {
        AddressDto billingAddress = new AddressDto("City", "Country", "County", "CA", "Street", "Zip");
        AddressDto shippingAddress = new AddressDto("City", "Country", "County", "CA", "Street", "Zip");
        List<ItemDto> items = createItemDtos(false, false);
        TimestampsDto timeStamps = new TimestampsDto(complytTimestampDto, complytTimestampDto);
        ShippingFeeDto shippingFeeDto = createShippingFeeDto(false,false);
        return new TransactionDto(UUID.randomUUID(), id, source, items, billingAddress, shippingAddress, customerIdOtherDomains, createCustomerDto(customerIdOtherDomains.toString()), null, TransactionStatusDto.ACTIVE, timeStamps, timeStamps, TransactionTypeDto.INVOICE, shippingFeeDto, null);
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
            add(new ItemDto(2000, 4, 8000, "description", "name", "C1S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRules() : null,
                    createSalesTaxRatesDto(), false, 0, withTangibleCategory ? TangibleCategoryDto.TANGIBLE : null, TaxableCategoryDto.TAXABLE));
            add(new ItemDto(2000, 4, 8000, "description", "name", "C3S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRules() : null,
                    createSalesTaxRatesDto(), false, 0, withTangibleCategory ? TangibleCategoryDto.TANGIBLE : null, TaxableCategoryDto.TAXABLE));

        }};
    }

    public List<ItemDto> createItemDtos(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        return new ArrayList<>() {{
            add(new ItemDto(2000, 4, 8000, "description", "name", "C1S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRules() : null,
                    null, false, 0, withTangibleCategory ? TangibleCategoryDto.TANGIBLE : null, TaxableCategoryDto.TAXABLE));
            add(new ItemDto(2000, 4, 8000, "description", "name", "C3S1", withJurisdictionalRules ? createJurisdictionalSalesTaxRules() : null,
                    null, false, 0, withTangibleCategory ? TangibleCategoryDto.TANGIBLE : null, TaxableCategoryDto.TAXABLE));

        }};
    }

    public SalesTaxRate createSalesTaxRates() {
        return new SalesTaxRate(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f);
    }

    public SalesTaxRateDto createSalesTaxRatesDto() {
        return new SalesTaxRateDto(0.1f, 0.1f, 0.1f, 0.1f, 0.1f, 0.5f);
    }

    public ShippingFee createShippingFee(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, 0, 1000, withJurisdictionalRules ? rules: null, null, "C6S1", TaxableCategory.TAXABLE, withTangibleCategory ? TangibleCategory.INTANGIBLE : null);
    }

    public ShippingFee createShippingFeeWithSalesTaxRates(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();
        return new ShippingFee(false, 0, 1000, withJurisdictionalRules ? rules: null, createSalesTaxRates(), "C6S1", TaxableCategory.TAXABLE, withTangibleCategory ? TangibleCategory.INTANGIBLE : null);
    }

    public ShippingFeeDto createShippingFeeDto(boolean withJurisdictionalRules, boolean withTangibleCategory) {
        JurisdictionalSalesTaxRules rules = createJurisdictionalSalesTaxRules();
        return new ShippingFeeDto(false, 0, 1000, withJurisdictionalRules ? rules: null, null, "C6S1", TaxableCategoryDto.TAXABLE, withTangibleCategory ? TangibleCategoryDto.INTANGIBLE: null);
    }

    public JurisdictionalSalesTaxRules createJurisdictionalSalesTaxRules() {
        return new JurisdictionalSalesTaxRules("California", "CA", true,
                false, CalculationType.FIXED, "description", 0.5f, null);
    }

    public Exemption createExemption(String id) {
        State state = new State("CA", "02", "California");
        Classification classification = new Classification("code", "description");
        ValidationDates validationDates = new ValidationDates( new ComplytTimestamp(complytTimestamp.getTimestamp().minusYears(1)), new ComplytTimestamp(complytTimestamp.getTimestamp().plusYears(1)));
        Timestamps internalTimestamps = new Timestamps(complytTimestamp, complytTimestamp);
        Status status = new Status("code", "name");
        Certificate certificate = new Certificate(certificateId, "url", "name");

        return new Exemption( UUID.randomUUID(), id, tenantId, customerIdOtherDomains,
                state, classification, validationDates, internalTimestamps, status, certificate, ExemptionType.FULLY);
    }

    public ExemptionDto createExemptionDto(String id) {
        StateDto state = new StateDto("CA", "02", "California");
        ClassificationDto classification = new ClassificationDto("code", "description");
        ValidationDatesDto validationDates = new ValidationDatesDto(
                new ComplytTimestampDto(complytTimestamp.getTimestamp().minusYears(1).toString()),
                new ComplytTimestampDto(complytTimestamp.getTimestamp().plusYears(1).toString()));
        TimestampsDto internalTimestamps = new TimestampsDto(complytTimestampDto, complytTimestampDto);
        StatusDto status = new StatusDto("code", "name");
        CertificateDto certificate = new CertificateDto(certificateId, "url", "name");

        return new ExemptionDto( UUID.randomUUID(), customerIdOtherDomains,
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
        return new SalesTaxTracking(UUID.randomUUID() , id, state,
                tenantId, true,
                new PhysicalNexusTracker(false, null),
                new EconomicNexusTracker(false, null), complytTimestamp.getTimestamp(),
                true, complytTimestamp.getTimestamp());
    }

    public SalesTaxTrackingDto createSalesTaxTrackingDto() {
        StateDto state = new StateDto("CA", "02", "California");
        return new SalesTaxTrackingDto(UUID.randomUUID(), state,
                true,
                new PhysicalNexusTrackerDto(false, null),
                new EconomicNexusTrackerDto(false, null), complytTimestamp.getTimestamp(),
                true, complytTimestamp.getTimestamp());
    }

    public Result createResult() {
        return new Result("", "", "injectedCounty", "", 0f, 0f, "", "",
                0f, 0f, 0f, 0f, "", 0f, 0, "",
                0f, 0f, "", 0, 0, "", 0,
                0, "", 0, 0, "", 0, 0, "",
                0, 0, "");
    }

}
