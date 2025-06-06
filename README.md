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

