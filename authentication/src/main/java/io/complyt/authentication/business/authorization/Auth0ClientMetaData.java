package io.complyt.authentication.business.authorization;

import com.nimbusds.jose.shaded.gson.annotations.SerializedName;
import io.complyt.authentication.auth0_client.ClientMetadata;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
public class Auth0ClientMetaData {
    final String clientName;
    final ClientMetadata client_metadata;

    public String getAsJson() {
        String json = "{ \"name\": \"" + clientName +
                "\", \"client_metadata\": { " +
                    "\"tenant_id\": \"" + client_metadata.getTenant_id() +
                    "\", \"clientId\": " + client_metadata.getClientId() +
                    ", \"clientSecret\": " + client_metadata.getClientSecret() + " } }";

        return json;
    }
}
