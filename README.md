# Slotify

Slotify is a Spring Boot application that supports multi-tenant scheduling. It relies on Maven for builds and uses a simple header-based approach to select the tenant.

## Build

Use the Maven wrapper to compile and package the application:

```bash
./mvnw clean package
```

After building, run the jar from the `target` directory:

```bash
java -jar target/slotify-*.jar
```

## Database configuration

Add a JDBC driver for your database. For PostgreSQL the Maven dependency is:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
</dependency>
```

Configure the datasource via `application.properties` or environment variables:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/slotify
SPRING_DATASOURCE_USERNAME=slotify
SPRING_DATASOURCE_PASSWORD=secret
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
```

Before running the application for the first time, create the `system` schema.
Liquibase will load its tables into this schema:

```sql
CREATE SCHEMA system;
```

## Environment variables

Several settings can be provided either via environment variables or by editing `src/main/resources/application.properties`:

- `APP_JWT_SECRET` (`app.jwt.secret`)
- `STRIPE_API_SECRET` (`stripe.api.secret`)
- `STRIPE_API_PUBLIC` (`stripe.api.public`)
- `STRIPE_PRICE_ID` (`stripe.price.id`)
- `STRIPE_SUCCESS_URL` (`stripe.success.url`)
- `STRIPE_CANCEL_URL` (`stripe.cancel.url`)
- `DB_URL` (`spring.datasource.url`)
- `DB_USERNAME` (`spring.datasource.username`)
- `DB_PASSWORD` (`spring.datasource.password`)
- `DB_DRIVER` (`spring.datasource.driver-class-name`)

These values configure JWT signing and Stripe integration.

## X-Tenant-ID header

The application uses a multi-tenancy filter. Every request that targets tenant data should include an `X-Tenant-ID` header containing the tenant identifier:

```
X-Tenant-ID: my_tenant
```

The filter stores the tenant in a thread-local context so repositories and services can resolve the correct schema for the request.

## Postman collection

A Postman collection is provided to exercise the API. Import `Slotify.postman_collection.json` into Postman and update the environment variables for your host, tenant, and auth token.

## API documentation

The project uses springdoc-openapi to expose Swagger UI. Once the application is running, navigate to [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) to explore available endpoints. The raw OpenAPI spec can be retrieved from [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs).

## Retrieving tenant info

Tenant admins can fetch their tenant details and obtain a new payment link when
their subscription is still pending:

```
GET /api/tenants/me
```

The backend looks up the tenant using the authenticated admin's account.
If the subscription status is `ACTIVE`, only the tenant information is
returned. When the status is `PENDING`, a new Stripe Checkout session is
created and its `paymentUrl` is included in the response.

Public clients can also fetch tenant details by key without authentication:

```
GET /api/tenants/key/{schemaName}
```

## Activating a tenant

Tenant admins can activate their tenant after payment is confirmed:

```
POST /api/tenants/activate
```

This sets the tenant's subscription status to `ACTIVE`, creates the schema and
applies Liquibase migrations.
