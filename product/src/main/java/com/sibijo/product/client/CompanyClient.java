package com.sibijo.product.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class CompanyClient {

    // application.yml에서 주입
    @Value("${company.service.url}")
    private String companyServiceUrl;

    private final RestTemplate restTemplate;

    public CompanyClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * 주어진 companyId로 회사 정보가 존재하는지 확인
     */
    public boolean existsCompany(Long companyId) {
        String url = companyServiceUrl + "/api/companies/" + companyId;
        try {
            restTemplate.getForEntity(url, Object.class);
            return true;
        } catch (HttpClientErrorException e) {
            if(e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return false;
            }
            throw e;
        }
    }
}
