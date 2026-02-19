# Microservices Demo Using Spring Boot

## Discovery Service (Eureka Server)

The Discovery Service is responsible for service registration and service discovery within the microservices architecture. It acts as a centralized registry where all microservices register themselves at startup and periodically send heartbeats to indicate they are alive.

Instead of hardcoding service URLs, client services communicate using logical service names. The Discovery Service resolves these names into actual host and port details, enabling dynamic routing and load balancing.

### Responsibilities

  Maintains a registry of all available microservice instances
  Tracks instance health through periodic heartbeats
  Removes unavailable instances automatically
  Enables client-side load balancing
  Decouples service location from service invocation

### How It Works

1. Each microservice registers with the Discovery Service at startup.
2. The Discovery Service stores instance metadata such as hostname, IP address, and port.
3. Client services request instance information using a logical service name.
4. A load balancer selects one of the available instances.
5. Requests are routed dynamically without hardcoded endpoints.

### Key Benefits

  Eliminates tight coupling between services
  Supports horizontal scaling with multiple instances
  Enables fault tolerance by automatically removing unhealthy instances
  Simplifies inter-service communication
  Improves system resilience in distributed environments

### Example Scenario

If two instances of PRODUCT-SERVICE are running on ports 8081 and 8082:

  Both instances register with the Discovery Service.
  ORDER-SERVICE calls [http://PRODUCT-SERVICE/api](http://PRODUCT-SERVICE/api).
  The Discovery Service provides available instances.
  The load balancer selects one instance for the request.

This allows scaling without changing client configurations.

How `@LoadBalanced` works?
Spring executes an Interceptor, LoadBalancerInterceptor, and resolves the address against the service name,
in our case this will resolve `http://PRODUCT-SERVICE` into `http://192.168.18.244:38969`

The LoadBalancerInterceptor
- Intercept the outgoing request
- Strip the SERVICE-NAME
- Checks with LoadBalancerClient

The LoadBalancerClient: will work with discovery server and to resolve the actual ip address
- Discover: Checks with Service Registry, Which service is mapped as PRODUCT-SERVICE?
- Selection: Service registry will return a list of ip addresses
- Picking: Based on config, Round Robin mostly, an ip address is selected

The LoadBalancerInterceptor:
Once an instance is chose The LoadBalancerInterceptor will reconstruct the url  with ip address and inject it into
RestClient builder.
	  

### Configuration Overview

Typical configuration for a Discovery Server includes:

  Disabling self-registration
  Disabling registry fetching
  Running on a dedicated port (commonly 8761)

Client services must:

  Set a unique spring.application.name
  Configure the Eureka server URL
  Enable service registration and registry fetching

### Architecture Role

The Discovery Service is a foundational component in a microservices ecosystem. It enables dynamic infrastructure, horizontal scalability, and resilient inter-service communication without requiring manual configuration updates when services scale or move.

In production systems, the Discovery Service is often deployed in a clustered setup for high availability.

