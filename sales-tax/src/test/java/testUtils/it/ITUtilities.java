package testUtils.it;

import com.complyt.domain.sales_tax.fast_tax.FastTaxData;
import com.complyt.domain.sales_tax.fast_tax.InformationComponent;
import com.complyt.domain.sales_tax.fast_tax.TaxInfoItem;
import com.complyt.v1.models.*;
import com.complyt.v1.models.timestamps.TimestampsDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ITUtilities {

    // if no items provided, puts a default stub
    static TransactionDto stubTransactionDto(String externalId, UUID customerId, ItemDto... items) {
        return new TransactionDto(null, externalId, "1",
                List.of(items.length < 1 ? new ItemDto[]{stubItemDto()} : items),
                null, new MandatoryAddressDto("Acampo", "US", null, "CA", "1525 R Jahant Rd", "95220"), customerId,
                null, null, TransactionStatusDto.ACTIVE, null, new TimestampsDto(LocalDateTime.now().toString(), LocalDateTime.now().toString()),
                TransactionTypeDto.INVOICE, null, null, 0, 0, 0);
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
}
