# RacePulse

**Real-Time Formula 1 Intelligence Platform**

A full-stack motorsport analytics platform providing real-time race monitoring, driver and constructor intelligence, telemetry visualization, historical analytics, and statistical projections.

---

## 1. Product Overview

RacePulse transforms raw Formula 1 session streams into actionable telemetry insights, driver consistency ratings, constructor pace graphs, and statistical predictions.

### Key Capabilities
- **Live Race Center**: Real-time high-frequency telemetry matrix with position deltas, sector timing (S1, S2, S3), pit window indicators, and FIA incident feeds via WebSockets with automatic REST polling fallbacks.
- **Driver Intelligence**: In-depth driver cards, teammate head-to-head comparison, recent finishing form, qualifying conversion, and multi-season points progression.
- **Constructor Intelligence**: Constructor standings, car pace development indices, points contribution ratios, and victory tracking.
- **Race Analytics Engine**: Multi-variable driver consistency scoring, qualifying vs. race performance scatter matrices, and position gain/loss analysis.
- **Statistical Trend Projections**: Transparent statistical estimation engine computing expected finishing ranges, recent performance trends, and championship projections (clearly labeled as statistical models).
- **Telemetry Analyzer**: Distance-based speed trace comparisons, throttle percentage overlays, and apex speed indicators.

---

## 2. Technology Stack

### Frontend Architecture
- **Framework**: React 18 with Vite
- **Routing**: React Router v6
- **State & Data Fetching**: TanStack React Query v5
- **Data Visualization**: Recharts (Interactive Area, Bar, Line, Scatter charts)
- **Styling & UI**: Tailwind CSS v3 with Glassmorphism design system & Lucide Icons
- **Real-Time Communication**: Native WebSockets with STOMP & SockJS support + REST polling fallback

### Backend Architecture
- **Framework**: Spring Boot 3.2 (Java 17/22)
- **API Protocol**: RESTful JSON (`/api/v1/...`) with unified `ApiResponse<T>` wrapper
- **Real-Time Messaging**: Spring WebSocket Message Broker (`/topic/live`, `/topic/timing`)
- **Reactive HTTP Clients**: Spring WebFlux `WebClient` for external F1 APIs (OpenF1 & Jolpica/Ergast)
- **Caching**: Spring Data Redis with local in-memory fallback
- **Health & Monitoring**: Spring Boot Actuator (`/actuator/health` & `/api/v1/health`)
- **Exception Handling**: `@RestControllerAdvice` global exception handling

---

## 3. System Architecture & API Endpoints

```
 ┌─────────────────────────────────────────────────────────────┐
 │                      RacePulse Frontend                     │
 │          React 18 + Vite + Recharts + React Query           │
 └──────────────┬──────────────────────────────▲───────────────┘
                │ REST API Calls               │ WebSockets
                ▼                              │ (/topic/live)
 ┌─────────────────────────────────────────────┴───────────────┐
 │                   RacePulse Spring Boot API                 │
 │            Controller -> Service -> Cache/Client            │
 └──────────────┬──────────────────────────────┬───────────────┘
                │                              │
                ▼                              ▼
 ┌──────────────────────────────┐ ┌─────────────────────────────┐
 │         Redis Cache          │ │        External APIs        │
 │  Standings, Schedule, Stats  │ │    OpenF1  +  Jolpica/Ergast │
 └──────────────────────────────┘ └─────────────────────────────┘
```

### REST API Reference (`/api/v1`)

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/v1/dashboard` | Main dashboard payload (Next race, standings, alerts, weather) |
| `GET` | `/api/v1/races` | Full season calendar & race schedule |
| `GET` | `/api/v1/races/{id}` | Specific race details by round or name |
| `GET` | `/api/v1/drivers` | Driver standings & profile overview |
| `GET` | `/api/v1/drivers/{id}` | Specific driver profile details |
| `GET` | `/api/v1/drivers/{id}/statistics` | Driver intelligence, form, teammate H2H |
| `GET` | `/api/v1/constructors` | Constructor standings & overview |
| `GET` | `/api/v1/constructors/{id}` | Constructor pace & driver breakdown |
| `GET` | `/api/v1/analytics` | Full motorsport analytics payload |
| `GET` | `/api/v1/predictions` | Statistical finishing range projections |
| `GET` | `/api/v1/standings` | Driver & Constructor championship standings |
| `GET` | `/api/v1/live/session` | Current live session telemetry snapshot |
| `GET` | `/api/v1/health` | Service health status |

---

## 4. Local Development Setup

### Prerequisites
- Node.js 18+ and npm
- Java JDK 17+
- Maven 3.8+ (or included wrapper)
- Docker & Docker Compose (optional for Redis container)

### Step 1: Run Backend API
```bash
cd pitwall-backend

# Set Java Home if required
$env:JAVA_HOME="path/to/jdk"

# Run tests and start Spring Boot app
mvn spring-boot:run
```
Backend will start at `http://localhost:8080` (Health check at `http://localhost:8080/actuator/health`).

### Step 2: Run Frontend App
```bash
# In project root
npm install
npm run dev
```
Frontend dev server starts at `http://localhost:5173` or `http://localhost:3000`.

---

## 5. Docker Setup

Run the full stack (Frontend, Backend, Redis) with Docker Compose:

```bash
docker compose up --build
```

Services exposed:
- Frontend: `http://localhost:3000`
- Backend API: `http://localhost:8080`
- Redis: `localhost:6379`

---

## 6. Testing

### Backend Unit & Service Tests
```bash
cd pitwall-backend
mvn clean test
```
Result:
```
Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

### Frontend Production Build Verification
```bash
npm run build
```

---

## 7. Known Limitations & Future Roadmap

- **Known Limitations**:
  - Live session telemetry relies on OpenF1 public APIs; during off-season or session intervals, high-frequency historical snapshots are served.
  - Prediction models are transparent statistical estimations based on rolling 5-session pace, qualifying conversion %, and pit stop efficiency.
- **Future Improvements**:
  - Machine Learning regression pipeline integration for tire wear prediction.
  - Live audio team radio stream integration.

---

## 8. Acknowledgements & Attribution

RacePulse was substantially re-engineered from an existing open-source Formula 1 dashboard project. The original project's license and attribution requirements are preserved. RacePulse introduces substantial changes to the architecture, UI, APIs, real-time infrastructure, caching, analytics, testing, and deployment configuration.

Formula 1 is a registered trademark of Formula One Licensing B.V. Unofficial fan analytics application.
