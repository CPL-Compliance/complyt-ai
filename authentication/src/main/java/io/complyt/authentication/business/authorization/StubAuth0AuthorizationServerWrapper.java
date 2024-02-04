package io.complyt.authentication.business.authorization;

import io.complyt.authentication.auth0_client.Auth0Client;
import io.complyt.authentication.auth0_client.ClientMetadata;
import io.complyt.authentication.domain.TenantIdAndNameObject;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

@EqualsAndHashCode
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class StubAuth0AuthorizationServerWrapper implements AuthorizationServerWrapper {
    String accessToken = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCIsImtpZCI6InJ0RU1OdWRnTWx5aTJtMzVLSnJQRSJ9.eyJ0ZW5hbnRfaWQiOiJvcmdfU3R0QWNCa0s3YjMydzdrQSIsImlzcyI6Imh0dHBzOi8vZGV2ZWxvcG1lbnQtY29tcGx5dC51cy5hdXRoMC5jb20vIiwic3ViIjoiOGZsQmcxd2NqbmhYbkFVSEdGREw2QWJTMmZHSHZGM2hAY2xpZW50cyIsImF1ZCI6Imh0dHBzOi8vc2FsZXMtdGF4LXNlcnZpY2UvIiwiaWF0IjoxNjkwMjkzNjU3LCJleHAiOjE2OTAzODAwNTcsImF6cCI6IjhmbEJnMXdjam5oWG5BVUhHRkRMNkFiUzJmR0h2RjNoIiwic2NvcGUiOiJjcmVhdGU6Y3VzdG9tZXIgZGVsZXRlOmN1c3RvbWVyIHJlYWQ6Y3VzdG9tZXIgdXBkYXRlOmN1c3RvbWVyIGNyZWF0ZTp0cmFuc2FjdGlvbiByZWFkOnRyYW5zYWN0aW9uIHVwZGF0ZTp0cmFuc2FjdGlvbiBkZWxldGU6dHJhbnNhY3Rpb24gcmVhZDpzdGF0ZSBjcmVhdGU6ZXhlbXB0aW9uIHVwZGF0ZTpleGVtcHRpb24gZGVsZXRlOmV4ZW1wdGlvbiByZWFkOmV4ZW1wdGlvbiBjcmVhdGU6bmV4dXMgcmVhZDpuZXh1cyBkZWxldGU6bmV4dXMgdXBkYXRlOm5leHVzIHJlYWQ6bGluayByZWFkOnNhbGVzX3RheF9yYXRlcyIsImd0eSI6ImNsaWVudC1jcmVkZW50aWFscyJ9.CMXw27HyIdfeSqjIBixGn47t5y8JRPcGJV7o7V3z2QmctgqxmISEt-GyvMrVHh922qI6BzBkzA0Q8fO7Z2trqtpnTMm2MBd7nAHeGzDJZpH8tc_T0mCwkkM3iagrIbVzfYgWFqxNWqaYhmpjCGSko3SigyhRqTkSRcf_2DC-xaYy7NdN9ed4Mpl6CPcIGZpWw1QGEpgH2uTnauKYUJ5pgpFNpM1lyHODV3Od-6MeRhbntgqEdyT5Kwk7aXAteeGO6Lzbjb6UX8NL_zscTK2nSNqYiTJwUR5x9rh-V9jgbQqwU-Jb-tywMiL0EAEkDUDhJT4Wt7Cjbdh9P7sk3k5Cpg";
    String managementToken = "eyJhbGciOiJSUzI1NiIstpZCI6InJ0RU1OdWRnTWx5aTJtMzVLSnJQRSJ9.eyJpc3MiOiJodHRwczovL2RldmVsb3BtZW50LWNvbXBseXQudXMuYXV0aDAuY29tLyIsInN1YiI6IkxpZEFCOHhHcWZ6REw2cHplRUg2ZVdtcWxURVowZ1FyQGNsaWVudHMiLCJhdWQiOiJodHRwczovL2RldmVsb3BtZW50LWNvbXBseXQudXMuYXV0aDAuY29tL2FwaS92Mi8iLCJpYXQiOjE3MDY0NDQxMTMsImV4cCI6MTcwNjUzMDUxMywiYXpwIjoiTGlkQUI4eEdxZnpETDZwemVFSDZlV21xbFRFWjBnUXIiLCJzY29wZSI6InJlYWQ6Y2xpZW50X2dyYW50cyBjcmVhdGU6Y2xpZW50X2dyYW50cyBkZWxldGU6Y2xpZW50X2dyYW50cyB1cGRhdGU6Y2xpZW50X2dyYW50cyByZWFkOnVzZXJzIHVwZGF0ZTp1c2VycyBkZWxldGU6dXNlcnMgY3JlYXRlOnVzZXJzIHJlYWQ6dXNlcnNfYXBwX21ldGFkYXRhIHVwZGF0ZTp1c2Vyc19hcHBfbWV0YWRhdGEgZGVsZXRlOnVzZXJzX2FwcF9tZXRhZGF0YSBjcmVhdGU6dXNlcnNfYXBwX21ldGFkYXRhIHJlYWQ6dXNlcl9jdXN0b21fYmxvY2tzIGNyZWF0ZTp1c2VyX2N1c3RvbV9ibG9ja3MgZGVsZXRlOnVzZXJfY3VzdG9tX2Jsb2NrcyBjcmVhdGU6dXNlcl90aWNrZXRzIHJlYWQ6Y2xpZW50cyB1cGRhdGU6Y2xpZW50cyBkZWxldGU6Y2xpZW50cyBjcmVhdGU6Y2xpZW50cyByZWFkOmNsaWVudF9rZXlzIHVwZGF0ZTpjbGllbnRfa2V5cyBkZWxldGU6Y2xpZW50X2tleXMgY3JlYXRlOmNsaWVudF9rZXlzIHJlYWQ6Y29ubmVjdGlvbnMgdXBkYXRlOmNvbm5lY3Rpb25zIGRlbGV0ZTpjb25uZWN0aW9ucyBjcmVhdGU6Y29ubmVjdGlvbnMgcmVhZDpyZXNvdXJjZV9zZXJ2ZXJzIHVwZGF0ZTpyZXNvdXJjZV9zZXJ2ZXJzIGRl";
    String scope = "create:customer delete:customer read:customer update:customer create:transaction read:transaction update:transaction delete:transaction read:state create:exemption update:exemption delete:exemption read:exemption create:nexus read:nexus delete:nexus update:nexus read:link read:sales_tax_rates";
    String managementScope = "read:client_grants create:client_grants delete:client_grants update:client_grants read:users update:users delete:users create:users read:users_app_metadata update:users_app_metadata delete:users_app_metadata create:users_app_metadata read:user_custom_blocks create:user_custom_blocks delete:user_custom_blocks create:user_tickets read:clients update:clients delete:clients create:clients read:client_keys update:client_keys delete:client_keys create:client_keys read:connections update:connections delete:connections create:connections read:resource_servers update:resource_servers delete:resource_servers create:resource_servers read:device_credentials update:device_credentials delete:device_credentials create:device_credentials read:rules update:rules delete:rules create:rules read:rules_configs update:rules_configs delete:rules_configs read:hooks update:hooks delete:hooks create:hooks read:actions update:actions delete:actions create:actions read:email_provider update:email_provider delete:email_provider create:email_provider blacklist:tokens read:stats read:insights read:tenant_settings update:tenant_settings read:logs read:logs_users read:shields create:shields update:shields delete:shields read:anomaly_blocks delete:anomaly_blocks update:triggers read:triggers read:grants delete:grants read:guardian_factors update:guardian_factors read:guardian_enrollments delete:guardian_enrollments create:guardian_enrollment_tickets read:user_idp_tokens create:passwords_checking_job delete:passwords_checking_job read:custom_domains delete:custom_domains create:custom_domains update:custom_domains read:email_templates create:email_templates update:email_templates read:mfa_policies update:mfa_policies read:roles create:roles delete:roles update:roles read:prompts update:prompts read:branding update:branding delete:branding read:log_streams create:log_streams delete:log_streams update:log_streams create:signing_keys read:signing_keys update:signing_keys read:limits update:limits create:role_members read:role_members delete:role_members read:entitlements read:attack_protection update:attack_protection read:organizations_summary create:authentication_methods read:authentication_methods update:authentication_methods delete:authentication_methods read:organizations update:organizations create:organizations delete:organizations create:organization_members read:organization_members delete:organization_members create:organization_connections read:organization_connections update:organization_connections delete:organization_connections create:organization_member_roles read:organization_member_roles delete:organization_member_roles create:organization_invitations read:organization_invitations delete:organization_invitations read:scim_config create:scim_config update:scim_config delete:scim_config create:scim_token read:scim_token delete:scim_token delete:phone_providers create:phone_providers read:phone_providers update:phone_providers delete:phone_templates create:phone_templates read:phone_templates update:phone_templates create:encryption_keys read:encryption_keys update:encryption_keys delete:encryption_keys read:sessions delete:sessions read:refresh_tokens delete:refresh_tokens read:client_credentials create:client_credentials update:client_credentials delete:client_credentials";
    int expiresIn = 86400;
    String tokenType = "Bearer";

    @Override
    public Mono<AccessToken> getAccessToken(@NonNull String clientId, @NonNull String clientSecret,
                                            @NonNull String audience, @NonNull String grantType) {
        return Mono.just(new AccessToken(accessToken, scope, expiresIn, tokenType));
    }

    @Override
    public Mono<Auth0Client> removeApiKeyFromClient(@NonNull String clientName, @NonNull String clientId, @NonNull String tenantId, @NonNull String accessToken, @RequestParam(value = "newClientId", required = false) String newClientId,
                                                    @RequestParam(value = "newClientSecret", required = false) String newClientSecret) {
        return Mono.just(new Auth0Client("tenant", false, false, "name", new ClientMetadata("tenantId", "ClientId", "clientSecret"),
                true, true, false, false,null, null, "clientId", true, "clientSecret",
                null, "appType", null, true ));
    }

    @Override
    public Mono<AccessToken> getManagementAccessToken() {
        return Mono.just(new AccessToken(managementToken, managementScope, expiresIn, tokenType));
    }

    @Override
    public Mono<TenantIdAndNameObject> getTenantIdAndClientNameFromAuth0(@NonNull String clientId, @NonNull String accessToken) {
        return Mono.just(new TenantIdAndNameObject("test_tenant", "test_name"));
    }
}
