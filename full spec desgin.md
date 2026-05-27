# MiniChat Backend Design Specification

## Tech Stack

* Java 21
* Spring Boot
* Spring WebSocket + STOMP
* Spring Security + JWT
* PostgreSQL
* Redis
* Flyway
* Docker
* Kafka (future extension)

---

# 1. System Overview

MiniChat is a realtime chat backend system supporting:

* Authentication
* Private chat
* Group chat
* Realtime messaging
* Message reply
* Seen status
* Typing indicator
* User presence
* User search
* Group management
* File/image attachment (future)
* Notifications (future)

---

# 2. High Level Architecture

```text
Frontend Client
       ↓
REST API + WebSocket Gateway
       ↓
Spring Boot Application
       ↓
---------------------------------
| Auth Module                  |
| User Module                  |
| Conversation Module          |
| Message Module               |
| WebSocket Module             |
---------------------------------
       ↓
PostgreSQL + Redis
       ↓
Kafka (future)
```e Eve

---

# 3. Database Design

---

# 3.1 ERD

```text
users
 ├──< conversation_members >── conversations
 │                                   │
 │                                   └──< messages
 │                                           │
 │                                           ├── sender_id -> users.id
 │                                           │
 │                                           ├── reply_to_message_id -> messages.id
 │                                           │
 │                                           └──< message_seen >── users
 │
 └──< user_sessions
```

---

# 3.2 Table Relationships

| Relationship                | Type           |
| --------------------------- | -------------- |
| users ↔ conversations       | Many-to-Many   |
| conversations ↔ messages    | One-to-Many    |
| users ↔ messages            | One-to-Many    |
| messages ↔ users (seen)     | Many-to-Many   |
| messages ↔ messages (reply) | Self Reference |
| users ↔ user_sessions       | One-to-Many    |

---

# 4. Database Schema

---

# 4.1 users

```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,

    username VARCHAR(50) UNIQUE NOT NULL,

    email VARCHAR(255) UNIQUE NOT NULL,

    password_hash TEXT NOT NULL,

    avatar_url TEXT,

    bio TEXT,

    is_online BOOLEAN DEFAULT FALSE,

    last_seen_at TIMESTAMP,

    created_at TIMESTAMP DEFAULT NOW(),

    updated_at TIMESTAMP DEFAULT NOW()
);
```

---

# 4.2 user_sessions

```sql
CREATE TABLE user_sessions (
    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL
        REFERENCES users(id),

    refresh_token TEXT NOT NULL,

    device_name VARCHAR(255),

    ip_address VARCHAR(255),

    expires_at TIMESTAMP,

    created_at TIMESTAMP DEFAULT NOW()
);
```

---

# 4.3 conversations

```sql
CREATE TABLE conversations (
    id BIGSERIAL PRIMARY KEY,

    type VARCHAR(20) NOT NULL,
    -- PRIVATE | GROUP

    name VARCHAR(255),

    avatar_url TEXT,

    created_by BIGINT
        REFERENCES users(id),

    created_at TIMESTAMP DEFAULT NOW(),

    updated_at TIMESTAMP DEFAULT NOW()
);
```

---

# 4.4 conversation_members

```sql
CREATE TABLE conversation_members (

    conversation_id BIGINT NOT NULL
        REFERENCES conversations(id),

    user_id BIGINT NOT NULL
        REFERENCES users(id),

    role VARCHAR(20) DEFAULT 'MEMBER',
    -- OWNER | ADMIN | MEMBER

    joined_at TIMESTAMP DEFAULT NOW(),

    muted_until TIMESTAMP,

    PRIMARY KEY(conversation_id, user_id)
);
```

---

# 4.5 messages

```sql
CREATE TABLE messages (

    id BIGSERIAL PRIMARY KEY,

    conversation_id BIGINT NOT NULL
        REFERENCES conversations(id),

    sender_id BIGINT NOT NULL
        REFERENCES users(id),

    reply_to_message_id BIGINT
        REFERENCES messages(id),

    content TEXT,

    message_type VARCHAR(20)
        DEFAULT 'TEXT',
    -- TEXT | IMAGE | FILE | SYSTEM

    is_edited BOOLEAN DEFAULT FALSE,

    edited_at TIMESTAMP,

    is_deleted BOOLEAN DEFAULT FALSE,

    deleted_at TIMESTAMP,

    created_at TIMESTAMP DEFAULT NOW()
);
```

---

# 4.6 message_seen

```sql
CREATE TABLE message_seen (

    message_id BIGINT NOT NULL
        REFERENCES messages(id),

    user_id BIGINT NOT NULL
        REFERENCES users(id),

    seen_at TIMESTAMP DEFAULT NOW(),

    PRIMARY KEY(message_id, user_id)
);
```

---

# 4.7 attachments

```sql
CREATE TABLE attachments (

    id BIGSERIAL PRIMARY KEY,

    message_id BIGINT NOT NULL
        REFERENCES messages(id),

    file_url TEXT NOT NULL,

    file_name VARCHAR(255),

    file_size BIGINT,

    mime_type VARCHAR(255),

    created_at TIMESTAMP DEFAULT NOW()
);
```

---

# 4.8 notifications (future)

```sql
CREATE TABLE notifications (

    id BIGSERIAL PRIMARY KEY,

    user_id BIGINT NOT NULL
        REFERENCES users(id),

    type VARCHAR(50),

    payload JSONB,

    is_read BOOLEAN DEFAULT FALSE,

    created_at TIMESTAMP DEFAULT NOW()
);
```

---

# 5. Recommended Indexes

```sql
CREATE INDEX idx_messages_conversation
ON messages(conversation_id);

CREATE INDEX idx_messages_created_at
ON messages(created_at DESC);

CREATE INDEX idx_members_user
ON conversation_members(user_id);

CREATE INDEX idx_seen_user
ON message_seen(user_id);

CREATE INDEX idx_users_username
ON users(username);
```

---

# 6. REST API Design

---

# 6.1 Authentication APIs

## Register

```http
POST /api/v1/auth/register
```

Request:

```json
{
  "username": "duc",
  "email": "duc@gmail.com",
  "password": "123456"
}
```

---

## Login

```http
POST /api/v1/auth/login
```

Response:

```json
{
  "accessToken": "jwt",
  "refreshToken": "jwt"
}
```

---

## Refresh Token

```http
POST /api/v1/auth/refresh
```

---

# 6.2 User APIs

## Search Users

```http
GET /api/v1/users/search?q=duc
```

---

## Get User Profile

```http
GET /api/v1/users/{id}
```

---

# 6.3 Conversation APIs

## Create Private Conversation

```http
POST /api/v1/conversations/private
```

Request:

```json
{
  "targetUserId": 2
}
```

---

## Create Group Conversation

```http
POST /api/v1/conversations/group
```

Request:

```json
{
  "name": "Backend Team",
  "memberIds": [2,3,4]
}
```

---

## Get My Conversations

```http
GET /api/v1/conversations
```

---

## Add Member

```http
POST /api/v1/conversations/{id}/members
```

---

## Remove Member

```http
DELETE /api/v1/conversations/{id}/members/{userId}
```

---

# 6.4 Message APIs

## Get Conversation Messages

```http
GET /api/v1/conversations/{id}/messages?page=0&size=30
```

---

## Delete Message

```http
DELETE /api/v1/messages/{id}
```

---

## Edit Message

```http
PUT /api/v1/messages/{id}
```

---

# 7. WebSocket Design

---

# 7.1 Connection Endpoint

```text
/ws/chat
```

---

# 7.2 Client Send Destinations

| Destination      | Purpose          |
| ---------------- | ---------------- |
| /app/chat.send   | Send message     |
| /app/chat.reply  | Reply message    |
| /app/chat.typing | Typing indicator |
| /app/chat.seen   | Seen message     |
| /app/chat.edit   | Edit message     |
| /app/chat.delete | Delete message   |

---

# 7.3 Client Subscribe Destinations

| Destination               | Purpose                        |
| ------------------------- | ------------------------------ |
| /topic/conversation.{id}  | Realtime conversation messages |
| /user/queue/messages      | Private events                 |
| /user/queue/notifications | Notifications                  |
| /user/queue/errors        | WebSocket errors               |

---

# 8. WebSocket Event Payloads

---

# 8.1 SEND_MESSAGE

```json
{
  "conversationId": 1,
  "content": "hello"
}
```

---

# 8.2 REPLY_MESSAGE

```json
{
  "conversationId": 1,
  "replyToMessageId": 100,
  "content": "reply content"
}
```

---

# 8.3 MESSAGE_RECEIVED

```json
{
  "id": 101,
  "conversationId": 1,
  "senderId": 2,
  "content": "hello",
  "createdAt": "timestamp"
}
```

---

# 8.4 MESSAGE_SEEN

```json
{
  "messageId": 100,
  "userId": 2
}
```

---

# 8.5 TYPING_START

```json
{
  "conversationId": 1,
  "userId": 2
}
```

---

# 8.6 TYPING_STOP

```json
{
  "conversationId": 1,
  "userId": 2
}
```

---

# 8.7 USER_ONLINE

```json
{
  "userId": 2
}
```

---

# 8.8 USER_OFFLINE

```json
{
  "userId": 2
}
```

---

# 9. Redis Design

---

# 9.1 Presence Tracking

```text
online:user:1 -> true
```

TTL optional.

---

# 9.2 Typing Status

```text
typing:conversation:1:user:2
```

---

# 9.3 Unread Count

```text
unread:conversation:1:user:2 -> 5
```

---

# 9.4 WebSocket Session Mapping

```text
ws:user:1 -> session-id
```

---

# 10. Message Lifecycle

```text
User A send message
        ↓
WebSocket endpoint receive
        ↓
Validate JWT
        ↓
Validate membership
        ↓
Save DB
        ↓
Broadcast realtime
        ↓
Update unread count
        ↓
Publish MessageSentEvent (future)
```

---

# 11. Kafka Events (Future)

---

# MessageSentEvent

```json
{
  "messageId": 100,
  "conversationId": 1,
  "senderId": 2
}
```

Consumers:

* Notification Service
* Analytics Service
* Moderation Service
* Audit Service

---

# 12. Recommended Spring Package Structure

```text
com.minichat
├── auth
├── config
├── conversation
├── message
├── notification
├── user
├── websocket
├── common
├── security
└── redis
```

---

# 13. Security Recommendations

* Use JWT authentication
* Validate WebSocket token during handshake
* Validate conversation membership before message send
* Rate limit spam users
* Escape dangerous content
* Store password with BCrypt

---

# 14. Recommended Future Improvements

* Message reactions
* Voice message
* Read receipts
* Pinned messages
* Threaded replies
* Message search
* Push notifications
* End-to-end encryption
* Multiple devices sync
* Kafka event sourcing
* Media service
* Microservices architecture

---

# 15. MVP Development Order

## Phase 1

* JWT auth
* User search
* Private messaging
* WebSocket realtime

## Phase 2

* Group chat
* Seen status
* Typing indicator
* Redis presence

## Phase 3

* Reply message
* File upload
* Message edit/delete

## Phase 4

* Kafka
* Notifications
* Analytics
* Moderation

---

# 16. Production Considerations

* Always paginate messages
* Avoid loading entire conversations
* Add indexes early
* Use Redis for ephemeral realtime state
* Keep DB as source of truth
* Use Flyway migrations
* Add observability/logging
* Handle reconnect logic
* Handle duplicate delivery safely

---

# 17. Suggested Docker Services

```yaml
services:
  app:
  postgres:
  redis:
  kafka:
  zookeeper:
```

---

# 18. Suggested Non-Functional Requirements

| Requirement              | Goal         |
| ------------------------ | ------------ |
| Message delivery latency | < 200ms      |
| Concurrent users         | 1000+        |
| Message persistence      | Guaranteed   |
| Realtime delivery        | Yes          |
| Horizontal scaling       | Future-ready |

---

# 19. Final Notes

This architecture is intentionally designed to:

* teach distributed-system thinking
* remain simple enough for a solo developer
* scale gradually
* follow production backend patterns
* work well with Spring ecosystem

Recommended progression:

```text
Spring Boot
→ WebSocket
→ Redis
→ Kafka
→ gRPC
→ Microservices
```
