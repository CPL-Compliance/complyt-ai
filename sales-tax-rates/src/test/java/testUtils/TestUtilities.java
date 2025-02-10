package testUtils;

import com.complyt.domain.*;
import com.complyt.domain.common_rates.CommonAddress;
import com.complyt.domain.common_rates.CommonRates;
import com.complyt.domain.common_rates.CommonSalesTaxRates;
import com.complyt.domain.common_rates.Scoring;
import com.complyt.domain.enums.FieldMatchType;
import com.complyt.domain.enums.FieldsMatchScore;
import com.complyt.domain.enums.MatchLevelType;
import com.complyt.domain.enums.SalesTaxSources;
import com.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import com.complyt.domain.fast_tax.TaxInfoItem;
import com.complyt.domain.gt.ComplytGtRates;
import com.complyt.domain.gt.GtAddress;
import com.complyt.domain.gt.GtRates;
import com.complyt.domain.internal_rates.*;
import com.complyt.domain.matched_address.MatchedAddressData;
import com.complyt.domain.zip_tax.Result;
import com.complyt.v1.model.AddressDto;
import com.complyt.v1.model.AddressWithDateDto;
import com.complyt.v1.model.common_sales_tax_rates.CommonAddressDto;
import com.complyt.v1.model.common_sales_tax_rates.SalesTaxRatesDto;
import com.complyt.v1.model.gt.ComplytGtRatesDto;
import com.complyt.v1.model.gt.GtAddressDto;
import com.complyt.v1.model.gt.GtRatesDto;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalAddressDto;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalRateDto;
import com.complyt.v1.model.internal_sales_tax_rates_dto.InternalSalesTaxRatesDto;
import feign.FeignException;
import feign.Request;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface TestUtilities {

    String LOMBOK_NON_NULL_ANNOTATION_MESSAGE = "is marked non-null but is null";

    static AddressWithDate createAddressInCaliforniaWithCreationDate(LocalDateTime localDateTime) {
        return new AddressWithDate(new Address("Fresno", "US", null, "CA", "7498 N Remington Ave", "93711-5508", false),
                localDateTime);
    }

    static SalesTaxRatesData createSalesTaxRatesData() {
        return new SalesTaxRatesData(null, TestUtilities.createAddressInCaliforniaWithCreationDate(), createMatchedAddressInCalifornia(), createCommonRates(), SalesTaxSources.FAST_SALES_TAX);
    }

    static AddressWithDate createAddressInCaliforniaWithCreationDate() {
        return new AddressWithDate(new Address("Fresno", "US", null, "California", "7498 N Remington Ave", "93711-5508", false),
                LocalDateTime.now());
    }

    static Address createAddressInCalifornia() {
        return new Address("Fresno", "US", null, "California", "7498 N Remington Ave", "93711-5508", false);
    }

    static AddressDto createAddressDtoInCalifornia() {
        return new AddressDto("Fresno", "US", null, "CA", "7498 N Remington Ave", "93711-5508", false);
    }

    static Scoring createScoring() {
        return new Scoring(MatchLevelType.EXCELLENT, 1, new FieldsMatchScore(FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.EXACT));
    }

    static AddressWithDateDto createAddressWithDateDtoInCalifornia(String date) {
        return new AddressWithDateDto(createAddressDtoInCalifornia(), date);
    }

    static AddressWithDate createAddressWithDateInCalifornia(LocalDateTime dateTime) {
        return new AddressWithDate(createAddressInCalifornia(), dateTime);
    }

    static MatchedAddressData createMatchedAddressInCalifornia() {
        return new MatchedAddressData(createAddressInCalifornia(), TestUtilities.createScoring());
    }

    static SalesTaxRates createCaliforniaSalesTaxRates() {
        return new SalesTaxRates(
                new BigDecimal("0"), // city
                new BigDecimal("0.00375"), // county
                new BigDecimal("0.00725"), // state
                new BigDecimal("0.082"), // tax
                new BigDecimal("0"), // combined
                null);
    }

    static ComplytSalesTaxRates createCaliforniaComplytSalesTaxRates() {
        Address address = createAddressInCalifornia();
        SalesTaxRates salesTaxRates = createCaliforniaSalesTaxRates();
        LocalDateTime now = LocalDateTime.now();

        return new ComplytSalesTaxRates(UUID.randomUUID(), UUID.randomUUID().toString(), address, salesTaxRates, now, now.plusMinutes(1));
    }

    /***
     *  getting the complytId because commonSalesTaxRates are sometimes received from conversion
     *  of ComplytSalesTaxRates, by ComplytSalesTaxRatesToCommonRatesMapper.INSTANCE::map
     */
    static CommonSalesTaxRates createExternalCommonSalesTaxRates(UUID complytId) {
        CommonSalesTaxRates commonSalesTaxRates = createExternalCommonSalesTaxRates();
        CommonRates commonRates = commonSalesTaxRates.salesTaxRates();
        return commonSalesTaxRates.withSalesTaxRates(commonRates);
    }

    static CommonAddress createCommonAddressFromAddress(InternalAddress address) {
        return new CommonAddress(null, address.state(), address.county(), address.city(), address.isUnincorporated(), address.zip(), address.lowerPlusFourDigits(), address.upperPlusFourDigits(), null, null);
    }

    static CommonRates createCommonRates() {
        return new CommonRates(
                new BigDecimal("0.00725"), // state
                new BigDecimal("0.00375"), // county
                new BigDecimal("0"), // city
                null, // combined
                null,  // rates
                new BigDecimal("0.071"), // mtd
                new BigDecimal("0"), // spd
                new BigDecimal("0"), // other
                new BigDecimal("0.082"));
    }

    static SalesTaxRatesDto createSalesTaxRatesDto() {
        return new SalesTaxRatesDto(
                new BigDecimal("0.00725"), // state
                new BigDecimal("0.00375"), // county
                new BigDecimal("0"), // city
                null, // combined
                null,  // rates
                new BigDecimal("0.071"), // mtd
                new BigDecimal("0"), // spd
                new BigDecimal("0"), // other
                new BigDecimal("0.082"));
    }

    static CommonSalesTaxRates createExternalCommonSalesTaxRates() {
        CommonAddress commonAddress = new CommonAddress("California", null, "Fresno", null, false, "12345", 0, 0, "", true);
        CommonRates commonRates = createCommonRates();
        return new CommonSalesTaxRates(UUID.randomUUID(), commonAddress, commonRates, SalesTaxSources.FAST_SALES_TAX);
    }

    static InternalSalesTaxRates createInternalSalesTaxRates(LocalDateTime dateTime, UUID uuid) {
        return createInternalSalesTaxRates(dateTime).withComplytId(uuid)
                .withSalesTaxRates(createInternalRates(dateTime));
    }


    static InternalSalesTaxRates createInternalSalesTaxRates(LocalDateTime dateTime) {

        return new InternalSalesTaxRates(UUID.randomUUID(), null, createInternalAddress(),
                createInternalRates(dateTime), createInternalEffectiveDates(), null, LocalDateTime.now());
    }

    static InternalEffectiveDates createInternalEffectiveDates() {
        LocalDateTime initialDate = LocalDateTime.of(2000, 1, 1, 0, 0, 0);
        return new InternalEffectiveDates(initialDate, initialDate, null, null, null, null, null, null, null, initialDate);
    }


    static InternalAddress createInternalAddress() {
        InternalAddress internalAddress = new InternalAddress("California", "Fresno", "Fresno", false, "", 0, 0);

        return internalAddress;
    }

    static InternalRates createInternalRates(LocalDateTime dateTime, UUID uuid) {
        return createInternalRates(dateTime);
    }

    static InternalRates createInternalRates(LocalDateTime dateTime) {
        InternalRates internalRates = new InternalRates(
                new BigDecimal("0.00725"),
                new BigDecimal("0.00375"),
                new BigDecimal("0"),
                new BigDecimal("0.071"),
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                new BigDecimal("0.082"));
        return internalRates;
    }

    static InternalSalesTaxRatesDto createInternalSalesTaxRatesDto() {
        return new InternalSalesTaxRatesDto(createInternalAddressDto(), createInternalRatesDto());
    }

    static InternalAddressDto createInternalAddressDto() {
        return new InternalAddressDto("California", "Fresno", "Fresno", false, "", 0, 0);
    }

    static InternalAddressDto createInternalAddressDto(String state, String county, String city) {
        return new InternalAddressDto(state, county, city, false, "", 0, 0);
    }

    static InternalAddressDto createInternalAddressDto(String state, String county, String city, String zip, boolean isUnincorporated,
                                                       boolean hasPlusFourZip, int lowerPlusFour, int upperPlusFour) {
        return new InternalAddressDto(state, county, city, isUnincorporated, zip, lowerPlusFour, upperPlusFour);
    }

    static InternalRateDto createInternalRatesDto(UUID complytId) {
        return createInternalRatesDto(complytId,
                LocalDateTime.of(2021, 01, 07, 0, 0).toString());
    }

    static InternalRateDto createInternalRatesDto(UUID complytId, String effectiveDate) {
        return new InternalRateDto(
                complytId,
                new BigDecimal("0.06"),
                new BigDecimal("0.01975"),
                new BigDecimal("0.00375"),
                new BigDecimal("0.0835"),
                effectiveDate,
                SalesTaxSources.FAST_SALES_TAX,
                null
        );
    }

    static InternalRateDto createInternalRatesDto() {
        return createInternalRatesDto(null);
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
        return new FastTaxGetBestMatchData(matchLevel, taxInfoItems, "1", null);
    }

    static AddressDto createStubFastTaxAddressDto() {
        return new AddressDto("Englewood", "US", null, "Colorado", null, "80112", true);
    }

    static CommonAddressDto createCommonAddressDto(AddressDto addressDto) {
        return  new CommonAddressDto(addressDto.country(), addressDto.state(), addressDto.county(), addressDto.city(), null, addressDto.zip(), null, null, addressDto.street(), addressDto.isPartial());
    }

    static CommonAddressDto createCommonAddressDto() {
        return new CommonAddressDto(null, "AK", "ANCHORAGE", "ANCHORAGE", false, "99501", 0, 0, null, null);
    }

    static AddressDto createStubInternalTaxAddressDto() {
        return new AddressDto("Anchorage", "USA", "Anchorage",
                "Alaska", "751-2696 205 E Benson Blvd",
                "99501", false);
    }

    static AddressDto createStubInternalTaxAddressUnincorporatedDto() {
        return new AddressDto("PRINCETON", "USA", "MERCER",
                "West Virginia", "751-2696 205 E Benson Blvd",
                "24740", false);
    }

    static SalesTaxRatesDto createStubInternalTax_SalesTaxRatesDto(BigDecimal bigDecimal) {
        return new SalesTaxRatesDto(
                bigDecimal,
                bigDecimal,
                BigDecimal.ZERO,
                null,
                null,
                bigDecimal,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                bigDecimal.add(bigDecimal).add(bigDecimal));
    }

    static SalesTaxRatesDto createStubInternalTaxUnincorporated_SalesTaxRatesDto() {
        return new SalesTaxRatesDto(
                new BigDecimal("0.00375"),
                new BigDecimal("0.01975"),
                new BigDecimal("0.06"),
                null,
                null,
                null,
                null,
                null,
                new BigDecimal("0.0835"));
    }

    static InternalSalesTaxRatesDto createInternalSalesTaxRatesDtoToInsert(String effectiveDate) {
        return new InternalSalesTaxRatesDto(createInternalAddressDto("Alaska", "Anchorage", "Anchorage",
                "99501", false, true, 0, 123)
                , createInternalRatesDto(null, effectiveDate));
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
        return  new SalesTaxRatesDto(
                new BigDecimal("0.0425"), // stateRate
                new BigDecimal("0.0125"), // countyRate
                new BigDecimal("0"),    // cityRate
                new BigDecimal("0.029"), // combinedDistrictRate (CountyDistrictRate in JSON)
                new RatesMetaData(BigDecimal.ZERO, new BigDecimal("0.029"), BigDecimal.ZERO),  // ratesMetaData
                null,    // mtaRate
                null,    // spdRate
                null,    // otherRate
                new BigDecimal("0.05975")); // taxRate
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

    static GtRates createCanadaGtRates() {
        return new GtRates(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.0975), BigDecimal.valueOf(0.14975));
    }

    static GtRatesDto createCanadaGtRatesDto() {
        return new GtRatesDto(BigDecimal.valueOf(0.05), BigDecimal.valueOf(0.0975), BigDecimal.valueOf(0.14975));
    }

    static GtAddress createCanadaGtAddress() {
        return new GtAddress("Canada", "Quebec");
    }

    static GtAddressDto createCanadaGtAddressDto() {
        return new GtAddressDto("Canada", "Quebec");
    }

    static ComplytGtRates createCanadaComplytGtRates() {
        GtAddress gtAddress = createCanadaGtAddress();
        GtRates gtRates = createCanadaGtRates();
        return new ComplytGtRates(UUID.randomUUID().toString(), gtAddress, gtRates);
    }

    static ComplytGtRatesDto createCanadaComplytGtRatesDto() {
        GtAddressDto gtAddress = createCanadaGtAddressDto();
        GtRatesDto gtRates = createCanadaGtRatesDto();
        return new ComplytGtRatesDto(gtAddress, gtRates);
    }

    static GtAddressDto createArmeniaGtAddressDto() {
        return new GtAddressDto("Armenia", null);
    }

    static GtRatesDto createArmeniaGtRatesDto() {
        return new GtRatesDto(BigDecimal.valueOf(0.18), null, BigDecimal.valueOf(0.18));
    }

    static ComplytGtRatesDto createArmeniaComplytGtRatesDto() {
        GtAddressDto gtAddress = createArmeniaGtAddressDto();
        GtRatesDto gtRates = createArmeniaGtRatesDto();
        return new ComplytGtRatesDto(gtAddress, gtRates);
    }

    static Document salesTaxRatesDocument() {
        return new Document("_id", new ObjectId())
                .append("address", new Document("city", "LOCUST FORK")
                        .append("country", "US")
                        .append("county", "BLOUNT")
                        .append("state", "AL")
                        .append("street", "10 5th Ave")
                        .append("zip", "35097")
                        .append("isPartial", true)
                        .append("score", 1.0))
                .append("salesTaxRates", new Document("cityRate", "0.02")
                        .append("countyRate", "0.03")
                        .append("stateRate", "0.04")
                        .append("taxRate", "0.09")
                        .append("combinedDistrictRate", "0")
                        .append("ratesMetaData", new Document("cityDistrictRate", "0")
                                .append("countyDistrictRate", "0")
                                .append("specialDistrictRate", "0")
                        ))
                .append("createdDate", LocalDateTime.now())
                .append("expireAt", LocalDateTime.now().plusYears(1))
                .append("_class", "com.complyt.domain.ComplytSalesTaxRates")
                .append("requestAddress", new Document("city", "LOCUST FORK")
                        .append("country", "US")
                        .append("county", "BLOUNT")
                        .append("state", "AL")
                        .append("zip", "35097")
                        .append("isPartial", true));
    }

    static Document gtRatesDocument() {
        return new Document("_id", new ObjectId())
                .append("gtAddress", new Document("country", "Armenia"))
                .append("gtRates", new Document("taxRate", "0.18")
                        .append("countryRate", "0.18"));
    }

    static Document internalSalesTaxRates() {
        return new Document("_id", new ObjectId())
                .append("complytId", UUID.randomUUID())
                .append("address", new Document("state", "AK")
                        .append("county", "ANCHORAGE")
                        .append("city", "ANCHORAGE")
                        .append("isUnincorporated", false)
                        .append("zip", "99501")
                        .append("lowerPlusFourDigits", 0)
                        .append("upperPlusFourDigits", 0))
                .append("salesTaxRates", new Document("stateRate", "0.1")
                        .append("countyRate", "0.1")
                        .append("cityRate", "0")
                        .append("mtaRate", "0.1")
                        .append("spdRate", "0")
                        .append("other1Rate", "0")
                        .append("other2Rate", "0")
                        .append("other3Rate", "0")
                        .append("other4Rate", "0")
                        .append("taxRate", "0.3"))
                .append("effectiveDates", new Document("state", new Date())
                        .append("maxEffectiveDate", new Date())
                        .append("county", new Date())
                        .append("mta", new Date()))
                .append("internalSalesTaxRatesMetaData", new Document("FIPS_STATE", "02")
                        .append("FIPS_COUNTY", "020")
                        .append("FIPS_CITY", "03000")
                        .append("FIPS_GEOCODE", "0202003000")
                        .append("COUNTY_TAXABLE_MAX", "N")
                        .append("CITY_TAXABLE_MAX", "N")
                        .append("TAX_SHIPPING_ALONE", "Y")
                        .append("TAX_SHIPPING_AND_HANDLING_TOGETHER", "Y"))
                .append("_class", "com.complyt.domain.internal_rates.InternalTaxRatesData")
                .append("createdDate", LocalDateTime.now());
    }

    static FeignException.BadRequest create400BadRequestFeignException() {
        return new FeignException.BadRequest("bad request",
                Request.create(Request.HttpMethod.GET, "address_validation_uri",
                        Map.of("Authorization", List.of("Dummy Bearer")),
                        null, null, null), null, Map.of("Authorization", List.of("Dummy Bearer")));
    }
}
