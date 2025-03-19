package com.sibijo.hub_routes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.sibijo.hub_routes", "com.sibijo.common"})
public class HubRoutesApplication {

	public static void main(String[] args) {
		SpringApplication.run(HubRoutesApplication.class, args);
	}

}
