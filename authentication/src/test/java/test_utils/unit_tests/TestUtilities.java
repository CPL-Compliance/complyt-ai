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

    static String accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InJ0RU1OdWRnTWx5aTJtMzVLSnJQRSJ9." +
            "eyJ0ZW5hbnRfaWQiOiJvcmdfU3R0QWNCa0s3YjMydzdrQSIsImlzcyI6Imh0dHBzOi8vZGV2ZWxvcG1lbnQtY29tcGx5dC51cy5" +
            "hdXRoMC5jb20vIiwic3ViIjoiOGZsQmcxd2NqbmhYbkFVSEdGREw2QWJTMmZHSHZGM2hAY2xpZW50cyIsImF1ZCI6Imh0dHBzOi8" +
            "vc2FsZXMtdGF4LXNlcnZpY2UvIiwiaWF0IjoxNjkwMjkzNjU3LCJleHAiOjE2OTAzODAwNTcsImF6cCI6IjhmbEJnMXdjam5oWG5B" +
            "VUhHRkRMNkFiUzJmR0h2RjNoIiwic2NvcGUiOiJjcmVhdGU6Y3VzdG9tZXIgZGVsZXRlOmN1c3RvbWVyIHJlYWQ6Y3VzdG9tZXIg" +
            "dXBkYXRlOmN1c3RvbWVyIGNyZWF0ZTp0cmFuc2FjdGlvbiByZWFkOnRyYW5zYWN0aW9uIHVwZGF0ZTp0cmFuc2FjdGlvbiBkZWx" +
            "ldGU6dHJhbnNhY3Rpb24gcmVhZDpzdGF0ZSBjcmVhdGU6ZXhlbXB0aW9uIHVwZGF0ZTpleGVtcHRpb24gZGVsZXRlOmV4ZW1w" +
            "dGlvbiByZWFkOmV4ZW1wdGlvbiBjcmVhdGU6bmV4dXMgcmVhZDpuZXh1cyBkZWxldGU6bmV4dXMgdXBkYXRlOm5leHVzIHJlYWQ6" +
            "bGluayByZWFkOnNhbGVzX3RheF9yYXRlcyIsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyJ9.CMXw27HyIdfeSqjIBixGn47t5y" +
            "8JRPcGJV7o7V3z2QmctgqxmISEt-GyvMrVHh922qI6BzBkzA0Q8fO7Z2trqtpnTMm2MBd7nAHeGzDJZpH8tc_T0mCwkkM3ia" +
            "grIbVzfYgWFqxNWqaYhmpjCGSko3SigyhRqTkSRcf_2DC-xaYy7NdN9ed4Mpl6CPcIGZpWw1QGEpgH2uTnauKYUJ5pgpFNpM1" +
            "lyHODV3Od-6MeRhbntgqEdyT5Kwk7aXAteeGO6Lzbjb6UX8NL_zscTK2nSNqYiTJwUR5x9rh-V9jgbQqwU-Jb-tywMiL0EAEkDU" +
            "DhJT4Wt7Cjbdh9P7sk3k5Cpg";
    static String scope = "create:customer delete:customer read:customer update:customer create:transaction " +
            "read:transaction update:transaction delete:transaction read:state create:exemption update:exemption " +
            "delete:exemption read:exemption create:nexus read:nexus delete:nexus update:nexus read:link " +
            "read:sales_tax_rates";
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