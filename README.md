# EV Price Service

A Spring Boot service to calculate the price of electric vehicle charging sessions based on a flexible tariff structure. Supports charging by energy consumed, charging time, and parking time.

## Features

- Save and manage custom tariff structures
- Calculate total charging cost based on energy and time
- H2 in-memory database (no external DB setup required)
- REST API for integration and testing

## Technologies

- Java 17
- Spring Boot
- Spring Data JPA (with H2 database)
- Gradle

---

## Getting Started

### 1. Run the application

Use your IDE or command line:

```bash
./gradlew bootRun
```

> Application runs on: `http://localhost:8080`

---

## API Endpoints

### 1. Create Tariff

**POST** `/tariffs`

#### Request body:

```json
{
  "name": "A Test Tariff",
  "components": [
    {
      "type": "KWH",
      "price": 10.0,
      "freeUnits": 0
    },
    {
      "type": "CHARGING_MINUTE",
      "price": 2.0,
      "freeUnits": 20
    },
    {
      "type": "PARKING_MINUTE",
      "price": 3.0,
      "freeUnits": 0
    }
  ]
}
```

#### Example (Postman):
- Method: `POST`
- URL: `http://localhost:8080/tariffs`
- Body: `raw` → `JSON`


####  Tariff Explanation

This tariff defines how you will be charged for using the charging station:
	•	KWH:
You pay 10 CZK for every kilowatt-hour (kWh) of electricity used.
There are no free kWh — all energy is billed.
	•	CHARGING_MINUTE:
You pay 2 CZK per minute while the car is actively charging.
The first 20 minutes are free.
	•	PARKING_MINUTE:
You pay 3 CZK per minute if the car stays plugged in after charging is done.
There are no free minutes for parking.

---

### 2. Calculate Charging Price

**POST** `/tariffs/calculate`

#### Request body:

```json
{
  "tariffId": 1,
  "states": [
    {
      "dateTime": "2025-07-11T10:00:00Z",
      "kwh": 0.0,
      "state": "CHARGING"
    },
    {
      "dateTime": "2025-07-11T11:00:00Z",
      "kwh": 120.0,
      "state": "CHARGING"
    },
    {
      "dateTime": "2025-07-11T11:01:00Z",
      "kwh": 120.0,
      "state": "PARKING"
    },
    {
      "dateTime": "2025-07-11T13:01:00Z",
      "kwh": 120.0,
      "state": "PARKING"
    }
  ]
}
```

#### Example (Postman):
- Method: `POST`
- URL: `http://localhost:8080/tariffs/calculate`
- Body: `raw` → `JSON`

---

## How Pricing Works

The service calculates total cost using these components:

- **KWH**: Difference between first and last energy reading (`kwh`)
- **CHARGING_MINUTE**: Time when charger state is `CHARGING` (minus any free minutes)
- **PARKING_MINUTE**: Time when state is `PARKING` (no free units in example)

Free units are subtracted before billing.
