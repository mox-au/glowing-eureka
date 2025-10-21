# Cognos Analytics Enterprise Management Portal - Project Specification

## Executive Summary

Build an enterprise web application to centrally manage approximately 400 IBM Cognos Analytics 12.0 servers. This portal will enable administrators to enroll servers, track content versions, perform bulk operations, and maintain comprehensive audit trails of all activities.

## Technology Stack

### Frontend
- **Framework**: React 18+
- **UI Library**: Ant Design (antd)
- **State Management**: React Context API or Redux (your choice)
- **HTTP Client**: Axios
- **Build Tool**: Vite or Create React App

### Backend
- **Framework**: Spring Boot 3.x
- **Language**: Java 17+
- **Database**: PostgreSQL 15+
- **ORM**: Spring Data JPA with Hibernate
- **Security**: Spring Security
- **Scheduling**: Spring Scheduler for polling tasks
- **Migration Tool**: Flyway for database versioning

### Deployment
- **Environment**: Pronto Cloud (treat as on-premise infrastructure)
- **Concurrent Users**: Optimized for 10 concurrent users

## Project Structure

```
cognos-portal/
├── backend/
│   ├── src/main/java/com/pronto/cognosportal/
│   │   ├── config/
│   │   │   ├── SecurityConfig.java
│   │   │   ├── SchedulerConfig.java
│   │   │   └── CorsConfig.java
│   │   ├── controller/
│   │   │   ├── AuthController.java
│   │   │   ├── UserController.java
│   │   │   ├── ServerController.java
│   │   │   ├── ContentController.java
│   │   │   ├── BulkOperationController.java
│   │   │   ├── ReportController.java
│   │   │   └── AuditController.java
│   │   ├── service/
│   │   │   ├── AuthService.java
│   │   │   ├── UserService.java
│   │   │   ├── ServerService.java
│   │   │   ├── PollingService.java
│   │   │   ├── CognosApiService.java
│   │   │   ├── BulkOperationService.java
│   │   │   ├── EncryptionService.java
│   │   │   └── AuditService.java
│   │   ├── repository/
│   │   │   ├── UserRepository.java
│   │   │   ├── CognosServerRepository.java
│   │   │   ├── ServerMetadataRepository.java
│   │   │   ├── ContentInventoryRepository.java
│   │   │   ├── BulkOperationRepository.java
│   │   │   ├── ChangeHistoryRepository.java
│   │   │   └── AuditLogRepository.java
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   ├── CognosServer.java
│   │   │   ├── ServerMetadata.java
│   │   │   ├── ContentInventory.java
│   │   │   ├── BulkOperation.java
│   │   │   ├── ChangeHistory.java
│   │   │   └── AuditLog.java
│   │   ├── dto/
│   │   │   ├── LoginRequest.java
│   │   │   ├── ServerEnrollmentRequest.java
│   │   │   ├── BulkDeployRequest.java
│   │   │   └── [other DTOs]
│   │   ├── security/
│   │   │   ├── JwtTokenProvider.java
│   │   │   ├── JwtAuthenticationFilter.java
│   │   │   └── CustomUserDetailsService.java
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java
│   │       └── [custom exceptions]
│   ├── src/main/resources/
│   │   ├── application.yml
│   │   ├── application-dev.yml
│   │   ├── application-prod.yml
│   │   └── db/migration/
│   │       ├── V1__initial_schema.sql
│   │       ├── V2__create_audit_tables.sql
│   │       └── [other migrations]
│   └── pom.xml
│
└── frontend/
    ├── src/
    │   ├── components/
    │   │   ├── Auth/
    │   │   │   └── LoginForm.jsx
    │   │   ├── Layout/
    │   │   │   ├── AppLayout.jsx
    │   │   │   ├── Header.jsx
    │   │   │   └── Sidebar.jsx
    │   │   ├── Dashboard/
    │   │   │   ├── DashboardOverview.jsx
    │   │   │   ├── StatCard.jsx
    │   │   │   └── RecentActivity.jsx
    │   │   ├── Servers/
    │   │   │   ├── ServerList.jsx
    │   │   │   ├── ServerDetail.jsx
    │   │   │   ├── ServerEnrollmentForm.jsx
    │   │   │   └── ServerContentInventory.jsx
    │   │   ├── BulkOperations/
    │   │   │   ├── BulkOperationWizard.jsx
    │   │   │   ├── ServerSelector.jsx
    │   │   │   ├── ContentUploader.jsx
    │   │   │   └── OperationProgress.jsx
    │   │   ├── Users/
    │   │   │   ├── UserManagement.jsx
    │   │   │   └── UserForm.jsx
    │   │   ├── Reports/
    │   │   │   ├── VersionComplianceReport.jsx
    │   │   │   └── ServerOverviewReport.jsx
    │   │   └── Audit/
    │   │       └── AuditLogViewer.jsx
    │   ├── services/
    │   │   ├── api.js
    │   │   ├── authService.js
    │   │   ├── serverService.js
    │   │   ├── bulkOperationService.js
    │   │   ├── userService.js
    │   │   └── auditService.js
    │   ├── context/
    │   │   └── AuthContext.jsx
    │   ├── utils/
    │   │   ├── constants.js
    │   │   └── helpers.js
    │   ├── App.jsx
    │   ├── App.css
    │   └── main.jsx
    └── package.json
```

## Database Schema

### 1. users
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    role VARCHAR(20) NOT NULL, -- 'ADMIN' or 'USER'
    is_active BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by BIGINT REFERENCES users(id),
    last_login TIMESTAMP
);
```

### 2. cognos_servers
```sql
CREATE TABLE cognos_servers (
    id BIGSERIAL PRIMARY KEY,
    server_name VARCHAR(100) UNIQUE NOT NULL,
    base_url VARCHAR(255) NOT NULL,
    api_key_encrypted TEXT NOT NULL,
    pronto_debtor_code VARCHAR(50) NOT NULL,
    pronto_xi_version VARCHAR(20) NOT NULL,
    enrollment_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    enrolled_by BIGINT REFERENCES users(id),
    last_poll_time TIMESTAMP,
    poll_status VARCHAR(20), -- 'SUCCESS', 'FAILED', 'IN_PROGRESS', 'NEVER_POLLED'
    is_active BOOLEAN DEFAULT true,
    last_error TEXT
);
```

### 3. server_metadata
```sql
CREATE TABLE server_metadata (
    id BIGSERIAL PRIMARY KEY,
    server_id BIGINT REFERENCES cognos_servers(id) ON DELETE CASCADE,
    report_count INTEGER DEFAULT 0,
    dashboard_count INTEGER DEFAULT 0,
    data_module_count INTEGER DEFAULT 0,
    captured_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 4. server_content_inventory
```sql
CREATE TABLE server_content_inventory (
    id BIGSERIAL PRIMARY KEY,
    server_id BIGINT REFERENCES cognos_servers(id) ON DELETE CASCADE,
    content_type VARCHAR(50) NOT NULL, -- 'REPORT', 'PACKAGE', 'DASHBOARD', 'DATA_MODULE'
    content_name VARCHAR(255) NOT NULL,
    content_version VARCHAR(50),
    content_path TEXT NOT NULL,
    last_updated TIMESTAMP,
    discovered_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_content_server ON server_content_inventory(server_id);
CREATE INDEX idx_content_type ON server_content_inventory(content_type);
```

### 5. bulk_operations
```sql
CREATE TABLE bulk_operations (
    id BIGSERIAL PRIMARY KEY,
    operation_type VARCHAR(50) NOT NULL, -- 'DEPLOY_REPORT', 'DEPLOY_PACKAGE', etc.
    operation_name VARCHAR(255),
    target_servers BIGINT[] NOT NULL,
    content_path VARCHAR(255),
    status VARCHAR(20) NOT NULL, -- 'PENDING', 'IN_PROGRESS', 'COMPLETED', 'FAILED'
    initiated_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    started_at TIMESTAMP,
    completed_at TIMESTAMP,
    success_count INTEGER DEFAULT 0,
    failure_count INTEGER DEFAULT 0,
    error_log TEXT
);
```

### 6. bulk_operation_details
```sql
CREATE TABLE bulk_operation_details (
    id BIGSERIAL PRIMARY KEY,
    bulk_operation_id BIGINT REFERENCES bulk_operations(id) ON DELETE CASCADE,
    server_id BIGINT REFERENCES cognos_servers(id),
    status VARCHAR(20) NOT NULL,
    error_message TEXT,
    executed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 7. change_history
```sql
CREATE TABLE change_history (
    id BIGSERIAL PRIMARY KEY,
    server_id BIGINT REFERENCES cognos_servers(id) ON DELETE CASCADE,
    change_type VARCHAR(50) NOT NULL, -- 'CONTENT_DEPLOYED', 'CONFIG_UPDATED', etc.
    change_details JSONB NOT NULL,
    changed_by BIGINT REFERENCES users(id),
    changed_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    initiated_from VARCHAR(20) DEFAULT 'PORTAL' -- 'PORTAL', 'API', 'SCHEDULED'
);

CREATE INDEX idx_change_server ON change_history(server_id);
CREATE INDEX idx_change_date ON change_history(changed_at);
```

### 8. audit_log
```sql
CREATE TABLE audit_log (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    action VARCHAR(100) NOT NULL,
    target_entity VARCHAR(50), -- 'SERVER', 'USER', 'BULK_OPERATION', etc.
    entity_id BIGINT,
    ip_address VARCHAR(45),
    timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    details JSONB,
    result VARCHAR(20) NOT NULL -- 'SUCCESS', 'FAILURE'
);

CREATE INDEX idx_audit_user ON audit_log(user_id);
CREATE INDEX idx_audit_timestamp ON audit_log(timestamp);
CREATE INDEX idx_audit_action ON audit_log(action);
```

## API Endpoints

### Authentication (`/api/auth`)

#### POST /api/auth/login
```json
Request:
{
  "username": "admin",
  "password": "password123"
}

Response:
{
  "token": "jwt-token-here",
  "user": {
    "id": 1,
    "username": "admin",
    "email": "admin@pronto.com",
    "role": "ADMIN"
  }
}
```

#### POST /api/auth/logout
```json
Response:
{
  "message": "Logged out successfully"
}
```

#### GET /api/auth/current-user
```json
Response:
{
  "id": 1,
  "username": "admin",
  "email": "admin@pronto.com",
  "role": "ADMIN"
}
```

### User Management (`/api/users`)

#### GET /api/users
- **Auth Required**: Admin only
- **Returns**: List of all users

#### POST /api/users
- **Auth Required**: Admin only
```json
Request:
{
  "username": "newuser",
  "email": "user@pronto.com",
  "password": "initialPassword",
  "role": "USER"
}
```

#### PUT /api/users/{id}
- **Auth Required**: Admin or self
- **Updates**: User details (not password)

#### DELETE /api/users/{id}
- **Auth Required**: Admin only
- **Action**: Soft delete (set is_active = false)

### Server Management (`/api/servers`)

#### GET /api/servers
- **Query params**: ?status=active&debtor_code=DC001
- **Returns**: List of enrolled servers with metadata

#### POST /api/servers
- **Auth Required**: Admin
```json
Request:
{
  "serverName": "Production Server 1",
  "baseUrl": "https://cognos.client1.com",
  "apiKey": "raw-api-key",
  "prontoDebtorCode": "DC001",
  "prontoXiVersion": "11.2.5"
}
```

#### GET /api/servers/{id}
- **Returns**: Full server details including latest metadata

#### PUT /api/servers/{id}
- **Auth Required**: Admin
- **Updates**: Server configuration

#### DELETE /api/servers/{id}
- **Auth Required**: Admin
- **Action**: Soft delete or hard delete (configurable)

#### POST /api/servers/{id}/poll
- **Returns**: Polling job ID
- **Action**: Triggers immediate poll of server

#### POST /api/servers/poll-all
- **Returns**: Batch job ID
- **Action**: Polls all active servers

### Content Management (`/api/content`)

#### GET /api/servers/{id}/content
- **Query params**: ?type=REPORT&version=1.2
- **Returns**: Content inventory for server

#### GET /api/servers/{id}/versions
- **Returns**: Version summary grouped by content type

### Bulk Operations (`/api/bulk-operations`)

#### POST /api/bulk-operations/deploy
```json
Request:
{
  "operationType": "DEPLOY_REPORT",
  "operationName": "Deploy Q4 Standard Reports",
  "targetServers": [1, 2, 3, 5, 8],
  "contentPath": "/Standard Reports/Q4",
  "contentFile": "base64-encoded-content"
}

Response:
{
  "operationId": 42,
  "status": "PENDING",
  "targetCount": 5
}
```

#### GET /api/bulk-operations
- **Query params**: ?status=COMPLETED&userId=3
- **Returns**: List of bulk operations

#### GET /api/bulk-operations/{id}
- **Returns**: Operation details with per-server status

#### GET /api/bulk-operations/{id}/status
- **Returns**: Real-time progress update

### Reporting (`/api/reports`)

#### GET /api/reports/server-overview
```json
Response:
{
  "totalServers": 400,
  "activeServers": 385,
  "lastPollSuccess": 380,
  "lastPollFailed": 5,
  "versionDistribution": {
    "11.2.5": 250,
    "11.2.4": 100,
    "11.2.3": 35
  }
}
```

#### GET /api/reports/version-compliance
- **Query params**: ?contentType=REPORT&targetVersion=1.5
- **Returns**: Servers with version drift

#### GET /api/servers/{id}/change-history
- **Query params**: ?startDate=2024-01-01&endDate=2024-12-31
- **Returns**: Audit trail of changes for server

### Audit (`/api/audit`)

#### GET /api/audit-log
- **Query params**: ?userId=3&action=SERVER_ENROLLED&startDate=2024-01-01
- **Returns**: Filtered audit log entries

#### GET /api/audit-log/export
- **Query params**: Same as above
- **Returns**: CSV file download

## Core Features & Implementation Details

### 1. Server Enrollment

**Backend Implementation**:
- Create `ServerController.enrollServer()` method
- Validate server connectivity by making test API call to Cognos
- Encrypt API key using AES-256 before storing
- Log enrollment action to audit_log
- Store initial metadata with status 'NEVER_POLLED'

**Frontend Implementation**:
- Multi-step form using Ant Design Steps component
- Fields: Server Name, Base URL, API Key, Pronto Debtor Code, Xi Version
- Test connection button before final submission
- Success notification with link to server detail page

### 2. Polling Mechanism

**Backend Implementation**:

Create `PollingService` with:

```java
@Scheduled(cron = "0 0 6,18 * * *") // 6 AM and 6 PM daily
public void scheduledPollAllServers() {
    List<CognosServer> servers = serverRepository.findByIsActiveTrue();
    servers.forEach(this::pollServer);
}

@Async
public void pollServer(CognosServer server) {
    try {
        // Update status to IN_PROGRESS
        server.setPollStatus("IN_PROGRESS");
        serverRepository.save(server);
        
        // Call Cognos API to get content
        CognosApiResponse response = cognosApiService.getContentInventory(server);
        
        // Update server_metadata
        ServerMetadata metadata = new ServerMetadata();
        metadata.setReportCount(response.getReportCount());
        metadata.setDashboardCount(response.getDashboardCount());
        metadata.setDataModuleCount(response.getDataModuleCount());
        metadataRepository.save(metadata);
        
        // Update content_inventory
        updateContentInventory(server, response.getContentList());
        
        // Update server status
        server.setPollStatus("SUCCESS");
        server.setLastPollTime(LocalDateTime.now());
        serverRepository.save(server);
        
        // Log to change_history
        logPollingEvent(server, "SUCCESS");
        
    } catch (Exception e) {
        server.setPollStatus("FAILED");
        server.setLastError(e.getMessage());
        serverRepository.save(server);
        logPollingEvent(server, "FAILED");
    }
}
```

**Frontend Implementation**:
- Display last poll time and status badge on server list
- "Poll Now" button on server detail page
- "Poll All Servers" button on dashboard
- Progress modal showing polling status for bulk polls

### 3. Bulk Operations

**Backend Implementation**:

```java
@Async
public void executeBulkDeploy(BulkDeployRequest request, User initiatedBy) {
    BulkOperation operation = createBulkOperation(request, initiatedBy);
    
    for (Long serverId : request.getTargetServers()) {
        try {
            CognosServer server = serverRepository.findById(serverId).orElseThrow();
            
            // Deploy content via Cognos API
            cognosApiService.deployContent(
                server, 
                request.getContentFile(), 
                request.getContentPath()
            );
            
            // Record success
            recordOperationDetail(operation, server, "SUCCESS", null);
            operation.incrementSuccessCount();
            
            // Log to change_history
            logContentDeployment(server, request, initiatedBy);
            
        } catch (Exception e) {
            recordOperationDetail(operation, server, "FAILED", e.getMessage());
            operation.incrementFailureCount();
        }
    }
    
    operation.setStatus("COMPLETED");
    operation.setCompletedAt(LocalDateTime.now());
    bulkOperationRepository.save(operation);
    
    // Audit log
    auditService.log(initiatedBy, "BULK_DEPLOY_COMPLETED", operation);
}
```

**Frontend Implementation**:
- Multi-step wizard (Ant Design Steps):
  1. Select target servers (filterable table with checkboxes)
  2. Upload content file (Ant Design Upload component)
  3. Configure deployment (destination path input)
  4. Review and confirm
- Real-time progress tracking during execution
- Results summary table showing per-server status

### 4. Audit Logging

**Implementation**: Aspect-Oriented Programming (AOP)

```java
@Aspect
@Component
public class AuditAspect {
    
    @Autowired
    private AuditService auditService;
    
    @Around("@annotation(auditable)")
    public Object auditMethod(ProceedingJoinPoint joinPoint, Auditable auditable) {
        User currentUser = SecurityContextHolder.getCurrentUser();
        String action = auditable.action();
        
        try {
            Object result = joinPoint.proceed();
            auditService.log(currentUser, action, "SUCCESS", extractDetails(joinPoint));
            return result;
        } catch (Exception e) {
            auditService.log(currentUser, action, "FAILURE", e.getMessage());
            throw e;
        }
    }
}

// Usage:
@Auditable(action = "SERVER_ENROLLED")
public CognosServer enrollServer(ServerEnrollmentRequest request) {
    // enrollment logic
}
```

**Frontend Implementation**:
- Audit log viewer with advanced filtering (Ant Design Table)
- Date range picker
- Multi-select filters for action type, user, result
- Export to CSV button
- Expandable rows showing full JSON details

### 5. Authentication & Authorization

**Backend Implementation**:

Use JWT tokens with Spring Security:

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeRequests()
                .antMatchers("/api/auth/**").permitAll()
                .antMatchers("/api/users/**").hasRole("ADMIN")
                .antMatchers("/api/servers/**").authenticated()
                .anyRequest().authenticated()
            .and()
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        
        return http.build();
    }
}
```

**Frontend Implementation**:
- Store JWT in localStorage or httpOnly cookie
- Axios interceptor to add Authorization header
- AuthContext for managing auth state
- Protected routes checking user role
- Automatic logout on token expiration

### 6. Cognos Analytics API Integration

**Service Implementation**:

```java
@Service
public class CognosApiService {
    
    private final RestTemplate restTemplate;
    private final EncryptionService encryptionService;
    
    public CognosContentResponse getContentInventory(CognosServer server) {
        String apiKey = encryptionService.decrypt(server.getApiKeyEncrypted());
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        String url = server.getBaseUrl() + "/api/v1/content";
        
        HttpEntity<?> entity = new HttpEntity<>(headers);
        ResponseEntity<CognosContentResponse> response = restTemplate.exchange(
            url, 
            HttpMethod.GET, 
            entity, 
            CognosContentResponse.class
        );
        
        return response.getBody();
    }
    
    public void deployContent(CognosServer server, byte[] content, String path) {
        // Implementation for content deployment
        // Reference: https://www.ibm.com/docs/en/cognos-analytics/12.0.x?topic=api-rest-reference
    }
}
```

**Note**: You will need to consult the IBM Cognos Analytics 12.0 REST API documentation for exact endpoint paths, authentication methods, and request/response formats. The documentation is available at: https://www.ibm.com/docs/en/cognos-analytics/12.0.x?topic=api-rest-reference

### 7. Encryption Service

```java
@Service
public class EncryptionService {
    
    @Value("${encryption.secret}")
    private String encryptionKey;
    
    public String encrypt(String plainText) {
        // Use AES-256 encryption
        // Store initialization vector with encrypted data
    }
    
    public String decrypt(String encryptedText) {
        // Decrypt using stored IV and secret key
    }
}
```

**Configuration**: Store encryption key in environment variable, not in code.

## Frontend Components Detail

### Dashboard Overview

```jsx
// DashboardOverview.jsx
import { Row, Col, Card, Statistic, Badge, Table } from 'antd';
import { ServerOutlined, CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons';

const DashboardOverview = () => {
  const [stats, setStats] = useState({});
  
  useEffect(() => {
    serverService.getOverviewStats().then(setStats);
  }, []);
  
  return (
    <div>
      <Row gutter={16}>
        <Col span={6}>
          <Card>
            <Statistic
              title="Total Servers"
              value={stats.totalServers}
              prefix={<ServerOutlined />}
            />
          </Card>
        </Col>
        <Col span={6}>
          <Card>
            <Statistic
              title="Active Servers"
              value={stats.activeServers}
              valueStyle={{ color: '#3f8600' }}
              prefix={<CheckCircleOutlined />}
            />
          </Card>
        </Col>
        {/* Additional stat cards */}
      </Row>
      
      {/* Recent operations table */}
      {/* Version distribution chart */}
    </div>
  );
};
```

### Server List Component

```jsx
// ServerList.jsx
import { Table, Button, Space, Tag, Input } from 'antd';
import { SyncOutlined, EyeOutlined } from '@ant-design/icons';

const ServerList = () => {
  const [servers, setServers] = useState([]);
  const [loading, setLoading] = useState(false);
  
  const columns = [
    {
      title: 'Server Name',
      dataIndex: 'serverName',
      key: 'serverName',
      sorter: true,
      filterable: true
    },
    {
      title: 'Debtor Code',
      dataIndex: 'prontoDebtorCode',
      key: 'prontoDebtorCode'
    },
    {
      title: 'Xi Version',
      dataIndex: 'prontoXiVersion',
      key: 'prontoXiVersion'
    },
    {
      title: 'Last Poll',
      dataIndex: 'lastPollTime',
      key: 'lastPollTime',
      render: (time) => time ? moment(time).fromNow() : 'Never'
    },
    {
      title: 'Status',
      dataIndex: 'pollStatus',
      key: 'pollStatus',
      render: (status) => {
        const colors = {
          SUCCESS: 'green',
          FAILED: 'red',
          IN_PROGRESS: 'blue',
          NEVER_POLLED: 'default'
        };
        return <Tag color={colors[status]}>{status}</Tag>;
      }
    },
    {
      title: 'Actions',
      key: 'actions',
      render: (_, record) => (
        <Space>
          <Button 
            icon={<SyncOutlined />} 
            onClick={() => handlePoll(record.id)}
          >
            Poll
          </Button>
          <Button 
            icon={<EyeOutlined />} 
            onClick={() => navigate(`/servers/${record.id}`)}
          >
            Details
          </Button>
        </Space>
      )
    }
  ];
  
  return (
    <div>
      <Space style={{ marginBottom: 16 }}>
        <Input.Search 
          placeholder="Search servers..." 
          onSearch={handleSearch}
        />
        <Button type="primary" onClick={() => navigate('/servers/enroll')}>
          Enroll New Server
        </Button>
      </Space>
      
      <Table 
        columns={columns}
        dataSource={servers}
        loading={loading}
        rowKey="id"
      />
    </div>
  );
};
```

## Security Considerations

### 1. API Key Storage
- **Encrypt at rest**: Use AES-256 encryption with a strong master key
- **Master key management**: Store in environment variable, never in code
- **Key rotation**: Implement ability to re-encrypt all API keys with new master key

### 2. Authentication
- **Password policy**: Minimum 12 characters, complexity requirements
- **Password hashing**: Use BCrypt with work factor of 12+
- **JWT tokens**: Short expiration (1 hour), refresh token mechanism
- **Session management**: Invalidate tokens on logout

### 3. Authorization
- **Role-based access**: Strict enforcement at API level
- **Principle of least privilege**: Users only see what they need
- **Admin actions**: Require re-authentication for sensitive operations

### 4. API Security
- **CORS**: Whitelist only Pronto Cloud domains
- **Rate limiting**: Prevent abuse (e.g., 100 requests/minute per user)
- **Input validation**: Sanitize all inputs to prevent injection attacks
- **HTTPS only**: Enforce TLS 1.2+ for all communications

### 5. Audit Compliance
- **Comprehensive logging**: Every action must be auditable
- **Immutable audit log**: Audit entries cannot be modified or deleted
- **Retention policy**: Define how long to keep audit logs
- **Regular review**: Automated alerts for suspicious activity

## Testing Strategy

### Backend Testing

**Unit Tests**:
- Service layer logic (mocking repositories)
- Encryption/decryption methods
- Business logic validation

**Integration Tests**:
- API endpoint testing with MockMvc
- Database operations with test containers
- Spring Security configuration

**Example**:
```java
@SpringBootTest
@AutoConfigureMockMvc
class ServerControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void testEnrollServer() throws Exception {
        mockMvc.perform(post("/api/servers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(enrollmentRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.serverName").value("Test Server"));
    }
}
```

### Frontend Testing

**Unit Tests** (Jest + React Testing Library):
- Component rendering
- User interactions
- State management

**Integration Tests**:
- API service calls (mocked with MSW)
- Form submissions
- Navigation flows

**Example**:
```jsx
test('renders server list and allows filtering', async () => {
  render(<ServerList />);
  
  expect(screen.getByText('Loading...')).toBeInTheDocument();
  
  await waitFor(() => {
    expect(screen.getByText('Production Server 1')).toBeInTheDocument();
  });
  
  const searchInput = screen.getByPlaceholderText('Search servers...');
  fireEvent.change(searchInput, { target: { value: 'Production' } });
  
  expect(screen.getByText('Production Server 1')).toBeInTheDocument();
  expect(screen.queryByText('Development Server 1')).not.toBeInTheDocument();
});
```

## Configuration Files

### Backend Configuration

**application.yml**:
```yaml
spring:
  application:
    name: cognos-portal
  datasource:
    url: jdbc:postgresql://localhost:5432/cognos_portal
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}
    driver-class-name: org.postgresql.Driver
  jpa:
    hibernate:
      ddl-auto: validate
    show-sql: false
    properties:
      hibernate:
        dialect: org.hibernate.dialect.PostgreSQLDialect
        format_sql: true
  flyway:
    enabled: true
    baseline-on-migrate: true

server:
  port: 8080
  servlet:
    context-path: /api

jwt:
  secret: ${JWT_SECRET}
  expiration: 3600000 # 1 hour in milliseconds

encryption:
  secret: ${ENCRYPTION_KEY}

polling:
  schedule:
    cron: "0 0 6,18 * * *" # 6 AM and 6 PM daily

logging:
  level:
    com.pronto.cognosportal: INFO
    org.springframework.security: DEBUG
```

**application-dev.yml**:
```yaml
spring:
  jpa:
    show-sql: true
  devtools:
    restart:
      enabled: true

logging:
  level:
    com.pronto.cognosportal: DEBUG
```

**application-prod.yml**:
```yaml
spring:
  jpa:
    show-sql: false

logging:
  level:
    com.pronto.cognosportal: INFO
    org.springframework.web: WARN

server:
  error:
    include-message: never
    include-stacktrace: never
```

**pom.xml** (key dependencies):
```xml
<dependencies>
    <!-- Spring Boot -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
    
    <!-- Database -->
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
    </dependency>
    <dependency>
        <groupId>org.flywaydb</groupId>
        <artifactId>flyway-core</artifactId>
    </dependency>
    
    <!-- JWT -->
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-api</artifactId>
        <version>0.11.5</version>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-impl</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>io.jsonwebtoken</groupId>
        <artifactId>jjwt-jackson</artifactId>
        <version>0.11.5</version>
        <scope>runtime</scope>
    </dependency>
    
    <!-- Lombok -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <optional>true</optional>
    </dependency>
    
    <!-- Testing -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-test</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

### Frontend Configuration

**package.json**:
```json
{
  "name": "cognos-portal-frontend",
  "version": "1.0.0",
  "private": true,
  "dependencies": {
    "react": "^18.2.0",
    "react-dom": "^18.2.0",
    "react-router-dom": "^6.20.0",
    "antd": "^5.12.0",
    "@ant-design/icons": "^5.2.6",
    "axios": "^1.6.2",
    "moment": "^2.29.4",
    "recharts": "^2.10.3"
  },
  "devDependencies": {
    "@vitejs/plugin-react": "^4.2.1",
    "vite": "^5.0.8",
    "@testing-library/react": "^14.1.2",
    "@testing-library/jest-dom": "^6.1.5",
    "vitest": "^1.0.4"
  },
  "scripts": {
    "dev": "vite",
    "build": "vite build",
    "preview": "vite preview",
    "test": "vitest"
  }
}
```

**vite.config.js**:
```javascript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 3000,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
});
```

**.env.development**:
```
VITE_API_BASE_URL=http://localhost:8080/api
```

**.env.production**:
```
VITE_API_BASE_URL=/api
```

## Deployment Guide

### Database Setup

1. **Create PostgreSQL Database**:
```sql
CREATE DATABASE cognos_portal;
CREATE USER cognos_admin WITH ENCRYPTED PASSWORD 'secure_password';
GRANT ALL PRIVILEGES ON DATABASE cognos_portal TO cognos_admin;
```

2. **Run Flyway Migrations**:
Migrations will run automatically on application startup.

### Backend Deployment

1. **Build JAR**:
```bash
cd backend
./mvnw clean package -DskipTests
```

2. **Set Environment Variables**:
```bash
export DB_USERNAME=cognos_admin
export DB_PASSWORD=secure_password
export JWT_SECRET=your-256-bit-secret-key
export ENCRYPTION_KEY=your-aes-256-encryption-key
export SPRING_PROFILES_ACTIVE=prod
```

3. **Run Application**:
```bash
java -jar target/cognos-portal-0.0.1-SNAPSHOT.jar
```

4. **Create Systemd Service** (optional, for Linux):
```ini
[Unit]
Description=Cognos Portal Backend
After=network.target postgresql.service

[Service]
Type=simple
User=cognos
WorkingDirectory=/opt/cognos-portal
ExecStart=/usr/bin/java -jar /opt/cognos-portal/cognos-portal.jar
Restart=on-failure
Environment="DB_USERNAME=cognos_admin"
Environment="DB_PASSWORD=secure_password"
Environment="JWT_SECRET=your-secret"
Environment="ENCRYPTION_KEY=your-key"
Environment="SPRING_PROFILES_ACTIVE=prod"

[Install]
WantedBy=multi-user.target
```

### Frontend Deployment

1. **Build Production Bundle**:
```bash
cd frontend
npm install
npm run build
```

2. **Serve with Nginx**:
```nginx
server {
    listen 80;
    server_name cognos-portal.pronto.com;
    
    root /var/www/cognos-portal;
    index index.html;
    
    location / {
        try_files $uri $uri/ /index.html;
    }
    
    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

3. **Enable HTTPS** (recommended):
```bash
certbot --nginx -d cognos-portal.pronto.com
```

## Initial Setup & Seeding

### Create Default Admin User

Run this SQL after first deployment:
```sql
INSERT INTO users (username, password_hash, email, role, is_active, created_at)
VALUES (
    'admin',
    '$2a$12$LQv3c1yqBWVHxkd0LHAkCOYz6TtxMQJqhN8/LewY5GyYKxR9z9aO2', -- 'admin123'
    'admin@pronto.com',
    'ADMIN',
    true,
    CURRENT_TIMESTAMP
);
```

**Important**: Change this password immediately after first login!

### First Login

1. Navigate to: `https://cognos-portal.pronto.com`
2. Login with username: `admin`, password: `admin123`
3. Go to Settings → Change Password
4. Create additional admin users as needed

## Monitoring & Maintenance

### Health Checks

**Backend Health Endpoint**:
```java
@RestController
@RequestMapping("/actuator")
public class HealthController {
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now().toString()
        ));
    }
}
```

### Logging

**Backend Logging**:
- Application logs: `/var/log/cognos-portal/application.log`
- Audit logs: Stored in database, exportable via UI
- Error tracking: Consider integrating Sentry or similar

**Frontend Logging**:
- Console errors in development
- Send critical errors to backend logging endpoint in production

### Database Maintenance

**Regular Tasks**:
```sql
-- Vacuum and analyze (run weekly)
VACUUM ANALYZE;

-- Reindex (run monthly)
REINDEX DATABASE cognos_portal;

-- Archive old audit logs (run quarterly)
-- Move records older than 2 years to archive table
```

### Backup Strategy

**Database Backup** (daily):
```bash
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
pg_dump -U cognos_admin cognos_portal > /backups/cognos_portal_$DATE.sql
# Retain last 30 days of backups
find /backups -name "cognos_portal_*.sql" -mtime +30 -delete
```

## Performance Optimization

### Database Indexes

Already included in schema, but verify:
```sql
-- Check existing indexes
SELECT tablename, indexname, indexdef 
FROM pg_indexes 
WHERE schemaname = 'public';

-- Add additional indexes as needed based on query patterns
CREATE INDEX idx_server_active_poll ON cognos_servers(is_active, last_poll_time);
CREATE INDEX idx_content_server_type ON server_content_inventory(server_id, content_type);
```

### Caching Strategy

**Backend Caching**:
```java
@Configuration
@EnableCaching
public class CacheConfig {
    
    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(
            "servers",
            "serverMetadata",
            "users"
        );
    }
}

// Usage:
@Cacheable(value = "servers", key = "#id")
public CognosServer getServerById(Long id) {
    return serverRepository.findById(id).orElseThrow();
}

@CacheEvict(value = "servers", key = "#server.id")
public void updateServer(CognosServer server) {
    serverRepository.save(server);
}
```

**Frontend Caching**:
- Use React Query or SWR for data fetching with automatic caching
- Cache server list data for 5 minutes
- Invalidate cache after mutations

### Connection Pooling

**HikariCP Configuration** (already included in Spring Boot):
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 30000
      idle-timeout: 600000
      max-lifetime: 1800000
```

## Troubleshooting Guide

### Common Issues

**Issue 1**: Polling fails for all servers
- **Check**: Network connectivity to Cognos servers
- **Check**: API keys are valid and not expired
- **Check**: Cognos servers are online and responding
- **Solution**: Review error logs in `audit_log` and `cognos_servers.last_error`

**Issue 2**: Bulk operations timeout
- **Check**: Operation complexity and target server count
- **Check**: Network latency to target servers
- **Solution**: Increase async thread pool size or break into smaller batches

**Issue 3**: Login fails
- **Check**: JWT secret is properly set in environment
- **Check**: User account is active (`is_active = true`)
- **Check**: Password hash matches
- **Solution**: Reset password via SQL or create new admin user

**Issue 4**: Frontend cannot reach backend
- **Check**: CORS configuration in backend
- **Check**: Proxy configuration in Nginx
- **Check**: Firewall rules
- **Solution**: Verify API base URL and network connectivity

### Debug Mode

Enable debug logging temporarily:
```yaml
logging:
  level:
    com.pronto.cognosportal: DEBUG
    org.springframework.security: DEBUG
    org.springframework.web: DEBUG
```

## Future Enhancements

### Phase 2 Features (Post-MVP)
1. **Scheduled Bulk Operations**: Schedule deployments for off-peak hours
2. **Content Comparison**: Compare content versions across servers
3. **Automated Rollbacks**: Rollback failed deployments automatically
4. **Email Notifications**: Alert admins of polling failures or bulk operation completion
5. **API Rate Limiting**: More sophisticated rate limiting per user/role
6. **Multi-factor Authentication**: Add 2FA for admin accounts
7. **Server Groups**: Organize servers into groups (by client, region, etc.)
8. **Advanced Reporting**: Custom report builder for ad-hoc analysis
9. **Webhook Integration**: Notify external systems of events
10. **Mobile App**: iOS/Android app for monitoring on the go

### Scalability Considerations

For scaling beyond 1000 servers:
- **Message Queue**: Use RabbitMQ or Kafka for async operations
- **Distributed Caching**: Implement Redis for shared cache
- **Load Balancing**: Deploy multiple backend instances behind load balancer
- **Database Read Replicas**: Separate read/write operations
- **Microservices**: Split polling, bulk operations into separate services

## Support & Documentation

### User Documentation

Create user guides for:
1. **Getting Started**: Login, navigation, basic concepts
2. **Server Management**: Enrolling, polling, viewing details
3. **Bulk Operations**: Step-by-step guide with screenshots
4. **Reporting**: How to use reports and export data
5. **User Management**: Admin guide for creating users

### API Documentation

Use Swagger/OpenAPI:
```java
@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Cognos Portal API")
                .version("1.0")
                .description("API for managing Cognos Analytics servers"));
    }
}
```

Access at: `http://localhost:8080/swagger-ui.html`

### Training Materials

Prepare:
- Video tutorials for common tasks
- FAQ document
- Quick reference card
- Admin troubleshooting guide

## Security Checklist

Before going to production:

- [ ] Change all default passwords
- [ ] Set strong JWT secret (256-bit minimum)
- [ ] Set strong encryption key for API keys
- [ ] Enable HTTPS with valid SSL certificate
- [ ] Configure firewall to restrict database access
- [ ] Enable database connection encryption
- [ ] Review and restrict CORS allowed origins
- [ ] Set up regular security updates schedule
- [ ] Enable SQL injection prevention (parameterized queries)
- [ ] Implement XSS prevention (input sanitization)
- [ ] Set secure session cookies (httpOnly, secure, sameSite)
- [ ] Configure rate limiting on all endpoints
- [ ] Set up intrusion detection monitoring
- [ ] Document incident response procedures
- [ ] Conduct security audit/penetration testing

## Development Workflow

### Git Branching Strategy

- `main`: Production-ready code
- `develop`: Integration branch
- `feature/*`: New features
- `bugfix/*`: Bug fixes
- `hotfix/*`: Production hotfixes

### Code Review Checklist

- [ ] Code follows project style guidelines
- [ ] All tests pass
- [ ] No hardcoded credentials or secrets
- [ ] Proper error handling implemented
- [ ] Audit logging in place for state changes
- [ ] API endpoints are properly secured
- [ ] Database queries are optimized
- [ ] Documentation updated

### CI/CD Pipeline (Recommended)

```yaml
# .github/workflows/ci.yml
name: CI

on: [push, pull_request]

jobs:
  backend-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
      - name: Run tests
        run: cd backend && ./mvnw test
  
  frontend-test:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Setup Node
        uses: actions/setup-node@v2
        with:
          node-version: '18'
      - name: Install and test
        run: cd frontend && npm install && npm test
```

## Success Metrics

Track these KPIs:
- **Server enrollment rate**: Target 400 servers within 3 months
- **Polling success rate**: > 95% successful polls
- **Bulk operation success rate**: > 90% successful deployments
- **User adoption**: All 10 users actively using within 1 month
- **Average response time**: < 2 seconds for dashboard load
- **System uptime**: > 99.5%

## Conclusion

This specification provides a comprehensive blueprint for building the Cognos Analytics Enterprise Management Portal. The solution addresses all stated requirements including security, audit logging, bulk operations, and scalability.

**Next Steps**:
1. Review this specification with stakeholders
2. Set up development environment
3. Begin with backend authentication and database schema
4. Build core server enrollment functionality
5. Implement polling mechanism
6. Develop frontend progressively
7. Conduct thorough testing
8. Deploy to Pronto Cloud
9. Train initial users
10. Iterate based on feedback

**Estimated Timeline**:
- Backend core: 3-4 weeks
- Frontend core: 3-4 weeks
- Integration & testing: 2-3 weeks
- Deployment & training: 1 week
- **Total**: 9-12 weeks for MVP

Good luck with your development! This portal will significantly streamline the management of your Cognos Analytics infrastructure.
