//package com.complyt.v1.controllers.router.handler;
//
//import com.complyt.domain.State;
//import com.complyt.domain.TimeStamps;
//import com.complyt.domain.customer.exemption.*;
//import com.complyt.facades.ExemptionFacade;
//import org.bson.types.ObjectId;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.test.context.junit.jupiter.SpringExtension;
//import org.springframework.web.reactive.function.server.ServerRequest;
//import org.springframework.web.reactive.function.server.ServerResponse;
//import reactor.core.publisher.Mono;
//
//import java.time.LocalDateTime;
//import java.util.UUID;
//
//import static org.mockito.Mockito.when;
//
//@ExtendWith(SpringExtension.class)
//@ExtendWith(MockitoExtension.class)
//public class ExemptionHandlerTest {
//
//    @InjectMocks
//    ExemptionHandler exemptionHandler;
//
//    @Mock
//    ExemptionFacade exemptionFacade;
//
//    Exemption exemption;
//
//    @BeforeEach
//    void setUp() {
//        exemption = createExemption();
//    }
//
//    private Exemption createExemption() {
//        State state = new State("CA", "02", "California");
//        Classification classification = new Classification("code", "description");
//        ValidationDates validationDates = new ValidationDates(LocalDateTime.now().minusYears(1), LocalDateTime.now().plusYears(1));
//        TimeStamps internalTimeStamps = new TimeStamps(LocalDateTime.now(), LocalDateTime.now());
//        Status status = new Status("code", "name");
//        Certificate certificate = new Certificate(UUID.randomUUID().toString(), "url", "name");
//
//        return new Exemption(UUID.randomUUID().toString(), new ObjectId(), new ObjectId(),
//                state, classification, validationDates, internalTimeStamps, status, certificate, ExemptionType.FULLY);
//    }
//
//    @Test
//    void getOne_FindsExemption_ReturnsExemption() {
//        // Given
//        String id = exemption.getId();
//        ServerRequest serverRequest = new ServerRequest.create()
//
//        // When
//        when(exemptionFacade.findById(id)).thenReturn(Mono.just(exemption));
//        Mono<ServerResponse> serverResponseMono = exemptionHandler.getOne()
//
//        // Then
//    }
//
//}
