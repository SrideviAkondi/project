# Case study 

## Author
- [Venkata Sridevi Akondi](https://github.com/SrideviAkondi)

## myRetail Restful service

myRetail is a rapidly growing company with HQ in Richmond, VA and over 200 stores across the east coast. myRetail wants to make its internal data available to any number of client devices, from myRetail.com to native mobile apps.

To clone this repo use 

```git clone command ```

## Technologies used

Programming Language: Kotlin 1.5.31

Web Service Framework: Spring Boot 2.5.5

Unit Testing Framework: Junit 5.7.2

Mocking Framework: Mockito 3.9.0

Data Store (NoSQL): MongoDB 5.0.3

MongoDB compass (GUI to view the collection): 1.28.4

Apache Maven: 3.8.3

## Mongo DB database and collection

If this application is directly run in an IDE then ProductService's init method will load the product price data directly into the productPrice collection in product database.

Otherwise, use the following mongodb commands

```
    database=product
    
    mongodb host=localhost
    
    mongodb port=27017
    
    mongo collection name =productPrice
    
    use product
    
    db.productPrice.insert({"_id":13860428,"value":13.49,"currency":"USD"})
    
    db.productPrice.insert({"_id":54456119,"value":12.25,"currency":"USD"})
    
    db.productPrice.insert({"_id":13264003,"value":9.0,"currency":"USD"})
    
    db.productPrice.insert({"_id":12954218,"value":4.5,"currency":"USD"})
```

## Build, Test and Run application

cd project

Then run

mvn clean package

Then run the jar

java -jar target/project-0.0.1-SNAPSHOT.jar

Application will start running on port 8080


## myRetail Endpoints

### GET product by id

1) On a valid product id the response is a 200 OK

```
Input: GET localhost:8080/products/13860428
Response: 200 OK
    {
        "id": 13860428,
        "name": "The Big Lebowski (Blu-ray)",
        "current_price": {
        "value": 13.49,
        "currency_code": "USD"
        }
    }
```

2) The response is 404 Not Found with the error message and error response from External API is logged

```
Input: GET localhost:8080/products/1386042800
Response: 404 Not Found
    Product not found: External API does not contain the product information
```

### PUT product price by id

1) On a valid product id the response is a 200 OK and returns an updated product price information

```
Input: PUT localhost:8080/products/13860428
Request Body: 
    {
        "current_price": {
            "value": 13000000,
            "currency_code": "USD"
        }
    }
Response: 200 OK
    {
        "id": 13860428,
        "name": "The Big Lebowski (Blu-ray)",
        "current_price": {
            "value": 13000000,
            "currency_code": "USD"
        }
    }
```

2) The response is 404 Not Found with the error message

```
Input: PUT localhost:8080/products/1386042899
Request Body: 
    {
        "current_price": {
            "value": 13000000,
            "currency_code": "USD"
        }
    }
Response: 404 Not Found
    current_price not found for id: 1386042899
```
