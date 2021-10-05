package com.retail.myRetail.exception

import com.mongodb.MongoException
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

/**
 * Handle exceptions globally
 */
@ControllerAdvice
class AppExceptionHandler : ResponseEntityExceptionHandler() {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(HttpClientErrorException::class)
    fun handleHttpClientErrorException(e: HttpClientErrorException): ResponseEntity<Any> {
        log.warn(e.message)
        return ResponseEntity("Product not found: External API does not contain the product information", e.statusCode)
    }

    @ExceptionHandler(MongoException::class)
    fun handleMongoException(e: MongoException): ResponseEntity<Any> {
        return ResponseEntity(e.message, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<Any> {
        return ResponseEntity(e.message, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
