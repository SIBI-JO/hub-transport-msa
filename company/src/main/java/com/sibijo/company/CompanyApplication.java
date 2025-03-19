package com.sibijo.company;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication(scanBasePackages = {"com.sibijo.company", "com.sibijo.common"})
public class CompanyApplication {

	public static void main(String[] args) {
		SpringApplication.run(CompanyApplication.class, args);
	}

}
