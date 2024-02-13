package com.complyt.v1.mappers;

import com.complyt.domain.ClientTracking;
import com.complyt.domain.Nexus;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.v1.models.ClientTrackingDtoTenant;
import com.complyt.v1.models.TimestampsDto;
import com.complyt.v1.models.nexus.NexusDto;

import java.time.LocalDateTime;

public class ClientTrackingMapperImp implements ClientTrackingMapper{

    private final TimestampsMapper timestampsMapper = TimestampsMapper.INSTANCE;
    private final NexusMapper nexusMapper = NexusMapper.INSTANCE;

    @Override
    public ClientTracking ClientTrackingDtoTenantToClientTracking(ClientTrackingDtoTenant ClientTrackingDtoTenant) {
        if (ClientTrackingDtoTenant == null) {
            return null;
        }

        String id= null;
        String tenantId = null;
        Timestamps internalTimestamps;

        Nexus nexus = nexusMapper.nexusDtoToNexus(ClientTrackingDtoTenant.nexus());
        String name = ClientTrackingDtoTenant.name();
        internalTimestamps = timestampsMapper.timestampsDtoTotimestamps(ClientTrackingDtoTenant.internalTimestamps());

        return new ClientTracking(id,tenantId, nexus, name, internalTimestamps);
    }

    @Override
    public ClientTrackingDtoTenant clientTrackingToClientTrackingDtoTenant(ClientTracking clientTracking) {
        if (clientTracking == null) {
            return null;
        }
        NexusDto nexusDto = nexusMapper.nexusToNexusDto(clientTracking.getNexus());
        String name = clientTracking.getName();
        TimestampsDto internalTimestamps = timestampsMapper.timestampsTotimestampsDto(clientTracking.getInternalTimestamps());
        String tenantId = clientTracking.getTenantId();

        return new ClientTrackingDtoTenant(nexusDto, name, internalTimestamps, tenantId);
    }

}
