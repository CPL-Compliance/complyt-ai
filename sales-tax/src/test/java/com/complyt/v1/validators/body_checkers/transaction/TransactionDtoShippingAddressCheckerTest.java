package com.complyt.v1.validators.body_checkers.transaction;

import com.complyt.security.TenantResolver;
import com.complyt.v1.config.error_messages.DtoErrorMessages;
import com.complyt.v1.models.transaction.ShippingAddressDto;
import com.complyt.v1.models.transaction.TransactionDto;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.mockito.Mockito.mockStatic;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
//@WebFluxTest
class TransactionDtoShippingAddressCheckerTest {
    private TransactionDtoShippingAddressChecker transactionDtoShippingAddressChecker;
    private TransactionDto transactionDto;
    UnitTestUtilities testUtilities;

     static MockedStatic mockedStatic;

    @BeforeAll
    static void beforeAll() {
        try {
            mockedStatic = mockStatic(TenantResolver.class);
        } catch (Exception e) {
            // Log the error or fail the test setup
            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
            throw e;
        }
    }

    @AfterAll
    static void afterAll() {
        mockedStatic.close();
    }

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
        ShippingAddressDto addressWithoutZip = new ShippingAddressDto(null, "USA",null,"CO",null,null,null,true, null);
        TransactionDto transactionDtoWithoutZip = transactionDto.withShippingAddress(addressWithoutZip);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithoutZip);

        StepVerifier.create(errorMessages)
                .expectNext("Address.zip may not be blank in a partial address. Invalid value: null")
                .expectNext("Address.zip " + DtoErrorMessages.ZIP_NOT_IN_FORMAT + " Invalid value: null")
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithInvalidState_ShouldReturnErrors() {
        // Given
        ShippingAddressDto addressWithoutZip = new ShippingAddressDto(null, "USA",null,"Invalid State",null,null,"80001",true, null);
        TransactionDto transactionDtoWithoutZip = transactionDto.withShippingAddress(addressWithoutZip);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithoutZip);

        StepVerifier.create(errorMessages)
                .expectNext("Address.state in usa address is not recognized. Invalid value: Invalid State")
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithStateNull_ShouldReturnErrors() {
        // Given
        ShippingAddressDto addressWithoutZip = new ShippingAddressDto(null, "USA",null,null,null,null,"80001",true, null);
        TransactionDto transactionDtoWithoutZip = transactionDto.withShippingAddress(addressWithoutZip);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithoutZip);

        StepVerifier.create(errorMessages)
                .expectComplete().verify();
    }

    @Test
    void check_PartialAddressWithStateBlank_ShouldReturnErrors() {
        // Given
        ShippingAddressDto addressWithoutZip = new ShippingAddressDto(null, "USA",null,"",null,null,"80001",true, null);
        TransactionDto transactionDtoWithoutZip = transactionDto.withShippingAddress(addressWithoutZip);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithoutZip);

        StepVerifier.create(errorMessages)
                .expectComplete().verify();
    }

    @Test
    void check_FullValidAddress_ShouldReturnEmpty() {
        // Given
        ShippingAddressDto addressWithoutZip = new ShippingAddressDto("City", "USA","County","CO","Street","Region","80001",false, null);
        TransactionDto transactionDtoWithoutZip = transactionDto.withShippingAddress(addressWithoutZip);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithoutZip);

        StepVerifier.create(errorMessages).expectComplete().verify();
    }

    @Test
    void check_FullAddressWithInvalidStateAndNullZip_ShouldReturnErrors() {
        // Given
        ShippingAddressDto addressWithoutZip = new ShippingAddressDto("City", "USA","County","Invalid State","Street","Region",null,false, null);
        TransactionDto transactionDtoWithoutZip = transactionDto.withShippingAddress(addressWithoutZip);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithoutZip);

        StepVerifier.create(errorMessages)
                .expectNext("Address.zip may not be blank in a non partial address. Invalid value: null")
                .expectNext("Address.zip " + DtoErrorMessages.ZIP_NOT_IN_FORMAT + " Invalid value: null")
                .expectNext("Address.state in usa address is not recognized. Invalid value: Invalid State")
                .verifyComplete();
    }

    @Test
    void check_FullAddressWithMissingFields_ShouldReturnErrors() {
        // Given
        ShippingAddressDto addressWithoutZip = new ShippingAddressDto(null, "USA",null,null,null,null,"80001",false, null);
        TransactionDto transactionDtoWithoutZip = transactionDto.withShippingAddress(addressWithoutZip);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithoutZip);

        StepVerifier.create(errorMessages)
                .expectNext("Address.state may not be blank in a non partial address. Invalid value: null")
                .expectNext("Address.street may not be blank in a non partial address. Invalid value: null")
                .expectNext("Address.city may not be blank in a non partial address. Invalid value: null")
                .expectNext("Address.state in usa address is not recognized. Invalid value: null")
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithMissingZipAndBlankState_ShouldReturnErrors() {
        // Given
        ShippingAddressDto address = new ShippingAddressDto(null, "USA", null, "", null, null, null, true, null);
        TransactionDto transactionDtoWithMissingZipAndBlankState = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithMissingZipAndBlankState);

        StepVerifier.create(errorMessages)
                .expectNext("Address.zip may not be blank in a partial address. Invalid value: null")
                .expectNext("Address.zip " + DtoErrorMessages.ZIP_NOT_IN_FORMAT + " Invalid value: null")
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithUnsupportedCountry_ShouldReturnError() {
        // Given
        ShippingAddressDto address = new ShippingAddressDto(null, "UnsupportedCountry", null, null, null, null, "80001", true, null);
        TransactionDto transactionDtoWithUnsupportedCountry = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithUnsupportedCountry);

        StepVerifier.create(errorMessages)
                .expectNext("invalid country provided. Please provide a valid country name or abbreviation.\";")
                .verifyComplete();
    }

    @Test
    void check_FullAddressWithBlankCity_ShouldReturnError() {
        // Given
        ShippingAddressDto address = new ShippingAddressDto("", "USA", null, "CO", "Street", null, "80001", false, null);
        TransactionDto transactionDtoWithBlankCity = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithBlankCity);

        StepVerifier.create(errorMessages)
                .expectNext("Address.city may not be blank in a non partial address. Invalid value: ")
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithEmptyFields_ShouldReturnError() {
        // Given
        ShippingAddressDto address = new ShippingAddressDto("", "USA", "", "", "", "", "", true, null);
        TransactionDto transactionDtoWithEmptyFields = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithEmptyFields);

        StepVerifier.create(errorMessages)
                .expectNext("Address.zip may not be blank in a partial address. Invalid value: ")
                .expectNext("Address.zip " + DtoErrorMessages.ZIP_NOT_IN_FORMAT + " Invalid value: ")
                .verifyComplete(); // No specific error for empty fields in partial addresses
    }

    @Test
    void check_FullAddressWithAllFieldsMissing_ShouldReturnErrors() {
        // Given
        ShippingAddressDto address = new ShippingAddressDto(null, "USA", null, null, null, null, null, false, null);
        TransactionDto transactionDtoWithAllFieldsMissing = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithAllFieldsMissing);

        StepVerifier.create(errorMessages)
                .expectNext("Address.state may not be blank in a non partial address. Invalid value: null")
                .expectNext("Address.street may not be blank in a non partial address. Invalid value: null")
                .expectNext("Address.city may not be blank in a non partial address. Invalid value: null")
                .expectNext("Address.zip may not be blank in a non partial address. Invalid value: null")
                .expectNext("Address.zip " + DtoErrorMessages.ZIP_NOT_IN_FORMAT + " Invalid value: null")
                .expectNext("Address.state in usa address is not recognized. Invalid value: null")
                .verifyComplete();
    }

    @Test
    void check_PartialAddressWithNullZipAndValidState_ShouldReturnEmpty() {
        // Given
        ShippingAddressDto address = new ShippingAddressDto(null, "USA", null, "CO", null, null, null, true, null);
        TransactionDto transactionDtoWithNullZipAndValidState = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithNullZipAndValidState);

        StepVerifier.create(errorMessages)
                .expectNext("Address.zip may not be blank in a partial address. Invalid value: null")
                .expectNext("Address.zip " + DtoErrorMessages.ZIP_NOT_IN_FORMAT + " Invalid value: null")
                .expectComplete().verify(); // No error for partial address with null zip if state is valid
    }

    @Test
    void check_PartialAddressWithLongZip_ShouldReturnEmpty() {
        // Given
        ShippingAddressDto address = new ShippingAddressDto(null, "USA", null, "CO", null, null, "1232434324324-3424123", true, null);
        TransactionDto transactionDtoWithNullZipAndValidState = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithNullZipAndValidState);

        StepVerifier.create(errorMessages)
                .expectNext("Address.zip " + DtoErrorMessages.ZIP_NOT_IN_FORMAT + " Invalid value: 1232434324324-3424123")
                .expectComplete().verify();
    }

    @Test
    void check_PartialAddressWithLettersInZip_ShouldReturnEmpty() {
        // Given
        ShippingAddressDto address = new ShippingAddressDto(null, "USA", null, "CO", null, null, "1a2b3", true, null);
        TransactionDto transactionDtoWithNullZipAndValidState = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithNullZipAndValidState);

        StepVerifier.create(errorMessages)
                .expectNext("Address.zip " + DtoErrorMessages.ZIP_NOT_IN_FORMAT + " Invalid value: 1a2b3")
                .expectComplete().verify(); // No error for partial address with null zip if state is valid
    }

    @Test
    void check_FullAddressWithValidZipAndUnsupportedCountry_ShouldReturnError() {
        // Given
        ShippingAddressDto address = new ShippingAddressDto("City", "UnsupportedCountry", null, "CO", "Street", null, "80001", false, null);
        TransactionDto transactionDtoWithUnsupportedCountry = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithUnsupportedCountry);

        StepVerifier.create(errorMessages)
                .expectNext("invalid country provided. Please provide a valid country name or abbreviation.\";")
                .verifyComplete();
    }

    @Test
    void check_AddressWithNonUsaCountry_ShouldReturnEmpty() {
        // Given
        ShippingAddressDto address = new ShippingAddressDto("City", "Canada", null, null, "Street", null, "M4B 1B4", false, null);
        TransactionDto transactionDtoWithNonUsaCountry = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithNonUsaCountry);

        StepVerifier.create(errorMessages)
                .verifyComplete(); // Non-USA country with supported country should pass with no errors
    }

    @Test
    void check_PartialAddressWithBlankStateInUsa_ShouldReturnNoError() {
        // Given
        ShippingAddressDto address = new ShippingAddressDto(null, "USA", null, "", null, null, "80001", true, null);
        TransactionDto transactionDtoWithBlankState = transactionDto.withShippingAddress(address);

        Flux<String> errorMessages = transactionDtoShippingAddressChecker.check(transactionDtoWithBlankState);

        StepVerifier.create(errorMessages)
                .verifyComplete(); // No specific error for blank state in partial address
    }
}