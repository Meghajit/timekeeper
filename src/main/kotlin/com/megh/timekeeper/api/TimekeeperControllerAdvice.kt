package com.megh.timekeeper.api

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime
import javax.xml.bind.ValidationException

@RestControllerAdvice
class TimekeeperControllerAdvice {

    /** This method handles exceptions due to validation failures **/
    @ExceptionHandler(value = [ValidationException::class])
    fun handleValidationException(ex: ValidationException): ResponseEntity<Any> {
        return ResponseEntity(object {
            val timestamp = LocalDateTime.now()
            val status = HttpStatus.UNPROCESSABLE_ENTITY
            val error = "Bad Request"
            val message = ex.message
        }, HttpStatus.BAD_REQUEST)
    }
}