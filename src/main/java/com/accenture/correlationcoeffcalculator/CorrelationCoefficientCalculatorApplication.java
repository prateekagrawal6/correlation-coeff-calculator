package com.accenture.correlationcoeffcalculator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Spring Boot Application to calculate correlation using pearson's formula
 */
@SpringBootApplication
public class CorrelationCoefficientCalculatorApplication {
	/**
	 * Java Main Method
	 *
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(CorrelationCoefficientCalculatorApplication.class, args);
	}

	/**
	 * RestTemplate bean
	 *
	 * @param builder
	 * @return restTemplate
	 */
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	/**
	 * Object Mapper bean
	 *
	 * @return objectMapper
	 */
	@Bean
	public ObjectMapper objectMapper() {
		return new ObjectMapper();
	}

}
