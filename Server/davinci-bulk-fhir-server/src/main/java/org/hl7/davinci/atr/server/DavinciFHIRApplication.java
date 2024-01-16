package org.hl7.davinci.atr.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import ca.uhn.fhir.rest.server.interceptor.LoggingInterceptor;

@SpringBootApplication
@EnableScheduling
@EnableAutoConfiguration(exclude=HibernateJpaAutoConfiguration.class)
public class DavinciFHIRApplication extends SpringBootServletInitializer{

	public static void main(String[] args) {
		SpringApplication.run(DavinciFHIRApplication.class, args);
	}
	
	@Bean
    public LoggingInterceptor loggingInterceptor() {
        return new LoggingInterceptor();
    }
}
