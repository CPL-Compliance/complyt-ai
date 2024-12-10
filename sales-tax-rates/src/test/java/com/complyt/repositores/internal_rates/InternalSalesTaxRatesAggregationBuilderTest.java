//package com.complyt.repositores.internal_rates;
//
//import com.complyt.business.internal_sales_tax_rates.InternalRatesCollectionNames;
//import com.complyt.domain.Address;
//import com.complyt.domain.AddressWithDate;
//import com.complyt.domain.enums.RatesStatus;
//import com.complyt.domain.internal_rates.InternalSalesTaxRates;
//import com.complyt.domain.internal_rates.InternalTaxRatesData;
//import com.complyt.repositories.internal_rates.InternalSalesTaxRatesByAddressWithDateAggregationBuilder;
//import lombok.NonNull;
//import org.bson.Document;
//import org.junit.Assert;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Sort;
//import org.springframework.data.mongodb.MongoExpression;
//import org.springframework.data.mongodb.core.aggregation.*;
//import org.springframework.data.mongodb.core.query.Criteria;
//import testUtils.TestUtilities;
//
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import static com.complyt.utils.query.AggregationOps.$arrayElemAt;
//import static com.complyt.utils.query.AggregationOps.$filter;
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//
//@ExtendWith(MockitoExtension.class)
//class InternalSalesTaxRatesAggregationBuilderTest {
//    AddressWithDate addressWithDate;
//    InternalSalesTaxRatesByAddressWithDateAggregationBuilder internalSalesTaxRatesAggregationBuilder;
//
//    @BeforeEach
//    void setUp() {
//        internalSalesTaxRatesAggregationBuilder = new InternalSalesTaxRatesByAddressWithDateAggregationBuilder();
//        addressWithDate = TestUtilities.createAddressInCaliforniaWithCreationDate();
//    }
//
//    @Test
//    void build_ReturnFullAddressQuery() {
//        Address address = addressWithDate.address().withCounty("somecounty").withCity("somecity");
//        addressWithDate = addressWithDate.withAddress(address);
//
//        TypedAggregation<InternalTaxRatesData> expectedAggregation = buildAggregation(addressWithDate.requiredDate(),
//                createStateCountyCityZipCriteria(addressWithDate.address()),
//                address.state());
//        TypedAggregation<InternalTaxRatesData> aggregationResult = internalSalesTaxRatesAggregationBuilder.build(addressWithDate);
//
//        assertEquals(expectedAggregation.toString(), aggregationResult.toString());
//    }
//
//    private String getISODate(LocalDateTime date) {
//        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
//        return timeFormatter.format(date);
//    }
//
//    private Criteria createStateCountyCityZipCriteria(Address address){
//        String stateEscapedSearchString = Pattern.quote(address.state());
//        Criteria searchByAddressCriteria = Criteria.where("address.state")
//                .regex(stateEscapedSearchString, "i");
//
//        if (address.zip() != null) {
//            searchByAddressCriteria.andOperator(createZipCriteria(address.zip()));
//        }
//
//        if (address.county() != null) {
//            String escapedSearchString = Pattern.quote(address.county());
//            searchByAddressCriteria.and("address.county").regex(escapedSearchString, "i");
//        }
//
//        if (address.city() != null) {
//            String escapedSearchString = Pattern.quote(address.city());
//
//            searchByAddressCriteria.orOperator(
//                    Criteria.where("address.city").regex(escapedSearchString, "i"),
//                    Criteria.where("address.city").regex("^UNINCORPORATED.*", "i")
//            );
//        }
//        return searchByAddressCriteria;
//    }
//
//    private List<Criteria> createZipCriteria(String zip) {
//        Pattern zipPattern = Pattern.compile("^(\\d{5})(?:-(\\d{4}))?$");
//        Matcher matcher = zipPattern.matcher(zip);
//        List<Criteria> criteriaList = new ArrayList<>();
//
//        if (matcher.matches()) {
//            Criteria zipCriteria = Criteria.where("address.zip").is(matcher.group(1));
//            criteriaList.add(zipCriteria);
//
//            if (matcher.group(2) != null) {
//                int plusFour = Integer.parseInt(matcher.group(2));
//                Criteria zipPlusFourCriteria = new Criteria().andOperator(
//                        Criteria.where("address.hasPlusFourZipCode").is(true),
//                        Criteria.where("address.lowerPlusFourDigits").lte(plusFour),
//                        Criteria.where("address.upperPlusFourDigits").gte(plusFour)
//                );
//
//                criteriaList.add(zipPlusFourCriteria);
//            }
//        }
//
//        return criteriaList;
//    }
//
//    private TypedAggregation<InternalTaxRatesData> buildAggregation(@NonNull LocalDateTime requiredDate,
//                                                                    @NonNull Criteria matchCriteria,
//                                                                    @NonNull String stateName) {
//        Document ratesInArchiveCollectionCond = AggregationExpression.from(MongoExpression.create(
//                "                  $and: [\n" +
//                        "                    {\n" +
//                        "                      $eq: [\n" +
//                        "                        \"$$item.status\",\n" +
//                        "                        \"" + RatesStatus.ACTIVE + "\"\n" +
//                        "                      ],\n" +
//                        "                    },\n" +
//                        "                    {\n" +
//                        "                      $lte: [\n" +
//                        "                        \"$$item.rates.effectiveDate\",\n" +
//                        "                          ISODate(\"" + getISODate(requiredDate) + "\")\n" +
//                        "                      ],\n" +
//                        "                    },\n" +
//                        "                  ],\n")).toDocument();
//
//        AggregationExpression archiveAddressMatchExpression = AggregationExpression.from(MongoExpression.create(
//                "         $expr: {\n" +
//                        "                  $and: [\n" +
//                        "                    {\n" +
//                        "                      $eq: [\n" +
//                        "                        \"$address.state\",\n" +
//                        "                        \"$$address.state\"\n" +
//                        "                      ],\n" +
//                        "                    },\n" +
//                        "                    {\n" +
//                        "                      $eq: [\n" +
//                        "                        \"$address.county\",\n" +
//                        "                        \"$$address.county\"\n" +
//                        "                      ],\n" +
//                        "                    },\n" +
//                        "                    {\n" +
//                        "                      $eq: [\n" +
//                        "                        \"$address.city\",\n" +
//                        "                        \"$$address.city\"\n" +
//                        "                      ],\n" +
//                        "                    },\n" +
//                        "                    {\n" +
//                        "                      $eq: [\n" +
//                        "                        \"$address.isUnincorporated\",\n" +
//                        "                        \"$$address.isUnincorporated\"\n" +
//                        "                      ],\n" +
//                        "                    },\n" +
//                        "                    {\n" +
//                        "                      $eq: [\n" +
//                        "                        \"$address.hasPlusFourZipCode\",\n" +
//                        "                        \"$$address.hasPlusFourZipCode\"\n" +
//                        "                      ],\n" +
//                        "                    },\n" +
//                        "                    {\n" +
//                        "                      $eq: [\n" +
//                        "                        \"$address.zip\",\n" +
//                        "                        \"$$address.zip\"\n" +
//                        "                      ],\n" +
//                        "                    },\n" +
//                        "                    {\n" +
//                        "                      $eq: [\n" +
//                        "                        \"$address.lowerPlusFourDigits\",\n" +
//                        "                        \"$$address.lowerPlusFourDigits\"\n" +
//                        "                      ],\n" +
//                        "                    },\n" +
//                        "                    {\n" +
//                        "                      $eq: [\n" +
//                        "                        \"$address.upperPlusFourDigits\",\n" +
//                        "                        \"$$address.upperPlusFourDigits\"\n" +
//                        "                      ]\n" +
//                        "                    }\n" +
//                        "                  ]\n" +
//                        "               }\n"
//        ));
//
//        MatchOperation matchedAddressOperation = new MatchOperation(archiveAddressMatchExpression);
//
//        SortOperation sortOperation = new SortOperation(Sort.by("rates.effectiveDate").descending());
//
//        String internalArchiveCollectionName = InternalRatesCollectionNames.stateArchiveInternalCollectionName(addressWithDate.address().state());
//
//
//        TypedAggregation<InternalTaxRatesData> aggregation = Aggregation.newAggregation(InternalTaxRatesData.class,
//                Aggregation.match(matchCriteria),
//                Aggregation.addFields().addFieldWithValue("rates",
//                        ConditionalOperators.Cond.when(Criteria.where("currentRates.effectiveDate")
//                                        .lte(requiredDate))
//                                .thenValueOf("currentRates")
//                                .otherwise(ConditionalOperators.Cond.when(Criteria.where("firstPastRates.effectiveDate")
//                                                .lte(requiredDate))
//                                        .thenValueOf("firstPastRates")
//                                        .otherwise(ConditionalOperators.Cond.when(Criteria.where("secondPastRates.effectiveDate")
//                                                        .lte(requiredDate))
//                                                .thenValueOf("secondPastRates")
//                                                .otherwise(new Document("found", false))))).build(),
//                Aggregation.facet(
//                                Aggregation.match(Criteria.where("rates.found").is(false)),
//                                Aggregation.lookup().from(internalArchiveCollectionName).let(
//                                        VariableOperators.Let.ExpressionVariable.newVariable("address").forField("$address")
//                                ).pipeline(matchedAddressOperation, sortOperation).as("joinedData"),
//                                Aggregation.addFields().addFieldWithValue("joinedData",
//                                        $arrayElemAt($filter("joinedData", "item", ratesInArchiveCollectionCond), 0)).build(),
//                                SetOperation.set("rates").toValue("$joinedData.rates").and().set("address").toValue("$joinedData.address"),
//                                Aggregation.project("address", "rates"))
//                        .as("rateNotFoundBranch")
//                        .and(Aggregation.match(Criteria.where("rates.found").ne(false)),
//                                Aggregation.project("address", "rates")).as("rateFoundBranch"),
//                Aggregation.project()
//                        .and(ArrayOperators.ConcatArrays.arrayOf("$rateNotFoundBranch").concat("$rateFoundBranch"))
//                        .as("result"),
//                Aggregation.unwind("$result"),
//                Aggregation.addFields().addFieldWithValue("rates", "$result.rates").addFieldWithValue("address", "$result.address").build(),
//                Aggregation.project("rates", "address")
//        );
//
//        return aggregation;
//    }
//}