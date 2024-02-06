package com.complyt.v1.mappers;

import com.complyt.domain.ClientTracking;
import com.complyt.domain.Nexus;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.v1.models.ClientTrackingDto;
import com.complyt.v1.models.TimestampsDto;
import com.complyt.v1.models.nexus.NexusDto;

import java.time.LocalDateTime;

public class ClientTrackingMapperImp implements ClientTrackingMapper{

    private final TimestampsMapper timestampsMapper = TimestampsMapper.INSTANCE;
    private final NexusMapper nexusMapper = NexusMapper.INSTANCE;

    @Override
    public ClientTracking clientTrackingDtoToClientTracking(ClientTrackingDto clientTrackingDto) {
        if (clientTrackingDto == null) {
            return null;
        }

        String id= null;
        String tenantId = null;
        Nexus nexus = nexusMapper.nexusDtoToNexus(clientTrackingDto.nexus());
        String name = clientTrackingDto.name();
        Timestamps internalTimestamps = timestampsMapper.timestampsDtoTotimestamps(clientTrackingDto.internalTimestamps());

        return new ClientTracking(id,tenantId, nexus, name, internalTimestamps);
    }

    @Override
    public ClientTrackingDto clientTrackingToClientTrackingDto(ClientTracking clientTracking) {
        if (clientTracking == null) {
            return null;
        }
        NexusDto nexusDto = nexusMapper.nexusToNexusDto(clientTracking.getNexus());
        String name = clientTracking.getName();
        TimestampsDto internalTimestamps = timestampsMapper.timestampsTotimestampsDto(clientTracking.getInternalTimestamps());

        return new ClientTrackingDto(nexusDto, name, internalTimestamps);
    }

}
