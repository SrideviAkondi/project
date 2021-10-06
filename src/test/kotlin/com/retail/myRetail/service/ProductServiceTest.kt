package com.retail.myRetail.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.mongodb.MongoException
import com.retail.myRetail.model.Product
import com.retail.myRetail.model.ProductMock
import com.retail.myRetail.model.ProductPriceMock
import com.retail.myRetail.model.persistent.ProductPrice
import com.retail.myRetail.repository.ProductPriceRepository
import java.math.BigDecimal
import java.math.BigInteger
import java.util.Optional
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito.`when`
import org.mockito.Mockito.any
import org.mockito.Mockito.anyList
import org.mockito.Mockito.anyString
import org.mockito.Mockito.eq
import org.mockito.Mockito.mock
import org.mockito.Mockito.never
import org.mockito.Mockito.reset
import org.mockito.Mockito.verify
import org.mockito.Mockito.verifyNoInteractions

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductServiceTest {
    // Constants
    private val mapper = jacksonObjectMapper()
    // private val appConfig: AppConfig = AppConfig("https://xyz.com/v3/pdp/tcin")
    private val productPrices = ProductPriceMock.list()
    private val expectedProduct = ProductMock.get()
    private val objectNode: ObjectNode = mapper.readTree(
        "{\n" +
                "    \"product\": {\n" +
                "        \"item\": {\n" +
                "            \"product_description\": {\n" +
                "                \"title\": \"abc\"\n" +
                "            }\n" +
                "        }\n" +
                "    }\n" +
                "}"
    ).deepCopy()

    // Dependencies
    // val restTemplate: RestTemplate = mock(RestTemplate::class.java)
    val productPriceRepository: ProductPriceRepository = mock(ProductPriceRepository::class.java)
    val objectMapper: ObjectMapper = mock(ObjectMapper::class.java)
    val productService =  ProductService(objectMapper, productPriceRepository)

    @BeforeEach
    fun setUp() {
        `when`(productPriceRepository.saveAll(anyList())).thenReturn(productPrices)
    }

    @AfterEach
    fun tearDown() {
        reset(objectMapper, productPriceRepository)
    }

    @Nested
    inner class Get {
        /*
        @Test
        fun `should throw HttpClientErrorException when product id is not found`() {
            `when`(restTemplate.getForObject(anyString(), eq(ObjectNode::class.java))).thenThrow(
                HttpClientErrorException(HttpStatus.NOT_FOUND)
            )
           assertThrows<HttpClientErrorException> {
                productService.get("1234567")
           }
        }
        */

        @Test
        fun `should return product when external API returns a valid resource`() {
            // `when`(restTemplate.getForObject(anyString(), eq(ObjectNode::class.java))).thenReturn(objectNode)
            `when`(productPriceRepository.findById(any())).thenReturn(Optional.of(productPrices.first()))
            `when`(objectMapper.readValue(anyString(), eq(ObjectNode::class.java))).thenReturn(objectNode)

            val product = productService.get("1234567")
            assertThat(product).isEqualTo(expectedProduct)
        }

        @Test
        fun `should throw MongoException when price for the product is not found in data store`() {
            // `when`(restTemplate.getForObject(anyString(), eq(ObjectNode::class.java))).thenReturn(objectNode)
            `when`(productPriceRepository.findById(any())).thenThrow(MongoException("current_price unavailable for id"))

            assertThrows<MongoException> {
                productService.get("123456")
            }
        }
    }

    @Nested
    inner class Update {
        @Test
        fun `should only update product's price in data store`() {
            `when`(productPriceRepository.findById(any())).thenReturn(Optional.of(productPrices.first()))
            `when`(productPriceRepository.save(any(ProductPrice::class.java))).thenReturn(ProductPrice(BigInteger("1234567"),BigDecimal("11.24"), "INR"))
            //`when`(restTemplate.getForObject(anyString(), eq(ObjectNode::class.java))).thenReturn(objectNode)
            `when`(objectMapper.readValue(anyString(), eq(ObjectNode::class.java))).thenReturn(objectNode)

            val updatedProduct = productService.updatePrice(
                Product(
                    BigInteger("1234567"),
                    null,
                    ProductPrice(null, BigDecimal("11.24"), "INR")
                )
            )
            assertThat(updatedProduct.price).isNotEqualTo(productPrices.first())
            assertThat(updatedProduct.name).isEqualTo("abc")
        }

        @Test
        fun `should throw MongoException when we update the price of a non-existing product in the data store`() {
            `when`(productPriceRepository.findById(any())).thenThrow(MongoException("current_price not found for id"))

            val ex = assertThrows<MongoException> {
                productService.updatePrice(
                    Product(BigInteger("1234567"),
                        "abc",
                        ProductPrice(BigInteger("1234567"), BigDecimal("11.24"), "USD")
                    )
                )
            }
            verify(productPriceRepository, never()).save(any(ProductPrice::class.java))
            verifyNoInteractions(objectMapper)
            // verifyNoInteractions(restTemplate)
            assertThat(ex.message).isEqualTo("current_price not found for id")
        }
    }
}