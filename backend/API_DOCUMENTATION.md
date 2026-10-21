# SpendWise REST API Documentation

**Application**: Personal Expense & Budget Manager (Group 4)  
**Authors**: Naomi Akilimali & Rabecca Chola  
**Base URL**: `http://<your-server-ip>/expense_api/api/`  

---

## 1. Authentication Endpoints

### `POST /auth/register`
Registers a new user account.

- **Request Body**:
  ```json
  {
    "fullName": "Naomi Akilimali",
    "email": "naomi@example.com",
    "password": "Password123"
  }
  ```
- **Response (200 OK)**:
  ```json
  {
    "success": true,
    "message": "User registered successfully.",
    "data": {
      "id": 1,
      "fullName": "Naomi Akilimali",
      "email": "naomi@example.com"
    }
  }
  ```

---

### `POST /auth/login`
Authenticates user credentials and returns session token.

- **Request Body**:
  ```json
  {
    "email": "naomi@example.com",
    "password": "Password123"
  }
  ```
- **Response (200 OK)**:
  ```json
  {
    "success": true,
    "message": "Login successful.",
    "data": {
      "token": "4f8a91c2e37b...",
      "user": {
        "id": 1,
        "fullName": "Naomi Akilimali",
        "email": "naomi@example.com"
      }
    }
  }
  ```

---

## 2. Transactions Endpoints

### `GET /transactions`
Fetches all user transactions sorted by date.

- **Query Parameters**: `userId` (optional, default: 1)
- **Response (200 OK)**:
  ```json
  {
    "success": true,
    "message": "Transactions retrieved.",
    "data": [
      {
        "id": 10,
        "clientUuid": "550e8400-e29b-41d4-a716-446655440000",
        "type": "expense",
        "category": "Groceries",
        "amount": 150.50,
        "description": "Weekly grocery shopping",
        "transactionDate": "2026-10-19"
      }
    ]
  }
  ```

---

### `POST /transactions`
Creates or synchronizes an offline transaction.

- **Request Body**:
  ```json
  {
    "clientUuid": "550e8400-e29b-41d4-a716-446655440000",
    "type": "expense",
    "category": "Groceries",
    "amount": 150.50,
    "description": "Weekly grocery shopping",
    "transactionDate": "2026-10-19"
  }
  ```
- **Response (200 OK)**:
  ```json
  {
    "success": true,
    "message": "Transaction synced successfully.",
    "data": {
      "id": 10,
      "clientUuid": "550e8400-e29b-41d4-a716-446655440000",
      "type": "expense",
      "category": "Groceries",
      "amount": 150.50,
      "description": "Weekly grocery shopping",
      "transactionDate": "2026-10-19"
    }
  }
  ```
