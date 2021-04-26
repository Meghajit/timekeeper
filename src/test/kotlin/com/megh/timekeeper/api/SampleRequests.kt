package com.megh.timekeeper.api

fun getSampleRequest(): String {
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