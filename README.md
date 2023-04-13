# Ewallet application

#### Requirements

- A customer should be able to get a wallet after providing his/her personal details (API)
- A customer should be able to withdraw and deposit funds (API)
- Single transaction limit is 2000 EUR
- Daily withdrawal limit is 5000 EUR
- Transactions over 10'000 EUR should be flagged as suspicious
- Data should be stored for audit

#### Technology stack

- Java 17
- Spring
- Maven 
- H2 memory database
- JPA with Hibernate
- JUnit, Mockito
- Lombok
- ...


#### Description of implementation

The implementation was done in one microservice with the standard layered structure of it: controller, service and DAO. Microservice has 5 different endpoints for different purposes: to save customer, to get customer, to deposit funds, to withdraw funds and to make funds transaction. Each controller is responsible for communication with external applications. Each service is responsible for business logic. Each DAO is responsible to retrieve data from database.

#### Launch requirements

- Internet
- JDK - to run Java application
- Maven - to download dependencies

#### How to launch

- Launch `EwalletApplication`

#### Application access links

- Database access link: http://localhost:8080/h2-console (username: sa, password: password)
- Postman collections: https://github.com/mantas462/ewallet-postman-collections for testing purposes
- Access links of endpoints:
  - Create customer: http://localhost:8080/api/v1/customer
  - Get customer: http://localhost:8080/api/v1/customer/{uuid}
  - Deposit funds: http://localhost:8080/api/v1/ewallet/{uuid}/deposit
  - Withdraw funds: http://localhost:8080/api/v1/ewallet/{uuid}/withdrawal
  - Make a transaction: http://localhost:8080/api/v1/ewallet/{uuid}/transaction

#### Improvements needed for application

- Finish all the tests. All classes for tests are created and needed tests but they are not fully completed. Needed tests are marked with `TODO`
- Make endpoints secure and not accessible by everyone
- Use cache, e.g. CaffeineCache for some of the cases, e.g. daily withdraw limit
- Finish nice to have requirements
- Add real database
- Add logging functionality to the classes
- Complete postman collections with all cases
- Remove Spring Data JPA and have querying API like dslContext
- Implement pessimistic or optimistic lock when fetching account balances
- Add Swagger-UI
- Fix response handler for exceptions
- ...
