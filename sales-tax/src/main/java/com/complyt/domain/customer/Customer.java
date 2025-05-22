package com.complyt.domain.customer;

import com.complyt.domain.properties.ComplytIdProperty;
import com.complyt.domain.properties.InternalTimestampsProperty;
import com.complyt.domain.timestamps.Timestamps;
import com.complyt.domain.transaction.Address;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@AllArgsConstructor
@Document(collection = "customer")
@With
@Data
@Accessors(chain = true)
public class Customer implements ComplytIdProperty, InternalTimestampsProperty {
  private UUID complytId;
  @Id
  private String id;
  private String externalId;
  private String source;
  private String name;
  private Address address;
  private String tenantId;
  private String email;
  private CustomerType customerType;
  private Timestamps internalTimestamps;
  private Timestamps externalTimestamps;
  private String comment;
  private CustomerStatus customerStatus;


}