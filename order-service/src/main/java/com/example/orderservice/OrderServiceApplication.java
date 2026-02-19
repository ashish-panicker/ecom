package com.example.orderservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestClient;

@SpringBootApplication
@EnableDiscoveryClient
public class OrderServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderServiceApplication.class, args);
	}

	// https://docs.spring.io/spring-cloud-commons/reference/spring-cloud-commons/common-abstractions.html#rest-client-loadbalancer-client
	@Bean
	@LoadBalanced
	RestClient.Builder restClientBuilder() {
		return RestClient.builder();
	}

	/**
	 * How @LoadBalanced works?
	 * Spring executes an Interceptor, LoadBalancerInterceptor, and resolves the address against the service name,
	 * in our case this will resolve http://PRODUCT-SERVICE into http://192.168.18.244:38969
	 *
	 * The LoadBalancerInterceptor
	 * - Intercept the outgoing request
	 * - Strip the SERVICE-NAME
	 * - Checks with LoadBalancerClient
	 *
	 * The LoadBalancerClient: will work with discovery server and to resolve the actual ip address
	 * - Discover: Checks with Service Registry, Which service is mapped as PRODUCT-SERVICE?
	 * - Selection: Service registry will return a list of ip addresses
	 * - Picking: Based on config, Round Robin mostly, an ip address is selected
	 *
	 * The LoadBalancerInterceptor:
	 * Once an instance is chose The LoadBalancerInterceptor will reconstruct the url  with ip address and inject it into
	 * RestClient builder
	 */
}
