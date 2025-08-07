# Complyt Integration Architecture: Webhooks & AI Transcript Processing

## Overview

This document describes how the Complyt webhook system and AI transcript processing flow integrate to provide a comprehensive, event-driven architecture for VAT compliance automation. The integration combines real-time webhook notifications with intelligent transcript processing to create a seamless end-to-end solution.

## System Architecture

### High-Level Integration Flow

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                           COMPLYT INTEGRATION ARCHITECTURE                      │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌─────────────────┐    ┌──────────────────┐    ┌─────────────────────────────┐ │
│  │                 │    │                  │    │                             │ │
│  │   Client        │───▶│   VATGPT         │───▶│   OpenAI Processing         │ │
│  │   Transcript    │    │   Gateway        │    │   (GPT-3.5-turbo)          │ │
│  │   Submission    │    │   (FastAPI)      │    │                             │ │
│  │                 │    │                  │    │                             │ │
│  └─────────────────┘    └──────────────────┘    └─────────────────────────────┘ │
│           │                       │                           │                  │
│           │                       │                           │                  │
│           ▼                       ▼                           ▼                  │
│  ┌─────────────────┐    ┌──────────────────┐    ┌─────────────────────────────┐ │
│  │                 │    │                  │    │                             │ │
│  │   Transcript    │    │   RabbitMQ       │    │   Structured Data           │ │
│  │   Validation    │    │   Job Queue      │    │   Extraction                │ │
│  │   & Storage     │    │                  │    │                             │ │
│  │                 │    │                  │    │                             │ │
│  └─────────────────┘    └──────────────────┘    └─────────────────────────────┘ │
│           │                       │                           │                  │
│           │                       │                           │                  │
│           ▼                       ▼                           ▼                  │
│  ┌─────────────────┐    ┌──────────────────┐    ┌─────────────────────────────┐ │
│  │                 │    │                  │    │                             │ │
│  │   Processing    │    │   Celery         │    │   Complyt Entity            │ │
│  │   Status        │    │   Workers        │    │   Creation                  │ │
│  │   Tracking      │    │                  │    │   (Sales Tax, Client)      │ │
│  │                 │    │                  │    │                             │ │
│  └─────────────────┘    └──────────────────┘    └─────────────────────────────┘ │
│           │                       │                           │                  │
│           │                       │                           │                  │
│           ▼                       ▼                           ▼                  │
│  ┌─────────────────┐    ┌──────────────────┐    ┌─────────────────────────────┐ │
│  │                 │    │                  │    │                             │ │
│  │   Webhook       │    │   Webhook        │    │   Entity Change             │ │
│  │   Notification  │    │   Queue          │    │   Detection                 │ │
│  │   Preparation   │    │   (RabbitMQ)     │    │                             │ │
│  │                 │    │                  │    │                             │ │
│  └─────────────────┘    └──────────────────┘    └─────────────────────────────┘ │
│           │                       │                           │                  │
│           │                       │                           │                  │
│           ▼                       ▼                           ▼                  │
│  ┌─────────────────┐    ┌──────────────────┐    ┌─────────────────────────────┐ │
│  │                 │    │                  │    │                             │ │
│  │   Client        │◄───│   Webhook        │◄───│   HMAC Signature            │ │
│  │   Webhook       │    │   Delivery       │    │   Generation                │ │
│  │   Endpoint      │    │   (HTTP POST)    │    │                             │ │
│  │                 │    │                  │    │                             │ │
│  └─────────────────┘    └──────────────────┘    └─────────────────────────────┘ │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

## Integration Points

### 1. Transcript Ingestion → Entity Creation

The integration begins when a transcript is submitted for processing:

```python
# Transcript submission triggers the integration flow
async def submit_transcript(transcript_data: TranscriptSubmission):
    """Entry point for transcript processing integration."""
    
    # 1. Validate and store transcript
    transcript = await validate_and_store_transcript(transcript_data)
    
    # 2. Queue for AI processing
    job_id = await queue_transcript_processing(transcript.id)
    
    # 3. Return immediate response with tracking info
    return TranscriptSubmissionResponse(
        transcript_id=transcript.id,
        job_id=job_id,
        status="QUEUED",
        estimated_completion=calculate_eta(),
        webhook_config=transcript_data.webhook_config
    )
```

### 2. AI Processing → Webhook Preparation

AI processing results are transformed into webhook-ready entities:

```python
async def process_transcript_to_webhooks(transcript_id: str):
    """Convert AI processing results into webhook notifications."""
    
    # 1. Process transcript with AI
    ai_results = await process_with_openai(transcript_id)
    
    # 2. Create Complyt entities based on AI results
    entities = await create_complyt_entities(ai_results)
    
    # 3. Prepare webhook notifications for each entity
    webhook_notifications = []
    
    for entity in entities:
        # Create webhook wrapper using existing infrastructure
        webhook = WebhookEntityWrapper(
            id=generate_uuid(),
            timestamp=datetime.now(),
            action=Action.CREATE,
            webhook_class=entity.__class__.__name__,
            object=entity,
            host=ai_results.webhook_config.host,
            path=ai_results.webhook_config.path
        )
        webhook_notifications.append(webhook)
    
    # 4. Queue webhooks for delivery
    for webhook in webhook_notifications:
        await webhook_queue.publish(webhook)
    
    # 5. Send processing completion notification
    completion_webhook = create_completion_webhook(transcript_id, entities)
    await webhook_queue.publish(completion_webhook)
    
    return ProcessingResult(
        transcript_id=transcript_id,
        entities_created=len(entities),
        webhooks_queued=len(webhook_notifications) + 1
    )
```

### 3. Entity Changes → Webhook Delivery

The existing webhook infrastructure handles delivery with full reliability:

```python
# The existing webhook system automatically handles:
# - HMAC signature generation
# - Retry logic with exponential backoff
# - Error handling and logging
# - Dead letter queue management (future enhancement)

# No additional integration code needed - the webhook system
# processes any WebhookEntityWrapper placed in the queue
```

## Data Flow Sequence

### Complete Integration Sequence

```mermaid
sequenceDiagram
    participant Client
    participant VATGPT
    participant OpenAI
    participant EntityService
    participant WebhookQueue
    participant WebhookDelivery
    participant ClientEndpoint
    
    Client->>VATGPT: POST /transcripts (audio/text)
    VATGPT->>VATGPT: Validate & queue transcript
    VATGPT->>Client: 202 Accepted (job_id)
    
    VATGPT->>OpenAI: Process transcript with GPT
    OpenAI->>VATGPT: Return structured data
    
    VATGPT->>EntityService: Create Complyt entities
    EntityService->>VATGPT: Return created entities
    
    VATGPT->>WebhookQueue: Queue entity webhooks
    VATGPT->>WebhookQueue: Queue completion webhook
    
    WebhookQueue->>WebhookDelivery: Process webhook queue
    WebhookDelivery->>WebhookDelivery: Generate HMAC signature
    WebhookDelivery->>ClientEndpoint: POST webhook (with retry)
    ClientEndpoint->>WebhookDelivery: 200 OK
    
    WebhookDelivery->>WebhookQueue: Mark webhook delivered
    
    Note over Client,ClientEndpoint: Process repeats for each created entity
```

## Integration Configuration

### Unified Configuration Schema

```yaml
# Complete integration configuration
complyt_integration:
  # VATGPT Configuration
  vatgpt:
    gateway_url: "https://vatgpt.complyt.io"
    api_version: "v1"
    timeout_seconds: 30
    
  # AI Processing Configuration  
  ai_processing:
    openai_model: "gpt-3.5-turbo-0125"
    timeout_ms: 8000
    rate_limit_rps: 6.7
    max_retries: 3
    confidence_threshold: 0.8
    
  # Entity Creation Configuration
  entity_creation:
    auto_approve_threshold: 0.95
    require_manual_review: true
    default_tenant_mapping: "ai_processed"
    
  # Webhook Configuration
  webhooks:
    queue_name: "complyt_webhooks"
    retry_attempts: 5
    retry_backoff_seconds: [1, 2, 4, 8, 16]
    timeout_seconds: 30
    signature_algorithm: "HMAC-SHA256"
    
  # Integration Monitoring
  monitoring:
    enable_metrics: true
    log_level: "INFO"
    alert_on_failure_rate: 0.05
    health_check_interval: 60
```

### Environment Variables

```bash
# Integration-specific environment variables
COMPLYT_INTEGRATION_MODE=production
VATGPT_API_KEY=vatgpt-api-key-here
OPENAI_API_KEY=sk-openai-key-here
WEBHOOK_SECRET_KEY=webhook-hmac-secret

# Queue Configuration
RABBITMQ_URL=amqp://rabbitmq.complyt.io:5672
TRANSCRIPT_QUEUE=transcript_processing
WEBHOOK_QUEUE=webhook_delivery
ENTITY_QUEUE=entity_creation

# Database Configuration
POSTGRES_URL=postgresql://complyt:password@db.complyt.io:5432/complyt
REDIS_URL=redis://redis.complyt.io:6379/0

# Monitoring Configuration
PROMETHEUS_ENDPOINT=http://prometheus.complyt.io:9090
GRAFANA_DASHBOARD_URL=https://grafana.complyt.io/d/complyt-integration
```

## Error Handling & Recovery

### Integration-Level Error Handling

```python
class IntegrationError(Exception):
    """Base exception for integration-level errors."""
    pass

class TranscriptProcessingError(IntegrationError):
    """Transcript processing failed."""
    pass

class EntityCreationError(IntegrationError):
    """Entity creation failed."""
    pass

class WebhookDeliveryError(IntegrationError):
    """Webhook delivery failed."""
    pass

async def handle_integration_error(
    transcript_id: str, 
    error: IntegrationError, 
    context: dict
):
    """Centralized error handling for integration failures."""
    
    error_details = {
        "transcript_id": transcript_id,
        "error_type": type(error).__name__,
        "error_message": str(error),
        "context": context,
        "timestamp": datetime.now().isoformat()
    }
    
    # Log error with full context
    logger.error("integration_error", **error_details)
    
    # Send error notification webhook
    error_webhook = WebhookEntityWrapper(
        id=generate_uuid(),
        timestamp=datetime.now(),
        action=Action.ERROR,
        webhook_class="io.complyt.domain.IntegrationError",
        object=IntegrationErrorResult(
            transcript_id=transcript_id,
            error_type=type(error).__name__,
            error_message=str(error),
            recovery_actions=determine_recovery_actions(error),
            manual_intervention_required=requires_manual_intervention(error)
        ),
        host=context.get('webhook_host'),
        path=context.get('webhook_path')
    )
    
    await webhook_queue.publish(error_webhook)
    
    # Trigger recovery workflow if applicable
    if is_recoverable(error):
        await schedule_recovery_attempt(transcript_id, error_details)
```

### Recovery Strategies

```python
async def attempt_recovery(transcript_id: str, error_details: dict):
    """Attempt to recover from integration failures."""
    
    error_type = error_details['error_type']
    
    if error_type == 'TranscriptProcessingError':
        # Retry with different AI model or parameters
        return await retry_with_fallback_model(transcript_id)
        
    elif error_type == 'EntityCreationError':
        # Attempt partial entity creation
        return await create_entities_with_validation(transcript_id)
        
    elif error_type == 'WebhookDeliveryError':
        # Webhook system handles its own retries
        # Just log and monitor
        logger.info("webhook_retry_in_progress", transcript_id=transcript_id)
        return True
        
    else:
        # Unknown error - requires manual intervention
        await escalate_to_support(transcript_id, error_details)
        return False
```

## Monitoring & Observability

### Integration Metrics

```python
# Key metrics to track across the integration
INTEGRATION_METRICS = {
    # Processing Metrics
    'transcript_processing_duration': 'histogram',
    'ai_processing_duration': 'histogram', 
    'entity_creation_duration': 'histogram',
    'webhook_delivery_duration': 'histogram',
    
    # Success Metrics
    'transcript_processing_success_rate': 'gauge',
    'entity_creation_success_rate': 'gauge',
    'webhook_delivery_success_rate': 'gauge',
    'end_to_end_success_rate': 'gauge',
    
    # Quality Metrics
    'ai_confidence_score_distribution': 'histogram',
    'manual_review_rate': 'gauge',
    'entity_accuracy_rate': 'gauge',
    
    # System Metrics
    'queue_depth_transcript': 'gauge',
    'queue_depth_webhook': 'gauge',
    'active_workers': 'gauge',
    'error_rate_by_type': 'counter'
}

# Metric collection example
async def collect_integration_metrics():
    """Collect and report integration metrics."""
    
    # Processing duration metrics
    processing_times = await get_recent_processing_times()
    for duration in processing_times:
        TRANSCRIPT_PROCESSING_DURATION.observe(duration)
    
    # Success rate metrics
    success_rate = await calculate_success_rate(time_window='1h')
    END_TO_END_SUCCESS_RATE.set(success_rate)
    
    # Queue depth metrics
    transcript_queue_depth = await get_queue_depth('transcript_processing')
    webhook_queue_depth = await get_queue_depth('webhook_delivery')
    
    QUEUE_DEPTH_TRANSCRIPT.set(transcript_queue_depth)
    QUEUE_DEPTH_WEBHOOK.set(webhook_queue_depth)
```

### Health Checks

```python
async def integration_health_check():
    """Comprehensive health check for the entire integration."""
    
    health_status = {
        "status": "healthy",
        "timestamp": datetime.now().isoformat(),
        "components": {}
    }
    
    # Check VATGPT Gateway
    try:
        await vatgpt_client.health_check()
        health_status["components"]["vatgpt"] = "healthy"
    except Exception as e:
        health_status["components"]["vatgpt"] = f"unhealthy: {e}"
        health_status["status"] = "degraded"
    
    # Check OpenAI API
    try:
        await openai_client.models.list()
        health_status["components"]["openai"] = "healthy"
    except Exception as e:
        health_status["components"]["openai"] = f"unhealthy: {e}"
        health_status["status"] = "degraded"
    
    # Check Entity Service
    try:
        await entity_service.health_check()
        health_status["components"]["entity_service"] = "healthy"
    except Exception as e:
        health_status["components"]["entity_service"] = f"unhealthy: {e}"
        health_status["status"] = "unhealthy"
    
    # Check Webhook System
    try:
        webhook_queue_depth = await get_queue_depth('webhook_delivery')
        if webhook_queue_depth > 1000:
            health_status["components"]["webhook_system"] = f"degraded: queue depth {webhook_queue_depth}"
            health_status["status"] = "degraded"
        else:
            health_status["components"]["webhook_system"] = "healthy"
    except Exception as e:
        health_status["components"]["webhook_system"] = f"unhealthy: {e}"
        health_status["status"] = "unhealthy"
    
    # Check Database Connectivity
    try:
        await database.execute("SELECT 1")
        health_status["components"]["database"] = "healthy"
    except Exception as e:
        health_status["components"]["database"] = f"unhealthy: {e}"
        health_status["status"] = "unhealthy"
    
    return health_status
```

## Security Considerations

### End-to-End Security

```python
# Security measures across the integration
SECURITY_MEASURES = {
    # Input Validation
    'transcript_validation': {
        'max_size_mb': 10,
        'allowed_formats': ['text/plain', 'audio/wav', 'audio/mp3'],
        'content_filtering': True,
        'pii_detection': True
    },
    
    # API Security
    'api_security': {
        'authentication': 'JWT + API Key',
        'rate_limiting': '100 requests/minute',
        'request_signing': 'HMAC-SHA256',
        'tls_version': 'TLS 1.3'
    },
    
    # Data Protection
    'data_protection': {
        'encryption_at_rest': 'AES-256',
        'encryption_in_transit': 'TLS 1.3',
        'pii_anonymization': True,
        'data_retention': '7 years'
    },
    
    # Webhook Security
    'webhook_security': {
        'signature_verification': 'HMAC-SHA256',
        'https_only': True,
        'ip_whitelisting': 'optional',
        'rate_limiting': '1000 requests/hour'
    }
}

async def validate_transcript_security(transcript_data: dict):
    """Validate transcript data for security compliance."""
    
    # Check file size
    if len(transcript_data.get('content', '')) > 10 * 1024 * 1024:
        raise SecurityError("Transcript exceeds maximum size limit")
    
    # Detect PII
    pii_detected = await detect_pii(transcript_data['content'])
    if pii_detected and not transcript_data.get('pii_consent'):
        raise SecurityError("PII detected without explicit consent")
    
    # Content filtering
    if await contains_malicious_content(transcript_data['content']):
        raise SecurityError("Malicious content detected")
    
    # Validate webhook endpoint
    webhook_host = transcript_data.get('webhook_config', {}).get('host')
    if webhook_host and not is_allowed_webhook_host(webhook_host):
        raise SecurityError(f"Webhook host not in allowlist: {webhook_host}")
    
    return True
```

## Performance Optimization

### Integration Performance Tuning

```python
# Performance optimization strategies
PERFORMANCE_CONFIG = {
    # Concurrent Processing
    'concurrency': {
        'max_concurrent_transcripts': 50,
        'max_concurrent_ai_calls': 10,
        'max_concurrent_webhooks': 100,
        'worker_pool_size': 20
    },
    
    # Caching Strategy
    'caching': {
        'ai_response_cache_ttl': 3600,  # 1 hour
        'entity_cache_ttl': 1800,       # 30 minutes
        'webhook_config_cache_ttl': 7200, # 2 hours
        'cache_backend': 'redis'
    },
    
    # Batch Processing
    'batching': {
        'webhook_batch_size': 10,
        'webhook_batch_timeout': 5,  # seconds
        'entity_creation_batch_size': 20
    },
    
    # Connection Pooling
    'connection_pools': {
        'database_pool_size': 20,
        'redis_pool_size': 10,
        'http_client_pool_size': 50
    }
}

async def optimize_processing_pipeline():
    """Optimize the integration processing pipeline."""
    
    # Implement connection pooling
    await setup_connection_pools()
    
    # Configure caching layers
    await setup_caching_layers()
    
    # Optimize queue processing
    await optimize_queue_consumers()
    
    # Enable batch processing where applicable
    await enable_batch_processing()
```

## Testing Strategy

### Integration Testing

```python
@pytest.mark.integration
async def test_end_to_end_integration():
    """Test complete integration flow from transcript to webhook delivery."""
    
    # 1. Submit transcript
    transcript_data = create_test_transcript()
    response = await client.post("/api/v1/transcripts", json=transcript_data)
    
    assert response.status_code == 202
    job_id = response.json()["job_id"]
    
    # 2. Wait for AI processing
    await wait_for_job_completion(job_id, timeout=30)
    
    # 3. Verify entities created
    entities = await get_entities_by_transcript(transcript_data["transcript_id"])
    assert len(entities) > 0
    
    # 4. Verify webhooks delivered
    webhooks = await get_delivered_webhooks(transcript_data["transcript_id"])
    assert len(webhooks) > 0
    
    # 5. Verify webhook signatures
    for webhook in webhooks:
        assert verify_webhook_signature(webhook)
    
    # 6. Verify client received webhooks
    client_webhooks = await get_client_received_webhooks(transcript_data["client_id"])
    assert len(client_webhooks) == len(webhooks)

@pytest.mark.integration
async def test_error_recovery_flow():
    """Test error handling and recovery across the integration."""
    
    # Simulate AI processing failure
    with patch('openai_client.chat.completions.create') as mock_openai:
        mock_openai.side_effect = OpenAIError("Service unavailable")
        
        transcript_data = create_test_transcript()
        response = await client.post("/api/v1/transcripts", json=transcript_data)
        job_id = response.json()["job_id"]
        
        # Wait for error handling
        await wait_for_job_completion(job_id, timeout=30)
        
        # Verify error webhook sent
        error_webhooks = await get_error_webhooks(transcript_data["transcript_id"])
        assert len(error_webhooks) > 0
        assert error_webhooks[0]["error_type"] == "TranscriptProcessingError"
```

## Deployment & Operations

### Deployment Architecture

```yaml
# Kubernetes deployment configuration
apiVersion: apps/v1
kind: Deployment
metadata:
  name: complyt-integration
spec:
  replicas: 3
  selector:
    matchLabels:
      app: complyt-integration
  template:
    metadata:
      labels:
        app: complyt-integration
    spec:
      containers:
      - name: vatgpt-gateway
        image: complyt/vatgpt:v0.6
        ports:
        - containerPort: 8000
        env:
        - name: OPENAI_API_KEY
          valueFrom:
            secretKeyRef:
              name: complyt-secrets
              key: openai-api-key
        - name: WEBHOOK_SECRET_KEY
          valueFrom:
            secretKeyRef:
              name: complyt-secrets
              key: webhook-secret-key
        resources:
          requests:
            memory: "512Mi"
            cpu: "250m"
          limits:
            memory: "1Gi"
            cpu: "500m"
      
      - name: webhook-processor
        image: complyt/complyt-ai:latest
        env:
        - name: RABBITMQ_URL
          value: "amqp://rabbitmq:5672"
        - name: WEBHOOK_QUEUE
          value: "webhook_delivery"
        resources:
          requests:
            memory: "256Mi"
            cpu: "100m"
          limits:
            memory: "512Mi"
            cpu: "250m"
```

### Operational Runbooks

```markdown
# Integration Operations Runbook

## Common Issues and Solutions

### High Queue Depth
**Symptoms**: Webhook queue depth > 1000, processing delays
**Investigation**:
1. Check worker health: `kubectl get pods -l app=complyt-integration`
2. Check queue stats: `rabbitmqctl list_queues`
3. Check webhook endpoint health

**Resolution**:
1. Scale workers: `kubectl scale deployment complyt-integration --replicas=5`
2. Check for failing webhook endpoints
3. Implement circuit breaker if needed

### AI Processing Failures
**Symptoms**: High error rate in transcript processing
**Investigation**:
1. Check OpenAI API status
2. Review error logs: `kubectl logs -l app=complyt-integration | grep -i error`
3. Check rate limiting metrics

**Resolution**:
1. Verify API key validity
2. Implement exponential backoff
3. Switch to fallback model if needed

### Webhook Delivery Failures
**Symptoms**: Webhooks not reaching client endpoints
**Investigation**:
1. Check webhook endpoint health
2. Verify HMAC signatures
3. Review retry attempts

**Resolution**:
1. Contact client to verify endpoint
2. Check firewall/network connectivity
3. Implement dead letter queue
```

## Future Roadmap

### Planned Enhancements

1. **Real-Time Processing**
   - WebSocket-based streaming transcript processing
   - Real-time webhook notifications
   - Live processing status updates

2. **Advanced AI Features**
   - Multi-model AI processing (GPT-4, Claude, etc.)
   - Custom fine-tuned models per client
   - Confidence-based routing

3. **Enhanced Reliability**
   - Dead letter queue implementation
   - Circuit breaker patterns
   - Automatic failover mechanisms

4. **Scalability Improvements**
   - Horizontal auto-scaling
   - Load balancing optimization
   - Database sharding

5. **Monitoring & Analytics**
   - Advanced metrics dashboard
   - Predictive failure detection
   - Cost optimization analytics

---

*This integration architecture documentation is maintained by the Complyt Engineering Team. For questions, updates, or operational issues, please contact the platform team or create an issue in the complyt-ai repository.*

