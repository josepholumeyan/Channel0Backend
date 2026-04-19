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

## Design Notes

- Playback failures are treated as state transitions
- Broken content is disabled instead of retried indefinitely
- Backend owns all playback decisions
- The system prioritizes continuity over strict completeness

---

## License

This project is source-available for educational and evaluation purposes only.

Commercial use, redistribution, or deployment requires explicit permission
from the author.See the [LICENSE](LICENSE) file for details.
