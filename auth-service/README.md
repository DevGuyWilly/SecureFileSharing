# Auth Service

## Setup

1. Copy `.env.example` to `.env`: 

2. Update the `.env` file with your credentials

3. Start the database:

```bash
docker compose up
``` 

4. Run the application:

```bash
./mvnw spring-boot:run
``` 

## Environment Variables

The following environment variables are required:

- `DB_URL`: Database URL
- `DB_USERNAME`: Database username
- `DB_PASSWORD`: Database password
- `JWT_SECRET`: Secret key for JWT token generation

## Development

For local development, you can use the provided `.env.example` as a template.