
# Bank

Application to create accounts with monetary balances and transfer money between them.

## Build

To build application issue following command:

```bash
./gradlew clean shadowJar
```
After successful build there is application JAR `bank-1.0.0-all.jar` available at `build/libs`.

## Run

To start application issue following command:

```bash
java -jar build/libs/bank-1.0.0-all.jar [options]
```

There are some options that can be used to configure application:

- `--port, -p` - Port to listen. Default is `18080`

- `--host, -h` - Host to listen. Default is `localhost`

- `--app, -a` - Application name. Used as first path segment in API methods. Default is `bank`.

- `--threads-count` - Number of threads to process API requests. Default is `100`.

- `--min-account-balance` - Minimum allowable amount of money on account balance. Default is `0`.

- `--max-account-balance` - Maximum allowable amount of money on account balance. Default is `1000000000000000000`.

- `--help` - Print help with short description of available options


## API

API methods are available at following base URL: `http://<host>:<port>/<application-name>`. Default is `http://localhost:18080/bank`.

There are following API methods:
- `/api/account/create` - Create account with given monetary balance

- `/api/account/{account}/balance` - Retreive current account's balance. Use existing account's ID instead `{account}`

- `/api/transfer` - Transfer money from one account to another

Swagger UI can be used to request API. Be default it is available at `http://localhost:18080/bank/docs/api`. 
Swagger documentation is available at `http://localhost:18080/bank/swagger.json`.

## Examples

### - Create two accounts with $250.00 on balances

```bash
curl -X POST -H 'Content-type: application/json' -d '{ "balance": { "amount": 250.00, "currency": "USD" } }' http://localhost:18080/bank/api/account/create
```
Response:`{"account":"1000000001"}`

```bash
curl -X POST -H 'Content-type: application/json' -d '{ "balance": { "amount": 250.00, "currency": "USD" } }' http://localhost:18080/bank/api/account/create
```

Response: `{"account":"1000000002"}`

### - Check initial balances on created accounts

```bash
curl -H 'Content-type: application/json' http://localhost:18080/bank/api/account/1000000001/balance
```
Response: `{"balance":{"amount":250.00, "currency":"USD"}}`

```bash
curl -H 'Content-type: application/json' http://localhost:18080/bank/api/account/1000000002/balance
```
Response: `{"balance":{"amount":250.00, "currency":"USD"}}`

### - Transfer $100 from first account to second account

```bash
curl -X POST -H 'Content-type: application/json' -d '{ "source": "1000000001", "destination": "1000000002", "amount": { "amount": 100.00, "currency": "USD" } }' http://localhost:18080/bank/api/transfer
```

Response: `{"status":"Success"}`
  
### - Check final balances on accounts

```bash
curl -H 'Content-type: application/json' http://localhost:18080/bank/api/account/1000000001/balance
```
Response: `{"balance":{"amount":150.00, "currency":"USD"}}`

```bash
curl -H 'Content-type: application/json' http://localhost:18080/bank/api/account/1000000002/balance
```
Response: `{"balance":{"amount":350.00, "currency":"USD"}}`
