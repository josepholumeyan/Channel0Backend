# Channel0 Backend
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-Backend-success)
![Kotlin](https://img.shields.io/badge/Kotlin-1.9-blueviolet)
![JPA](https://img.shields.io/badge/Persistence-JPA%2FHibernate-important)

## Overview

The Channel0 backend is responsible for playback orchestration, user progress tracking, and failure recovery.

It determines what content should play next and ensures playback remains continuous even when underlying video sources fail.

> This backend powers the [Channel0 Android client](https://github.com/josepholumeyan/Channel0).

## Core Responsibilities

- Resolve next playable segment
- Track user playback progress
- Handle playback failures
- Disable broken episodes
- Maintain deterministic playback order

## Key Features

- Deterministic playback engine (channel → show → episode → segment)
- Per-user playback state management
- Lazy initialization of user progress
- Backend-driven failure recovery
- Episode invalidation for broken content
- Direct database update queries for performance
- Lightweight caching strategies
- Rate limiting and request control

## Architecture

### Controller Layer
- ChannelController
- UserController
- PlaybackController

### Service Layer
- PlaybackService
- ChannelService
- ShowService
- UserService

### Persistence Layer
- JPA Entities
- Repositories
- Custom update queries

## Playback Model

Playback is state-driven rather than time-based.

```text
Channel → Show → Episode → Segment → Video
```
The system always resolves the next valid playable unit based on stored state.

---
## API Endpoints

```text
GET    /channels
GET    /channels/{channelId}/shows
GET    /channels/{channelId}/shows/enabled
POST   /channels/{channelId}/shows/disable
POST   /channels/{channelId}/shows/enable

POST   /users

GET    /playback/next
GET    /playback/next-episode
GET    /playback/next-show
POST   /playback/disable-show
POST   /playback/error
```
---
## Tech Stack

- **Language:** Kotlin
- **FrameWork:** Spring Boot
- **Persistence:** JPA / Hibernate
- **Database:** Relational (SQL)
- **Logging:** SLF4J
- **Concurrency:** Transactional Services

---

## Project Structure

```bash
src/main/kotlin/com/example/channel0
│
├── controller
├── config
│   ├── security
│   └── rateLimit
├── data
│   ├── config
│   ├── dto
│   ├── entities
│   └── repositories
├── domain
│   ├── services
│   └── validation
├── exception
└── utils
```
---

##  Getting Started

To run this project locally or deploy your own instance, follow the steps below.

---

### 1. Setup your database (PostgreSQL)

- Install and run PostgreSQL
- Create a new database
- Update your environment variables with your database credentials

---

### 2. Configure environment variables

Create a `.env` file in the root directory and define:

```bash
SPRING_DATASOURCE_URL=your_postgres_connection_string
SPRING_DATASOURCE_USERNAME=your_postgres_connection_user_name
SPRING_DATASOURCE_PASSWORD=your_postgres_connection_password
ADMIN_KEY=your_secure_admin_key
```

> `ADMIN_KEY` is required to authorize admin-level operations such as data population.

---

### 3. Prepare initial data

This project does not ship with a preloaded database.

- A [sample.json](src/main/resources/sample.json) file is provided to demonstrate the expected data structure
- Use this as a reference when preparing your own dataset for population

> ⚠️ Note: The sample file only contains a minimal dataset for demonstration purposes.  
> You are expected to supply your own data when running this project.


---

### 4. Populate the database

Send a POST request to:

POST /admin/populate

#### Headers:

X_ADMIN_KEY: your_secure_admin_key Content-Type: application/json

#### Body:
- Include your dataset JSON

This will seed your database with channels, shows, and related data.

---

### 5. Run the server

Start the application locally:

./gradlew bootRun

Or deploy to any cloud platform of your choice.

---

### 6. Connect the Android client

- Copy your server's base URL (local or deployed)
- Add it to the Android app( app/src/main/java/com/intricatelabs/channel0/di/NetworkModule.kt ):

BASE_URL=your_backend_url

[Channel0 Android client](https://github.com/josepholumeyan/Channel0/blob/main/app/src/main/java/com/intricatelabs/channel0/di/NetworkModule.kt)

---

## 📌 Notes

- Each developer should run their own backend instance
- Do not rely on any external or shared database
- This project is intended for learning, experimentation, and portfolio use


---


## Design Notes

- Playback failures are treated as state transitions
- Broken content is disabled instead of retried indefinitely
- Backend owns all playback decisions
- The system prioritizes continuity over strict completeness

---

##  Disclaimer

This backend service is part of the **Channel0 demonstration ecosystem**, built to explore content structuring, metadata curation, and backend-to-client data workflows for an Android-based media application.


This project does **not host, upload, stream, or distribute any video content**.


It solely manages structured metadata consumed by the Android client.  
All media playback is handled externally via the **YouTube embedded player** within the client application.


All video content remains the property of its respective rights holders.


This project is:
- not affiliated with YouTube
- not endorsed by YouTube
- not connected to any content providers


---


## Intended Use


This project is provided strictly for:
- educational purposes
- backend system design demonstration
- portfolio and technical showcase


It is not intended to function as a production streaming service or content distribution platform.


---


##  Notes


- API structure, endpoints, and data contracts may evolve as the system is refined or adjusted for compliance with third-party platform policies.
- This repository represents a **technical prototype**, not a commercial product.


## License

This project is source-available for educational and evaluation purposes only.

Commercial use, redistribution, or deployment requires explicit permission
from the author.See the [LICENSE](LICENSE) file for details.
