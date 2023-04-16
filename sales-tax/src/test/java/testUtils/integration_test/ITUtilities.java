package testUtils.integration_test;

import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.fast_tax.InformationComponent;
import com.complyt.domain.sales_tax.fast_tax.TaxInfoItem;
import com.complyt.v1.models.*;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.models.customer.CustomerTypeDto;
import com.complyt.v1.models.timestamps.TimestampsDto;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.UUID;

public interface ITUtilities {

    String NON_EXISTING_COMPLYT_ID = "1111111-1111-1111-1111-111111111111";

    // if no items provided, puts a default stub
    static TransactionDto stubTransactionDto(String externalId, UUID customerId, ItemDto... items) {
        return new TransactionDto(null, externalId, "1",
                List.of(items.length < 1 ? new ItemDto[]{stubItemDto()} : items),
                null, new MandatoryAddressDto("Acampo", "US", null, "CA", "1525 R Jahant Rd", "95220"), customerId,
                null, null, TransactionStatusDto.ACTIVE, null, new TimestampsDto(LocalDateTime.now().toString(), LocalDateTime.now().toString()),
                TransactionTypeDto.INVOICE, null, null, 0, 0, 0);
    }

    static CustomerDto stubCustomerDto(String externalId) {
        return new CustomerDto(null, externalId, "1",
                "stub customer", null, CustomerTypeDto.RETAIL,
                null, new TimestampsDto(LocalDateTime.now().toString(), LocalDateTime.now().toString()));
    }

    static SalesTaxTrackingDto stubSalesTaxTrackingDto(StateDto state) {
        return new SalesTaxTrackingDto(null, state, true,
                new PhysicalNexusTrackerDto(false, LocalDateTime.now()),
                new EconomicNexusTrackerDto(false, LocalDateTime.now()),
                LocalDateTime.now(), false, LocalDateTime.now());
    }

    static ItemDto stubItemDto() {
        return new ItemDto(10000, 1, 10000, "some description", "Hardware", "C1S1",
                null, null, false, 0, null, null);
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

    static Jwt.Builder stubJwt() {
        return Jwt.withTokenValue("token")
                .header("typ", "JWT")
                .claim("tenant_id", "it_tenant");
    }
}
