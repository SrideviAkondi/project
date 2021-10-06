# Case study 

## Author
- [Venkata Sridevi Akondi](https://github.com/SrideviAkondi)

## myRetail Restful service

myRetail is a rapidly growing company with HQ in Richmond, VA and over 200 stores across the east coast. myRetail wants to make its internal data available to any number of client devices, from myRetail.com to native mobile apps.

To clone this repo use 

```git clone "https://github.com/SrideviAkondi/project" ```

## Technologies used

Programming Language: Kotlin 1.5.31

Web Service Framework: Spring Boot 2.5.5

Unit Testing Framework: Junit 5.7.2

Mocking Framework: Mockito 3.9.0

Data Store (NoSQL): MongoDB 5.0.3

MongoDB compass (GUI to view the collection): 1.28.4

Apache Maven: 3.8.3

Java 1.8.0_211

Postman API testing tool

IDE: IntelliJ IDEA

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

```
cd project
```

Then run

```
mvn clean package
```

<img width="1672" alt="Clean package" src="https://user-images.githubusercontent.com/24579657/136049884-5617690c-a011-48fa-9673-c93927f8ac35.png">

Then run the jar

```
java -jar target/project-0.0.1-SNAPSHOT.jar
```

<img width="1672" alt="Run the jar" src="https://user-images.githubusercontent.com/24579657/136049941-ccc2111f-5173-499b-845b-ee5394a5fab3.png">

Run the unit tests in an IDE

<img width="1658" alt="Unit Tests passed" src="https://user-images.githubusercontent.com/24579657/136050027-957902ef-6731-4d6c-9f76-713546d82aa7.png">


Application will start running on port 8080

The mongo collections before and after App start

<img width="1671" alt="Before App start" src="https://user-images.githubusercontent.com/24579657/136050153-65d56c25-8b3f-459a-808d-615acf904042.png">

<img width="1671" alt="Collection created after App start" src="https://user-images.githubusercontent.com/24579657/136050172-5b642edd-1de0-4827-adab-d411fe0cdd8e.png">


## myRetail Endpoints

### GET product by id

Response: 

![Get Product by Id](https://user-images.githubusercontent.com/24579657/136066882-246d77dc-a85a-4947-b42c-157fd6107f12.JPG)

Other Examples: 

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

Response: 

![Put Product price By Id](https://user-images.githubusercontent.com/24579657/136066945-bccc5c5e-01ac-4ace-ba34-9bfe613a2a0c.JPG)

Other Examples: 

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

#### If the external API is down/not responding then the following 410 error will be returned

![image](https://user-images.githubusercontent.com/24579657/136055576-9fadcf86-f036-4db2-80e5-b4be81916d5a.png)


#### Note:

If there is a Socket Exception and Mongo is unable to start; then that could possibly be due to mongo not being properly set up/discovered in the system:

Use the following commands then 

```
mkdir $HOME/data
$ mongod --dbpath $HOME/data
```
