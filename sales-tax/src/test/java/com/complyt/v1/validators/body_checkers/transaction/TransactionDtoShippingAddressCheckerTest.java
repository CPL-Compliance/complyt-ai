package com.complyt.v1.validators.body_checkers.transaction;

import com.complyt.business.address.CountryIsSupportedNonUsaChecker;
import com.complyt.v1.models.transaction.MandatoryAddressDto;
import com.complyt.v1.models.transaction.TransactionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
//@WebFluxTest
class TransactionDtoShippingAddressCheckerTest {
    private TransactionDtoShippingAddressChecker transactionDtoShippingAddressChecker;
    private TransactionDto transactionDto;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setUp() {
        transactionDtoShippingAddressChecker = new TransactionDtoShippingAddressChecker();
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        transactionDto = testUtilities.createTransactionDto(UUID.randomUUID().toString())
                .withCustomer(null);
    }

    @Test
    void check_PartialAddressWithMissingZipCode_ShouldReturnErrors() {
        // Given
        MandatoryAddressDto addressWithoutZip = new MandatoryAddressDto(null, "USA",null,"CO",null,null,null,true);
        TransactionDto transactionDtoWithoutZip = transactionDto.withShippingAddress(addressWithoutZip);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithoutZip);

        StepVerifier.create(errorMessages)
                .expectNext("Address.zip may not be blank in a non partial address")
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithInvalidState_ShouldReturnErrors() {
        // Given
        MandatoryAddressDto addressWithoutZip = new MandatoryAddressDto(null, "USA",null,"Invalid State",null,null,"80001",true);
        TransactionDto transactionDtoWithoutZip = transactionDto.withShippingAddress(addressWithoutZip);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithoutZip);

        StepVerifier.create(errorMessages)
                .expectNext("Address.state in usa address is not recognized")
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithStateNull_ShouldReturnErrors() {
        // Given
        MandatoryAddressDto addressWithoutZip = new MandatoryAddressDto(null, "USA",null,null,null,null,"80001",true);
        TransactionDto transactionDtoWithoutZip = transactionDto.withShippingAddress(addressWithoutZip);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithoutZip);

        StepVerifier.create(errorMessages)
                .expectComplete().verify();
    }

    @Test
    void check_PartialAddressWithStateBlank_ShouldReturnErrors() {
        // Given
        MandatoryAddressDto addressWithoutZip = new MandatoryAddressDto(null, "USA",null,"",null,null,"80001",true);
        TransactionDto transactionDtoWithoutZip = transactionDto.withShippingAddress(addressWithoutZip);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithoutZip);

        StepVerifier.create(errorMessages)
                .expectComplete().verify();
    }

    @Test
    void check_FullValidAddress_ShouldReturnEmpty() {
        // Given
        MandatoryAddressDto addressWithoutZip = new MandatoryAddressDto("City", "USA","County","CO","Street","Region","80001",false);
        TransactionDto transactionDtoWithoutZip = transactionDto.withShippingAddress(addressWithoutZip);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithoutZip);

        StepVerifier.create(errorMessages).expectComplete().verify();
    }

    @Test
    void check_FullAddressWithInvalidStateAndNullZip_ShouldReturnErrors() {
        // Given
        MandatoryAddressDto addressWithoutZip = new MandatoryAddressDto("City", "USA","County","Invalid State","Street","Region",null,false);
        TransactionDto transactionDtoWithoutZip = transactionDto.withShippingAddress(addressWithoutZip);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithoutZip);

        StepVerifier.create(errorMessages)
                .expectNext("Address.zip may not be blank in a non partial address")
                .expectNext("Address.state in usa address is not recognized")
                .verifyComplete();
    }

    @Test
    void check_FullAddressWithMissingFields_ShouldReturnErrors() {
        // Given
        MandatoryAddressDto addressWithoutZip = new MandatoryAddressDto(null, "USA",null,null,null,null,"80001",false);
        TransactionDto transactionDtoWithoutZip = transactionDto.withShippingAddress(addressWithoutZip);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithoutZip);

        StepVerifier.create(errorMessages)
                .expectNext("Address.state may not be blank in a non partial address")
                .expectNext("Address.street may not be blank in a non partial address")
                .expectNext("Address.city may not be blank in a non partial address")
                .expectNext("Address.state in usa address is not recognized")
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithMissingZipAndBlankState_ShouldReturnErrors() {
        // Given
        MandatoryAddressDto address = new MandatoryAddressDto(null, "USA", null, "", null, null, null, true);
        TransactionDto transactionDtoWithMissingZipAndBlankState = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithMissingZipAndBlankState);

        StepVerifier.create(errorMessages)
                .expectNext("Address.zip may not be blank in a non partial address")
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithUnsupportedCountry_ShouldReturnError() {
        // Given
        MandatoryAddressDto address = new MandatoryAddressDto(null, "UnsupportedCountry", null, null, null, null, "80001", true);
        TransactionDto transactionDtoWithUnsupportedCountry = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithUnsupportedCountry);

        StepVerifier.create(errorMessages)
                .expectNext("invalid country provided. Please provide a valid country name or abbreviation.\";")
                .verifyComplete();
    }

    @Test
    void check_FullAddressWithBlankCity_ShouldReturnError() {
        // Given
        MandatoryAddressDto address = new MandatoryAddressDto("", "USA", null, "CO", "Street", null, "80001", false);
        TransactionDto transactionDtoWithBlankCity = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithBlankCity);

        StepVerifier.create(errorMessages)
                .expectNext("Address.city may not be blank in a non partial address")
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithEmptyFields_ShouldReturnError() {
        // Given
        MandatoryAddressDto address = new MandatoryAddressDto("", "USA", "", "", "", "", "", true);
        TransactionDto transactionDtoWithEmptyFields = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithEmptyFields);

        StepVerifier.create(errorMessages)
                .expectNext("Address.zip may not be blank in a non partial address")
                .verifyComplete(); // No specific error for empty fields in partial addresses
    }

    @Test
    void check_FullAddressWithAllFieldsMissing_ShouldReturnErrors() {
        // Given
        MandatoryAddressDto address = new MandatoryAddressDto(null, "USA", null, null, null, null, null, false);
        TransactionDto transactionDtoWithAllFieldsMissing = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithAllFieldsMissing);

        StepVerifier.create(errorMessages)
                .expectNext("Address.state may not be blank in a non partial address")
                .expectNext("Address.street may not be blank in a non partial address")
                .expectNext("Address.city may not be blank in a non partial address")
                .expectNext("Address.zip may not be blank in a non partial address")
                .expectNext("Address.state in usa address is not recognized")
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithNullZipAndValidState_ShouldReturnEmpty() {
        // Given
        MandatoryAddressDto address = new MandatoryAddressDto(null, "USA", null, "CO", null, null, null, true);
        TransactionDto transactionDtoWithNullZipAndValidState = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithNullZipAndValidState);

        StepVerifier.create(errorMessages)
                .expectNext("Address.zip may not be blank in a non partial address")
                .expectComplete().verify(); // No error for partial address with null zip if state is valid
    }

    @Test
    void check_FullAddressWithValidZipAndUnsupportedCountry_ShouldReturnError() {
        // Given
        MandatoryAddressDto address = new MandatoryAddressDto("City", "UnsupportedCountry", null, "CO", "Street", null, "80001", false);
        TransactionDto transactionDtoWithUnsupportedCountry = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithUnsupportedCountry);

        StepVerifier.create(errorMessages)
                .expectNext("invalid country provided. Please provide a valid country name or abbreviation.\";")
                .verifyComplete();
    }

    @Test
    void check_AddressWithNonUsaCountry_ShouldReturnEmpty() {
        // Given
        MandatoryAddressDto address = new MandatoryAddressDto("City", "Canada", null, null, "Street", null, "M4B 1B4", false);
        TransactionDto transactionDtoWithNonUsaCountry = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithNonUsaCountry);

        StepVerifier.create(errorMessages)
                .verifyComplete(); // Non-USA country with supported country should pass with no errors
    }

    @Test
    void check_PartialAddressWithBlankStateInUsa_ShouldReturnNoError() {
        // Given
        MandatoryAddressDto address = new MandatoryAddressDto(null, "USA", null, "", null, null, "80001", true);
        TransactionDto transactionDtoWithBlankState = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithBlankState);

        StepVerifier.create(errorMessages)
                .verifyComplete(); // No specific error for blank state in partial address
    }
}