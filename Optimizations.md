#Optimizations
Before we go ahead and discuss any alternative data format to save the restaurant opening hours, we should try to
pinpoint a few assumptions. These decisions will affect the schema.

I'll use the term "system" to refer to the server/application which will store the restaurant opening hours. This
system can either be a simple MS Excel file with different sheets storing the opening hours of different restaurants, OR
it can be a full-blown web application which saves the opening hours for each restaurant in some kind of persistent
storage like Postgres DB.

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
- To reduce the data size, we can remove days from the data when the restaurant is closed. The logic in Timekeeper 
  can be changed so that it is able to understand that missing days in the data means the restaurant is closed on
  that day and hence format the data accordingly.
  So, the initial data can be transformed into something like this
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
  saving the `type` as well into the data. Also, we can merge a pair of open close timings into just one JSON by just
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
  
### Optimization 3: Ease of parsing and reduce duplication
- While writing the Timekeeper service, I had to write a good amount of code in order to convert the request into an
  iterable form. The reason for this is that the input JSON has days of the week as top level keys which makes it
  difficult to walk through the keys, unless we do some messy stuff using Reflection and casting to a `DayOfWeek` enum.  
    
  In order to ease the parsing logic and applying of validation checks by Timekeeper, the input JSON can be changed to
  a format like this:-
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
  array, apply the business validations and other pre-processing steps and format the data much faster.