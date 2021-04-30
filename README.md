
# Timekeeper

Timekeeper is a simple REST API which accepts opening hours data as an input (JSON) and returns a more human readable version of the data formatted using 12-hour clock.

There is just one POST endpoint: `/v1/timings/format`

**Sample cURL request**
```bash  
curl --location --request POST 'localhost:8080/v1/timings/format' --header 'Content-Type: application/json' --data-raw '{ "monday": [], "tuesday": [{"type": "open","value": 36000},{"type": "close","value": 64800}], "wednesday": [], "thursday": [{"type": "open","value": 36000},{"type": "close","value": 64800}], "friday": [{"type": "open","value": 36000}], "saturday": [{"type": "close","value": 3600},{"type": "open","value": 36000}], "sunday": [{"type": "close","value": 3600},{"type": "open","value": 43200},{"type": "close","value": 75600}]}'  
```  
**Response**
```json  
{  
  "Monday": "12:20 AM - 2:13 AM, 10:13 PM - 12:20 AM",  
  "Tuesday": "1:40 AM - 3:20 AM",  
  "Wednesday": "Closed",  
  "Thursday": "Closed",  
  "Friday": "12:20 AM - 2:13 AM",  
  "Saturday": "Closed",  
  "Sunday": "Closed"  
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

The service is built only using Kotlin with SpringBoot. Apart from the starter libraries provided by [SpringBoot Initializer](https://start.spring.io/), there are no dependencies on third party frameworks or services.

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

This validation checks whether the restaurant timings provided in the request are valid, i:e, they are non-negative and within the range (0 to 86399).

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