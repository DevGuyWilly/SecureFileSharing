# Auth Service

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/YourUsername/YourRepo)

## Development in Gitpod

1. Click the "Open in Gitpod" button above
2. Wait for the workspace to start
3. The application will automatically:
   - Start MySQL
   - Build the application
   - Run the Spring Boot service

## API Endpoints

All endpoints are available at:
https://8080-${GITPOD_WORKSPACE_URL#https://}

- POST /api/auth/register - Register new user
- POST /api/auth/login - Login user
- GET /api/auth/validate-token - Validate JWT token
- POST /api/auth/logout - Logout user

## Testing in Gitpod

Use curl or Postman to test the endpoints:
