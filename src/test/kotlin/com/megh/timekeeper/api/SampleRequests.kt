package com.megh.timekeeper.api

fun getSampleHTTP200Request(): String {
    return """
        {
        "monday" : [{"type":"OPEN", "value":36000},{"type" : "CLOSE", "value" : 64800}],
        "tuesday": [{"type":"OPEN", "value":36000},{"type" : "CLOSE", "value" : 64800}],
        "wednesday": [{"type":"OPEN", "value":36000},{"type" : "CLOSE", "value" : 64800}],
        "thursday": [{"type":"OPEN", "value":36000},{"type" : "CLOSE", "value" : 64800}],
        "friday": [{"type":"OPEN", "value":36000},{"type" : "CLOSE", "value" : 64800}],
        "saturday": [{"type":"OPEN", "value":36000},{"type" : "CLOSE", "value" : 64800}],
        "sunday": [{"type":"OPEN", "value":36000},{"type" : "CLOSE", "value" : 64800}]
       }
    """.trimIndent()
}

fun getSampleMissingDaysOfWeekRequest(): String {
    return """
        {
        "monday" : [{"type":"OPEN", "value":36000},{"type" : "CLOSE", "value" : 64800}],
        "timbektu" : [{"type":"OPEN", "value":36000},{"type" : "CLOSE", "value" : 64800}],
        "tuesday": [{"type":"OPEN", "value":36000},{"type" : "CLOSE", "value" : 64800}],
        "sunday": [{"type":"OPEN", "value":36000},{"type" : "CLOSE", "value" : 64800}]
       }
    """.trimIndent()
}

fun getSampleNullOpeningHoursRequest(): String {
    return """
        {
        "monday" : null,
        "tuesday": [{"type":"OPEN", "value":36000},{"type" : "CLOSE", "value" : 64800}],
        "wednesday": [{"type":"OPEN", "value":36000},{"type" : "CLOSE", "value" : 64800}],
        "thursday": [{"type":"OPEN", "value":36000},{"type" : "CLOSE", "value" : 64800}],
        "friday": [{"type":"OPEN", "value":36000},{"type" : "CLOSE", "value" : 64800}],
        "saturday": [{"type":"OPEN", "value":36000},{"type" : "CLOSE", "value" : 64800}],
        "sunday": [{"type":"OPEN", "value":36000},{"type" : "CLOSE", "value" : 64800}]
       }
    """.trimIndent()
}

fun getSampleAtLeastOneNonEmptyOpeningHoursRequest(): String {
    return """
        {
        "monday" : [{"type":"OPEN", "value":36000},{"type" : "CLOSE", "value" : 64800}],
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