package com.megh.timekeeper.api

import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import javax.xml.bind.ValidationException

@RestControllerAdvice
class TimekeeperControllerAdvice {

    /** This method handles exceptions due to validation failures **/
    @ExceptionHandler(value = [ValidationException::class])
    fun handleValidationException(ex: ValidationException): ResponseEntity<HttpStatus> {
        return ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
    }


    /** This method handles exceptions due to invalid request **/
    @ExceptionHandler(value = [HttpMessageNotReadableException::class, HttpMediaTypeNotSupportedException::class])
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<HttpStatus> {
        if (ex.rootCause?.javaClass == MissingKotlinParameterException::class.java) {
            return ResponseEntity(HttpStatus.UNPROCESSABLE_ENTITY)
        }
        return ResponseEntity(HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [Exception::class])
    fun handleException(ex: Exception): ResponseEntity<HttpStatus> {
        return ResponseEntity(HttpStatus.I_AM_A_TEAPOT)
    }
}