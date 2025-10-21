# Cognos Analytics Enterprise Management Portal

A full-stack enterprise web application for centrally managing IBM Cognos Analytics 12.0 servers. This portal enables administrators to enroll servers, track content versions, perform bulk operations, and maintain comprehensive audit trails.

## Technology Stack

### Backend
- **Framework**: Spring Boot 3.2.0
- **Language**: Java 17
- **Database**: PostgreSQL 15+
- **Security**: Spring Security with JWT
- **ORM**: Spring Data JPA with Hibernate
- **Migrations**: Flyway

### Frontend
- **Framework**: React 18
- **UI Library**: Ant Design 5
- **Build Tool**: Vite
- **Routing**: React Router v6
- **HTTP Client**: Axios

## Project Structure

```
cognos-portal/
├── backend/                    # Spring Boot backend
│   ├── src/main/java/
│   │   └── com/pronto/cognosportal/
│   │       ├── config/        # Security, CORS, Async configs
│   │       ├── controller/    # REST controllers
│   │       ├── service/       # Business logic
│   │       ├── repository/    # Data access layer
│   │       ├── model/         # JPA entities
│   │       ├── dto/           # Data transfer objects
│   │       ├── security/      # JWT & authentication
│   │       └── exception/     # Exception handlers
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   └── db/migration/      # Flyway migrations
│   └── pom.xml
│
└── frontend/                   # React frontend
    ├── src/
    │   ├── components/        # React components
    │   │   ├── Auth/
    │   │   ├── Layout/
    │   │   ├── Dashboard/
    │   │   └── Servers/
    │   ├── services/          # API services
    │   ├── context/           # React context (Auth)
    │   ├── utils/             # Constants & helpers
    │   ├── App.jsx
    │   └── main.jsx
    └── package.json
```

## Prerequisites

- **Java 17 or higher**
- **Maven 3.6+**
- **Node.js 18+**
- **PostgreSQL 15+**

## Database Setup

1. Install PostgreSQL and create a database:

```sql
CREATE DATABASE cognos_portal;
CREATE USER cognos_admin WITH ENCRYPTED PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE cognos_portal TO cognos_admin;
```

2. The database schema will be created automatically by Flyway when the application starts.

3. A default admin user is created automatically:
   - **Username**: `admin`
   - **Password**: `admin123`
   - **IMPORTANT**: Change this password after first login!

## Backend Setup

1. Navigate to the backend directory:
```bash
cd backend
```

2. Set environment variables (or use application.yml):
```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=cognos_portal
export DB_USERNAME=cognos_admin
export DB_PASSWORD=your_password
export JWT_SECRET=your-256-bit-secret-key-change-this
export ENCRYPTION_KEY=your-aes-256-encryption-key-must-be-32-chars-long
```

3. Build the project:
```bash
./mvnw clean install
```

4. Run the application:
```bash
./mvnw spring-boot:run
```

The backend will start on `http://localhost:8080`

### Backend Configuration

Key configuration files:
- `application.yml`: Main configuration
- `application-dev.yml`: Development overrides
- `application-prod.yml`: Production overrides

To run with a specific profile:
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

## Frontend Setup

1. Navigate to the frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm run dev
```

The frontend will start on `http://localhost:3000`

### Frontend Build

To create a production build:
```bash
npm run build
```

The build artifacts will be in the `dist/` directory.

## Running the Complete Application

1. Start PostgreSQL database
2. Start the backend (port 8080)
3. Start the frontend (port 3000)
4. Navigate to `http://localhost:3000`
5. Login with admin/admin123

## Features Implemented

### Core Features
- ✅ User Authentication (JWT-based)
- ✅ Server Enrollment & Management
- ✅ Automated Server Polling (scheduled at 6 AM and 6 PM)
- ✅ Manual Server Polling
- ✅ Server Metadata Tracking
- ✅ Bulk Operations Framework
- ✅ Audit Logging
- ✅ Role-Based Access Control (ADMIN/USER)

### API Endpoints

#### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/logout` - User logout
- `GET /api/auth/current-user` - Get current user

#### Servers
- `GET /api/servers` - List all servers
- `GET /api/servers/{id}` - Get server details
- `POST /api/servers` - Enroll new server (Admin only)
- `DELETE /api/servers/{id}` - Delete server (Admin only)
- `POST /api/servers/{id}/poll` - Poll specific server
- `POST /api/servers/poll-all` - Poll all active servers

#### Users
- `GET /api/users` - List all users
- `GET /api/users/{id}` - Get user details
- `POST /api/users` - Create new user (Admin only)
- `DELETE /api/users/{id}` - Delete user (Admin only)

#### Bulk Operations
- `POST /api/bulk-operations/deploy` - Create bulk deploy operation
- `GET /api/bulk-operations` - List all operations
- `GET /api/bulk-operations/{id}` - Get operation details
- `GET /api/bulk-operations/{id}/details` - Get operation status per server

#### Audit
- `GET /api/audit/logs` - Get audit logs (Admin only)

## Security Features

### Encryption
- API keys are encrypted using AES-256 before storage
- Initialization Vector (IV) is generated per encryption
- Encryption key should be stored as environment variable

### Authentication
- JWT tokens with 1-hour expiration
- BCrypt password hashing with work factor 12
- Role-based authorization (ADMIN, USER)
- Stateless session management

### CORS
- Configured for development (localhost:3000, localhost:5173)
- Must be updated for production with actual domain

## Environment Variables

### Required Environment Variables

```bash
# Database
DB_HOST=localhost
DB_PORT=5432
DB_NAME=cognos_portal
DB_USERNAME=cognos_admin
DB_PASSWORD=your_secure_password

# Security
JWT_SECRET=your-256-bit-secret-key-change-in-production
ENCRYPTION_KEY=your-aes-256-encryption-key-32

# Optional
SPRING_PROFILES_ACTIVE=dev
```

## Development Notes

### Backend Development
- Hot reload is enabled with Spring Boot DevTools
- Lombok is used for boilerplate reduction
- JPA entities use Hibernate for ORM
- Async operations use Spring's @Async with custom thread pool

### Frontend Development
- Vite provides fast HMR (Hot Module Replacement)
- API proxy configured in vite.config.js for development
- Ant Design components for UI consistency
- dayjs for date formatting

### Cognos API Integration

The `CognosApiService` contains placeholder implementations for:
- Content inventory retrieval
- Content deployment
- Connection testing

**IMPORTANT**: These methods need to be updated with actual IBM Cognos Analytics 12.0 REST API endpoints. Consult the official documentation:
https://www.ibm.com/docs/en/cognos-analytics/12.0.x?topic=api-rest-reference

## Testing

### Backend Testing
```bash
cd backend
./mvnw test
```

### Frontend Testing
```bash
cd frontend
npm test
```

## Deployment

### Production Build

#### Backend
```bash
cd backend
./mvnw clean package -DskipTests
java -jar target/cognos-portal-1.0.0-SNAPSHOT.jar
```

#### Frontend
```bash
cd frontend
npm run build
# Serve the dist/ folder with nginx or any static file server
```

### Recommended Production Setup
1. Use environment-specific application.yml profiles
2. Secure JWT secret and encryption keys
3. Enable HTTPS
4. Configure proper CORS origins
5. Set up reverse proxy (nginx)
6. Use a production-grade database setup
7. Enable database backups
8. Set up monitoring and logging

## Troubleshooting

### Common Issues

**Database connection failed**
- Verify PostgreSQL is running
- Check credentials in application.yml
- Ensure database exists

**JWT validation fails**
- Check JWT_SECRET is properly set
- Ensure token hasn't expired
- Clear localStorage and login again

**API calls fail with 401**
- Token may be expired
- Login again to get new token

**Polling fails**
- Verify Cognos API endpoints are correct
- Check API keys are valid
- Review server base URL format

## Future Enhancements

The following features are planned for future releases:
- Bulk Operations wizard UI
- User management interface
- Audit log viewer with filtering
- Reports and analytics
- Server grouping
- Content version comparison
- Email notifications
- Scheduled bulk operations
- Webhook integrations

## Support

For issues and questions:
1. Check the troubleshooting section above
2. Review application logs
3. Consult the full specification in README.md

## License

Proprietary - Pronto Software

## Contributors

Built with Claude Code
