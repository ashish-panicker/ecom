# Auth Service – JWT Based Identity & Authorization Server

## JWT

JSON Web Token compact, URL safe token format used to securely transmit information between services as a JSON object.
This token is digitally signed

- trust the contents
- cannot be modified without detection
- is commonly used for authorization
  JWT is widely used:
- microservices
- REST APIs
- OAuth2
- Spring security

## JWT Structure

JWT has three section each separated by `.`

```json
XXXXXXX.YYYYYYYYY.ZZZZZZZZZZZZZZ
```

The three parts are

1. Header
2. Payload
3. Signature

### Header

Contains information about:

- The type of token (JWT,JOSE)
- The signing algorithm used (HSA,RSA)

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

### Payload

Contains the `claims`(data), claims are pieces of information asserted about a subject.

Types of claims:
- public
- private
- registered

```json
{
  "sub": "ashish",
  "role": "ADMIN",
  "iss": "emudra",
  "iat": 175861541,
  "exp": 175863541
}
```
Registered Claims
- `iss` (issuer): Issuer of the JWT
- `sub` (subject): Subject of the JWT (the user)
- `aud` (audience): Recipient for which the JWT is intended
- `exp` (expiration time): Time after which the JWT expires
- `nbf` (not before time): Time before which the JWT must not be accepted for processing
- `iat` (issued at time): Time at which the JWT was issued; can be used to determine age of the JWT
- `jti` (JWT ID): Unique identifier; can be used to prevent the JWT from being replayed (allows a token to be used only once)

[JWT Claims](https://auth0.com/docs/secure/tokens/json-web-tokens/json-web-token-claims)

Custom Claims

[Custom Claims](https://auth0.com/docs/get-started/apis/scopes/sample-use-cases-scopes-and-claims)

```json
{
  "name": "John Doe",
  "nickname": "john.doe",
  "picture": "https://myawesomeavatar.com/avatar.png",
  "updated_at": "2017-03-30T15:13:40.474Z",
  "email": "john.doe@test.com",
  "email_verified": false,
  "iss": "https://{yourDomain}/",
  "sub": "auth0|USER-ID",
  "aud": "{yourClientId}",
  "exp": 1490922820,
  "iat": 1490886820,
  "nonce": "crypto-value",
  "at_hash": "IoS3ZGppJKUn3Bta_LgE2A"
}
```

### Signature

used to validate that the token is trustworthy and has not been tampered with. 
When you use a JWT, you must check its signature before storing and using it.

```text
HMACSHA256(
   base64UrlEncode(header) + "." +
   base64UrlEncode(payload),
   secretKey
)
```

## JWt Authentication Workflow

1. Login request
2. Authentication
  - Validate credentials
  - Create `Authentication` obeject
  - If valid then generate JWT
3. Generate the JWT and add claims
4. Client stores the JWT
5. Client will send the JWT using `Authorization` header

## Purpose

The Auth Service is a centralized authentication and authorization server responsible for:

* User registration
* Login
* JWT token issuance
* Role resolution
* Token validation
* (Next Phase) Refresh token management

This service is designed to integrate with:

* Service Discovery (Eureka)
* Product Service
* Order Service
* Upcoming API Gateway

---

# Overall Architecture

## Microservices Landscape

```
                    ┌────────────────────────┐
                    │       API Gateway      │   (Future Enhancement)
                    │   Spring Cloud Gateway │
                    └────────────┬───────────┘
                                 │
               ┌─────────────────┼─────────────────┐
               │                 │                 │
       ┌───────▼───────┐ ┌──────▼────────┐ ┌──────▼────────┐
       │ Auth Service  │ │ Product Service │ │ Order Service │
       │ JWT Issuer    │ │ Business Logic  │ │ Business Logic│
       └───────┬───────┘ └────────────────┘ └────────────────┘
               │
       ┌───────▼────────┐
       │    MariaDB     │
       │  (Users/Roles) │
       └────────────────┘

Discovery Service (Eureka) – All services register here
```

---

# Auth Flow (Login)

```
Client → POST /api/auth/login
         → Auth Service authenticates user
         → Generates JWT
         → Returns token

Client → Calls Product/Order with JWT in Authorization Header

Product/Order Service:
    → Validates JWT signature
    → Extract roles
    → Apply authorization rules
```

---

# Tech Stack

| Layer             | Technology        |
|-------------------|-------------------|
| Build             | Maven             |
| Security          | Spring Security 6 |
| Token             | JWT               |
| ORM               | JPA (Hibernate)   |
| DB                | MariaDB           |
| Migration         | Flyway            |
| Discovery         | Eureka            |
| DTO               | Java Records      |
| Password Encoding | BCrypt            |

---

## Database Schema

## users

| Column     | Type           |
|------------|----------------|
| id         | BIGINT PK      |
| username   | VARCHAR UNIQUE |
| email      | VARCHAR UNIQUE |
| password   | VARCHAR        |
| enabled    | BOOLEAN        |
| created_at | TIMESTAMP      |

---

## roles

| Column | Type           |
|--------|----------------|
| id     | BIGINT PK      |
| name   | VARCHAR UNIQUE |

Example:

* ROLE_USER
* ROLE_ADMIN
* ROLE_MANAGER

---

## user_roles

| Column  | Type |
|---------|------|
| user_id | FK   |
| role_id | FK   |

Relationship:

```
User 1 ---- * Role
```

---

## JWT Design

## Access Token

Contains:

```json
{
  "sub": "ashish",
  "roles": [
    "ROLE_ADMIN",
    "ROLE_USER"
  ],
  "iat": 1700000000,
  "exp": 1700003600
}
```

Signed using HMAC SHA-256.

---

## Why JWT Suits Microservices Better Than Sessions

| Session                       | JWT                |
|-------------------------------|--------------------|
| Requires shared memory        | Stateless          |
| Harder in distributed systems | Works naturally    |
| Needs sticky sessions         | No sticky sessions |
| Horizontal scaling complex    | Fully scalable     |
| Stored server-side            | Stored client-side |

JWT is ideal because:

* No server-side session state
* Perfect for distributed microservices
* Each service independently validates token
* Reduces DB hits
* Gateway-friendly

---

## Refresh Token Provision

We will add:

Table: refresh_tokens

| Column      | Type      |
|-------------|-----------|
| id          | BIGINT    |
| token       | VARCHAR   |
| user_id     | FK        |
| expiry_date | TIMESTAMP |

Flow:

1. Login → return access + refresh token
2. Access expires
3. Client calls /api/auth/refresh
4. New access token generated

---

# Security Design

## Security Configuration

* Stateless session policy
* Custom JWT filter
* AuthenticationProvider
* Custom UserDetailsService
* Method-level security (@PreAuthorize)

---

# Integration with Existing Services

## With Discovery Service

* Register Auth Service in Eureka
* Other services discover it

---

## With Product & Order Services

Two possible strategies:

### Strategy 1 – Shared JWT Secret

* All services share secret
* Validate token locally
* Fastest
* Most common

### Strategy 2 – Token Introspection Endpoint

* Services call Auth Service
* Auth Service validates
* More secure but slower

We will implement Strategy 1 first.

---

# Future Enhancement – API Gateway

Next step: Integrate Spring Cloud Gateway

Planned Architecture:

```
Client
   ↓
Gateway (JWT Filter)
   ↓
Downstream Services
```

Gateway Responsibilities:

* Validate JWT before forwarding
* Centralized security
* Rate limiting
* Logging
* CORS
* Header manipulation

Auth Service Responsibilities:

* Only issue tokens
* Manage identity
* Handle refresh tokens

---

# Use Cases

### User Registration

* Create user
* Assign default ROLE_USER

### Admin Creates Manager

* ROLE_ADMIN creates users with roles

### Login

* Validate credentials
* Issue JWT

### Call Product Service

* Only ROLE_USER can view products
* ROLE_ADMIN can manage products

### Call Order Service

* ROLE_USER can create orders
* ROLE_ADMIN can cancel any order

---

# Production Considerations

* Use HTTPS only
* Use strong secret key
* Rotate keys periodically
* Limit token expiration
* Implement refresh token revocation
* Add login rate limiting
* Add audit logs

---

# Service Responsibilities Summary

| Service | Responsibility          |
|---------|-------------------------|
| Auth    | Identity + Token        |
| Product | Business                |
| Order   | Business                |
| Gateway | Routing + Edge Security |

---
