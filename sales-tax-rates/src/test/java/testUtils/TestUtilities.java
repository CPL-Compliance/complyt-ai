package testUtils;

import com.complyt.domain.Address;
import com.complyt.domain.CityCountyWrapper;
import com.complyt.domain.ComplytSalesTaxRates;
import com.complyt.domain.SalesTaxRates;
import com.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import com.complyt.domain.fast_tax.TaxInfoItem;
import com.complyt.domain.zip_tax.Result;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.RatesMetaDataDto;
import com.complyt.v1.model.SalesTaxRatesDto;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface TestUtilities {

    String LOMBOK_NON_NULL_ANNOTATION_MESSAGE = "is marked non-null but is null";

    static Address createAddressInCalifornia() {
        return new Address("Fresno", "US", null, "CA", "7498 N Remington Ave", "93711-5508", false);
    }

    static CityCountyWrapper createCityCountyInCalifornia() {
        return new CityCountyWrapper("Fresno", "Fresno");
    }

    static AddressDto createAddressDtoInCalifornia() {
        return new AddressDto("Fresno", "US", null, "CA", "7498 N Remington Ave", "93711-5508", false);
    }

    static SalesTaxRates createCaliforniaSalesTaxRates() {
        return new SalesTaxRates(new BigDecimal("0.00375"), BigDecimal.ZERO, new BigDecimal("0.00725"), new BigDecimal("0.071"), new BigDecimal("0.06"), null);
    }

    static ComplytSalesTaxRates createCaliforniaComplytSalesTaxRates() {
        Address address = createAddressInCalifornia();
        SalesTaxRates salesTaxRates = createCaliforniaSalesTaxRates();
        LocalDateTime now = LocalDateTime.now();
        return new ComplytSalesTaxRates(UUID.randomUUID().toString(), address, salesTaxRates, now, now.plusMinutes(1));
    }

    static Query createAddressSearchQuery(Address address) {
        return Query.query(Criteria
                .where("address.city").is(address.city())
                .and("address.street").is(address.street())
                .and("address.zip").is(address.zip()));
    }

    static FastTaxGetBestMatchData createFastTaxGetBestMatchData() {
        String matchLevel = "Address";
        TaxInfoItem taxInfoItem = new TaxInfoItem("Fresno", "0.00375", "0", "Fresno", "0.00725", "0.0125", null, "", "", "0", "CA", "California", "0.06", "0.0835", "LABOR/FREIGHT/SERVICES", "93711-5508");
        List<TaxInfoItem> taxInfoItems = List.of(taxInfoItem);
        return new FastTaxGetBestMatchData(matchLevel, taxInfoItems, "1");
    }

    static AddressDto createStubFastTaxAddressDto() {
        return new AddressDto("Englewood", "US", null, "CO", "street", "80112", false);
    }

    static Result createResult() {
        return new Result("", "", "injectedCounty", "", 0f, 0, "", "",
                0, 0, 0, 0, "", 0, 0, "",
                0, 0, "", 0, 0, "", 0,
                0, "", 0, 0, "", 0, 0, "",
                0, 0, "");
    }

    static void checkErrorMessages(LinkedHashMap map, Set<String> expectedErrors) {
        String message = (String) map.get("message");
        String[] errors = message.substring(1, message.length() - 1).split(", ");
        assertEquals(expectedErrors.size(), errors.length);
        for (String err : errors) {
            assertTrue(expectedErrors.contains(err));
        }
    }

    static SalesTaxRatesDto createStubFastTaxSalesTaxRatesDto() {
        BigDecimal cityDistrictRate = BigDecimal.ZERO;
        BigDecimal countyDistrictRate = new BigDecimal("0.029");
        RatesMetaDataDto ratesMetaDataDto = new RatesMetaDataDto(cityDistrictRate, countyDistrictRate);
        return new SalesTaxRatesDto(new BigDecimal("0.0"), new BigDecimal("0.0"), new BigDecimal("0.011"), new BigDecimal("0.04"), cityDistrictRate.add(countyDistrictRate), ratesMetaDataDto);
    }

    static String stringWithLength(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (; 0 < length; length--) stringBuilder.append('a');
        return stringBuilder.toString();
    }

    static TaxInfoItem createTaxInfoItemWithNullValues() {
        return new TaxInfoItem(
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null
        );
    }

}
