
# Timekeeper

Timekeeper is a simple REST API which accepts restaurant opening hours' data as an input (JSON) and returns a more 
human-readable version of the data formatted using 12-hour clock.

There is just one POST endpoint: `/v1/timings/format`

**Sample cURL request**
```bash  
curl --location --request POST 'localhost:8080/v1/timings/format' --header 'Content-Type: application/json' --data-raw '{ "monday": [], "tuesday": [{"type": "open","value": 36000},{"type": "close","value": 64800}], "wednesday": [], "thursday": [{"type": "open","value": 36000},{"type": "close","value": 64800}], "friday": [{"type": "open","value": 36000}], "saturday": [{"type": "close","value": 3600},{"type": "open","value": 36000}], "sunday": [{"type": "close","value": 3600},{"type": "open","value": 43200},{"type": "close","value": 75600}]}'  
```  
**Response**
```json  
{
    "Monday": "Closed",
    "Tuesday": "10 AM - 6 PM",
    "Wednesday": "Closed",
    "Thursday": "10 AM - 6 PM",
    "Friday": "10 AM - 1 AM",
    "Saturday": "10 AM - 1 AM",
    "Sunday": "12 PM - 9 PM"
}  
```  
## Steps

### 1. Download dependencies and build
```bash
./gradlew clean build
```
### 2. Run unit tests
```bash
./gradlew clean test -i
```
### 3. Run
```bash
./gradlew bootRun
```
This will start the server at localhost:8080

## Tech Stack

The service is built using only Kotlin with SpringBoot. Apart from the starter libraries provided by [SpringBoot Initializer](https://start.spring.io/), there are no dependencies on third party frameworks or services.
Unit tests have been written with good code coverage.

## Tech Notes

1. The code is organized into 3 main packages:
    - **api**: This package contains the SpringBoot controller and ControllerAdvice along with classes used for deserialisation from JSON.
    - **domain**: This package contains the validator class. This class contains business rules which dictate the semantics of the input request.
    - **service**: This package contains the formatter service and classes used for serialisation to JSON.

2. The validator class has been built to check for various edge cases in order to block invalid requests at the API layer itself.  Whenever a validation failure occurs, a `ValidationException` is thrown with a specific message ID and handled by the ControllerAdvice to return an HTTP 422 status code along with the message.  The validations built in are mentioned in the below section.


## Validations

1. **TIMEKEEPER_VALIDATION_EXCEPTION_ALL_DAYS_TIMINGS_EMPTY**

This validation checks whether the restaurant is closed for all days of the week. The assumption made by me is that a restaurant is at-least open for one day in a week.

**Sample cURL request**
```bash  
curl --location --request POST 'localhost:8080/v1/timings/format' --header 'Content-Type: application/json' --data-raw '{
    "monday": [],
    "tuesday": [],
    "wednesday": [],
    "thursday": [],
    "friday": [],
    "saturday": [],
    "sunday": []
}' 
```  
**Response**
```json  
{
    "timestamp": "2021-04-30T21:21:19.128",
    "status": 422,
    "error": "UNPROCESSABLE_ENTITY",
    "message": "TIMEKEEPER_VALIDATION_EXCEPTION_ALL_DAYS_TIMINGS_EMPTY"
} 
```  

2. **TIMEKEEPER_VALIDATION_EXCEPTION_TIMINGS_OUT_OF_RANGE**

This validation checks whether the restaurant timings provided in the request are valid UNIX time, i:e, they are non-negative and within the range (0 to 86399).

**Sample cURL request**
```bash  
curl --location --request POST 'localhost:8080/v1/timings/format' --header 'Content-Type: application/json' --data-raw '{
    "monday": [],
    "tuesday": [{"type": "open","value": -36000},{"type": "close","value": 64800}],
    "wednesday": [],
    "thursday": [],
    "friday": [],
    "saturday": [],
    "sunday": []
}'
``` 
**Response**
```bash
{
    "timestamp": "2021-04-30T21:25:04.986",
    "status": 422,
    "error": "UNPROCESSABLE_ENTITY",
    "message": "TIMEKEEPER_VALIDATION_EXCEPTION_TIMINGS_OUT_OF_RANGE"
}
```
3. **TIMEKEEPER_VALIDATION_EXCEPTION_OPENING_HOURS_NOT_COMPLEMENTARY**

This checks if the opening closing times for the restaurant exists as pairs. That is, for every opening time there is a corresponding closing time.

**Sample cURL request**
```bash  
curl --location --request POST 'localhost:8080/v1/timings/format' --header 'Content-Type: application/json' --data-raw '      {
        "monday" : [],
        "tuesday": [],
        "wednesday": [],
        "thursday": [],
        "friday": [{"type":"open", "value":1200},{"type" : "close", "value" : 64800}],        
        "saturday": [{"type":"open", "value":1200}],
        "sunday": [{"type":"close", "value":8000},{"type" : "open", "value" : 10000}]
       }'
``` 
**Response**
```bash
{
    "timestamp": "2021-04-30T21:31:16.018",
    "status": 422,
    "error": "UNPROCESSABLE_ENTITY",
    "message": "TIMEKEEPER_VALIDATION_EXCEPTION_OPENING_HOURS_NOT_COMPLEMENTARY"
}
```

4. **TIMEKEEPER_VALIDATION_EXCEPTION_INVALID_CHRONOLOGICAL_ORDER_OF_OPENING_HOURS**

This checks if the opening closing times follow chronological order. That is, the restaurant should not have a closing time which is earlier than the opening time. This rule should be followed for both same-day-close as well as next-day-close timings.

**Sample cURL request**
```bash  
curl --location --request POST 'localhost:8080/v1/timings/format' --header 'Content-Type: application/json' --data-raw '        {
        "monday" : [{"type" : "close", "value" : 64800}, {"type" : "open", "value" : 1200}],
        "tuesday": [{"type":"open", "value":7000},{"type":"close", "value":2200}],
        "wednesday": [],
        "thursday": [],
        "friday": [],        
        "saturday": [],
        "sunday": []
       }'
``` 
**Response**
```bash
{
    "timestamp": "2021-04-30T21:38:21.529",
    "status": 422,
    "error": "UNPROCESSABLE_ENTITY",
    "message": "TIMEKEEPER_VALIDATION_EXCEPTION_INVALID_CHRONOLOGICAL_ORDER_OF_OPENING_HOURS"
}
```

# Optimizations
Before we go ahead and discuss any alternative data format to save the restaurant opening hours, we should try to
pinpoint a few assumptions. These decisions will affect the schema.

I'll use the term "system" to refer to the server/application which will store the restaurant opening hours.

## Assumption: The system is going to be a read heavy system
Consider the system is read heavy. The restaurant will update its opening hours less frequently, maybe once a week.
However, the data will be read quite frequently by some other system. Hence, the response time should be minimal.
We would ideally like to reduce the data size so that network calls take less time to transfer the payload. The data
should be easy to validate as well by the formatter service, Timekeeper.

Consider, this is the initial data we have
```json
    {
        "monday": [],
        "tuesday": [{"type": "open","value": "3600"},{"type": "close","value": "18000"}, {"type": "open","value": "40000"},{"type": "close","value": "80000"}],
        "wednesday": [{"type": "open","value": "3600"},{"type": "close","value": "18000"}, {"type": "open","value": "40000"},{"type": "close","value": "80000"}],
        "thursday": [{"type": "open","value": "64800"}],
        "friday": [{"type": "close","value": "36000"}],
        "saturday": [],
        "sunday": []
    }
```

### Optimization 1: Reducing the payload
- To reduce the payload, we can remove days when the restaurant is closed. The logic in Timekeeper
  can be changed so that it is able to understand that missing days in the data means the restaurant is closed on
  that day and hence format the data accordingly.

  With this, the initial data can be transformed into something like this:
    ```json
       {
        "tuesday": [{"type": "open","value": "3600"},{"type": "close","value": "18000"}, {"type": "open","value": "40000"},{"type": "close","value": "80000"}],
        "wednesday": [{"type": "open","value": "3600"},{"type": "close","value": "18000"}, {"type": "open","value": "40000"},{"type": "close","value": "80000"}],
        "thursday": [{"type": "open","value": "64800"}],
        "friday": [{"type": "close","value": "36000"}]
        }
    ```
### Optimization 2: Simplifying the data structure
- We know that there can only be 2 status of a restaurant: `open` and `close`. As such, there is not much point in
  saving the `type` as well into the data. Also, we can merge a pair of open close timings into just one JSON by
  mentioning the opening time in UNIX seconds and the duration for which it will remain open in seconds. The logic in Timekeeper
  can also be changed accordingly so that it is able to format the data accordingly.
  The closing time can be obtained as

  `closing time = (opening time + duration) % 86400`

  So the data obtained after **Optimization 1** above can be further optimized as :

    ```json
     {
        "tuesday": [{"opening-time": "3600", "duration": "14400"}, {"opening-time": "40000", "duration": "40000"}],
        "wednesday": [{"opening-time": "3600", "duration": "14400"}, {"opening-time": "40000", "duration": "40000"}],
        "thursday": [{"opening-time": "64800", "duration": "57600"}]
      }
    ```
  Point to note is, this optimization even takes care of timings when the restaurant opens on one day but closes
  the next day. For example, in the above, the restaurant opens at 64800 UNIX seconds
  on Thursday and closes on `(64800+57600) % 86400 = 36000` UNIX seconds on Friday. This is the same as denoted
  by the initial data.

### Optimization 3: Ease of parsing
- While writing the Timekeeper service, I had to write a good amount of code in order to convert the request into an
  iterable form. The reason for this is that the input JSON has days of the week as top level keys which makes it
  difficult to walk through the keys, unless we do some messy stuff using Reflection and casting to a `DayOfWeek` enum.

  In order to ease the parsing logic and applying of validation checks by Timekeeper, the data from `Optimization 2`
  above can be changed to a format like this:-
  ```json
     {"data": [{
                "day": "tuesday",
                "opening-hours": [{"opening-time": "3600", "duration": "14400"}, {"opening-time": "40000", "duration": "40000"}]
              },
              {
                "day": "wednesday",
                "opening-hours": [{"opening-time": "3600", "duration": "14400"}, {"opening-time": "40000", "duration": "40000"}]
              },
              {
               "day": "thursday",
               "opening-hours": [{"opening-time": "64800", "duration": "57600"}]
            }]
      }
  ```

  Since, the input JSON has just one key `data` with an array as its value, Timekeeper can now easily iterate through the
  request, apply the business validations and other pre-processing steps and format the data much faster.