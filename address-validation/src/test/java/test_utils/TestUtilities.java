package test_utils;

import io.complyt.config.web_clients.WebClientWrapperProperties;
import io.complyt.domain.Address;
import io.complyt.domain.CachedAddressData;
import io.complyt.domain.Scoring;
import io.complyt.domain.ValidatedAddress;
import io.complyt.domain.enums.FieldMatchType;
import io.complyt.domain.enums.FieldsMatchScore;
import io.complyt.domain.enums.MatchLevelType;
import io.complyt.domain.here.*;
import io.complyt.v1.models.AddressDto;
import io.complyt.v1.models.CachedAddressDataDto;
import io.complyt.v1.models.ScoringDto;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.javatuples.Pair;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public interface TestUtilities {

    static Address getAddress() {
        return new Address("Beverly Hills", "US", null, "CA", "1008 Elden Way", "90210", null,false);
    }

    static ValidatedAddress getValidatedAddress() {
        return new ValidatedAddress(null, List.of(getCachedAddressData()), getAddress(), LocalDateTime.now());
    }

    static CachedAddressData getCachedAddressData() {
        Address address = new Address("Beverly Hills", "US", "County", "CA", "1008 Elden Way", "90210", null, null);
        Scoring scoring = new Scoring(MatchLevelType.GOOD, 0.8, new FieldsMatchScore(FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.PARTIAL, FieldMatchType.PARTIAL, FieldMatchType.PARTIAL, null));
        return new CachedAddressData(address, scoring);
    }

    static CachedAddressDataDto getCachedAddressDataDto() {
        AddressDto address = new AddressDto("Beverly Hills", "US", "County", "CA", "1008 Elden Way", "90210", null, true);
        ScoringDto scoring = new ScoringDto(MatchLevelType.GOOD, 0.8, new FieldsMatchScore(FieldMatchType.EXACT, FieldMatchType.EXACT, FieldMatchType.PARTIAL, FieldMatchType.PARTIAL, FieldMatchType.PARTIAL, null));
        return new CachedAddressDataDto(address, scoring);
    }

    static Scoring getScoring() {
        return new Scoring(MatchLevelType.POOR, 0.5f, new FieldsMatchScore(FieldMatchType.PARTIAL, FieldMatchType.PARTIAL, FieldMatchType.PARTIAL, FieldMatchType.PARTIAL, FieldMatchType.PARTIAL, null));
    }

    static ScoringDto getScoringDto() {
        return new ScoringDto(MatchLevelType.EXCELLENT, 1, new FieldsMatchScore(FieldMatchType.PARTIAL, FieldMatchType.PARTIAL, FieldMatchType.PARTIAL, FieldMatchType.PARTIAL, FieldMatchType.PARTIAL, null));
    }

    static ScoringDto getScoringGlobalDto() {
        return new ScoringDto(MatchLevelType.EXCELLENT, 1, new FieldsMatchScore(FieldMatchType.PARTIAL,null, FieldMatchType.PARTIAL, FieldMatchType.PARTIAL, FieldMatchType.PARTIAL, FieldMatchType.PARTIAL));
    }

    static HereAddressData getHereAddressData() {
        return new HereAddressData().withItems(List.of(getHereAddressItem()));
    }

    static HereAddressItem getHereAddressItem() {
        return new HereAddressItem("","","","",
                new HereAddress("","USA","US",null,"CA","County","Beverly Hills", "1008 Elden Way","90210"),
                new HerePosition(12345,12345),new HereMapView(0.5,0.5,0.5,0.5),
                new HereScoring(0.5f,new HereFieldScore(0.5,0.5, 0.5,  List.of(0.5), 0.5)));
    }

    static HereScoring getHereScoring() {
        return new HereScoring(0.9f,new HereFieldScore(0.5, 0.5, 0.5, List.of(0.5), 0.5));
    }

    static AddressDto getAddressDto() {
        return new AddressDto("Beverly Hills", "US", null, "CA", "1008 Elden Way", "90210", null,false);
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
                .append("matchedAddresses", Collections.singletonList(
                        new Document("address", new Document("city", "Los Angeles")
                                .append("country", "United States")
                                .append("countryCode", "USA")
                                .append("county", "Los Angeles County")
                                .append("state", "California")
                                .append("street", "123 Hollywood Blvd")
                                .append("zip", "90028")
                                .append("region", "region")
                                .append("isPartial", false))
                                .append("scoring", new Document("matchLevel", "EXCELLENT")
                                        .append("score", 1.0)
                                        .append("fieldScore", new Document("countryMatch", "EXACT")
                                                .append("stateMatch", "EXACT")
                                                .append("cityMatch", "PARTIAL")
                                                .append("zipMatch", "EXACT")
                                                .append("streetMatch", "NO_MATCH")
                                                .append("regionMatch", "NO_MATCH")
                                        )
                                )
                ))
                .append("requestAddress", new Document("city", "street")
                        .append("country", "US")
                        .append("state", "California")
                        .append("zip", "90210")
                        .append("street", "10 5th Ave")
                        .append("isPartial", false)
                        .append("region", "region")
                )
                .append("createdDate", LocalDateTime.now())
                .append("_class", "io.complyt.domain.ValidatedAddress");
    }
}
