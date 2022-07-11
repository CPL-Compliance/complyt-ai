package com.complyt.services;

import com.complyt.business.query.TimeFrameQueryBuilder;
import com.complyt.domain.Order;
import com.complyt.domain.nexus.NexusCalculator;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusTracking;
import com.complyt.domain.nexus.checker.NexusChecker;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
                    Query query = timeFrameQueryBuilder.build(nexusStateRule.getTimeFrame());
                    orderService.getOrdersByFilter(query).collectList()
                            .subscribe(orders -> System.out.println(orders));
//                            .map(orders -> nexusCalculator.calculate(orders,nexusStateRule))
//                            .map(nexusCalculationSummary -> nexusChecker.exceededNexus(nexusCalculationSummary,nexusStateRule))
//                            .flatMap(exceeded -> exceeded ? nexusTrackingService.findByState(order.getShippingAddress().getState()) : Mono.empty());
                    return order;
                });
    }
}
