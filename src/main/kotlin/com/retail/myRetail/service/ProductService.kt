package com.retail.myRetail.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.mongodb.MongoException
import com.retail.myRetail.model.Product
import com.retail.myRetail.model.persistent.ProductPrice
import com.retail.myRetail.repository.ProductPriceRepository
import java.math.BigDecimal
import java.math.BigInteger
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Service class that provides methods related to product
 */
@Service
class ProductService(
    /*
    keeping these in for future API call
    private val appConfig: AppConfig,
    private val restTemplate: RestTemplate,
    */
    private val objectMapper: ObjectMapper,
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
        /*
        redsky.target API is deprecated. So for now, I have mocked the product name response
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
        */
        val json = "{\"product\":{\"available_to_promise_network\":{\"product_id\":\"12954218\",\"id_type\":\"TCIN\",\"available_to_promise_quantity\":10.0,\"availability\":\"AVAILABLE\",\"online_available_to_promise_quantity\":10.0,\"stores_available_to_promise_quantity\":10.0,\"availability_status\":\"IN_STOCK\",\"multichannel_options\":[\"HOLD\",\"SHIPGUEST\",\"SCHEDULED_DELIVERY\"],\"is_infinite_inventory\":false,\"loyalty_availability_status\":\"IN_STOCK\",\"loyalty_purchase_start_date_time\":\"1970-01-01T00:00:00.000Z\",\"is_loyalty_purchase_enabled\":false,\"is_out_of_stock_in_all_store_locations\":false,\"is_out_of_stock_in_all_online_locations\":false},\"item\":{\"tcin\":\"12954218\",\"bundle_components\":{\"is_component\":true,\"related_master_dpci\":\"212-14-0029\",\"related_master_tcin\":\"81493156\",\"product_relationship_type\":\"Bundle Component\",\"product_relationship_type_code\":\"BNDLCMP\"},\"dpci\":\"212-14-0318\",\"upc\":\"021000658831\",\"product_description\":{\"title\":\"Kraft Macaroni &#38; Cheese Dinner Original - 7.25oz\",\"downstream_description\":\"KRAFT Macaroni and Cheese Original Flavor is a convenient boxed dinner. Kids and adults love the delicious taste of macaroni with cheesy goodness. Our 7.25 ounce mac and cheese dinner includes macaroni pasta and original flavor cheese sauce mix, so you just need milk and margarine or butter to make a tasty mac and cheese, Macaroni and cheese is a quick dinner kids love. With no artificial flavors, no artificial preservatives, and no artificial dyes, KRAFT Macaroni and Cheese is always a great family dinner choice. Preparing macaroni and cheese is a breeze. Just boil the pasta for 7-8 minutes, drain the water and stir in the cheese mix, milk and margarine or butter. Add our macaroni and cheese to your back to school supplies list. Now you can have your KRAFT Mac and Cheese and eat it too.\",\"bullet_description\":[\"<B>Contains:</B> Milk, Wheat\",\"<B>May Contain:</B> Eggs\",\"<B>Package Quantity:</B> 1\",\"<B>Package type:</B> Individual Item Multi-Serving\",\"<B>Net weight:</B> 7.25 Ounces\"],\"soft_bullets\":{\"title\":\"highlights\",\"bullets\":[\"One 7.25 oz. box of Kraft Original Macaroni & Cheese Dinner\",\"Add our macaroni and cheese to your back to school supplies list\",\"Try our easy to make mac and cheese with your family's busy school schedule\",\"Kraft Original Macaroni & Cheese is a convenient boxed dinner\",\"Box includes macaroni noodles and original flavor cheese sauce mix\",\"Kraft mac and cheese contains no artificial flavors, no artificial preservatives, and no artificial dyes\",\"One box makes about 3 servings\",\"Boxed macaroni and cheese is a quick and easy dinner\",\"Cheese sauce mix is individually sealed\",\"SNAP & EBT eligible food item\"]}},\"buy_url\":\"https://www.target.com/p/kraft-macaroni-38-cheese-dinner-original-7-25oz/-/A-12954218\",\"enrichment\":{\"images\":[{\"base_url\":\"https://target.scene7.com/is/image/Target/\",\"primary\":\"GUEST_9d8212f6-55bb-49be-892b-857d00f93514\",\"alternate_urls\":[\"GUEST_d2440c87-fad2-4589-8b71-3987651774c8\",\"GUEST_1296b715-0c42-4808-b9b2-93f234922c65\",\"GUEST_d591e3c5-4517-4279-969e-164aef8ac376\",\"GUEST_fca93b0b-df30-4c26-b897-8aa16c56b875\",\"GUEST_749b22aa-50fc-4d65-9645-60b9b42fa830\",\"GUEST_2d6d9a21-e304-49af-a71d-88cfde648d6b\",\"GUEST_d60ddc2b-7a94-4d2b-9450-7639d3d64466\",\"GUEST_6de75eba-4236-46a6-80f2-12fe6f5e32f1\"],\"content_labels\":[{\"image_url\":\"GUEST_d2440c87-fad2-4589-8b71-3987651774c8\"},{\"image_url\":\"GUEST_1296b715-0c42-4808-b9b2-93f234922c65\"},{\"image_url\":\"GUEST_9d8212f6-55bb-49be-892b-857d00f93514\"},{\"image_url\":\"GUEST_fca93b0b-df30-4c26-b897-8aa16c56b875\"},{\"image_url\":\"GUEST_d591e3c5-4517-4279-969e-164aef8ac376\"},{\"image_url\":\"GUEST_749b22aa-50fc-4d65-9645-60b9b42fa830\"},{\"image_url\":\"GUEST_2d6d9a21-e304-49af-a71d-88cfde648d6b\"},{\"image_url\":\"GUEST_d60ddc2b-7a94-4d2b-9450-7639d3d64466\"},{\"image_url\":\"GUEST_6de75eba-4236-46a6-80f2-12fe6f5e32f1\"}]}],\"nutrition_facts\":{\"ingredients\":\"ENRICHED MACARONI (WHEAT FLOUR, DURUM FLOUR, NIACIN, FERROUS SULFATE [IRON], THIAMIN MONONITRATE [VITAMIN B1], RIBOFLAVIN [VITAMIN B2], FOLIC ACID), CHEESE SAUCE MIX (WHEY, MILKFAT, SALT, MILK PROTEIN CONCENTRATE, SODIUM TRIPHOSPHATE, CONTAINS LESS THAN 2% OF TAPIOCA FLOUR, CITRIC ACID, LACTIC ACID, SODIUM PHOSPHATE, CALCIUM PHOSPHATE, WITH PAPRIKA, TURMERIC, AND ANNATTO ADDED FOR COLOR, ENZYMES, CHEESE CULTURE). \",\"warning\":\"CONTAINS: WHEAT, MILK.\",\"prepared_count\":2,\"value_prepared_list\":[{\"prepared_type\":0,\"description\":\"Per 2.5 oz\\ndry mix\",\"serving_size\":\"2.5\",\"serving_size_unit_of_measurement\":\"oz\",\"servings_per_container\":\"About 3\",\"nutrients\":[{\"prepared_type\":0,\"master_type\":0,\"name\":\"Calories\",\"quantity\":250.0,\"unit_of_measurement\":\"Cal\"},{\"prepared_type\":0,\"master_type\":0,\"name\":\"Total Fat\",\"quantity\":2.0,\"unit_of_measurement\":\"g\",\"percentage\":2.0},{\"prepared_type\":0,\"master_type\":0,\"name\":\"Saturated Fat\",\"quantity\":1.0,\"unit_of_measurement\":\"g\",\"percentage\":4.0},{\"prepared_type\":0,\"master_type\":0,\"name\":\"Trans Fat\",\"quantity\":0.0,\"unit_of_measurement\":\"g\"},{\"prepared_type\":0,\"master_type\":0,\"name\":\"Cholesterol\",\"quantity\":5.0,\"unit_of_measurement\":\"mg\",\"percentage\":2.0},{\"prepared_type\":0,\"master_type\":0,\"name\":\"Sodium\",\"quantity\":560.0,\"unit_of_measurement\":\"mg\",\"percentage\":24.0},{\"prepared_type\":0,\"master_type\":0,\"name\":\"Total Carbohydrate\",\"quantity\":49.0,\"unit_of_measurement\":\"g\",\"percentage\":18.0},{\"prepared_type\":0,\"master_type\":0,\"name\":\"Dietary Fiber\",\"quantity\":2.0,\"unit_of_measurement\":\"g\",\"percentage\":8.0},{\"prepared_type\":0,\"master_type\":0,\"name\":\"Sugars\",\"quantity\":9.0,\"unit_of_measurement\":\"g\"},{\"prepared_type\":0,\"master_type\":0,\"name\":\"Added Sugars\",\"quantity\":0.0,\"unit_of_measurement\":\"g\",\"percentage\":0.0},{\"prepared_type\":0,\"master_type\":0,\"name\":\"Protein\",\"quantity\":9.0,\"unit_of_measurement\":\"g\"},{\"prepared_type\":0,\"master_type\":0,\"name\":\"Vitamin D\",\"quantity\":0.0,\"unit_of_measurement\":\"mcg\",\"percentage\":0.0},{\"prepared_type\":0,\"master_type\":0,\"name\":\"Calcium\",\"quantity\":110.0,\"unit_of_measurement\":\"mg\",\"percentage\":8.0},{\"prepared_type\":0,\"master_type\":0,\"name\":\"Iron\",\"quantity\":2.5,\"unit_of_measurement\":\"mg\",\"percentage\":15.0},{\"prepared_type\":0,\"master_type\":0,\"name\":\"Potassium\",\"quantity\":330.0,\"unit_of_measurement\":\"mg\",\"percentage\":8.0}]},{\"prepared_type\":1,\"description\":\"Per 1 cup\\nprepared*\",\"nutrients\":[{\"prepared_type\":1,\"master_type\":0,\"name\":\"Calories\",\"quantity\":350.0,\"unit_of_measurement\":\"Cal\"},{\"prepared_type\":1,\"master_type\":0,\"name\":\"Total Fat\",\"quantity\":11.0,\"unit_of_measurement\":\"g\",\"percentage\":15.0},{\"prepared_type\":1,\"master_type\":0,\"name\":\"Saturated Fat\",\"quantity\":4.0,\"unit_of_measurement\":\"g\",\"percentage\":19.0},{\"prepared_type\":1,\"master_type\":0,\"name\":\"Trans Fat\",\"quantity\":0.0,\"unit_of_measurement\":\"g\"},{\"prepared_type\":1,\"master_type\":0,\"name\":\"Cholesterol\",\"quantity\":10.0,\"unit_of_measurement\":\"mg\",\"percentage\":3.0},{\"prepared_type\":1,\"master_type\":0,\"name\":\"Sodium\",\"quantity\":710.0,\"unit_of_measurement\":\"mg\",\"percentage\":31.0},{\"prepared_type\":1,\"master_type\":0,\"name\":\"Total Carbohydrate\",\"quantity\":50.0,\"unit_of_measurement\":\"g\",\"percentage\":18.0},{\"prepared_type\":1,\"master_type\":0,\"name\":\"Dietary Fiber\",\"quantity\":2.0,\"unit_of_measurement\":\"g\",\"percentage\":8.0},{\"prepared_type\":1,\"master_type\":0,\"name\":\"Sugars\",\"quantity\":10.0,\"unit_of_measurement\":\"g\"},{\"prepared_type\":1,\"master_type\":0,\"name\":\"Added Sugars\",\"quantity\":0.0,\"unit_of_measurement\":\"g\",\"percentage\":0.0},{\"prepared_type\":1,\"master_type\":0,\"name\":\"Protein\",\"quantity\":10.0,\"unit_of_measurement\":\"g\"},{\"prepared_type\":1,\"master_type\":0,\"name\":\"Vitamin D\",\"quantity\":0.0,\"unit_of_measurement\":\"mcg\",\"percentage\":0.0},{\"prepared_type\":1,\"master_type\":0,\"name\":\"Calcium\",\"quantity\":130.0,\"unit_of_measurement\":\"mg\",\"percentage\":10.0},{\"prepared_type\":1,\"master_type\":0,\"name\":\"Iron\",\"quantity\":2.5,\"unit_of_measurement\":\"mg\",\"percentage\":15.0},{\"prepared_type\":1,\"master_type\":0,\"name\":\"Potassium\",\"quantity\":370.0,\"unit_of_measurement\":\"mg\",\"percentage\":8.0}]}],\"nutrition_label_type_code\":\"LEGACY_FORMAT\"}},\"return_method\":\"This item can be returned to any Target store or Target.com.\",\"handling\":{},\"recall_compliance\":{\"is_product_recalled\":false},\"tax_category\":{\"tax_class\":\"F\",\"tax_code_id\":9010,\"tax_code\":\"09010\"},\"display_option\":{\"is_size_chart\":false},\"fulfillment\":{\"is_po_box_prohibited\":false,\"box_percent_filled_by_volume\":0.55,\"box_percent_filled_by_weight\":1.34,\"box_percent_filled_display\":1.34},\"package_dimensions\":{\"weight\":\"0.565\",\"weight_unit_of_measure\":\"POUND\",\"width\":\"3.5\",\"depth\":\"7.25\",\"height\":\"1.3\",\"dimension_unit_of_measure\":\"INCH\"},\"environmental_segmentation\":{\"is_hazardous_material\":false,\"has_lead_disclosure\":false},\"manufacturer\":{},\"product_vendors\":[{\"id\":\"2965716\",\"manufacturer_style\":\"21000\",\"vendor_name\":\"KRAFT FOODS GROUP INC\"},{\"id\":\"1213676\",\"manufacturer_style\":\"2100001871\",\"vendor_name\":\"STORE ONLY DUMMY VENDOR\"}],\"product_classification\":{\"product_type\":\"109420\",\"product_type_name\":\"GROCERY\",\"item_type_name\":\"prepared foods\",\"item_type\":{\"type\":439602,\"name\":\"Prepared Meals and Sides\"}},\"product_brand\":{\"brand\":\"Kraft\",\"manufacturer_brand\":\"Kraft\",\"facet_id\":\"5scoh\"},\"item_state\":\"READY_FOR_LAUNCH\",\"specifications\":[],\"attributes\":{\"gift_wrapable\":\"Y\",\"has_prop65\":\"N\",\"is_hazmat\":\"N\",\"manufacturing_brand\":\"Kraft\",\"max_order_qty\":10,\"merch_class\":\"MEAL ESSENTIALS\",\"merch_classid\":212,\"merch_subclass\":14,\"return_method\":\"This item can be returned to any Target store or Target.com.\"},\"country_of_origin\":\"US\",\"relationship_type_code\":\"Stand Alone\",\"subscription_eligible\":false,\"disclaimer\":{\"type\":\"Grocery Disclaimer\",\"description\":\"Content on this site is for reference purposes only.  Target does not represent or warrant that the nutrition, ingredient, allergen and other product information on our Web or Mobile sites are accurate or complete, since this information comes from the product manufacturers.  On occasion, manufacturers may improve or change their product formulas and update their labels.  We recommend that you do not rely solely on the information presented on our Web or Mobile sites and that you review the product's label or contact the manufacturer directly if you have specific product concerns or questions.  If you have specific healthcare concerns or questions about the products displayed, please contact your licensed healthcare professional for advice or answers. Any additional pictures are suggested servings only.\"},\"ribbons\":[],\"tags\":[],\"regulatory_data\":[{\"identifier\":\"NUTRITION_FACTS_FLAG\",\"url\":\"https://img1.targetimg1.com/wcsstore/TargetSAS/html/p/nutrition/12/95/42/12954218_NutritionFacts.html\"}],\"estore_item_status_code\":\"A\",\"is_cart_add_on\":true,\"eligibility_rules\":{\"add_on\":{\"is_active\":true},\"inventory_notification_to_guest_excluded\":{\"is_active\":true},\"hold\":{\"is_active\":true},\"ship_to_guest\":{\"is_active\":true},\"scheduled_delivery\":{\"is_active\":true}},\"is_proposition_65\":false,\"return_policies\":{\"user\":\"Regular Guest\",\"policyDays\":\"90\",\"guestMessage\":\"This item must be returned within 90 days of the in-store purchase, ship date, or online order pickup. See return policy for details.\"},\"packaging\":{\"is_retail_ticketed\":false}}}}"
        val mockObjectNode = objectMapper.readValue(json, ObjectNode::class.java)
        return mockObjectNode?.at("/product/item/product_description/title")?.asText() ?: "Unavailable"
    }

    private fun getPrice(productId: BigInteger): ProductPrice {
        val existingProduct = productPriceRepository.findById(productId)
        return existingProduct.orElseThrow {
            log.warn("product with id: $productId does not exist in data store")
            MongoException("current_price not found for id: $productId")
        }
    }
}