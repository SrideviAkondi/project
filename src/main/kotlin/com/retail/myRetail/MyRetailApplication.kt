package com.retail.myRetail

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication

/**
 * Assessment Case Study 1
 *
 * @author Sridevi Akondi
 */
@SpringBootApplication
@ConfigurationPropertiesScan
class MyRetailApplication

fun main(args: Array<String>) {
    runApplication<MyRetailApplication>(*args)
}
