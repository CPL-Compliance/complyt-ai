//package integration;
//
//import integration.test_utils.TestUtilities;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.reactive.server.WebTestClient;
//
//@Slf4j
//public class TempTest {
//
//
//    String loadTestCustomer = "4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5";
//    String demoCustomer = "2d7dd100-3389-4b08-8b05-38dc88a114d3";
//
//    WebTestClient webTestClient =
//            WebTestClient.bindToServer().baseUrl("https://load-test.complyt.io").build();
//
//
//    @Test
//    public void downTimeTest2() throws InterruptedException {
//        while (true) {
//            Thread.sleep(500);
//            webTestClient
//                    .put()
//                    .uri(uriBuilder -> uriBuilder
//                            .path(TestUtilities.TRANSACTION_BASE_URL +
//                                  "/source/1/externalId/4444")
//                            .build())
//                    .headers(headers -> {
//                        headers.setContentType(MediaType.APPLICATION_JSON);
//                        headers.setBearerAuth("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InJ0RU1OdWRnTWx5aTJtMzVLSnJQRSJ9.eyJ0ZW5hbnRfaWQiOiJvcmdfU3R0QWNCa0s3YjMydzdrQSIsImlzcyI6Imh0dHBzOi8vZGV2ZWxvcG1lbnQtY29tcGx5dC51cy5hdXRoMC5jb20vIiwic3ViIjoiOGZsQmcxd2NqbmhYbkFVSEdGREw2QWJTMmZHSHZGM2hAY2xpZW50cyIsImF1ZCI6Imh0dHBzOi8vc2FsZXMtdGF4LXNlcnZpY2UvIiwiaWF0IjoxNjg5NTgwNzQzLCJleHAiOjE2ODk2NjcxNDMsImF6cCI6IjhmbEJnMXdjam5oWG5BVUhHRkRMNkFiUzJmR0h2RjNoIiwic2NvcGUiOiJjcmVhdGU6Y3VzdG9tZXIgZGVsZXRlOmN1c3RvbWVyIHJlYWQ6Y3VzdG9tZXIgdXBkYXRlOmN1c3RvbWVyIGNyZWF0ZTp0cmFuc2FjdGlvbiByZWFkOnRyYW5zYWN0aW9uIHVwZGF0ZTp0cmFuc2FjdGlvbiBkZWxldGU6dHJhbnNhY3Rpb24gcmVhZDpzdGF0ZSBjcmVhdGU6ZXhlbXB0aW9uIHVwZGF0ZTpleGVtcHRpb24gZGVsZXRlOmV4ZW1wdGlvbiByZWFkOmV4ZW1wdGlvbiBjcmVhdGU6bmV4dXMgcmVhZDpuZXh1cyBkZWxldGU6bmV4dXMgdXBkYXRlOm5leHVzIHJlYWQ6bGluayByZWFkOnNhbGVzX3RheF9yYXRlcyIsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyJ9.DeHNjBgSDjqENKw2viUbDqCet2oEDJLos-v15oYlDsTfZ45DzE4y9Q3aD32G89XV4f_eEYhVAUhGxSIqVGTmr_NPVwSZW-50w3hKYQcKIGeGbLqrCpHbJfYNBGKab71vwZAUPlzyPFHCGI_tDizQkkV_CgxIOlpMiDeSF7UnUetbo-LCR_yGRnuzUT8sLe3pBng0vORSXcn152bm8SSSus8JUCO0RQxyIzyv4BQeZPuXaWzypJZ9EfCZ4Lnj5Wrx1A_-1m4KHG5Q-IRsgp4V7eXqHXR_Ixc2Ynnk07ud0F1f8ZibsQW2VhwdiTgnmf4Jr772DGo1PzT_9O6ju-8xzw");
//                    })
//                    .bodyValue(TestUtilities.transactionJsonExample(
//                            "4444", loadTestCustomer))
//                    .exchange()
//                    .expectStatus().value(status -> log.info("status: " + status));
//        }
//    }
//}
