package com.complyt.services;

import com.complyt.business.timestamps_injection.ExistingClientTrackingInternalTimestampsInjector;
import com.complyt.business.timestamps_injection.NewClientTrackingInternalTimestampsInjector;
import com.complyt.domain.ClientTracking;
import com.complyt.domain.Nexus;
import com.complyt.repositories.ClientTrackingRepository;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Service
@AllArgsConstructor
@Slf4j
public class ClientTrackingServiceImpl implements ClientTrackingService {

    @NonNull
    private ClientTrackingRepository clientTrackingRepository;


    @Override
    public Mono<ClientTracking> save(ClientTracking clientTracking) {
        return clientTrackingRepository.save(clientTracking);
    }

    @Override
    public Mono<ClientTracking> findById(@NonNull String id) {
        return clientTrackingRepository.findById(id);
    }

    @Override
    public Mono<ClientTracking> getClientTracking() {
        return clientTrackingRepository.findClient();
    }

    public Mono<Nexus> getNexusInfo() {
        return getClientTracking()
                .map(ClientTracking::getNexus);
    }

    @Override
    public Mono<ClientTracking> saveByTenantId(ClientTracking clientTracking, String tenantId) {
        return clientTrackingRepository.saveByTenantId(clientTracking, tenantId);
    }

    @Override
    public Mono<ClientTracking> injectDataToExistingClientTracking(ClientTracking newClientTracking, ClientTracking originalClientTracking) {
        return Mono.just(newClientTracking).map(clientTracking -> clientTracking
                        .withInternalTimestamps(originalClientTracking.getInternalTimestamps()))
                .map(ExistingClientTrackingInternalTimestampsInjector::new)
                .map(ExistingClientTrackingInternalTimestampsInjector::inject);
    }

    @Override
    public Mono<ClientTracking> injectDataToNewClientTracking(ClientTracking clientTracking) {
        return Mono.just(clientTracking)
                .map(NewClientTrackingInternalTimestampsInjector::new)
                .map(NewClientTrackingInternalTimestampsInjector::inject);
    }

    @Override
    public Mono<ClientTracking> update(ClientTracking newClientTracking, ClientTracking originalClientTracking) {
        return injectDataToExistingClientTracking(newClientTracking, originalClientTracking)
                .map(clientTrackingWithInjectedData -> createFunctionUpdatedClientTracking(clientTrackingWithInjectedData)
                        .apply(originalClientTracking));
    }

    @Override
    public Flux<ClientTracking> findAll(int page, int size) {
        return clientTrackingRepository.findAll(page, size);
    }

    @Override
    public Flux<ClientTracking> getByName(String name) {
        return clientTrackingRepository.getByName(name);
    }

    @Override
    public Mono<ClientTracking> getByTenantId(String tenantId) {
        return clientTrackingRepository.getByTenantId(tenantId);
    }

    private Function<ClientTracking, ClientTracking> createFunctionUpdatedClientTracking(final ClientTracking clientTracking) {
        return clientTrackingInfo ->
                new ClientTracking(clientTrackingInfo.getId(), clientTrackingInfo.getTenantId(),
                        clientTracking.getNexus(), clientTracking.getName(), clientTracking.getInternalTimestamps());
    }
}
