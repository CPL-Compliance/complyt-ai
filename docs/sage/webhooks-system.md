# Complyt Webhook System Documentation

## Overview

The Complyt webhook system provides a robust, secure, and reliable mechanism for notifying external systems about entity changes within the Complyt platform. Built on Spring WebFlux reactive principles, the system ensures high performance and scalability while maintaining enterprise-grade security standards.

## Architecture

### Core Components

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│                 │    │                  │    │                 │
│   Entity        │───▶│  WebhookEntity   │───▶│  RabbitMQ       │
│   Changes       │    │  Wrapper         │    │  Queue          │
│                 │    │                  │    │                 │
└─────────────────┘    └──────────────────┘    └─────────────────┘
                                                         │
                                                         ▼
                                               ┌─────────────────┐
                                               │                 │
                                               │  MQ Consumer    │
                                               │  (Webhook       │
                                               │   Processor)    │
                                               │                 │
                                               └─────────────────┘
                                                         │
                                                         ▼
                                               ┌─────────────────┐
                                               │                 │
                                               │  Webhook        │
                                               │  Web Client     │
                                               │  Wrapper        │
                                               │                 │
                                               └─────────────────┘
                                                         │
                                                         ▼
                                               ┌─────────────────┐
                                               │                 │
                                               │  External       │
                                               │  System         │
                                               │  (Webhook       │
                                               │   Endpoint)     │
                                               │                 │
                                               └─────────────────┘
```

### Key Classes

1. **WebhookEntityWrapper** - Core data structure that wraps entities for webhook delivery
2. **WebhookWebClientWrapper** - HTTP client responsible for webhook delivery
3. **MQConsumer** - RabbitMQ consumer that processes webhook messages
4. **HmaacGenerator** - Security component for generating HMAC signatures

## Data Payload Structure

### WebhookEntityWrapper

The `WebhookEntityWrapper` is the core data structure that encapsulates all information needed for webhook delivery:

```java
public record WebhookEntityWrapper<T extends ComplytIdProperty>(
    UUID id,                    // Unique identifier for this webhook event
    LocalDateTime timestamp,    // When the event occurred
    Action action,             // Type of action (CREATE, UPDATE, DELETE)
    String webhookClass,       // Class name of the wrapped entity
    T object,                  // The actual entity data
    String host,               // Target webhook host
    String path                // Target webhook path
) {}
```

### JSON Payload Example

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "timestamp": "2024-08-07T10:30:00",
  "action": "CREATE",
  "webhookClass": "io.complyt.domain.SalesTaxTracking",
  "object": {
    "complytId": "123e4567-e89b-12d3-a456-426614174000",
    "tenantId": "tenant-123",
    "state": {
      "name": "California",
      "code": "CA",
      "abbreviation": "CA"
    },
    "enforcesSalesTax": true,
    "approved": true,
    "approvalDate": "2024-08-07T10:30:00"
  },
  "host": "client-webhook.example.com",
  "path": "/webhooks/complyt/sales-tax"
}
```

## Security Implementation

### HMAC SHA-256 Authentication

All webhook requests are secured using HMAC SHA-256 signatures to ensure:
- **Authenticity** - Verify the webhook came from Complyt
- **Integrity** - Ensure the payload hasn't been tampered with
- **Non-repudiation** - Provide proof of origin

#### Signature Generation Process

1. **Serialize Payload**: Convert the entire `WebhookEntityWrapper` to JSON
2. **Generate HMAC**: Create HMAC SHA-256 using the shared secret key
3. **Add Header**: Include signature in `X-Signature` header

```java
// Example signature generation
ObjectMapper objectMapper = new ObjectMapper()
    .registerModule(new JavaTimeModule())
    .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

String payload = objectMapper.writeValueAsString(webhookEntityWrapper);
String signature = HmaacGenerator.generateHmacSHA256(secretKey, payload);

// HTTP Header: X-Signature: {signature}
```

#### Signature Verification (Client Side)

```java
// Client-side verification example
public boolean verifyWebhookSignature(String payload, String receivedSignature, String secretKey) {
    String expectedSignature = HmaacGenerator.generateHmacSHA256(secretKey, payload);
    return MessageDigest.isEqual(
        expectedSignature.getBytes(StandardCharsets.UTF_8),
        receivedSignature.getBytes(StandardCharsets.UTF_8)
    );
}
```

## Reliability & Error Handling

### Retry Mechanism

The webhook system implements exponential backoff retry logic:

```java
.retryWhen(Retry.backoff(5, Duration.ofSeconds(1))
    .maxBackoff(Duration.ofSeconds(10))
    .jitter(0.2)
    .filter(throwable -> {
        log.info("Retrying webhook due to error: {}", throwable.getMessage());
        return true;
    }))
```

**Retry Configuration:**
- **Max Attempts**: 5 retries
- **Initial Delay**: 1 second
- **Max Backoff**: 10 seconds
- **Jitter**: 20% randomization to prevent thundering herd
- **Retry All Errors**: Currently retries on any exception

### Error Scenarios

1. **Network Timeouts**: Automatic retry with exponential backoff
2. **HTTP 5xx Errors**: Retried as temporary server issues
3. **HTTP 4xx Errors**: Currently retried (may need refinement)
4. **Connection Refused**: Retried with backoff
5. **DNS Resolution Failures**: Retried with backoff

### Dead Letter Queue (Recommended Enhancement)

For production resilience, consider implementing:
- Dead letter queue for failed webhooks after all retries
- Manual retry mechanism for dead letter items
- Alerting for webhook delivery failures

## Message Queue Integration

### RabbitMQ Configuration

Webhooks are processed asynchronously through RabbitMQ:

```java
@Component
public class MQConsumer<T extends ComplytIdProperty> {
    
    @Autowired
    WebClientWrapper<T> webhookWebClientWrapper;

    @RabbitListener(queues = "webhook.queue")
    public Mono<Void> consume(WebhookEntityWrapper<T> wrapper) {
        log.info("Processing webhook for entity: {}", wrapper.id());
        
        return webhookWebClientWrapper
            .sendWebhook(wrapper)
            .then();
    }
}
```

### Queue Benefits

1. **Decoupling**: Entity changes don't block on webhook delivery
2. **Scalability**: Multiple consumers can process webhooks in parallel
3. **Reliability**: Messages persist until successfully processed
4. **Load Balancing**: Distribute webhook load across multiple workers

## Configuration

### WebClient Configuration

```java
@Configuration
public class WebClientsConfig {
    
    @Bean(name = "webhookWebClient")
    public WebClient webhookWebClient(WebClient.Builder webClientBuilder) {
        return webClientBuilder
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024))
            .build();
    }
}
```

### Properties Configuration

```java
@Bean("webhookWebClientWrapperProperties")
public WebClientWrapperProperties webhookWebClientWrapperProperties() {
    return WebClientWrapperProperties.builder()
        .scheme("https")
        .host("default-webhook-host.com")
        .path("/webhooks/complyt")
        .secretKey("${webhook.secret.key}")
        .build();
}
```

## Client Implementation Guide

### Webhook Endpoint Requirements

Your webhook endpoint should:

1. **Accept POST Requests**: All webhooks are sent via HTTP POST
2. **Verify HMAC Signature**: Always validate the `X-Signature` header
3. **Return HTTP 2xx**: Indicate successful processing
4. **Handle Idempotency**: Use the webhook `id` to prevent duplicate processing
5. **Process Asynchronously**: Don't block the webhook response

### Example Webhook Endpoint

```java
@RestController
@RequestMapping("/webhooks/complyt")
public class ComplytWebhookController {
    
    @PostMapping("/sales-tax")
    public ResponseEntity<Void> handleSalesTaxWebhook(
            @RequestBody WebhookEntityWrapper<SalesTaxTracking> webhook,
            @RequestHeader("X-Signature") String signature) {
        
        // 1. Verify signature
        if (!verifySignature(webhook, signature)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // 2. Check for duplicate processing
        if (isAlreadyProcessed(webhook.id())) {
            return ResponseEntity.ok().build();
        }
        
        // 3. Process webhook asynchronously
        webhookProcessor.processAsync(webhook);
        
        // 4. Return success immediately
        return ResponseEntity.ok().build();
    }
}
```

## Monitoring & Observability

### Logging

The webhook system provides comprehensive logging:

```java
// Webhook sending
log.info("Sending webhook entity with request id: {}", webhookEntityWrapper.id());

// Retry attempts
log.info("Retrying webhook due to error: {}", throwable.getMessage());

// Success confirmation
log.info("Sent webhook entity with request id: {}", webhookEntityWrapper.id());

// Final failure
log.error("Webhook failed after retries: {}", err.getMessage());
```

### Metrics (Recommended)

Consider implementing metrics for:
- Webhook delivery success rate
- Average delivery time
- Retry attempt distribution
- Error rate by error type
- Queue depth and processing rate

### Health Checks

Monitor:
- RabbitMQ queue health
- Webhook endpoint availability
- HMAC key rotation status
- Dead letter queue size

## Best Practices

### For Complyt Developers

1. **Entity Design**: Ensure entities implement `ComplytIdProperty`
2. **Serialization**: Use Jackson annotations for proper JSON serialization
3. **Testing**: Mock webhook endpoints in integration tests
4. **Configuration**: Use environment-specific webhook configurations
5. **Monitoring**: Implement proper logging and metrics

### For Webhook Consumers

1. **Signature Verification**: Always verify HMAC signatures
2. **Idempotency**: Handle duplicate webhooks gracefully
3. **Async Processing**: Don't block webhook responses
4. **Error Handling**: Return appropriate HTTP status codes
5. **Timeouts**: Implement reasonable timeout values
6. **Monitoring**: Track webhook processing metrics

## Troubleshooting

### Common Issues

1. **Signature Verification Failures**
   - Check secret key configuration
   - Verify JSON serialization matches exactly
   - Ensure timestamp handling is consistent

2. **Webhook Timeouts**
   - Check network connectivity
   - Verify endpoint availability
   - Review processing time on consumer side

3. **Duplicate Processing**
   - Implement idempotency using webhook ID
   - Check for race conditions in processing logic

4. **Queue Backlog**
   - Monitor RabbitMQ queue depth
   - Scale webhook consumers horizontally
   - Investigate slow webhook endpoints

### Debug Commands

```bash
# Check RabbitMQ queue status
rabbitmqctl list_queues name messages consumers

# Test webhook endpoint
curl -X POST https://your-webhook-endpoint.com/webhooks/complyt \
  -H "Content-Type: application/json" \
  -H "X-Signature: your-test-signature" \
  -d '{"test": "payload"}'

# Monitor webhook logs
kubectl logs -f deployment/processing-service | grep -i webhook
```

## Future Enhancements

### Planned Improvements

1. **Dead Letter Queue**: Implement DLQ for failed webhooks
2. **Webhook Registry**: Dynamic webhook endpoint management
3. **Filtering**: Allow clients to specify which events they want
4. **Batching**: Group multiple events into single webhook calls
5. **Circuit Breaker**: Prevent cascading failures
6. **Webhook Templates**: Customizable payload formats
7. **Rate Limiting**: Prevent overwhelming webhook consumers

### Configuration Enhancements

1. **Per-Client Configuration**: Different retry policies per client
2. **Environment-Specific Settings**: Dev/staging/prod configurations
3. **Dynamic Configuration**: Runtime configuration updates
4. **Webhook Versioning**: Support multiple webhook payload versions

---

*This documentation is maintained by the Complyt Engineering Team. For questions or updates, please contact the backend team or create an issue in the complyt-ai repository.*

