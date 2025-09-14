# Room Reservation Service

## TODOs

- Rounding of input time
  - the smallest unit selected is minutes -> if the input time is not a whole minute, it would be appropriate to choose a rounding strategy. Currently, only lower units (seconds, milliseconds, etc.) are truncated.
- Make some hard-coded values in the product code configurable
- Room name validation (regex?)
- Possible separation between integration testing (future DB) and unit testing using service mocking