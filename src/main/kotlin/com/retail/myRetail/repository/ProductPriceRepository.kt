package com.retail.myRetail.repository

import com.retail.myRetail.model.persistent.ProductPrice
import java.math.BigInteger
import org.springframework.data.mongodb.repository.MongoRepository

/**
 * Interface to perform Mongo operations on [ProductPrice] collection
 */
interface ProductPriceRepository : MongoRepository<ProductPrice, BigInteger>
