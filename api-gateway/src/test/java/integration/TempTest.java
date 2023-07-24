//package integration;
//
//import integration.test_utils.TestUtilities;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.jupiter.api.Test;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.reactive.server.WebTestClient;
//
//import java.time.Duration;
//
//@Slf4j
//public class TempTest {
//
//
//    String loadTestCustomer = "4cfbbf0b-d3e5-4954-8a90-c9c2e832e5f5";
//    String demoCustomer = "2d7dd100-3389-4b08-8b05-38dc88a114d3";
//
//    WebTestClient webTestClient =
//            WebTestClient.bindToServer().baseUrl("https://load-test.complyt.io")
//                    .responseTimeout(Duration.ofSeconds(15)).build();
//
//
//    @Test
//    public void downTimeTest() throws InterruptedException {
//        while (true) {
//            Thread.sleep(500);
//            webTestClient
//                    .put()
//                    .uri(uriBuilder -> uriBuilder
//                            .path(TestUtilities.FILES_BASE_URL)
//                            .build())
//                    .headers(headers -> {
//                        headers.setContentType(MediaType.APPLICATION_JSON);
//                        headers.setBearerAuth("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InJ0RU1OdWRnTWx5aTJtMzVLSnJQRSJ9.eyJ0ZW5hbnRfaWQiOiJvcmdfU3R0QWNCa0s3YjMydzdrQSIsImlzcyI6Imh0dHBzOi8vZGV2ZWxvcG1lbnQtY29tcGx5dC51cy5hdXRoMC5jb20vIiwic3ViIjoiOGZsQmcxd2NqbmhYbkFVSEdGREw2QWJTMmZHSHZGM2hAY2xpZW50cyIsImF1ZCI6Imh0dHBzOi8vc2FsZXMtdGF4LXNlcnZpY2UvIiwiaWF0IjoxNjkwMTk3MjAxLCJleHAiOjE2OTAyODM2MDEsImF6cCI6IjhmbEJnMXdjam5oWG5BVUhHRkRMNkFiUzJmR0h2RjNoIiwic2NvcGUiOiJjcmVhdGU6Y3VzdG9tZXIgZGVsZXRlOmN1c3RvbWVyIHJlYWQ6Y3VzdG9tZXIgdXBkYXRlOmN1c3RvbWVyIGNyZWF0ZTp0cmFuc2FjdGlvbiByZWFkOnRyYW5zYWN0aW9uIHVwZGF0ZTp0cmFuc2FjdGlvbiBkZWxldGU6dHJhbnNhY3Rpb24gcmVhZDpzdGF0ZSBjcmVhdGU6ZXhlbXB0aW9uIHVwZGF0ZTpleGVtcHRpb24gZGVsZXRlOmV4ZW1wdGlvbiByZWFkOmV4ZW1wdGlvbiBjcmVhdGU6bmV4dXMgcmVhZDpuZXh1cyBkZWxldGU6bmV4dXMgdXBkYXRlOm5leHVzIHJlYWQ6bGluayByZWFkOnNhbGVzX3RheF9yYXRlcyIsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyJ9.JRGn4vN_926f_9BH-X2c4nY0Yfd5o3K3SONQGoKdJQQMKW-OOczKhDIupQKtDvMGW-IWRkKYNC5Z_evRwFPJhs9b6hqDvLslFwFr9yylFd8HuRExBvCwH4z068KeiV9wTqUqznQEctIBUBPueSLdpF3McKpsXSWbWEFu6QTWAxn07OWGS77u90FIgraQ52Jj8BzyVVjF5PMMy-xneF4iVUWpcmm9uE3fE_K4ZOuTgYHg9k0GqTWsKA1lDVbq3ojuJ-hBC1zIZ3Eo4qdx6hxuy2h6leJeFESXI6rl7EoOfGlj4n-Xk49adzlZYUAXspLqJdsn8TRIinIKfb9G8VxgJA");
//                    })
//                    .exchange()
//                    .expectStatus().value(status -> log.info("status: " + status));
//        }
//    }
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
//                        headers.setBearerAuth("eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InJ0RU1OdWRnTWx5aTJtMzVLSnJQRSJ9.eyJ0ZW5hbnRfaWQiOiJvcmdfU3R0QWNCa0s3YjMydzdrQSIsImlzcyI6Imh0dHBzOi8vZGV2ZWxvcG1lbnQtY29tcGx5dC51cy5hdXRoMC5jb20vIiwic3ViIjoiOGZsQmcxd2NqbmhYbkFVSEdGREw2QWJTMmZHSHZGM2hAY2xpZW50cyIsImF1ZCI6Imh0dHBzOi8vc2FsZXMtdGF4LXNlcnZpY2UvIiwiaWF0IjoxNjkwMTk3MjAxLCJleHAiOjE2OTAyODM2MDEsImF6cCI6IjhmbEJnMXdjam5oWG5BVUhHRkRMNkFiUzJmR0h2RjNoIiwic2NvcGUiOiJjcmVhdGU6Y3VzdG9tZXIgZGVsZXRlOmN1c3RvbWVyIHJlYWQ6Y3VzdG9tZXIgdXBkYXRlOmN1c3RvbWVyIGNyZWF0ZTp0cmFuc2FjdGlvbiByZWFkOnRyYW5zYWN0aW9uIHVwZGF0ZTp0cmFuc2FjdGlvbiBkZWxldGU6dHJhbnNhY3Rpb24gcmVhZDpzdGF0ZSBjcmVhdGU6ZXhlbXB0aW9uIHVwZGF0ZTpleGVtcHRpb24gZGVsZXRlOmV4ZW1wdGlvbiByZWFkOmV4ZW1wdGlvbiBjcmVhdGU6bmV4dXMgcmVhZDpuZXh1cyBkZWxldGU6bmV4dXMgdXBkYXRlOm5leHVzIHJlYWQ6bGluayByZWFkOnNhbGVzX3RheF9yYXRlcyIsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyJ9.JRGn4vN_926f_9BH-X2c4nY0Yfd5o3K3SONQGoKdJQQMKW-OOczKhDIupQKtDvMGW-IWRkKYNC5Z_evRwFPJhs9b6hqDvLslFwFr9yylFd8HuRExBvCwH4z068KeiV9wTqUqznQEctIBUBPueSLdpF3McKpsXSWbWEFu6QTWAxn07OWGS77u90FIgraQ52Jj8BzyVVjF5PMMy-xneF4iVUWpcmm9uE3fE_K4ZOuTgYHg9k0GqTWsKA1lDVbq3ojuJ-hBC1zIZ3Eo4qdx6hxuy2h6leJeFESXI6rl7EoOfGlj4n-Xk49adzlZYUAXspLqJdsn8TRIinIKfb9G8VxgJA");
//                    })
//                    .bodyValue(TestUtilities.transactionJsonExample(
//                            "4444", loadTestCustomer))
//                    .exchange()
//                    .expectStatus().value(status -> log.info("status: " + status));
//        }
//    }
//}
