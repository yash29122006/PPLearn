# P2P Learn — Peer-to-Peer Learning Competition Platform

P2P Learn is a full-stack peer-to-peer learning competition platform where students learn collaboratively by sharing programming questions, attempting questions posted by other students, earning points, and competing on a weekly leaderboard.

The platform provides separate experiences for **Students** and **Administrators**, with secure authentication, student approval, question management, timed attempts, automated scoring, weekly competitions, leaderboards, and real-time WebSocket updates.

---

## Demo

### Live Application

Frontend:

https://p2plearn-git-main-p2plearn.vercel.app/login

Backend API:

https://p2p-learn.onrender.com

---

## Features

### Student Features

- Student registration
- Secure login using JWT authentication
- Passwords stored using BCrypt hashing
- Account approval system
- View current competition
- View total points
- Post one LeetCode question per day
- Cannot attempt a question posted by themselves
- Start a question attempt
- Finish a question attempt
- Automatic score calculation
- View other students who attempted a question
- View points earned by each participant
- Weekly competition
- Weekly leaderboard
- Real-time updates using WebSockets
- Logout functionality

---

### Admin Features

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

# Competition Rules

The platform follows a weekly competition model.

### Competition Duration

Each competition runs from:

```text
Monday → Sunday
