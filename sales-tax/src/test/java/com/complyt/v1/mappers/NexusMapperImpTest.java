package com.complyt.v1.mappers;

import com.complyt.domain.Nexus;
import com.complyt.v1.models.nexus.NexusDto;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import testUtils.unit_test.UnitTestUtilities;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class NexusMapperImpTest {

    private Nexus nexus;
    private NexusDto nexusDto;
    UnitTestUtilities testUtilities;

    @BeforeEach
    void setup() {
        testUtilities = new UnitTestUtilities(LocalDateTime.now(), UUID.randomUUID().toString());
        LocalDateTime localDateTime = LocalDateTime.now();
        nexus = new Nexus(localDateTime);
        nexusDto = new NexusDto(localDateTime);
    }


    @Test
    public void nexusToNexusDto_Nexus_returnNexusDto() {
        // Given
        Nexus givenNexus = nexus;

        // When
        NexusDto actualNexusDto = NexusMapper.INSTANCE.nexusToNexusDto(givenNexus);

        // Then
        assertEquals(nexusDto, actualNexusDto);
    }

    @Test
    public void nexusDtoToNexus_NexusDto_returnNexus() {
        // Given
        NexusDto givenNexusDto = nexusDto;

        // When
        Nexus actualNexus = NexusMapper.INSTANCE.nexusDtoToNexus(givenNexusDto);

        // Then
        assertEquals(nexus, actualNexus);
    }

    @Test
    public void mapping_NullState_ReturnNull() {
        // Given + When
        Nexus givenNexus = NexusMapper.INSTANCE.nexusDtoToNexus(null);
        NexusDto givenNexusDto = NexusMapper.INSTANCE.nexusToNexusDto(null);

        // Then
        assertNull(givenNexusDto);
        assertNull(givenNexus);
    }

    @Test
    public void nexusDtoToNexus_NexusDtoIsNull_returnNull() {
        // Given
        NexusDto givenNexusDto = null;

        // When
        Nexus actualNexus = NexusMapper.INSTANCE.nexusDtoToNexus(givenNexusDto);

        // Then
        assertNull(actualNexus);
    }

    @Test
    public void nexusToNexusDto_NexusIsNull_returnNull() {
        // Given
        Nexus givenNexus = null;

        // When
        NexusDto actualNexusDto = NexusMapper.INSTANCE.nexusToNexusDto(givenNexus);

        // Then
        assertNull(actualNexusDto);
    }
}