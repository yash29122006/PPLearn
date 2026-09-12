# P2P Learn — Peer-to-Peer Learning Competition Platform

P2P Learn is a full-stack peer-to-peer learning competition platform where students learn collaboratively by sharing programming questions, attempting questions posted by other students, earning points, and competing on a weekly leaderboard.

The platform provides separate experiences for **Students** and **Administrators**, with secure authentication, student approval, question management, timed attempts, automated scoring, weekly competitions, leaderboards, and real-time WebSocket updates.

---

## 🚀 Live Demo

### Frontend

https://p2plearn-git-main-p2plearn.vercel.app/login

### Backend API

https://p2p-learn.onrender.com

---

# ✨ Features

## 👨‍🎓 Student Features

- Student registration
- Secure login using JWT authentication
- Passwords securely hashed using BCrypt
- Account approval system
- View current competition
- View total points
- Post one LeetCode question per day
- Cannot attempt a question posted by themselves
- Start a question attempt
- Finish a question attempt
- Automatic score calculation
- View students who attempted a question
- View points earned by each participant
- Weekly competition
- Weekly leaderboard
- Real-time updates using WebSockets
- Logout functionality

---

## 👨‍💼 Admin Features

- Secure admin login
- View pending student applications
- Accept student applications
- Reject student applications
- View active students
- Search students by username or email
- View student total points
- Remove students
- Refresh student/application data
- View leaderboard preview
- Manage the student community

---

# 🏆 Competition Rules

The platform follows a weekly competition model.

## Competition Duration

Each competition runs from:

```text
Monday → Sunday
```

The competition week is calculated using the `Asia/Kolkata` timezone.

---

## 📝 Question Posting

An accepted student can post:

```text
1 question per day
```

Each successfully posted question gives the student:

```text
+5 points
```

A student cannot attempt their own question.

A database constraint ensures that the same student cannot post more than one question per day for the same competition.

---

# ⏱️ Attempt Scoring

When a student starts a question, the attempt is recorded.

The score depends on how quickly the student finishes the question after it was posted.

## Finished Within 15 Minutes

```text
Base Points  = 15
Bonus Points = 5

Total = 20 points
```

## Finished After 15 Minutes

```text
Base Points  = 10
Bonus Points = 0

Total = 10 points
```

### Scoring Table

| Condition | Base Points | Bonus | Total |
|---|---:|---:|---:|
| Finished within 15 minutes | 15 | +5 | **20** |
| Finished after 15 minutes | 10 | +0 | **10** |

---

# 🥇 Weekly Leaderboard

At the end of the weekly competition, the system calculates the weekly scores of students.

The top three students are stored in the leaderboard.

```text
🥇 Rank 1
🥈 Rank 2
🥉 Rank 3
```

The leaderboard contains:

- Rank
- Student ID
- Username
- Weekly points
- Competition start date
- Competition end date

The leaderboard becomes visible after the competition finishes.

After the configured visibility period expires, the competition and its associated weekly data are deleted according to the competition lifecycle.

---

# 📊 Weekly Points Calculation

A student's weekly score consists of:

## Question Posting Points

Every question posted by the student:

```text
+5 points
```

## Attempt Points

Every completed attempt contributes:

```text
10 points
```

or:

```text
20 points
```

Therefore:

```text
Weekly Score
=
Question Posting Points
+
Completed Attempt Points
```

---

# ⚡ Real-Time Updates

P2P Learn uses **WebSockets with STOMP** for real-time communication.

Important events are broadcast to connected students without requiring a page refresh.

## New Question

```text
Student A posts a question
        ↓
Spring Boot processes the question
        ↓
Question saved to PostgreSQL
        ↓
WebSocket broadcasts update
        ↓
Other connected students receive the question
```

## Completed Attempt

```text
Student B finishes an attempt
        ↓
Spring Boot calculates the score
        ↓
Attempt saved to PostgreSQL
        ↓
WebSocket broadcasts update
        ↓
Connected students receive the update
```

Leaderboard updates can also be broadcast through WebSockets.

---

# 🏗️ System Architecture

```text
                    ┌──────────────────────┐
                    │      Students        │
                    │       / Admin        │
                    └──────────┬───────────┘
                               │
                               │ HTTPS
                               ▼
                    ┌──────────────────────┐
                    │   Angular Frontend   │
                    │       Vercel         │
                    └──────────┬───────────┘
                               │
                 REST API      │      WebSocket
                               │
                               ▼
                    ┌──────────────────────┐
                    │   Spring Boot API    │
                    │       Render         │
                    └──────────┬───────────┘
                               │
                         JDBC / PostgreSQL
                               │
                               ▼
                    ┌──────────────────────┐
                    │      Supabase        │
                    │     PostgreSQL       │
                    └──────────────────────┘
```

---

# 🛠️ Technology Stack

## Frontend

- Angular
- TypeScript
- HTML5
- CSS3
- Angular Reactive Forms
- Angular Router
- RxJS
- STOMP.js

## Backend

- Java 25
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- JWT
- BCrypt
- Spring WebSocket
- STOMP
- Bean Validation
- Lombok
- Maven

## Database

- PostgreSQL
- Supabase

## API Documentation

- Swagger UI
- OpenAPI

## Deployment

- Vercel — Angular frontend
- Render — Spring Boot backend
- Supabase — PostgreSQL database

---

# 📁 Project Structure

```text
P2P Learn/
│
├── learning/
│   │
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── p2plearn/
│   │   │   │           └── learning/
│   │   │   │
│   │   │   │               ├── config/
│   │   │   │               ├── controller/
│   │   │   │               ├── dto/
│   │   │   │               ├── entity/
│   │   │   │               ├── repository/
│   │   │   │               ├── response/
│   │   │   │               └── service/
│   │   │   │
│   │   │   └── resources/
│   │   │       └── application.properties
│   │   │
│   │   └── test/
│   │
│   ├── .mvn/
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── Dockerfile
│   ├── pom.xml
│   └── .gitignore
│
└── peer-learn-frontend/
    │
    ├── src/
    │   └── app/
    │       ├── guards/
    │       ├── pages/
    │       │   ├── login/
    │       │   ├── register/
    │       │   ├── admin/
    │       │   └── home/
    │       │
    │       ├── services/
    │       ├── app.config.ts
    │       ├── app.routes.ts
    │       └── app.ts
    │
    ├── public/
    ├── angular.json
    ├── package.json
    └── tsconfig.json
```

---

# 🧩 Backend Architecture

The backend follows a layered architecture:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Database
```

## Controllers

Controllers expose REST API endpoints.

Main controllers include:

```text
AuthController
QuestionController
AttemptController
AdminController
CompetitionController
LeaderboardController
```

## Services

Services contain the application's business logic.

Main services include:

```text
AuthService
QuestionService
AttemptService
CompetitionService
CompetitionLifecycleService
LeaderboardService
WebSocketBroadcastService
```

## Repositories

Spring Data JPA repositories provide database access.

Main repositories include:

```text
UserRepository
QuestionRepository
AttemptRepository
CompetitionRepository
LeaderboardRepository
CommunityApplicationRepository
```

---

# 🗄️ Database Design

The system uses PostgreSQL through Supabase.

Main tables:

```text
users
competitions
questions
attempts
leaderboard
community_applications
```

---

## Users

Stores registered users and authentication information.

Important fields:

```text
id
username
email
password_hash
role
is_active
total_points
created_at
```

Roles:

```text
ADMIN
STUDENT
```

Passwords are never stored in plaintext.

Passwords are hashed using BCrypt before being stored.

---

## Competitions

Stores weekly competition information.

Important fields:

```text
id
week_start
week_end
status
leaderboard_visible_until
```

Competition statuses include:

```text
ACTIVE
LEADERBOARD_VISIBLE
```

---

## Questions

Stores programming questions posted by students.

Important information includes:

```text
id
competition
posted_by
title
leetcode_url
posted_at
posted_date
```

A database constraint prevents the same student from posting more than one question for the same competition on the same day.

---

## Attempts

Stores student attempts.

Important information includes:

```text
id
question
student
started_at
finished_at
status
base_points
bonus_points
total_points
```

Attempt statuses include:

```text
STARTED
COMPLETED
```

---

## Leaderboard

Stores the finalized top students for a competition.

Important fields:

```text
id
competition
student
rank
total_points
```

Only the top three students are stored.

---

## Community Applications

Stores student registration/application status.

Application statuses:

```text
PENDING
ACCEPTED
REJECTED
```

A newly registered student is initially:

```text
PENDING
```

The admin can then accept or reject the application.

---

# 🔐 Authentication and Security

P2P Learn uses Spring Security with JWT-based authentication.

## Registration

A student registers using:

```text
Username
Email
Password
```

The password is hashed using BCrypt.

The newly registered account is initially inactive and a community application is created.

```text
Register
   ↓
Application Created
   ↓
PENDING
   ↓
Admin Decision
   ↓
ACCEPTED / REJECTED
```

---

## Login

After successful authentication, the backend generates a JWT.

The Angular application stores the token and sends it with protected API requests.

```http
Authorization: Bearer <JWT>
```

---

## Role-Based Access Control

The application separates access based on roles.

### Student

Students can access:

```text
/home
/api/questions
/api/attempts
/api/leaderboard
```

### Admin

Admins can access:

```text
/admin
/api/admin/*
```

Protected frontend routes are additionally handled by Angular route guards.

---

# 🛡️ Angular Route Protection

The frontend uses two route guards.

## Authentication Guard

Prevents unauthenticated users from accessing protected pages.

```text
Not logged in
     ↓
  /login
```

## Guest Guard

Prevents already authenticated users from unnecessarily opening login/register pages.

```text
Logged-in ADMIN
      ↓
   /admin

Logged-in STUDENT
      ↓
    /home
```

---

# 🔌 API Endpoints

## Authentication

### Register

```http
POST /api/auth/register
```

Request:

```json
{
  "username": "student1",
  "email": "student1@example.com",
  "password": "password123"
}
```

### Login

```http
POST /api/auth/login
```

Request:

```json
{
  "username": "student1",
  "password": "password123"
}
```

### Current User

```http
GET /api/auth/me
```

Requires authentication.

---

# Questions API

### Post Question

```http
POST /api/questions
```

Student only.

### Get Current Competition Questions

```http
GET /api/questions
```

Student only.

---

# Attempts API

### Start Attempt

```http
POST /api/attempts/{questionId}/start
```

### Finish Attempt

```http
POST /api/attempts/{questionId}/finish
```

### Get My Attempt

```http
GET /api/attempts/{questionId}/me
```

### Get Question Attempts

```http
GET /api/attempts/{questionId}
```

Returns the students who attempted the question and their earned points.

---

# Competition API

### Get Current Competition

```http
GET /api/competition/current
```

Returns information about the current weekly competition.

---

# Leaderboard API

### Get Leaderboard

```http
GET /api/leaderboard
```

Student access.

Returns the currently visible finalized leaderboard.

### Admin Leaderboard Preview

```http
GET /api/leaderboard/preview
```

Admin access.

Provides a preview of the current competition leaderboard.

---

# Admin API

### Get Pending Applications

```http
GET /api/admin/applications
```

### Accept Application

```http
PATCH /api/admin/applications/{applicationId}
```

Request:

```json
{
  "decision": "ACCEPT"
}
```

### Reject Application

```http
PATCH /api/admin/applications/{applicationId}
```

Request:

```json
{
  "decision": "REJECT"
}
```

### Get Active Students

```http
GET /api/admin/students
```

### Remove Student

```http
DELETE /api/admin/students/{studentId}
```

---

# 🔴 WebSocket

The backend exposes a STOMP WebSocket endpoint:

```text
/ws
```

The frontend connects using STOMP.js.

The WebSocket connection is authenticated using the JWT.

## WebSocket Topics

### Questions

```text
/topic/questions
```

Used for real-time question updates.

### Attempts

```text
/topic/attempts
```

Used for real-time attempt updates.

### Leaderboard

```text
/topic/leaderboard
```

Used for real-time leaderboard updates.

---

# 💻 Local Development

## Requirements

Install the following:

- Java 25
- Node.js
- npm
- Git
- PostgreSQL/Supabase database

Docker is optional for local development.

---

# Backend Setup

Navigate to the backend directory:

```bash
cd learning
```

Set the required environment variables.

## Windows PowerShell

```powershell
$env:DB_PASSWORD="YOUR_DATABASE_PASSWORD"
$env:JWT_SECRET="YOUR_RANDOM_JWT_SECRET"
```

Run the backend:

```bash
./mvnw spring-boot:run
```

On Windows CMD:

```cmd
mvnw.cmd spring-boot:run
```

The backend will run on:

```text
http://localhost:8080
```

---

# Frontend Setup

Navigate to the frontend:

```bash
cd peer-learn-frontend
```

Install dependencies:

```bash
npm install
```

Start Angular:

```bash
npm start
```

The frontend will normally run on:

```text
http://localhost:4200
```

---

# 🔄 Frontend Development Proxy

During local development, Angular uses a proxy configuration to forward API requests to Spring Boot.

```text
Angular
localhost:4200
      ↓
Angular Proxy
      ↓
Spring Boot
localhost:8080
```

This allows the frontend to use API paths such as:

```text
/api/auth/login
/api/questions
/api/attempts
```

without hardcoding the local backend URL throughout the application.

---

# 🔑 Environment Variables

The application uses environment variables for sensitive configuration.

Required backend variables:

```text
DB_PASSWORD
JWT_SECRET
```

Example:

```text
DB_PASSWORD=your_database_password
JWT_SECRET=your_long_random_secret
```

> **Never commit real values to GitHub.**

Do not place credentials directly inside:

```text
application.properties
```

or source code.

---

# ☁️ Production Deployment

The application uses the following deployment architecture:

```text
Angular
   ↓
Vercel

Spring Boot
   ↓
Render

PostgreSQL
   ↓
Supabase
```

---

## Backend Deployment

The Spring Boot backend includes a Dockerfile.

Render can build and deploy the backend using the Dockerfile.

Backend root directory:

```text
learning
```

Required environment variables on Render:

```text
DB_PASSWORD
JWT_SECRET
```

The backend uses:

```properties
server.port=${PORT:8080}
```

so that Render can provide the production port.

---

## Frontend Deployment

The Angular frontend is deployed using Vercel.

Frontend root directory:

```text
peer-learn-frontend
```

Build command:

```bash
npm run build
```

The deployed frontend communicates with the deployed Spring Boot backend.

---

# 🔗 Production WebSocket

For local development:

```text
ws://localhost:8080/ws
```

For production:

```text
wss://YOUR-BACKEND-URL/ws
```

The production WebSocket must use:

```text
wss://
```

because the Angular application is served over HTTPS.

The backend WebSocket configuration must allow the actual deployed frontend URL.

Example:

```java
.setAllowedOrigins(
    "https://your-frontend.vercel.app"
);
```

---

# 📖 Swagger API Documentation

During development, Swagger UI is available at:

```text
/swagger-ui/index.html
```

Local Swagger URL:

```text
http://localhost:8080/swagger-ui/index.html
```

The OpenAPI documentation is available at:

```text
/v3/api-docs
```

Swagger provides an interactive interface for testing the REST APIs.

---

# 🔄 Application Flow

## New Student

```text
                 Register
                    │
                    ▼
            Student Application
                    │
                    ▼
                 PENDING
                    │
           ┌────────┴────────┐
           ▼                 ▼
        ACCEPT             REJECT
           │                 │
           ▼                 ▼
      Account Active      Login Denied
           │
           ▼
      Student Login
           │
           ▼
       Student Home
```

---

# 📝 Question Flow

```text
Student logs in
      │
      ▼
Current Competition
      │
      ▼
Post LeetCode Question
      │
      ▼
+5 points
      │
      ▼
Other students see question
      │
      ▼
Start Attempt
      │
      ▼
Solve Question
      │
      ▼
Finish Attempt
      │
      ▼
Automatic Score Calculation
      │
      ▼
10 or 20 points
```

---

# 📅 Weekly Competition Lifecycle

```text
             Monday
                │
                ▼
        Competition ACTIVE
                │
                │
          Student Activity
                │
                ▼
             Sunday
                │
                ▼
       Competition Finalized
                │
                ▼
      Calculate Weekly Scores
                │
                ▼
              Top 3
                │
                ▼
      LEADERBOARD_VISIBLE
                │
                ▼
         Visibility Period
                │
                ▼
      Competition Data Deleted
```

---

# ⚡ Real-Time System Flow

```text
Student A
   │
   │ POST Question
   ▼
Spring Boot
   │
   ├──────────────► PostgreSQL
   │
   └──────────────► WebSocket
                         │
                         ▼
                  /topic/questions
                         │
                 ┌───────┴───────┐
                 ▼               ▼
            Student B        Student C
```

---

# ⚠️ Error Handling

The application handles common errors such as:

- Invalid username/password
- Duplicate username
- Duplicate email
- Inactive student account
- Unauthorized API requests
- Forbidden role access
- Attempting own question
- Duplicate attempts
- Invalid question submission
- Invalid application decisions
- Missing competition

The frontend displays user-friendly error messages rather than exposing internal backend details.

---

# 🔒 Security Considerations

The project implements several security mechanisms.

## Password Hashing

Passwords are stored using BCrypt.

```text
Plain Password
      ↓
    BCrypt
      ↓
Password Hash
```

## JWT Authentication

Protected APIs require a valid JWT.

## Stateless Sessions

Spring Security uses stateless authentication.

## Role-Based Authorization

Endpoints are protected using roles such as:

```text
ROLE_ADMIN
ROLE_STUDENT
```

## Environment-Based Secrets

Database passwords and JWT secrets are provided through environment variables rather than being committed to source code.

---

# 🚀 Future Improvements

Possible future improvements include:

- Profile management
- Student avatars
- Question categories
- Difficulty-based scoring
- More detailed analytics
- Competition history
- Achievement/badge system
- Email notifications
- Password reset
- Admin dashboard charts
- Advanced leaderboard statistics
- Question moderation
- Multiple competition modes
- Redis-based WebSocket messaging for larger deployments
- Automated testing and CI/CD
- Rate limiting
- Refresh tokens
- Production-grade secret management

---

# 📸 Screenshots

## Login

Add your login screenshot here.
<img width="646" height="511" alt="image" src="https://github.com/user-attachments/assets/b2cbc673-8eb8-420a-8cdd-e2494e3be87f" />

---

## Student Dashboard

Add your student dashboard screenshot here.

<img width="1517" height="610" alt="image" src="https://github.com/user-attachments/assets/fa47f416-2188-40f7-af68-5f55f2fda1f7" />


---

## Admin Dashboard

Add your admin dashboard screenshot here.

<img width="1365" height="667" alt="image" src="https://github.com/user-attachments/assets/cd1f1601-d05e-4556-95dd-104544174ab4" />


---

## Registration

Add your registration screenshot here.

<img width="562" height="563" alt="image" src="https://github.com/user-attachments/assets/ddc4e52a-cc77-4da1-8980-fa34ce7989f7" />


---

## Question & Attempt System

Add your question/attempt screenshot here.

<img width="903" height="673" alt="image" src="https://github.com/user-attachments/assets/046e090b-64c5-47d1-9494-fccc54dbe616" />


---

# 👨‍💻 Author

**Yash Gupta**

P2P Learn — Peer-to-Peer Learning Competition Platform

---

# 📄 License

This project is developed as an academic/software development project.

If you intend to distribute the source code publicly, an appropriate open-source license such as MIT can be added.

---

# 💡 Project Summary

P2P Learn combines peer-to-peer learning with competitive programming.

The core idea is:

```text
LEARN
  ↓
SHARE
  ↓
SOLVE
  ↓
EARN POINTS
  ↓
COMPETE
  ↓
LEADERBOARD
```

Students don't just solve problems — they contribute problems for other students to solve, creating a collaborative learning environment with a competitive scoring system.
