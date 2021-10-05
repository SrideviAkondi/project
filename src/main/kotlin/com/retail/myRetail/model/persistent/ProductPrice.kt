package com.retail.myRetail.model.persistent

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Currency
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Mongo model used to define price of a product
 */
@Document
data class ProductPrice(
    // ignore the property while writing to json in the response
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Id
    val id: BigInteger?,
    val value: BigDecimal,
    @JsonProperty("currency_code") val currency: String,
) {
    init {
        // validate currency
        Currency.getInstance(currency)
    }
}