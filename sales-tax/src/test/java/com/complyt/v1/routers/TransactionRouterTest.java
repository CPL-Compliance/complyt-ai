package com.complyt.v1.routers;

import com.complyt.config.ApiExceptionConfig;
import com.complyt.domain.Transaction;
import com.complyt.domain.TransactionStatus;
import com.complyt.domain.timestamps.ComplytTimestamp;
import com.complyt.facades.TransactionFacade;
import com.complyt.repositories.exceptions.OperationFailedException;
import com.complyt.v1.exceptions.GlobalErrorAttributes;
import com.complyt.v1.exceptions.GlobalExceptionHandler;
import com.complyt.v1.exceptions.types.ConflictedDataApiException;
import com.complyt.v1.handlers.TransactionHandler;
import com.complyt.v1.mappers.TransactionMapper;
import com.complyt.v1.models.*;
import com.complyt.v1.models.customer.CustomerDto;
import com.complyt.v1.validators.ValidatorConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import testUtils.TestUtilities;

import java.time.LocalDateTime;
import java.util.*;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
@WebFluxTest
@ContextConfiguration(classes = {TransactionRouter.class, TransactionHandler.class, ApiExceptionConfig.class,
        ValidatorConfig.class,
        GlobalErrorAttributes.class,
        GlobalExceptionHandler.class})
public class TransactionRouterTest implements TransactionRouterTestTemplate {

    Transaction transaction;

    TransactionDto transactionDto;

    @Autowired
    TransactionRouter transactionRouter;
    String source;
    TestUtilities testUtilities;
    @MockBean
    private TransactionFacade transactionFacade;
    @Autowired
    private WebTestClient webTestClient;

    @BeforeEach
    void setUp() {
        testUtilities = new TestUtilities(
                new ComplytTimestamp(LocalDateTime.now()), UUID.randomUUID().toString());
        transactionDto = testUtilities.createTransactionDto(UUID.randomUUID().toString())
                .withCustomer(null)
                .withExternalTimestamps(null)
                .withInternalTimestamps(null);
        transaction = TransactionMapper.INSTANCE.transactionDtoToTransaction(transactionDto);
        source = testUtilities.getUnifiedSource();
    }

    @Override
    @Test
    @WithMockUser
    public void getByExternalIdAndSource_Exists_Returns200() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        when(transactionFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(transaction));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transactionItem -> transactionItem, equalTo(transactionDto));
    }

    @Test
    @Override
    @WithMockUser
    public void getByExternalIdAndSource_DoesntExists_Returns404() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        when(transactionFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.empty());

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    public void getByExternalIdAndSource_UnauthenticatedUser_Returns401() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        when(transactionFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(transaction));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @Override
    @WithMockUser
    public void getByExternalIdAndSource_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Test
    @Override
    @WithMockUser
    public void getByExternalIdAndSource_InternalServerError_Returns500() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        when(transactionFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.error(new OperationFailedException()));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Override
    @Test
    @WithMockUser
    public void getAll_Exists_Returns200WithList() {
        // Given
        String firstId = UUID.randomUUID().toString();
        String secondId = UUID.randomUUID().toString();
        TransactionDto transactionNoId = transactionDto.withExternalId(firstId);
        TransactionDto secondTransactionNoId = transactionDto.withExternalId(secondId);
        Transaction firstTransaction = transaction.withExternalId(firstId);
        Transaction secondTransaction = transaction.withExternalId(secondId);
        List<TransactionDto> allTransactionsWithNoId = new ArrayList<>() {{
            add(transactionNoId);
            add(secondTransactionNoId);
        }};

        // When
        when(transactionFacade.getAll()).thenReturn(Flux.just(firstTransaction, secondTransaction));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(transactionDtos -> transactionDtos, equalTo(allTransactionsWithNoId));
    }

    @Test
    @Override
    @WithMockUser
    public void getAll_EmptyCollection_Returns200WithEmptyList() {
        // Given
        List<TransactionDto> allTransactionsWithNoId = new ArrayList<>();

        // When
        when(transactionFacade.getAll()).thenReturn(Flux.empty());

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(transactionDtos -> transactionDtos, equalTo(allTransactionsWithNoId));
    }

    @Test
    @Override
    public void getAll_UnauthenticatedUser_Returns401() {
        // Given
        List<TransactionDto> allTransactionsWithNoId = new ArrayList<>();

        // When
        when(transactionFacade.getAll()).thenReturn(Flux.empty());

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @Override
    @WithMockUser
    public void getAll_UserWithoutAuthorities_Returns403() {

    }

    @Test
    @Override
    @WithMockUser
    public void getAll_InternalServerError_Returns500() {
        // Given + When
        when(transactionFacade.getAll()).thenReturn(Flux.error(new OperationFailedException()));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Override
    @Test
    @WithMockUser
    public void getAllBySource_Exists_Returns200WithList() {
        // Given
        String firstId = UUID.randomUUID().toString();
        String secondId = UUID.randomUUID().toString();
        TransactionDto transactionNoId = transactionDto.withExternalId(firstId);
        TransactionDto secondTransactionNoId = transactionDto.withExternalId(secondId);
        Transaction firstTransaction = transaction.withExternalId(firstId);
        Transaction secondTransaction = transaction.withExternalId(secondId);
        List<TransactionDto> allTransactionsWithNoId = new ArrayList<>() {{
            add(transactionNoId);
            add(secondTransactionNoId);
        }};

        // When
        when(transactionFacade.getAllBySource(source)).thenReturn(Flux.just(firstTransaction, secondTransaction));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(transactionDtos -> transactionDtos, equalTo(allTransactionsWithNoId));
    }

    @Test
    @Override
    @WithMockUser
    public void getAllBySource_EmptyCollection_Returns200WithEmptyList() {
        // Given
        String firstId = UUID.randomUUID().toString();
        String secondId = UUID.randomUUID().toString();
        List<TransactionDto> allTransactionsWithNoId = new ArrayList<>();

        // When
        when(transactionFacade.getAllBySource(source)).thenReturn(Flux.empty());

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TransactionDto.class)
                .value(transactionDtos -> transactionDtos, equalTo(allTransactionsWithNoId));
    }

    @Test
    @Override
    public void getAllBySource_UnauthenticatedUser_Returns401() {
        // Given
        String firstId = UUID.randomUUID().toString();
        String secondId = UUID.randomUUID().toString();
        Transaction firstTransaction = transaction.withExternalId(firstId);
        Transaction secondTransaction = transaction.withExternalId(secondId);

        // When
        when(transactionFacade.getAllBySource(source)).thenReturn(Flux.just(firstTransaction, secondTransaction));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @Override
    @WithMockUser
    public void getAllBySource_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Test
    @Override
    @WithMockUser
    public void getAllBySource_InternalServerError_Returns500() {
        // Given + When
        when(transactionFacade.getAllBySource(source)).thenReturn(Flux.error(new OperationFailedException()));

        // Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Override
    @Test
    @WithMockUser
    public void getByComplytId_Exists_Returns200() {
        // Given
        UUID complytId = transaction.getComplytId();
        when(transactionFacade.findByComplytId(complytId)).thenReturn(Mono.just(transaction));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(transactionItem -> transactionItem, equalTo(transactionDto));
    }

    @Test
    @Override
    @WithMockUser
    public void getByComplytId_DoesntExists_Returns404() {
        // Given
        UUID complytId = transaction.getComplytId();
        when(transactionFacade.findByComplytId(complytId)).thenReturn(Mono.empty());

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    public void getByComplytId_UnauthenticatedUser_Returns401() {
        // Given
        UUID complytId = transaction.getComplytId();
        when(transactionFacade.findByComplytId(complytId)).thenReturn(Mono.empty());

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @Override
    @WithMockUser
    public void getByComplytId_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Test
    @Override
    @WithMockUser
    public void getByComplytId_InternalServerError_Returns500() {
        // Given
        UUID complytId = transaction.getComplytId();
        when(transactionFacade.findByComplytId(complytId)).thenReturn(Mono.error(new OperationFailedException()));

        // When + Then
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/complytId/" + complytId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByExternalIdAndSource_DoesntExists_Returns201() {
        // Given
        String externalId = transactionDto.externalId();

        // When + Then
        when(transactionFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.empty());
        when(transactionFacade.saveTransaction(TransactionMapper.INSTANCE.transactionDtoToTransaction(transactionDto))).thenReturn(Mono.just(transaction));
        TransactionDto expectedTransactionDto = TransactionMapper.INSTANCE.transactionToTransactionDto(transaction);

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(transactionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TransactionDto.class)
                .value(transactionDtoItem -> transactionDtoItem, equalTo(expectedTransactionDto));
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_CoupleValidationsFailure_Returns400WithErrorList() {
        // Given
        ItemDto givenItemDto = new ItemDto(-25, 4, 8000, "description", "name", "C1S1", null, null, false, 0, null, TaxableCategoryDto.TAXABLE);
        List<ItemDto> itemDtoList = testUtilities.createItemDtos(false, false);
        itemDtoList.add(givenItemDto);

        TransactionDto givenTransaction = transactionDto.withShippingAddress(null).withItems(itemDtoList);

        String externalId = transactionDto.externalId();
        String source = transactionDto.source();

        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Shipping address may not be null",
                "Unit Price can not be a negative number"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(givenTransaction)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_DifferentSourceInBody_Returns400ConflictedData() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        String differentSource = "9";

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(transactionDto.withSource(differentSource))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("The requested operation failed because there was an unresolvable conflict between two or more inputs.", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_DifferentExternalIdInBody_Returns400ConflictedData() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        String differentExternalId = UUID.randomUUID().toString();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withExternalId(differentExternalId))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("The requested operation failed because there was an unresolvable conflict between two or more inputs.", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_ExistWithDifferentComplytId_Returns400ConflictedData() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        UUID differentComplytId = UUID.randomUUID();

        // When
        when(transactionFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(transaction.withComplytId(differentComplytId)));
        when(transactionFacade.updateIfModified(externalId, source, transaction, transaction.withComplytId(differentComplytId))).thenReturn(Mono.error(new ConflictedDataApiException()));
        when(transactionFacade.saveTransaction(any())).thenReturn(Mono.empty())
        ;
        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("The requested operation failed because there was an unresolvable conflict between two or more inputs.", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_DoesntExistAndHasComplytId_Returns400ConflictedData() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();

        // When
        when(transactionFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.empty());
        when(transactionFacade.saveTransaction(any())).thenReturn(Mono.error(new ConflictedDataApiException()))
        ;
        // Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("The requested operation failed because there was an unresolvable conflict between two or more inputs.", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_BlankSource_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        String blankSource = "";
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Source should be a single digit",
                "Source may not be blank"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withSource(blankSource))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_nonDigitSource_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        String invalidSource = "d";

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withSource(invalidSource))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[Source should be a single digit]", message);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_MoreThenOneDigitSource_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        String invalidSource = "10";

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withSource(invalidSource))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    String message = (String) map.get("message");
                    assertEquals("[Source should be a single digit]", message);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_BlankExternalId_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        String blankExternalId = "";
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "External ID should be 1-256 characters maximum",
                "External ID may not be blank"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withExternalId(blankExternalId))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_LengthGreaterThen256ExternalId_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        String externalIdWithLengthOf257 = testUtilities.stringWithLength(257);

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withExternalId(externalIdWithLengthOf257))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("[External ID should be 1-256 characters maximum]", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_ComplytIdFailedToParse_Returns400() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        String invalidComplytId = "hello";

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n   \"externalId\": \"" + externalId + "\",\n" +
                        "   \"source\": \"" + source + "\",\n" +
                        "   \"complytId\": \"" + invalidComplytId + "\",\n" +
                        "   \"customerId\": \"0d3e260d-3555-4fb6-bcdd-926beb4bad51\",\n" +
                        "   \"items\": [\n" +
                        "        {\n" +
                        "            \"unitPrice\": 25,\n" +
                        "            \"totalPrice\": 5000,\n" +
                        "            \"name\": \"HW Installation Services\",\n" +
                        "            \"quantity\": 200,\n" +
                        "            \"description\": \"wd\",\n" +
                        "            \"taxCode\": \"C1S1\",\n" +
                        "            \"manualSalesTax\": false,\n" +
                        "            \"manualSalesTaxRate\": 0\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"shippingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"billingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"transactionType\": \"INVOICE\",\n" +
                        "    \"transactionStatus\": \"ACTIVE\",\n" +
                        "    \"externalTimestamps\":  {\n" +
                        "       \"createdDate\":  \"2023-01-24T08:00:00.000Z\",\n" +
                        "       \"updatedDate\":  \"2023-01-24T08:00:00.000Z\"\n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("Failed to read HTTP message", map.get("message"));
                });
    }

    @Test
    @Override
    public void upsertByExternalIdAndSource_UnauthenticatedUser_Returns401() {
        // Given
        String externalId = transactionDto.externalId();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(transactionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_UserWithoutCSRFToken_Returns403() {
        // Given
        String externalId = transactionDto.externalId();

        // When + Then
        webTestClient
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(transactionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @Override
    @WithMockUser
    public void upsertByExternalIdAndSource_InternalServerError_Returns500() {
        // Given
        String externalId = transactionDto.externalId();

        when(transactionFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.error(new OperationFailedException()));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(transactionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByExternalIdAndSource_Exists_Returns200() {
        // Given
        String externalId = transactionDto.externalId();
        Transaction mappedTransaction = TransactionMapper.INSTANCE.transactionDtoToTransaction(transactionDto);
        Transaction updatedTransaction = mappedTransaction.withId(transaction.getId());

        // When + Then
        when(transactionFacade.findByExternalIdAndSource(externalId, source)).thenReturn(Mono.just(transaction));
        when(transactionFacade.saveTransaction(mappedTransaction)).thenReturn(Mono.empty());
        when(transactionFacade.updateIfModified(externalId, source, mappedTransaction, transaction)).thenReturn(Mono.just(updatedTransaction));

        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .bodyValue(transactionDto)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TransactionDto.class)
                .value(returnedTransaction -> returnedTransaction, equalTo(transactionDto));
    }

    @Override
    @Test
    @WithMockUser
    public void deleteByExternalIdAndSource_Exists_Returns204() {
        // Given
        Transaction cancelledTransaction = transaction.withTransactionStatus(TransactionStatus.CANCELLED);
        String externalId = transactionDto.externalId();

        // When + Then
        when(transactionFacade.markAsCancelled(externalId, source)).thenReturn(Mono.just(cancelledTransaction));
        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    @Override
    @WithMockUser
    public void deleteByExternalIdAndSource_DoesntExists_Returns404() {
        // Given
        String externalId = transactionDto.externalId();

        // When + Then
        when(transactionFacade.markAsCancelled(externalId, source)).thenReturn(Mono.empty());
        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    public void deleteByExternalIdAndSource_UnauthenticatedUser_Returns401() {
        // Given
        String externalId = transactionDto.externalId();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @Override
    @WithMockUser
    public void deleteByExternalIdAndSource_UserWithoutAuthorities_Returns403() {
        // ???
    }

    @Test
    @Override
    @WithMockUser
    public void deleteByExternalIdAndSource_UserWithoutCSRFToken_Returns403() {
        // Given
        String externalId = transactionDto.externalId();

        // When + Then
        webTestClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @Override
    @WithMockUser
    public void deleteByExternalIdAndSource_InternalServerError_Returns500() {
        // Given
        String externalId = transactionDto.externalId();

        // When + Then
        when(transactionFacade.markAsCancelled(externalId, source)).thenReturn(Mono.error(new OperationFailedException()));
        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Override
    @Test
    @WithMockUser
    public void getByExternalIdAndSource_NullHandler_ThrowsNullPointerException() {
        // Given
        TransactionHandler nullTransactionHandler = null;
        transactionRouter = new TransactionRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            transactionRouter.getTransactionByExternalIdAndSourceRouterFunction(nullTransactionHandler);
        });

        // Then
        assertEquals("transactionHandler is marked non-null but is null", exception.getMessage());
    }

    @Override
    @Test
    @WithMockUser
    public void getAll_NullHandler_ThrowsNullPointerException() {
        // Given
        TransactionHandler nullTransactionHandler = null;
        transactionRouter = new TransactionRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            transactionRouter.getAllTransactionsRouterFunction(nullTransactionHandler);
        });

        // Then
        assertEquals("transactionHandler is marked non-null but is null", exception.getMessage());
    }

    @Override
    @Test
    @WithMockUser
    public void getAllBySource_NullHandler_ThrowsNullPointerException() {
        // Given
        TransactionHandler nullTransactionHandler = null;
        transactionRouter = new TransactionRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            transactionRouter.getAllTransactionsBySourceRouterFunction(nullTransactionHandler);
        });

        // Then
        assertEquals("transactionHandler is marked non-null but is null", exception.getMessage());
    }

    @Override
    @Test
    @WithMockUser
    public void getByComplytId_NullHandler_ThrowsNullPointerException() {
        // Given
        TransactionHandler nullTransactionHandler = null;
        transactionRouter = new TransactionRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            transactionRouter.getTransactionByComplytIdRouterFunction(nullTransactionHandler);
        });

        // Then
        assertEquals("transactionHandler is marked non-null but is null", exception.getMessage());
    }

    @Override
    @Test
    @WithMockUser
    public void upsertByExternalIdAndSource_NullHandler_ThrowsNullPointerException() {
        // Given
        TransactionHandler nullTransactionHandler = null;
        transactionRouter = new TransactionRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            transactionRouter.upsertTransactionRouterFunction(nullTransactionHandler);
        });

        // Then
        assertEquals("transactionHandler is marked non-null but is null", exception.getMessage());
    }

    @Override
    @Test
    @WithMockUser
    public void deleteByExternalIdAndSource_NullHandler_ThrowsNullPointerException() {
        // Given
        TransactionHandler nullTransactionHandler = null;
        transactionRouter = new TransactionRouter();

        // When
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            transactionRouter.deleteTransactionRouterFunction(nullTransactionHandler);
        });

        // Then
        assertEquals("transactionHandler is marked non-null but is null", exception.getMessage());
    }

    @Test
    @Override
    @WithMockUser
    public void getAny_InvalidUrl_Returns404() {
        // Given + When + Then
        webTestClient
                .mutateWith(csrf())
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/wrong/url")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser
    public void deleteAny_InvalidUrl_Returns404() {
        // Given + When + Then
        webTestClient
                .mutateWith(csrf())
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/wrong/url")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser
    public void putAny_InvalidUrl_Returns404() {
        // Given + When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/wrong/url")
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullShippingAddress_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Shipping address may not be null"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withShippingAddress(null))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }


    @Test
    @Override
    @WithMockUser
    public void upsert_LengthGreaterThen100CountyBillingAddress_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        OptionalAddressDto givenBillingAddress = transactionDto.billingAddress().withCounty(testUtilities.stringWithLength(101));
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "County should be 1-100 characters maximum"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withBillingAddress(givenBillingAddress))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthGreaterThen20ZipInBillingAddress_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        OptionalAddressDto givenBillingAddress = transactionDto.billingAddress().withZip("baaabbaaabbaaabbaaab1");
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "ZIP should be 1-20 characters maximum"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withBillingAddress(givenBillingAddress))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthGreaterThen50CountryInBillingAddress_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        OptionalAddressDto givenBillingAddress = transactionDto.billingAddress().withCountry(testUtilities.stringWithLength(101));
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Country should be 1-50 characters maximum"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withBillingAddress(givenBillingAddress))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthGreaterThen100CityInBillingAddress_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        OptionalAddressDto givenBillingAddress = transactionDto.billingAddress().withCity(testUtilities.stringWithLength(101));
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "City should be 1-100 characters maximum"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withBillingAddress(givenBillingAddress))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthGreaterThen100StateInBillingAddress_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        OptionalAddressDto givenBillingAddress = transactionDto.billingAddress().withState(testUtilities.stringWithLength(101));
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "State should be 1-100 characters maximum"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withBillingAddress(givenBillingAddress))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthGreaterThen200StreetInBillingAddress_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        OptionalAddressDto givenBillingAddress = transactionDto.billingAddress().withStreet(testUtilities.stringWithLength(201));
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Street should be 1-200 characters maximum"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withBillingAddress(givenBillingAddress))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthGreaterThen100CountyShippingAddress_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        MandatoryAddressDto givenShippingAddress = transactionDto.shippingAddress().withCounty(testUtilities.stringWithLength(101));
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "County should be 1-100 characters maximum"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withShippingAddress(givenShippingAddress))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthGreaterThen20ZipInShippingAddress_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        MandatoryAddressDto givenShippingAddress = transactionDto.shippingAddress().withZip("baaabbaaabbaaabbaaab1");
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "ZIP should be 1-20 characters maximum"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withShippingAddress(givenShippingAddress))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthGreaterThen50CountryInShippingAddress_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        MandatoryAddressDto givenShippingAddress = transactionDto.shippingAddress().withCountry(testUtilities.stringWithLength(101));
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Country should be 1-50 characters maximum"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withShippingAddress(givenShippingAddress))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthGreaterThen100CityInShippingAddress_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        MandatoryAddressDto givenShippingAddress = transactionDto.shippingAddress().withCity(testUtilities.stringWithLength(101));
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "City should be 1-100 characters maximum"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withShippingAddress(givenShippingAddress))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthGreaterThen100StateInShippingAddress_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        MandatoryAddressDto givenShippingAddress = transactionDto.shippingAddress().withState(testUtilities.stringWithLength(101));
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "State should be 1-100 characters maximum"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withShippingAddress(givenShippingAddress))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthGreaterThen200StreetInShippingAddress_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        MandatoryAddressDto givenShippingAddress = transactionDto.shippingAddress().withStreet(testUtilities.stringWithLength(201));
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Street should be 1-200 characters maximum"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withShippingAddress(givenShippingAddress))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullTransactionType_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Transaction Type may not be null"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withTransactionType(null))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullItemsList_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Items may not be null",
                "Items list cannot be empty"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withItems(null))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_EmptyItemsList_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Items list cannot be empty"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withItems(new ArrayList<>()))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NegativeAmountInSalesTax_Returns400validationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        SalesTaxDto salesTax = new SalesTaxDto(-0.1f, testUtilities.createSalesTaxRatesDto());
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Amount can not be a negative number"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withSalesTax(salesTax))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthGreaterThan256CreatedFrom_Returns400validationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        String lengthOf257CreatedFrom = testUtilities.stringWithLength(257);
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Created From should be 1-256 characters maximum"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withCreatedFrom(lengthOf257CreatedFrom))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankCreatedFrom_Returns400validationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        String blankCreatedFrom = "";
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Created From should be 1-256 characters maximum"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withCreatedFrom(blankCreatedFrom))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_CustomerIdFailedToParse_Returns400() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        String invalidCustomerId = "0d3e260d-3555-4fb6-bcdd-926beb4bad51$";

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n   \"externalId\": \"" + externalId + "\",\n" +
                        "   \"source\": \"" + source + "\",\n" +
                        "   \"customerId\": \"" + invalidCustomerId + "\",\n" +
                        "   \"items\": [\n" +
                        "        {\n" +
                        "            \"unitPrice\": 25,\n" +
                        "            \"totalPrice\": 5000,\n" +
                        "            \"name\": \"HW Installation Services\",\n" +
                        "            \"quantity\": 200,\n" +
                        "            \"description\": \"wd\",\n" +
                        "            \"taxCode\": \"C1S1\",\n" +
                        "            \"manualSalesTax\": false,\n" +
                        "            \"manualSalesTaxRate\": 0\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"shippingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"billingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"transactionType\": \"INVOICE\",\n" +
                        "    \"transactionStatus\": \"ACTIVE\",\n" +
                        "    \"externalTimestamps\":  {\n" +
                        "       \"createdDate\":  \"2023-01-24T08:00:00.000Z\",\n" +
                        "       \"updatedDate\":  \"2023-01-24T08:00:00.000Z\"\n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("Failed to read HTTP message", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullCustomerId_Returns400() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withCustomerId(null))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("[Customer Id may not be null]", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_InvalidCustomer_Returns400() {
        // Given
        String lengthOf101City = testUtilities.stringWithLength(101);
        CustomerDto invalidCustomerDto = testUtilities.createCustomerDto(UUID.randomUUID().toString())
                .withExternalTimestamps(null)
                .withInternalTimestamps(null)
                .withSource("")
                .withAddress(new OptionalAddressDto(lengthOf101City, "country", null, "state", "street", "zip"));
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Source should be a single digit",
                "City should be 1-100 characters maximum",
                "Source may not be blank"));

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withCustomer(invalidCustomerDto))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullExternalTimestamps_Returns400ValidationError() {
        // ???
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullCreatedDateInExternalTimestamps_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n   \"externalId\": \"" + externalId + "\",\n" +
                        "   \"source\": \"" + source + "\",\n" +
                        "   \"customerId\": \"0d3e260d-3555-4fb6-bcdd-926beb4bad51\",\n" +
                        "   \"items\": [\n" +
                        "        {\n" +
                        "            \"unitPrice\": 25,\n" +
                        "            \"totalPrice\": 5000,\n" +
                        "            \"name\": \"HW Installation Services\",\n" +
                        "            \"quantity\": 200,\n" +
                        "            \"description\": \"wd\",\n" +
                        "            \"taxCode\": \"C1S1\",\n" +
                        "            \"manualSalesTax\": false,\n" +
                        "            \"manualSalesTaxRate\": 0\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"shippingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"billingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"transactionType\": \"INVOICE\",\n" +
                        "    \"transactionStatus\": \"ACTIVE\",\n" +
                        "    \"externalTimestamps\":  {\n" +
                        "       \"updatedDate\":  \"2023-01-24T08:00:00.000Z\"\n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("[Created date may not be null]", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullUpdatedDateInExternalTimestamps_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n   \"externalId\": \"" + externalId + "\",\n" +
                        "   \"source\": \"" + source + "\",\n" +
                        "   \"customerId\": \"0d3e260d-3555-4fb6-bcdd-926beb4bad51\",\n" +
                        "   \"items\": [\n" +
                        "        {\n" +
                        "            \"unitPrice\": 25,\n" +
                        "            \"totalPrice\": 5000,\n" +
                        "            \"name\": \"HW Installation Services\",\n" +
                        "            \"quantity\": 200,\n" +
                        "            \"description\": \"wd\",\n" +
                        "            \"taxCode\": \"C1S1\",\n" +
                        "            \"manualSalesTax\": false,\n" +
                        "            \"manualSalesTaxRate\": 0\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"shippingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"billingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"transactionType\": \"INVOICE\",\n" +
                        "    \"transactionStatus\": \"ACTIVE\",\n" +
                        "    \"externalTimestamps\":  {\n" +
                        "       \"createdDate\":  \"2023-01-24T08:00:00.000Z\"\n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("[Updated date may not be null]", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankTimestampInUpdatedDateInExternalTimestamps_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n   \"externalId\": \"" + externalId + "\",\n" +
                        "   \"source\": \"" + source + "\",\n" +
                        "   \"customerId\": \"0d3e260d-3555-4fb6-bcdd-926beb4bad51\",\n" +
                        "   \"items\": [\n" +
                        "        {\n" +
                        "            \"unitPrice\": 25,\n" +
                        "            \"totalPrice\": 5000,\n" +
                        "            \"name\": \"HW Installation Services\",\n" +
                        "            \"quantity\": 200,\n" +
                        "            \"description\": \"wd\",\n" +
                        "            \"taxCode\": \"C1S1\",\n" +
                        "            \"manualSalesTax\": false,\n" +
                        "            \"manualSalesTaxRate\": 0\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"shippingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"billingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"transactionType\": \"INVOICE\",\n" +
                        "    \"transactionStatus\": \"ACTIVE\",\n" +
                        "    \"externalTimestamps\":  {\n" +
                        "       \"createdDate\":  \"2023-01-24T08:00:00.000Z\",\n" +
                        "       \"updatedDate\":  \"\"\n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("[Timestamp may not be blank]", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankTimestampInCreatedDateInExternalTimestamps_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n   \"externalId\": \"" + externalId + "\",\n" +
                        "   \"source\": \"" + source + "\",\n" +
                        "   \"customerId\": \"0d3e260d-3555-4fb6-bcdd-926beb4bad51\",\n" +
                        "   \"items\": [\n" +
                        "        {\n" +
                        "            \"unitPrice\": 25,\n" +
                        "            \"totalPrice\": 5000,\n" +
                        "            \"name\": \"HW Installation Services\",\n" +
                        "            \"quantity\": 200,\n" +
                        "            \"description\": \"wd\",\n" +
                        "            \"taxCode\": \"C1S1\",\n" +
                        "            \"manualSalesTax\": false,\n" +
                        "            \"manualSalesTaxRate\": 0\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"shippingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"billingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"transactionType\": \"INVOICE\",\n" +
                        "    \"transactionStatus\": \"ACTIVE\",\n" +
                        "    \"externalTimestamps\":  {\n" +
                        "       \"updatedDate\":  \"2023-01-24T08:00:00.000Z\",\n" +
                        "       \"createdDate\":  \"\"\n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("[Timestamp may not be blank]", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullCreatedDateInInternalTimestamps_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n   \"externalId\": \"" + externalId + "\",\n" +
                        "   \"source\": \"" + source + "\",\n" +
                        "   \"customerId\": \"0d3e260d-3555-4fb6-bcdd-926beb4bad51\",\n" +
                        "   \"items\": [\n" +
                        "        {\n" +
                        "            \"unitPrice\": 25,\n" +
                        "            \"totalPrice\": 5000,\n" +
                        "            \"name\": \"HW Installation Services\",\n" +
                        "            \"quantity\": 200,\n" +
                        "            \"description\": \"wd\",\n" +
                        "            \"taxCode\": \"C1S1\",\n" +
                        "            \"manualSalesTax\": false,\n" +
                        "            \"manualSalesTaxRate\": 0\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"shippingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"billingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"transactionType\": \"INVOICE\",\n" +
                        "    \"transactionStatus\": \"ACTIVE\",\n" +
                        "    \"internalTimestamps\":  {\n" +
                        "       \"updatedDate\":  \"2023-01-24T08:00:00.000Z\"\n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("[Created date may not be null]", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullUpdatedDateInInternalTimestamp_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n   \"externalId\": \"" + externalId + "\",\n" +
                        "   \"source\": \"" + source + "\",\n" +
                        "   \"customerId\": \"0d3e260d-3555-4fb6-bcdd-926beb4bad51\",\n" +
                        "   \"items\": [\n" +
                        "        {\n" +
                        "            \"unitPrice\": 25,\n" +
                        "            \"totalPrice\": 5000,\n" +
                        "            \"name\": \"HW Installation Services\",\n" +
                        "            \"quantity\": 200,\n" +
                        "            \"description\": \"wd\",\n" +
                        "            \"taxCode\": \"C1S1\",\n" +
                        "            \"manualSalesTax\": false,\n" +
                        "            \"manualSalesTaxRate\": 0\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"shippingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"billingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"transactionType\": \"INVOICE\",\n" +
                        "    \"transactionStatus\": \"ACTIVE\",\n" +
                        "    \"internalTimestamps\":  {\n" +
                        "       \"createdDate\":  \"2023-01-24T08:00:00.000Z\"\n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("[Updated date may not be null]", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankTimestampInUpdatedDateInInternalTimestamp_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n   \"externalId\": \"" + externalId + "\",\n" +
                        "   \"source\": \"" + source + "\",\n" +
                        "   \"customerId\": \"0d3e260d-3555-4fb6-bcdd-926beb4bad51\",\n" +
                        "   \"items\": [\n" +
                        "        {\n" +
                        "            \"unitPrice\": 25,\n" +
                        "            \"totalPrice\": 5000,\n" +
                        "            \"name\": \"HW Installation Services\",\n" +
                        "            \"quantity\": 200,\n" +
                        "            \"description\": \"wd\",\n" +
                        "            \"taxCode\": \"C1S1\",\n" +
                        "            \"manualSalesTax\": false,\n" +
                        "            \"manualSalesTaxRate\": 0\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"shippingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"billingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"transactionType\": \"INVOICE\",\n" +
                        "    \"transactionStatus\": \"ACTIVE\",\n" +
                        "    \"internalTimestamps\":  {\n" +
                        "       \"createdDate\":  \"2023-01-24T08:00:00.000Z\",\n" +
                        "       \"updatedDate\":  \"\"\n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("[Timestamp may not be blank]", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_BlankTimestampInCreatedDateInInternalTimestamp_Returns400ValidationError() {
        // Given
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\n   \"externalId\": \"" + externalId + "\",\n" +
                        "   \"source\": \"" + source + "\",\n" +
                        "   \"customerId\": \"0d3e260d-3555-4fb6-bcdd-926beb4bad51\",\n" +
                        "   \"items\": [\n" +
                        "        {\n" +
                        "            \"unitPrice\": 25,\n" +
                        "            \"totalPrice\": 5000,\n" +
                        "            \"name\": \"HW Installation Services\",\n" +
                        "            \"quantity\": 200,\n" +
                        "            \"description\": \"wd\",\n" +
                        "            \"taxCode\": \"C1S1\",\n" +
                        "            \"manualSalesTax\": false,\n" +
                        "            \"manualSalesTaxRate\": 0\n" +
                        "        }\n" +
                        "    ],\n" +
                        "    \"shippingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"billingAddress\": {\n" +
                        "        \"city\": \"City\",\n" +
                        "        \"country\": \"Country\",\n" +
                        "        \"county\": \"County\",\n" +
                        "        \"state\": \"CA\",\n" +
                        "        \"street\": \"Street\",\n" +
                        "        \"zip\": \"Zip\"\n" +
                        "    },\n" +
                        "    \"transactionType\": \"INVOICE\",\n" +
                        "    \"transactionStatus\": \"ACTIVE\",\n" +
                        "    \"internalTimestamps\":  {\n" +
                        "       \"updatedDate\":  \"2023-01-24T08:00:00.000Z\",\n" +
                        "       \"createdDate\":  \"\"\n" +
                        "   }\n}")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("[Timestamp may not be blank]", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NegativeUnitPriceInItem_Returns400ValidationError() {
        // Given
        List<ItemDto> itemList = new ArrayList<>();
        itemList.add(new ItemDto(-25, 200, 5000, "desc", "HW Installation Services", "C1S1", null, null, false, 0, null, null));
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withItems(itemList))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("[Unit Price can not be a negative number]", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NegativeQuantityInItem_Returns400ValidationError() {
        // Given
        List<ItemDto> itemList = new ArrayList<>();
        itemList.add(new ItemDto(25, -200, 5000, "desc", "HW Installation Services", "C1S1", null, null, false, 0, null, null));
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withItems(itemList))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("[Quantity can not be a negative number]", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NegativeTotalPriceInItem_Returns400ValidationError() {
        // Given
        List<ItemDto> itemList = new ArrayList<>();
        itemList.add(new ItemDto(25, 200, -5000, "desc", "HW Installation Services", "C1S1", null, null, false, 0, null, null));
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();

        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withItems(itemList))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    assertEquals("[Total Price can not be a negative number]", map.get("message"));
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullNameInItem_Returns400ValidationError() {
        // Given
        List<ItemDto> itemList = new ArrayList<>();
        itemList.add(new ItemDto(25, 200, 5000, "desc", null, "C1S1", null, null, false, 0, null, null));
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Name may not be blank"));


        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withItems(itemList))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @WithMockUser
    @Override
    public void upsert_BlankNameInItem_Returns400ValidationError() {
        // Given
        List<ItemDto> itemList = new ArrayList<>();
        itemList.add(new ItemDto(25, 200, 5000, "desc", "", "C1S1", null, null, false, 0, null, null));
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Name may not be blank",
                "Name should be 1-256 characters maximum"));


        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withItems(itemList))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthGreaterThen256NameInItem_Returns400ValidationError() {
        // Given
        List<ItemDto> itemList = new ArrayList<>();
        itemList.add(new ItemDto(25, 200, 5000, "desc", testUtilities.stringWithLength(257), "C1S1", null, null, false, 0, null, null));
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Name should be 1-256 characters maximum"));


        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withItems(itemList))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullTaxCodeInItem_Returns400ValidationError() {
        // Given
        List<ItemDto> itemList = new ArrayList<>();
        itemList.add(new ItemDto(25, 200, 5000, "desc", "HW Installation Services", null, null, null, false, 0, null, null));
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Tax Code may not be blank"));


        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withItems(itemList))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @WithMockUser
    @Override
    public void upsert_BlankTaxCodeInItem_Returns400ValidationError() {
        // Given
        List<ItemDto> itemList = new ArrayList<>();
        itemList.add(new ItemDto(25, 200, 5000, "desc", "HW Installation Services", "", null, null, false, 0, null, null));
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Tax Code should be 1-256 characters maximum",
                "Tax Code may not be blank"));


        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withItems(itemList))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthGreaterThen256TaxCodeInItem_Returns400ValidationError() {
        // Given
        List<ItemDto> itemList = new ArrayList<>();
        itemList.add(new ItemDto(25, 200, 5000, "desc", "HW Installation Services", testUtilities.stringWithLength(257), null, null, false, 0, null, null));
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Tax Code should be 1-256 characters maximum"));


        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withItems(itemList))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NegativeManualSalesTaxRateInItem_Returns400ValidationError() {
        // Given
        List<ItemDto> itemList = new ArrayList<>();
        itemList.add(new ItemDto(25, 200, 5000, "desc", "HW Installation Services", "C1S1", null, null, false, -0.5f, null, null));
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Manual Sales Tax Rate's minimum value is 0"));


        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withItems(itemList))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LargerThanMaxManualSalesTaxRateInItem_Returns400ValidationError() {
        // Given
        List<ItemDto> itemList = new ArrayList<>();
        itemList.add(new ItemDto(25, 200, 5000, "desc", "HW Installation Services", "C1S1", null, null, false, 0.5f, null, null));
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Manual Sales Tax Rate's maximum value is 0.2"));


        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withItems(itemList))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NegativeManualSalesRateTaxInShippingFee_Returns400ValidationError() {
        // Given
        ShippingFeeDto givenShippingFee = new ShippingFeeDto(false, -0.5f, 5000, null, testUtilities.createSalesTaxRatesDto(), "C1S1", null, null);
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Manual Sales Tax Rate can not be a negative number"));


        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withShippingFee(givenShippingFee))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NegativeTotalPriceInShippingFee_Returns400ValidationError() {
        // Given
        ShippingFeeDto givenShippingFee = new ShippingFeeDto(false, 0.1f, -5000, null, testUtilities.createSalesTaxRatesDto(), "C1S1", null, null);
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Total Price can not be a negative number"));


        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withShippingFee(givenShippingFee))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_NullTaxCodeInShippingFee_Returns400ValidationError() {
        // Given
        ShippingFeeDto givenShippingFee = new ShippingFeeDto(false, 0.1f, 5000, null, testUtilities.createSalesTaxRatesDto(), null, null, null);
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Tax Code may not be blank"));


        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withShippingFee(givenShippingFee))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Override
    @Test
    @WithMockUser
    public void upsert_BlankTaxCodeInShippingFee_Returns400ValidationError() {
        // Given
        ShippingFeeDto givenShippingFee = new ShippingFeeDto(false, 0.1f, 5000, null, testUtilities.createSalesTaxRatesDto(), "", null, null);
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Tax Code may not be blank",
                "Tax Code should be 1-256 characters maximum"));


        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withShippingFee(givenShippingFee))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }

    @Test
    @Override
    @WithMockUser
    public void upsert_LengthGreaterThan256TaxCodeInShippingFee_Returns400ValidationError() {
        // Given
        ShippingFeeDto givenShippingFee = new ShippingFeeDto(false, 0.1f, 5000, null, testUtilities.createSalesTaxRatesDto(), testUtilities.stringWithLength(257), null, null);
        String externalId = transactionDto.externalId();
        String source = transactionDto.source();
        HashSet<String> expectedErrors = new HashSet<>();
        expectedErrors.addAll(List.of(
                "Tax Code should be 1-256 characters maximum"));


        // When + Then
        webTestClient
                .mutateWith(csrf())
                .put()
                .uri(uriBuilder -> uriBuilder
                        .path(TransactionRouter.BASE_URL + "/source/" + source + "/externalId/" + externalId)
                        .build()).contentType(MediaType.APPLICATION_JSON)
                .bodyValue(transactionDto.withShippingFee(givenShippingFee))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest().expectBody(LinkedHashMap.class)
                .value(map -> {
                    testUtilities.checkErrorMessages(map, expectedErrors);
                });
    }
}
