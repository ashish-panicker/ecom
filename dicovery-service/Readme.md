# Discovery Service (Service Registry)

## Overview

The Discovery Service is the central registry in the microservices architecture. It enables dynamic service registration and discovery using Netflix Eureka.

Instead of hardcoding service URLs, microservices communicate using logical service names. The Discovery Service resolves those names into actual instance locations at runtime, allowing dynamic routing, scalability, and fault tolerance.

This service is built using Spring Boot and Spring Cloud Netflix Eureka Server.

---

## Architecture Overview

![Image](https://miro.medium.com/1%2A3pDkCvoBu7E2j-1smZ1pxQ.jpeg)

![Image](https://d2908q01vomqb2.cloudfront.net/972a67c48192728a34979d9a35164c1295401b71/2023/06/01/MIGMOD-174-SpringCloudServicesArchitetcure.jpg)

![Image](https://miro.medium.com/0%2AJmAL9KQRSZK-R26j)

![Image](https://github.com/Netflix/eureka/wiki/images/eureka2_architecture.png)

### Architecture Flow Explanation

1. The Discovery Service (Eureka Server) runs as a standalone application.
2. All microservices (Product Service, Order Service, etc.) register themselves with the Discovery Service at startup.
3. Each service periodically sends heartbeats to confirm availability.
4. When one service needs to call another, it uses the logical service name (e.g., [http://PRODUCT-SERVICE](http://PRODUCT-SERVICE)).
5. The client-side load balancer queries Eureka for available instances.
6. One instance is selected (typically round-robin).
7. The request is routed dynamically.

This removes hardcoded URLs and enables horizontal scaling without client changes.

---

## Responsibilities

* Maintain a registry of active service instances
* Track instance health through lease renewal (heartbeat)
* Remove unhealthy or unavailable instances
* Provide instance metadata (IP, port, instance ID)
* Enable client-side load balancing
* Support dynamic scaling

---

## Core Configuration

### Server Configuration

```yaml
server:
  port: 8761

spring:
  application:
    name: discovery-service

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```

### Client Configuration Example

```yaml
spring:
  application:
    name: PRODUCT-SERVICE

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
```

---

## Production Best Practices

### 1. Deploy in High Availability Mode

Never run a single Eureka server in production.

Use multiple Eureka instances:

* eureka-1
* eureka-2
* eureka-3

Each server registers with the others to form a cluster.

This prevents a single point of failure.

---

### 2. Enable Self-Preservation

Self-preservation prevents mass eviction during network instability.

```yaml
eureka:
  server:
    enable-self-preservation: true
```

This protects the registry during transient outages.

---

### 3. Secure the Registry

Eureka dashboard should not be publicly exposed.

Recommended:

* Basic authentication
* OAuth2 protection
* Network-level firewall restrictions
* Internal VPC deployment

---

### 4. Tune Lease and Eviction Settings

Default settings may not fit all workloads.

Important properties:

```yaml
eureka:
  instance:
    lease-renewal-interval-in-seconds: 30
    lease-expiration-duration-in-seconds: 90

  server:
    eviction-interval-timer-in-ms: 60000
```

Adjust based on:

* Network stability
* Scaling frequency
* Infrastructure latency

---

### 5. Use Monitoring and Observability

Integrate with:

* Micrometer
* Prometheus
* Grafana
* Distributed tracing tools

Avoid exposing instance-level debugging data to public APIs.

---

### 6. Container & Cloud Deployment

When using Docker or cloud environments:

```yaml
eureka:
  instance:
    prefer-ip-address: true
```

This ensures proper instance registration in dynamic environments.

---

## Eureka vs Kubernetes Service Discovery

When running microservices, discovery can be handled by either Eureka or Kubernetes-native mechanisms.

### Eureka (Application-Level Discovery)

Characteristics:

* Runs as a Spring Boot application
* Works well in VM-based or hybrid environments
* Client-side load balancing
* Framework-driven service discovery
* Technology-specific (Spring ecosystem)

Advantages:

* Easy integration with Spring Cloud
* Rich metadata support
* Works outside Kubernetes
* Good for traditional microservice deployments

Limitations:

* Requires maintaining registry cluster
* Additional infrastructure component
* Not cloud-native by default

---

### Kubernetes Service Discovery (Infrastructure-Level Discovery)

Characteristics:

* Built into Kubernetes
* Uses DNS and Services
* No separate registry application
* Load balancing handled by kube-proxy

How it works:

Each service gets a DNS entry:

```
product-service.default.svc.cluster.local
```

Pods are automatically discovered via Kubernetes Service abstraction.

Advantages:

* No additional registry required
* Fully cloud-native
* Scales automatically
* Managed by cluster control plane

Limitations:

* Requires Kubernetes environment
* Less application-level metadata control
* Not suitable outside K8s

---

## When to Use What

Use Eureka when:

* Running on VMs or traditional servers
* Using Spring Cloud ecosystem
* Need cross-platform discovery
* Not fully migrated to Kubernetes

Use Kubernetes discovery when:

* Fully containerized environment
* Running entirely inside Kubernetes
* Want infrastructure-managed discovery
* Avoid managing additional registry servers

---

## Architectural Role in the System

The Discovery Service acts as the backbone of dynamic microservices communication. It enables:

* Zero hardcoded endpoints
* Horizontal scaling without configuration changes
* Fault tolerance via automatic instance removal
* Clean separation between service provider and consumer

In distributed systems, discovery is essential for resilience and scalability.

