//package com.sibijo.hub.config;
//
//import java.io.IOException;
//import org.springframework.http.client.ClientHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.ResponseErrorHandler;
//
//@Component
//public class RestTemplateResponseErrorHandler implements ResponseErrorHandler {
//
//    /**
//     * @param response
//     * @return
//     * @throws IOException
//     */
//    @Override
//    public boolean hasError(ClientHttpResponse response) throws IOException {
//        return response.getStatusCode().isError();
//    }
//
//    @Override
//    public void handleError(ClientHttpResponse response) throws  IOException {
//        throw new HttpClientErrorException(
//                response.getStatusCode(),
//                "API 호출 실패: " + response.getStatusText()
//        );
//    }
//}
