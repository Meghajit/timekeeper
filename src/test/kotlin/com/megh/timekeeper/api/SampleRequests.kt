package com.megh.timekeeper.api

fun getSampleHTTP200Request(): String {
    return """
        {
        "monday" : [{"type":"open", "value":1200},{"type" : "close", "value" : 8000}],
        "tuesday": [{"type":"open", "value":1200},{"type" : "close", "value" : 8000}],
        "wednesday": [{"type":"open", "value":1200},{"type" : "close", "value" : 8000}],
        "thursday": [{"type":"open", "value":1200},{"type" : "close", "value" : 8000}],
        "friday": [{"type":"open", "value":1200},{"type" : "close", "value" : 8000}],
        "saturday": [{"type":"open", "value":1200},{"type" : "close", "value" : 8000}],
        "sunday": [{"type":"open", "value":1200},{"type" : "close", "value" : 8000}]
       }
    """.trimIndent()
}

fun getSampleMissingDaysOfWeekRequest(): String {
    return """
        {
        "monday" : [{"type":"open", "value":1200},{"type" : "close", "value" : 64800}],
        "timbektu" : [{"type":"open", "value":1200},{"type" : "close", "value" : 64800}],
        "tuesday": [{"type":"open", "value":1200},{"type" : "close", "value" : 64800}],
        "sunday": [{"type":"open", "value":1200},{"type" : "close", "value" : 64800}]
       }
    """.trimIndent()
}

fun getSampleNullOpeningHoursRequest(): String {
    return """
        {
        "monday" : null,
        "tuesday": [{"type":"open", "value":1200},{"type" : "close", "value" : 64800}],
        "wednesday": [{"type":"open", "value":1200},{"type" : "close", "value" : 64800}],
        "thursday": [{"type":"open", "value":1200},{"type" : "close", "value" : 64800}],
        "friday": [{"type":"open", "value":1200},{"type" : "close", "value" : 64800}],
        "saturday": [{"type":"open", "value":1200},{"type" : "close", "value" : 64800}],
        "sunday": [{"type":"open", "value":1200},{"type" : "close", "value" : 64800}]
       }
    """.trimIndent()
}

fun getSampleAtLeastOneNonEmptyOpeningHoursRequest(): String {
    return """
        {
        "monday" : [{"type":"open", "value":2343},{"type" : "close", "value" : 4500}],
        "tuesday": [],
        "wednesday": [],
        "thursday": [],
        "friday": [],
        "saturday": [],
        "sunday": []
       }
    """.trimIndent()
}

fun getSampleAllEmptyOpeningHoursRequest(): String {
    return """
        {
        "monday" : [],
        "tuesday": [],
        "wednesday": [],
        "thursday": [],
        "friday": [],
        "saturday": [],
        "sunday": []
       }
    """.trimIndent()
}

fun getSampleInvalidOpeningHoursRequest(): String {
    return """
        {
        "monday" : [],
        "tuesday": [],
        "wednesday": [],
        "thursday": [],
        "friday": [{"type":"open", "value":-45},{"type" : "close", "value" : 64800}],
        "saturday": [{"type":"open", "value":1200},{"type" : "close", "value" : 86400}],
        "sunday": []
       }
    """.trimIndent()
}

fun getSampleUnParsableOpeningHoursRequest(): String {
    return """
        {
        "monday" : [],
        "tuesday": [],
        "wednesday": [],
        "thursday": [],
        "friday": [{"type":"open", "value":1200},{"type" : "close", "value" : 64800}],
        "saturday": [{"type":"open", "value":"hello"},{"type" : "close", "value" : "this should have been an integer !"}],
        "sunday": [{"type":"open", "value":1200},{"type" : "close", "value" : 8000}]
       }
    """.trimIndent()
}

fun getSampleUnParsableOpeningHoursTypeRequest(): String {
    return """
        {
        "monday" : [],
        "tuesday": [],
        "wednesday": [],
        "thursday": [],
        "friday": [{"type":"open", "value":1200},{"type" : "close", "value" : 64800}],
        "saturday": [{"type":"THIS IS NOT A VALID TYPE", "value":1200},{"type" : "close", "value" : 8000}],
        "sunday": [{"type":"open", "value":1200},{"type" : "close", "value" : 8000}]
       }
    """.trimIndent()
}

fun getSampleNotPairedOpeningHoursRequest(): String {
    return """
        {
        "monday" : [],
        "tuesday": [],
        "wednesday": [],
        "thursday": [],
        "friday": [{"type":"open", "value":1200},{"type" : "close", "value" : 64800}],        
        "saturday": [{"type":"open", "value":1200}],
        "sunday": [{"type":"close", "value":8000},{"type" : "open", "value" : 10000}]
       }
    """.trimIndent()
}

fun getSampleIncorrectChronologicalOrderOfOpenAndCloseTimingsOnSameDayRequest(): String {
    return """
        {
        "monday" : [{"type" : "close", "value" : 64800}, {"type" : "open", "value" : 1200}],
        "tuesday": [{"type":"open", "value":7000},{"type":"close", "value":2200}],
        "wednesday": [],
        "thursday": [],
        "friday": [],        
        "saturday": [],
        "sunday": []
       }
    """.trimIndent()
}