package com.complyt.services.nexus;

import com.complyt.business.nexus.checker.NexusChecker;
import com.complyt.business.nexus.data_extractor.NexusCalculator;
import com.complyt.business.query.TimeFrameQueryBuilder;
import com.complyt.domain.Order;
import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusTracking;
import com.complyt.services.ClientTrackingService;
import com.complyt.services.OrderService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class NexusService {

    @NonNull
    @Qualifier("nexusTrackingServiceImpl")
    private NexusTrackingService nexusTrackingService;

    @NonNull
    @Qualifier("nexusStateRuleServiceImpl")
    private NexusStateRuleService nexusStateRuleService;

    @NonNull
    @Qualifier("orderServiceImpl")
    private OrderService orderService;

    @NonNull
    @Qualifier("clientTrackingServiceImpl")
    private ClientTrackingService clientTrackingService;

    @NonNull
    private NexusCalculator nexusCalculator;

    @NonNull
    private NexusChecker nexusChecker;

    @NonNull
    private TimeFrameQueryBuilder timeFrameQueryBuilder;

    public Mono<NexusTracking> findTrackingByState(String state) {
        return nexusTrackingService.findByState(state);
    }

    public Mono<NexusStateRule> findRuleByState(String state) {
        return nexusStateRuleService.findByState(state);
    }

    public Mono<Boolean> hasNexus(@NonNull String state) {
        return findTrackingByState(state)
                .map(nexusTracking -> nexusChecker.hasNexus(nexusTracking));
    }

    public Mono<Order> handle(@NonNull Order order) {
        return findRuleByState(order.getShippingAddress().getState())
                .map(nexusStateRule -> {
                    clientTrackingService.getNexusInfo()
                            .map(nexusInfo -> {

                                Query query = timeFrameQueryBuilder.buildNexusTimeFrame(nexusInfo, nexusStateRule);
                                return orderService.getOrdersByFilter(query).collectList()
                                        .flatMap(orders -> aggregateNexusInfo(orders, nexusStateRule));
                            });
                    
                    return order;
                });
    }

    public Mono<NexusTracking> aggregateNexusInfo(List<Order> orders, NexusStateRule stateRule) {
        NexusCalculationSummary summary = nexusCalculator.calculate(orders, stateRule);
        boolean passedThreshold = nexusChecker.passedThreshold(summary, stateRule);

        if (passedThreshold) {
            return findTrackingByState(stateRule.getState().getAbbreviation())
                    .flatMap(nexusTrackingService::saveWithEconomicQualified);
        }
        return Mono.empty();

    }
}


//    public Mono<Order> handle(@NonNull Order order) {
//        return findRuleByState(order.getShippingAddress().getState())
//                .map(nexusStateRule -> {
//                    clientTrackingService.getNexusInfo()
//                            .map(nexusInfo -> {
//
//                                Query query = timeFrameQueryBuilder.buildNexusTimeFrame(nexusInfo, nexusStateRule);
//                                return orderService.getOrdersByFilter(query).collectList()
//                                        .map(orders -> nexusCalculator.calculate(orders, nexusStateRule))
//                                        .map(nexusCalculationSummary -> nexusChecker.passedThreshold(nexusCalculationSummary, nexusStateRule))
//                                        .flatMap(exceeded -> exceeded ? findTrackingByState(order.getShippingAddress().getState()) : Mono.empty())
//                                        .flatMap(nexusTrackingService::saveWithEconomicQualified);
//                            });
//
//                    return order;
//                });
//    }
//
//
//}


//                                orderService.getOrdersByFilter(query).collectList()
//                                        .map(orders -> nexusCalculator.calculate(orders, nexusStateRule))
//                                        .map(nexusCalculationSummary -> nexusChecker.passedThreshold(nexusCalculationSummary, nexusStateRule))
//                                        .flatMap(exceeded -> exceeded ? findTrackingByState(order.getShippingAddress().getState()) : Mono.empty())
//                                        .flatMap(nexusTrackingService::saveWithEconomicQualified);