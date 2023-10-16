# Authentication

Authentication is a service that manages access tokens.

## Authorization Server Profiles
- auth0 - The default profile. The service configured to communicate with Auth0 as the authorization server.
- stubAuth0 - The service configured to use constant access token saved in the local files.

## Environment Profiles
- development - Use for development on you local computer.
- demo - Use for the demo environment
- integration-test - Used as part of the integration tests
- load-test - Used for load tests
- production - Used in the production environment
- test - Used in unit tests

## Environment Variables
- CLUSTER_ID - Not relevant for production. The ID of the DB cluster
- VAULT_DB_ROLE - The DB role
- VAULT_HOST= The host of the vault cluster
- VAULT_ROLE= The 
- VAULT_ROLE_ID=ae5cef06-ab2e-c87a-312b-d083ee7931b6
- VAULT_SECRET_ID=880c4015-88dc-7b95-23fb-c1ba2823ae21
## Environment Variables

## Build and Test

```bash
./mvnw clean packge
```

## Usage
### Not production
You can change the development profile to another profile from the list.
The same applied to authorization server profiles.
```bash
java -Dspring.profiles.active=development,auth0 \
-DCLUSTER_ID={{cluster-db-id}} \
-DVAULT_DB_ROLE={{vault-role}} \
-DVAULT_HOST={{vault-host}} \
-DVAULT_ROLE={{vault-role}} \
-DVAULT_ROLE_ID={{vault-role-id}} \
-DVAULT_SECRET_ID={{vault-secret-id}} \
-jar target/{{application-file.jar}} 
```

### Producrion
```bash
java -Dspring.profiles.active=production,auth0 \
-DVAULT_DB_ROLE={{vault-role}} \
-DVAULT_HOST={{vault-host}} \
-DVAULT_ROLE={{vault-role}} \
-DVAULT_ROLE_ID={{vault-role-id}} \
-DVAULT_SECRET_ID={{vault-secret-id}} \
-jar target/{{application-file.jar}} 
```