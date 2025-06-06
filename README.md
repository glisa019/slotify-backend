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

These values configure JWT signing and Stripe integration.

## X-Tenant-ID header

The application uses a multi-tenancy filter. Every request that targets tenant data should include an `X-Tenant-ID` header containing the tenant identifier:

```
X-Tenant-ID: my_tenant
```

The filter stores the tenant in a thread-local context so repositories and services can resolve the correct schema for the request.

