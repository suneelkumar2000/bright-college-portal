package com.project.college_portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.scheduling.annotation.EnableScheduling;

import springfox.documentation.swagger2.annotations.EnableSwagger2;

@EnableScheduling
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableSwagger2
public class BrightCollegePortalApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrightCollegePortalApplication.class, args);
	}
}