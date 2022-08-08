package com.complyt.services.nexus;

import com.complyt.business.nexus.checker.NexusChecker;
import com.complyt.business.nexus.data_extractor.NexusCalculator;
import com.complyt.business.query.NexusOrdersSearchQueryBuilder;
import com.complyt.domain.Order;
import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.SalesTaxTracking;
import com.complyt.services.ClientTrackingService;
import com.complyt.services.OrderService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class NexusService {

    @Qualifier("salesTaxTrackingServiceImpl")
    @NonNull
    private SalesTaxTrackingService salesTaxTrackingService;

    @Qualifier("nexusStateRuleServiceImpl")
    @NonNull
    private NexusStateRuleService nexusStateRuleService;

    @Qualifier("orderServiceImpl")
    @NonNull
    private OrderService orderService;

    @Qualifier("clientTrackingServiceImpl")
    @NonNull
    private ClientTrackingService clientTrackingService;

    @NonNull
    private NexusCalculator nexusCalculator;

    @NonNull
    private NexusChecker nexusChecker;

    @NonNull
    private NexusOrdersSearchQueryBuilder nexusOrdersSearchQueryBuilder;

    public Mono<SalesTaxTracking> findTrackingByState(String state) {
        return salesTaxTrackingService.findByState(state);
    }

    public Mono<SalesTaxTracking> findTrackingByState(Order order) {
        return salesTaxTrackingService.findByState(order.getShippingAddress().getState());
    }

    public Mono<NexusStateRule> findRuleByState(String state) {
        return nexusStateRuleService.findByState(state);
    }

    public boolean hasNexus(@NonNull SalesTaxTracking salesTaxTracking) {
        return nexusChecker.hasNexus(salesTaxTracking);
    }

    public Mono<SalesTaxTracking> calculateNexusTracking(@NonNull Order order) {
        String state = order.getShippingAddress().getState();
        LocalDateTime referenceDate = order.getExternalTimeStamps().getCreatedDate();

        return clientTrackingService.getNexusInfo()
                .flatMap(nexusInfo -> findRuleByState(state)
                        .flatMap(stateRule -> {
                            Query nexusOrdersSearchQuery = nexusOrdersSearchQueryBuilder.buildNexusOrdersSearch(nexusInfo, stateRule, referenceDate);
                            return orderService.getOrdersByQuery(nexusOrdersSearchQuery)
                                    .collectList().flatMap(orders -> aggregateNexusInfo(orders, stateRule,referenceDate));
                        }));
    }

    public Mono<SalesTaxTracking> aggregateNexusInfo(List<Order> orders, NexusStateRule stateRule,LocalDateTime referenceDate) {
        NexusCalculationSummary summary = nexusCalculator.calculate(orders, stateRule);
        boolean passedThreshold = nexusChecker.passedThreshold(summary, stateRule);

        return findTrackingByState(stateRule.getState().getAbbreviation())
                .flatMap(salesTaxTracking -> passedThreshold ?
                        salesTaxTrackingService.saveWithEconomicQualified(salesTaxTracking,stateRule,referenceDate) :
                        Mono.just(salesTaxTracking)
                );
    }
}