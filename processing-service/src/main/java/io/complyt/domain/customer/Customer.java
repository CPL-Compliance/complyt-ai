package io.complyt.domain.customer;

import io.complyt.domain.properties.ComplytIdProperty;
import io.complyt.domain.properties.InternalTimestampsProperty;
import io.complyt.domain.timestamps.Timestamps;
import io.complyt.domain.transaction.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.With;
import lombok.experimental.Accessors;

import java.util.UUID;

@AllArgsConstructor
@With
@Data
@Accessors(chain = true)
public class Customer implements ComplytIdProperty, InternalTimestampsProperty {
  private UUID complytId;
  
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