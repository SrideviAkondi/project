package com.retail.myRetail.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.retail.myRetail.model.persistent.ProductPrice
import java.math.BigInteger

/**
 * Model class used to define a product
 */
data class Product(
    var id: BigInteger?,
    val name: String?,
    @JsonProperty("current_price") val price: ProductPrice,
)
