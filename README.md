# Payment Gateway

A unit testing assignment.

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites
1. Java 10
2. Gradle

### Building
Building the sources.

```
bash gradlew compilejava
```

Running the server.
```
bash gradlew bootRun
```

## Running the tests

Running all of the tests
```
bash gradlew tests
```

Running web tests
```
bash gradlew test --tests "com.cps3230.assignment.payment.webapp.PaymentProcessorWebTests"

```

### Coding style tests
Verifying the source follows the google code style.
```
bash gradlew checkstyleMain pmdMain
```


## Authors

* **Matthew Drago**

