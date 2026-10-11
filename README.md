# DigitalMind (DocMind) - AI-Powered RAG Document Intelligence Platform

**DigitalMind** is an enterprise-grade document intelligence platform powered by Retrieval-Augmented Generation (RAG). It enables users to upload, process, search, and converse with documents through high-accuracy semantic embeddings and local LLMs.

---

## 🌟 Key Features

- **Document Ingestion & Processing**: Intelligent extraction, chunking, and metadata parsing for PDFs and text documents.
- **Vector Search with pgvector**: High-performance semantic similarity search using PostgreSQL with the `pgvector` extension and HNSW indexing.
- **Local AI & Privacy First**: Powered by Spring AI with local Ollama models (`llama3.1` for chat and `nomic-embed-text` for embeddings) — zero data leaks to third parties.
- **Chat Memory & Multi-turn Conversations**: Persistent chat history and conversational context stored in PostgreSQL with conversation isolation.
- **Secure Authentication**: JWT-based stateless authentication and user-specific document isolation.
- **Modern Web Interface**: Responsive, high-performance UI built with React 19, TypeScript, Tailwind CSS, and Framer Motion.

---

## 🏗️ Architecture & Tech Stack

### Backend
- **Framework**: Spring Boot 3 / 4.x (Java 21)
- **AI / LLM Integration**: Spring AI (Ollama starter, Vector Store starter)
- **Database**: PostgreSQL 16 with `pgvector` extension
- **Security**: Spring Security 6 with JWT authentication
- **Persistence**: Spring Data JPA & Hibernate

### Frontend
- **Framework**: React 19 + TypeScript + Vite
- **Styling**: Tailwind CSS + Lucide Icons + Framer Motion
- **State Management**: Zustand
- **HTTP Client**: Axios with automatic JWT injection

---

## 🚀 Getting Started

### Prerequisites
- **Java 21** or later
- **Node.js 18+** & npm
- **Docker & Docker Compose**
- **Ollama** installed locally (`ollama pull llama3.1`, `ollama pull nomic-embed-text`)

---

### 1. Database Setup (Docker)

Start the PostgreSQL instance with `pgvector`:

```bash
docker compose up -d
```

This starts PostgreSQL on port `5434` (mapped to internal `5432`) and automatically initializes the `vector` and `uuid-ossp` extensions.

---

### 2. Configure Environment

Copy `.env.example` to `.env` if custom variables are needed:

```bash
cp .env.example .env
```

Default development properties are configured in `src/main/resources/application-dev.yml`.

---

### 3. Backend Setup

Run the Spring Boot application using Maven:

```bash
./mvnw spring-boot:run
```

The backend server will start on `http://localhost:8081`.

---

### 4. Frontend Setup

Navigate to the frontend directory, install dependencies, and launch the dev server:

```bash
cd frontend/docmind-frontend
npm install
npm run dev
```

The frontend will be available at `http://localhost:3000` with requests to `/api` proxied to the backend.

---

## 🔒 Security & Privacy

- All sensitive environment files (`.env`, `.env.*`) and credentials (`*.pem`, `*.key`, etc.) are ignored by Git.
- Local LLM inference ensures document contents and queries never leave your private infrastructure.

---

## 📄 License

This project is licensed under the MIT License.
