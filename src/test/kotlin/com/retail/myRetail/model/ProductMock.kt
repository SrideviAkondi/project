package com.retail.myRetail.model

import com.retail.myRetail.model.persistent.ProductPrice
import java.math.BigDecimal
import java.math.BigInteger

object ProductMock {
    fun get(): Product {
        val id = BigInteger("1234567")
        return Product(id, "abc", ProductPrice(id, BigDecimal("12.25"), "USD"))
    }
}