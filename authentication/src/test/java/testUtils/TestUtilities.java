package testUtils;

import io.complyt.authentication.domain.Token;
import io.complyt.authentication.v1.models.TokenDto;


public class TestUtilities {
    static String apiKey = "929f1749-cfa7-46c0-8b6f-ee9602c7819c";
    static String accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InJ0RU1OdWRnTWx5aTJtMzVLSnJQRSJ9.eyJ0ZW5hbnRfaWQiOiJvcmdfU3R0QWNCa0s3YjMydzdrQSIsImlzcyI6Imh0dHBzOi8vZGV2ZWxvcG1lbnQtY29tcGx5dC51cy5hdXRoMC5jb20vIiwic3ViIjoiOGZsQmcxd2NqbmhYbkFVSEdGREw2QWJTMmZHSHZGM2hAY2xpZW50cyIsImF1ZCI6Imh0dHBzOi8vc2FsZXMtdGF4LXNlcnZpY2UvIiwiaWF0IjoxNjkwMjkzNjU3LCJleHAiOjE2OTAzODAwNTcsImF6cCI6IjhmbEJnMXdjam5oWG5BVUhHRkRMNkFiUzJmR0h2RjNoIiwic2NvcGUiOiJjcmVhdGU6Y3VzdG9tZXIgZGVsZXRlOmN1c3RvbWVyIHJlYWQ6Y3VzdG9tZXIgdXBkYXRlOmN1c3RvbWVyIGNyZWF0ZTp0cmFuc2FjdGlvbiByZWFkOnRyYW5zYWN0aW9uIHVwZGF0ZTp0cmFuc2FjdGlvbiBkZWxldGU6dHJhbnNhY3Rpb24gcmVhZDpzdGF0ZSBjcmVhdGU6ZXhlbXB0aW9uIHVwZGF0ZTpleGVtcHRpb24gZGVsZXRlOmV4ZW1wdGlvbiByZWFkOmV4ZW1wdGlvbiBjcmVhdGU6bmV4dXMgcmVhZDpuZXh1cyBkZWxldGU6bmV4dXMgdXBkYXRlOm5leHVzIHJlYWQ6bGluayByZWFkOnNhbGVzX3RheF9yYXRlcyIsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyJ9.CMXw27HyIdfeSqjIBixGn47t5y8JRPcGJV7o7V3z2QmctgqxmISEt-GyvMrVHh922qI6BzBkzA0Q8fO7Z2trqtpnTMm2MBd7nAHeGzDJZpH8tc_T0mCwkkM3iagrIbVzfYgWFqxNWqaYhmpjCGSko3SigyhRqTkSRcf_2DC-xaYy7NdN9ed4Mpl6CPcIGZpWw1QGEpgH2uTnauKYUJ5pgpFNpM1lyHODV3Od-6MeRhbntgqEdyT5Kwk7aXAteeGO6Lzbjb6UX8NL_zscTK2nSNqYiTJwUR5x9rh-V9jgbQqwU-Jb-tywMiL0EAEkDUDhJT4Wt7Cjbdh9P7sk3k5Cpg";
    static String scope = "create:customer delete:customer read:customer update:customer create:transaction read:transaction update:transaction delete:transaction read:state create:exemption update:exemption delete:exemption read:exemption create:nexus read:nexus delete:nexus update:nexus read:link read:sales_tax_rates";
    static int expiresIn = 86400;
    static String tokenType = "Bearer";

    public static Token createOutputToken() {
        return createOutputToken(apiKey);
    }

    public static Token createInputToken() {
        return createInputToken(apiKey);
    }

    public static Token createOutputToken(String apiKey) {
        return new Token(apiKey, accessToken, scope, expiresIn, tokenType);
    }

    public static Token createInputToken(String apiKey) {
        return new Token(apiKey, "", "", 0, "");
    }

    public static TokenDto createTokenDto() {
        return createTokenDto(apiKey);
    }

    public static TokenDto createTokenDto(String apiKey) {
        return new TokenDto(apiKey, "" , "", 0, "");
    }

    public static TokenDto createOutputTokenDto(String apiKey) {
        return new TokenDto(apiKey, accessToken , scope, expiresIn, tokenType);
    }
}
