package com.complyt.v1.mappers;

import com.complyt.domain.Nexus;
import com.complyt.v1.models.nexus.NexusDto;

import java.time.LocalDateTime;

public class NexusMapperImp implements NexusMapper{

    @Override
    public NexusDto nexusToNexusDto(Nexus nexus) {
        if (nexus == null ) {
            return null;
        }

        LocalDateTime taxableDate = nexus.getTaxableDate();

        return new NexusDto(taxableDate);
    }

    @Override
    public Nexus nexusDtoToNexus(NexusDto nexusDto) {
        if (nexusDto == null ) {
            return null;
        }

        LocalDateTime taxableDate = nexusDto.taxableDate();

        return new Nexus(taxableDate);
    }
}
