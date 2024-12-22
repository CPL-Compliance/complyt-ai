package test_utils;

import io.complyt.config.web_clients.WebClientWrapperProperties;
import io.complyt.domain.Address;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.ValidatedAddress;
import io.complyt.domain.fast_tax.FastTaxGetBestMatchData;
import io.complyt.domain.fast_tax.TaxInfoItem;
import io.complyt.domain.here.*;
import io.complyt.v1.models.AddressDto;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.javatuples.Pair;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface TestUtilities {

    static Address getAddress() {
        return new Address("Beverly Hills", "US", null, "CA", "1008 Elden Way", "90210", false);
    }

    static ValidatedAddress getValidatedAddress() {
        return new ValidatedAddress(null, getCachedAddressData(), getAddress(), LocalDateTime.now());
    }

    static CachedAddressData getCachedAddressData() {
        return new CachedAddressData("Beverly Hills", "US", "County", "CA", "1008 Elden Way", "90210", false, 0.5f);
    }

    static HereAddressData getHereAddressData() {
        return new HereAddressData().withItems(List.of(getHereAddressItem()));
    }

    static HereAddressItem getHereAddressItem() {
        return new HereAddressItem("","","","",
                new HereAddress("",null,"US",null,"CA","County","Beverly Hills","1008 Elden Way","90210"),
                new HerePosition(12345,12345),new HereMapView(0.5,0.5,0.5,0.5),
                new HereScoring(0.5f,new HereFieldScore(0.5,0.5,  List.of(0.5)),1,90210));
    }

    static HereScoring getHereScoring() {
        return new HereScoring(0.9f,new HereFieldScore(0.5,0.5,  List.of(0.5)),1,90210);
    }

    static AddressDto getAddressDto() {
        return new AddressDto("Beverly Hills", "US", null, "CA", "1008 Elden Way", "90210", false);
    }

    static String stringByLength(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) stringBuilder.append("a");
        return stringBuilder.toString();
    }

    static void checkErrorMessages(LinkedHashMap map, Set<String> expectedErrors) {
        String message = (String) map.get("message");
        String[] errors = message.substring(1, message.length() - 1).split(", ");
        assertEquals(expectedErrors.size(), errors.length);
        for (String err : errors) {
            assertTrue(expectedErrors.contains(err));
        }
    }

    String LOMBOK_NON_NULL_ANNOTATION_MESSAGE = "is marked non-null but is null";

    static WebClientWrapperProperties getWebClientWrapperPropertiesStub() {
        return new WebClientWrapperProperties("", "", "", new Pair<>("", ""));
    }

    static Document createAddressSynDocument() {
        return new Document("_id", new ObjectId())
                .append("mappingType", "equivalent")
                .append("synonyms", List.of("South Carolina", "SC"));
    }

    static Document createAddressValidationDocument() {
        return new Document("_id", new ObjectId())
                .append("address", new Document("city", "street")
                        .append("country", "US")
                        .append("county", "California")
                        .append("state", "California")
                        .append("street", "10 5th Ave")
                        .append("zip", "90210")
                        .append("isPartial", false)
                        .append("score", 1.0))
                .append("requestAddress", new Document("city", "street")
                        .append("country", "US")
                        .append("state", "California")
                        .append("zip", "90210")
                        .append("street", "10 5th Ave")
                        .append("isPartial", false))
                .append("createdDate", LocalDateTime.now())
                .append("_class", "io.complyt.domain.ValidatedAddress");
    }

    static FastTaxGetBestMatchData createFastTaxGetBestMatchData() {
        String matchLevel = "Address";
        TaxInfoItem taxInfoItem = new TaxInfoItem("Fresno", "0.00375", "0", "County", "0.00725", "0.0125", null, "", "", "0", "CA", "California", "0.06", "0.0835", "LABOR/FREIGHT/SERVICES", "93711-5508");
        List<TaxInfoItem> taxInfoItems = List.of(taxInfoItem);
        return new FastTaxGetBestMatchData(matchLevel, taxInfoItems,null);
    }

}
