package com.retail.myRetail.configuration

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.ConstructorBinding

/**
 * Application Configuration properties
 */
@ConstructorBinding
@ConfigurationProperties(prefix = "application.app")
data class AppConfig(
    /**
     * External API
     */
    val redSkyUri: String,
)
