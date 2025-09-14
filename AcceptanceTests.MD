# Room Reservation API — Acceptance test scenarios

> Assumes your Spring Boot app is running locally on `http://localhost:8080`.  
> You can change `BASE_URL` to point to another host/port.

```bash
# Set your base URL once (Linux/macOS)
export BASE_URL="http://localhost:8080"
# For Windows PowerShell use:
# $env:BASE_URL="http://localhost:8080"
```

---

## Scenario 1 — book (happy path + duplication check)

1) **Create a booking** for room `A-101` from `2025-07-01T08:00:00Z` to `2025-07-01T10:00:00Z`.  
   Expected: **201 Created**

```bash
curl -i -X POST "$BASE_URL/api/v1/room/book"   -H "accept: application/json"   -H "content-type: application/json"   -d '{
    "roomId": "A-101",
    "reservationStart": "2025-07-01T08:00:00Z",
    "reservationEnd": "2025-07-01T10:00:00Z"
  }'
```

2) **Try to book an overlapping reservation** for the same room `A-101` from `2025-07-01T09:00:00Z` to `2025-07-01T11:00:00Z`.  
   Expected: **409 Conflict**

```bash
curl -i -X POST "$BASE_URL/api/v1/room/book"   -H "accept: application/json"   -H "content-type: application/json"   -d '{
    "roomId": "A-101",
    "reservationStart": "2025-07-01T09:00:00Z",
    "reservationEnd": "2025-07-01T11:00:00Z"
  }'
```

---

## Scenario 2 — available (after prior booking attempt)

1) **Check availability** for the window `2025-07-01T08:00:00Z` to `2025-07-01T09:00:00Z`.  
   Expected: **204 No Content** (meaning: at least one room is available in that window based on your API semantics).

```bash
curl -i "$BASE_URL/api/v1/room/available?start=2025-07-01T08:00:00Z&end=2025-07-01T09:00:00Z"
```
