package test_utils.unit_tests;

import io.complyt.authentication.auth0_client.ClientMetadata;
import io.complyt.authentication.business.authorization.AccessToken;
import io.complyt.authentication.business.authorization.Auth0AccessToken;
import io.complyt.authentication.auth0_client.Auth0Client;
import io.complyt.authentication.domain.ApiKey;
import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.domain.TenantIdAndNameObject;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.domain.enums.ApiKeyStatus;
import io.complyt.authentication.security.EncryptedData;
import io.complyt.authentication.v1.models.ApiKeyDto;
import io.complyt.authentication.v1.models.CredentialsDto;
import io.complyt.authentication.v1.models.TokenDto;
import io.complyt.authentication.auth0_client.Auth0Client;
import lombok.NonNull;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDateTime;
import java.util.UUID;

public class TestUtilities {
    public static String tenantId = UUID.randomUUID().toString();

    public static String apiKeyClientIdStr = "9a62acdf-cc85-4009-a57b-cf77c3eba1ec";
    public static String apiKeyClientSecretStr = "3572db2e-486b-480a-995b-2e4d2b9104fa";
    public static String invalidApiKeyClientIdStr = "9a62acdf-cc85-4009-a57b-cf77c3eba1e";
    public static String invalidApiKeyClientSecretStr = "3572db2e-486b-480a-995b-";

    public static String name = "Name";

    public static String managementToken = "managementToken";

    public static String managementScope = "managementScope";

    static String accessToken = "accessToken";
    static String scope = "scope";
    public static int expiresIn = 86400;
    static String tokenType = "Bearer";


    public static Jwt.Builder stubJwt() {
        return Jwt.withTokenValue("token").header("typ", "JWT")
                .claim("tenant_id", "it_tenant");
    }

    public static Token createOutputToken() {
        return new Token("", "", "", "", "", "",
                "", 0, "", LocalDateTime.now(), LocalDateTime.now());
    }

    public static Token createInputToken() {
        return createInputToken(apiKeyClientIdStr);
    }

    public static Token createInputToken(String apiKey) {
        return new Token(apiKey, "", "", "", "", "",
                "", 0, "", LocalDateTime.now(), LocalDateTime.now());
    }

    public static TokenDto createTokenDto() {
        return new TokenDto("", "", 0, "", LocalDateTime.now(), LocalDateTime.now());
    }

    public static TokenDto createOutputTokenDto() {
        return new TokenDto(accessToken, scope, expiresIn, tokenType, LocalDateTime.now(), LocalDateTime.now());
    }

    public static AccessToken createStubAccessToken() {
        return new AccessToken(accessToken, scope, expiresIn, tokenType);
    }

    public static AccessToken createAccessToken() {
        return new AccessToken("Access Token", "Scope", expiresIn, "Token Type");
    }

    public static AccessToken createManagementAccessToken() {
        return new AccessToken("Access Token", "Scope", expiresIn, "Token Type");
    }
    public static AccessToken createStubManagementAccessToken() {
        return new AccessToken(managementToken, managementScope, expiresIn, tokenType);
    }

    public static Auth0AccessToken createAuth0AccessToken() {
        return new Auth0AccessToken("Access Token", "Scope", expiresIn, "Token Type");
    }

    public static Credentials createCredentials() {
        return new Credentials("id", "complytClientId", "complytClientSecret",
                "ClientID", "ClientSecret", "Audience", "GrantType",
                "audience", "Grant Type", "TenantId", "Name", ApiKeyStatus.ACTIVE);
    }

    public static Auth0Client createAuth0Client() {
        return new Auth0Client("tenant", false, false, "name", new ClientMetadata("tenantId", "ClientId", "clientSecret"),
                true, true, false, false,null, null, "clientId", true, "clientSecret",
                null, "appType", null, true );
    }

    public static ApiKey createApiKey() {
        return new ApiKey(apiKeyClientIdStr, apiKeyClientSecretStr);
    }

    public static ApiKeyDto createApiKeyDto() {
        return new ApiKeyDto(apiKeyClientIdStr, apiKeyClientSecretStr);
    }

    public static Token createToken() {
        return createToken(createCredentials(), createAccessToken());
    }

    public static Token createToken(Credentials credentials, AccessToken accessToken) {
        return Token.builder()
                .complytClientId(credentials.getComplytClientId())
                .complytClientSecret(credentials.getComplytClientSecret())
                .accessToken(accessToken.accessToken())
                .scope(accessToken.scope())
                .expiresIn(accessToken.expiresIn())
                .tokenType(accessToken.tokenType())
                .createdAt(LocalDateTime.now())
                .accessToken("")
                .build();
    }

    public static CredentialsDto createCredentialsDto() {
        return new CredentialsDto("clientId", "clientSecret");
    }

    public static CredentialsDto createCredentialsDtoMissingClientId() {
        return new CredentialsDto(null, "clientSecret");
    }

    public static CredentialsDto createCredentialsDtoBlankClientId() {
        return new CredentialsDto("", "clientSecret");
    }

    public static CredentialsDto createCredentialsDtoMissingClientSecret() {
        return new CredentialsDto("clientId", null);
    }

    public static CredentialsDto createCredentialsDtoBlankClientSecret() {
        return new CredentialsDto("clientId", "");
    }

    public static Credentials createCredentials(String clientId, String clientSecret) {
        return Credentials.builder().clientId(clientId).clientSecret(clientSecret).build();
    }

    public static Credentials createDecryptedCreds(Credentials credentials) {
        return Credentials.builder().clientId(credentials.getClientId()).clientSecret(credentials.getClientSecret())
                .audience(credentials.getAudience())
                .grantType(credentials.getGrantType()).complytClientId(credentials.getComplytClientId())
                .complytClientSecret(credentials.getComplytClientSecret()).build();
    }

    public static Credentials createEncryptedCredentials(@NonNull ApiKey apiKey,
                                                         @NonNull EncryptedData clientIdEncryptedData,
                                                         @NonNull EncryptedData clientSecretEncryptedData,
                                                         String clientSecretEncoded) {
        return Credentials.builder().clientId(clientIdEncryptedData.cipherText())
                .clientIdIv(clientIdEncryptedData.iv())
                .clientSecret(clientSecretEncryptedData.cipherText())
                .clientSecretIv(clientSecretEncryptedData.iv()).audience("audience").grantType("grantType")
                .complytClientId(apiKey.clientId())
                .complytClientSecret(clientSecretEncoded)
                .name("Name")
                .tenantId(tenantId)
                .build();
    }

    public static Token createEncryptedToken(@NonNull Token token, @NonNull EncryptedData accessTokenEncryptedData,
                                             @NonNull EncryptedData scopeEncryptedData) {
        return Token.builder()
                .accessToken(accessTokenEncryptedData.cipherText())
                .accessTokenIv(accessTokenEncryptedData.iv())
                .tokenType(token.getTokenType())
                .complytClientSecret(token.getComplytClientSecret())
                .complytClientId(token.getComplytClientId())
                .expiresIn(token.getExpiresIn())
                .scope(scopeEncryptedData.cipherText())
                .scopeIv(scopeEncryptedData.iv())
                .expireAt(token.getExpireAt())
                .createdAt(token.getCreatedAt())
                .build();
    }
    public static EncryptedData createEncryptedClientId(Credentials credentials){
        return new EncryptedData(credentials.getClientIdIv(), credentials.getClientId());
    }
    public static TenantIdAndNameObject createTenantIdAndNameObject(Credentials credentials){
        return new TenantIdAndNameObject(credentials.getTenantId(), credentials.getName());
    }
}