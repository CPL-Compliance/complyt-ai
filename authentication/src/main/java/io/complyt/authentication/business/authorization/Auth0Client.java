package io.complyt.authentication.business.authorization;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
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

    // Add getters and setters
    @Getter
    @AllArgsConstructor
    public static class ClientMetadata {
        private String tenant_id;
        private String clientId;
        private String clientSecret;

        // Add getters and setters
    }
    @Getter
    @AllArgsConstructor
    public static class RefreshTokenConfiguration {
        private String expiration_type;
        private int leeway;
        private boolean infinite_token_lifetime;
        private boolean infinite_idle_token_lifetime;
        private int token_lifetime;
        private int idle_token_lifetime;
        private String rotation_type;

        // Add getters and setters
    }
    @Getter
    @AllArgsConstructor
    public static class SigningKey {
        private String cert;
        private String pkcs7;
        private String subject;

        // Add getters and setters
    }
    @Getter
    @AllArgsConstructor
    public static class JwtConfiguration {
        private int lifetime_in_seconds;
        private boolean secret_encoded;

        // Add getters and setters
    }
}
