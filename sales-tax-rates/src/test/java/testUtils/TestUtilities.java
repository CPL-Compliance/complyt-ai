package testUtils;

import com.complyt.domain.Address;
import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.fast_tax.FastTaxData;
import com.complyt.domain.fast_tax.InformationComponent;
import com.complyt.domain.fast_tax.TaxInfoItem;
import com.complyt.domain.zip_tax.Result;

import java.util.LinkedHashMap;
import java.util.Set;
import java.util.UUID;

import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.ComplytSalesTaxRatesDto;
import com.complyt.v1.model.SalesTaxRatesDto;
import lombok.NonNull;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface TestUtilities {

    public static Address createAddressInNewYork() {
        return new Address("New York", "US", null, "NY", "160 Broadway", "10038");
    }

    public static SalesTaxRates createNewYorkSalesTaxRates() {
        return new SalesTaxRates(0.0f, 0.045f, 0.00375f, 0.0f, 0.04f, 0.08875f);
    }

    public static ComplytSalesTaxRates createNewYorkComplytSalesTaxRates() {
        Address address = createAddressInNewYork();
        SalesTaxRates salesTaxRates = createNewYorkSalesTaxRates();
        LocalDateTime now = LocalDateTime.now();
        return new ComplytSalesTaxRates(UUID.randomUUID().toString(), address, salesTaxRates, now, now.plusMinutes(1));
    }

    public static Address createAddressInCalifornia() {
        return new Address("Fresno", "US", null, "CA", "7498 N Remington Ave", "93711-5508");
    }

    public static AddressDto createAddressDtoInCalifornia() {
        return new AddressDto("Fresno", "US", null, "CA", "7498 N Remington Ave", "93711-5508");
    }

    public static SalesTaxRates createCaliforniaSalesTaxRates() {
        return new SalesTaxRates(0.00375f, 0.0f, 0.00725f, 0.0125f, 0.06f, 0.0835f);
    }

    public static SalesTaxRatesDto createCaliforniaSalesTaxRatesDto() {
        return new SalesTaxRatesDto(0.00375f, 0.0f, 0.00725f, 0.0125f, 0.06f, 0.0835f);
    }

    public static ComplytSalesTaxRates createCaliforniaComplytSalesTaxRates() {
        Address address = createAddressInCalifornia();
        SalesTaxRates salesTaxRates = createCaliforniaSalesTaxRates();
        LocalDateTime now = LocalDateTime.now();
        return new ComplytSalesTaxRates(UUID.randomUUID().toString(), address, salesTaxRates, now, now.plusMinutes(1));
    }

    public static Query createAddressSearchQuery(Address address) {
        return Query.query(Criteria
                .where("address.city").is(address.getCity())
                .and("address.street").is(address.getStreet())
                .and("address.zip").is(address.getZip()));
    }

    public static FastTaxData createFastTaxData() {
        String matchLevel = "Address";
        TaxInfoItem taxInfoItem = new TaxInfoItem("Fresno", "0.00375", "0", "Fresno", "0.00725", "0.0125", null, "", "", "0", "CA", "California", "0.06", "0.0835", "LABOR/FREIGHT/SERVICES", "93711-5508");
        List<TaxInfoItem> taxInfoItems = List.of(taxInfoItem);
        return new FastTaxData(matchLevel, taxInfoItems);
    }

    public static AddressDto createStubFastTaxAddressDto() {
        return new AddressDto("Englewood", "US", null, "CO", "street", "80112");
    }

    public static Result createResult() {
        return new Result("", "", "injectedCounty", "", 0f, 0f, "", "",
                0f, 0f, 0f, 0f, "", 0f, 0, "",
                0f, 0f, "", 0, 0, "", 0,
                0, "", 0, 0, "", 0, 0, "",
                0, 0, "");
    }

    public static void checkErrorMessages(LinkedHashMap map, Set<String> expectedErrors) {
        String message = (String) map.get("message");
        String[] errors = message.substring(1, message.length() - 1).split(", ");
        assertEquals(expectedErrors.size(), errors.length);
        for (String err : errors) {
            assertTrue(expectedErrors.contains(err));
        }
    }

    public static FastTaxData stubFastTaxNewYork() {
        return new FastTaxData("Address", List.of(new TaxInfoItem(
                "Fresno", "0.00375", "0",
                "Fresno", "0.00725", "0.0125",
                List.of(new InformationComponent("CountyFIPS", "C1")
                        , new InformationComponent("CountyFIPS", "019")),
                "", "", "0",
                "CA", "California", "0.06",
                "0.08350", "LABOR/FREIGHT/SERVICES", "93711-5508")));
    }

    public static ComplytSalesTaxRatesDto createCaliforniaAddressWithSalesTaxRatesDto() {
        AddressDto address = createAddressDtoInCalifornia();
        SalesTaxRatesDto salesTaxRates = createCaliforniaSalesTaxRatesDto();
        return new ComplytSalesTaxRatesDto(address, salesTaxRates);
    }

    public static SalesTaxRatesDto createStubFastTaxSalesTaxRates() {
        return new SalesTaxRatesDto(0f, 0f, 0.011f, 0.0025f, 0.029f, 0.0775f);
    }

    public static String stringWithLength(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (; 0 < length; length--) stringBuilder.append('a');
        return stringBuilder.toString();
    }

}
