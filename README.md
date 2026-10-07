# SmartBus Backend

Spring Boot REST API for the SmartBus application. Requires Java 25, Maven 3.8 or later, and MySQL 8 or later.

## Local demo setup

1. Copy `.env.example` to `.env`.
2. Set `DB_PASSWORD` to your local MySQL password. Change `ADMIN_PASSWORD` from the demo value before exposing the app to a network.
3. Start MySQL, then from this directory run:

   ```powershell
   mvn spring-boot:run
   ```

The backend loads `.env` from its working directory. It creates the demo admin account on first start if one does not already exist. The default local demo credentials are `admin` / `admin123`; replace them for any shared or deployed environment. Never commit `.env`; only `.env.example` is intended for Git.

Run backend tests with `mvn test`.

## Environment variables

| Variable | Purpose |
| --- | --- |
| `DB_URL` | MySQL JDBC connection URL |
| `DB_USERNAME` | MySQL account |
| `DB_PASSWORD` | MySQL password |
| `ADMIN_USERNAME` | Initial admin login name |
| `ADMIN_PASSWORD` | Initial admin password (used only when creating the account) |
| `CORS_ALLOWED_ORIGINS` | Comma-separated frontend origins |

For a separate backend GitHub repository, use this directory as the repository root. Its `.gitignore` excludes local `.env`, Maven output, and logs while retaining `.env.example`.
