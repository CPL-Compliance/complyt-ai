package com.complyt.services.nexus;

import com.complyt.business.nexus.checker.NexusChecker;
import com.complyt.business.nexus.data_extractor.NexusCalculator;
import com.complyt.business.query.TimeFrameQueryBuilder;
import com.complyt.domain.ClientTracking;
import com.complyt.domain.Order;
import com.complyt.domain.nexus.NexusCalculationSummary;
import com.complyt.domain.nexus.NexusStateRule;
import com.complyt.domain.nexus.NexusTracking;
import com.complyt.domain.nexus.enums.TimeFrame;
import com.complyt.services.ClientTrackingService;
import com.complyt.services.OrderService;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class NexusService {

    @Qualifier("nexusTrackingServiceImpl")
    @NonNull
    private NexusTrackingService nexusTrackingService;

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
    private TimeFrameQueryBuilder timeFrameQueryBuilder;

    public Mono<ClientTracking> getClientTracking() {
        return clientTrackingService.getClientTracking();
    }

    public Mono<NexusTracking> findTrackingByState(String state) {
        return nexusTrackingService.findByState(state);
    }

    public Mono<NexusStateRule> findRuleByState(String state) {
        return nexusStateRuleService.findByState(state);
    }

    public Mono<Boolean> hasNexus(@NonNull Order order) {
        return findTrackingByState(order.getShippingAddress().getState())
                .map(nexusTracking -> nexusChecker.hasNexus(nexusTracking));
    }

    public Flux<Order> getOrdersByTimeFrame() {
        Query query = timeFrameQueryBuilder.buildDateRange(TimeFrame.CURRENT_AND_PREVIOUS_CALENDER_YEAR);

        return orderService.getOrdersByFilter(query).log();
    }

    public Mono<Order> handle(@NonNull Order order) {
        return  Mono.empty();
    }

//    public Mono<Order> handle(@NonNull Order order) {
//        System.out.println("1 *******");
//        return clientTrackingService.getNexusInfo().log()
//                .flatMap(nexusInfo -> findRuleByState(order.getShippingAddress().getState())
//                        .flatMap(nexusStateRule -> {
//                            getOrdersByTimeFrame().collectList().log()
//                                    .subscribe(orders -> aggregateNexusInfo(orders, nexusStateRule).map(nexusTracking -> order));
//
//                        });
//                        .map(nexusStateRule -> {
//                            System.out.println("2 *******");
//                            Query query = timeFrameQueryBuilder.buildNexusTimeFrame(nexusInfo, nexusStateRule);
//
//                            getOrdersByTimeFrame()
//                                    .collectList().log()
//                                    .subscribe(orders -> aggregateNexusInfo(orders, nexusStateRule));
//
//                            return order;
//                        }));
//    }

    public Mono<NexusTracking> aggregateNexusInfo(List<Order> orders, NexusStateRule stateRule) {
        NexusCalculationSummary summary = nexusCalculator.calculate(orders, stateRule);
        boolean passedThreshold = nexusChecker.passedThreshold(summary, stateRule);
        System.out.println("3 *******");
        return findTrackingByState(stateRule.getState().getAbbreviation())
                .flatMap(nexusTracking -> passedThreshold ?
                        nexusTrackingService.saveWithEconomicQualified(nexusTracking) :
                        Mono.just(nexusTracking)).log();
    }
}
