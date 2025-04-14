//package com.complyt.v1.config.patch;
//
//import com.complyt.v1.models.transaction.TransactionDto;
//import org.junit.jupiter.api.BeforeEach;
//import testUtils.unit_test.UnitTestUtilities;
//
//import java.time.LocalDateTime;
//
//import static com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat.UUID;
//
//public class TransactionPatcherFunctionsTest {
//
//    private TransactionDto transaction;
//    UnitTestUtilities unitTestUtilities;
//
//     static MockedStatic mockedStatic;

//    @BeforeAll
//    static void beforeAll() {
//        try {
//            mockedStatic = mockStatic(TenantResolver.class);
//        } catch (Exception e) {
//            // Log the error or fail the test setup
//            System.err.println("Failed to mock TenantResolver: " + e.getMessage());
//            throw e;
//        }
//    }
//
//    @AfterAll
//    static void afterAll() {
//        mockedStatic.close();
//    }
//
//    @BeforeEach
//    void setUp() {
//        unitTestUtilities = new UnitTestUtilities(LocalDateTime.now(), "tenant_id");
//        transaction = unitTestUtilities.createTransactionDto(UUID.toString());
//    }
//}
