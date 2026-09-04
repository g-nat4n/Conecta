# Conecta — Rede Social

## Estrutura

- `backend/` — Spring Boot + JWT + PostgreSQL + WebSocket/STOMP
- `frontend/` — React + TypeScript + Vite

## Pré-requisitos

- Java 17+
- Node.js 20+
- PostgreSQL (banco `rede` na porta `5433`)

## Backend

```bash
cd backend
./mvnw spring-boot:run
```

API: `http://localhost:8080`  
WebSocket: `http://localhost:8080/ws`

## Frontend

```bash
cd frontend
npm install
npm run dev
```

App: `http://localhost:5173`

## Funcionalidades

1. Auth (registro/login JWT)
2. Perfil (editar dados, foto, senha)
3. Posts (criar/editar/excluir, curtidas, comentários)
4. Amigos (pesquisa, solicitações, aceitar/recusar, remover)
5. Chat (conversas, enviar/editar/excluir mensagens)
6. Notificações + toasts em tempo real (STOMP)
