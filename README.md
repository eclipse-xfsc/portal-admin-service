# Admin service
In order to run this application locally, the following should be satisfied:
- Identity system (keycloak) should be accessible from localhost
- Valid SMTP account should be available
- PostgreSQL database should be accessible
- GAIA-X Federated Catalogue service or Mocking service (https://gitlab.com/gaia-x/data-infrastructure-federation-services/por/demo) 
should be accessible

## Configuring database access
Access to PostgreSQL database is controlled with the following environment variables:
  * PSQL_DB_HOST (host)
  * PSQL_DB_PORT (port)
  * PSQL_DB_DS  (schema)
  * PSQL_DB_USERNAME (username) 
  * PSQL_DB_PASSWORD (password)

If DB is deployed to kubernetes cluster, the port-forwarding option is an appropriate workaround for making access working. In order to achieve this,
run the following:

~~~~
$ kubectl port-forward --namespace gaia-x-portal svc/portal-infra-postgresql 5432:5432
Forwarding from 127.0.0.1:5432 -> 5432
Forwarding from [::1]:5432 -> 5432
~~~~

In such scenario, environment variables should use the following values:
 * PSQL_DB_HOST="localhost" 
 * PSQL_DB_PORT=5432

Other variables - PSQL_DB_DS, PSQL_DB_USERNAME and PSQL_DB_PASSWORD - are configured in Portal infrastructure project 
(https://gitlab.com/gaia-x/data-infrastructure-federation-services/por/infra-mesh/-/settings/ci_cd) and contains sensitive information.

For instance, they might have the following values:
  * PSQL_DB_DS="gaiax"
  * PSQL_DB_USERNAME="gaiax_ppr"
  * PSQL_DB_PASSWORD="12345"

## Configuring mailing system
In order to send different emails from this service, the working SMTP server and account should be available.
Configuration is done with the following environment variables:
  * MAIL_SUPPORT_ADDRESS (email address used for "sender") 
  * MAIL_SMTP_HOST (SMTP server's hostname) 
  * MAIL_SMTP_PORT (SMTP port) 
  * MAIL_SMTP_USERNAME (SMTP account's username) 
  * MAIL_SMTP_PASSWORD (SMTP account's password)
All these variables are configured in Portal infrastructure project 
(https://gitlab.com/gaia-x/data-infrastructure-federation-services/por/infra-mesh/-/settings/ci_cd) and contains sensitive information.

For instance, they might have the following values:
  * MAIL_SUPPORT_ADDRESS="noreply@gxfs.dev" 
  * MAIL_SMTP_HOST="in-v3.mailjet.com" 
  * MAIL_SMTP_PORT=587 
  * MAIL_SMTP_USERNAME="314159265759" 
  * MAIL_SMTP_PASSWORD="12345"

## Portal external address
The URL for the Portal itself is a configurable setting incorporated in PORTAL_URL environment variable.
For instance:
  * PORTAL_URL="portal.gxfs.dev"

## Configuring keycloak
In order to protect provided endpoints and develop/test authorization with OIDC, the access to working identity system (keycloak)
should be configured. The following variables should be specified:
  * KC_URL (URL to Keycloak)
  * KC_REALM (realm name in Keycloak)
  * KC_RESOURCE (client in Keycloak)

All these variables are configured in Portal infrastructure project 
(https://gitlab.com/gaia-x/data-infrastructure-federation-services/por/infra-mesh/-/settings/ci_cd) and contains sensitive information.

For instance, they might have the following values:
  * KC_URL="http://78.138.66.50/" 
  * KC_REALM="gaiax_realm"
  * KC_RESOURCE="gaiax_client"

## Configuration of external systems
Open the application.yml file and configure value for the services.identity.uri.internal setting representing URI for
GAIA-X Federated Catalogue service. For instance:

~~~~
services.identity.uri.internal: http://gaia-x.identity.ext:8081/demo
~~~~

Make sure that such external system is available from your host.


## Application run
With all these configurations, use the following command to run application:

~~~~
$ PSQL_DB_HOST="localhost" PSQL_DB_PORT=5432 PSQL_DB_DS="gaiax" PSQL_DB_USERNAME="gaiax_ppr" PSQL_DB_PASSWORD="12345" \
  MAIL_SUPPORT_ADDRESS="noreply@gxfs.dev" MAIL_SMTP_HOST="in-v3.mailjet.com" MAIL_SMTP_PORT=587 \
  MAIL_SMTP_USERNAME="314159265759" MAIL_SMTP_PASSWORD="12345" \
  PORTAL_URL="portal.gxfs.dev" \
  KC_URL="http://78.138.66.50/" KC_REALM="gaiax_realm" KC_RESOURCE="gaiax_client" \
  ./mvnw clean spring-boot:run
~~~~

The application should be started on localhost and use port, configured in application.yml file:

~~~~
server.port: 8088
~~~~
