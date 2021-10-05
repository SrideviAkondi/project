package com.retail.myRetail.service

import com.fasterxml.jackson.databind.node.ObjectNode
import com.mongodb.MongoException
import com.retail.myRetail.configuration.AppConfig
import com.retail.myRetail.model.Product
import com.retail.myRetail.model.persistent.ProductPrice
import com.retail.myRetail.repository.ProductPriceRepository
import java.math.BigDecimal
import java.math.BigInteger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

/**
 * Service class that provides methods related to product
 */
@Service
class ProductService(
    private val appConfig: AppConfig,
    private val restTemplate: RestTemplate,
    private val productPriceRepository: ProductPriceRepository,
) {
    private val log: Logger = LoggerFactory.getLogger(javaClass)

    // Persist price data in productPrice collection on initialize
    init {
        productPriceRepository.saveAll(
            mutableListOf(
                ProductPrice(BigInteger("13860428"), BigDecimal("13.49"), "USD"),
                ProductPrice(BigInteger("54456119"), BigDecimal("12.25"), "USD"),
                ProductPrice(BigInteger("13264003"), BigDecimal("9.0"), "USD"),
                ProductPrice(BigInteger("12954218"), BigDecimal("4.5"), "USD")
            )
        )
    }

    /**
     * Get [Product] by id
     *
     * @param productId represents path variable id. String and cannot be empty
     *
     * @throws HttpClientErrorException when external api fails to return a product name
     * @throws MongoException when product price info does not exist in data store
     */
    fun get(productId: String): Product {
        val productName = getProductName(productId)
        val id = productId.toBigInteger()
        val productPrice = getPrice(id)
        return Product(id, productName, productPrice)
    }

    /**
     * Update [Product] price by productId
     *
     * @param requestBody consists of product
     *
     * @throws HttpClientErrorException when external api fails to return a product name
     * @throws MongoException when product price info does not exist in data store
     */
    fun updatePrice(requestBody: Product): Product {
        // Validate if product exists in the data store
        getPrice(requestBody.id ?: throw IllegalArgumentException("id is invalid"))
        val productName = getProductName(requestBody.id.toString())
        if (!requestBody.name.isNullOrEmpty() && productName != requestBody.name) {
            log.warn("Cannot update name to ${requestBody.name}")
        }
        val updatedPrice = productPriceRepository.save(
            ProductPrice(
                requestBody.id,
                requestBody.price.value,
                requestBody.price.currency
            )
        )
        return Product(requestBody.id, productName, updatedPrice)
    }

    private fun getProductName(id: String): String {
        val url = UriComponentsBuilder
            .fromHttpUrl(appConfig.redSkyUri)
            .pathSegment(id)
            .queryParam(
                "excludes",
                "taxonomy,price,promotion,bulk_ship,rating_and_review_reviews,rating_and_review_statistics,question_answer_statistics"
            )
            .queryParam("key", "candidate")
            .toUriString()
        // Make HTTP call to external API and find the title of product
        val response = restTemplate.getForObject(url, ObjectNode::class.java)
        return response?.at("/product/item/product_description/title")?.asText() ?: "Unavailable"
    }

    private fun getPrice(productId: BigInteger): ProductPrice {
        val existingProduct = productPriceRepository.findById(productId)
        return existingProduct.orElseThrow {
            log.warn("product with id: $productId does not exist in data store")
            MongoException("current_price not found for id: $productId")
        }
    }
}