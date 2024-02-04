package io.complyt.authentication.auth0_client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.With;

import java.util.List;

@Getter
@AllArgsConstructor
@With
public class Auth0Client {

    private String tenant;
    private boolean global;
    private boolean is_token_endpoint_ip_header_trusted;
    private String name;
    private ClientMetadata client_metadata;
    private boolean is_first_party;
    private boolean sso_disabled;
    private boolean cross_origin_auth;
    private boolean oidc_conformant;
    private RefreshTokenConfiguration refresh_token;
    private List<SigningKey> signing_keys;
    private String client_id;
    private boolean callback_url_template;
    private String client_secret;
    private JwtConfiguration jwt_configuration;
    private String app_type;
    private List<String> grant_types;
    private boolean custom_login_page_on;
}
