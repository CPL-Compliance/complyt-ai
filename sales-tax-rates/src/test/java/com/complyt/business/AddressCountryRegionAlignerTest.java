package com.complyt.business;

import com.complyt.domain.gt.GtAddress;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import testUtils.TestUtilities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class AddressCountryRegionAlignerTest {

    @InjectMocks
    AddressCountryRegionAligner qddressCountryRegionAligner;

    @Test
    void align_NullGtAddressPassed_ThrowsException() {
        // Given
        GtAddress gtAddress = null;

        // When + Then
        NullPointerException nullPointerException = assertThrows(NullPointerException.class, () -> {
            qddressCountryRegionAligner.align(gtAddress);
        });

        assertEquals(nullPointerException.getMessage(), "gtAddress " + TestUtilities.LOMBOK_NON_NULL_ANNOTATION_MESSAGE);
    }

    @Test
    void align_GtAddressWithCorrectRegion_AlignedToRegion() {
        // Given
        GtAddress gtAddress = TestUtilities.createCanadaGtAddress().withRegion("Quebec");

        // When
        GtAddress alignedGtAddress = qddressCountryRegionAligner.align(gtAddress);

        // Then
        assertEquals(gtAddress, alignedGtAddress);
    }

    @Test
    void align_GtAddressWithCorrectRegionButWrongCapitalization_AlignedToRegion() {
        // Given
        GtAddress gtAddress = TestUtilities.createCanadaGtAddress().withRegion("qUEBEC");

        // When
        GtAddress alignedGtAddress = qddressCountryRegionAligner.align(gtAddress);

        // Then
        assertEquals(gtAddress.withRegion("Quebec"), alignedGtAddress);
    }

    @Test
    void align_GtAddressWithExistingMisspellRegion_AlignedToRegion() {
        // Given
        GtAddress gtAddress = TestUtilities.createCanadaGtAddress().withRegion("Quebecq");

        // When
        GtAddress alignedGtAddress = qddressCountryRegionAligner.align(gtAddress);

        // Then
        assertEquals(gtAddress.withRegion("Quebec"), alignedGtAddress);
    }

    @Test
    void align_GtAddressWithNonExistingMisspellRegion_RegionIsNull() {
        // Given
        GtAddress gtAddress = TestUtilities.createCanadaGtAddress().withRegion("Quebecccccccc");

        // When
        GtAddress alignedGtAddress = qddressCountryRegionAligner.align(gtAddress);

        // Then
        assertEquals(gtAddress.withRegion(null), alignedGtAddress);
    }

    @Test
    void align_GtAddressWithNullRegion_ReturnGtAddress() {
        // Given
        GtAddress gtAddress = TestUtilities.createCanadaGtAddress().withRegion(null);

        // When
        GtAddress alignedGtAddress = qddressCountryRegionAligner.align(gtAddress);

        // Then
        assertEquals(gtAddress, alignedGtAddress);
    }
}
