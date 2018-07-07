# Transaction Service

	Transaction service can hold transactions and provide last minute aggregate transaction details
	
## Assumptions

	Transactions with future timestamp will be considered invalid
	Any transaction with same timestamp and same amount is considered duplicate transaction

## Project details

	This is a stand alone project built on spring boot, It uses maven for dependency resolution and building as well as starting the project.

## Commands

`mvn clean install` can be used to clean and build the project
`mvn test` to run test cases
`mvn spring-boot:run` can be used to build and start the project with embedded tomcat
`mvn package` can be used to generate a compiled jar file which can be run with below command
`java -jar TransactionService-0.0.1-SNAPSHOT.jar`

## info

	after starting the project, it can be reached by http://localhost:8099
	Default port 8099 can be modified in application.properties file

## APIs
	
### Adding transactions
	
	POST http://localhost:8099/transactions
	
	{
		"amount": 36.9,
		"timestamp": 1540994079431
	}
#### curl command

`curl -X POST -H 'Content-Type: application/json' -i http://localhost:8099/transactions --data '{
"amount": 36.9,
"timestamp": 1540994079431
}'`
	
### fetching aggregate statistics

	GET http://localhost:8099/statistics

#### curl command

`curl -X GET -i http://localhost:8099/statistics`

