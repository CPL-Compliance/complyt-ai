//package io.complyt.business.message_queue.rabbit_mq;
//
//import com.fasterxml.jackson.core.JsonParser;
//import com.fasterxml.jackson.core.ObjectCodec;
//import com.fasterxml.jackson.databind.*;
//import io.complyt.domain.nexus.SalesTaxTracking;
//import io.complyt.domain.properties.ComplytIdProperty;
//import io.complyt.domain.transaction.Transaction;
//
//import java.io.IOException;
//import java.util.Map;
//
//public class ComplytIdPropertyDeserializer extends JsonDeserializer<ComplytIdProperty> {
//
//    private static final Map<String, Class<? extends ComplytIdProperty>> classMap = Map.of(
//            "Transaction", Transaction.class,
//            "SalesTaxTracking", SalesTaxTracking.class
//    );
//
//    @Override
//    public ComplytIdProperty deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
//        ObjectCodec codec = p.getCodec();
//        JsonNode node = codec.readTree(p);
//
//        String webhookClass = node.get("webhookClass").asText();
//
//        Class<? extends ComplytIdProperty> targetType = classMap.get(webhookClass);
//        if (targetType == null) {
//            throw new IllegalArgumentException("Unsupported webhookClass: " + webhookClass);
//        }
//
//        JsonNode objectNode = node.get("object");
//        return codec.treeToValue(objectNode, targetType);
//    }
//}
