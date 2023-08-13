package testUtils;

import io.complyt.authentication.business.authorization.AccessToken;
import io.complyt.authentication.business.authorization.Auth0AccessToken;
import io.complyt.authentication.domain.Credentials;
import io.complyt.authentication.domain.Token;
import io.complyt.authentication.v1.models.ApiKey;
import io.complyt.authentication.v1.models.TokenDto;

import java.time.LocalDateTime;

public class TestUtilities {
    public static String apiKeyStr = "9a62acdf-cc85-4009-a57b-cf77c3eba1ec-3572db2e-486b-480a-995b-2e4d2b9104fa";
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
    static int expiresIn = 86400;
    static String tokenType = "Bearer";

    public static Token createOutputToken() {
        return new Token("", "", "", "", "", "",
                "", 0, "", LocalDateTime.now(), LocalDateTime.now());
    }

    public static Token createInputToken() {
        return createInputToken(apiKeyStr);
    }

    public static Token createInputToken(String apiKey) {
        return new Token(apiKey, "", "", "", "", "",
                "", 0, "", LocalDateTime.now(), LocalDateTime.now());
    }

    public static TokenDto createTokenDto() {
        return createTokenDto(apiKeyStr);
    }

    public static TokenDto createTokenDto(String apiKey) {
        return new TokenDto("", "", 0,
                "", LocalDateTime.now());
    }

    public static TokenDto createOutputTokenDto() {
        return new TokenDto(accessToken, scope, expiresIn, tokenType, LocalDateTime.now());
    }

    public static AccessToken createStubAccessToken() {
        return new AccessToken(accessToken, scope, expiresIn, tokenType);
    }

    public static AccessToken createAccessToken() {
        return new AccessToken("Access Token", "Scope", 0, "Token Type");
    }

    public static Auth0AccessToken createAuth0AccessToken() {
        return new Auth0AccessToken("Access Token", "Scope", 0, "Token Type");
    }

    public static Credentials createCredentials() {
        return new Credentials("id", "complytClientId", "complytClientSecret",
                "ClientID", "ClientSecret", "Audience", "GrantType",
                "audience", "Grant Type");
    }

    public static ApiKey createApiKey() {
        return new ApiKey(apiKeyStr);
    }

    public static Token createToken() {
        return createToken(createCredentials(), createAccessToken());
    }

    public static Token createToken(Credentials credentials, AccessToken accessToken) {
        return Token.builder()
                .complytClientId(credentials.getComplytClientId())
                .complytClientSecret(credentials.getComplytClientSecret())
                .accessToken(accessToken.getAccessToken())
                .scope(accessToken.getScope())
                .expiresIn(accessToken.getExpiresIn())
                .tokenType(accessToken.getTokenType())
                .build();
    }
}