package com.retail.myRetail.model

import com.retail.myRetail.model.persistent.ProductPrice
import java.math.BigDecimal
import java.math.BigInteger

object ProductPriceMock {
    fun list(): MutableList<ProductPrice> {
        return mutableListOf(
            ProductPrice(BigInteger("1234567"), BigDecimal("12.25"), "USD"),
        )
    }
}