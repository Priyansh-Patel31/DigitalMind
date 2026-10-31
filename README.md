# DocMind – RAG‑Based Document Intelligence Platform

## Overview
DocMind is a full‑stack application that provides AI‑driven search and question‑answering over a corpus of user‑uploaded documents. It combines:

| Layer | Technology |
|-------|------------|
| **Backend** | Spring Boot 4.1, Spring AI (Ollama, PGVector), Spring Security (JWT), PostgreSQL, Docker |
| **Frontend** | React + TypeScript, Vite, Tailwind CSS v4 |
| **RAG** | Vector store (PGVector), document ingestion via Tika & PDF readers, search‑advisor tool |

The backend exposes a REST API (`/api/**`) that the React frontend consumes. Docker Compose bundles the services for easy local development.

---

## Architecture Diagrams
- **Chat Memory DB Architecture** – see [CHAT_MEMORY_DB_ARCHITECTURE.md](file:///Users/patelpriyansh/Desktop/PROJECTS/Internship%20resume%20for%20startup/docmind-rag-based-project-main%202/docs/CHAT_MEMORY_DB_ARCHITECTURE.md)
- **User Document Isolation Analysis** – see [USER_DOCUMENT_ISOLATION_ANALYSIS.md](file:///Users/patelpriyansh/Desktop/PROJECTS/Internship%20resume%20for%20startup/docmind-rag-based-project-main%202/docs/USER_DOCUMENT_ISOLATION_ANALYSIS.md)

---

## Prerequisites

| Item | Minimum version |
|------|-----------------|
| **JDK** | 21 |
| **Maven** | 3.9+ |
| **Node.js** | 20.x |
| **npm** | 10.x |
| **Docker & Docker‑Compose** | 2.20+ |
| **Ollama** (for local LLM) – optional if you use remote model | see Ollama docs |

---

## Backend Setup

1. **Clone the repo** (already done).
2. **Configure environment** – copy `src/main/resources/application-dev.yml` to `application.yml` if you want to customise.
3. **Start PostgreSQL & Ollama** (Docker Compose will do this automatically – see below).
4. **Build & run**

```bash
# From the project root
mvn clean install          # compile, test, package
java -jar target/docmind-backend-0.0.1-SNAPSHOT.jar   # runs on http://localhost:8081
```

The backend runs on **Port 8081** and expects a PostgreSQL DB named `docmind` (see `docker-compose.yml`).

### Available API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `POST /api/auth/register` | Register a new user (email + password). |
| `POST /api/auth/login` | Obtain JWT token. |
| `POST /api/documents` | Upload a document (PDF/DOCX). |
| `GET /api/documents/{id}` | Retrieve document metadata. |
| `POST /api/chat` | Ask a question; returns streaming answer with citations. |
| `GET /api/chat/history` | List past conversations. |

---

## Frontend Setup

```bash
cd frontend/docmind-frontend
npm install
npm run dev          # http://localhost:3000
npm run build        # production build → dist/
```

The dev server proxies `/api/**` to the backend automatically (see Vite config).

---

## Running the Whole Stack with Docker Compose

```bash
docker compose up -d      # starts PostgreSQL, Ollama, backend, and (optionally) nginx proxy
```

* `docker-compose.yml` defines services: `postgres`, `ollama`, `backend`, `frontend`.
* **Stop** with `docker compose down`.

---

## Configuration

Key configuration files:

| File | Purpose |
|------|---------|
| `src/main/resources/application-dev.yml` | Development‑time Spring properties (DB URL, JWT secret, AI model config). |
| `frontend/docmind-frontend/vite.config.ts` | Vite dev server & build settings. |
| `docker-compose.yml` | Service orchestration (ports, volumes, environment). |
| `prompt.md` (in `docs/`) | The system prompt used for the LLM. |
| `FRONTEND_BUILD_PROMPT.md` | Guidance for building the frontend UI. |

---

## Testing

### Backend

```bash
mvn test          # runs JUnit tests (including integration tests with Testcontainers)
```

### Frontend

```bash
npm run test      # jest / vitest tests (if present)
```

---

## Documentation

Additional useful markdown files are located under `docs/`:

- `CHAT_MEMORY_DB_ARCHITECTURE.md` – DB schema for chat memory.
- `USER_DOCUMENT_ISOLATION_ANALYSIS.md` – security considerations for multi‑tenant document handling.
- `FRONTEND_BUILD_PROMPT.md` – design notes for the UI.
- `prompt.md` – the LLM prompt used by the search‑advisor tool.

---

## Contributing

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/xyz`).
3. Follow the existing code style (Java 21, Lombok, Spring conventions; React/TS linting).
4. Submit a Pull Request with a clear description and any required migrations.

---

## License

This project is provided under the **MIT License** (see `LICENSE` file).

---

## Quick Start Summary

```bash
# Backend
mvn clean install
java -jar target/docmind-backend-0.0.1-SNAPSHOT.jar

# Frontend
cd frontend/docmind-frontend
npm install && npm run dev
```

Or, run everything with Docker Compose:

```bash
docker compose up -d
```

Open your browser at **http://localhost:3000** and start uploading documents!
