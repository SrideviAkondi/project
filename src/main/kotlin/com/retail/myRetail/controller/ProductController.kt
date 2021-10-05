package com.retail.myRetail.controller

import com.retail.myRetail.model.Product
import com.retail.myRetail.service.ProductService
import java.math.BigInteger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * [ProductController] handles all the methods related to [Product]
 */
@RestController
@RequestMapping("/products")
class
ProductController(
    private val productService: ProductService
) {
    /**
     * Returns [Product] by id
     * @param id used to uniquely identify a product
     * @return [Product] if one exists
     * @return error response on invalid id or when an error occurs while retrieving price information
     */
    @GetMapping("/{id}")
    fun get(@PathVariable id: String): Product? {
        return productService.get(id)
    }

    /**
     * Update [Product] price by id
     * @param id provide the product id
     * @param requestBody consists of [Product]. Cannot be null
     * @return updated product price if one exists
     * @return error response on invalid id, invalid price
     */
    @PutMapping("/{id}")
    fun updatePrice(@RequestBody requestBody: Product, @PathVariable id: String): Product {
        requestBody.id = BigInteger(id)
        return productService.updatePrice(requestBody)
    }
}