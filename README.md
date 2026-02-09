# 🙏 Prayly

> **Connect, Share, and Intercede.**
> A full-stack web application connecting those in need of prayer with a community ready to intercede.

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)
![Vue.js](https://img.shields.io/badge/Vue.js-35495E?style=for-the-badge&logo=vuedotjs&logoColor=4FC08D)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-316192?style=for-the-badge&logo=postgresql&logoColor=white)

## 📖 About
**Prayly** is a platform designed to foster spiritual support. It allows users to post prayer requests, receive notifications when others are praying for them, and track "Praise Reports" when prayers are answered.

This project demonstrates a robust full-stack architecture using a **Java Spring Boot** backend to handle RESTful APIs and business logic, coupled with a reactive **Vue.js** frontend for a seamless user experience, all powered by a **PostgreSQL** database.

## ✨ Features
- **Create & Manage Requests:** Users can post public or anonymous prayer requests.
- **Intercession Button:** A "Pray" button that increments a counter and notifies the requester.
- **Feed Filtering:** Filter requests by category (Healing, Guidance, Family, etc.) or recency.
- **User Dashboard:** View your prayer history and requests you are following.
- **Praise Reports:** Mark requests as "Answered" and share the story.

## 🛠️ Tech Stack

### Backend
- **Language:** Java (JDK 17+)
- **Framework:** Spring Boot 3.x
- **Security:** Spring Security & JWT (JSON Web Tokens)
- **Database:** PostgreSQL
- **ORM:** Spring Data JDBC

### Frontend
- **Framework:** Vue.js 3 (Composition API)
- **Build Tool:** Vite
- **State Management:** Pinia
- **Styling:** CSS
- **HTTP Client:** Axios

## 🚀 Getting Started

### Prerequisites
- Java JDK 17 or higher
- Node.js & npm
- PostgreSQL installed and running locally
- Maven (or Gradle)

### 1. Database Setup
Create a PostgreSQL database named `PraylyDB` (or whatever you prefer):
```sql
CREATE DATABASE PraylyDB;
