# Assignment

Using your favorite Go or Java framework / libraries build a service, that will accept a request with text parameter on
input. It will return maximum of 5 books and maximum of 5 albums that are related to the input term. The response
elements will only contain title, authors(/artists) and information whether it's a book or an album. For albums please
use the iTunes API. For books please use Google Books API. Sort the result by title alphabetically. Make sure the
software is production-ready from resilience, stability and performance point of view. The stability of the downstream
service may not be affected by the stability of the upstream services. Results originating from one upstream service (
and its stability /performance) may not affect the results originating from the other upstream service.

Make sure the service:

- Your service needs to respond within a minute;
- is self-documenting
- exposes metrics on response times for upstream services
- exposes health check
- Limit of results on upstream services must be configurable per environment and preconfigured to 5.

Bonus points:

- Think about resilience
- Think about concurrency

Please document how we can run it. Please shortly document your justification of technology / mechanism choice.

# Book and Album Service

This repository contains a service built using Kotlin and Spring Boot 3.1.1 with Web Flux that accepts a text parameter
as input and returns a maximum of 5 books and 5 albums related to the input term. The service utilizes the Google Books
API for books and the ITunes API for albums. The results are sorted alphabetically by title.
The service is designed with clean architecture and domain-driven design principles, providing a clear separation of
concerns between API models, domain models, and infrastructure models.

## Architecture and Design Choices

## Language and Framework

The service is implemented using Kotlin, a modern and concise programming language that runs on the Java Virtual
Machine (JVM). Kotlin combines functional and object-oriented programming paradigms and provides strong static typing
and null-safety features. Spring Boot with Web Flux is used as the framework for building reactive microservices,
leveraging non-blocking I/O to achieve high concurrency and scalability.

## Libraries and Dependencies

The service utilizes several libraries to enhance its functionality and maintain code quality:

- **Resilience4j**: A fault tolerance library that provides various resilience patterns such as circuit breakers, rate
  limiters, bulk head, and retries. Resilience4j helps ensure the stability of the service even when the upstream
  services are
  unstable.
- **Prometheus and Grafana**: Prometheus is used for metrics collection and monitoring, while Grafana is used for data
  visualization and analysis. This combination enables tracking of response times for upstream services and provides
  insights into the performance of the service.
- **Swagger**: Swagger is integrated with Spring Boot to generate API documentation. It provides a user-friendly
  interface to
  explore and interact with the service's endpoints.
- **Micrometer**: Micrometer is used for application metrics instrumentation. It integrates with Spring Boot Actuator
  and
  allows exposing custom metrics, such as response times, error rates, and throughput.

## How to Run the Application

To run the application, please follow these steps:

1. Clone the repository and ensure that Docker is installed and running on your machine.
2. Navigate to the scripts folder in project directory by executing the following command in your terminal or command
   prompt:

```bash
cd scripts
```

3. Run the following script to start the application using Docker Compose:

```bash
sh start.sh
```

4. Run the following script to down the application using Docker Down:

```bash
sh stop.sh
```

## API Documentation

The API is thoroughly documented using OpenAPI. The documentation provides details about endpoint, including
request and response formats, parameters, and example usage. To access the API documentation, please follow these steps:

1. Start the application by following the instructions mentioned in the previous section.
2. To access Swagger UI, open your web browser and go to [Swagger UI](http://localhost:8080/swagger-ui.html).
3. The API documentation page will be displayed, allowing you to explore the available endpoints and interact with the
   API.

## Testing

The application includes both unit tests and integration tests to ensure the correctness of the implemented
functionality. The tests cover different scenarios and edge cases to validate the behavior of the code.
To run the tests, execute the following command:

```bash
cd scripts
```

```bash
sh test.sh
```

## Monitoring with Prometheus and Grafana

To monitor the service using Prometheus and Grafana, please follow these steps:

1. Ensure that the service is running.
2. Open your web browser and go to [Grafana](http://localhost:3000).
3. Open Dashboards.

## Load Testing and Chaos Monkey

To generate load and produce metrics for monitoring purposes, you can use the simulate.sh and chaos-monkey.sh scripts
provided in the project. These scripts can simulate traffic and introduce failures to observe how the service behaves
under different conditions.

To run the load testing script:

```bash
cd scripts
```

The simulate.sh script iterates 1000 times to perform sequential random calls to the service.

```bash
sh simulate.sh
```

The chaos-monkey.sh script iterates 20 times to perform 5 concurrent calls to the service, simulating a chaos scenario.

```bash
sh chaos-monkey.sh 
```

## Example Dashboards

Here are some example dashboards that can be used to visualize and analyze the metrics collected by Prometheus and
displayed in Grafana:

![circuit-breaker2.png](docs%2Fcircuit-breaker2.png)
![api.png](docs%2Fapi.png)
![circuit-breaker1.png](docs%2Fcircuit-breaker1.png)
![circuit-breaker3.png](docs%2Fcircuit-breaker3.png)
![circuit-breaker-half-open.png](docs%2Fcircuit-breaker-half-open.png)
![circuit-breaker-half-open2.png](docs%2Fcircuit-breaker-half-open2.png)
![retry-bulkhead.png](docs%2Fretry-bulkhead.png)
![spring-boot-observability.png](docs%2Fspring-boot-observability.png)

These dashboards provide insights into different aspects of the service, such as circuit breaker status, API
performance, retry behavior, and overall observability.

By following the steps mentioned above and using the provided scripts and example dashboards, you can effectively
monitor the service using Prometheus and Grafana, and gain valuable insights into its performance and behavior.