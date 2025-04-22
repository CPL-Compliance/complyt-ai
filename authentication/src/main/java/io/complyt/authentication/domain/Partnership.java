package io.complyt.authentication.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Value
@Builder
@With
@AllArgsConstructor
@Document(collection = "partnerships")
public class Partnership {
    @Id
    String id;

    @NonNull
    String tenantId;

    @NonNull
    String partnerName;

    @NonNull
    Map<String, Referral> supportedReferrals;
}