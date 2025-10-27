# DASTO

| Method   | Endpoint                              | Detail                                   |
|----------|---------------------------------------|------------------------------------------|
| GET      | `/users`                              | get all users                            |
| GET      | `/users/{id}`                         | get by id                                |
| POST     | `/users`                              | create a new user                        |
| PATCH    | `/users/{id}`                         | update a user                            |
| DELETE   | `/users/{id}`                         | delete a user (soft delete)              |
|          |                                       |                                          |
| GET      | `/users/{id}/budgets`                 | get user all budgets config              |
| GET      | `/users/{id}/budgets/{budget_id}`     | get budget config                        |
| GET      | `/users/{id}/budgets/active`          | get current active budget config         |
| POST     | `/users/{id}/budgets`                 | set a new budget config to user          |
| PATCH    | `/users/{id}/budgets/{budget_id}`     | update the budget config                 |
|          |                                       |                                          |
| GET      | `/users/{id}/expenses`                | get users expenses                       |
| GET      | `/users/{id}/expenses/{expense_id}`   | get detailed expense info                |
| POST     | `/users/{id}/expenses`                | add a new expense to user                |
| PATCH    | `/users/{id}/expenses/{expense_id}`   | update expense                           |
| DELETE   | `/users/{id}/expenses/{expense_id}`   | delete expense                           |

---

## `GET /users`
1. get all users paginated without the expenses and with the budget config

## `GET /users/{id}`
1. get user by public id (uuid) with expenses

## `POST /users`
1. receive a json, with user info and it's budget config

    ```json
    {
        "firstName": "string",
        "lastName": "string",
        "email": "string",
        "password": "string",
        "totalBudget": int,
        "fixedExpenses": int,
        "investmentPercentage": int,
        "investmentAmount": int
    }
    ```
2. budget is validated, it should only have positive values, only one of `investmentPercentage` or `investmentAmount` should have a value, never both
3. check if email already exists
4. register user with budget config with effective date of registration day and infinite termination date

## `