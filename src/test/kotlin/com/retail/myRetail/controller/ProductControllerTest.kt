package com.retail.myRetail.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.retail.myRetail.model.Product
import com.retail.myRetail.model.ProductMock
import com.retail.myRetail.model.persistent.ProductPrice
import com.retail.myRetail.service.ProductService
import java.math.BigDecimal
import java.math.BigInteger
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.Mockito.anyString
import org.mockito.Mockito.mock
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

@SpringBootTest
class ProductControllerTest {
    // Constants
    val mockProduct = ProductMock.get()
    val mapper = jacksonObjectMapper()

    // Dependencies
    val mockProductService: ProductService = mock(ProductService::class.java)
    val productController = ProductController(mockProductService)

    @Nested
    inner class Get {
        @Test
        fun `should get product on valid id`() {
            `when`(mockProductService.get(anyString())).thenReturn(mockProduct)
            val product = productController.get("1234567")
            assertThat(mapper.writeValueAsString(product)).isEqualTo("{\"id\":1234567,\"name\":\"abc\",\"current_price\":{\"value\":12.25,\"currency_code\":\"USD\"}}")
        }

        @Test
        fun `should return 404 when id is improperly formatted`() {
            `when`(mockProductService.get(anyString())).thenThrow(ResponseStatusException(HttpStatus.NOT_FOUND))
            val ex = assertThrows<ResponseStatusException> {
                productController.get("999999999999999999999")
            }
            assertThat(ex.status.value()).isEqualTo(404)
        }
    }

    @Nested
    inner class Update {
        @Test
        fun `should return 400 response on invalid currency_code`() {
            assertThrows<IllegalArgumentException> {
                productController.updatePrice(
                    Product(BigInteger("1234567"), "abc", ProductPrice(BigInteger("1234567"), BigDecimal("11.25"), "US")),
                    "1234567"
                )
            }
        }
    }
}