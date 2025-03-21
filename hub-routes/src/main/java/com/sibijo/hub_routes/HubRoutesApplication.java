package com.sibijo.hub_routes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {"com.sibijo.hub_routes", "com.sibijo.common"})
@EnableFeignClients
public class HubRoutesApplication {

    public static void main(String[] args) {
        SpringApplication.run(HubRoutesApplication.class, args);
    }

}
