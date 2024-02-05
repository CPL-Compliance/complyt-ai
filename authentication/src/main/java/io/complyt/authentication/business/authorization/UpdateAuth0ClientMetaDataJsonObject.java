package io.complyt.authentication.business.authorization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

@AllArgsConstructor
@Getter
public class UpdateAuth0ClientMetaDataJsonObject {
    final String clientName;
    final String tenantId;
    final String newClientId;
    final String newClientSecret;

    public String getAsJson() {
        String json = "{ \"name\": \"" + clientName +
                "\", \"client_metadata\": { \"tenant_id\": \"" + tenantId +
                "\", \"clientId\": " + newClientId + ", \"clientSecret\": " + newClientSecret + " } }";

        return json;
    }
}
