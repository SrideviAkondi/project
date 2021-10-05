package com.retail.myRetail.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

/**
 * Define a bean of type RestTemplate
 */
@Configuration
class RestTemplateConfiguration {
    @Bean
    fun getRestTemplate(): RestTemplate {
        return RestTemplate()
    }
}