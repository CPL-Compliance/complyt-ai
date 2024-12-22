package io.complyt.business.address_checkers;

import io.complyt.domain.Address;
import io.complyt.domain.CachedAddressData;
import io.complyt.utils.exceptions.types.ObjectNotValidException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import test_utils.TestUtilities;


import static org.junit.jupiter.api.Assertions.*;

import static org.junit.Assert.assertThrows;

@ExtendWith(MockitoExtension.class)
class HereAddressCheckerTest {

    @InjectMocks
    HereAddressChecker hereAddressChecker;

    private Address address;
    private CachedAddressData cachedAddressData;
    private float score;

    @BeforeEach
    void setUp() {
        address = TestUtilities.getAddress();
        cachedAddressData = TestUtilities.getCachedAddressData();
        score = 0.9f;
    }

    @Test
    void checkAddress_ScoreAboveAcceptable_ReturnsAddress() {
        // When
        cachedAddressData = cachedAddressData.withScore(score);
        Mono<CachedAddressData> addressMono = hereAddressChecker.checkAddress(cachedAddressData, address);

        // Then
        StepVerifier.create(addressMono).expectNext(cachedAddressData).verifyComplete();
    }

    @Test
    void checkAddress_ScoreUnacceptable_ReturnsError() {
        // Given
        score = 0.1f;
        cachedAddressData = cachedAddressData.withScore(score);

        // When
        Mono<CachedAddressData> addressMono = hereAddressChecker.checkAddress(cachedAddressData, address);

        // Then
        StepVerifier.create(addressMono).expectNextCount(0).verifyComplete();
    }

    @Test
    void approveResponseIfZipIncludesRequestZip_zipIsNull_ReturnMonoEmpty() {
        cachedAddressData = cachedAddressData.withZip(null).withScore(1);

        // When
        Mono<CachedAddressData> addressMono = hereAddressChecker.checkAddress(cachedAddressData, address);

        // Then
        StepVerifier.create(addressMono).expectNext(cachedAddressData.withZip(address.zip())).verifyComplete();
    }

    @Test
    void approveResponseIfZipIncludesRequestZip_zipIsEmpty_ReturnsZip() {
        cachedAddressData = cachedAddressData.withZip("").withScore(1);

        // When
        Mono<CachedAddressData> addressMono = hereAddressChecker.checkAddress(cachedAddressData, address);

        // Then
        StepVerifier.create(addressMono).expectNext(cachedAddressData.withZip(address.zip())).verifyComplete();
    }

    @Test
    void approveResponseIfZipIncludesRequestZip_requestAddressStartsWithOutsource_ReturnsZip() {
        // Given
        cachedAddressData = cachedAddressData.withZip("11111").withScore(1);;
        address = address.withZip("11111-12345");

        // When
        Mono<CachedAddressData> addressMono = hereAddressChecker.checkAddress(cachedAddressData, address);

        // Then
        StepVerifier.create(addressMono).expectNext(cachedAddressData).verifyComplete();
    }

    @Test
    void zipMismatch_OverrideZipToRequestZip_ReturnsZip() {
        // Given
        cachedAddressData = cachedAddressData.withZip("12345").withScore(1);;
        address = address.withZip("11111");

        // When
        Mono<CachedAddressData> addressMono = hereAddressChecker.checkAddress(cachedAddressData, address);

        // Then
        StepVerifier.create(addressMono).expectNext(cachedAddressData.withZip("11111")).verifyComplete();
    }


    @Test
    void build_NullCachedAddressData_ThrowsException() {
        // Given
        CachedAddressData nullAddress = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            hereAddressChecker.checkAddress(nullAddress, address);
        });

        assertEquals(nullPointerException.getMessage(), "data " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }
    @Test
    void build_NullCachedAddress_ThrowsException() {
        // Given
        Address nullAddress = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            hereAddressChecker.checkAddress(cachedAddressData, nullAddress);
        });

        assertEquals(nullPointerException.getMessage(), "requestAddress " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }
}