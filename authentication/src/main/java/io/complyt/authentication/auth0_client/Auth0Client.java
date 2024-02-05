package io.complyt.authentication.auth0_client;

import lombok.*;

import java.util.List;


@With
public record Auth0Client(String tenant, boolean global, boolean is_token_endpoint_ip_header_trusted, String name,
                          ClientMetadata client_metadata, boolean is_first_party, boolean sso_disabled,
                          boolean cross_origin_auth, boolean oidc_conformant, RefreshTokenConfiguration refresh_token,
                          List<SigningKey> signing_keys, String client_id, boolean callback_url_template,
                          String client_secret, JwtConfiguration jwt_configuration, String app_type,
                          List<String> grant_types, boolean custom_login_page_on) {

}
